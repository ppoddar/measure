package junit.allocation;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nutanix.resource.Capacities;
import com.nutanix.resource.Capacity;
import com.nutanix.resource.Resource;
import com.nutanix.resource.Unit;
import com.nutanix.resource.impl.DefaultCapacities;
import com.nutanix.resource.impl.unit.Memory;
import com.nutanix.resource.impl.unit.MemoryUnit;

public class TestCapacity {
	private static double epsilon = 1.0E-8;
	@Test
	public void testCapacityCanBeCompared() {
		Capacity q  = new Memory(100, MemoryUnit.GB);
		Capacity q2 = new Memory(50, MemoryUnit.GB);
		Capacity q3 = new Memory(100, MemoryUnit.MB);
		
		assertEquals(100, q.getAmount(), epsilon);
		assertSame(MemoryUnit.GB, q.getUnit());
		
		assertTrue(q.compareTo(q2) > 0);
		assertTrue(q.compareTo(q3) > 0);
		assertTrue(q3.compareTo(q2) < 0);
	}
	
	@Test
	public void testNewCapacityResultFromOperation() {
		Capacity q = new Memory(100, MemoryUnit.GB);
		Capacity q2 = new Memory(50, MemoryUnit.GB);

		Capacity q3 = q.minus(q2);
		Capacity q4 = q.plus(q2);
		Capacity q5 = q.times(5);
		
		assertNotSame(q, q3);
		assertNotSame(q, q4);
		assertNotSame(q, q5);
		
		assertEquals(100-50, q3.getAmount(), epsilon);
		assertEquals(100+50, q4.getAmount(), epsilon);
		assertEquals(5*100,  q5.getAmount(), epsilon);
	}
	
	@Test
	public void testUnitConversion() {
		assertEquals(1024,      MemoryUnit.MB.getBaseConversionFactor(), epsilon);
		assertEquals(1024*1024, MemoryUnit.GB.getBaseConversionFactor(), epsilon);
		assertTrue(MemoryUnit.KB.isBase());
		
		// 1024MB == 1GB
		assertEquals(1024,  
			MemoryUnit.MB.getConversionFactor(MemoryUnit.GB), epsilon);
	}
	
	@Test
	public void testCapacities() {
		Resource.Kind memory = Resource.Kind.MEMORY;
		Capacities cap = new DefaultCapacities();
		Capacity q  = new Memory(100, MemoryUnit.GB);
		Capacity q2 = new Memory(10, MemoryUnit.GB);
		cap.addCapacity(q);
		
		assertTrue(cap.hasKind(memory));
		assertEquals(q, cap.getCapacity(memory));
		
		cap.addCapacity(q2);
		assertEquals(q.plus(q2), cap.getCapacity(memory));
		assertNotSame(q.plus(q2), cap.getCapacity(memory));
	}
	
	@Test
	public void testReduceCapacities() {
		Resource.Kind memory = Resource.Kind.MEMORY;
		Capacities cap1 = new DefaultCapacities();
		Capacity q1 = new Memory(100, MemoryUnit.GB);
		
		
		Capacities cap2 = new DefaultCapacities();
		Capacity q2 = new Memory(10, MemoryUnit.GB);


		cap1.addCapacity(q1);
		cap2.addCapacity(q2);
		
		Capacities cap3 = new DefaultCapacities();
		cap3.addCapacity(q1.minus(q2));
		
		cap1.reduceCapacities(cap2);
		
		assertEquals(q1.minus(q2), cap3.getCapacity(memory));
	}
	
	@Test
	public void testUnits() {
		Resource.Kind memory = Resource.Kind.MEMORY;
		
		assertEquals(MemoryUnit.B, Resource.Kind.getUnit(null, "B"));
		
	}



}
