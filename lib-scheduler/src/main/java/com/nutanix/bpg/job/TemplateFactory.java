package com.nutanix.bpg.job;

import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nutanix.bpg.model.Factory;

public class TemplateFactory implements Factory<JobTemplate> {

	@Override
	public JobTemplate build(InputStream in) throws Exception {
		return new JobTemplate(new ObjectMapper(
				new YAMLFactory())
					.readTree(in));
		
	}

	@Override
	public Class<JobTemplate> getType() {
		return JobTemplate.class;
	}

}
