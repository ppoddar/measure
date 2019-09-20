package com.nutanix.bpg.model;

import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class DatabaseFactory implements Factory<Database> {
	
	static ObjectMapper mapper;
	static {
		
		SimpleModule module = new SimpleModule();
		module.addAbstractTypeMapping(Database.class, PostgresDatabase.class);
		
		mapper = new ObjectMapper(new YAMLFactory());
		mapper.registerModule(module);
	}
	
	@Override
	public Database build(InputStream in) throws Exception {
		return mapper.readValue(in, Database.class);
	}

	@Override
	public Class<Database> getType() {
		return Database.class;
	}
	
	public static Database create(DatabaseKind kind, 
			String name, String host, String port, String user, String pwd) {
		if (DatabaseKind.POSTGRES == kind) {
			AbstractDatabase pg = new PostgresDatabase();
			pg.setName(name);
			if (host != null && !host.trim().isEmpty())
				pg.setHost(host);
			if (port != null && !port.trim().isEmpty())
				pg.setPort(Integer.parseInt(port));
			if (user != null && !user.trim().isEmpty())
				pg.setUser(user);
			if (pwd != null && !pwd.trim().isEmpty())
				pg.setPassword(pwd);
			return pg;
		} else {
			return null;
		}
	}

}
