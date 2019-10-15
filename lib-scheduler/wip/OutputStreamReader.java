package com.nutanix.job.execution;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * reads an input stream till end.
 * Typically invoked in a thread.
 * 
 * @author pinaki.poddar
 *
 */
public class OutputStreamReader<T> extends StreamReader<T> {
	private static Logger logger = LoggerFactory.getLogger(OutputStreamReader.class);
	
	/**
	 * create a reader with an input stream to read
     * and a parser to parse the output
	 * @param parser
	 */
	public OutputStreamReader(InputStream in,
			StreamParser<T> parser) {
		super(in, parser);
	}
	
	/**
	 * reads underlying stream until EOF
	 * or parser has parsed a measurement.
	 * <p>
	 * The caller must wait on this to be notified
	 * <p>
	 * notifies all upon completion.
	 * Throws exception if  EOF and no measurement
	 * has been parsed.
	 */
	@Override
	public void run()  {
	    logger.trace("starting " + this);
		String line = null;
		T result = null;
		try {
			while ((line = in.readLine()) != null) {
				logger.trace(">>" + line);
				result = parser.parse(line.toString());
				if (result != null) {
					synchronized (parser) {
						parser.notifyAll();
					}
					logger.trace("parsed stream " + this  + " to  " + result);
					return;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			synchronized (parser) {
				parser.notifyAll();
			}
		}
		if (parser.getResult() == null) {
			logger.warn("reached output stream EOF, but not parsed any measurement");
		}
		
	}
	
	public String toString() {
		return "OutputStreamReader@" + Integer.toHexString(System.identityHashCode(this));
	}
}
