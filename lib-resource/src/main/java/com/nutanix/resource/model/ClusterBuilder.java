package com.nutanix.resource.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.resource.prism.PrismGateway;

/**
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
	public void build(Cluster cluster) throws Exception {
		logger.info("building " + cluster);
		PrismGateway gateway = new PrismGateway(cluster.getHost(), cluster.getPort());
		JsonNode response = gateway.getResponse("vms/?include_vm_disk_config=true");
		JsonNode entities = response.get("entities");
		logger.info("found " + entities.size() + " vms");
		for (JsonNode entity : entities) {
			VirtualMachine vm = new VirtualMachine();
			vm.setId(entity.get("uuid").asText());
			vm.setName(entity.get("name").asText());
			vm.setMemory(entity.get("memory_mb").asInt());
			vm.setCpuCount(entity.get("num_cores_per_vcpu").asInt()
					* entity.get("num_vcpus").asInt());
			JsonNode diskArray = entity.get("vm_disk_info");
			logger.info(vm.getName() + " has " + diskArray.size() + " disks");
			long diskSize = 0;
			for (JsonNode disk : diskArray) {
				if (disk.has("size")) {
					long s = disk.get("size").asLong();
					logger.info("adding disk of size " + s);
					diskSize += s;
				} else {
					logger.warn(disk.path("name").asText() + " does not have size");
				}
			}
			vm.setDiskSize(diskSize);
			logger.info("virtual machine: " + vm + " capacities:" + vm.getCapacities());
			cluster.addResource(vm);
		}
	}
}
