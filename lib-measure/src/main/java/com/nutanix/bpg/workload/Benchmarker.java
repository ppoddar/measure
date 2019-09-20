package com.nutanix.bpg.workload;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.model.Database;
import com.nutanix.bpg.measure.model.Measurement;
import com.nutanix.bpg.measure.model.Snapshot;

/**
 * takes multiple measurement sequentially.
 * 
 * @author pinaki.poddar
 *
 */
public class Benchmarker implements Supplier<Snapshot> {
	private static final Logger logger = LoggerFactory.getLogger(Benchmarker.class);
	private final String name;
	private final Database database;
	private final PGBenchOptions[] options;
	
	private Snapshot benchmarks;

	public Benchmarker(final String name, 
			final Database database, 
			final PGBenchOptions[] options) {
		this.name = name;
		this.database = database;
		this.options = options;
	}

	@Override
	public Snapshot get() {
		if (benchmarks == null) {
			benchmarks = run();
		}
		return benchmarks;
	}

	public Snapshot run() {
		benchmarks = new Snapshot();
		benchmarks.setName(name);
		benchmarks.setMetrics(PGBench.METRICS);
		benchmarks.setExpectedMeasurementCount(options.length);
		for (PGBenchOptions option : options) {
			
			logger.debug("run PGBENCH with " + option);
			PGBench pgbench = new PGBench(database, option);
			try {
				Measurement m = pgbench.takeMeasurement();
				if (m == null) {
					throw new RuntimeException("pgbench retured null measurment");
				}
				m.setContext(Snapshot.CONTEXT_BENCHMARK, benchmarks.getId());
				//benchmarks.addMeasurement(m);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return benchmarks;
	}

}
