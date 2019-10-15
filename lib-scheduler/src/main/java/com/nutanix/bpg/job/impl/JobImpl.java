package com.nutanix.bpg.job.impl;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobDescription;
import com.nutanix.bpg.job.JobTemplate;
import com.nutanix.capacity.Capacity;
import com.nutanix.resource.Resource;

/**
 * A Job specifies what to execute.
 * 
 * 
 * @author pinaki.poddar
 */
public class JobImpl implements Job {
	private String id;
	
	private JobTemplate template;
	private JobDescription spec;
	
	private long startTime = -1;
	private Capacity demand;
	private Capacity supply;
	private Resource supplier;
	private List<String> commands;
	private Map<String,String> env;
	
	private String submitter;
	
	
	public JobImpl(JobTemplate template, JobDescription spec) {
		id = UUID.randomUUID().toString();
		this.template = template;
		this.spec = spec;
	}
	
	
	public Resource getSupplier() {
		return supplier;
	}


	public void setSupplier(Resource supplier) {
		this.supplier = supplier;
	}



	public void setEnvironment(Map<String, String> env) {
		this.env = env;
	}

	public void setSupply(Capacity supply) {
		this.supply = supply;
	}


	public void setCommand(List<String> command) {
		this.commands = command;
	}


	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}


	
	
	
	
	public String getId() {
		return id;
	}
	
	protected Job setId(String id) {
		this.id = id;
		return this;
	}
	
	public String getName() {
		return getSpecification().getName();
	}

	public String getDescription() {
		return getSpecification().getDescription();
	}
	
	@Override
	public String toString() {
		return "job:" + getName();
	}

	@Override
	public String getCategory() {
		return getSpecification().getCategory();
	}

	

	@Override
	public long getExpectedCompletionTimeInMillis() {
		return -1;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long ts) {
		startTime = ts;
	}

	@Override
	public void setExpectedCompletionTimeInMillis(long ms) {
		
	}

	@Override
	public void setDemand(Capacity capacity) {
		this.demand = capacity;
	}

	@Override
	public Capacity getDemand() {
		if (demand == null) {
			throw new IllegalStateException("no demand capacity is set on " + this);
		}
		return demand;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobImpl other = (JobImpl) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	/**
	 * gets execution environment. null if not configured.
	 */
	@Override
	public Map<String, String> getEnvironment() {
		return env == null ? new HashMap<String, String>()
			: env;
	}


	@Override
	public Path getWorkingDirectory() {
		return template.getWorkingDirectory();
	}

	@Override
	public String getSubmitter() {
		return submitter;
	}

	@Override
	public Capacity getSupply() {
		return supply;
	}

	@Override
	public Resource getResource() {
		return supplier;
	}

	@Override
	public void setSupply(Resource rsrc, Capacity alloc) {
		supplier = rsrc;
		supply = alloc;
	}

	@Override
	public List<String> getCommand() {
		if (commands == null) {
			throw new IllegalStateException("no command set on this " + this);
		}
		return commands;
	}


	@Override
	public JobTemplate getTemplate() {
		return template;
	}


	@Override
	public JobDescription getSpecification() {
		return spec;
	}
	
	

	
}
