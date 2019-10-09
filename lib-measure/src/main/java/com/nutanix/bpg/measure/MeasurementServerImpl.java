package com.nutanix.bpg.measure;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.model.JDBCMeasurmentTaker;
import com.nutanix.bpg.measure.model.Measurement;
import com.nutanix.bpg.measure.model.Snapshot;
import com.nutanix.bpg.measure.model.SnapshotPlugin;
import com.nutanix.bpg.measure.model.SnapshotSchedule;
import com.nutanix.bpg.model.Database;
import com.nutanix.bpg.model.Metrics;
import com.nutanix.bpg.repo.Repository;
import com.nutanix.bpg.repo.RepositoryImpl;
import com.nutanix.bpg.workload.Benchmarker;
import com.nutanix.bpg.workload.PGBenchOptions;
import com.nutanix.config.Configuration;

/**
 * implements {@link MeasurementServer}.
 * This implementation is based on in-memory catalogs
 * of {@link Metrics}, {@link Database} and {@link Plugin}.
 * These catalogs are sourced from YAML files
 * found in respective directories.
 * <p>
 * It stores measurements in a data Warehouse.
 * 
 * @author pinaki.poddar
 *
 */
public class MeasurementServerImpl implements MeasurementServer {
	private Repository repo;
	private Executor pool = Executors.newCachedThreadPool();
	
	private static Logger logger = LoggerFactory.getLogger(MeasurementServerImpl.class);

	private static MeasurementServer singleton;
	private static Configuration config;

	public static MeasurementServer instance() {
		if (singleton == null) {
			singleton = new MeasurementServerImpl();
		}
		return singleton;
	}
	
	/**
	 * initializes this server with given properties
	 * (merged with default properties).
	 * 
	 * @return a singleton.
	 * @param props set of properties. can be null
	 * implying default. 
	 * @throws Exception if anything goes wrong
	 */
	public static MeasurementServer configure(Configuration conf) throws Exception {
		config = conf;
		return instance();
	}

	private MeasurementServerImpl() {
		try {
			repo  =  RepositoryImpl.configure(config);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}





	

	/**
	 * takes a snapshot. A snapshot is a set of measurements
	 * taken at regular interval.
	 * This method returns a snapshot when not all measurements
	 * are taken.
	 * A count down begins as each measurement is taken.
	 * When all measurements are taken, this snapshot would
	 * notify all waiting threads.
	 * 
	 */
	@Override
	public CompletableFuture<Snapshot> takeSnapshot(
			String name, 
			Database database, 
			Metrics metrics,
			SnapshotSchedule schedule)
			throws Exception {
		
		MeasuremementTaker taker = new JDBCMeasurmentTaker(database, metrics);
		schedule.setMeasurmentTaker(taker);
		SnapshotPlugin plugin = new SnapshotPlugin(
				name, 
				database, 
				metrics, 
				schedule, 
				taker,
				repo.getConnection());
		return CompletableFuture.supplyAsync(plugin, pool);
	}
	
	@Override
	public Measurement takeMeasurement(
			String name, 
			Database database, 
			Metrics metrics)
			throws Exception {
		
		MeasuremementTaker taker = new JDBCMeasurmentTaker(database, metrics);
		return taker.takeMeasurement();
	}



	@Override
	public CompletableFuture<Snapshot> takeBenchmark(
			String name, 
			Database database, 
			PGBenchOptions[] options) throws Exception {
		logger.debug("name:" + name + " database:" + database
				+ " options=" + Arrays.toString(options));
		Benchmarker bn = new Benchmarker(name, database, options);
		return CompletableFuture.supplyAsync(bn, pool);
	}
}
