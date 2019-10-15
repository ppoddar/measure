package junit;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nutanix.config.Configuration;
import com.nutanix.config.YAMLConfiguration;

public class TestConfiguration {

	@Test
	public void testConfiguration() throws Exception {
		JsonNode yaml = getJson("test-config.yml");
		Configuration config = new YAMLConfiguration(yaml);
		
		assertNotNull(config);
		
		assertEquals("x", config.getString("p1"));
	}
	
	
	JsonNode getJson(String rsrc) throws Exception {
		return new ObjectMapper(new YAMLFactory())
				.readTree(getInput(rsrc));
	}
	
	InputStream getInput(String rsrc) {
		InputStream in = Thread.currentThread().getContextClassLoader()
		.getResourceAsStream(rsrc);
		assertNotNull(rsrc + " not in classpath", in);
		return in;
	}

}
