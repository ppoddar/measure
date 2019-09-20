package com.nutanix.bpg.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.utils.StringUtils;

public class DatabaseBuilder {
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(DatabaseBuilder.class);
	
	public static Database build(DatabaseProperties props) throws Exception {
		DatabaseKind kind = props.getKind();
		if (kind == null) {
			throw new IllegalArgumentException("can not recognize "
					+ " database kind [" + props.getKind() + "]");
		}
		String driverClassName = kind.getDriver();
		try {
			Class.forName(driverClassName);
		} catch (Exception e) {
			throw new RuntimeException("can not load driver [" + driverClassName + "]"
					+ " for  database kind [" + kind + "]");
		}
		switch (kind) {
		case POSTGRES:
			PostgresDatabase pg = new PostgresDatabase();
			pg.setName(props.getName());
			pg.setHost(props.getHost() == null 
					? kind.getDefaultHost() 
					: props.getHost());
			pg.setPort(props.getPort() == 0  
					? kind.getDefaultPort() 
					: props.getPort());
			pg.setUser(StringUtils.isEmpty(props.getUser())
					? kind.getDefaultUser()
					: props.getUser());
			if (!StringUtils.isEmpty(props.getPwd())) {
				pg.setPassword(props.getPwd());
			}
			
			return pg;

		default:
			throw new RuntimeException("unrecognized kind of database:[" + kind + "]");
		}
		
		
	}

}
