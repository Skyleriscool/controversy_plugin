package test.utils;

import static org.junit.Assert.*;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.BeforeClass;

import utils.StringUtils;


/**
 * Testing edu.pitt.sis.iris.utils.StringUtils.
 * 
 * @author Jiepu Jiang
 * @version Feb 27, 2013
 */
public class TestStringUtils {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	@Test
	public void testFormatLocaleStringObjectArray() {
		String actual = StringUtils.format( Locale.US, "%-5.2f", 0.23 );
		String expected = "0.23 ";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testFormatStringObjectArray() {
		String actual = StringUtils.format( "%-5.2f", 0.23 );
		String expected = "0.23 ";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testFormatIntegerIntIntStringBoolean() {
		String actual = StringUtils.formatInteger( 312919, 10, "R", true );
		String expected = "   +312919";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testFormatIntegerIntIntString() {
		String actual = StringUtils.formatInteger( 312919, 10, "R" );
		String expected = "    312919";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testFormatIntegerIntInt() {
		String actual = StringUtils.formatInteger( 312919, 10 );
		String expected = "312919    ";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testFormatDoubleDoubleBooleanIntIntStringBoolean() {
		String actual = StringUtils.formatDouble( 0.43521352, true, 10, 4, "R", true );
		String expected = " +43.5214%";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testFormatDoubleDoubleIntIntStringBoolean() {
		String actual = StringUtils.formatDouble( 0.43521352, 10, 4, "R", true );
		String expected = "   +0.4352";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testFormatDoubleDoubleIntIntString() {
		String actual = StringUtils.formatDouble( 0.43521352, 10, 4, "R" );
		String expected = "    0.4352";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testFormatDoubleDoubleIntInt() {
		String actual = StringUtils.formatDouble( 0.43521352, 10, 4 );
		String expected = "0.4352    ";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testFormatDoubleDoubleInt() {
		String actual = StringUtils.formatDouble( 0.43521352, 4 );
		String expected = "0.4352";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testFormatPercentageDoubleIntIntStringBoolean() {
		String actual = StringUtils.formatPercentage( 0.43521352, 10, 3, "R", true );
		String expected = "  +43.521%";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testFormatPercentageDoubleIntIntString() {
		String actual = StringUtils.formatPercentage( 0.43521352, 10, 3, "R" );
		String expected = "   43.521%";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testFormatPercentageDoubleIntInt() {
		String actual = StringUtils.formatPercentage( 0.43521352, 10, 3 );
		String expected = "43.521%   ";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testFormatPercentageDoubleInt() {
		String actual = StringUtils.formatPercentage( 0.43521352, 3 );
		String expected = "43.521%";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testConcatStringArray() {
		String[] tokens = new String[] { "1", "2", "3" };
		String actual = StringUtils.concat( tokens );
		String expected = "1 2 3";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testConcatStringArrayString() {
		String[] tokens = new String[] { "1", "2", "3" };
		String actual = StringUtils.concat( tokens, "-" );
		String expected = "1-2-3";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testParseToIntArray() {
		int[] actuals_1 = StringUtils.parseToIntArray("1-5,1");
		int[] expecteds_1 = new int[] { 1, 2, 3, 4 };
		int[] actuals_2 = StringUtils.parseToIntArray("1-9,2");
		int[] expecteds_2 = new int[] { 1, 3, 5, 7 };
		int[] actuals_3 = StringUtils.parseToIntArray("1-10,2");
		int[] expecteds_3 = new int[] { 1, 3, 5, 7, 9 };
		int[] actuals_4 = StringUtils.parseToIntArray("1-9,3");
		int[] expecteds_4 = new int[] { 1, 4, 7 };
		int[] actuals_5 = StringUtils.parseToIntArray("1-10,3");
		int[] expecteds_5 = new int[] { 1, 4, 7 };
		int[] actuals_6 = StringUtils.parseToIntArray("1-11,3");
		int[] expecteds_6 = new int[] { 1, 4, 7, 10 };
		int[] actuals_7 = StringUtils.parseToIntArray("1-2,1");
		int[] expecteds_7 = new int[] { 1 };
		int[] actuals_8 = StringUtils.parseToIntArray("1-10,10");
		int[] expecteds_8 = new int[] { 1 };
		assertArrayEquals( expecteds_1, actuals_1 );
		assertArrayEquals( expecteds_2, actuals_2 );
		assertArrayEquals( expecteds_3, actuals_3 );
		assertArrayEquals( expecteds_4, actuals_4 );
		assertArrayEquals( expecteds_5, actuals_5 );
		assertArrayEquals( expecteds_6, actuals_6 );
		assertArrayEquals( expecteds_7, actuals_7 );
		assertArrayEquals( expecteds_8, actuals_8 );
	}
	
	@Test
	public void testParseToDoubleArray() {
		double[] actuals_1 = StringUtils.parseToDoubleArray("0-0.05,0.01");
		double[] expecteds_1 = new double[] { 0, 0.01, 0.02, 0.03, 0.04 };
		double[] actuals_2 = StringUtils.parseToDoubleArray("0-0.051,0.01");
		double[] expecteds_2 = new double[] { 0, 0.01, 0.02, 0.03, 0.04, 0.05 };
		double[] actuals_3 = StringUtils.parseToDoubleArray("0-0.05,0.02");
		double[] expecteds_3 = new double[] { 0, 0.02, 0.04 };
		double[] actuals_4 = StringUtils.parseToDoubleArray("0-0.06,0.02");
		double[] expecteds_4 = new double[] { 0, 0.02, 0.04 };
		double[] actuals_5 = StringUtils.parseToDoubleArray("0-0.061,0.02");
		double[] expecteds_5 = new double[] { 0, 0.02, 0.04, 0.06 };
		double[] actuals_6 = StringUtils.parseToDoubleArray("0-0.06,0.1");
		double[] expecteds_6 = new double[] { 0 };
		double[] actuals_7 = StringUtils.parseToDoubleArray("0-0.8,0.000001");
		double[] expecteds_7 = new double[800000];
		for(int ix=0;ix<800000;ix++){
			expecteds_7[ix] = 1.0*ix/1000000;
		}
		assertArrayEquals( expecteds_1, actuals_1, 0 );
		assertArrayEquals( expecteds_2, actuals_2, 0 );
		assertArrayEquals( expecteds_3, actuals_3, 0 );
		assertArrayEquals( expecteds_4, actuals_4, 0 );
		assertArrayEquals( expecteds_5, actuals_5, 0 );
		assertArrayEquals( expecteds_6, actuals_6, 0 );
		assertArrayEquals( expecteds_7, actuals_7, 0 );
	}
	
	@Test
	public void testEditDistance() {
		// no error
		int actual_1 = StringUtils.editDistance( "abc", "abc" );
		int expected_1 = 0;
		// replace
		int actual_2 = StringUtils.editDistance( "acc", "abc" );
		int expected_2 = 1;
		// insertion
		int actual_3 = StringUtils.editDistance( "abc", "abcd" );
		int expected_3 = 1;
		// deletion
		int actual_4 = StringUtils.editDistance( "abc", "ab" );
		int expected_4 = 1;
		// transpose
		int actual_5 = StringUtils.editDistance( "abc", "acb" );
		int expected_5 = 2;
		Assert.assertEquals( expected_1, actual_1 );
		Assert.assertEquals( expected_2, actual_2 );
		Assert.assertEquals( expected_3, actual_3 );
		Assert.assertEquals( expected_4, actual_4 );
		Assert.assertEquals( expected_5, actual_5 );
	}
	
	@Test
	public void testDecodeWebcontent() {
		String actual = StringUtils.decodeWebcontent("&lt;h&gt;");
		String expected = "<h>";
		Assert.assertEquals(expected, actual);
	}
	
}
