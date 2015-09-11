package utils.thread;

/**
 * Utilities related to processing concurrent running tasks.
 * 
 * @author Jiepu Jiang
 * @date Jun 3, 2013
 */
public class TaskUtils {
	
	/**
	 * Run the provided procs in separate threads until all threads are finished.
	 * 
	 * @param procs
	 * @throws InterruptedException
	 */
	public static void run( Runnable[] procs ) throws InterruptedException {
		Thread[] threads = new Thread[procs.length];
		for(int ix=0;ix<procs.length;ix++){
			threads[ix] = new Thread(procs[ix]);
		}
		run( threads );
	}
	
	/**
	 * Run the provided threads in separate threads until all threads are finished.
	 * 
	 * @param procs
	 * @throws InterruptedException
	 */
	public static void run( Thread[] threads ) throws InterruptedException {
		for(Thread thread:threads){
			thread.start();
		}
		for(Thread thread:threads){
			thread.join();
		}
	}
	
}
