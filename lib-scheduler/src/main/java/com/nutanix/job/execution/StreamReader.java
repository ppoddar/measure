package com.nutanix.job.execution;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * reads an input stream till end.
 * Typically invoked in a thread.
 * 
 * @author pinaki.poddar
 *
 */
public abstract class StreamReader<T> implements Runnable {
	protected BufferedReader in;
	protected StreamParser<T> parser;
	
	/**
	 * create a reader that delegates to given input stream.
	 * @param in an input stream to read
	 * @param err is it a error stream
	 */
	public StreamReader(InputStream in, StreamParser<T> parser) {
		setStream(in);
		this.parser = parser;
	}
	
	private void setStream(InputStream in) {
		this.in = new BufferedReader(new InputStreamReader(in));
	}
}
