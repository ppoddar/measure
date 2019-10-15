package junit;


import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutanix.bpg.spring.ResourceManagerController;

public class TestResourceManager {
	static ResourceManagerController controller;
	@BeforeClass
	public static void initService() throws Exception {
		System.setProperty("config", "../config/application-dev.yml");
		controller = new ResourceManagerController();
		controller.setObjectMapper(new ObjectMapper());
		controller.initService();
	}
	
	@Test
	public void testOfflineSubmitJob() throws Exception {
		String payload = readFileContent("jobrequest.json");
		controller.submitJob(payload);
	}
	
	String readFileContent(String rsrc) throws Exception {
		InputStream in = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(rsrc);
		assertNotNull(rsrc + " not in classpath", in);
		StringBuffer str = new StringBuffer();
		int ch = 0;
		while ((ch = in.read()) != -1) {
			str.append((char)ch);
		}
		in.close();
		return str.toString();
	}
}
