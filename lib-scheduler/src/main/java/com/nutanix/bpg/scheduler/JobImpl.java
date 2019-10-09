package com.nutanix.bpg.scheduler;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.utils.StringUtils;
import com.nutanix.capacity.Capacity;
import com.nutanix.job.execution.JobTemplate;
import com.nutanix.resource.Resource;

/**
 * A Job specifies what to execute.
 * 
 * 
 * @author pinaki.poddar
 */
public class JobImpl implements Job {
	private String id;
	private String category;
	private String name;
	private String descriptor = "";
	private JobTemplate template;
	private long startTime = -1;
	private Capacity demand;
	private Capacity supply;
	private Resource supplier;
	private List<String> commands;
	private Map<String,String> env;
	
	private String submitter;
	
	
	public JobImpl(JobTemplate template) {
		id = UUID.randomUUID().toString();
		this.template = template;
	}
	
	
	public Resource getSupplier() {
		return supplier;
	}


	public void setSupplier(Resource supplier) {
		this.supplier = supplier;
	}


	public Map<String, String> getEnv() {
		return env;
	}


	public void setEnv(Map<String, String> env) {
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


	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	public String getId() {
		return id;
	}
	
	protected Job setId(String id) {
		this.id = id;
		return this;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return descriptor;
	}
	
	@Override
	public String toString() {
		return "job:" + getName();
	}

	
	

	@Override
	public String getCategory() {
		return category;
	}

	

	@Override
	public long getExpectedCompletionTimeInMillis() {
		return -1;
	}

	@Override
	public void setDesciptor(String desc) {
		if (!StringUtils.isEmpty(desc)) 
			this.descriptor = desc;
		
	}

	@Override
	public void setCategory(String cat) {
		this.category = cat;
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
		return env == null ? Collections.emptyMap(): Collections.unmodifiableMap(env);
	}


	@Override
	public String getWorkingDirectory() {
		return template.getDirectory();
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
		return commands;
	}


	@Override
	public JobTemplate getTemplate() {
		return template;
	}

	
}
