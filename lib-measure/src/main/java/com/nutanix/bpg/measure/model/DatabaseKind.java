package com.nutanix.bpg.measure.model;


public enum DatabaseKind implements Named {
	POSTGRES("POSTGRES", "jdbc:postgresql",
			"org.postgresql.Driver",
			"127.0.0.1", 5432, "postgres");
		
	
	private String kind;
	private String host;
	private String user;
	private String protocol;
	private String driver;
	private int port;
	
	
	private DatabaseKind(String kind, 
			String protocol, 
			String driver,
			String host, int port, 
			String user) {
		this.kind = kind;
		this.protocol = protocol;
		this.driver = driver;
		this.host = host;
		this.port = port;
		this.user = user;
	}
	
	public String getDriver() {
		return driver;
	}

	public String getName() {
		return kind;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public String getDefaultHost() {
		return host;
	}
	
	public int getDefaultPort() {
		return port;
	}
	
	public String getDefaultUser() {
		return user;
	}
	
	public String toString() {
		return getName();
	}


}
