package com.nutanix.bpg.spring;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobQueueManager;
import com.nutanix.bpg.job.JobToken;
import com.nutanix.bpg.job.impl.JobImpl;
import com.nutanix.capacity.Capacity;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.ResourceManager;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.ResourceProvider;
import com.nutanix.resource.impl.ResourceManagerImpl;

@RestController
@RequestMapping("/resource")
public class ResourceManagerController {
	ResourceManager api;
	JobQueueManager queue;
	@Autowired ObjectMapper mapper;
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceManagerController.class);
	@PostConstruct
	public void initService() {
		ResourceManagerImpl.setProperties(null);
		api = ResourceManagerImpl.instance();
	}
	
	@GetMapping("/pools")
	public Collection<ResourcePool> getResourcePools() {
		return api.getResourcePools();
	}
	@GetMapping("/pool/")
	public ResourcePool getResourcePoolByName(@RequestParam ("name")  String name) {
		ResourcePool pool = api.getResourcePoolByName(name);
		if (pool == null) {
			throw new IllegalArgumentException("resource pool named [" + name + "] does not exist"
					+ " availble pools are " + api.getPoolNames());
		}
		return pool;
		
	}
	
	@GetMapping("/pool/{id}")
	public ResourcePool getResourcePool(@PathVariable ("id")  String id) {
		ResourcePool pool = api.getResourcePool(id);
		if (pool == null) {
			throw new IllegalArgumentException("resource pool id [" + id + "] does not exist");
		}
		return pool;
		
	}

	
	@GetMapping("/pool/{id}/capacity")
	public Capacity getUtilization(
			@PathVariable("id")  String id) {
		ResourcePool pool = api.getResourcePool(id);
		return pool.getTotalCapacity();
	}
	
	@GetMapping("/pool/{id}/clusters")
	public List<ResourceProvider> getClusters(
			@PathVariable("id")  String id) {
		ResourcePool pool = getResourcePool(id);
		
		return pool.getProviders();
	}
	
	
	@PostMapping(value="/pool/{name}/allocate",
		consumes=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Allocation allocate(
			@PathVariable("name")  String name,
			@RequestBody Capacity demand) {
		try {
			logger.info("received allcation request for [" + name + "] pool");
			ResourcePool pool = getResourcePoolByName(name);
			logger.info("found allcation pool " + pool);
			if (pool == null) {
				throw new IllegalArgumentException("pool named [" + name + "] does not exist. "
						+ " available pool names are " + api.getPoolNames());
			}
			logger.info("allocating demand  " + demand);
			return api.allocate(pool, demand);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	
	@PostMapping(value="/pool/{name}/submit",consumes=MediaType.TEXT_PLAIN_VALUE)
	public JobToken submitJob(
			@PathVariable("name") String poolName,
			@RequestBody String payload) {
		logger.info("received " + payload);
		try {
			JsonNode json = mapper.readTree(payload);
			Capacity demand = mapper.convertValue(json.get("demand"), Capacity.class);
			Allocation alloc = allocate(poolName, demand);
			Job<?,?> job = new JobImpl<>();
			job.setDemand(alloc.getDemand());
			JobToken token = queue.getQueue(poolName).addJob(job);
			return token;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	
	

	
	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
		CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
		loggingFilter.setIncludeClientInfo(false);
		loggingFilter.setIncludeQueryString(true);
		loggingFilter.setIncludePayload(true);
		return loggingFilter;
	}

}
