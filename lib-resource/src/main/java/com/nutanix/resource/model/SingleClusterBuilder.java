package com.nutanix.resource.model;

import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.bpg.model.Catalog;
import com.nutanix.bpg.utils.JsonUtils;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.Storage;
import com.nutanix.resource.prism.PrismGateway;

public class SingleClusterBuilder implements Callable<Cluster> {
		private Cluster cluster;
		
		public SingleClusterBuilder(Cluster c) {
			cluster = c;
		}
		
		@Override
		public Cluster call() throws Exception {
			PrismGateway gateway = new PrismGateway(cluster);
			JsonNode response = gateway.getResponse("cluster/");
			cluster.addCapacity(getStorageCapacity(response.get("usage_stats")));
			Catalog<Host> hosts = new HostBuilder().build(gateway);
			for (Host host: hosts) {
				cluster.addCapacity(host.getMemory());
			}
			return cluster;
		}
		/**
		 * adds storage capacity to cluster
		 * @param json
		 * @return
		 */
		Storage getStorageCapacity(JsonNode usage_stats) {
			long capacity_bytes = JsonUtils.getLong(usage_stats, "storage.capacity_bytes");
			Storage storage = new Storage(capacity_bytes, MemoryUnit.B);
			return storage;
		}

	}