package com.nutanix.resource.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nutanix.resource.Resource;
import com.nutanix.resource.impl.AbstarctResource;
import com.nutanix.resource.unit.CPU;
import com.nutanix.resource.unit.CpuUnit;
import com.nutanix.resource.unit.Memory;
import com.nutanix.resource.unit.MemoryUnit;
import com.nutanix.resource.unit.Storage;

public class VirtualMachine extends AbstarctResource {
	@JsonCreator
	public VirtualMachine(@JsonProperty("id") String id) {
		this(id, id);
	}
	
	public VirtualMachine(String id, String name) {
		super(id);
		setName(name);
		
		setPreferredUnit(Resource.Kind.MEMORY,  MemoryUnit.MB);
		setPreferredUnit(Resource.Kind.STORAGE, MemoryUnit.GB);
		setPreferredUnit(Resource.Kind.COMPUTE, CpuUnit.NONE);
		
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
