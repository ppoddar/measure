package junit.test;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutanix.bpg.job.Job;
import com.nutanix.bpg.job.Stage;
import com.nutanix.bpg.job.Task;
import com.nutanix.bpg.job.impl.JobImpl;
import com.nutanix.bpg.job.impl.SequentialStage;

public class TestSerde {
	
	static ObjectMapper mapper;
	@BeforeClass
	public static void setupObjectMapper() throws Exception {
		mapper = new ObjectMapper();
	}
	@Test
	public void testJob() throws Exception {
		Job job = new JobImpl();
		job.setName("test-serde");
		Stage stage = new SequentialStage();
		stage.setName("test-stage");
		job.addStage(stage);
		Task task = new TaskNormal("normal");
		stage.addTask(task);
		assertRoundtrip(job, Job.class);
	}
	
	public <T> void assertRoundtrip(T obj, Class<T> cls) throws Exception {
		String json = mapper
				.writerWithDefaultPrettyPrinter()
				.writeValueAsString(obj);
		T other = mapper.convertValue(mapper.readTree(json), cls);
		
		System.out.println(json);
		assertEquals(obj, other);
	}


}
