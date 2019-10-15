package junit.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobBuilder;
import com.nutanix.bpg.job.JobDescription;
import com.nutanix.bpg.job.JobTemplate;
import com.nutanix.bpg.job.ScriptOption;
import com.nutanix.job.execution.VariableParser;

public class TestTemplate {
	
	@Test
	public void testInstantiateTemplateReplacesVariables() throws Exception {
		String name = "test-template.yml";
		JobTemplate template = new JobTemplate(getJSON(name));
		
		assertEquals("nutest", template.getName());
		Map<String, String> vars = new HashMap<>();
		String cluster = "10.46.31.26";
		vars.put("cluster", cluster);
		List<ScriptOption> options = template.getScriptOptions();
		assertFalse(options.isEmpty());
		boolean found = false;
		for (ScriptOption option : options) {
			if ("clusters".equals(option.getKey())) {
				found = true;
				break;
			}
		}
		assertTrue("expected option [clusters]", found);
		
		JobBuilder builder = new JobBuilder();
		JobDescription jobSpec = new JobDescription();
		jobSpec.setOption("cluster", cluster);
		Job job = builder.build(template, jobSpec);
		
		builder.setJobCommand(template, job);
		List<String> values = job.getCommand();
		assertTrue(values + " do not contain " + cluster,
				values.contains(cluster));
	}
	

	
	@Test
	public void testVariableParser() {
		String original = "${cluster}";
		List<String> varNames = VariableParser.parse(original);
		assertEquals(1, varNames.size());
	}
	
	@Test
	public void testReplacement() {
		String original = "${cluster}";
		String after = original.replaceAll("\\$\\{cluster\\}", "10.15.254.161");
		assertEquals("10.15.254.161", after);
	}


	
	public JsonNode getJSON(String name) throws Exception {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		InputStream in = getInputStream(name);
		return mapper.readTree(in);
		
	}
	
	public InputStream getInputStream(String name) throws Exception {
		URL code = this.getClass().getProtectionDomain()
				.getCodeSource().getLocation();
		Path path = Paths.get(code.toURI()).resolve(name);
		
		return Files.newInputStream(path);
	}
	
	/**
	 * 	@BeforeClass 
	public static void init() throws Exception {
		CodeSource source = TestTemplate.class
				.getProtectionDomain()
				.getCodeSource();
		URI codePath = source.getLocation().toURI();
	
		
		//System.err.println(path.toFile().getAbsolutePath());
	}

	 */
}
