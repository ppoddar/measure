package com.nutanix.bpg.job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nutanix.bpg.utils.StringUtils;

/**
 * an execution stage executes its tasks either in sequence or in parallel.
 * <p>
 * 
 * 
 * 
 * @author pinaki.poddar
 */
// TODO: result of a stage is unclear

public interface Stage extends Callable<Boolean>  {
	public static char OPEN_GROUP = '(';
	public static char CLOSE_GROUP = ')';
	
	void setName(String name);
	String getName();
	int addTask(Task task);
	long getExpectedCompletionTimeInMillis();
}
