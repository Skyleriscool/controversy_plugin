package test.utils;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.BeforeClass;

import utils.ArrayUtils;
import utils.MathUtils;


public class TestMathUtils {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	@Test
	public void testJaccard() {
		int[] set1 = new int[] { 1, 2, 3 };
		int[] set2 = new int[] { 3, 4, 5 };
		double expected = 0.2;
		double actual = MathUtils.jaccard( ArrayUtils.toArrayList(set1), ArrayUtils.toArrayList(set2) );
		Assert.assertEquals( expected, actual );
		
	}
	
}
