package nachos.threads;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nachos.machine.*;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
	/**
	 * Allocate a new Alarm. Set the machine's timer interrupt handler to this
	 * alarm's callback.
	 *
	 * <p><b>Note</b>: Nachos will not function correctly with more than one
	 * alarm.
	 */
	public Alarm() {
		waitingList = new HashMap<KThread, Long>();
		Machine.timer().setInterruptHandler(new Runnable() {
			public void run() { timerInterrupt(); }
		});
	}

	/**
	 * The timer interrupt handler. This is called by the machine's timer
	 * periodically (approximately every 500 clock ticks). Causes the current
	 * thread to yield, forcing a context switch if there is another thread
	 * that should be run.
	 */
	public void timerInterrupt() {
		Machine.interrupt().disable();
		if(!waitingList.isEmpty()){
			long time = Machine.timer().getTime();
			Iterator it = waitingList.keySet().iterator();
			KThread key;
			while(it.hasNext()){
				key = (KThread) it.next(); 
				if (waitingList.get(key).longValue() <= time){
					key.ready();
					waitingList.remove(key);
				}			
			}
		}
		Machine.interrupt().enable();
		KThread.yield();
	}

	/**
	 * Put the current thread to sleep for at least <i>x</i> ticks,
	 * waking it up in the timer interrupt handler. The thread must be
	 * woken up (placed in the scheduler ready set) during the first timer
	 * interrupt where
	 *
	 * <p><blockquote>
	 * (current time) >= (WaitUntil called time)+(x)
	 * </blockquote>
	 *
	 * @param	x	the minimum number of clock ticks to wait.
	 *
	 * @see	nachos.machine.Timer#getTime()
	 */
	public void waitUntil(long x) {
		if(x == 0)
			return;
		Machine.interrupt().disable();
		long wakeTime = Machine.timer().getTime() + x;
		waitingList.put(KThread.currentThread(), new Long(wakeTime));
		KThread.sleep();
		Machine.interrupt().enable();	
	}

	HashMap<KThread, Long> waitingList;
}
