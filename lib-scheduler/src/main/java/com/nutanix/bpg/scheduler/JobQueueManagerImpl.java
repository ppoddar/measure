package com.nutanix.bpg.scheduler;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.job.DefaultResourcePoolSelectionPolicy;
import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.ResourcePoolSelectionPolicy;
import com.nutanix.bpg.model.Catalog;
import com.nutanix.bpg.model.CatalogBuilder;
import com.nutanix.bpg.repo.Repository;
import com.nutanix.config.Configuration;
import com.nutanix.job.execution.JobBuilder;
import com.nutanix.job.execution.JobTemplate;
import com.nutanix.job.execution.TemplateFactory;
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
	private ResourcePoolSelectionPolicy policy;
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
		Map<String, String> job2Pool = new HashMap<String, String>();
		for (JobTemplate t : jobTemplates) {
			try {
				String poolName = config.getString(t.getName());
				job2Pool.put(t.getName(), poolName);
				
			} catch (RuntimeException ex) {
				throw ex;
			}
		}
		policy = new DefaultResourcePoolSelectionPolicy(job2Pool);
	}
	
	/**
	 * creates a new queue of given name.
	 * starts Executor and Cleaner threads 
	 */
	@Override
	public JobQueue newQueue(String name, Repository repo) {
		logger.info("creating new queue " + name);
		JobQueueImpl queue = new JobQueueImpl(name);
		logger.info("created new queue " + queue);
		
		queues.add(queue);
		logger.info("starting  queue cleaner");
		new Thread(new JobCleaner(queue)).start();
		JobSchedulerImpl scheduler = new JobSchedulerImpl(queue, outputRoot);
		new Thread(scheduler).start();
		
		queue.setScheduler(scheduler);
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
			JobQueue queue = newQueue(name, null);
			queues.add(queue);
			return queue;
		}
	}
	
	/**
	 * add a job to given queue.
	 */
	@Override
	public JobScheduler addJob(JobQueue queue, Job job, Resource r) {
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
		return policy;
	}
	
	
	
}
