package com.nutanix.bpg.measure;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.Repository;
import com.nutanix.bpg.RepositoryImpl;
import com.nutanix.bpg.measure.jdbc.SnapshotPlugin;
import com.nutanix.bpg.measure.model.Catalog;
import com.nutanix.bpg.measure.model.CatalogBuilder;
import com.nutanix.bpg.measure.model.Database;
import com.nutanix.bpg.measure.model.DatabaseFactory;
import com.nutanix.bpg.measure.model.Factory;
import com.nutanix.bpg.measure.model.JDBCMeasurmentTaker;
import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.Named;
import com.nutanix.bpg.measure.model.Snapshot;
import com.nutanix.bpg.measure.model.SnapshotSchedule;
import com.nutanix.bpg.measure.utils.ClasspathUtils;
import com.nutanix.bpg.workload.Benchmarker;
import com.nutanix.bpg.workload.PGBenchOptions;

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
	private Catalog<Database> databases;
	private static Properties properties;
	private Executor pool = Executors.newCachedThreadPool();
	
	private static Logger logger = LoggerFactory.getLogger(MeasurementServerImpl.class);

	private static MeasurementServer singleton;
	public static final String DATABASE_CATALOG = "catalog.databases";


	public static MeasurementServer getInstance() {
		if (singleton == null) {
			throw new IllegalStateException("must call init before");
		}
		return singleton;
	}
	
	private MeasurementServerImpl() {
	}

	/**
	 * initializes this server with default properties
	 * @return a singleton.
	 * 
	 * @throws Exception if anything goes wrong
	 */
	public static MeasurementServer init() throws Exception {
		return init(null);
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
	public static MeasurementServer init(Properties props) throws Exception {
		properties = defaultProperties();
		if (props == null) {
			logger.warn("No configuration to initialize Measurement Server."
					+ " The server would be initialized with default configuration");
		} else {
			properties.putAll(props);
		}		
		logger.info(String.format("configuring server with properties %s", properties));
		RepositoryImpl.init(properties);
		MeasurementServerImpl instance = new MeasurementServerImpl();
		instance.repo      = RepositoryImpl.getInstance();
		instance.databases = getCatalog(new DatabaseFactory(),       DATABASE_CATALOG);

		return singleton = instance;
	}

	/**
	 * gets catalog of given type.
	 * 
	 * @param factory creates instance of 
	 * given type from an input stream of YAML content
	 * @param path a property name that points to a directory. 
	 * @return a catalog
	 * @throws Exception
	 */
	private static <T extends Named> Catalog<T> getCatalog(
			Factory<T> factory, String path) 
					throws Exception {
		try {
			DirectoryStream<Path> dir = getConfiguredDirectory(path);
			CatalogBuilder<T> builder = new CatalogBuilder<T>()
					.withFactory(factory)
					.withDirectory(dir);
			return builder.build();
		} catch (Exception ex) {
			logger.warn("can not load catalog from [" + path + "]"
					+ " the catalog would be empty");
			return (Catalog<T>)new Catalog();
		}
	}

	private static DirectoryStream<Path> getConfiguredDirectory(String key) {
		if (!properties.containsKey(key)) {
			throw new IllegalArgumentException(
					"missing key [" + key + "]" + " available keys are " + properties.keySet());
		}

		String name = properties.getProperty(key);
		return ClasspathUtils.getDirectory(name);
	}

	/**
	 * reads default properties from a classpath
	 * resource named <code>bpg.properties</code>.
	 * 
	 * @return
	 */
	private static Properties defaultProperties() {
		Properties p = new Properties();
		String rsrc = System.getProperty("bpg.properties","bpg.properties");
		try {
			InputStream in = ClasspathUtils.getInputStream(rsrc);
			p.load(in);
		} catch (Exception e) {
			logger.warn("can not read configuration properties from " + rsrc);
			p.setProperty(DATABASE_CATALOG, "catalog/databases");
		}
		return p;
	}


	@Override
	public Database getDatabase(String name) {
		return databases.get(name);
	}

	

	@Override
	public Collection<Database> getDatabases() {
		return databases.values();
	}
	
	public void takeSnapshot(String name,  
			Database database, 
			String metricsName,
			SnapshotSchedule schedule)
			throws Exception {
		takeSnapshot(name, database, 
				database.getMetrics(metricsName),  schedule);
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
	public void addDatabase(Database db, boolean overwrite) {
		db.getConnection();
		databases.add(db);
	}

	@Override
	public CompletableFuture<Snapshot> takeBenchmark(
			String name, 
			Database database, 
			PGBenchOptions[] options) throws Exception {
		
		Benchmarker bn = new Benchmarker(name, database, options);
		return CompletableFuture.supplyAsync(bn, pool);
	}
}
