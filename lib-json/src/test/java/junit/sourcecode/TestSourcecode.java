package junit.sourcecode;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nutanix.resource.json.Json2POJO;
import com.nutanix.resource.json.Sourcecode;
import com.nutanix.resource.json.SourcecodeModel;

public class TestSourcecode {

	//@Test
	public void testModel() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode json = mapper.createObjectNode();
		json.put("a", 1234);
		json.put("b", "test");
		Json2POJO builder = new Json2POJO();
		
		SourcecodeModel model =
				builder.generateModel("test", "xyz", json);
		for (Sourcecode c : model) {
			c.write(System.out);
		}
	}
	
	//@Test
	public void testSingleClass() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode json = mapper.createObjectNode();
		json.put("a", 1234);
		json.put("b", "test");
		Json2POJO builder = new Json2POJO();
		
		SourcecodeModel model = 
				builder.generateModel("a.b.c", "D", json);
		for (Sourcecode c : model) {
			c.write(System.out);
		}
	}
	
	@Test
	public void testMultipleClass() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode json = mapper.createObjectNode();
		ObjectNode obj = mapper.createObjectNode();
		obj.put("c", "");
		json.put("a", 1234);
		json.put("b", "test");
		json.set("obj", obj);
		Json2POJO builder = new Json2POJO();
		
		SourcecodeModel model = 
				builder.generateModel("a.b.c", "D", json);
		for (Sourcecode c : model) {
			c.write(System.out);
		}
	}


}
