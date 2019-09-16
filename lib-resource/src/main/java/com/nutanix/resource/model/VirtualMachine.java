package com.nutanix.resource.model;

import java.util.Collection;

import com.nutanix.resource.Capacities;
import com.nutanix.resource.Capacity;
import com.nutanix.resource.Resource;
import com.nutanix.resource.impl.DefaultCapacities;
import com.nutanix.resource.impl.unit.CPU;
import com.nutanix.resource.impl.unit.Memory;
import com.nutanix.resource.impl.unit.MemoryUnit;
import com.nutanix.resource.impl.unit.Storage;

public class VirtualMachine implements Resource {
	private String uuid;
	private String name;
	private Capacities limit      = new DefaultCapacities();
	private Capacities available = new DefaultCapacities();

	public VirtualMachine() {
		
	}
	public VirtualMachine(String id) {
		this(id, id);
	}
	
	public VirtualMachine(String id, String name) {
		setId(id);
		setName(name);
	}
	
	public String getId() {
		return uuid;
	}
	
	public void setId(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	public void setCpuCount(int cpus) {
		addCapacity(new CPU(cpus));
		
	}
	
	public void setMemory(int memory) {
		addCapacity(new Memory(memory, MemoryUnit.MB));
	}
	
	
	
	public void setDiskSize(long size) {
		addCapacity(new Storage(size, MemoryUnit.B));
	}
	
	

	@Override
	public Capacities getCapacities() {
		return available;
	}

	@Override
	public boolean hasKind(Resource.Kind kind) {
		return available.hasKind(kind);
	}

	@Override
	public Collection<Resource.Kind> getKinds() {
		return available.getKinds();
	}

	@Override
	public Resource addCapacity(Capacity cap) {
		available.addCapacity(cap);
		limit.addCapacity(cap);
		return this;
	}
	
	public String toString() {
		return getName();
	}

	@Override
	public Capacity getCapacity(Resource.Kind kind) {
		return available.getCapacity(kind);
	}

	@Override
	public Capacities getMaxCapacities() {
		return limit;
	}

	@Override
	public Resource reduceCapacity(Capacities q) {
		available.reduceCapacities(q);
		return this;
	}

}
