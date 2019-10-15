package com.nutanix.resource.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nutanix.bpg.model.Catalog;
import com.nutanix.bpg.utils.JsonUtils;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.impl.DefaultCapacity;
import com.nutanix.capacity.serde.CapacityDeserilaizer;
import com.nutanix.capacity.serde.CapacitySerializer;
import com.nutanix.config.Configuration;
import com.nutanix.config.ConfigurationFactory;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.AllocationPolicy;
import com.nutanix.resource.ResourceManager;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.ClusterBuilder;

public class ResourceManagerImpl 
	implements ResourceManager {
	private Catalog<Cluster> clusters;
	private Catalog<ResourcePool> pools;
	private AllocationPolicy allocationPolicy;
	private static ObjectMapper mapper;
	private static ResourceManagerImpl singleton;
	private static Configuration config;
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceManager.class);
	
	static {
		mapper = configureObjectMapper();
	}
	
	public static ResourceManager configure(Configuration conf) {
		config = conf;
		return instance();
	}
	
	public static ResourceManager configure(URI confLocation) {
		config = ConfigurationFactory.newConfiguration(confLocation);
		return instance();
	}

	
	/**
	 * @return
	 */
	public static  ResourceManager instance() {
		if (singleton == null) {
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
	 * 
	 * 
	 */
	private ResourceManagerImpl() throws Exception {
		allocationPolicy = new DefaultAllocationPolicy();
		pools = new Catalog<ResourcePool>(true, true);
		
		JsonNode clusterDescriptor = config
				.getSection("clusters").asJson();
		
		clusters = readClusterDesciptiors(clusterDescriptor);
		new ClusterBuilder().build(clusters);
		
		JsonNode poolAssignment = config.getSection("pools").asJson();
		pools = assignPools(poolAssignment, clusters);
	}
	
	
	
	
	@Override
	public ResourcePool getResourcePool(String name) {
		return pools.get(name);
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

//	@Override
//	public Allocation allocate(ResourcePool pool, Capacity demand) {
//		logger.debug("allocating " + demand);
//		Allocation alloc = pool.allocate(new DefaultCapacity(demand));
//		return alloc;
//	}


	public ObjectMapper getObjectMapper() {
		return mapper;
	}
	
	private static ObjectMapper configureObjectMapper() {
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
				//pool.setAllocationPolicy(allocationPolicy);
				pool.setName(poolName);
				pools.add(pool);
				for (JsonNode clusterName : poolsNode.get(poolName)) {
					String name = clusterName.asText();
					if (!clusters.has(name)) {
						throw new RuntimeException("Cluster [" + name + "] is specifed in " + pool
								+ " but it is not defiend");
					}
					Cluster cluster = clusters.get(clusterName.asText());
					pool.addResource(cluster);
					cluster.assignTo(pool);
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
	
	/**
	 * reads multiple cluster description from 
	 * given JSONNode.
	 * 
	 * @param json
	 * @return
	 * @throws Exception
	 * 
	 * @see {@link ClusterBuilder}
	 */
	public Catalog<Cluster> readClusterDesciptiors(JsonNode json) throws Exception {
		Catalog<Cluster> clusters = new Catalog<>(true);
		JsonNode clustersNode = JsonUtils.assertProperty(json, "clusters");
		int DEFAULT_PORT    = 9440;
		String DEFAULT_USER = "admin";
		String DEFAULT_PWD  = "Nutanix.1";
		String DEFAULT_HV   = "AHV";
		
		Iterator<String> names = clustersNode.fieldNames();
		while (names.hasNext()) {
			String clusterName = names.next();
			JsonNode clusterNode = clustersNode.get(clusterName);
			Cluster cluster = new Cluster();
			cluster.setName(clusterName);
			cluster.setHost(clusterNode.get("host").asText());
			
			cluster.setPort(JsonUtils.getInt(clusterNode, "port",    DEFAULT_PORT));
			cluster.setUser(JsonUtils.getString(clusterNode, "user", DEFAULT_USER));
			cluster.setPassword(JsonUtils.getString(clusterNode, "pwd", DEFAULT_PWD));
			cluster.setHypervisor(JsonUtils.getString(clusterNode, "hypervisor", DEFAULT_HV));

			clusters.add(cluster);
		}
		return clusters;
	}
	
	
	
}
