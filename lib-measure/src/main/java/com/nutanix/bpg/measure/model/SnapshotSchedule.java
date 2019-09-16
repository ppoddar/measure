package com.nutanix.bpg.measure.model;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.MeasuremementTaker;

/**
 * a schedule {@link #takeMeasurement() takes}
 * multiple measurement at regular {@link #getInterval()
 * interval}.
 * 
 * @author pinaki.poddar
 *
 */
public class SnapshotSchedule  implements Runnable {
	private  int count;
	private  int interval;
	private  TimeUnit unit;
	private  CountDownLatch latch;
	private  Snapshot sn;
	private  MeasuremementTaker taker;
	private static int DEFAULT_COUNT    = 2; 
	private static int DEFAULT_INTERVAL = 1; 
	private static TimeUnit DEFAULT_TIMEUNIT = TimeUnit.MINUTES; 
	
	private static Logger logger = LoggerFactory.getLogger(SnapshotSchedule.class);

	/**
	 * create a schedule to take 2 measurements
	 * at interval of 1 minute.
	 */
	public SnapshotSchedule() {
		logger.debug("creating SnapshotSchedule with default values" );
		setCount(DEFAULT_COUNT);
		setInterval(DEFAULT_INTERVAL);
		setIntervalTimeUnit(DEFAULT_TIMEUNIT);
	}
	
	/**
	 * set number of measurement to be taken.
	 * @param count
	 */
	public void setCount(int count) {
		logger.debug("SnapshotSchedule set count  " + count );
		this.count = count;
	}
	
	public void setIntervalTimeUnit(String unit) {
		logger.debug("SnapshotSchedule set interval unit  " + unit );
		try {
			this.unit = TimeUnit.valueOf(unit.toUpperCase());
		} catch (Exception ex) {
			ex.printStackTrace();
			this.unit = TimeUnit.MINUTES;
		}
	}

	/**
	 * set interval between measurements.
	 * @param count
	 */
	public void setIntervalTimeUnit(TimeUnit unit) {
		logger.debug("SnapshotSchedule set interval unit  " + unit );
		this.unit = unit;
	}
	
	public void setInterval(int i) {
		logger.debug("SnapshotSchedule set interval  " + i );
		this.interval = i;
	}

	/**
	 * time interval in millisecond between measurements
	 */
	public int getInterval() {
		return interval;
	}
	
	/**
	 * gets expected number of millisecond for this
	 * snapshot
	 * 
	 * @return
	 */
	public long getTimeLimit() {
		return TimeUnit.MILLISECONDS.convert(
				count*interval, unit);
	}
	
	/**
	 * gets number of measurements to be taken 
	 * in this schedule.
	 * @return
	 */
	public int getCount() {
		return count;
	}
	
	public int getRemainingCount() {
		return latch == null ? getCount() : 
			(int)latch.getCount();
	}
	
	public int getTakenCount() {
		return getCount() - getRemainingCount();
	}
	
	
	public TimeUnit getIntervalTimeUnit() {
		return unit;
	}
	
	
	public void setLatch(CountDownLatch l) {
		this.latch = l;
	}
	

	/**
	 * set the snapshot to which measurement would be added
	 * @param sn
	 */
	public void setSnapshot(Snapshot sn) {
		this.sn = sn;
	}
	
	public void setMeasurmentTaker(MeasuremementTaker t) {
		this.taker = t;
	}
 
	/**
	 * takes one measurement.
	 * adds it to snapshot
	 * counts down the latch
	 */
	public void run() {
		try {
			if (latch == null) {
				throw new IllegalStateException("no count down latch is set");
			}
			if (sn == null) {
				throw new IllegalStateException("no snapshot is set");
			}
			if (taker == null) {
				throw new IllegalStateException("no measurement taker is set");
			}
			logger.debug(this + " taking measurment. " + latch.getCount() 
			+ " more to go... next measurment would be taken after "
			+ getInterval() + " " + getIntervalTimeUnit());
			Measurement m = taker.takeMeasurement();
			m.setContext(Snapshot.CONTEXT_SNAPSHOT, sn.getId());
			//sn.addMeasurement(m);
			latch.countDown();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	


//	void saveMeasurment(Connection con, 
//			Snapshot sn,
//			Measurement m) throws SQLException {
//		SnapshotDAO dao     = new SnapshotDAO();
//		MeasurementDAO dao2 = new MeasurementDAO(con, 
//				m.getMetrics());
//	
//		logger.debug("insert snapshot " + sn.getId() + " measurement " + m.getId());
//		con.setAutoCommit(false);
//		//snapshot and measurement are logically related
//		dao.insert(con, sn, m);
//		m.setContext("snapshot", sn.getId());
//		dao2.insert(con, m);
//	
//		con.commit();
//	}
	
	public String toString() {
		return "schedule:" + getCount() + " in " 
				+ getInterval() + " " 
				+ getIntervalTimeUnit() + " interval";
	}
}
