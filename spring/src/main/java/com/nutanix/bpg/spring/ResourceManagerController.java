package com.nutanix.bpg.spring;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
import com.nutanix.bpg.job.JobBuilder;
import com.nutanix.bpg.job.JobDescription;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.JobQueueManager;
import com.nutanix.bpg.job.JobTemplate;
import com.nutanix.bpg.job.JobToken;
import com.nutanix.bpg.job.impl.JobImpl;
import com.nutanix.bpg.job.impl.JobQueueImpl;
import com.nutanix.bpg.job.impl.JobQueueManagerImpl;
import com.nutanix.bpg.repo.Repository;
import com.nutanix.bpg.repo.RepositoryImpl;
import com.nutanix.capacity.Capacity;
import com.nutanix.config.Configuration;
import com.nutanix.resource.Resource;
import com.nutanix.resource.ResourceManager;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.impl.ResourceManagerImpl;

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
		
		
		/**
		 * each pool is bi-directionaly associated to 
		 * a job queue
		 */
		for (ResourcePool pool : resourceManager.getResourcePools()) {
			String queueName = pool.getName();
			JobQueue queue = jobQueueManager.newQueue(queueName)
				.setPool(pool);
			Path outputRoot = Paths.get(ctx.getRealPath("/"));
			((JobQueueImpl)queue).setOutputRoot(outputRoot);
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
	public List<Resource> getClusters(
			@PathVariable("name")  String name) {
		ResourcePool pool = getResourcePoolByName(name);
		
		return pool.getResources();
	}
	
	
	
	/**
	 * Submits a Job.
	 * 
	 * @param poolName
	 * @param payload
	 * @return
	 */
	@Async
	@PostMapping(value="/job/",
			consumes=MediaType.TEXT_PLAIN_VALUE)
	public CompletableFuture<JobToken> submitJob(@RequestBody String payload) {
		logger.info("received " + payload);
		try {
			JsonNode json     = new ObjectMapper().readTree(payload);
			JobDescription jobSpec = new JobDescription(json);
			String poolName = jobQueueManager
					.getResourcePoolSelectionPolicy()
					.getPoolByJobCategory(jobSpec.getCategory());
			
			JobQueue queue = jobQueueManager.getQueue(poolName);
			JobTemplate template = jobQueueManager
					.getJobTemplate(jobSpec.getCategory());
			JobBuilder builder   = jobQueueManager.getJobBuilder();
			JobImpl job = builder.build(template, jobSpec);
			JobToken token = queue.addJob(job);
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
