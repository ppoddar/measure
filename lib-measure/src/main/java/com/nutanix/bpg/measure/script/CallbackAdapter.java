package com.nutanix.bpg.measure.script;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.Callback;

public abstract class CallbackAdapter<T> implements Future<T>, Callback<T> {
	private static Logger logger = LoggerFactory.getLogger(Callback.class);
	protected final AtomicBoolean done = new AtomicBoolean(false);
	protected final AtomicBoolean cancelled = new AtomicBoolean(false);
	
	@Override
	public boolean cancel(boolean flag) {
		cancelled.getAndSet(flag);
		return false;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled.get();
	}
	
	@Override
	public boolean isDone() {
		return done.get();
	}
	
	public void done() {
		logger.info(this + " is done");
		synchronized (done) {
			done.getAndSet(true);
			done.notifyAll();
		}
	}
	
	
	public T get() throws InterruptedException, ExecutionException {
		return null;
	}
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
		return null;
	}

}
