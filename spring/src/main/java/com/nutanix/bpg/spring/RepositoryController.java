package com.nutanix.bpg.measure.spring;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nutanix.bpg.Repository;
import com.nutanix.bpg.RepositoryImpl;
import com.nutanix.bpg.measure.model.Database;
import com.nutanix.bpg.measure.model.DatabaseBuilder;
import com.nutanix.bpg.measure.model.DatabaseProperties;

@RestController
@RequestMapping("/repo")
public class RepositoryController {
	private Repository api;
	private static Logger logger = LoggerFactory.getLogger(RepositoryController.class);
	@Autowired YAMLConfig config;
	
	@PostConstruct
	public void initService() throws Exception {
		System.err.println("config catalog read from applicatiopn.yml:" + config.getCatalog());
		Properties props = new Properties();
//		props.setProperty(Repository.CATALOG_DATABASE_URL, 
//			catalog.get("catalog.database"));
		
		RepositoryImpl.init(props);;
		api = RepositoryImpl.instance();
	}

	@GetMapping(value = "/databases")
	public Collection<Database> getDatabases() {
		return api.getDatabases();
	}
	
	@PostMapping(value = "/database", 
			consumes = "application/json")
	public ResponseEntity<?> registerDatabases(@RequestBody DatabaseProperties dbProps) throws Exception {
		logger.info("register " + dbProps);
		Database db = DatabaseBuilder.build(dbProps);
		api.addDatabase(db, false);
		return new ResponseEntity<Database>(db, HttpStatus.CREATED);
	}

}
