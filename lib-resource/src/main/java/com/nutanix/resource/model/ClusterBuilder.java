package com.nutanix.resource.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.bpg.model.Catalog;
import com.nutanix.resource.prism.PrismGateway;

/**
 * Gathers resource capacity for each virtual
 * machine in a cluster by HTTP response via
 * Prism gateway.
 *  
 * @author pinaki.poddar
 *
 */
public class ClusterBuilder {
	private static Logger logger = LoggerFactory.getLogger(ClusterBuilder.class);
	
	
	/**
	 * populates given cluster with resource capacity
	 * information.
	 * <p>
	 * The capacity information is obtained by calling
	 * Prism gateway for available virtual machines,
	 * parsing the response for memory, cpu and disk 
	 * capacity.
	 *  
	 * @param cluster
	 * @throws Exception
	 */
	public void build(Catalog<Cluster> clusters) {
		
		ExecutorService threadPool = Executors.newCachedThreadPool();
		List<Future<Cluster>> futures = new ArrayList<Future<Cluster>>();
		for (Cluster cluster : clusters) {
			SingleClusterBuilder builder = 
					new SingleClusterBuilder(cluster);
			Future<Cluster> f = threadPool.submit(builder);
			futures.add(f);
		}
		
		for (Future<Cluster> f : futures) {
			try {
				clusters.add(f.get());
			} catch (Exception ex) {
				logger.debug("error building cluster", ex);
			}
		}
		
	}
	
	private static class SingleClusterBuilder implements Callable<Cluster> {
		private Cluster cluster;
		
		SingleClusterBuilder(Cluster c) {
			cluster = c;
		}
		@Override
		public Cluster call() throws Exception {
			PrismGateway gateway = new PrismGateway(cluster);
			JsonNode response = gateway.getResponse("cluster/");
			cluster.populate(response);
			Catalog<Host> hosts = new HostBuilder().build(gateway);
			Catalog<Disk> disks = new DiskBuilder().build(gateway);
			for (Disk disk : disks) {
				cluster.addDisk(disk);
			}
			for (Host host : hosts) {
				cluster.addHost(host);
			}
			return cluster;
		}
	}


}
