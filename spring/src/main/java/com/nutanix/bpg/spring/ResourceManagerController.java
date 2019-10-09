package com.nutanix.bpg.spring;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.JobToken;
import com.nutanix.bpg.repo.Repository;
import com.nutanix.bpg.repo.RepositoryImpl;
import com.nutanix.bpg.scheduler.JobImpl;
import com.nutanix.bpg.scheduler.JobQueueManager;
import com.nutanix.bpg.scheduler.JobQueueManagerImpl;
import com.nutanix.bpg.utils.JsonUtils;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.CapacityFactory;
import com.nutanix.config.Configuration;
import com.nutanix.job.execution.JobBuilder;
import com.nutanix.job.execution.JobTemplate;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.ResourceManager;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.ResourceProvider;
import com.nutanix.resource.impl.ResourceManagerImpl;
import com.nutanix.resource.model.VirtualMachine;

@RequestMapping("/resource")
@RestController
public class ResourceManagerController extends MicroService {
	private ResourceManager resourceManager;
	private JobQueueManager jobQueueManager;
	private Repository      repo;
	
	@Autowired ServletContext ctx;
	@Autowired ObjectMapper customMapper;
	private static final Logger logger = LoggerFactory.getLogger(ResourceManagerController.class);
	
	@PostConstruct
	public void initService() throws Exception {
		Configuration config = loadConfiguration("resource");
		resourceManager    = ResourceManagerImpl.configure(config);
		jobQueueManager    = JobQueueManagerImpl.configure(loadConfiguration("jobQueue"));
		repo               = RepositoryImpl.configure(loadConfiguration("database"));
		for (String name : resourceManager.getPoolNames()) {
			jobQueueManager.newQueue(name, repo);
		}
	}
	
	
	@GetMapping("/pools")
	public Collection<ResourcePool> getResourcePools() {
		return resourceManager.getResourcePools();
	}
	
	@GetMapping("/pool/names")
	public Collection<String> getResourcePoolNames() {
		List<String> names = new ArrayList<String>();
		for (ResourcePool pool : resourceManager.getResourcePools()) {
			names.add(pool.getName());
		}
		return names;
	}
	
	


	
	@GetMapping("/pool/")
	public ResourcePool getResourcePoolByName(@RequestParam ("name")  String name) {
		ResourcePool pool = resourceManager.getResourcePool(name);
		if (pool == null) {
			throw new IllegalArgumentException("resource pool named [" + name + "] does not exist"
					+ " availble pools are " + resourceManager.getPoolNames());
		}
		return pool;
		
	}

	@GetMapping("/pool/{name}/capacity")
	public Capacity getUtilization(
			@PathVariable("name")  String name) {
		ResourcePool pool = resourceManager.getResourcePool(name);
		return pool.getTotalCapacity();
	}
	
	@GetMapping("/pool/{name}/clusters")
	public List<ResourceProvider> getClusters(
			@PathVariable("name")  String name) {
		ResourcePool pool = getResourcePoolByName(name);
		
		return pool.getProviders();
	}
	
	
//	@PostMapping(value="/pool/{name}/allocate",
//		consumes=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Allocation allocate(
			@PathVariable("name")  String name,
			@RequestBody Capacity demand) {
		try {
			logger.info("received allcation request for [" + name + "] pool");
			ResourcePool pool = getResourcePoolByName(name);
			logger.info("found allcation pool " + pool);
			if (pool == null) {
				throw new IllegalArgumentException("pool named [" + name + "] does not exist. "
						+ " available pool names are " + resourceManager.getPoolNames());
			}
			logger.info("allocating demand  " + demand);
			return pool.allocate(demand);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Submits a Job.
	 * 
	 * @param poolName
	 * @param payload
	 * @return
	 */
	@Async
	@PostMapping(value="/job/{category}/",
			consumes=MediaType.TEXT_PLAIN_VALUE)
	public Future<JobToken> submitJob(
			@PathVariable("category") String jobCategory,
			@RequestBody String payload) {
		logger.info("received " + payload);
		try {
			JsonNode json     = customMapper.readTree(payload);
			ResourcePool pool = resourceManager.getResourcePool(
					jobQueueManager.getResourcePoolSelectionPolicy()
					.select(jobCategory));
					
			Capacity demand = CapacityFactory.newCapacity(
					JsonUtils.getMap(json, "demand"));
			
			Allocation alloc = pool.allocate(demand);
			
			JobTemplate template = jobQueueManager.getJobTemplate(jobCategory);
			JobBuilder builder   = jobQueueManager.getJobBuilder();
			Map<String, String> optionValues =
					new HashMap<>();
			//optionValues.put("cluster", ((VirtualMachine)alloc.getSupply()).getIPAddress());
			optionValues.put("cluster", "10.46.31.26");
			JobImpl job = builder.build(template, json, optionValues);

			job.setSupply(alloc.getSupply(), alloc.getDemand());
			JobQueue queue = jobQueueManager.getQueue(pool.getName());
			
			JobToken token = queue.addJob(job);
			token.setRoot(Paths.get(ctx.getRealPath("/")));
			logger.debug("returning job token " + token);
			return CompletableFuture.completedFuture(token);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	/**
	 * for offline testing
	 * @param mapper
	 */
	public void setObjectMapper(ObjectMapper mapper) {
		this.customMapper = mapper;
	}
	
}
