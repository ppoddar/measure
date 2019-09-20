package com.nutanix.resource.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import com.nutanix.resource.Allocation;
import com.nutanix.resource.AllocationPolicy;
import com.nutanix.resource.Capacity;
import com.nutanix.resource.Quantity;
import com.nutanix.resource.ResourceManager;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.ClusterBuilder;
import com.nutanix.resource.model.serde.CapacityDeserilaizer;
import com.nutanix.resource.model.serde.CapacitySerializer;
import com.nutanix.resource.model.serde.QuantityDeserializer;
import com.nutanix.resource.model.serde.QuantitySerializer;

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
		
		String poolAssignmentLocation = config.getProperty(POOL_ASSIGNMENT_URL);
		url = ResourceUtils.getURL(poolAssignmentLocation);
		JsonNode poolAssignment = JsonUtils.readResource(url, true);
		pools = assignPools(poolAssignment, clusters);
		for (Cluster cluster : clusters) {
			new ClusterBuilder().build(cluster);
		}
			
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
		module.addSerializer(new QuantitySerializer());
		module.addDeserializer(Capacity.class, new CapacityDeserilaizer());
		module.addDeserializer(Quantity.class, new QuantityDeserializer());
		mapper.registerModule(module);
		
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return mapper;
	}
	
	public Catalog<ResourcePool> assignPools(JsonNode json, Catalog<Cluster> clusters) throws Exception {
		Catalog<ResourcePool> pools = new Catalog<ResourcePool>();
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
					pool.addProvider(cluster);
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
		Catalog<Cluster> clusters = new Catalog<>();
		try {
			JsonNode clustersNode = JsonUtils.assertProperty(json, "clusters");
			MapLikeType type = mapper.getTypeFactory()
				.constructMapLikeType(Map.class, String.class, Cluster.class);
			
			Map<String, Cluster> clusterMap = mapper.convertValue(clustersNode, type);

			for (Map.Entry<String, Cluster> e : clusterMap.entrySet()) {
				Cluster cluster = e.getValue();
				cluster.setName(e.getKey());
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
