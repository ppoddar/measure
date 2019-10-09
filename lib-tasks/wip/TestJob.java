package junit.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobExecutor;
import com.nutanix.bpg.job.Stage;
import com.nutanix.bpg.job.impl.JobExecutorImpl;
import com.nutanix.bpg.job.impl.JobImpl;
import com.nutanix.bpg.job.impl.ParallelStage;
import com.nutanix.bpg.job.impl.SequentialStage;

public class TestJob extends JobImpl {
	
	//@Test
	public void testExecuteJobRunNormally() throws Exception {
		JobExecutor executor = new JobExecutorImpl();
		Job job  = new JobImpl();
		Stage s1 = new ParallelStage();
		Stage s2 = new SequentialStage();
		s1.addTask(new TaskNormal("1.1"));
		s2.addTask(new TaskNormal("2.1"));
		s2.addTask(new TaskNormal("2.2"));
		job.addStage(s1);
		job.addStage(s2);
		
		
		CompletableFuture<Boolean> promise = executor.execute(job);
		assertTrue(promise.get());
		assertFalse(promise.isCompletedExceptionally());
		
	}
	
	@Test
	public void testExecuteJobRunExceptionally() throws Exception  {
		JobExecutor executor = new JobExecutorImpl();
		Job job  = new JobImpl();
		Stage s1 = new SequentialStage();
//		Stage s2 = new SequentialStage(false);
		s1.addTask(new TaskError("e1.1"));
//		s2.addTask(new TaskNormal("2.1"));
//		s2.addTask(new TaskError("e2.2"));
		job.addStage(s1);
//		job.addStage(s2);
		
		CompletableFuture<Boolean> promise = executor.execute(job);
		assertFalse(promise.get());
		assertTrue(promise.isCompletedExceptionally());
	}

}
