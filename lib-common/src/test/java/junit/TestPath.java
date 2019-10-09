package junit;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class TestPath {

	@Test
	public void testPath() {
		String outputRoot = "job-output";
		String fileName = "job.out";
		Path filePath = Paths.get(outputRoot, fileName);
		System.err.println(filePath.toString());
	}

}
