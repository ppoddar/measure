package junit.test;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.JobExecutor;
import com.nutanix.bpg.job.Stage;
import com.nutanix.bpg.job.Task;
import com.nutanix.bpg.job.impl.JobExecutorImpl;
import com.nutanix.bpg.utils.JsonUtils;
import com.nutanix.bpg.utils.ResourceUtils;

public class TestJobQueue {
	@Test
	public void testJobExcutionNormally() throws Exception {
		Job<Integer,Integer> job = new TestJob();
		job.setName("normal");;
		Stage<Integer,Integer> stage = new TestStage("normal");
		Task<Integer> t = new TestTask("normal");
		stage.addTask(t);
		job.addStage(stage);
		
		JobExecutor executor = new JobExecutorImpl();
		CompletableFuture<?> future = executor.execute(job);
		future.get();
		
	}
	@Test(expected=RuntimeException.class)
	public void testJobExcutionExceptionally() throws Exception {
		Job<Integer,Integer> job = new TestJob();
		job.setName("error");
		Stage<Integer,Integer> stage = new TestStage("error");
		Task<Integer> t = new TestTask("error", 1, true);
		stage.addTask(t);
		job.addStage(stage);
		
		JobExecutor executor = new JobExecutorImpl();
		CompletableFuture<?> future = executor.execute(job);
		future.get();
	}
	
	@Test
	public void testReadJobDescription() throws Exception {
		URL url = ResourceUtils.getURL("src/test/resources/job.yml");
		JsonNode json = JsonUtils.readResource(url, true);
		JsonNode jobNode = JsonUtils.assertProperty(json, "job");
		
		Job<Integer,Integer> job = new TestJob();
		job.setName(jobNode.get("name").asText());
		JsonNode stagesNode = JsonUtils.assertProperty(jobNode, "stages", true);
		for (JsonNode stageNode : stagesNode) {
			String stageName = JsonUtils.assertProperty(stageNode, "name").asText();
			boolean parallel = stageNode.has("parallel")
					? stageNode.get("parallel").asBoolean()
					: false;
			Stage<Integer,Integer> stage = new TestStage(parallel).setName(stageName);
			job.addStage(stage);
			JsonNode tasksNode = JsonUtils.assertProperty(stageNode, "tasks");
			for (JsonNode taskNode : tasksNode) {
				String taskName = JsonUtils.assertProperty(taskNode,"name").asText();
				Task<Integer> task = new TestTask(taskName);
				stage.addTask(task);
			}
		}
		
		assertEquals("(t1.1-t1.2)-(t2.1|t2.2|t2.3)-(t3.1)", 
			job.getDesciptor());

	}

}
