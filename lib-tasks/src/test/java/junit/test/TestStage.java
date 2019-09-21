package junit.test;


import com.nutanix.bpg.job.Stage;

public class TestStage extends Stage<Integer, Integer> {
	public TestStage(String name) {
		this(name, false);
	}
	public TestStage(boolean flag) {
		this("no name", flag);
	}
	
	public TestStage(String name, boolean p) {
		super(p);
	}
	public Integer combine(Integer s, Integer t) {
		if (s == null) return t;
		if (t == null) return s;
		return s.intValue() + t.intValue();
	}
}
