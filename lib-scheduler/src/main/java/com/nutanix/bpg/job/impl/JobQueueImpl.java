package com.nutanix.bpg.job.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobExecutor;
import com.nutanix.bpg.job.JobQueue;
import com.nutanix.bpg.job.JobQueueManager;
import com.nutanix.bpg.job.JobScheduler;
import com.nutanix.bpg.job.JobToken;
import com.nutanix.resource.ResourcePool;


/**
 * a list of {@link Job jobs}.
 * 
 * @author pinaki.poddar
 *
 */
public class JobQueueImpl implements JobQueue {
	private final String name;
	private final JobQueueManager mgr;
	private ResourcePool pool;
	private final Map<String, JobToken> tokens;
	private Path outputRoot;
	private static ExecutorService threadPool
		= Executors.newCachedThreadPool();
	private static Logger logger = LoggerFactory.getLogger(JobQueueImpl.class);
	
	/**
	 * package scoped constructor 
	 */
	public JobQueueImpl(JobQueueManager mgr, String name) {
		this.mgr = mgr;
		this.name = name;
		this.tokens = new HashMap<String, JobToken>();
	}
	
	public JobQueueManager getManager() {
		return mgr;
	}
	
	/**
	 * sets the path to root directory. 
	 * 
	 * @param root typically the root of public/static
	 * directory of a web server. The job output/error
	 * are written to file relative to this directory.
	 * <br>
	 * Must be non-null path. If path exists, it must
	 * be a directory, otherwise the directory is created
	 * direct
	 * 
	 * @return the same receiver
	 */
	public JobQueue setOutputRoot(Path root) {
		if (root == null) {
			throw new IllegalArgumentException("null output root path");
		}
		if (!root.toFile().exists()) {
			logger.debug("output root " + root.toUri() + " does not exists. creating directory");
			root.toFile().mkdirs();
		} else if (!root.toFile().isDirectory()) {
			throw new IllegalArgumentException("output root path " + root.toUri() + " is not a directory");
		}
		outputRoot = root;
		
		return this;
	}
	
	/**
	 * gets a token of given id, if any. Otherwise null
	 * @param id a token identifier. 
	 * @return a job token, or null
	 */
	@Override
	public JobToken getJob(String id) {
		return tokens.get(id);
	}
	
	public Path getOutputRoot() {
		return outputRoot;
	}
	/**
	 * inserts given job to this queue.
	 * notifies all threads waiting on this queue. 
	 * 
	 * @param job
	 * @return a token that wraps the job
	 * @throws Exception
	 */
	@Override
	public JobToken addJob(Job job) throws Exception {
		JobToken token = new JobToken((JobImpl)job, this);
		tokens.put(token.getId(), token);
		logger.debug("added " + tokens.size() + "-th " + token + " to " + this); 

		token.setStatus(Job.Status.QUEUED);
		synchronized (this) {
			notifyAll();
		}
		return token;
	}
	

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Iterator<JobToken> iterator() {
		return tokens.values().iterator();
	}

	
	
	public String toString() {
		return "queue-" + getName();
	}

	@Override
	public ResourcePool getPool() {		
		return pool;
	}
	
	/**
	 * associates this queue with given pool.
	 * <br>
	 * <b>IMPORTANT:</b>
	 * This methods has side-effect: a scheduler
	 * and executor gets started. 
	 * Because their require a pool to function.
	 * <br>
	 * Unless a pool is set, the scheduler would
	 * not start
	 */
	public JobQueue setPool(ResourcePool pool) {
		 this.pool = pool;
		JobScheduler scheduler = new JobSchedulerImpl(this);
		JobExecutor  executor  = new JobExecutorImpl(this);
//		JobCleanerImpl  cleaner    = new JobCleanerImpl(this);
		
		logger.debug("starting processes on " + this);
		threadPool.submit(scheduler);
		threadPool.submit(executor);
//		threadPool.submit(cleaner);
		
		
		 return this;
	}
	/**
	 * create a path with given name.
	 * the path is resolved w.r.t. output root.
	 *  
	 * @param name
	 * @param token
	 * @param error
	 * @return
	 * @throws IOException
	 */
	public Path createPath(String name, boolean error) throws IOException {
		if (outputRoot == null) {
			throw new IllegalStateException("output root is not set. "
					+ " output root must be set to create path to "
					+ " standard output/error for job execution");
		}
		String fileName = name + (error ? ".err" : ".out");
			Path path = outputRoot.resolve(fileName);
			File file = path.toFile();
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (Exception e) {
				logger.warn("error creating " + file + " see reasons below");
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			return path;
	}

	/**
	 * creates output path for this job.
	 * 
	 * @param token
	 * @return
	 */
//	private Path createOutputFor(JobToken token) throws IOException {
//		Path path = createPath("job-" + token.getId(), token, false);
//		logger.debug("create output path for " + token + " " + path.toUri());
//		return path;
//	}

	/**
	 * creates error output path for this job.
	 * 
	 * @param token
	 * @return
	 */
//	private Path createErrorOutputFor(JobToken token) throws IOException {
//		return createPath("job-" + token.getId(), token, true);
//	}
}
