package junit.allocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nutanix.resource.Resource;
import com.nutanix.resource.ResourceManager;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.ResourceProvider;
import com.nutanix.resource.impl.DefaultResourceProvider;
import com.nutanix.resource.impl.DefaultResourcePool;
import com.nutanix.resource.impl.ResourceManagerImpl;
import com.nutanix.resource.impl.unit.CPU;
import com.nutanix.resource.impl.unit.Memory;
import com.nutanix.resource.impl.unit.MemoryUnit;
import com.nutanix.resource.impl.unit.Storage;
import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.VirtualMachine;

public class TestResourcePool {
	private static ResourcePool testPool;
	private static ResourceManager mgr;
	
	@BeforeClass
	public static void setResourcePool() {
		Properties props = new Properties();
		String cwd = System.getProperty("user.dir");
		props.setProperty(ResourceManager.CATALOG_CLUSTER_URL, 
				"file:///" + cwd + "/src/test/resources/clusters.yml");
		ResourceManagerImpl.setProperties(props);
		mgr = ResourceManagerImpl.instance();
		
		testPool = new DefaultResourcePool();
		
		ResourceProvider provider = new DefaultResourceProvider();
		provider.addResource(new VirtualMachine()
				.addCapacity(new Memory(100, MemoryUnit.GB))
				.addCapacity(new CPU(4)));
		provider.addResource(new VirtualMachine()
				.addCapacity(new Memory(20, MemoryUnit.GB))
				.addCapacity(new CPU(2)));
		provider.addResource(new VirtualMachine()
				.addCapacity(new Memory(50, MemoryUnit.GB))
				.addCapacity(new CPU(1)));
		
		testPool.addProvider(provider);
	}
	
	@Test
	public void testPoolHasMultipleVM() {
		List<ResourcePool> pools = mgr.getResourcePools();
		assertFalse(pools.isEmpty());
		ResourcePool pool = pools.get(0);
		for (Resource vm : pool) {
			assertTrue(VirtualMachine.class.isInstance(vm));
			System.out.println(vm);
		}
	}
	

	
	
	@Test
	public void testAddResourceToVM() {
		VirtualMachine vm = new VirtualMachine();
		CPU cpu = new CPU(10);
		Memory memory = new Memory(1000, MemoryUnit.GB);
		Storage d1 = new Storage(1024, MemoryUnit.B);
		Storage d2 = new Storage(24, MemoryUnit.B);
		vm.addCapacity(cpu);
		vm.addCapacity(memory);
		vm.addCapacity(d1);
		vm.addCapacity(d2);
		
		System.err.println(vm);
		
		assertEquals(cpu, vm.getCapacity(Resource.Kind.COMPUTE));
		assertEquals(memory, vm.getCapacity(Resource.Kind.MEMORY));
		assertEquals(d1.plus(d2), vm.getCapacity(Resource.Kind.STORAGE));
	}
	
	@Test
	public void testAddResourceToCluster() {
		VirtualMachine vm = new VirtualMachine();
		vm.setId("A");
		CPU cpu = new CPU(10);
		Memory memory = new Memory(1000, MemoryUnit.GB);
		Storage d1 = new Storage(1024, MemoryUnit.B);
		Storage d2 = new Storage(24, MemoryUnit.B);
		vm.addCapacity(cpu);
		vm.addCapacity(memory);
		vm.addCapacity(d1);
		vm.addCapacity(d2);
		VirtualMachine vm2 = new VirtualMachine();
		vm2.setId("B");
		vm2.addCapacity(cpu);
		vm2.addCapacity(memory);
		vm2.addCapacity(d1);
		vm2.addCapacity(d2);
		Cluster cluster = new Cluster();
		cluster.addResource(vm);
		cluster.addResource(vm2);
		
//		assertEquals(cpu.times(2),           cluster.getCapacity(Resource.Kind.CPU));
//		assertEquals(memory.times(2),        cluster.getCapacity(Resource.Kind.MEMORY));
//		assertEquals((d1.plus(d2).times(2)), cluster.getCapacity(Resource.Kind.STORAGE));
	}


}
