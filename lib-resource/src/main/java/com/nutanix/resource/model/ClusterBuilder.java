package com.nutanix.resource.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectWriter.GeneratorSettings;
import com.nutanix.bpg.model.Catalog;
import com.nutanix.bpg.utils.JsonUtils;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.Storage;

/**
 *  Builds cluster. each cluster in separte
 *  thread
 *
 */
public class ClusterBuilder {
	private static Logger logger = LoggerFactory.getLogger(ClusterBuilder.class);
	/**
	 * populates given clusters with resource capacity
	 * information.
	 * <p>
	 * The capacity information is obtained by calling
	 * Prism gateway for cluster,
	 * parsing the response for 
	 * capacity.
	 *  
	 * @param cluster
	 * @throws Exception
	 */
	public void build(Catalog<Cluster> clusters) {
		ExecutorService threadPool = Executors.newCachedThreadPool();
		List<Future<Cluster>> futures = new ArrayList<Future<Cluster>>();
		for (Cluster cluster : clusters) {
			SingleClusterBuilder builder = new SingleClusterBuilder(cluster);
			Future<Cluster> f = threadPool.submit(builder);
			futures.add(f);
		}
		
		for (Future<Cluster> f : futures) {
			try {
				Cluster cluster = f.get();
				clusters.add(cluster);
				logger.info("built " + cluster + " capacity " + cluster.getTotalCapacity());
			} catch (Exception ex) {
				logger.debug("error building cluster", ex);
			}
		}
		
	}
	
	
	

}
