package com.nutanix.bpg.job.impl;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobBuilder;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.JobQueueManager;
import com.nutanix.bpg.job.JobScheduler;
import com.nutanix.bpg.job.JobTemplate;
import com.nutanix.bpg.job.ResourcePoolSelectionPolicy;
import com.nutanix.bpg.job.TemplateFactory;
import com.nutanix.bpg.model.Catalog;
import com.nutanix.bpg.model.CatalogBuilder;
import com.nutanix.bpg.utils.JsonUtils;
import com.nutanix.config.Configuration;
import com.nutanix.resource.Resource;

/**
 * manages multiple {@link JobQueue queues}.
 * for each queue runs two threads, 
 * <ul>
 * 	<li>Execution: picks up job to be executed. 
 *      Does not remove the job,
 *      only updates its status.
 *  <li>Cleaner: picks up job that expired 
 *      and removes them from queue.
 * </ul>
 * 
 * @author pinaki.poddar
 *
 */
public class JobQueueManagerImpl implements JobQueueManager {
	private Catalog<JobQueue> queues;
	private Catalog<JobTemplate> jobTemplates;
	private Path outputRoot;
	private ResourcePoolSelectionPolicy poolSelectionPolicy;
	private static JobQueueManager singleton;
	private static Configuration config;
	public static final Logger logger = LoggerFactory.getLogger(JobQueueManagerImpl.class);
	
	public static JobQueueManager configure(Configuration conf) {
		config = conf;
		return instance();
	}
	
	/**
	 * get singleton instance
	 * 
	 * @return
	 */
	public static JobQueueManager instance() {
		if (singleton == null) {
			singleton = new JobQueueManagerImpl();
		} 
		return singleton;
	}
	
	private JobQueueManagerImpl() {
		queues = new Catalog<>();
		outputRoot = config.resolvePath("job-output");
		Path templateDir = config.resolvePath("template-root");
		jobTemplates =
			new CatalogBuilder<JobTemplate>()
			.withDirectory(templateDir)
		    .withFactory(new TemplateFactory())
			.build();
		Map<String, String> job2Pool = JsonUtils.getMap(config.asJson(), "job2pool");
		poolSelectionPolicy = new DefaultResourcePoolSelectionPolicy(job2Pool);
	}
	
	/**
	 * creates a new queue of given name.
	 * starts Executor and Cleaner threads 
	 */
	@Override
	public JobQueue newQueue(String name) {
		JobQueueImpl queue = new JobQueueImpl(this, name);
		logger.info("created new queue " + queue);
		queues.add(queue);
		return queue;
	}
	
	/**
	 * get queue of given name.
	 */
	@Override
	public JobQueue getQueue(String name) {
		if (queues.has(name)) {
			return queues.get(name);
		} else {
			JobQueue queue = newQueue(name);
			queues.add(queue);
			return queue;
		}
	}
	
	/**
	 * add a job to given queue.
	 */
	@Override
	public JobScheduler addJob(JobQueue queue, Job job, Resource r) 
	throws Exception {
		queue.addJob(job);
		
		return null;
	}

	@Override
	public Collection<String> getQueues() {
		return queues.names();
	}
	@Override
	public JobBuilder getJobBuilder() {
		return new JobBuilder();
		
	}
	@Override
	public JobTemplate getJobTemplate(String name) {
		return jobTemplates.get(name);
	}
	
	@Override
	public ResourcePoolSelectionPolicy getResourcePoolSelectionPolicy() {
		return poolSelectionPolicy;
	}
	
	
	
}
