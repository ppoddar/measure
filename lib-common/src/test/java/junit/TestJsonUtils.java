package junit;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Properties;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nutanix.bpg.utils.JsonUtils;

public class TestJsonUtils {

	@Test
	public void testConvertYMLtoProperties() throws Exception {
		String rsrc = "database.yml";
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		InputStream in = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(rsrc);
		assertNotNull(rsrc + " not in classpath", in);
		JsonNode json = mapper.readTree(in);
		Properties p = JsonUtils.toProperties(json);
		
		p.store(System.out, "created properties");
		
		assertTrue(p.containsKey("catalog.database.url"));
		assertEquals("catalog/databases", 
				p.getProperty("catalog.database.url"));
		assertEquals("jdbc:postgresql://localhost:5432/bpg", 
				p.getProperty("database.url"));
	
	}

}
