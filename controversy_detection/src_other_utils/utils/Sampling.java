package utils;

import java.util.Set;
import java.util.Random;
import java.util.HashSet;

/**
 * Utilities related to sampling.
 * 
 * @author Jiepu Jiang
 * @version Feb 28, 2013
 */
public class Sampling {
	
	public static void main( String[] args ) {
		try{
			
			int[] sample = woReplacement(4, 4);
			for( int val:sample ) {
				System.out.print(val+" ");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Create a size n random sample (with replacement) of integers from 0 (include) to the specified upperbound (exclude).
	 * 
	 * @param upperbound
	 * @param size_n
	 * @return
	 */
	public static int[] wReplacement ( int upperbound, int size_n ) {
		int[] samples = new int[size_n];
		Random random = new Random();
		for(int ix=0;ix<size_n;ix++){
			samples[ix] = random.nextInt(upperbound);
		}
		return samples;
	}
	
	/**
	 * Create a size n random sample (with replacement) of the specified source array.
	 * 
	 * @param source
	 * @param size_n
	 * @return
	 */
	public static int[] wReplacement ( int[] source, int size_n ) {
		int[] samples = new int[size_n];
		Random random = new Random();
		for(int ix=0;ix<size_n;ix++){
			int pos = random.nextInt(source.length);
			samples[ix] = source[pos];
		}
		return samples;
	}
	
	/**
	 * Create a size n random sample (with replacement) of the source array.
	 * 
	 * @param source
	 * @param size_n
	 * @return
	 */
	public static double[] wReplacement ( double[] source, int size_n ) {
		double[] samples = new double[size_n];
		Random random = new Random();
		for(int ix=0;ix<size_n;ix++){
			int pos = random.nextInt(source.length);
			samples[ix] = source[pos];
		}
		return samples;
	}
	
	/**
	 * Create a size n random sample (without replacement) of integers from 0 (include) to the specified upperbound (exclude).
	 * The current implementation would be somewhat slow if you are sampling a large sample from an also large whole sample.
	 * 
	 * @param upperbound
	 * @param size_n
	 * @return
	 */
	public static int[] woReplacement ( int upperbound, int size_n ) {
		Set<Integer> exist = new HashSet<Integer>();
		int[] samples = new int[size_n];
		Random random = new Random();
		for(int ix=0;ix<size_n;){
			int sampled_val = random.nextInt(upperbound);
			if(!exist.contains(sampled_val)){
				samples[ix] = sampled_val;
				exist.add(sampled_val);
				ix++;
			}
		}
		return samples;
	}
	
	/**
	 * Create a size n random sample (without replacement) of the source.
	 * The current implementation would be somewhat slow if you are sampling a large sample from an also large whole sample.
	 * 
	 * @param source
	 * @param size_n
	 * @return
	 */
	public static int[] woReplacement ( int[] source, int size_n ) {
		Set<Integer> exist = new HashSet<Integer>();
		int[] samples = new int[size_n];
		Random random = new Random();
		for(int ix=0;ix<size_n;){
			int sampled_pos = random.nextInt(source.length);
			if(!exist.contains(sampled_pos)){
				samples[ix] = source[sampled_pos];
				exist.add(sampled_pos);
				ix++;
			}
		}
		return samples;
	}
	
	/**
	 * Create a size n random sample (without replacement) of the source.
	 * The current implementation would be somewhat slow if you are sampling a large sample from an also large whole sample.
	 * 
	 * @param source
	 * @param size_n
	 * @return
	 */
	public static double[] woReplacement ( double[] source, int size_n ) {
		Set<Integer> exist = new HashSet<Integer>();
		double[] samples = new double[size_n];
		Random random = new Random();
		for(int ix=0;ix<size_n;){
			int sampled_pos = random.nextInt(source.length);
			if(!exist.contains(sampled_pos)){
				samples[ix] = source[sampled_pos];
				exist.add(sampled_pos);
				ix++;
			}
		}
		return samples;
	}
	
}
