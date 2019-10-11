package com.nutanix.bpg.job;

import java.util.concurrent.Callable;

/**
 * a marker interface to execute 
 * job associated with {@link JobQueue#iterator() tokens}.
 * <p>
 * An executor operates on tokens that are {@link Job.Status#SCHEDULED}
 * and transitions them to {@link Job.Status#RUNNING}
 * <p>
 * An executor <em>assumes</em> that the {@link Job job}
 * associated with {@link JobToken tokens} is essentially
 * a {@link ScriptExecutor parameterized script}.
 * <p> 
 * 
 *
 */
public interface JobExecutor extends Callable<Void> {
}
