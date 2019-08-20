package test.swing;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.PropertyResourceBundle;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.junit.Test;

import com.nutanix.bpg.measure.swing.dialogs.TakeSnapshotWizard;
import com.nutanix.bpg.measure.swing.dialogs.Wizard;

public class GUITest {

	//@Test 
	public void testResource() throws Exception {
		String rsrc = "bpg.properties";
		InputStream in = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(rsrc);
		assertNotNull("resource " + rsrc + " not found", in);
		
		PropertyResourceBundle resources = new PropertyResourceBundle(in);
		assertNotNull("resource " + rsrc + " not found", resources);
	}
	
//	@Test
	public void testGUI() {
		Wizard wizard = new TakeSnapshotWizard();
		
		wizard.setAlwaysOnTop(true);
		wizard.setVisible(true);
	}
	
	@Test
	public void testTable() throws Exception {
		JFrame frame = new JFrame();
		frame.setTitle("test table");
		
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("C1");
		model.addColumn("C2");
		model.addRow(new String[] {"A", "B"});
		model.addRow(new String[] {"C", "D"});
		model.addRow(new String[] {"E", "F"});
		JTable table = new JTable(model);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				frame.getContentPane().add(new JScrollPane(table));
				frame.pack();
				frame.setVisible(true);
			}
		});
		System.in.read();
	}

}
