package com.nutanix.bpg.repo;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.model.Catalog;
import com.nutanix.bpg.model.CatalogBuilder;
import com.nutanix.bpg.model.Database;
import com.nutanix.bpg.model.DatabaseFactory;
import com.nutanix.bpg.model.Metrics;
import com.nutanix.bpg.model.MetricsFactory;
import com.nutanix.config.Configuration;


public class RepositoryImpl implements Repository {
	private Catalog<Database> databases;
	private Catalog<Metrics>  metrices;
	private static Configuration config;
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
	
	/**
	 * initialize singleton repository instance.
	 * 
	 * @param props 
	 * @return
	 */
	public static Repository configure(Configuration conf) 
		throws Exception {
		config = conf;
		return instance();
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
	
	/**
	 * private constructor.
	 * Configuration must be set.
	 * 
	 * @throws Exception
	 */
	private RepositoryImpl() throws Exception {
		if (config == null) {
			throw new IllegalStateException("configuration is not set");
		}
		Connection con = getConnection();
		con.close();
		
		buildDatabaseCatalog(config.resolvePath("catalog-root"));
	}
	// 
	public void buildDatabaseCatalog(Path root) throws Exception {
		CatalogBuilder<Database> builder 
			= new CatalogBuilder<Database>()
			.withDirectory(root)
			.withFactory(new DatabaseFactory());
		databases = builder.build();
		metrices = new Catalog<>();
		for (Database db : databases) {
			metrices.addAll(MetricsFactory.build(db));
		}
	}
	
	@Override
	public Connection getConnection() {
		String databaseURL  = config.getString("url");
		String databaseUser = config.getString("user", null);
		String databasePwd  = config.getOptionalString("password");
		try {
			return DriverManager.getConnection(
					databaseURL,
				    databaseUser, databasePwd);
		} catch (SQLException ex) {
			throw new RuntimeException("can not connect to database "
					+ databaseURL + " as user " + databaseUser, ex);
		}
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
	

	@Override
	public Metrics getMetrics(String name) {
		return metrices.get(name);
	}

	@Override
	public Collection<Metrics> getMetrices() {
		return metrices.values();
	}


}
