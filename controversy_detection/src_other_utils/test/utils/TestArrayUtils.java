package test.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


import org.junit.Test;
import org.junit.Assert;
import org.junit.BeforeClass;

import utils.ArrayUtils;
import utils.Comparators;

/**
 * Test for:
 * 1. utils.ArrayUtils
 * 2. utils.Comparators
 * 
 * @author Jiepu Jiang
 * @version Feb 28, 2013
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class TestArrayUtils {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	@Test
	public void testCreateIntIntInt() {
		// tested in TestStringUtils
	}
	
	@Test
	public void testCreateIntInt() {
		// tested in TestStringUtils
	}
	
	@Test
	public void testCreateDoubleDoubleDouble() {
		// tested in TestStringUtils
	}
	
	static int[] array_int = new int[] { 1, 2, 3, 4, 5 };
	static long[] array_long = new long[] { 1l, 2l, 3l, 4l, 5l };
	static float[] array_float = new float[] { 1f, 2f, 3f, 4f, 5f };
	static double[] array_double = new double[] { 1.0, 2.0, 3.0, 4.0, 5.0 };
	static String[] array_string_int = new String[] { "1", "2", "3", "4", "5" };
	static String[] array_string_double = new String[] { "1.0", "2.0", "3.0", "4.0", "5.0" };
	static String[] array_string_double_prec = new String[] { "1.0000", "2.0000", "3.0000", "4.0000", "5.0000" };
	static String[] array_string_collection = new String[] { "1", "2", "3.0", "4.0", "5" };
	static Collection collection = new ArrayList();
	static {
		collection.add( 1 );
		collection.add( 2l );
		collection.add( 3f );
		collection.add( 4.0 );
		collection.add( "5" );
	}
	
	@Test
	public void testComparators() {
		List<Integer> list = ArrayUtils.toArrayList( 2, 1, 3 ); 
		Collections.sort( list, Comparators.IntegerAsc );
		Assert.assertArrayEquals( new int[]{1, 2, 3}, ArrayUtils.toIntArray(list) );
		Collections.sort( list, Comparators.IntegerDesc );
		Assert.assertArrayEquals( new int[]{3, 2, 1}, ArrayUtils.toIntArray(list) );
	}
	
	@Test
	public void testTransformationArrayCollection() {
		Assert.assertArrayEquals( array_int, ArrayUtils.toIntArray( ArrayUtils.toArrayList( array_int ).toArray( new Integer[array_int.length] ) ) );
	}
	
	@Test
	public void testToDoubleArrayIntArray() {
		Assert.assertArrayEquals( array_double, ArrayUtils.toDoubleArray(array_int), 0 );
	}
	
	@Test
	public void testToDoubleArrayFloatArray() {
		Assert.assertArrayEquals( array_double, ArrayUtils.toDoubleArray(array_float), 0 );
	}
	
	@Test
	public void testToDoubleArrayLongArray() {
		Assert.assertArrayEquals( array_double, ArrayUtils.toDoubleArray(array_long), 0 );
	}
	
	@Test
	public void testToDoubleArrayStringArray() {
		Assert.assertArrayEquals( array_double, ArrayUtils.toDoubleArray(array_string_int), 0 );
	}
	
	@Test
	public void testToDoubleArrayCollectionOfQ() {
		Assert.assertArrayEquals( array_double, ArrayUtils.toDoubleArray(collection), 0 );
	}
	
	@Test
	public void testToFloatArrayIntArray() {
		Assert.assertArrayEquals( array_float, ArrayUtils.toFloatArray(array_int), 0 );
	}
	
	@Test
	public void testToFloatArrayDoubleArray() {
		Assert.assertArrayEquals( array_float, ArrayUtils.toFloatArray(array_double), 0 );
	}
	
	@Test
	public void testToFloatArrayLongArray() {
		Assert.assertArrayEquals( array_float, ArrayUtils.toFloatArray(array_long), 0 );
	}
	
	@Test
	public void testToFloatArrayStringArray() {
		Assert.assertArrayEquals( array_float, ArrayUtils.toFloatArray(array_string_int), 0 );
	}
	
	@Test
	public void testToFloatArrayCollectionOfQ() {
		Assert.assertArrayEquals( array_float, ArrayUtils.toFloatArray(collection), 0 );
	}
	
	@Test
	public void testToIntegerArrayFloatArray() {
		Assert.assertArrayEquals( array_int, ArrayUtils.toIntArray(array_float) );
	}
	
	@Test
	public void testToIntegerArrayDoubleArray() {
		Assert.assertArrayEquals( array_int, ArrayUtils.toIntArray(array_double) );
	}
	
	@Test
	public void testToIntegerArrayLongArray() {
		Assert.assertArrayEquals( array_int, ArrayUtils.toIntArray(array_long) );
	}
	
	@Test
	public void testToIntegerArrayStringArray() {
		Assert.assertArrayEquals( array_int, ArrayUtils.toIntArray(array_string_int) );
	}
	
	@Test
	public void testToIntegerArrayCollectionOfQ() {
		Assert.assertArrayEquals( array_int, ArrayUtils.toIntArray(collection) );
	}
	
	@Test
	public void testToLongArrayIntArray() {
		Assert.assertArrayEquals( array_long, ArrayUtils.toLongArray(array_int) );
	}
	
	@Test
	public void testToLongArrayFloatArray() {
		Assert.assertArrayEquals( array_long, ArrayUtils.toLongArray(array_float) );
	}
	
	@Test
	public void testToLongArrayDoubleArray() {
		Assert.assertArrayEquals( array_long, ArrayUtils.toLongArray(array_double) );
	}
	
	@Test
	public void testToLongArrayStringArray() {
		Assert.assertArrayEquals( array_long, ArrayUtils.toLongArray(array_string_int) );
	}
	
	@Test
	public void testToLongArrayCollectionOfQ() {
		Assert.assertArrayEquals( array_long, ArrayUtils.toLongArray(collection) );
	}
	
	@Test
	public void testToStringArrayIntArray() {
		Assert.assertArrayEquals( array_string_int, ArrayUtils.toStringArray(array_int) );
	}
	
	@Test
	public void testToStringArrayLongArray() {
		Assert.assertArrayEquals( array_string_int, ArrayUtils.toStringArray(array_long) );
	}
	
	@Test
	public void testToStringArrayFloatArray() {
		Assert.assertArrayEquals( array_string_double, ArrayUtils.toStringArray(array_float) );
	}
	
	@Test
	public void testToStringArrayFloatArrayInt() {
		Assert.assertArrayEquals( array_string_double_prec, ArrayUtils.toStringArray(4, array_float) );
	}
	
	@Test
	public void testToStringArrayDoubleArray() {
		Assert.assertArrayEquals( array_string_double, ArrayUtils.toStringArray(array_double) );
	}
	
	@Test
	public void testToStringArrayDoubleArrayInt() {
		Assert.assertArrayEquals( array_string_double_prec, ArrayUtils.toStringArray(4, array_double) );
	}
	
	@Test
	public void testToStringArrayCollectionOfQ() {
		Assert.assertArrayEquals( array_string_collection, ArrayUtils.toStringArray(collection) );
	}
	
}
