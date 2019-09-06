package com.nutanix.bpg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RepositoryImpl implements Repository {
	private static final String PROP_DATABASE_NAME = "database.name";
	private static final String PROP_DATABASE_HOST = "database.host";
	private static final String PROP_URL           = "database.url";
	private static final String PROP_DRIVER        = "database.driver";
	private static final String PROP_USER          = "database.user";
	private static final String PROP_PASSWORD      = "database.password";
	private static Properties properties;
	
	private static Logger logger = LoggerFactory.getLogger(RepositoryImpl.class);
	
	private static Repository singleton;
	
	
	static {
		try {
			String driver = "org.postgresql.Driver";
			Class.forName(driver);
			logger.info("loaded JDBC driver " + driver);
		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	
	public static Repository init(Properties props) {
		if (props == null || props.isEmpty()) {
			properties = defaultProperties();
			logger.warn("Database is not explicitly configured. Using defaults " 
					+ " JDBC url " + properties.getProperty(PROP_URL)
					+ " as user "  + properties.getProperty(PROP_USER));
		} else {
			properties = defaultProperties();
			properties.putAll(props);
			logger.info("initializing database " + properties.getProperty(PROP_URL));
			assertProperty(properties, PROP_URL);
			assertProperty(properties, PROP_DRIVER);
			assertProperty(properties, PROP_USER);
			assertProperty(properties, PROP_USER);
		}
		
		String driver = getProperty(PROP_DRIVER);
		try {
			Class.forName(driver);
		} catch (Exception ex) {
			throw new ExceptionInInitializerError("can not load database driver " + driver);
		}
		singleton = new RepositoryImpl();
		singleton.getConnection();

		return singleton;
	}
	
	public static Repository getInstance() {
		if (singleton == null) {
			throw new IllegalStateException("Repository is not initialized"
					+ " Please call init()");
		} 
		return singleton;
	}
	
	private RepositoryImpl() {
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
		assertProperty(properties, key);
		return properties.getProperty(key);
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
		p.setProperty(PROP_URL,               "jdbc:postgresql://localhost/" 
				+ "bpg" + "?" + "stringtype=unspecified");
		p.setProperty(PROP_USER,              "postgres");
		p.setProperty(PROP_PASSWORD,          "");
		return p;
	}
}
