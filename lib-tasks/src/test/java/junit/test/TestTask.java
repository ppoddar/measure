package junit.test;

import com.nutanix.bpg.task.Task;

public class TestTask implements Task {
	final boolean throwError;
	
	public TestTask() {
		this(false);
	}
	public TestTask(boolean e) {
		throwError = e;
	}
	@Override
	public String call() throws Exception {
		if (throwError) {
			throw new RuntimeException();
		}
		return "ran " + this;
	}

}
