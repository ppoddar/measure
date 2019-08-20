package test.basic;

import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nutanix.bpg.measure.MeasurementServer;
import com.nutanix.bpg.measure.MeasurementServerImpl;
import com.nutanix.bpg.measure.model.Database;
import com.nutanix.bpg.measure.model.Measurement;
import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.Snapshot;
import com.nutanix.bpg.measure.model.SnapshotSchedule;

public class TestDemoWorkflow {
	@SuppressWarnings("unused")
	private static MeasurementServer api;
	private String TEST_DATABASE = "test_db";
	private String TEST_METRICS = "pg_stat_activity";
	@BeforeClass
	public static void init() throws Exception {
		Properties props = new Properties();
		MeasurementServerImpl.init(props);
		api = MeasurementServerImpl.getInstance();
	}
	
	@Test
	public void testTakeSnapshot() throws Exception {
		Database database = api.getDatabase(TEST_DATABASE);
		Metrics metrics = api.getMetrics(TEST_METRICS);
		SnapshotSchedule schedule = new SnapshotSchedule(4, 10, TimeUnit.SECONDS);
		String name = "test-snapshot";
		Snapshot snapshot = api.takeSnapshot(name, metrics, database, schedule);
		System.err.println("taken " + snapshot + " id=" + snapshot.getId());
		Thread.sleep(5*1000);
		List<Measurement> measurements = api.getMeasurements(snapshot, metrics);
			
		assertFalse(measurements.isEmpty());
	}

}
