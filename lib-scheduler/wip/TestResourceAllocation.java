package junit.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.nutanix.capacity.CPU;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Memory;
import com.nutanix.capacity.QuantityFactory;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.Statistics;
import com.nutanix.capacity.Storage;
import com.nutanix.capacity.Utilization;
import com.nutanix.capacity.impl.DefaultCapacity;
import com.nutanix.capacity.impl.DefaultUtilization;
import com.nutanix.resource.Allocation;
import com.nutanix.resource.Resource;
import com.nutanix.resource.ResourcePool;
import com.nutanix.resource.ResourceProvider;
import com.nutanix.resource.impl.DefaultResourcePool;
import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.VirtualMachine;

public class TestResourceAllocation {
	double delta = 1.0E-8;
	@Test
	public void testUtilization() {
		Utilization u = new DefaultUtilization();
		Statistics s = u.get(ResourceKind.MEMORY);
		assertEquals(0, s.getSize());
		assertEquals(0, s.getMean(), delta);
		assertEquals(0, s.getStandardDevitaion(), delta);
		assertEquals(0, s.getCoefficientOfVariance(), delta);
		assertEquals(0, s.getVariance(), delta);
		assertEquals(0.0, u.getAverage(), delta);
		
		u.put(ResourceKind.MEMORY, 0.7);
		
		s = u.get(ResourceKind.MEMORY);
		assertEquals(1, s.getSize());
		assertEquals(0.7, s.getMean(), delta);
		assertEquals(0.0, s.getStandardDevitaion(), delta);
		assertEquals(1.0, s.getCoefficientOfVariance(), delta);
		assertEquals(0.0, s.getVariance(), delta);
		assertEquals(0.7, u.getAverage(), delta);
	}
	
	/**
	 * test simulates all aspects of resource utilization.
	 */
	@Test
	public void testResourceAllocation() {
		// create a pool
		ResourcePool pool = new DefaultResourcePool();
		pool.setName("test");
		// create a cluster i.e. resource provider
		ResourceProvider cluster = new Cluster("test-cluster");
		// create resource i.e. VirtualMachone 
		// and add capacity
		
		int N = 3;
		double fraction = 0.2;
		Memory  memory  = (Memory) QuantityFactory.createQuantity("MEMORY", "100 MB");
		Storage storage = (Storage) QuantityFactory.createQuantity("STORAGE", "100 GB");
		CPU     cpu     = (CPU) QuantityFactory.createQuantity("COMPUTE", "1")
				.times((1.0/fraction));
		
		Resource vm = new VirtualMachine("vm1");
		vm.addQuanity(memory);
		vm.addQuanity(storage);
		vm.addQuanity(cpu);
		cluster.addResource(vm);
		for (int i = 1; i < N; i++) {
			cluster.addResource(vm.copy());
		}
		// add provider to pool
		pool.addProvider(cluster);
		
		Utilization u0 = pool.getUtilization();
		System.err.println("original " + u0);
		assertEquals(0.0, u0.getAverage(), delta);

		Capacity demand = new DefaultCapacity();
		demand.addQuantity(memory.times(fraction));
		demand.addQuantity(storage.times(fraction));
		demand.addQuantity(new CPU(1));
		
		for (int i = 0; i < N*(1/fraction); i++) {
			Allocation allocation = pool.allocate(demand);
			assertNotNull(allocation);
			Utilization u = pool.getUtilization();
			System.err.println("after allocation " + u);
			
			assertTrue(u.getAverage() > u0.getAverage());
			u0 = u;
		}
	}
	
	
	

}
