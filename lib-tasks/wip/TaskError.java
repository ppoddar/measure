package junit.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.job.Task;

public class TaskError implements Task {
	private String name;
	private static Logger logger = LoggerFactory.getLogger(TaskError.class);

	public TaskError(String name) {
		super();
		this.name = name;
	}

	@Override
	public Boolean call() throws Exception {
		logger.info("threw exceptoin  " + this);
		throw new Exception("test exception from " + name);
	}

	@Override
	public long getExpectedCompletionTimeInMillis() {
		return 1000;
	}
	public String toString() {
		return "error task-" + name;
	}

}
