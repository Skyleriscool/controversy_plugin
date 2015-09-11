package utils;

import java.util.*;
import java.math.*;

/**
 * Utilities related to array operation.
 * 
 * @author Jiepu Jiang
 * @date May 31, 2013
 */
public class ArrayUtils {
	
	/**
	 * Fill selected parts of an existing array with auto-increment integers.
	 * The first position, i.e. $ix_bg, will be filled with value $val_bg;
	 * then, the second position, i.e. $ix_bg + 1, will be filled with value $val_bg + step;
	 * until the position $ix_ed - 1 is filled.
	 * 
	 * @param array
	 * @param ix_bg
	 * @param ix_ed
	 * @param val_bg
	 * @param step
	 */
	public static void fill( int[] array, int ix_bg, int ix_ed, int val_bg, int step ) {
		for( int ix=ix_bg, val=val_bg;ix<ix_ed;ix++, val+=step ) {
			array[ix] = val;
		}
	}
	
	/**
	 * Create an array with auto-increment integers starting from $bg (include) 
	 * and ending at $ed (exclude), with the specified step value ($step).
	 * <pre>
	 * 	$begin <= element_value < $end
	 * </pre>
	 * 
	 * @param bg
	 * @param ed
	 * @param step
	 * @return
	 */
	public static int[] createIntArray( int bg, int ed, int step ) {
		int array_length = (ed-bg)/step + 1;
		if( (array_length-1)*step == ed-bg ) {
			array_length = array_length - 1;
		}
		int[] vals = new int[array_length];
		for(int val=bg,ix=0;val<ed;val=val+step,ix++){
			vals[ix] = val;
		}
		return vals;
	}
	
