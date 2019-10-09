package junit.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

import org.junit.Test;

import com.nutanix.job.execution.ScriptExecutor;

public class TestScriptExecution {

	@Test
	public void testWrongCommandThrowsException() {
		ScriptExecutor executor = new ScriptExecutor()
				.withCommand(Arrays.asList("invalidcommand", "p1"));
		
		try {
			executor.call();
			fail("expeced wrong command " 
			+ " [" + executor.getOSCommand() + "]"
			+ " to throw exception");
		} catch (Exception ex) {
			ex.printStackTrace();
			// expected 
		}
	}
	
	@Test
	public void testCommandExceptionIsCapturedInErrorOutput() 
		throws Exception{
		File err = File.createTempFile("test", ".err");
		ScriptExecutor executor = new ScriptExecutor()
				.withOutput(new File("test.out"))
				.withErrorOutput(err)
				.withCommand(Arrays.asList("python", "p1"));
		assertEquals(0, countFileSize(err));
		
		try {
			executor.call();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("did not expec  command " 
			+ " [" + executor.getOSCommand() + "]"
			+ " to throw exception");
		}
		assertTrue(countFileSize(err) > 0);
	}
	

	int countFileSize(File f) throws Exception {
		BufferedReader reader = new BufferedReader(
				new FileReader(f));
		int i = 0;
		String line = null;
		while ((line = reader.readLine()) != null) {
			i++;
		}
		reader.close();
		return i;
	}

}
