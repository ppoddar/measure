package junit.allocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.nutanix.bpg.model.Catalog;
import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Memory;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.Quantity;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.impl.DefaultCapacity;
import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.ClusterBuilder;

public class TestCapacity {
	private static double epsilon = 1.0E-1;
	@Test
	public void testCapacityCanBeCompared() {
		Quantity q  = new Memory(100, MemoryUnit.GB);
		Quantity q2 = new Memory(50, MemoryUnit.GB);
		Quantity q3 = new Memory(100, MemoryUnit.MB);
		
		assertEquals(100, q.getValue(), epsilon);
		assertSame(MemoryUnit.GB, q.getUnit());
		
		assertTrue(q.compareTo(q2) > 0);
		assertTrue(q.compareTo(q3) > 0);
		assertTrue(q3.compareTo(q2) < 0);
	}
	
	@Test
	public void testNewCapacityResultFromOperation() {
		Quantity q = new Memory(100, MemoryUnit.GB);
		Quantity q2 = new Memory(50, MemoryUnit.GB);

		Quantity q3 = q.minus(q2);
		Quantity q4 = q.plus(q2);
		Quantity q5 = q.times(5);
		
		assertNotSame(q, q3);
		assertNotSame(q, q4);
		assertNotSame(q, q5);
		
		assertEquals(100-50, q3.getValue(), epsilon);
		assertEquals(100+50, q4.getValue(), epsilon);
		assertEquals(5*100,  q5.getValue(), epsilon);
	}
	
	@Test 
	public void testAdditionResultCapacityUnitOfReceiver() {
		Quantity q1 = new Memory(1, MemoryUnit.KB);
		Quantity q2 = new Memory(1, MemoryUnit.MB);
		
		Quantity q3 = q1.plus(q2);
		assertEquals(q1.getUnit(), q3.getUnit());
		
		Quantity q4 = q2.plus(q1);
		assertEquals(q2.getUnit(), q4.getUnit());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCanNotSubtractNegativeResult() {
		Quantity small = new Memory(1, MemoryUnit.KB);
		Quantity large = new Memory(1, MemoryUnit.MB);
		
		small.minus(large);
	}

	
	@Test
	public void testUnitConversion() {
		assertEquals(1024,      MemoryUnit.MB.getBaseConversionFactor(), epsilon);
		assertEquals(1024*1024, MemoryUnit.GB.getBaseConversionFactor(), epsilon);
		assertTrue(MemoryUnit.KB.isBase());
		
		// 1024MB == 1GB
		assertEquals(1024,  
			MemoryUnit.MB.getConversionFactor(MemoryUnit.GB), epsilon);
		
		Memory q = new Memory(1024*2, MemoryUnit.MB);
		Quantity c = q.convert(MemoryUnit.TB);
		
		assert(Memory.class.isInstance(q));
		assertEquals(MemoryUnit.TB, c.getUnit());
	}
	
	@Test
	public void testCapacities() {
		ResourceKind memory = ResourceKind.MEMORY;
		Capacity cap = new DefaultCapacity();
		Quantity q  = new Memory(100, MemoryUnit.GB);
		Quantity q2 = new Memory(10, MemoryUnit.GB);
		cap.addQuantity(q);
		
		assertEquals(q, cap.getQuantity(memory));
		
		cap.addQuantity(q2);
		assertEquals(q.plus(q2), cap.getQuantity(memory));
		assertNotSame(q.plus(q2), cap.getQuantity(memory));
	}
	
	@Test
	public void testReduceCapacities() {
		ResourceKind memory = ResourceKind.MEMORY;
		Capacity cap1 = new DefaultCapacity();
		Quantity q1 = new Memory(100, MemoryUnit.GB);
		
		
		Capacity cap2 = new DefaultCapacity();
		Quantity q2 = new Memory(10, MemoryUnit.GB);


		cap1.addQuantity(q1);
		cap2.addQuantity(q2);
		
		Capacity cap3 = new DefaultCapacity();
		cap3.addQuantity(q1.minus(q2));
		
		cap1.reduceCapacity(cap2);
		
		assertEquals(q1.minus(q2), cap3.getQuantity(memory));
	}
	
	@Test
	public void testUnits() {
		assertEquals(MemoryUnit.B, ResourceKind.MEMORY.getUnit("B"));
		
	}
	
	@Test
	public void testActualCluster() throws Exception {
		Cluster tomahawk = new Cluster();
		tomahawk.setName("tomahawk");
		tomahawk.setHost("10.46.31.50");
		
		Catalog<Cluster> clusters = new Catalog<>();
		clusters.add(tomahawk);
		new ClusterBuilder().build(clusters);
		System.err.println("capacity " + tomahawk.getTotalCapacity());
		
		assertSame(tomahawk, clusters.iterator().next());
		
		
		Quantity memory = tomahawk.getAvailable(ResourceKind.MEMORY);
		assertNotNull(memory);
		assertSame(MemoryUnit.MB, memory.getUnit());
		assertEquals(1547490, memory.getValue(), epsilon);
		
	}
	@Test
	public void testCapacityAddition() {
		Memory m1 = new Memory(2147483647, MemoryUnit.B);
		Memory m2 = new Memory(0, MemoryUnit.GB);
		
		Memory m3 = (Memory)m2.plus(m1);
		assertSame(MemoryUnit.GB, m3.getUnit());
		assertEquals(2, m3.getValue(), epsilon);
		
		m1 = new Memory(1031660, MemoryUnit.MB);
		m2 = new Memory(2147483647, MemoryUnit.B);
		m3 = (Memory)m1.plus(m2);
		assertEquals(1033708, m3.getValue(), epsilon);

	}
	
	



}
