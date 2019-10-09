package com.nutanix.job.execution;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.utils.StringUtils;

/**
 * Executes a command by invoking a remote process. 
 * the remote process output is piped to this receiver.
 * <p>
 * I/O handling of remote process:
 * Process is executed such that its output and error
 * stream is piped to current process. Separate {@link 
 * StreamReader} is attached to these remote streams.
 * <p>
 * 
 * @author pinaki.poddar
 *
 */
public class CommandExecutor implements Supplier<Boolean>{
	String[] commands;
	File directory;
	File out;
	File err;
	Map<String, String> env;
	
	private static Logger logger = LoggerFactory.getLogger(CommandExecutor.class);
	
	/**
	 * create a command executor.
	 */
	public CommandExecutor() {
	}
	
	public CommandExecutor withCommands(String[] cmds) {
		commands = cmds;
		return this;
	}
	
	public CommandExecutor withDirectory(File d) {
		directory = d;
		return this;
	}
	
	public CommandExecutor withEnv(Map<String, String> env) {
		env = new HashMap<String, String>(env);
		return this;
	}
	
	public CommandExecutor withOutput(File out) {
		this.out = out;
		return this;
	}
	public CommandExecutor withErrorOutput(File err) {
		this.err = err;
		return this;
	}


	private void executeRemoteProcess(
			ProcessBuilder pb,
			boolean wait) throws Exception {
		logger.info("running [" + StringUtils.join(' ', commands) + "]");
		try {
			Process p = pb.command(commands).start();
			logger.debug("waiting for remote process to comlete");
			if (wait)
				p.waitFor();
		} catch (Throwable ex) {
			String msg = "error starting command " + StringUtils.join(' ', commands);
			ex.printStackTrace();
			if (commands.length == 1) {
				msg += " commnon cause of this error is commands were "
					+ " supplied as a concatenated string"
					+ " but expected an array of commands";
			}
			throw new RuntimeException(msg, ex);
		}
	}

	@Override
	public Boolean get() {
		ProcessBuilder pb = new ProcessBuilder();
		if (directory != null) {
			logger.debug("working directory:" + directory);
			pb.directory(directory);
		}
		if (env != null && env.isEmpty()) {
			logger.debug("environment:" + env);
			pb.environment().putAll(env);
		}
		if (out != null) {
			logger.debug("output:" + out);
			pb.redirectOutput(Redirect.to(out));
		} else {
			pb.redirectOutput(Redirect.PIPE);
		}
		if (err != null) {
			logger.debug("error output:" + err);
			pb.redirectError(Redirect.to(err));
		} else {
			pb.redirectError(Redirect.PIPE);

		}
		try {
			executeRemoteProcess(pb, true);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
}
