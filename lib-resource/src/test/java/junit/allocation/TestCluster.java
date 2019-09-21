package junit.allocation;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.ClusterBuilder;

public class TestCluster {

	@Test
	public void testBuildClusterByPrismRequest() throws Exception {
		Cluster cluster = new Cluster("tomahawk");
		ClusterBuilder builder = new ClusterBuilder(cluster);
		cluster.setHost("tomahawk-v1.eng.nutanix.com");
		cluster.setPort(9440);
		builder.call();
		
		System.err.println(cluster.getTotalCapacity());
		assertEquals(309, cluster.getResourceCount());
		
		
		
	}

}
