package junit.allocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nutanix.resource.Capacities;
import com.nutanix.resource.ResourceManager;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.ResourceProvider;
import com.nutanix.resource.impl.DefaultAllocation;
import com.nutanix.resource.impl.DefaultCapacities;
import com.nutanix.resource.impl.DefaultResourcePool;
import com.nutanix.resource.impl.ResourceManagerImpl;
import com.nutanix.resource.impl.unit.CPU;
import com.nutanix.resource.impl.unit.Memory;
import com.nutanix.resource.impl.unit.MemoryUnit;
import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.VirtualMachine;
import com.nutanix.resource.model.serde.CapacityDeserilaizer;
import com.nutanix.resource.model.serde.CapacitySerializer;

public class TestSerde {
	private static ResourceManager mgr;
	private static ObjectMapper mapper;
	@BeforeClass
	public static void setResourceManger() {
		String cwd = System.getProperty("user.dir");
		String rsrcURL = "file:///" + cwd + "/src/test/resources/clusters.yml";
		Properties props = new Properties();
		props.setProperty(ResourceManager.CATALOG_CLUSTER_URL, rsrcURL);
		ResourceManagerImpl.setProperties(props);
		mgr = ResourceManagerImpl.instance();
		
		mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addSerializer(new CapacitySerializer());
		module.addDeserializer(Capacities.class, new CapacityDeserilaizer());
		mapper.registerModule(module);
		
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	}
	
//	@Test
	public void test() throws Exception {
		String rsrc = "vms.json";
		InputStream in = Thread.currentThread()
			.getContextClassLoader()
			.getResourceAsStream(rsrc);
		assertNotNull(rsrc + " not found", in);
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		JsonNode json = mapper.readTree(in);
		JsonNode entities = json.get("entities");
		for (JsonNode entity : entities) {
			VirtualMachine vm = mapper.convertValue(entity, VirtualMachine.class);
			System.err.println(vm);
			mapper.writerWithDefaultPrettyPrinter()
			.writeValue(System.out, entity);
			
			JsonNode diskArray = entity.get("vm_disk_info");
			for (JsonNode disk : diskArray) {
				if (!disk.has("size")) continue;
				String size = disk.get("size").asText();
				System.err.println("size=" + size);
			}
		}
	}
	
	@Test
	public void testSerializeCapacities() throws Exception  {
		Capacities cap  = new DefaultCapacities();
		cap.addCapacity(new Memory(123, MemoryUnit.MB));
		cap.addCapacity(new CPU(32));
		
		String json = mapper.writerWithDefaultPrettyPrinter()
		.writeValueAsString(cap);
		System.out.println(json);
		Capacities cap2 = mapper.readValue(json, Capacities.class);
		
		assertEquals(cap, cap2);
		
	}
	@Test
	public void testSerializePool() throws Exception  {
		ResourcePool pool = new DefaultResourcePool();
		pool.setName("test");
		ResourceProvider cluster = new Cluster();
		cluster.setName("tomahwak");
		pool.addProvider(cluster);
		
		VirtualMachine vm1 = new VirtualMachine("vm1");
		VirtualMachine vm2 = new VirtualMachine("vm2");
		vm1.addCapacity(new Memory(100, "GB"));
		vm2.addCapacity(new Memory(500, "MB"));
		cluster.addResource(vm1);
		cluster.addResource(vm2);
		
		String json = mapper.writerWithDefaultPrettyPrinter()
		.writeValueAsString(pool);
		//System.out.println(json);
		
		//System.out.println(pool.getProviderNames());
		
		String json2 = mapper.writerWithDefaultPrettyPrinter()
		.writeValueAsString(pool.getProviderNames());
		//System.out.println(json2);
	}

}
