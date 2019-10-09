package junit.capacity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.nutanix.capacity.Capacity;
import com.nutanix.capacity.Memory;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.Quantity;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.capacity.impl.DefaultCapacity;

public class TestCapacity {
	private static double epsilon = 1.0E-8;
	
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
		assertEquals(2, c.getValue(), epsilon);
	}
	
	@Test
	public void testCapacitiesAreComputedInPreferredUnit() {
		ResourceKind memory = ResourceKind.MEMORY;
		Capacity cap = new DefaultCapacity();
		cap.setPreferredUnit(ResourceKind.MEMORY, MemoryUnit.GB);
		Quantity q  = new Memory(100, MemoryUnit.GB);
		Quantity q2 = new Memory(10, MemoryUnit.GB);
		cap.addQuantity(q);
		
		assertEquals(q, cap.getQuantity(memory));
		
		cap.addQuantity(q2);
		Quantity expected = q.plus(q2);
		assertEquals(expected, cap.getQuantity(memory));
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
	
	



}
