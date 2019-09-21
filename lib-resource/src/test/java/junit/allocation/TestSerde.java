package junit.allocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nutanix.bpg.utils.JsonUtils;
import com.nutanix.bpg.utils.ResourceUtils;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.ResourceManager;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.ResourceProvider;
import com.nutanix.resource.impl.DefaultCapacity;
import com.nutanix.resource.impl.DefaultResourcePool;
import com.nutanix.resource.impl.ResourceManagerImpl;
import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.VirtualMachine;
import com.nutanix.capacity.serde.CapacityDeserilaizer;
import com.nutanix.capacity.serde.CapacitySerializer;
import com.nutanix.capacity.CPU;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Memory;
import com.nutanix.capacity.MemoryUnit;

public class TestSerde {
	private static ObjectMapper mapper;
	@BeforeClass
	public static void setResourceManger() {
		String cwd = System.getProperty("user.dir");
		String rsrcURL = "file:///" + cwd + "/src/test/resources/clusters.yml";
		Properties props = new Properties();
		props.setProperty(ResourceManager.CATALOG_CLUSTER_URL, rsrcURL);
		ResourceManagerImpl.setProperties(props);
		
		mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addSerializer(new CapacitySerializer());
		//module.addSerializer(new QuantitySerializer());
		module.addDeserializer(Capacity.class, new CapacityDeserilaizer());
		//module.addDeserializer(Quantity.class, new QuantityDeserializer());
		mapper.registerModule(module);
		
//		mapper.registerSubtypes(new NamedType(DefaultResourcePool.class));
//		mapper.registerSubtypes(new NamedType(DefaultResourceProvider.class));
		
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
	
	
	void assertRoundTrip(Object obj, Class<?> cls) throws Exception {
		String serailized = mapper.writerWithDefaultPrettyPrinter()
				.writeValueAsString(obj);
		JsonNode deserialized = 
		mapper.readTree(serailized);
		
		Object obj2 = mapper.convertValue(deserialized, cls);
		
		System.err.println("Serailized " + obj 
				+ "\n" + serailized);
		
		assertEquals(obj, obj2);
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
	public void testSerializeCapacity() throws Exception  {
		Capacity cap  = new DefaultCapacity();
		cap.addQuantity(new Memory(123, MemoryUnit.MB));
		cap.addQuantity(new CPU(32));
		assertRoundTrip(cap, Capacity.class);
		
	}
	@Test
	public void testPoolRoundtrip() throws Exception  {
		ResourcePool pool = new DefaultResourcePool();
		pool.setName("test");
		ResourceProvider cluster = new Cluster("test");
		cluster.setName("tomahwak");
		pool.addProvider(cluster);
		
		VirtualMachine vm1 = new VirtualMachine("vm1");
		VirtualMachine vm2 = new VirtualMachine("vm2");
		vm1.addQuanity(new Memory(100, "GB"));
		vm2.addQuanity(new Memory(500, "MB"));
		cluster.addResource(vm1);
		cluster.addResource(vm2);
		
		Capacity demand = new DefaultCapacity();
		demand.addQuantity(new Memory(10, "GB"));
		
		Allocation alloc = pool.allocate(demand);
		
		assertNotNull(alloc);

		assertRoundTrip(pool, ResourcePool.class);
		assertRoundTrip(cluster, ResourceProvider.class);
		assertRoundTrip(vm1, VirtualMachine.class);
		assertRoundTrip(vm2, VirtualMachine.class);
		assertRoundTrip(alloc, Allocation.class);
	}
	
	
	@Test
	public void testCapacityRoundtrip() throws Exception {
		Capacity demand = new DefaultCapacity();
		demand.addQuantity(new Memory(10, "GB"));
		
		assertRoundTrip(demand, Capacity.class);
	}
	
	
	@Test
	public void testCapacity() {
		String cwd = System.getProperty("user.dir");
		String rsrcURL = "file:///" + cwd + "/src/test/resources/capacity.json";
		URL url = ResourceUtils.getURL(rsrcURL);
		JsonNode json = JsonUtils.readResource(url, false);
		mapper.convertValue(json.get("demand"), Capacity.class);
	}
}
