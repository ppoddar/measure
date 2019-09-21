package com.nutanix.resource.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.nutanix.bpg.model.Catalog;
import com.nutanix.bpg.utils.JsonUtils;
import com.nutanix.bpg.utils.ResourceUtils;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.serde.CapacityDeserilaizer;
import com.nutanix.capacity.serde.CapacitySerializer;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.AllocationPolicy;
import com.nutanix.resource.ResourceManager;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.ClusterBuilder;

public class ResourceManagerImpl 
	implements ResourceManager {
	private static ResourceManagerImpl singleton;
	private static Properties config;
	private Catalog<Cluster> clusters;
	private Catalog<ResourcePool> pools;
	private AllocationPolicy allocationPolicy;
	private ObjectMapper mapper;
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceManager.class);
	
	public static  ResourceManager instance() {
		if (singleton == null) {
			if (config == null) {
				config = defaultProperties();
				logger.warn("Resource Manager is not initialized."
						+ " It would use default properties"
						+ " call ResourceManger.init(Properties) before "
						+ " obtaining handle to Resource Mangemnet service");
			}
			try {
				singleton = new ResourceManagerImpl();
			} catch (Exception ex) {
				throw new ExceptionInInitializerError(ex);
			}
 			
		}
		return singleton;
	}
	
	/**
	 * create singleton ResourceManger implementation.
	 * Must initialize before.
	 * 
	 */
	private ResourceManagerImpl() throws Exception {
		mapper = configureObjectMapper();
		allocationPolicy = (AllocationPolicy)config.get(POLICY_ALLOCATION);
		pools = new Catalog<ResourcePool>();
		
		String catalogLocation = config.getProperty(CATALOG_CLUSTER_URL);
		URL url = ResourceUtils.getURL(catalogLocation);
		JsonNode clusterDescriptor = JsonUtils.readResource(url, true);
		clusters = readClusterDesciptiors(clusterDescriptor);
		
		ExecutorService threadPool = Executors.newCachedThreadPool();
		Map<Cluster,Future<Boolean>> futures = 
				new HashMap<Cluster, Future<Boolean>>();
		for (Cluster cluster : clusters) {
			futures.put(cluster, threadPool.submit(new ClusterBuilder(cluster)));
		}
		for (Map.Entry<Cluster, Future<Boolean>> e: futures.entrySet()) {
			try {
				boolean available = e.getValue().get(10, TimeUnit.SECONDS);
				if (!available)
					logger.warn("cluster " + e.getKey() + " is unavailable"
							+ e.getKey().getReasonForUnavailability());
			} catch (TimeoutException e1) {
				e.getKey().markUnavailable("unreachable");
				logger.warn("cluster " + e.getKey() + " is unreachable");
			}
		}
		
		String poolAssignmentLocation = config.getProperty(POOL_ASSIGNMENT_URL);
		url = ResourceUtils.getURL(poolAssignmentLocation);
		JsonNode poolAssignment = JsonUtils.readResource(url, true);
		pools = assignPools(poolAssignment, clusters);
		
		
	}
	
	public static void setProperties(Properties p) {
		config = defaultProperties();
		if (p != null) {
			config.putAll(p);
		} else {
			logger.warn("using default properties for Resource Manager");
		}
	}
	
	private static Properties defaultProperties() {
		Properties props = new Properties();
		props.put(POLICY_ALLOCATION, new DefaultAllocationPolicy());
		props.put(CATALOG_CLUSTER_URL, "config/clusters.yml");
		props.put(POOL_ASSIGNMENT_URL, "config/pools.yml");
		return props;
	}
	
	@Override
	public ResourcePool getResourcePoolByName(String name) {
		ResourcePool pool = null;
		for (ResourcePool p : pools.values()) {
			if (p.getName().equalsIgnoreCase(name)) {
				pool = p;
				break;
			}
		}
		return pool;
	}
	
	@Override
	public ResourcePool getResourcePool(String id) {
		
		return pools.get(id);
	}


	@Override
	public Collection<ResourcePool> getResourcePools() {
		return pools.values();
	}
	@Override
	public List<String> getPoolNames() {
		List<String> names = new ArrayList<>();
		for (ResourcePool pool : pools.values()) {
			names.add(pool.getName());
		}
		return names;
	}

	@Override
	public Allocation allocate(ResourcePool pool, Capacity demand) {
		logger.debug("allocating " + demand);
		Allocation allocation = pool.allocate(new DefaultCapacity(demand));
		return allocation;
	}


	public ObjectMapper getObjectMapper() {
		return mapper;
	}
	private ObjectMapper configureObjectMapper() {
		mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addSerializer(new CapacitySerializer());
		module.addDeserializer(Capacity.class, new CapacityDeserilaizer());
		mapper.registerModule(module);
		
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return mapper;
	}
	
	public Catalog<ResourcePool> assignPools(JsonNode json, Catalog<Cluster> clusters) throws Exception {
		Catalog<ResourcePool> pools = new Catalog<ResourcePool>(true);
		try {
			JsonNode poolsNode  = JsonUtils.assertProperty(json, "pools");
			Iterator<String> poolNames = poolsNode.fieldNames();
			while (poolNames.hasNext()) {
				String poolName = poolNames.next();
				ResourcePool pool = new DefaultResourcePool();
				pool.setAllocationPolicy(allocationPolicy);
				pool.setName(poolName);
				pools.add(pool);
				for (JsonNode clusterName : poolsNode.get(poolName)) {
					String name = clusterName.asText();
					if (!clusters.has(name)) {
						throw new RuntimeException("Cluster [" + name + "] is specifed in " + pool
								+ " but it is not defiend");
					}
					Cluster cluster = clusters.get(clusterName.asText());
					if (cluster.isAvalable()) {
						pool.addProvider(cluster);
					} else {
						logger.warn("" + cluster + " is unavailable due to:"
								+ cluster.getReasonForUnavailability());
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (RuntimeException.class.isInstance(ex)) {
				throw ex;
			} else {
				throw new RuntimeException(ex);
			}
		}
		return pools;
	}
	
	public Catalog<Cluster> readClusterDesciptiors(JsonNode json) throws Exception {
		Catalog<Cluster> clusters = new Catalog<>(true);
		try {
			JsonNode clustersNode = JsonUtils.assertProperty(json, "clusters");
//			MapLikeType type = mapper.getTypeFactory()
//				.constructMapLikeType(Map.class, String.class, Cluster.class);
//			
//			Map<String, Cluster> clusterMap = mapper.convertValue(clustersNode, type);
			int DEFAULT_PORT = 9440;
			String DEFAULT_USER = "admin";
			String DEFAULT_PWD = "Nutanix.1";
			String DEFAULT_HV = "AHV";
			
			Iterator<String> names = clustersNode.fieldNames();
			while (names.hasNext()) {
				String clusterName = names.next();
				JsonNode clusterNode = clustersNode.get(clusterName);
				Cluster cluster = new Cluster(clusterName);
				cluster.setName(clusterName);
				cluster.setHost(clusterNode.get("host").asText());
				if (clusterNode.has("port")) {
					cluster.setPort(clusterNode.get("port").asInt());
				} else {
					cluster.setPort(DEFAULT_PORT);
				}
				if (clusterNode.has("user")) {
					cluster.setUser(clusterNode.get("user").asText());
				} else {
					cluster.setUser(DEFAULT_USER);
				}
				if (clusterNode.has("user")) {
					cluster.setUser(clusterNode.get("user").asText());
				} else {
					cluster.setUser(DEFAULT_USER);
				}
				if (clusterNode.has("pwd")) {
					cluster.setPassword(clusterNode.get("pwd").asText());
				} else {
					cluster.setPassword(DEFAULT_PWD);
				}
				if (clusterNode.has("hypervisor")) {
					cluster.setHypervisor(clusterNode.get("hypervisor").asText());
				} else {
					cluster.setHypervisor(DEFAULT_HV);
				}
				clusters.add(cluster);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (RuntimeException.class.isInstance(ex)) {
				throw ex;
			} else {
				throw new RuntimeException(ex);
			}
		}
		return clusters;
	}

	
}
