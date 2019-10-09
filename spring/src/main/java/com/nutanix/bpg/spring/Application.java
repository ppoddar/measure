package com.nutanix.bpg.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;

@SpringBootApplication(
	exclude= {
		DataSourceAutoConfiguration.class,
		SecurityAutoConfiguration.class,
		ManagementWebSecurityAutoConfiguration.class
	}
)

public class Application  {
	Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
//		for (String name: ctx.getBeanDefinitionNames()) {
//            System.out.println(name);
//        }
	}


}
