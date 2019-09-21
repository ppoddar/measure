package com.nutanix.bpg.measure;

import java.util.concurrent.CompletableFuture;

import com.nutanix.bpg.model.Database;
import com.nutanix.bpg.model.Metrics;
import com.nutanix.bpg.measure.model.Measurement;
import com.nutanix.bpg.measure.model.Snapshot;
import com.nutanix.bpg.measure.model.SnapshotSchedule;
import com.nutanix.bpg.workload.PGBenchOptions;

/**
 * A measurement server takes {@link Measurement measurement}
 * of a {@link Database Database} environment.
 * <p>
 * A measurement is taken via {@link Plugin plug-in}.
 * A measurement describes <em>what</em> is measured
 * via {@link Metrics}, while a plug-in provides 
 * <em>how</em>.
 * <br>
 * As there are various metrics to characterize a
 * database environment (e.g. database statistics,
 * OS, workload etc.), so there are means to collect
 * them. 
 * {@link Plugin plug-in} provides
 * a parameterized mechanics to take such measurements,
 * <p>
 * For example, a {@link ScriptPlugin plug-in} executes a 
 * Python or shell script to measure OS parameters.
 * A {@link JDBCPlugin SQL} plug-in run SQL 
 * command to read collected statistics from a database.
 * 
 * @author pinaki.poddar
 *
 */
public interface MeasurementServer  {
	Measurement takeMeasurement(String name, 
			Database database, 
			Metrics metrics) throws Exception ;
	/**
	 * Takes snapshot.
	 * A snapshot is a finite sequence of measurements
	 * taken at regular interval.
	 * 
	 * @param name name of the snapshot. 
	 * @param metrics the name of a metrics to be measured
	 * @param database the name of target database whose
	 * metrics are being measured
	 * @param schedule describes how many measurement
	 * are to be taken and at what interval
	 * 
	 * @return a future that would return snapshot being
	 * taken. 
	 * @throws Exception
	 */
	CompletableFuture<Snapshot> takeSnapshot(String name, 
			Database database, 
			Metrics metrics, 
			SnapshotSchedule schedule) throws Exception ;
	
	/**
	 * Runs a benchmark.
	 * A benchmark is a finite sequence of measurements
	 * taken at different workload.
	 *  
	 * 
	 * @param name name of the benchmark.
	 * @param database the name of target database whose
	 * metrics are being measured
	 * @param options benchmark options
	 * 
	 * @return a future that would return snapshot being
	 * taken. 
	 * @throws Exception
	 */
	CompletableFuture<Snapshot> takeBenchmark(
			String name, 
			Database database,
			PGBenchOptions[] options) throws Exception ;
}
