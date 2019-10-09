package com.nutanix.resource.json;

import java.io.PrintStream;
import java.util.List;

public class LineWriter {
	final PrintStream out;
	public LineWriter(PrintStream out) {
		this.out = out;
	}

	
	public void write(int indent, Sourcecode.Line...lines) {
		int i = 0;
		int N = lines.length-1;
		for (Sourcecode.Line l : lines) {
			l.withIndent((i == 0 || i == N) ? indent : indent+1)
			 .write(out);
			i++;
		}
	}
	
	public void write(int indent, List<Sourcecode.Line> lines) {
		int i = 0;
		int N = lines.size()-1;
		for (Sourcecode.Line l : lines) {
			l.withIndent((i == 0 || i == N) ? indent : indent+1)
			 .write(out);
			i++;
		}
	}

}