	/**
	 * Create an array list with auto-increment integers starting from $begin (include) 
	 * and ending at $end (exclude), with the specified step value ($step).
	 * <pre>
	 * 	$begin <= element_value < $end
	 * </pre>
	 * 
	 * @param bg
	 * @param ed
	 * @param step
	 * @return
	 */
	public static ArrayList<Integer> createIntArrayList( int bg, int ed, int step ) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int val=bg;val<ed;val=val+step){
			list.add( val );
		}
		return list;
	}
	
	/**
	 * Create an array with auto-increment integers starting from $begin (include) 
	 * and ending at $end (exclude), with the step value 1.
	 * <pre>
	 * 	$begin <= element_value < $end
	 * </pre>
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public static int[] createIntArray( int begin, int end ) {
		return createIntArray( begin, end, 1 );
	}
	
	/**
	 * Create an array list with auto-increment integers starting from $begin (include) 
	 * and ending at $end (exclude), with the step value 1.
	 * <pre>
	 * 	$begin <= element_value < $end
	 * </pre>
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public static ArrayList<Integer> createIntArrayList( int begin, int end ) {
		return createIntArrayList( begin, end, 1 );
	}
	
	/**
	 * Create an array with auto-increment doubles starting from $begin (include) 
	 * and ending at $end (exclude), with the specified step value ($step).
	 * <pre>
	 * 	$begin <= element_value < $end
	 * </pre>
	 * 
	 * @param bg
	 * @param ed
	 * @param step
	 * @return
	 */
	public static double[] createDoubleArray( double bg, double ed, double step ) {
		List<Double> values = new ArrayList<Double>();
		BigDecimal val = BigDecimal.valueOf(bg);
		BigDecimal augend = BigDecimal.valueOf(step);
		while( val.doubleValue()<ed ) {
			values.add( val.doubleValue() );
			val = val.add( augend );
		}
		return ArrayUtils.toDoubleArray( values );
	}
	
	/**
	 * Transform the double array into a Double object array.
	 * 
	 * @param values
	 * @return
	 */
	public static Double[] toDoubleArray( double... values ) {
		Double[] vals = null;
		if(values!=null){
			vals = new Double[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the Double object array into a double array.
	 * 
	 * @param values
	 * @return
	 */
	public static double[] toDoubleArray(Double... values) {
		double[] vals = null;
		if(values!=null){
			vals = new double[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the int array into a double array.
	 * 
	 * @param values
	 * @return
	 */
	public static double[] toDoubleArray(int... values) {
		double[] vals = null;
		if(values!=null){
			vals = new double[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the float array into a double array.
	 * 
	 * @param values
	 * @return
	 */
	public static double[] toDoubleArray(float... values) {
		double[] vals = null;
		if(values!=null){
			vals = new double[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the long array into a double array.
	 * 
	 * @param values
	 * @return
	 */
	public static double[] toDoubleArray(long... values) {
		double[] vals = null;
		if(values!=null){
			vals = new double[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Parse and transform the String array into a double array.
	 * Note that exception may be thrown if irregular String exists.
	 * 
	 * @param values
	 * @return
	 */
	public static double[] toDoubleArray(String... values) {
		double[] vals = null;
		if(values!=null){
			vals = new double[values.length];
			int ix = 0;
			for(String val:values){
				vals[ix] = Double.parseDouble(val);
				ix++;
			}
		}
		return vals;
	}
	
	/**
	 * Iteratively visit each element in the collection,
	 * transform or parse the element into double value,
	 * and finally save into a double array. The elements 
	 * can be String type or sub-class of Number type.
	 * 
	 * @param values
	 * @return
	 */
	public static double[] toDoubleArray(Collection<?> values) {
		double[] vals = null;
		if(values!=null){
			vals = new double[values.size()];
			int ix = 0;
			for(Object val:values){
				if(val instanceof Number){
					vals[ix] = ((Number)val).doubleValue();
				}else{
					vals[ix] = Double.parseDouble(val.toString());
				}
				ix++;
			}
		}
		return vals;
	}
	
	/**
	 * Transform the float array into a Float object array.
	 * 
	 * @param values
	 * @return
	 */
	public static Float[] toFloatArray(float... values) {
		Float[] vals = null;
		if(values!=null){
			vals = new Float[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the Float object array into a float array.
	 * 
	 * @param values
	 * @return
	 */
	public static float[] toFloatArray(Float... values) {
		float[] vals = null;
		if(values!=null){
			vals = new float[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the int array into a float array.
	 * 
	 * @param values
	 * @return
	 */
	public static float[] toFloatArray(int... values) {
		float[] vals = null;
		if(values!=null){
			vals = new float[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = (float)values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the double array into a float array.
	 * 
	 * @param values
	 * @return
	 */
	public static float[] toFloatArray(double... values) {
		float[] vals = null;
		if(values!=null){
			vals = new float[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = (float)values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the long array into a float array.
	 * 
	 * @param values
	 * @return
	 */
	public static float[] toFloatArray(long... values) {
		float[] vals = null;
		if(values!=null){
			vals = new float[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = (float)values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Parse and transform the String array into a float array.
	 * Note that exception may be thrown if irregular String exists.
	 * 
	 * @param values
	 * @return
	 */
	public static float[] toFloatArray(String... values) {
		float[] vals = null;
		if(values!=null){
			vals = new float[values.length];
			int ix = 0;
			for(String val:values){
				vals[ix] = Float.parseFloat(val);
				ix++;
			}
		}
		return vals;
	}
	
	/**
	 * Iteratively visit each element in the collection,
	 * transform or parse the element into float value,
	 * and finally save into a float array. The elements 
	 * can be String type or sub-class of Number type.
	 * 
	 * @param values
	 * @return
	 */
	public static float[] toFloatArray(Collection<?> values) {
		float[] vals = null;
		if(values!=null){
			vals = new float[values.size()];
			int ix = 0;
			for(Object val:values){
				if(val instanceof Number){
					vals[ix] = ((Number)val).floatValue();
				}else{
					vals[ix] = Float.parseFloat(val.toString());
				}
				ix++;
			}
		}
		return vals;
	}
	
	/**
	 * Transform the integer array into an Integer object array.
	 * 
	 * @param values
	 * @return
	 */
	public static Integer[] toIntArray(int... values) {
		Integer[] vals = null;
		if(values!=null){
			vals = new Integer[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the Integer object array into an int array.
	 * 
	 * @param values
	 * @return
	 */
	public static int[] toIntArray(Integer... values) {
		int[] vals = null;
		if(values!=null){
			vals = new int[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the float array into an int array.
	 * 
	 * @param values
	 * @return
	 */
	public static int[] toIntArray(float... values) {
		int[] vals = null;
		if(values!=null){
			vals = new int[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = (int) values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the double array into an int array.
	 * 
	 * @param values
	 * @return
	 */
	public static int[] toIntArray(double... values) {
		int[] vals = null;
		if(values!=null){
			vals = new int[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = (int) values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the long array into an int array.
	 * 
	 * @param values
	 * @return
	 */
	public static int[] toIntArray(long... values) {
		int[] vals = null;
		if(values!=null){
			vals = new int[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = (int)values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Parse and transform the String array into an int array.
	 * Note that exception may be thrown if irregular String exists.
	 * 
	 * @param values
	 * @return
	 */
	public static int[] toIntArray(String... values) {
		int[] vals = null;
		if(values!=null){
			vals = new int[values.length];
			int ix = 0;
			for(String val:values){
				vals[ix] = Integer.parseInt(val);
				ix++;
			}
		}
		return vals;
	}
	
	/**
	 * Iteratively visit each element in the collection,
	 * transform or parse the element into int value,
	 * and finally save into an int array. The elements 
	 * can be String type or sub-class of Number type.
	 * 
	 * @param values
	 * @return
	 */
	public static int[] toIntArray(Collection<?> values) {
		int[] vals = null;
		if(values!=null){
			vals = new int[values.size()];
			int ix = 0;
			for(Object val:values){
				if(val instanceof Number){
					vals[ix] = ((Number)val).intValue();
				}else{
					vals[ix] = Integer.parseInt(val.toString());
				}
				ix++;
			}
		}
		return vals;
	}
	
	/**
	 * Transform the long array into a Long object array.
	 * 
	 * @param values
	 * @return
	 */
	public static Long[] toLongArray(long... values) {
		Long[] vals = null;
		if(values!=null){
			vals = new Long[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the Long object array into a long array.
	 * 
	 * @param values
	 * @return
	 */
	public static long[] toLongArray(Long... values) {
		long[] vals = null;
		if(values!=null){
			vals = new long[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the int array into a long array.
	 * 
	 * @param values
	 * @return
	 */
	public static long[] toLongArray(int... values) {
		long[] vals = null;
		if(values!=null){
			vals = new long[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = (long)values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the float array into a long array.
	 * 
	 * @param values
	 * @return
	 */
	public static long[] toLongArray(float... values) {
		long[] vals = null;
		if(values!=null){
			vals = new long[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = (long)values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Transform the double array into a long array.
	 * 
	 * @param values
	 * @return
	 */
	public static long[] toLongArray(double... values) {
		long[] vals = null;
		if(values!=null){
			vals = new long[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = (long)values[i];
			}
		}
		return vals;
	}
	
	/**
	 * Parse and transform the String array into a long array.
	 * Note that exception may be thrown if irregular String exists.
	 * 
	 * @param values
	 * @return
	 */
	public static long[] toLongArray(String... values) {
		long[] vals = null;
		if(values!=null){
			vals = new long[values.length];
			int ix = 0;
			for(String val:values){
				vals[ix] = Long.parseLong(val);
				ix++;
			}
		}
		return vals;
	}
	
	/**
	 * Iteratively visit each element in the collection,
	 * transform or parse the element into long value,
	 * and finally save into a long array. The elements 
	 * can be String type or sub-class of Number type.
	 * 
	 * @param values
	 * @return
	 */
	public static long[] toLongArray(Collection<?> values) {
		long[] vals = null;
		if(values!=null){
			vals = new long[values.size()];
			int ix = 0;
			for(Object val:values){
				if(val instanceof Number){
					vals[ix] = ((Number)val).longValue();
				}else{
					vals[ix] = Long.parseLong(val.toString());
				}
				ix++;
			}
		}
		return vals;
	}
	
	/**
	 * Transform the integer array into a String array.
	 * 
	 * @param values
	 * @return
	 */
	public static String[] toStringArray(int... values) {
		String[] vals = null;
		if(values!=null){
			vals = new String[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = Integer.toString(values[i]);
			}
		}
		return vals;
	}
	
	/**
	 * Transform the long integer array into a String array.
	 * 
	 * @param values
	 * @return
	 */
	public static String[] toStringArray(long... values) {
		String[] vals = null;
		if(values!=null){
			vals = new String[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = Long.toString(values[i]);
			}
		}
		return vals;
	}
	
	/**
	 * Transform the float array into a String array with the highest precision for float.
	 * 
	 * @param values
	 * @return
	 */
	public static String[] toStringArray(float... values) {
		String[] vals = null;
		if(values!=null){
			vals = new String[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = Float.toString(values[i]);
			}
		}
		return vals;
	}
	
	/**
	 * Transform the float array into a String array with the specified precision.
	 * 
	 * @param values
	 * @return
	 */
	public static String[] toStringArray(int precision, float... values) {
		String[] vals = null;
		if(values!=null){
			vals = new String[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = StringUtils.formatDouble(values[i], precision);
			}
		}
		return vals;
	}
	
	/**
	 * Transform the float array into a String array with the highest precision for double.
	 * 
	 * @param values
	 * @return
	 */
	public static String[] toStringArray(double... values) {
		String[] vals = null;
		if(values!=null){
			vals = new String[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = Double.toString(values[i]);
			}
		}
		return vals;
	}
	
	/**
	 * Transform the float array into a String array with the specified precision.
	 * 
	 * @param values
	 * @return
	 */
	public static String[] toStringArray(int precision, double... values) {
		String[] vals = null;
		if(values!=null){
			vals = new String[values.length];
			for(int i=0;i<values.length;i++){
				vals[i] = StringUtils.formatDouble(values[i], precision);
			}
		}
		return vals;
	}
	
	/**
	 * Iteratively visiting each element and transform them into strings.
	 * 
	 * @param values
	 * @return
	 */
	public static String[] toStringArray(Collection<?> values) {
		String[] array = new String[values.size()];
		int ix = 0;
		for(Object val:values){
			array[ix] = val.toString();
			ix++;
		}
		return array;
	}
	
	/**
	 * Add the array's elements into the collection.
	 * Note that duplicate elements may be removed.
	 * 
	 * @param collection
	 * @param values
	 * @return
	 */
	public static Collection<Integer> addAll( Collection<Integer> collection, int... values ) {
		for( int value:values ) {
			collection.add( value );
		}
		return collection;
	}
	
	/**
	 * Add the array's elements into the collection.
	 * Note that duplicate elements may be removed.
	 * 
	 * @param collection
	 * @param values
	 * @return
	 */
	public static Collection<Long> addAll( Collection<Long> collection, long... values ) {
		for( long value:values ) {
			collection.add( value );
		}
		return collection;
	}
	
	/**
	 * Add the array's elements into the collection.
	 * Note that duplicate elements may be removed.
	 * 
	 * @param collection
	 * @param values
	 * @return
	 */
	public static Collection<Float> addAll( Collection<Float> collection, float... values ) {
		for( float value:values ) {
			collection.add( value );
		}
		return collection;
	}
	
	/**
	 * Add the array's elements into the collection.
	 * Note that duplicate elements may be removed.
	 * 
	 * @param collection
	 * @param values
	 * @return
	 */
	public static Collection<Double> addAll( Collection<Double> collection, double... values ) {
		for( double value:values ) {
			collection.add( value );
		}
		return collection;
	}
	
	/**
	 * Add the array's elements into the collection.
	 * Note that duplicate elements may be removed.
	 * 
	 * @param collection
	 * @param values
	 * @return
	 */
	public static Collection<String> addAll( Collection<String> collection, String... values ) {
		for( String value:values ) {
			collection.add( value );
		}
		return collection;
	}
	
	public static ArrayList<Integer> toArrayList( int... values ) {
		return (ArrayList<Integer>) addAll( new ArrayList<Integer>(), values );
	}
	
	public static ArrayList<Long> toArrayList( long... values ) {
		return (ArrayList<Long>) addAll( new ArrayList<Long>(), values );
	}
	
	public static ArrayList<Float> toArrayList( float... values ) {
		return (ArrayList<Float>) addAll( new ArrayList<Float>(), values );
	}
	
	public static ArrayList<Double> toArrayList( double... values ) {
		return (ArrayList<Double>) addAll( new ArrayList<Double>(), values );
	}
	
	public static ArrayList<String> toArrayList( String... values ) {
		return (ArrayList<String>) addAll( new ArrayList<String>(), values );
	}
	
	public static LinkedList<Integer> toLinkedList( int... values ) {
		return (LinkedList<Integer>) addAll( new LinkedList<Integer>(), values );
	}
	
	public static LinkedList<Long> toLinkedList( long... values ) {
		return (LinkedList<Long>) addAll( new LinkedList<Long>(), values );
	}
	
	public static LinkedList<Float> toLinkedList( float... values ) {
		return (LinkedList<Float>) addAll( new LinkedList<Float>(), values );
	}
	
	public static LinkedList<Double> toLinkedList( double... values ) {
		return (LinkedList<Double>) addAll( new LinkedList<Double>(), values );
	}
	
	public static LinkedList<String> toLinkedList(String... values) {
		return (LinkedList<String>) addAll( new LinkedList<String>(), values );
	}
	
	public static TreeSet<Integer> toTreeSet(int... values) {
		return (TreeSet<Integer>) addAll( new TreeSet<Integer>(), values );
	}
	
	public static TreeSet<Long> toTreeSet(long... values) {
		return (TreeSet<Long>) addAll( new TreeSet<Long>(), values );
	}
	
	public static TreeSet<Float> toTreeSet(float... values) {
		return (TreeSet<Float>) addAll( new TreeSet<Float>(), values );
	}
	
	public static TreeSet<Double> toTreeSet(double... values) {
		return (TreeSet<Double>) addAll( new TreeSet<Double>(), values );
	}
	
	public static TreeSet<String> toTreeSet(String... values) {
		return (TreeSet<String>) addAll( new TreeSet<String>(), values );
	}
	
	public static HashSet<Integer> toHashSet(int... values) {
		return (HashSet<Integer>) addAll( new HashSet<Integer>(), values );
	}
	
	public static HashSet<Long> toHashSet(long... values) {
		return (HashSet<Long>) addAll( new HashSet<Long>(), values );
	}
	
	public static HashSet<Float> toHashSet(float... values) {
		return (HashSet<Float>) addAll( new HashSet<Float>(), values );
	}
	
	public static HashSet<Double> toHashSet(double... values) {
		return (HashSet<Double>) addAll( new HashSet<Double>(), values );
	}
	
	public static HashSet<String> toHashSet(String... values) {
		return (HashSet<String>) addAll( new HashSet<String>(), values );
	}
	
}
