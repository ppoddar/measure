package junit.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.job.Task;

public class TaskNormal implements Task {
	private String name;
	private static Logger logger = LoggerFactory.getLogger(TaskNormal.class);
	
	public TaskNormal(String name) {
		super();
		this.name = name;
	}
	
	@Override
	public Boolean call() throws Exception {
		logger.info("ran  " + this);
		return true;
	}

	@Override
	public long getExpectedCompletionTimeInMillis() {
		return 1000;
	}
	
	public String toString() {
		return "task-" + name;
	}

}
