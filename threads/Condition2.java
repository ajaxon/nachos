package nachos.threads;

import java.util.LinkedList;

import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
	/**
	 * Allocate a new condition variable.
	 *
	 * @param	conditionLock	the lock associated with this condition
	 *				variable. The current thread must hold this
	 *				lock whenever it uses <tt>sleep()</tt>,
	 *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
	 */
	public Condition2(Lock conditionLock) {
		this.conditionLock = conditionLock;
		waitingList = new LinkedList<KThread>();
	}

	/**
	 * Atomically release the associated lock and go to sleep on this condition
	 * variable until another thread wakes it using <tt>wake()</tt>. The
	 * current thread must hold the associated lock. The thread will
	 * automatically reacquire the lock before <tt>sleep()</tt> returns.
	 */

	/**
	 * 1. Release. 2. Disable interrupts. 3. Add thread to a list. 4. Sleep thread.
	 */
	public void sleep() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());	
		conditionLock.release();
		Machine.interrupt().disable();
		waitingList.add(KThread.currentThread());
		KThread.sleep();
		Machine.interrupt().enable();
		conditionLock.acquire();
	}

	/**
	 * Wake up at most one thread sleeping on this condition variable. The
	 * current thread must hold the associated lock.
	 */
	public void wake() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());

		if(!waitingList.isEmpty()){
			Machine.interrupt().disable();
			KThread runnable = waitingList.removeFirst();
			runnable.ready();
			Machine.interrupt().enable();
		}

	}

	/**
	 * Wake up all threads sleeping on this condition variable. The current
	 * thread must hold the associated lock.
	 */
	public void wakeAll() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		Machine.interrupt().disable();
		KThread runnable;
		while(!waitingList.isEmpty()){
			runnable = waitingList.removeFirst();
			runnable.ready();
		}
		Machine.interrupt().enable();

	}

	private Lock conditionLock;
	private LinkedList<KThread> waitingList;
}
