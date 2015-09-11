package test.utils;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.BeforeClass;

import utils.KVPair;


public class TestKVPair {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}
	
	static KVPair data = new KVPair();
	static {
		data.put( "int", 1 );
		data.put( "long", 2l );
		data.put( "float", 3.0f );
		data.put( "double", 4.0 );
		data.put( "string", "5" );
		data.put( "int[]", new int[] { 1, 2 } );
		data.put( "long[]", new long[] { 2l, 3l } );
		data.put( "float[]", new float[] { 3.0f, 4.0f } );
		data.put( "double[]", new double[] { 4.0, 5.0 } );
		data.put( "object", new TreeSet<String>() );
	}
	
	@Test
	public void testGetIntegerString() {
		Assert.assertEquals( new Integer(1), data.getInteger("int") );
	}
	
	@Test
	public void testGetIntegerStringInteger() {
		Assert.assertEquals( new Integer(0), data.getInteger("non-exist", 0) );
	}
	
	@Test
	public void testGetLongString() {
		Assert.assertEquals( new Long(2l), data.getLong("long") );
	}
	
	@Test
	public void testGetLongStringLong() {
		Assert.assertEquals( new Long(0l), data.getLong("non-exist", 0l) );
	}
	
	@Test
	public void testGetFloatString() {
		Assert.assertEquals( new Float(3.0f), data.getFloat("float") );
	}
	
	@Test
	public void testGetFloatStringFloat() {
		Assert.assertEquals( new Float(0.0f), data.getFloat("non-exist", 0.0f) );
	}
	
	@Test
	public void testGetDoubleString() {
		Assert.assertEquals( new Double(4.0), data.getDouble("double") );
	}
	
	@Test
	public void testGetDoubleStringDouble() {
		Assert.assertEquals( new Double(0.0), data.getDouble("non-exist", 0.0) );
	}
	
	@Test
	public void testGetDoubleKVPairArrayString() {
		assertArrayEquals( new double[] { 4.0, 4.0 }, KVPair.getDouble( new KVPair[] { data, data }, "double" ), 0 );
	}
	
	@Test
	public void testGetStringString() {
		Assert.assertEquals( "5", data.getString("string") );
	}
	
	@Test
	public void testGetStringStringString() {
		Assert.assertEquals( "0", data.getString("non-exist", "0") );
	}
	
	@Test
	public void testGetIntArrayString() {
		assertArrayEquals( new int[]{1, 2}, data.getIntArray("int[]") );
	}
	
	@Test
	public void testGetIntArrayStringIntArray() {
		assertArrayEquals( new int[0], data.getIntArray("non-exist", new int[0]) );
	}
	
	@Test
	public void testGetLongArrayString() {
		assertArrayEquals( new long[]{2l, 3l}, data.getLongArray("long[]") );
	}
	
	@Test
	public void testGetLongArrayStringLongArray() {
		assertArrayEquals( new long[0], data.getLongArray("non-exist", new long[0]) );
	}
	
	@Test
	public void testGetFloatArrayString() {
		assertArrayEquals( new float[]{3.0f, 4.0f}, data.getFloatArray("float[]"), 0 );
	}
	
	@Test
	public void testGetFloatArrayStringFloatArray() {
		assertArrayEquals( new float[0], data.getFloatArray("non-exist", new float[0]), 0 );
	}
	
	@Test
	public void testGetDoubleArrayString() {
		assertArrayEquals( new double[]{4.0f, 5.0f}, data.getDoubleArray("double[]"), 0 );
	}
	
	@Test
	public void testGetDoubleArrayStringDoubleArray() {
		assertArrayEquals( new double[0], data.getDoubleArray("non-exist", new double[0]), 0 );
	}
	
	@Test
	public void testGetObjectString() {
		Assert.assertEquals( new TreeSet<String>(), data.getObject("object") );
	}
	
	@Test
	public void testGetObjectStringObject() {
		Assert.assertEquals( new ArrayList<String>(), data.getObject("non-exist", new ArrayList<String>()) );
	}
	
	@Test
	public void testToStringStringInt() throws UnsupportedEncodingException {
		Assert.assertEquals( "key:I:1", KVPair.toString("key", 1) );
	}
	
	@Test
	public void testToStringStringLong() throws UnsupportedEncodingException {
		Assert.assertEquals( "key:L:2", KVPair.toString("key", 2l) );
	}
	
	@Test
	public void testToStringStringFloat() throws UnsupportedEncodingException {
		Assert.assertEquals( "key:F:3.0", KVPair.toString("key", 3.0f) );
	}
	
	@Test
	public void testToStringStringDouble() throws UnsupportedEncodingException {
		Assert.assertEquals( "key:D:4.0", KVPair.toString("key", 4.0) );
	}
	
	@Test
	public void testToStringStringString() throws UnsupportedEncodingException {
		Assert.assertEquals( "key:S:5", KVPair.toString("key", "5") );
	}
	
	@Test
	public void testToStringStringIntArray() throws UnsupportedEncodingException {
		Assert.assertEquals( "key:Is:2,1,2", KVPair.toString("key", new int[]{1,2}) );
	}
	
	@Test
	public void testToStringStringLongArray() throws UnsupportedEncodingException {
		Assert.assertEquals( "key:Ls:2,1,2", KVPair.toString("key", new long[]{1l,2l}) );
	}
	
	@Test
	public void testToStringStringFloatArray() throws UnsupportedEncodingException {
		Assert.assertEquals( "key:Fs:2,1.0,2.0", KVPair.toString("key", new float[]{1.0f,2.0f}) );
	}
	
	@Test
	public void testToStringStringDoubleArray() throws UnsupportedEncodingException {
		Assert.assertEquals( "key:Ds:2,1.0,2.0", KVPair.toString("key", new double[]{1.0,2.0}) );
	}
	
	@Test
	public void testToStringStringObject() throws UnsupportedEncodingException {
		Assert.assertNull( KVPair.toString("key", new TreeSet<Integer>()) );
	}
	
	@Test
	public void testToString() {
		String expected = "double:D:4.0;double%5B%5D:Ds:2,4.0,5.0;float:F:3.0;float%5B%5D:Fs:2,3.0,4.0;int:I:1;int%5B%5D:Is:2,1,2;long:L:2;long%5B%5D:Ls:2,2,3;string:S:5";
		String actual = data.toString();
		Assert.assertEquals( expected, actual );
	}
	
	@Test
	public void testParse() throws UnsupportedEncodingException {
		KVPair expected = data;
		KVPair actual = KVPair.parse("double:D:4.0;double%5B%5D:Ds:2,4.0,5.0;float:F:3.0;float%5B%5D:Fs:2,3.0,4.0;int:I:1;int%5B%5D:Is:2,1,2;long:L:2;long%5B%5D:Ls:2,2,3;string:S:5");
		Assert.assertEquals( expected.toString(), actual.toString() );
	}
	
}
