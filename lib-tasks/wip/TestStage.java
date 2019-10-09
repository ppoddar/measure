package junit.test;


import com.nutanix.bpg.job.impl.SequentialStage;

public class TestStage extends SequentialStage {
	
	public TestStage(String name) {
		super();
		setName(name);
	}
}
