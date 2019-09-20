package junit.allocation;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.ClusterBuilder;

public class TestCluster {

	@Test
	public void testBuildClusterByPrismRequest() throws Exception {
		ClusterBuilder builder = new ClusterBuilder();
		Cluster cluster = new Cluster("tomahawk");
		cluster.setHost("tomahawk-v1.eng.nutanix.com");
		cluster.setPort(9440);
		builder.build(cluster);
		
		System.err.println(cluster.getTotalCapacity());
		assertEquals(309, cluster.getResourceCount());
		
		
		
	}

}
