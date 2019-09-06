package com.nutanix.bpg.measure.script;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.utils.StringUtils;

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
public class CommandExecutor {
	//private String executable;
	private String name             = "";
	//private String[] commandLineOptions = {};
	private final ProcessBuilder pb;
	private StreamReader<?> outputStreamReader;
	private StreamReader<?> errorStreamReader;
	
	private static Logger logger = LoggerFactory.getLogger(CommandExecutor.class);
	
	/**
	 * create a command executor.
	 */
	public CommandExecutor() {
		this("remote");
	}
	
	/**
	 * create a command executor with given name.
	 */
	public CommandExecutor(String name) {
		pb = new ProcessBuilder();
		pb.redirectError(Redirect.PIPE);
		pb.redirectOutput(Redirect.PIPE);
		setName(name);
	}
	
//	public String getExecutable() {
//		return executable;
//	}
//	
//
//	public void setExecutable(String ex) {
//		this.executable = ex;
//	}

	public String getName() {
		return name;
	}
	
	public CommandExecutor setName(String name) {
		this.name = name;
		return this;
	}
//	public CommandExecutor setCommandLineOptions(String[] cmds) {
//		return setCommandLineOptions(Arrays.asList(cmds));
//	}
//	public CommandExecutor setCommandLineOptions(List<String> cmds) {
//		assert cmds != null    : "null commands";
//		assert !cmds.isEmpty() : "empty command";
//		commandLineOptions = cmds.toArray(new String[cmds.size()]);
//		return this;
//	}
	
	public CommandExecutor setDirectory(File d) {
		assert d != null       : "can not set null working directory";
		assert d.isDirectory() : "working directory " + d + " is not a directory!";
		pb.directory(d);
		return this;
	}
	
	public CommandExecutor setEnv(Map<String, String> env) {
		pb.environment().putAll(env);
		return this;
	}

	
	/**
	 * launches a remote process.
	 * The remote process output/error stream are parsed
	 * by given parsers.
	 * <p>
	 * The parser holds {@link StreamParser#getResult()
	 * result of parse}.
	 * 
	 * <p>
	 * Precondition:
	 *      * executable have been set.
	 *      
	 * @param outputParser a parser to parse remote 
	 * process standard output stream
	 * @param errorParser a parser to parse remote 
	 * process standard error stream
	 * 
	 * NOTE: 
	 * Both streams must be consumed.
	 * 
	 * @return
	 * @throws IllegalStateException if no command
	 * have {@link #setCommands(List) set} been
	 * to this receiver.
	 */
	public <T> void executeRemoteProcess(
			String[] commands,
			StreamParser<T> outputParser,
			StreamParser<Object> errorParser) throws Exception {
		
		Objects.requireNonNull(outputParser);
		Objects.requireNonNull(errorParser);
		logger.info("running [" + StringUtils.join(" ", commands) + "]");
		Process p = null;
		try {
			p = pb.command(commands).start();
			setStreamReader(p, outputParser, errorParser);
		} catch (Exception ex) {
			String msg = "error starting command " + StringUtils.join(" " , commands);
			ex.printStackTrace();
			if (commands.length == 1) {
				msg += " commnon cause of this error is commands were "
					+ " supplied as a concatenated string"
					+ " but expected an array of commands";
			}
			throw new ProcessStartException(msg, ex);
		}
	}
	
	public void executeRemoteProcessAndWait(
			String[] commands) throws Exception {
		
		logger.info("running [" + StringUtils.join(" ", commands) + "]");
		Process p = null;
		try {
			p = pb.command(commands).start();
			logger.debug("waiting for remote process to comlete");
			int status = p.waitFor();
			logger.debug("remote process return status " +  status);
			if (status == 0) {
				return;
			} else {
				throw new RuntimeException("can not initailize PGBench."
						+ " commad was [" + StringUtils.join(" ", commands)
						+ "] returned process status " + status);
			}
		} catch (Throwable ex) {
			String msg = "error starting command " + StringUtils.join(" ", commands);
			ex.printStackTrace();
			if (commands.length == 1) {
				msg += " commnon cause of this error is commands were "
					+ " supplied as a concatenated string"
					+ " but expected an array of commands";
			}
			throw new ProcessStartException(msg, ex);
		}
	}

	private <T> void setStreamReader(Process p, 
			StreamParser<T> outputParser, 
			StreamParser<Object> errorParser) 
		throws Exception {
		if (errorParser != null) {
			errorStreamReader = new OutputStreamReader<Object>(p.getErrorStream(), errorParser);
			new Thread(errorStreamReader, "error stream reader")
				.start();
		}
		if (outputParser != null) {
			outputStreamReader = new OutputStreamReader<T>(
					p.getInputStream(), outputParser);
			new Thread(outputStreamReader, "output stream reader").start();
		}
	}
	public String toString() {
		return "commandExecutor:" + getName();
	}

}
