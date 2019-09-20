package junit.test;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.bpg.task.Job;
import com.nutanix.bpg.task.JobImpl;
import com.nutanix.bpg.task.Stage;
import com.nutanix.bpg.task.StageImpl;
import com.nutanix.bpg.task.Task;
import com.nutanix.bpg.task.TaskImpl;
import com.nutanix.bpg.utils.JsonUtils;
import com.nutanix.bpg.utils.ResourceUtils;

public class TestJob {

	@Test
	public void testJobRepresntation() {
		Job job = new JobImpl();
		Stage s1 = new StageImpl();
		Stage s2 = new StageImpl(true);
		Stage s3 = new StageImpl();
		Task a = new TaskImpl("A");
		Task b = new TaskImpl("B");
		Task c = new TaskImpl("C");
		Task d = new TaskImpl("D");
		s1.addTask(a);
		s2.addTask(b);
		s2.addTask(c);
		s3.addTask(d);
		job.addStage(s1);
		job.addStage(s2);
		job.addStage(s3);

		assertEquals("(A)-(B|C)-(D)", job.getDesciptor());
		
		job.execute(1, TimeUnit.SECONDS);
	}
	
	@Test
	public void testJobExcutionNormally() {
		Job job = new JobImpl("normal");
		Stage stage = new StageImpl("stage-1");
		Task t = new TestTask();
		stage.addTask(t);
		job.addStage(stage);
		
		job.execute(1, TimeUnit.SECONDS);
		
	}
	@Test(expected=RuntimeException.class)
	public void testJobExcutionExceptionally() {
		Job job = new JobImpl("error");
		Stage stage = new StageImpl("stage-1");
		Task t = new TestTask(true);
		stage.addTask(t);
		job.addStage(stage);
		
		job.execute(1, TimeUnit.SECONDS);
	}
	
	@Test
	public void testReadJobDescription() throws Exception {
		URL url = ResourceUtils.getURL("src/test/resources/job.yml");
		JsonNode json = JsonUtils.readResource(url, true);
		JsonNode jobNode = JsonUtils.assertProperty(json, "job");
		
		Job job = new JobImpl(jobNode.get("name").asText());
		JsonNode stagesNode = JsonUtils.assertProperty(jobNode, "stages", true);
		for (JsonNode stageNode : stagesNode) {
			String name = JsonUtils.assertProperty(stageNode, "name").asText();
			boolean parallel = stageNode.has("parallel")
					? stageNode.get("parallel").asBoolean()
					: false;
			Stage stage = new StageImpl(name, parallel);
			job.addStage(stage);
			JsonNode tasksNode = JsonUtils.assertProperty(stageNode, "tasks");
			for (JsonNode taskNode : tasksNode) {
				Task task = new TaskImpl(JsonUtils.assertProperty(taskNode,"name").asText());
				stage.addTask(task);
			}
		}
		
		//System.out.println(job);
		assertEquals("(t1.1-t1.2)-(t2.1|t2.2|t2.3)-(t3.1)", job.getDesciptor());

	}
}
