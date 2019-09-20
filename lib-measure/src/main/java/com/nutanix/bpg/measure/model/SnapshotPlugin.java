package com.nutanix.bpg.measure.model;

import java.sql.Connection;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.MeasuremementTaker;
import com.nutanix.bpg.model.Database;
import com.nutanix.bpg.model.Metrics;
import com.nutanix.bpg.measurer.dao.SnapshotDAO;

/**
 * takes snapshot.
 * 
 * @author pinaki.poddar
 *
 */
public class SnapshotPlugin implements Supplier<Snapshot> {
	private static final Logger logger = LoggerFactory.getLogger(SnapshotPlugin.class);
	private Snapshot snapshot;
	private final String name;
	private final Database db;
	private final SnapshotSchedule schedule;
	private final Metrics metrics;
	private final MeasuremementTaker taker;
	private final Connection con;

	public SnapshotPlugin(String snapshotNmae, Database db, Metrics metrics, SnapshotSchedule schedule,
			MeasuremementTaker taker, Connection con) {
		name = snapshotNmae;
		this.schedule = schedule;
		this.db = db;
		this.metrics = metrics;
		this.taker = taker;
		this.con = con;
		
	}

	public Snapshot get() {
		if (snapshot == null) {
			try {
				snapshot = takeMeasurementsInSchedule();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return snapshot;
	}

	public Snapshot takeMeasurementsInSchedule() throws Exception {
		Snapshot sn = createSnapshot(name, db, metrics, schedule);

		logger.debug("starting " + this);
		CountDownLatch latch = new CountDownLatch(schedule.getCount());
		ScheduledExecutorService pool = Executors.newScheduledThreadPool(2); // at least 2
		schedule.setLatch(latch);
		schedule.setMeasurmentTaker(taker);
		ScheduledFuture<?> scheduleHandler = pool.scheduleAtFixedRate(schedule, 0, schedule.getInterval(),
				schedule.getIntervalTimeUnit());

		// Ref:
		// https://www.cosmocode.de/en/blog/schoenborn/2009-12/17-uncaught-exceptions-in-scheduled-tasks
		// SnapshotTaker would re-throw any exception it
		// may encounter in future!
		// A simple get would block
		Callable<Exception> exceptionHandler = new Callable<Exception>() {
			@Override
			public Exception call() throws Exception {
				try {
					scheduleHandler.get();
					return null;
				} catch (Exception e) {
					return e;
				}
			}
		};
		Executors.newFixedThreadPool(1).submit(exceptionHandler);

		try {
			logger.debug("waiting for countdown to end");
			latch.await();
			end(pool, sn);
			// TODO: handle exception
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

		return sn;
	}

	private Snapshot createSnapshot(String name, Database db, Metrics metrics, SnapshotSchedule schedule) {
		if (schedule == null) {
			throw new IllegalStateException("no schedule is set on snapshot");
		}
		if (taker == null) {
			throw new IllegalStateException("no snapshot taker is set on snapshot");
		}
		Snapshot sn = new Snapshot();
		sn.setId(UUID.randomUUID().toString());
		// set up before taking a snapshot
		sn.setName(name);
		sn.setExpectedMeasurementCount(schedule.getCount());
		sn.setDatabase(db);
		sn.setMetrics(metrics);
		schedule.setSnapshot(sn);
		return sn;
	}


	/**
	 * ends this schedule. notifies all waiting thread that the snapshot has ended.
	 */
	private void end(ScheduledExecutorService pool, Snapshot sn) {
		logger.debug(sn + " is complete");
		pool.shutdown();
		SnapshotDAO dao = new SnapshotDAO();
		if (con != null) {
			try {
				dao.insert(con, sn);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		synchronized (sn) {
			logger.trace("Notify all waiting threads that " + sn + " is complete");
			sn.notifyAll();
		}
	}

}
