package junit.test;

import com.nutanix.bpg.job.Task;

public class TestTask implements Task<Integer> {
	private String name;
	final int result;
	final boolean throwError;
	
	public TestTask() {
		this("no name");
	}
	
	public TestTask(String name) {
		this(name, 1, false);
	}
	
	public TestTask(int i) {
		this("no name", i, false);
	}
	
	public TestTask(String name, int i) {
		this(name, i, false);
	}
	
	public TestTask(String name, int i, boolean err) {
		this.name = name;
		result = i;
		throwError = err;
	}
	
	public String getName() {
		return name;
	}
	@Override
	public Integer call() throws Exception {
		if (throwError) {
			throw new RuntimeException();
		}
		System.out.println("ran to return " + result);
		return result;
	}

	@Override
	public long getExpectedCompletionTimeInMillis() {
		return 1000;
	}
	
	public String toString() {
		return getName();
	}

}
