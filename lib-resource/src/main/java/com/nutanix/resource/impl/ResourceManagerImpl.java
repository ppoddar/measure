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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nutanix.bpg.measure.model.Catalog;
import com.nutanix.bpg.measure.utils.JsonUtils;
import com.nutanix.bpg.measure.utils.ResourceUtils;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.AllocationPolicy;
import com.nutanix.resource.Capacity;
import com.nutanix.resource.ResourceManager;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.model.Cluster;

public class ResourceManagerImpl 
	implements ResourceManager {
	private static ResourceManagerImpl singleton;
	private static Properties config;
	private List<ResourcePool> pools;
	private List<Allocation>   allocations;
	private AllocationPolicy allocationPolicy;
	private Catalog<Cluster> clusters;
	
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
		allocationPolicy = (AllocationPolicy)config.get(POLICY_ALLOCATION);
		pools = new ArrayList<>();
		
		String catalogLocation = config.getProperty(CATALOG_CLUSTER_URL);
		URL url = ResourceUtils.getURL(catalogLocation);
		JsonNode json = JsonUtils.readResource(url, true);
		readClusterAndResourcePoolAssignment(json);
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
		return props;
	}
	
	@Override
	public ResourcePool getResourcePool(String id) {
		ResourcePool pool = null;
		for (ResourcePool p : pools) {
			if (p.getId().equals(id)) {
				pool = p;
				break;
			}
		}
		return pool;
	}

	@Override
	public List<ResourcePool> getResourcePools() {
		return pools;
	}

	@Override
	public Allocation allocate(ResourcePool pool, Collection<Capacity> demand) {
		Allocation allocation = pool.allocate(new DefaultCapacities(demand));
		allocations.add(allocation);
		return allocation;
	}

	@Override
	public boolean deallocate(String allocId) {
		Allocation alloc = findAllocation(allocId);
		boolean removed = allocations.remove(alloc);
		
		return removed;
	}

	@Override
	public Allocation findAllocation(String allocId) {
		Allocation alloc = null;
		for (Allocation a : allocations) {
			if (a.getId().equals(allocId)) {
				alloc = a;
				break;
			}
		}
		return alloc;
	}

	
	
	public void readClusterAndResourcePoolAssignment(JsonNode json) throws Exception {
		try {
			
			JsonNode clustersNode = JsonUtils.assertProperty(json, "clusters", true);
			ObjectMapper mapper = new ObjectMapper();
			MapLikeType type = mapper.getTypeFactory()
				.constructMapLikeType(Map.class, String.class, Cluster.class);
			
			Map<String, Cluster> clusterMap = mapper.convertValue(clustersNode, type);

			clusters = new Catalog<>();
			for (Map.Entry<String, Cluster> e : clusterMap.entrySet()) {
				Cluster cluster = e.getValue();
				cluster.setName(e.getKey());
				clusters.add(cluster);
			}
			
			JsonNode poolsNode  = JsonUtils.assertProperty(json, "pools", true);
			pools = new ArrayList<ResourcePool>();
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
	}
	
}
