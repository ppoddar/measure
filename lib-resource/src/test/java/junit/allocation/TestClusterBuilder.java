package junit.allocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assume.assumeTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nutanix.bpg.model.Catalog;
import com.nutanix.capacity.MemoryUnit;
import com.nutanix.capacity.Quantity;
import com.nutanix.capacity.ResourceKind;
import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.ClusterBuilder;
import com.nutanix.resource.model.SingleClusterBuilder;
import com.nutanix.resource.prism.PrismGateway;

public class TestClusterBuilder {
	private static double epsilon = 1.0E-2;
	
	
	
	public void testActualCluster() throws Exception {
		Cluster tomahawk = new Cluster();
		tomahawk.setName("tomahawk");
		tomahawk.setHost("10.46.31.50");
		assumeTrue(new PrismGateway(tomahawk).isReachable());
		
		
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
	public void test() throws Exception{
		Cluster cluster = new Cluster();
		cluster.setName("tomahawk");
		cluster.setHost("10.46.31.50");
		
		assumeTrue(new PrismGateway(cluster).isReachable());
		
		new SingleClusterBuilder(cluster)
			.call();
		
		
		System.err.println("storage capacity:" + 
				cluster.getTotal(ResourceKind.STORAGE).convert(MemoryUnit.TB));
		System.err.println("storage used:" + 
				cluster.getAvailable(ResourceKind.STORAGE).convert(MemoryUnit.TB));
		
		
	}

}
