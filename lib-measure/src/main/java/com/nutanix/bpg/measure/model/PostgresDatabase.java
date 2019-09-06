package com.nutanix.bpg.measure.model;

public class PostgresDatabase extends AbstractDatabase {
	
	public PostgresDatabase() {
		super(DatabaseKind.POSTGRES);
		setHost(getKind().getDefaultHost());
		setPort(getKind().getDefaultPort());
		setUser(getKind().getDefaultUser());
	}
	
	public PostgresDatabase(String name) {
		super(DatabaseKind.POSTGRES);
		setName(name);
		
		setHost(getKind().getDefaultHost());
		setPort(getKind().getDefaultPort());
		setUser(getKind().getDefaultUser());
	}

	public String getSchema() {
		return "pg_catalog";
	}
	

	@Override
	public String getUrl() {
		return String.format("%s://%s:%d/%s",
				getKind().getProtocol(), 
				getHost(), getPort(), getName());
		
	}
	
	@Override
	public boolean isStatisticsTable(String tableName) {
		return tableName.startsWith("pg_stat");
	}

	@Override
	public String[] getStatisticsTableTypes() {
		String[] types = {"SYSTEM VIEW"};
		return types;
	}

}
