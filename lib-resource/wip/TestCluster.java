package junit.allocation;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nutanix.bpg.utils.URLBuilder;
import com.nutanix.resource.model.Cluster;
import com.nutanix.resource.model.ClusterBuilder;

public class TestCluster {

	@Test
	public void testBuildClusterByPrismRequest() throws Exception {
		Cluster cluster = new Cluster("tomahawk");
		cluster.setHost("10.46.31.50");
		ClusterBuilder builder = new ClusterBuilder(cluster);
		builder.build();
		
		System.err.println(cluster.getTotalCapacity());
		assertEquals(309, cluster.getResourceCount());
	}
	
	@Test
	public void testURL() throws Exception {
		String expected = "https://tomahawk-v1.eng.nutanix.com:9440/PrismGateway/services/rest/v2.0/vms/?include_vm_disk_config=true";
	
		URLBuilder builder = new URLBuilder()
				.withScheme("https")
				.withHost("tomahawk-v1.eng.nutanix.com")
				.withPort(9440)
				.withPath("PrismGateway/services/rest/v2.0/vms/")
				.withQueryParams("include_vm_disk_config", "true");
		
		assertEquals(expected, builder.build().toString());
		
	}

}
