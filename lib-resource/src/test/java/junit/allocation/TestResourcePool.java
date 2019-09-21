package junit.allocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nutanix.resource.Resource;
import com.nutanix.resource.ResourceManager;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.ResourceProvider;
import com.nutanix.resource.impl.DefaultResourcePool;
import com.nutanix.resource.impl.DefaultResourceProvider;
import com.nutanix.resource.impl.ResourceManagerImpl;
import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.VirtualMachine;
import com.nutanix.capacity.CPU;
import com.nutanix.capacity.Memory;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.Storage;
/**
 * test allocation on configured resource pools
 * 
 * @author pinaki.poddar
 *
 */
public class TestResourcePool {
	private static ResourcePool testPool;
	private static ResourceManager mgr;
	
	@BeforeClass
	public static void setResourcePool() {
		// Resource Manager is configured with 
		// two files 
		//  clusters.yml -- cluster coordinates
		//  pools.yml    -- assignment of clusters to pools
		Properties props = new Properties();
		String cwd = System.getProperty("user.dir");
		props.setProperty(ResourceManager.CATALOG_CLUSTER_URL, 
				"file:///" + cwd + "/../config/clusters.yml");
		props.setProperty(ResourceManager.POOL_ASSIGNMENT_URL, 
				"file:///" + cwd + "/../config/pools.yml");
		
		ResourceManagerImpl.setProperties(props);
		mgr = ResourceManagerImpl.instance();
	}
	
	@Test
	public void testPoolsAreConfigured() {
		assertEquals(2, mgr.getResourcePools().size());
		String[] expectedPoolNames = new String[] {"default", "jenkins"};
		for (String poolName : expectedPoolNames) {
			ResourcePool pool = mgr.getResourcePoolByName(poolName);
			assertNotNull(poolName + "  not found", pool);
			assertFalse(poolName + " has no providers", pool.getProviders().isEmpty());
			assertTrue(pool.getSize() > 0);
		}
	}
	
	@Test
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
		
		System.err.println(vm);
		
		assertEquals(cpu, vm.getAvailableCapacity().getQuantity(ResourceKind.COMPUTE));
		assertEquals(memory, vm.getAvailableCapacity().getQuantity(ResourceKind.MEMORY));
		assertEquals(d1.plus(d2), vm.getAvailableCapacity().getQuantity(ResourceKind.STORAGE));
	}
	
	@Test
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
