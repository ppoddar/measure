package com.nutanix.bpg.job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.nutanix.bpg.utils.StringUtils;

/**
 * an execution stage executes its tasks either in sequence or in parallel,
 * combines the results of each task.
 * 
 * 
 * @author pinaki.poddar
 *
 * @param <R> type of value resulted in execution of this stage
 * @param <T> type of value resulted in execution of tasks of this stage
 */
public abstract class Stage<R, T> extends CompletableFuture<R>
	implements Iterable<Task<T>>, Callable<R> {
	public static char OPEN_GROUP = '(';
	public static char CLOSE_GROUP = ')';

	String name;
	private boolean parallel;
	private long expectedCompletionTimeInMills = 1000;
	List<Task<T>> tasks = new ArrayList<Task<T>>();

	public Stage() {
		this(false);
	}

	public Stage(boolean p) {
		parallel = p;
	}

	public String getName() {
		return name;
	}

	public Stage<R, T> setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * affirms if tasks in this stage are executed in parallel.
	 * 
	 * @return true if tasks are executed in parallel
	 */
	boolean isParallel() {
		return parallel;
	}

	/**
	 * gets an estimate of time to complete this stage of execution. The expected
	 * time of completion is either sum or maximum of expected time of completion of
	 * all tasks for sequential and parallel execution respectively.
	 * 
	 * @return time in millisecond.
	 */
	public long getExpectedCompletionTimeInMillis() {
		return expectedCompletionTimeInMills;
	}

	/**
	 * set timeout explicitly. If not set, timeout would be equal to
	 * {@link #getExpectedCompletionTimeInMillis() expected completion time}.
	 * 
	 * @param time
	 * @param unit
	 * @return
	 */
	public Stage<R, T> setTimeout(long time, TimeUnit unit) {
		expectedCompletionTimeInMills = TimeUnit.MILLISECONDS.convert(time, unit);
		return this;
	}

	public int addTask(Task<T> t) {
		tasks.add(t);
		return tasks.size();
	}

	@Override
	public Iterator<Task<T>> iterator() {
		return tasks.iterator();
	}

	@Override
	public R call() throws Exception {
		int N = tasks.size();
		R result = null;
		CompletionService<T> ecs = new ExecutorCompletionService<T>(Executors.newFixedThreadPool(N));
		for (Task<T> task : tasks) {
			ecs.submit(task);
			if (isParallel())
				continue;
			try {
				T t = ecs.take().get();
				result = combine(result, t);
			} catch (Exception ex) {
				this.completeExceptionally(ex);
			}
		}
		if (isParallel()) {
			for (int i = 0; i < N; i++) {
				result = combine(result, ecs.take().get());
			}
		}
		return result;
	}

	public String toString() {
		char sep = isParallel() ? Job.TASK_SEPARTAOR_PARRALEL : Job.TASK_SEPARTAOR;
		return OPEN_GROUP + StringUtils.join(sep, tasks) + CLOSE_GROUP;
	}

	protected abstract R combine(R r, T t);

	public static RuntimeException convertError(Throwable ex) {
		if (ExecutionException.class.isInstance(ex)) {
			return convertError(ExecutionException.class.cast(ex).getCause());
		} else if (RuntimeException.class.isInstance(ex)) {
			return RuntimeException.class.cast(ex);
		} else {
			return new RuntimeException(ex);
		}
	}

}
