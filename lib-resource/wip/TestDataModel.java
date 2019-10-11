package junit.allocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nutanix.bpg.model.Catalog;
import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.Disk;
import com.nutanix.resource.model.DiskBuilder;
import com.nutanix.resource.model.Host;
import com.nutanix.resource.model.HostBuilder;
import com.nutanix.resource.prism.PrismGateway;

public class TestDataModel {
//	public JsonNode readJson(String path) throws Exception {
//		InputStream in = Thread.currentThread()
//				.getContextClassLoader()
//				.getResourceAsStream(path);
//		ObjectMapper mapper = new ObjectMapper();
//		return mapper.readTree(in);
//	}
//	
//	@Test
//	public void test() throws Exception {
//		JsonNode json = readJson("modelvms.json");
//		
//		DataObjectModel model = new DataObjectModel();
//		model.buildType("VirtualMachine", json);
//		model.print();
//	}
	private static PrismGateway prism;
	
	@BeforeClass
	public static void connectToPrism() {
		Cluster cluster = new Cluster("Tomahawk");
		cluster.setHost("10.46.31.50");
		cluster.setPort(9440);
		
		prism = new PrismGateway(cluster);
	}
	
	@Test
	public void testBuildDisk() throws Exception {
		Catalog<Disk> disks = DiskBuilder.build(prism);
		assertFalse(disks.isEmpty());
		for (Disk disk : disks) {
			System.out.println(disk);
			
			assertEquals(disk.getCapacity(), disk.getSize());
			assertEquals(disk.getCapacity(), 
					disk.getAvailable() + disk.getUsed());
		}
	}
	
	@Test
	public void testBuildHosts() throws Exception {
		Catalog<Host> hosts = HostBuilder.build(prism);
		assertFalse(hosts.isEmpty());
		for (Host host : hosts) {
			System.out.println(host);
		}
	}


}
