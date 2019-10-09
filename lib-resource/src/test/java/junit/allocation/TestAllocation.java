package junit.allocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nutanix.resource.Allocation;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.ResourceProvider;
import com.nutanix.resource.impl.DefaultResourcePool;
import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.VirtualMachine;
import com.nutanix.capacity.CPU;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Memory;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.impl.DefaultCapacity;

public class TestAllocation {
	private static ResourcePool pool;
	private static ResourceProvider cluster;
	@BeforeClass
	public static void setUp() throws Exception {
		pool = new DefaultResourcePool();
		
		cluster = new Cluster("test");
		VirtualMachine vm1 = new VirtualMachine("large");
		vm1.addQuanity(new Memory(100, MemoryUnit.GB))
		   .addQuanity(new CPU(10));
		
		VirtualMachine vm2 = new VirtualMachine("small");
		vm2.addQuanity(new Memory(10, MemoryUnit.GB))
		   .addQuanity(new CPU(1));

		
		cluster.addResource(vm1);
		cluster.addResource(vm2);
		
		pool.addProvider(cluster);
		
	}
	
	@Test
	public void testPoolIteratesOverMultipleClusters() {
		ResourcePool pool = new DefaultResourcePool();
		ResourceProvider c1 = new Cluster("test");
		c1.addResource(new VirtualMachine("vm1"));
		c1.addResource(new VirtualMachine("vm2"));
		
		ResourceProvider c2 = new Cluster("test2");
		c1.addResource(new VirtualMachine("vm3"));
		pool.addProvider(c1);
		pool.addProvider(c2);
		
		assertEquals(3, pool.getSize());
		
		
	}
	
	@Test
	public void testSimpleAllocation() {
		Capacity demand = new DefaultCapacity();
		demand.addQuantity(new Memory(100, MemoryUnit.GB));
		demand.addQuantity(new CPU(2));
		
		Capacity availableBefore = pool.getAvailableCapacity();
		Capacity totalBefore = pool.getTotalCapacity();
		System.err.println("total capacities (before)    :" + totalBefore);
		System.err.println("available capacities (before):" + availableBefore);
		
		Allocation alloc = pool.allocate(demand);
		assertNotNull(alloc);
		assertEquals("large", alloc.getSupply().getId());
		
		Capacity availableAfter = pool.getAvailableCapacity();
		Capacity totalAfter = pool.getTotalCapacity();
		System.err.println("total capacities (after)    :" + totalAfter);
		System.err.println("available capacities (after):" + availableAfter);
		assertEquals(totalAfter, totalBefore);
		Capacity actual = new DefaultCapacity(availableBefore);
		actual.reduceCapacity(availableAfter);
		assertEquals(demand, actual);
		
	}

}
