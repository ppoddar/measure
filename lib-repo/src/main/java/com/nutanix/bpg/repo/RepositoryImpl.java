package com.nutanix.bpg;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.model.Catalog;
import com.nutanix.bpg.measure.model.CatalogBuilder;
import com.nutanix.bpg.measure.model.Database;
import com.nutanix.bpg.measure.model.DatabaseFactory;
import com.nutanix.bpg.measure.model.Factory;
import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.MetricsFactory;
import com.nutanix.bpg.measure.model.Named;


public class RepositoryImpl implements Repository {
	private Catalog<Database> databases;
	private Catalog<Metrics>  metrices;
	private static Properties config;
	
	
	private static Repository singleton;
	
	private static Logger logger = LoggerFactory.getLogger(RepositoryImpl.class);
	
	static {
		try {
			String driver = "org.postgresql.Driver";
			Class.forName(driver);
			logger.info("loaded JDBC driver " + driver);
		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	
	public static void init(Properties props) {
		if (props != null) {
			config = defaultProperties();
			config.putAll(props);
		}
	}
	
	public static Repository instance() {
		if (singleton == null) {
			try {
				singleton = new RepositoryImpl();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		} 
		return singleton;
	}
	
	private RepositoryImpl() throws Exception {
		if (config == null) {
			logger.warn("repository is not configured. using default");
			config = defaultProperties();
		}
		String driver = getProperty(PROP_DRIVER);
		try {
			Class.forName(driver);
		} catch (Exception ex) {
			throw new ExceptionInInitializerError("can not load database driver " + driver);
		}
		Connection con = getConnection();
		con.close();
		
		databases = getCatalog(new DatabaseFactory(), getProperty(CATALOG_DATABASE_URL));
		metrices = new Catalog<>();
		for (Database db : databases) {
			metrices.addAll(MetricsFactory.build(db));
		}
		
	}
	
	@Override
	public Connection getConnection() {
		try {
			return DriverManager.getConnection(
						getDatabaseURL(),
						getUserName(), getPassword());
		} catch (SQLException ex) {
			throw new RuntimeException("can not connect to database "
					+ getDatabaseURL() + " as user " + getUserName(), ex);
		}
	}
	
	public String getUserName() {
		return RepositoryImpl.getProperty(PROP_USER);
	}
	public String getDatabaseURL() {
		return RepositoryImpl.getProperty(PROP_URL);
	}
	public String getDriverClassName() {
		return RepositoryImpl.getProperty(PROP_DRIVER);
	}
	private String getPassword() {
		return RepositoryImpl.getProperty(PROP_PASSWORD);
	}
	
	
	private static String getProperty(String key) {
		assertProperty(config, key);
		return config.getProperty(key);
	}
	
	private static void assertProperty(Properties p, String key) {
		if (!p.containsKey(key)) {
			throw new IllegalArgumentException("missing property " + key
					+ " available properties " + p.keySet());
		}
	}
	
	private static Properties defaultProperties() {
		Properties p = new Properties();
		p.setProperty(PROP_DRIVER,            "org.postgresql.Driver");
		p.setProperty(PROP_DATABASE_NAME,     "bpg");
		p.setProperty(PROP_DATABASE_HOST,     "localhost");
		p.setProperty(PROP_URL,               "jdbc:postgresql://localhost/bpg"); 
		p.setProperty(PROP_USER,              "postgres");
		p.setProperty(PROP_PASSWORD,          "");
		
		p.setProperty(CATALOG_DATABASE_URL, "catalog/databases/");
		return p;
	}
	
	@Override
	public void addDatabase(Database db, boolean overwrite) {
		db.getConnection();
		databases.add(db);
		Catalog<Metrics> catalog = MetricsFactory.build(db);
		metrices.addAll(catalog);
		
	}
	@Override
	public Database getDatabase(String name) {
		return databases.get(name);
	}

	

	@Override
	public Collection<Database> getDatabases() {
		return databases.values();
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
			Path dir = getConfiguredDirectory(path);
			if (dir == null) {
				logger.warn("can not read catalog directory [" + path + "]"
						+ " catalog would be empty");
				
				return (Catalog<T>)new Catalog();
			}
			CatalogBuilder<T> builder = new CatalogBuilder<T>()
					.withFactory(factory)
					.withDirectory(dir);
			return builder.build();
		} catch (Exception ex) {
			logger.warn("can not load catalog from [" + path + "]"
					+ " due to " + ex.getMessage()
					+ " the catalog would be empty");
			return (Catalog<T>)new Catalog();
		}
	}

	/**
	 * gets a directory as specified by given key
	 * @param key
	 * @return
	 */
	private static Path getConfiguredDirectory(String pathName) {
		Path path = Paths.get(pathName);
		if (!path.toFile().exists()) {
			logger.warn("configured path [" 
				+ path.toFile().getAbsolutePath() + "] "
				+ " does not exist");
		} else if (!path.toFile().isDirectory()) {
			logger.warn("configured path [" 
		+ path.toFile().getAbsolutePath() + "] " + " is not a directory");
		}
		return path;
	}

	@Override
	public Metrics getMetrics(String name) {
		return metrices.get(name);
	}

	@Override
	public Collection<Metrics> getMetrices() {
		return metrices.values();
	}


}
