package test.utils;

import org.junit.Test;

import utils.thread.Counter;

public class TestCounter {
	
	@Test
	public void test() throws InterruptedException {
		
		Counter counter = new Counter( 0, 1000 );
		
		Thread[] runs = new Thread[5];
		for( int ix=0;ix<5;ix++ ) {
			runs[ix] = new Thread( new Run( counter, "thread#"+ix ) );
		}
		for( int ix=0;ix<5;ix++ ) {
			runs[ix].start();
		}
		for( int ix=0;ix<5;ix++ ) {
			runs[ix].join();
		}
		
	}
	
	public static class Run implements Runnable {
		
		protected Counter counter;
		protected String name;
		
		public Run( Counter counter, String name ) {
			this.counter = counter;
			this.name = name;
		}
		
		public void run() {
			Integer next = counter.next();
			while( next!=null ) {
				System.out.println( " >> " + name + " get counter value " + next );
				next = counter.next();
			}
		}
		
	}
	
}
