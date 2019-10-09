package junit.allocation;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutanix.resource.model.DataObjectModel;

public class TestDataModel {
	public JsonNode readJson(String path) throws Exception {
		InputStream in = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(path);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(in);
	}
	
	@Test
	public void test() throws Exception {
		JsonNode json = readJson("modelvms.json");
		
		DataObjectModel model = new DataObjectModel();
		model.buildType("VirtualMachine", json);
		model.print();
	}

}
