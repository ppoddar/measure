package com.nutanix.job.execution;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.utils.StringUtils;

/**
 * execute a local script on a separate Java process.
 * this class does not handle remoting, but by the
 * script itself.
 *  
 * 
 * 
 * 
 * @author pinaki.poddar
 *
 */
public class ScriptExecutor implements Callable<Exception> {
	private List<String> commands;
	private File output;
	private File err;
	private String directory;
	private String osCommand;
	private Map<String, String> env;
	
	private static Logger logger = LoggerFactory.getLogger(ScriptExecutor.class);
	
	/**
	 * 
	 * @param e
	 * @return
	 */
	public ScriptExecutor withEnvironment(Map<String, String> e) {
		if (e == null) return this;
		env = new HashMap<String, String>();
		env.putAll(e);
		return this;
	}

	public ScriptExecutor withOutput(File f) {
		output = f;
		return this;
	}
	
	public ScriptExecutor withErrorOutput(File f) {
		err = f;
		return this;
	}
	
	
	public ScriptExecutor withDirectory(String f) {
		directory = f;
		return this;
	}
	/**
	 * sets command to be executed.
	 * 
	 * @param s at least one command must be present
	 * Each command is a separate element. For example,
	 * <code>ls -l </code> is two elements
	 * @return
	 */
	public ScriptExecutor withCommand(List<String> s) {
		if (s == null || s.isEmpty()) {
			throw new IllegalArgumentException("can not set null/empoty command");
		}
		this.commands = new ArrayList<>(s);
		return this;
	}
	
	
	// echo 'set -- -l; cd www' | cat - remote.script | ssh -T root@10.15.254.161	/**

	
	
	
	/* executes a script in a separate  process
	 * and waits for it to complete.
	 * 
	 * @return exception, if any
	 * @throws Exception if command itself is invalid.
	 * If command is valid, but its execution fails,
	 * the error is written to error output 
	 */
	public Exception call() throws Exception {
		if (this.commands == null || this.commands.isEmpty()) {
			throw new IllegalStateException("can not execute script. no commands has been set");
		}
		osCommand = "";
		List<String> fullCmds = new ArrayList<>();
		fullCmds.addAll(this.commands);

		ProcessBuilder pb = new ProcessBuilder();
		pb.command(fullCmds);
		if (env != null && !env.isEmpty()) {
			pb.environment().putAll(env);
		}
		if (err != null) {
			pb.redirectError(err);
		} else {
			pb.redirectError(Redirect.PIPE);
		}
		if (output != null) {
			pb.redirectOutput(output);
		} else {
			pb.redirectOutput(Redirect.PIPE);
		}
		if (!StringUtils.isEmpty(directory)) {
			pb.directory(new File(directory));
		}
		osCommand = StringUtils.join(' ', commands);
		logger.debug("executing " + osCommand);
		Process p = pb.start();
		p.waitFor();
		
		return null;
	}
	
	public String getOSCommand() {
		return osCommand;
	}

	
}

//if (scriptArgs != null && scriptArgs.length > 0) {
//commands.add("echo");
//commands.add(QUOTE);
//commands.add("set");
//commands.add(PACIFY_BASH);
//commands.addAll(Arrays.asList(scriptArgs));
//if (remoteDirectory != null) {
//	commands.add(SEMICOLON);
//	commands.add("cd");
//	commands.add(remoteDirectory);
//}
//commands.add(QUOTE);
//}
//commands.add(LINE_BREAK);
//if (!commands.isEmpty()) {
//commands.add(PIPE);
//}
//commands.add("cat");
//commands.add("-");
//commands.add(script.getAbsolutePath());
//commands.add(LINE_BREAK);
//commands.add(PIPE);
//commands.add("ssh");
//commands.add("-T");
//commands.add(remoteUser + "@" + remoteHost);
//commands.add(LINE_BREAK);
/* <p>
* Assuming, we want listing of remote directory <code>www</code>
* we have a script named <code>test.script</code> with content
* <pre>
*   #!/bin/sh
*   ls $*
* </pre>
* We  
* <pre>
* echo ' set -- -l ' | cat - test.script | ssh -T root@10.15.254.161 > test.out 2> test.err
* </pre>
*/