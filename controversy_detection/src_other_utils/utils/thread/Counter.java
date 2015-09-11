package utils.thread;

import java.util.concurrent.locks.*;

/**
 * A synchronized counter.
 * 
 * @author Jiepu Jiang
 * @date May 31, 2013
 */
public class Counter {
	
	protected ReentrantLock lock;
	protected int next;
	protected int end;
	
	/**
	 * Constructor.
	 * Create a synchronized auto-increment integer counter, whose value
	 * starts from $init_value (include) and ends at $end (exclude).
	 * 
	 * @param bg				The starting value (include)
	 * @param ed				The termination value (exclude)
	 * @param lock				A ReentrantLock for synchronization
	 */
	public Counter( int bg, int ed, ReentrantLock lock ) {
		this.next = bg;
		this.end = ed;
		this.lock = lock;
	}
	
	/**
	 * Constructor.
	 * Create a synchronized auto-increment integer counter, whose value
	 * starts from $init_value (include) and ends at $end (exclude).
	 * 
	 * @param bg				The starting value (include)
	 * @param ed				The termination value (exclude)
	 */
	public Counter( int bg, int ed ) {
		this( bg, ed, new ReentrantLock() );
	}
	
	/**
	 * Constructor.
	 * Create an auto-increment integer counter, whose value
	 * starts from 0 (include) and ends at $end (exclude).
	 * 
	 * @param ed				The termination value (exclude)
	 */
	public Counter( int ed ) {
		this( 0, ed );
	}
	
	/**
	 * Request the next counter value, or null if it is the end of the counter.
	 * All the requests to the counter will be synchronized.
	 * Note that the returned value is not an int type.
	 * 
	 * @return
	 */
	public Integer next() {
		lock.lock();
		try{
			Integer return_value = null;
			if( next<end ) {
				return_value = next;
				next++;
			}
			return return_value;
		}finally{
			lock.unlock();
		}
	}
	
}
