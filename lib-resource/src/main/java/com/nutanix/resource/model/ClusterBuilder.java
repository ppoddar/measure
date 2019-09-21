package com.nutanix.resource.model;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.resource.prism.PrismGateway;

/**
 * Gathers resource capacity for each virtual
 * machine in a cluster from HTTP response via
 * Prism gateway.
 *  
 * @author pinaki.poddar
 *
 */
public class ClusterBuilder implements Callable<Boolean> {
	
	private final Cluster cluster;
	private static Logger logger = LoggerFactory.getLogger(ClusterBuilder.class);

	public ClusterBuilder(Cluster c) {
		cluster = c;
	}
	
	
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
	public void build() throws Exception {
		logger.debug("building " + cluster);
		PrismGateway gateway = new PrismGateway(cluster);
		JsonNode response = gateway.getResponse("vms/?include_vm_disk_config=true");
		JsonNode entities = response.get("entities");
		logger.debug("found " + entities.size() + " vms for " + cluster);
		for (JsonNode entity : entities) {
			VirtualMachine vm = new VirtualMachine(entity.get("uuid").asText());
			vm.setName(entity.get("name").asText());
			vm.setMemory(entity.get("memory_mb").asInt());
			vm.setCpuCount(entity.get("num_cores_per_vcpu").asInt()
				      	 * entity.get("num_vcpus").asInt());
			JsonNode diskArray = entity.get("vm_disk_info");
			logger.debug(vm.getName() + " has " + diskArray.size() + " disks");
			long diskSize = 0;
			for (JsonNode disk : diskArray) {
				if (disk.has("size")) {
					long s = disk.get("size").asLong();
					logger.debug("adding disk of size " + s);
					diskSize += s;
				} else {
					//logger.warn(disk.path("name").asText() + " does not have size");
				}
			}
			vm.setDiskSize(diskSize);
			logger.info("virtual machine: [" + vm + "] capacity:" + vm.getAvailableCapacity());
			cluster.addResource(vm);
		}
	}


	@Override
	public Boolean call() {
		try {
			build();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.warn("can not get resources from cluster " + cluster);
			cluster.markUnavailable(ex.getMessage());
			return false;
		}
	}
}
