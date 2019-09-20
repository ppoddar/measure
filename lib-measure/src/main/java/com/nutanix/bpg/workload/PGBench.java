package com.nutanix.bpg.workload;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.MeasuremementTaker;
import com.nutanix.bpg.model.Database;
import com.nutanix.bpg.model.Metrics;
import com.nutanix.bpg.model.MetricsDimension;
import com.nutanix.bpg.measure.model.Measurement;
import com.nutanix.bpg.measure.script.CommandExecutor;
import com.nutanix.bpg.measure.script.QuietParser;
import com.nutanix.bpg.measure.script.StreamParser;
import com.nutanix.bpg.utils.StringUtils;

/**
 * executes PGBench command in a separate process.
 * The remote process output and error are streamed
 * to this instance. 
 * <br>
 * A specialized, stateful {@link PGBenchoutputParser 
 * parser} parses those remote process output to 
 * a {@link Measurement}.
 * <br>
 * 
 * @author pinaki.poddar
 *
 */
public class PGBench implements MeasuremementTaker {
	private Database db;
	private PGBenchOptions options;
	public static Metrics METRICS;
	public static String METRICS_NAME  = "pgbench";
	public static String METRICS_TPS   = "tps";
	public static String METRICS_SCALE = "scale";
	public static String CONTEXT_TYPE  = "benchmark";
	public static String EXECUATABLE = "/usr/local/bin/pgbench";
	
//	public static final List<String> INIT_OPTIONS
//	= Arrays.asList(
//			OPTION_DATABASE, 
//			OPTION_INIT, 
//			OPTION_SCALE);
//	public static final List<String> RUN_OPTIONS
//		= Arrays.asList(
//			OPTION_DATABASE, 
//			OPTION_SCALE,
//			OPTION_TIME_TO_RUN);

	
	private Logger logger = LoggerFactory.getLogger(PGBench.class);
	// a metric for PGBENCH
	static {
		METRICS = new Metrics(PGBench.METRICS_NAME);
		METRICS.addDimension(new MetricsDimension(PGBench.METRICS_TPS));
		METRICS.addDimension(new MetricsDimension(PGBench.METRICS_SCALE));
	}
	
	/**
	 * 
	 * @param db target database
	 * @param options command line arguments to PGBench
	 */
	public PGBench(Database db, PGBenchOptions options) {
		this.db      = db;
		this.options = options;
	}
	
	/**
	 * runs <code>pgbench</code> command.
	 * <p>
	 * The command would spawn a separate OS process,
	 * parse process output, and block until
	 * result is available.
	 * 
	 * @param db target database
	 * @param options benchmark options
	 * 
	 * @throws Exception
	 */
	@Override
	public Measurement takeMeasurement() throws Exception {
		logger.info("run pgbench with options " + options);
		CommandExecutor ex = new CommandExecutor();
		initPGBench(ex, db, options);
		// run benchmark
		String[] commandOptions = options.getCommandOptions(db.getName());
		String[] commands = new String[commandOptions.length+1];
		commands[0] = EXECUATABLE;
		System.arraycopy(commandOptions, 0, commands, 1, commandOptions.length);
		logger.debug("running Postgres for collecting benchmark "
				+ "[" + StringUtils.join(' ', commands) + "]");
		StreamParser<Measurement> outputParser 
			= new PGBenchoutputParser();
		StreamParser<Object> errorParser 
			= new QuietParser();
		ex.executeRemoteProcess(
				commands,
				outputParser,errorParser);
		TimeUnit unit = options.getTimeToRunUnit();
		long timeout = 10*unit.toMillis(options.getTimeToRun());
		synchronized (outputParser) {
			try {
				logger.debug("waiting " + timeout + " ms for output parser " + outputParser);
				outputParser.wait(timeout);
				Measurement m = outputParser.getResult();
				if (m == null) {
					throw new RuntimeException("parser returned null measurement");
				}
				return m;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	
	private void initPGBench(CommandExecutor ex, 
			Database db,
			PGBenchOptions options) throws Exception {
		// run initialization and wait
		if (!options.isInit()) return;
		String[] commandOptions = options.getInitCommandOptions(db.getName());
		String[] commands = new String[commandOptions.length+1];
		commands[0] = EXECUATABLE;
		System.arraycopy(commandOptions, 0, commands, 1, commandOptions.length);
		ex.executeRemoteProcessAndWait(commands);
	}
}
