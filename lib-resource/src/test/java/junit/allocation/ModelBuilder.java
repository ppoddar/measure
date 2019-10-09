package junit.allocation;

import java.io.InputStream;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ModelBuilder {
	ObjectNode meta = null;
	
	public ModelBuilder() {
		ObjectMapper mapper = new ObjectMapper();
		meta = mapper.createObjectNode();
		meta.put("entities",   "Volume");
		meta.put("error_info", "Error");
		meta.put("metadata",   "Meta");
	}
	
	public void build(JsonNode json) throws Exception {
		Iterator<String> fields = json.fieldNames();
		while (fields.hasNext()) {
			String field = fields.next();
			JsonNode node = json.get(field);
			switch (node.getNodeType()) {
			case STRING:
			case BOOLEAN:
			case NUMBER:
			case BINARY:
				System.err.println("value " + field);
				break;
			case OBJECT:
				String type = meta.get(field).asText();
				System.err.println(type + " " +  field);
				break;
			case ARRAY:
				String arraytype = meta.get(field).asText();
				System.err.println(arraytype + "[] " + field);
				break;
			case MISSING:
			case NULL:
			case POJO:
				System.err.println("not handled " + field);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		String path = args[0];
		InputStream in = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(path);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = mapper.readTree(in);
		
		new ModelBuilder().build(json);
				
		
	}

}
