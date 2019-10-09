package junit.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nutanix.job.execution.JobTemplate;
import com.nutanix.job.execution.VariableParser;

public class TestTemplate {
	@BeforeClass 
	public static void init() throws Exception {
		
		CodeSource source = TestTemplate.class
				.getProtectionDomain()
				.getCodeSource();
		URI codePath = source.getLocation().toURI();
	
		Path path = Paths.get(codePath);
		
		//System.err.println(path.toFile().getAbsolutePath());
	}
	
	@Test
	public void testInstantiateTemplateReplacesVariables() throws Exception {
		String name = "test-template.yml";
		JobTemplate t = new JobTemplate(getJSON(name));
		
		assertEquals("nutest", t.getName());
		Map<String, String> vars = new HashMap<>();
		String cluster = "10.46.31.26";
		vars.put("cluster", cluster);
		Map<String,String> options = t.getCommandOptions();
		assertTrue("expected option [clusters]", options.containsKey("clusters"));
		List<String> values = t.fillCommandOptions(vars);
		assertTrue(values + " do not contain " + cluster,
				values.contains(cluster));
	}
	
	@Test
	public void testInstantiateTemplateMissingVariables() throws Exception {
		String name = "test-template.yml";
		JobTemplate t = new JobTemplate(getJSON(name));
		
		assertEquals("nutest", t.getName());
		Map<String, String> vars = new HashMap<>();
		try {
			t.fillCommandOptions(vars);
			fail("expected error on missing variable");
		} catch (Exception ex) {
			String phrase = "undefined variable";
			assertTrue(ex.getMessage() + " does not conatin " + phrase, 
					ex.getMessage().indexOf(phrase) != -1);
		}
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
}
