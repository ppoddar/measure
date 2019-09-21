package com.nutanix.resource.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nutanix.resource.impl.AbstarctResource;
import com.nutanix.capacity.CPU;
import com.nutanix.capacity.CpuUnit;
import com.nutanix.capacity.Memory;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.Storage;

public class VirtualMachine extends AbstarctResource {
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
	
	
	public String toString() {
		return getName();
	}
}
