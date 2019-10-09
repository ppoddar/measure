package com.nutanix.bpg.job;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.scheduler.JobImpl;
import com.nutanix.job.execution.ScriptExecutor;

public class JobExecutorImpl implements JobExecutor {
	private final Path outputRoot;
	private final JobToken token;
	private static Logger logger = LoggerFactory.getLogger(JobExecutorImpl.class);
	
	/**
	 * an executor requires a {@link JobToken token}
	 * that provides the {@link Job job} to execute
	 * and root w.r.t. which all output|error paths
	 * are relativized.
	 * 
	 * @param t
	 * @param root
	 * @throws IOException
	 */
	public JobExecutorImpl(JobToken t, Path root) throws IOException {
		this.outputRoot = root;
		if (outputRoot == null) {
			throw new IllegalArgumentException("null output root path");
		}
		if (!outputRoot.toFile().exists()) {
			logger.debug("output root " + root.toUri() + " does not exists. creating directory");
			outputRoot.toFile().mkdirs();
		} else if (!outputRoot.toFile().isDirectory()) {
			throw new IllegalArgumentException("output root path " + outputRoot.toUri() + " is not a directory");
		}
		this.token = t;
		token.setOutput(createOutputFor(t));
		token.setErrorOutput(createErrorOutputFor(t));
	}
	/**
	 * executes given job in a remote process. standard and error output of remote
	 * process are found in two remote files.
	 * 
	 */
	@Override
	public void execute() {
		try {
			JobImpl job = token.getJob();
			ScriptExecutor script = new ScriptExecutor()
					.withDirectory(job.getWorkingDirectory())
					.withEnvironment(job.getEnvironment())
					.withOutput(token.getOutputFile())
					.withErrorOutput(token.getErrorOutputFile())
					.withCommand(job.getCommand());

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					job.setStartTime(System.currentTimeMillis());
					try {
						Exception ex = script.call();
						if (ex != null) {
							logger.debug("job returned error below ");
							ex.printStackTrace();
							token.setError(ex);
						} else {
							if (!token.hasErrorOutput()) {
								logger.info("job [" + job.getName() + "] completed without error");
							} else {
								logger.warn("job [" + job.getName() + "] completed with error\r\n"
										+ token.getErrorMessage());
								
							}
						}
					} catch (Exception ex) {
						logger.debug("job execution error below ");
						ex.printStackTrace();
						token.setError(ex);
					}
				}
			};
			CompletableFuture<?> promise = CompletableFuture.runAsync(runnable);
			token.setPromise(promise);
		} catch (Exception ex) {
			token.setError(ex);
			ex.printStackTrace();
		}
	}


	/**
	 * creates output path for this job.
	 * 
	 * @param token
	 * @return
	 */
	public Path createOutputFor(JobToken token) throws IOException {
		return createPath("job-" + token.getId(), token, false);
	}

	/**
	 * creates error output path for this job.
	 * 
	 * @param token
	 * @return
	 */
	public Path createErrorOutputFor(JobToken token) throws IOException {
		return createPath("job-" + token.getId(), token, true);
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
	private Path createPath(String name, JobToken token, boolean error) throws IOException {
			String fileName = name + (error ? ".err" : ".out");
			Path path = outputRoot.resolve(fileName);
			logger.debug("path to file:" + path.toUri());
			logger.debug("output root:" + outputRoot.toUri());
			
			File file = path.toFile();
			logger.debug("creating output|error file:" + file);
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				logger.debug("created file " + file.getAbsolutePath());
			} catch (Exception e) {
				logger.warn("error creating " + file + " see reasons below");
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			if (error) {
				token.setErrorOutput(path);
			} else {
				token.setOutput(path);
			}
			return path;
	}
	
}
