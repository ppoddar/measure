package com.nutanix.bpg.measure.spring;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nutanix.resource.Capacities;
import com.nutanix.resource.ResourceManager;
import com.nutanix.resource.ResourcePool;

@RestController
@RequestMapping("/resource")
public class ResourceAllocationController {
	ResourceManager api;
	
	@GetMapping("/pools")
	public List<ResourcePool> getPools() {
		return api.getResourcePools();
	}
	
	@GetMapping("/pool/{id}/capacity")
	public Capacities getUtilization(
			@PathVariable("id")  String id) {
		ResourcePool pool = api.getResourcePool(id);
		return pool.getTotalCapacity();
	}

}
