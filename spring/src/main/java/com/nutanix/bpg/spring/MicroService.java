package com.nutanix.bpg.spring;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nutanix.bpg.utils.JsonUtils;
import com.nutanix.config.Configuration;
import com.nutanix.config.ConfigurationFactory;

public abstract class MicroService {
	ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	private static final Logger logger = LoggerFactory.getLogger(MicroService.class);
	
	
	/**
	 * load module properties.
	 * 
	 * Locates a configuration by JVM properties -Dconfig.
	 * The configuration is a YML file.
	 * Within this configuration, a module can be found
	 * at '/modules/<module-name>' node
	 * This node is converted to a Java properties
	 * and used to initialize corresponding module
	 *   
	 * @param module
	 * @return null if properties can  not be loaded
	 */
	protected JsonNode loadProperties(String module) {
		String configFileLocation = System.getProperty("config");
		if (configFileLocation == null) {
			throw new RuntimeException("config file loccation -Dconfig not present");
		}
		try {
			Path path = Paths.get(configFileLocation);
			JsonNode config = readJson(path);
			JsonNode moduleConfig = config.at("/modules/"+module);
			if (moduleConfig.isMissingNode()) {
				throw new IllegalArgumentException("configuration for " + module + " not found "
						+ " in main configuration as " + configFileLocation
						+ " in modules/" + module + " section."
						+ " Module [" + module + "] will not be initailized");
			}
			return moduleConfig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	protected Configuration loadConfiguration(String module) {
		String configFileLocation = System.getProperty("config");
		if (configFileLocation == null) {
			throw new RuntimeException("config file loccation -Dconfig not present");
		}
		
		Path path = Paths.get(configFileLocation).normalize();
		Configuration config = ConfigurationFactory.newConfiguration(path.toUri());
		return config.getSection("modules").getSection(module);
	}

	
	JsonNode readJson(Path path) {
		try {
			InputStream in  = Files.newInputStream(path);
			if (in == null) {
				throw new RuntimeException(path + " not found");
			}
			JsonNode json = mapper.readTree(in);
			return json;
		} catch (Exception ex) {
			throw new RuntimeException("error reading " + path, ex);
		}
	}
	
//	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
		CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
		loggingFilter.setIncludeClientInfo(false);
		loggingFilter.setIncludeQueryString(true);
		loggingFilter.setIncludePayload(true);
		return loggingFilter;
	}
	


}
