package com.nutanix.resource.model;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.bpg.utils.URLBuilder;
import com.nutanix.resource.prism.PrismGateway;

/**
 * Gathers resource capacity for each virtual
 * machine in a cluster by HTTP response via
 * Prism gateway.
 *  
 * @author pinaki.poddar
 *
 */
public class ClusterBuilder implements Callable<Boolean> {
	
	private final Cluster cluster;
	private static Logger logger = LoggerFactory.getLogger(ClusterBuilder.class);

	/**
	 * create a builder for given cluster.
	 * @param c a cluster. must not be null.
	 */
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
		JsonNode response = gateway.getVMs();
		JsonNode entities = response.get("entities");
		logger.debug("found " + entities.size() + " vms for " + cluster);
		for (JsonNode entity : entities) {
			if (!entity.has("uuid")) {
				logger.warn("ignore vm because it does not have uuid");
				continue;
			}
			VirtualMachine vm = new VirtualMachine(entity.get("uuid").asText());
			if (!entity.has("name")) {
				logger.warn("ignore  " + vm.getId() + " because it does not have name");
				continue;
			}
			if (!entity.has("vm_disk_info")) {
				logger.warn("ignore " + vm + " because it does not have vm_disk_info");
				continue;
			}
			if (!entity.has("vm_nics")) {
				logger.warn("ignore  " + vm + " because it does not have vm_nics");
				continue;
			}
			
			vm.setName(entity.get("name").asText());
			vm.setMemory(entity.get("memory_mb").asInt());
			vm.setCpuCount(entity.get("num_cores_per_vcpu").asInt()
				      	 * entity.get("num_vcpus").asInt());
			JsonNode diskArray = entity.get("vm_disk_info");
			//logger.debug(vm.getName() + " has " + diskArray.size() + " disks");
			long diskSize = 0;
			for (JsonNode disk : diskArray) {
				if (disk.has("size")) {
					long s = disk.get("size").asLong();
					//logger.debug("adding disk of size " + s);
					diskSize += s;
				} else {
					//logger.warn(disk.path("name").asText() + " does not have size");
				}
			}
			vm.setDiskSize(diskSize);
			
			for (JsonNode nic: entity.get("vm_nics")) {
				if (nic.has("ip_address")) {
					vm.setIPAddress(nic.get("ip_address").asText());
					break;
				}
			}
			if (vm.hasIPAddress()) {
				logger.debug("virtual machine: [" + vm + ":" + vm.getIPAddress() + "] capacity:" + vm.getAvailableCapacity());
				cluster.addResource(vm);
			} else {
				logger.warn("ignore vm " + vm + " becuase it does have an ip address");
			}
		}
		logger.info("fetched resource for cluster " + cluster.getName()
			+ " capacity " + cluster.getTotalCapacity());
	}
	
	


	@Override
	public Boolean call() {
		try {
			build();
			return true;
		} catch (Exception ex) {
			logger.warn("can not get resources from cluster " + cluster
				+ " due to " + ex);
			ex.printStackTrace();
			cluster.setAvailable(false);
			cluster.setReason(ex.getMessage());
			return false;
		}
	}
}
