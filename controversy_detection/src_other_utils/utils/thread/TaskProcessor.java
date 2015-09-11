package utils.thread;

/**
 * A template for parallel threads processing pooled tasks. We assume that 
 * you will pile up all the tasks in a static pool, where each task can be 
 * characterized by an integer taskid (typically the taskids are auto-incremental 
 * integers starting from 0 with stepping 1). Each thread will iteratively 
 * request unprocessed tasks through a synchronized counter until all the 
 * tasks have been finished.
 * 
 * @author Jiepu Jiang
 * @date May 31, 2013
 */
public abstract class TaskProcessor implements Runnable {
	
	protected Counter counter;
	protected String name;
	
	/**
	 * Constructor.
	 * 
	 * @param counter
	 */
	public TaskProcessor( Counter counter ) {
		this( counter, null );
	}
	
	/**
	 * Constructor.
	 * 
	 * @param counter
	 * @param name
	 */
	public TaskProcessor( Counter counter, String name ) {
		this.counter = counter;
		this.name = name;
	}
	
	/**
	 * 
	 * @param counter
	 * @return
	 */
	public TaskProcessor setCounter( Counter counter ) {
		this.counter = counter;
		return this;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public TaskProcessor setName( String name ) {
		this.name = name;
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public Counter Counter() {
		return this.counter;
	}
	
	/**
	 * 
	 * @return
	 */
	public String name() {
		return this.name;
	}
	
	public void run() {
		Integer next_task = counter.next();
		while( next_task!=null ) {
			process( next_task );
			next_task = counter.next();
		}
	}
	
	/**
	 * This should be implemented by subclasses.
	 * This method specifies how to process each individual task (as characterized by the taskid).
	 * 
	 * @param taskid
	 */
	public abstract void process( int taskid );
	
	/**
	 * Get a factory class of the processor.
	 * 
	 * @return
	 */
	public abstract TaskProcessorFactory getFactory();
	
	/**
	 * An interface for factory classes of TaskProcessor.
	 * 
	 * @author Jiepu Jiang
	 * @date Jun 3, 2013
	 */
	public interface TaskProcessorFactory {
		
		/**
		 * Create a copy for the template task processor with a new name.
		 * 
		 * @param template
		 * @param name
		 * @return
		 */
		public abstract TaskProcessor create( TaskProcessor template, String name );
		
	}
	
	/**
	 * Create several parallel threads based on current thread as a template.
	 * 
	 * @param numThreads
	 * @return
	 */
	public TaskProcessor[] createParallelThreads( int numThreads ) {
		TaskProcessor[] procs = new TaskProcessor[ numThreads ];
		for( int ix=0;ix<numThreads;ix++ ) {
			String name = "Thread#" + (ix+1);
			procs[ix] = this.getFactory().create( this, name );
		}
		return procs;
	}
	
	/**
	 * Create several parallel threads based on current thread as a template.
	 * Then, process the task pool using these threads until all tasks are finisehd.
	 * 
	 * @param numThreads
	 * @throws InterruptedException
	 */
	public void processWithParallelThreads( int numThreads ) throws InterruptedException {
		TaskProcessor[] procs = createParallelThreads( numThreads );
		TaskUtils.run( procs );
	}
	
}
