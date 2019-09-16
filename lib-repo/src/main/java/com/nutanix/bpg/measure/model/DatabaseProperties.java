package com.nutanix.bpg.measure.model;

import java.util.Arrays;

import com.nutanix.bpg.measure.utils.StringUtils;

/**
 * properties to specify a database.
 * 
 * @author pinaki.poddar
 *
 */
public class DatabaseProperties {
	private DatabaseKind kind;
	private String name;
	private String host;
	private int    port;
	private String user;
	private String pwd;
	
	public DatabaseKind getKind() {
		return kind;
	}
	public String getName() {
		return name;
	}
	public String getHost() {
		return host;
	}
	public int getPort() {
		return port;
	}
	public String getUser() {
		return user;
	}
	public String getPwd() {
		return pwd;
	}
	
	public void setKind(DatabaseKind k) {
		this.kind = k;
	}
	
	public void setKind(String k) {
		this.kind = DatabaseKind.valueOf(k.toUpperCase());
		if (kind == null) {
			throw new IllegalArgumentException("[" + k + "]"
					+ " is not a valid databse kind"
					+ " valid kinds are " 
					+ Arrays.toString(DatabaseKind.values()));
		}
	}
	
	public void setName(String name) {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("null/empty databse name");
		}
		this.name = name;
	}
	
	public void setHost(String host) {
		if (StringUtils.isEmpty(host)) {
			throw new IllegalArgumentException("null/empty databse host");
		}
		this.host = host;
	}
	
	public void setPort(int port) {
		
		this.port = port;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public String toString() {
		String s = "database:";
		s += getName();
		s += "@" + getHost() + ":" + getPort();
		return s;
	}
}
