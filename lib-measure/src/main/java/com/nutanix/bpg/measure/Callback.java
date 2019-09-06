package com.nutanix.bpg.measure;

import java.util.Observer;
import java.util.concurrent.Future;
/**
 * A callback is an {@link Observer observer} 
 * to a {@link Plugin#run(java.util.Map, Object) 
 * measurement process}.
 * A callback {@link Observable#addObserver(Observer) registers}
 * itself with a  {@link Plugin plug-in} which is an {@link
 * Observable}.
 * <p>
 * The {@link Plugin plug-in}
 * notifies/calls back each registered {@link Observer observer}
 * via {@link Observer#update(Observable, Object) update}
 * method. 
 * <p>
 * The exact type second argument of update(...) method depends on specific
 * plug-in. However, a specific callback is responsible for
 * processing the update. 
 * <b>
 * For example, a plug-in may save the update to database,
 * another may simply collect the updates whereas one
 * may simply print them.
 * @author pinaki.poddar
 *
 * @param <T> generic type of return 
 */
public interface Callback<T> extends Observer, Future<T> {
	void done();
}
