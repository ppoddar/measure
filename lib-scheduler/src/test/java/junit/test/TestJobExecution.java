package junit.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.model.Catalog;
import com.nutanix.job.execution.Command;
import com.nutanix.job.execution.CommandExecutor;
import com.nutanix.job.execution.JobBuilder;
import com.nutanix.job.execution.JobTemplate;
import com.nutanix.job.execution.VariableParser;

public class TestJobExecution {
	private static Job job;
	
	@BeforeClass
	public static void createJob() throws Exception {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		String rsrc = "test-job.yml";
		InputStream in = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(rsrc);
		assertNotNull(rsrc + " not found in classpath", in);
		JsonNode  json   = mapper.readTree(in);
		
		Catalog<JobTemplate> templates = new Catalog<>();
		InputStream in2 = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("config/templates/nutest-template.yml");
		assertNotNull(in2);
		JsonNode json2 = new ObjectMapper(new YAMLFactory())
				.readTree(in2);
		JobTemplate t = new JobTemplate(json2);
		templates.add(t);
		Map<String, String> optionValues = new HashMap<>();
		optionValues.put("cluster", "1.2.3.4");
		job = new JobBuilder().build(t,json, optionValues);
	}
	
	@Test
	public void testJobDeserialization() throws Exception {
		InputStream in = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("test/jobrequest.json");
		JsonNode payload = new ObjectMapper()
				.readTree(in);
		Catalog<JobTemplate> templates = new Catalog<>();
		InputStream in2 = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("config/templates/nutest-template.yml");
		JsonNode json = new ObjectMapper(new YAMLFactory())
				.readTree(in2);
		JobTemplate t = new JobTemplate(json);
		templates.add(t);
		Map<String, String> optionValues = new HashMap<>();
		optionValues.put("cluster", "1.2.3.4");
		
		new JobBuilder().build(t,payload, optionValues);
	}
	
	@Test
	public void testCreateJobFromDescriptor() throws Exception {
		assertTrue(job.getEnvironment().isEmpty());
	}
	
//	@Test
//	public void testExecuteJob() throws Exception {
//		JobExecutor executor = new JobExecutorImpl();
//		CompletableFuture<Boolean> future =
//				executor.execute(job);
//		assertTrue(future.get());
//	}
	@Test
	public void testCommandParsing() {
		String cmd = "echo ${msg}";
		Map<String, String> replacements = new HashMap<String, String>();
		replacements.put("msg", "hello parsing");
		List<String> vars = VariableParser.parse(cmd);
		assertFalse("no variable parsed in " + cmd, vars.isEmpty());
		assertEquals(1, vars.size());
		assertEquals("msg", vars.get(0));
		
		Command command = new Command(cmd);
		String[] cmds = command.replaceVariables(replacements);
		assertEquals("echo", cmds[0]);
		assertEquals("hello parsing", cmds[1]);
		
	}
	
	@Test
	public void testRunRemoteCommand() throws Exception {
		File out = new File("remote.out");
		out.createNewFile();
		String[] commands = {"ls"};
		CommandExecutor ex = new CommandExecutor()
				.withOutput(out)
				.withCommands(commands);
		ex.get();
		
		System.out.println("output is at " + out.getAbsolutePath());
		
		new ProcessBuilder("cat", out.getAbsolutePath()).start();
	}

	
	
	
	
	

}
