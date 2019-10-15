package com.nutanix.resource.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.bpg.utils.JsonUtils;
import com.nutanix.bpg.utils.Named;
import com.nutanix.capacity.Memory;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.Quantity;
/**
 * Physical host provides memory and cpu capacity 
 * to a cluster.
 * Often a cluster has multiple physical hosts.
 * 
 * @author pinaki.poddar
 *
 */
public class Host implements Named {
	private String name;
	private String model;
	private long memory_capacity;
	private long cpu_capacity;
	private long cpu_frequency;
	private int cpu_cores;
	private int cpu_sockets;
	private int cpu_threads;
	private int vm_count;
	
	public Host(JsonNode json) {
		name  = JsonUtils.getString(json, "name");
		model = JsonUtils.getString(json, "model", "");
		memory_capacity = JsonUtils.getLong(json, "memory_capacity_in_bytes");
		cpu_capacity    = JsonUtils.getLong(json, "cpu_capacity_in_hz");
		cpu_frequency   = JsonUtils.getLong(json, "cpu_frequency_in_hz");
		cpu_cores       = JsonUtils.getInt(json, "num_cpu_cores");
		cpu_cores       = JsonUtils.getInt(json, "num_cpu_sockets");
		cpu_threads     = JsonUtils.getInt(json, "num_cpu_threads");
		vm_count        = JsonUtils.getInt(json, "num_vms");
	}
	
	public String getName() {
		return name;
	}
	
	public long getCPUCapacity() {
		return cpu_capacity;
	}
	
	public int getCPUCores() {
		return cpu_cores;
	}
	
	public int getVMCount() {
		return vm_count;
	}
	
	public Quantity getMemory() {
		return new Memory(memory_capacity, MemoryUnit.B);
	}
	
	public String toString() {
		return "host-"+getName() 
			+ " cpu:" + getCPUCapacity() 
			+ "(hz) cores:" + getCPUCores()
			+ " vms:" + getVMCount();
	}
}
