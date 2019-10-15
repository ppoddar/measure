package com.nutanix.resource.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.bpg.model.Catalog;
import com.nutanix.bpg.utils.JsonUtils;
import com.nutanix.bpg.utils.Named;
import com.nutanix.capacity.CPU;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Memory;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.Quantity;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.Storage;
import com.nutanix.capacity.Utilization;
import com.nutanix.capacity.impl.DefaultCapacity;
import com.nutanix.capacity.impl.DefaultUtilization;
import com.nutanix.resource.Resource;
import com.nutanix.resource.ResourcePool;

/**
 * represents a Nutanix cluster from resource utilization
 * perspective.
 * A cluster supplies {@link com.nutanix.resource.Resource
 * resource} to a {@link com.nutanix.resource.ResourcePool pool}.
 * A cluster uses {@link PrismConnect connection} to
 * Nutanix cluster via Prism gateway, issues HTTPS requests,
 * parses JSON response  and populates resource pool.
 *  
 * @author pinaki.poddar
 *
 */
public class Cluster implements Resource,Named {
	private String id;
	private String name;
	private String host;
	private int    port;
	private String user;
	private String password;
	private String hypervisor;
	private Catalog<Disk> disks;
	private Catalog<Host> hosts;
	private Catalog<ResourcePool> assignedPools;
	private Capacity total;
	private Capacity available;
	
	private static Logger logger = LoggerFactory.getLogger(Cluster.class);
	
	public Cluster() {
		id = UUID.randomUUID().toString();
		name = "unnamed";
		port = 9440;
		user     = "admin";
		password = "Nutanix.1";
		hypervisor = "AHV";
		initCapacity();
		disks = new Catalog<Disk>();
		hosts = new Catalog<Host>();
		assignedPools = new Catalog<ResourcePool>();
	}
	
	public void populate (JsonNode json) {
		id   = JsonUtils.getString(json, "uuid");
		name = JsonUtils.getString(json, "name");
		host = JsonUtils.getString(json, "cluster_external_ipaddress");
		total = new DefaultCapacity();
		
		JsonNode usage = JsonUtils.getObject(json, "usage_stats");
		long capacity_bytes = JsonUtils.getLong(usage, "storage.capacity_bytes");
		long usage_bytes = JsonUtils.getLong(usage, "storage.usage_bytes");
		
		total = new DefaultCapacity();
		total.setPreferredUnit(ResourceKind.STORAGE, MemoryUnit.TB);
		total.addQuantity(new Storage(capacity_bytes/2, MemoryUnit.B));
		available = new DefaultCapacity();
		available.setPreferredUnit(ResourceKind.STORAGE, MemoryUnit.TB);
		available.addQuantity(new Storage((capacity_bytes-usage_bytes)/2, MemoryUnit.B));
		
		logger.debug("total: " + total);
		logger.debug("available: " + available);
	}
	
	void initCapacity() {
		total     = new DefaultCapacity();
		available = new DefaultCapacity();
		Memory m = new Memory(0, MemoryUnit.MB);
		Storage s = new Storage(0, MemoryUnit.MB);
		CPU c = new CPU(0);
		total.addQuantity(m);
		available.addQuantity(m);
		total.addQuantity(s);
		available.addQuantity(s);
		total.addQuantity(c);
		available.addQuantity(c);
		
		total.setPreferredUnit(ResourceKind.STORAGE, MemoryUnit.GB);
		available.setPreferredUnit(ResourceKind.STORAGE, MemoryUnit.GB);
	}
	
	public void assignTo(ResourcePool pool) {
		assignedPools.add(pool);
	}
	
	public String getHost() {
		return host;
	}
	
	public Cluster setHost(String host) {
		this.host = host;
		return this;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}




	public String toString() {
		return getName() + "@" + getHost();
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHypervisor() {
		return hypervisor;
	}

	public void setHypervisor(String hypervisor) {
		this.hypervisor = hypervisor;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String getName() {
		return name;
	}

	
	
	@Override
	public Quantity getAvailable(ResourceKind kind) {
		return available.getQuantity(kind);
	}

	@Override
	public Quantity getTotal(ResourceKind kind) {
		return total.getQuantity(kind);
	}

	@Override
	public Map<ResourceKind, Utilization> getUtilization() {
		Map<ResourceKind, Utilization> result 
			= new HashMap<ResourceKind, Utilization>();
		for (ResourceKind kind : ResourceKind.values()) {
			result.put(kind, getUtilization(kind));
		}
		return result;
	}
	@Override
	public Utilization getUtilization(ResourceKind kind) {
		Quantity total     = this.total.getQuantity(kind);
		Quantity available = this.available.getQuantity(kind);
		Quantity used = total.minus(available);
		Utilization result = new DefaultUtilization(
			total,
			used,
			available);
		return result;
	}

	@Override
	public boolean acquire(Capacity cap) {
		logger.debug("acquire " + cap + " on " + this);
		logger.debug("capcity before acquire " + this.getAvailableCapacity());
		logger.debug("utilzation before acquire " + this.getUtilization());
		for (Quantity q: cap) {
			reduceQuantity(available, q);
		}
		for (ResourcePool pool : assignedPools) {
			pool.getTotalCapacity().reduceCapacity(cap);
			pool.getAvailableCapacity().reduceCapacity(cap);
		}
		logger.debug("capcity after acquire " + this.getAvailableCapacity());
		logger.debug("utilzation after acquire " + this.getUtilization());
		return true;
	}

	@Override
	public boolean release(Capacity cap) {
		for (Quantity q: cap) {
			addQuantity(available, q);
		}
		for (ResourcePool pool : assignedPools) {
			pool.getTotalCapacity().addCapacity(cap);
			pool.getAvailableCapacity().addCapacity(cap);
		}
		return true;
	}
	
	private void addQuantity(Capacity cap, Quantity q) {
		cap.addQuantity(q);
	}
	
	public void reduceQuantity(Capacity cap, Quantity q) {
		cap.reduceQuantity(q);
	}

	@Override
	public Capacity getAvailableCapacity() {
		return available;
	}

	@Override
	public Capacity getTotalCapacity() {
		return total;
	}
	
	public void addDisk(Disk disk) {
		logger.debug("add " + disk);
		disks.add(disk);
		long diskSize = disk.getSize();
		Quantity q = new Storage(diskSize, MemoryUnit.B);
		total.addQuantity(q);
		available.addQuantity(q);
	}
	
//	public void addHost(Host host) {
//		logger.debug("add " + host);
//		hosts.add(host);
//		int coreCount = host.getCPUCores();
//		Quantity q = new CPU(coreCount);
//		total.addQuantity(q);
//		available.addQuantity(q);
//		
//		Quantity m = new Memory(host.getMemory(), MemoryUnit.B);
//		total.addQuantity(m);
//		available.addQuantity(m);
//	}
	
	/**
	 * adds given quantity to toal and available capacity
	 * of this cluster.
	 * @param q
	 */
	public void addCapacity(Quantity q) {
		total.addQuantity(q);
		available.addQuantity(q);
	}

}
