package junit.allocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nutanix.capacity.CPU;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Memory;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.Storage;
import com.nutanix.capacity.Utilization;
import com.nutanix.capacity.impl.DefaultCapacity;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.ResourceManager;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.impl.ResourceManagerImpl;
import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.ClusterBuilder;
import com.nutanix.resource.model.VirtualMachine;
/**
 * test allocation on configured resource pools
 * 
 * @author pinaki.poddar
 *
 */
public class TestResourcePool {
	
	ResourceManager initResourceManager() {
		// Resource Manager is configured with 
		// two configuration descriptors 
		//  clusters.yml -- cluster coordinates
		//  pools.yml    -- assignment of clusters to pools
		// specify URL location of these two of descriptors
		// with respect to current working directory
//		Properties props = new Properties();
//		String cwd = System.getProperty("user.dir");
//		String configPath = "file:///" + cwd + "/../config";
//		props.setProperty(ResourceManager.CATALOG_CLUSTER_URL, 
//				configPath + "/clusters.yml");
//		props.setProperty(ResourceManager.POOL_ASSIGNMENT_URL, 
//				configPath + "/pools.yml");
		
		ResourceManager mgr = ResourceManagerImpl.instance();
		
		return mgr;
	}
	
	
	@Test
	public void testClusterCapacity() throws Exception {
		Cluster cluster = new Cluster("tomahawk");
		cluster.setHost("10.46.31.50");
		cluster.setPort(9440);
		cluster.setUser("admin");
		cluster.setPassword("Nutanix.1");
		
		new ClusterBuilder(cluster).build();
		
		
		System.err.println(cluster 
				+ " size " + cluster.getResourceCount()
				+ " capacity " + cluster.getTotalCapacity());
		assertEquals(311,cluster.getResourceCount());
		assertEquals(new Memory(2288, MemoryUnit.GB),
				cluster.getTotalCapacity().getQuantity(ResourceKind.MEMORY));
		assertEquals(new Storage(1.11, MemoryUnit.TB),
				cluster.getTotalCapacity().getQuantity(ResourceKind.STORAGE));
		
	}
	
//	@Test
	public void testPoolsAreConfigured() {
		ResourceManager mgr = initResourceManager();
		assertEquals(2, mgr.getResourcePools().size());
		String[] expectedPoolNames = new String[] {"default", "jenkins"};
		for (String poolName : expectedPoolNames) {
			ResourcePool pool = mgr.getResourcePool(poolName);
			assertNotNull(poolName + "  not found", pool);
			assertFalse(poolName + " has no providers", pool.getProviders().isEmpty());
			assertTrue(pool.getSize() > 0);
		}
	}
	
	@Test
	public void testAllocationIncresesUtilization() {
		ResourceManager mgr = initResourceManager();
		ResourcePool pool = mgr.getResourcePool("default");
		Capacity demand = new DefaultCapacity();
		demand.addQuantity(new Memory(100, MemoryUnit.MB));
		demand.addQuantity(new CPU(2));
		demand.addQuantity(new Storage(100, MemoryUnit.GB));
		
		Utilization before = pool.getUtilization();
		Allocation alloc = mgr.allocate(pool, demand);
		assertNotNull("can not allocate " + demand, alloc);
		Utilization after = pool.getUtilization();
		
		System.err.println("utilzation before:" + before);
		System.err.println("utilzation after:" + after);
		
	}
	
	
	
//	@Test
	public void testAddResourceToVM() {
		VirtualMachine vm = new VirtualMachine("test");
		CPU cpu = new CPU(10);
		Memory memory = new Memory(1000, MemoryUnit.GB);
		Storage d1 = new Storage(1024, MemoryUnit.B);
		Storage d2 = new Storage(24, MemoryUnit.B);
		vm.addQuanity(cpu);
		vm.addQuanity(memory);
		vm.addQuanity(d1);
		vm.addQuanity(d2);
		
		//System.err.println(vm);
		
		assertEquals(cpu,         vm.getAvailableCapacity().getQuantity(ResourceKind.COMPUTE));
		assertEquals(memory,      vm.getAvailableCapacity().getQuantity(ResourceKind.MEMORY));
		assertEquals(d1.plus(d2), vm.getAvailableCapacity().getQuantity(ResourceKind.STORAGE));
	}
	
//	@Test
	public void testAddResourceToCluster() {
		VirtualMachine vm = new VirtualMachine("A");
		CPU cpu = new CPU(10);
		Memory memory = new Memory(1000, MemoryUnit.GB);
		Storage d1 = new Storage(1024, MemoryUnit.B);
		Storage d2 = new Storage(24, MemoryUnit.B);
		vm.addQuanity(cpu);
		vm.addQuanity(memory);
		vm.addQuanity(d1);
		vm.addQuanity(d2);
		VirtualMachine vm2 = new VirtualMachine("B");
		vm2.addQuanity(cpu);
		vm2.addQuanity(memory);
		vm2.addQuanity(d1);
		vm2.addQuanity(d2);
		Cluster cluster = new Cluster("test");
		cluster.addResource(vm);
		cluster.addResource(vm2);
	}


}
