package com.nutanix.resource.model;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.resource.Resource;
import com.nutanix.resource.impl.AbstarctResource;
import com.nutanix.capacity.CPU;
import com.nutanix.capacity.CpuUnit;
import com.nutanix.capacity.Memory;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.Storage;

public class VirtualMachine  {
	private String ipAddress;
	private static Logger logger = LoggerFactory.getLogger(VirtualMachine.class);
	@JsonCreator
	public VirtualMachine(@JsonProperty("id") String id) {
		this(id, id);
	}
	
	public VirtualMachine(String id, String name) {
		super(id);
		setName(name);
		
		setPreferredUnit(ResourceKind.MEMORY,  MemoryUnit.MB);
		setPreferredUnit(ResourceKind.STORAGE, MemoryUnit.GB);
		setPreferredUnit(ResourceKind.COMPUTE, CpuUnit.NONE);
		
	}
	
	
	@JsonIgnore
	public void setCpuCount(int cpus) {
		addQuanity(new CPU(cpus));
	}
	@JsonIgnore
	public void setMemory(int memory) {
		addQuanity(new Memory(memory, MemoryUnit.MB));
	}
	
	@JsonIgnore
	public void setDiskSize(long size) {
		addQuanity(new Storage(size, MemoryUnit.B));
	}
	
	public String getIPAddress() {
		return ipAddress;
	}
	public void setIPAddress(String addr) {
		 ipAddress = addr;
	}
	public boolean hasIPAddress() {
		 return ipAddress != null && !ipAddress.trim().isEmpty();
	}
	
	public String toString() {
		String s = "vm-" + getName();
		if (hasIPAddress()) {
			s += ":" + getIPAddress() ;
		}
		s +=  " available " + getAvailableCapacity();
		return s;
	}

	@Override
	protected <R extends Resource> R createNew() {
		VirtualMachine copy = new VirtualMachine(UUID.randomUUID().toString());
		return (R)copy;
	}
	
	/**
	 * create a VM from given JSONNode
	 * @param entity
	 * @return null if JSON is invalid
	 */
	public static VirtualMachine fromJson(JsonNode entity) {
		if (!entity.has("uuid")) {
			logger.warn("ignore vm because it does not have uuid");
			return null;
		}
		VirtualMachine vm = new VirtualMachine(entity.get("uuid").asText());
		if (!entity.has("name")) {
			logger.warn("ignore  " + vm.getId() + " because it does not have name");
			return null;
		}
		if (!entity.has("vm_disk_info")) {
			logger.warn("ignore " + vm + " because it does not have vm_disk_info");
			return null;
		}
		if (!entity.has("vm_nics")) {
			logger.warn("ignore  " + vm + " because it does not have vm_nics");
			return null;
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
		return vm;
	}

}
