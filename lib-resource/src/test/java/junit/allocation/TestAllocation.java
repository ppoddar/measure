package junit.allocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nutanix.resource.Allocation;
import com.nutanix.resource.Capacities;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.ResourceProvider;
import com.nutanix.resource.impl.DefaultCapacities;
import com.nutanix.resource.impl.DefaultResourcePool;
import com.nutanix.resource.impl.unit.CPU;
import com.nutanix.resource.impl.unit.Memory;
import com.nutanix.resource.impl.unit.MemoryUnit;
import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.VirtualMachine;

public class TestAllocation {
	private static ResourcePool pool;
	private static ResourceProvider cluster;
	@BeforeClass
	public static void setUp() throws Exception {
		pool = new DefaultResourcePool();
		
		cluster = new Cluster();
		VirtualMachine vm1 = new VirtualMachine();
		vm1.setId("large");
		vm1.setName("large");
		vm1.addCapacity(new Memory(100, MemoryUnit.GB))
		   .addCapacity(new CPU(10));
		
		VirtualMachine vm2 = new VirtualMachine();
		vm2.setId("small");
		vm2.setName("small");
		vm2.addCapacity(new Memory(10, MemoryUnit.GB))
		   .addCapacity(new CPU(1));

		
		cluster.addResource(vm1);
		cluster.addResource(vm2);
		
		pool.addProvider(cluster);
		
	}
	
	@Test
	public void testPoolIteratesOverMultipleClusters() {
		ResourcePool pool = new DefaultResourcePool();
		ResourceProvider c1 = new Cluster();
		c1.addResource(new VirtualMachine("vm1"));
		c1.addResource(new VirtualMachine("vm2"));
		
		ResourceProvider c2 = new Cluster();
		c1.addResource(new VirtualMachine("vm3"));
		pool.addProvider(c1);
		pool.addProvider(c2);
		
		assertEquals(3, pool.getSize());
		
		
	}
	
	@Test
	public void testSimpleAllocation() {
		Capacities demand = new DefaultCapacities();
		demand.addCapacity(new Memory(100, MemoryUnit.GB));
		demand.addCapacity(new CPU(2));
		
		Capacities availableBefore = pool.getAvailableCapacity();
		Capacities totalBefore = pool.getTotalCapacity();
		System.err.println("total capacities (before)    :" + totalBefore);
		System.err.println("available capacities (before):" + availableBefore);
		
		Allocation alloc = pool.allocate(demand);
		assertNotNull(alloc);
		assertEquals("large", alloc.getSupply().getId());
		
		Capacities availableAfter = pool.getAvailableCapacity();
		Capacities totalAfter = pool.getTotalCapacity();
		System.err.println("total capacities (after)    :" + totalAfter);
		System.err.println("available capacities (after):" + availableAfter);
		assertEquals(totalAfter, totalBefore);
		Capacities actual = new DefaultCapacities(availableBefore);
		actual.reduceCapacities(availableAfter);
		assertEquals(demand, actual);
		
	}

}
