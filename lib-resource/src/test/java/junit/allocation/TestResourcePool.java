package junit.allocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import com.nutanix.resource.unit.CPU;
import com.nutanix.resource.unit.Memory;
import com.nutanix.resource.unit.MemoryUnit;
import com.nutanix.resource.unit.Storage;

public class TestResourcePool {
	private static ResourcePool testPool;
	private static ResourceManager mgr;
	
	@BeforeClass
	public static void setResourcePool() {
		Properties props = new Properties();
		String cwd = System.getProperty("user.dir");
		props.setProperty(ResourceManager.CATALOG_CLUSTER_URL, 
				"file:///" + cwd + "/../config/clusters.yml");
		props.setProperty(ResourceManager.POOL_ASSIGNMENT_URL, 
				"file:///" + cwd + "/../config/pools.yml");
		ResourceManagerImpl.setProperties(props);
		mgr = ResourceManagerImpl.instance();
		
		testPool = new DefaultResourcePool();
		
		ResourceProvider provider = new DefaultResourceProvider("test");
		provider.addResource(new VirtualMachine("vm1")
				.addQuanity(new Memory(100, MemoryUnit.GB))
				.addQuanity(new CPU(4)));
		provider.addResource(new VirtualMachine("vm2")
				.addQuanity(new Memory(20, MemoryUnit.GB))
				.addQuanity(new CPU(2)));
		provider.addResource(new VirtualMachine("vm3")
				.addQuanity(new Memory(50, MemoryUnit.GB))
				.addQuanity(new CPU(1)));
		
		testPool.addProvider(provider);
	}
	
	@Test
	public void testPoolHasMultipleVM() {
		Collection<ResourcePool> pools = mgr.getResourcePools();
		assertFalse(pools.isEmpty());
		ResourcePool pool = pools.iterator().next();
		for (Resource vm : pool) {
			assertTrue(VirtualMachine.class.isInstance(vm));
			System.out.println(vm);
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
		
		assertEquals(cpu, vm.getAvailableCapacity().getQuantity(Resource.Kind.COMPUTE));
		assertEquals(memory, vm.getAvailableCapacity().getQuantity(Resource.Kind.MEMORY));
		assertEquals(d1.plus(d2), vm.getAvailableCapacity().getQuantity(Resource.Kind.STORAGE));
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
		
//		assertEquals(cpu.times(2),           cluster.getCapacity(Resource.Kind.CPU));
//		assertEquals(memory.times(2),        cluster.getCapacity(Resource.Kind.MEMORY));
//		assertEquals((d1.plus(d2).times(2)), cluster.getCapacity(Resource.Kind.STORAGE));
	}


}
