package test.utils;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.BeforeClass;

import utils.ByteUtils;


public class TestByteUtils {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	@Test
	public void testToBytesLong() {
		byte[] bytes = ByteUtils.toBytes(123l);
		long value = ByteUtils.toLong(bytes);
		Assert.assertEquals( 123l, value );
	}
	
	@Test
	public void testToBytesLongByteArrayInt() {
		byte[] bytes = new byte[12];
		ByteUtils.toBytes(123l, bytes, 4);
		long value = ByteUtils.toLong(bytes, 4);
		Assert.assertEquals( 123l, value );
	}
	
	@Test
	public void testToLongByteArrayInt() {
		// tested in testToBytesLong
	}
	
	@Test
	public void testToLongByteArray() {
		// tested in testToBytesLongByteArrayInt
	}
	
}
