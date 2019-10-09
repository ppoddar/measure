package com.nutanix.bpg.job.impl;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobToken;
import com.nutanix.bpg.repo.Repository;
import com.nutanix.bpg.sql.InsertSQL;

public class PersistenceJoQueue extends JobQueueImpl {
	private Repository repo;
	
	public PersistenceJoQueue(String name, Repository repo) {
		super(name);
		this.repo = repo;
	}
	@Override
	public JobToken addJob(Job job) {
		JobToken t = super.addJob(job);
		save(repo, t);
		return t;
	}

	void save(Repository repo, JobToken token) {
		InsertSQL sql = new InsertSQL();
		sql.into("JOBS");
		sql.insert("JOB_ID", token.getId());
		sql.insert("NAME", token.getName());
		sql.insert("START_TIME", token.getStartTime());
		sql.insert("END_TIME", token.getExpectedEndTime());
	}



}
