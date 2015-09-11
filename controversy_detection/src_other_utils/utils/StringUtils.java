package utils;

import java.util.*;
import java.util.regex.*;

import org.apache.commons.lang3.*;
import org.apache.commons.lang3.math.*;

/**
 * Utilities related to String and textual operation.
 * 
 * @author Jiepu Jiang
 * @version Feb 26, 2013
 */
public class StringUtils {
	
	public static void main( String[] args ) {
		try {
			
			System.out.println( StringUtils.formatIntegerZeroPadded( 1, 6 ) );
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * (I kind of believe it is) a comparatively easier way to format strings.
	 * This is equivalent to using a formatter's format.
	 * 
	 * @param locale
	 * @param pattern
	 * @param values
	 * @return
	 */
	public static String format( Locale locale, String pattern, Object... values ) {
		StringBuilder sb = new StringBuilder();
		Formatter fmt = new Formatter( sb, locale );
		fmt.format( pattern, values );
		fmt.close();
		return sb.toString();
	}
	
	/**
	 * (I kind of believe it is) a comparatively easier way to format strings.
	 * This is equivalent to using a formatter's format with the default US
	 * locale.
	 * 
	 * @param pattern
	 * @param values
	 * @return
	 */
	public static String format( String pattern, Object... values ) {
		return format( Locale.US, pattern, values );
	}
	
	/**
	 * Format the integer as a zero-padded string with the specified width.
	 * 
	 * @param value
	 *            An integer value
	 * @param width
	 *            Minimum width of the string
	 * @return A formatted string for the integer
	 */
	public static String formatIntegerZeroPadded( int value, int width ) {
		String fmt_str = "%0" + ( width > 0 ? Integer.toString( width ) : "1" ) + "d";
		return format( fmt_str, value );
	}
	
	/**
	 * Format the integer as a string.
	 * 
	 * @param value
	 *            An integer value
	 * @param width
	 *            Minimum width of the string
	 * @param alignment
	 *            Alignment setting (case insensitive): "left" or "L" for
	 *            alignment left; "right" or "R" for alignment right
	 * @param sign
	 *            If true, the result will always include a sign, e.g. +1234
	 * @return A formatted string for the integer
	 */
	public static String formatInteger( int value, int width, String alignment, boolean sign ) {
		
		String str_flag = sign ? "+" : "";
		if ( alignment.equalsIgnoreCase( "L" ) || alignment.equalsIgnoreCase( "left" ) ) {
			str_flag = str_flag + "-";
		}
		String fmt_str = "%" + str_flag + ( width > 0 ? Integer.toString( width ) : "1" ) + "d";
		
		return format( fmt_str, value );
		
	}
	
	/**
	 * Format the integer as a string.
	 * 
	 * @param value
	 *            An integer value
	 * @param width
	 *            Minimum width of the string
	 * @param alignment
	 *            Alignment setting (case insensitive): "left" or "L" for
	 *            alignment left; "right" or "R" for alignment right
	 * @return A formatted string for the integer
	 */
	public static String formatInteger( int value, int width, String alignment ) {
		return formatInteger( value, width, alignment, false );
	}
	
	/**
	 * Format the integer as a string. By default, the text will be aigned left.
	 * 
	 * @param value
	 *            An integer value
	 * @param width
	 *            Minimum width of the string
	 * @return A formatted string for the integer
	 */
	public static String formatInteger( int value, int width ) {
		return formatInteger( value, width, "L", false );
	}
	
	/**
	 * Format the double as a string %m.nf: m is $width; n is $precision. The
	 * text can be aligned left or right.
	 * 
	 * @param value
	 *            A double value
	 * @param asPercentage
	 *            Format as a percentage
	 * @param width
	 *            Minimum width of the string
	 * @param precision
	 *            Length of the fraction part
	 * @param alignment
	 *            Alignment setting (case insensitive): "left" or "L" for
	 *            alignment left; "right" or "R" for alignment right
	 * @param sign
	 *            If true, the result will always include a sign, e.g. +0.3281
	 * @return A formatted string for the double
	 */
	public static String formatDouble( double value, boolean asPercentage, int width, int precision, String alignment, boolean sign ) {
		
		if ( asPercentage ) {
			value = value * 100;
			width = width - 1; // leave one character for "%"
		}
		
		String str_flag = sign ? "+" : "";
		if ( alignment.equalsIgnoreCase( "L" ) || alignment.equalsIgnoreCase( "left" ) ) {
			str_flag = str_flag + "-";
		}
		String fmt_str = "%" + str_flag + ( width > 0 ? Integer.toString( width ) : "1" ) + "." + precision + "f" + ( asPercentage ? "%%" : "" );
		
		String output = format( fmt_str, value );
		
		if ( asPercentage ) {
			if ( alignment.equalsIgnoreCase( "L" ) || alignment.equalsIgnoreCase( "left" ) ) {
				output = output.replaceAll( "([^\\s]+)(\\s*)(%)", "$1$3$2" );
			}
		}
		
		return output;
		
	}
	
	/**
	 * Format the double as a string %m.nf: m is $width; n is $precision. The
	 * text can be aligned left or right.
	 * 
	 * @param value
	 *            A double value
	 * @param width
	 *            Minimum width of the string
	 * @param precision
	 *            Length of the fraction part
	 * @param alignment
	 *            Alignment setting (case insensitive): "left" or "L" for
	 *            alignment left; "right" or "R" for alignment right
	 * @param sign
	 *            If true, the result will always include a sign, e.g. +0.3281
	 * @return Formatted string for the double
	 */
	public static String formatDouble( double value, int width, int precision, String alignment, boolean sign ) {
		return formatDouble( value, false, width, precision, alignment, sign );
	}
	
	/**
	 * Format the double as a string %m.nf: m is $width; n is $precision. The
	 * text can be aligned left or right.
	 * 
	 * @param value
	 *            A double value
	 * @param width
	 *            Minimum width of the string
	 * @param precision
	 *            Length of the fraction part
	 * @param alignment
	 *            Alignment setting (case insensitive): "left" or "L" for
	 *            alignment left; "right" or "R" for alignment right
	 * @return Formatted string for the double
	 */
	public static String formatDouble( double value, int width, int precision, String alignment ) {
		return formatDouble( value, width, precision, alignment, false );
	}
	
	/**
	 * Format the double as a string %m.nf: m is $width; n is $precision. The
	 * text will be aligned left.
	 * 
	 * @param value
	 *            A double value
	 * @param width
	 *            Minimum width of the string
	 * @param precision
	 *            Length of the fraction part
	 * @return Formatted string for the double
	 */
	public static String formatDouble( double value, int width, int precision ) {
		return formatDouble( value, width, precision, "L" );
	}
	
	/**
	 * Format the double as a string %m.nf: m = 1; n is $precision. The text
	 * will be aligned left.
	 * 
	 * @param value
	 *            A double value
	 * @param precision
	 *            Length of the fraction part
	 * @return Formatted string for the double
	 */
	public static String formatDouble( double value, int precision ) {
		return formatDouble( value, 1, precision, "L" );
	}
	
	/**
	 * Format the double as a percentage string.
	 * 
	 * @param value
	 *            A double value
	 * @param width
	 *            Minimum width of the string
	 * @param precision
	 *            Length of the fraction part
	 * @param alignment
	 *            Alignment setting (case insensitive): "left" or "L" for
	 *            alignment left; "right" or "R" for alignment right
	 * @param sign
	 *            If true, the result will always include a sign, e.g. +0.3281
	 * @return Formatted string for the double
	 */
	public static String formatPercentage( double value, int width, int precision, String alignment, boolean sign ) {
		return formatDouble( value, true, width, precision, alignment, sign );
	}
	
	/**
	 * Format the double as a percentage string.
	 * 
	 * @param value
	 *            A double value
	 * @param width
	 *            Minimum width of the string
	 * @param precision
	 *            Length of the fraction part
	 * @param alignment
	 *            Alignment setting (case insensitive): "left" or "L" for
	 *            alignment left; "right" or "R" for alignment right
	 * @return Formatted string for the double
	 */
	public static String formatPercentage( double value, int width, int precision, String alignment ) {
		return formatPercentage( value, width, precision, alignment, false );
	}
	
	/**
	 * Format the double as a percentage string.
	 * 
	 * @param value
	 *            A double value
	 * @param width
	 *            Minimum width of the string
	 * @param precision
	 *            Length of the fraction part
	 * @return Formatted string for the double
	 */
	public static String formatPercentage( double value, int width, int precision ) {
		return formatPercentage( value, width, precision, "L" );
	}
	
	/**
	 * Format the double as a percentage string.
	 * 
	 * @param value
	 *            A double value
	 * @param precision
	 *            Length of the fraction part
	 * @return Formatted string for the double
	 */
	public static String formatPercentage( double value, int precision ) {
		return formatPercentage( value, 1, precision, "L" );
	}
	
	/**
	 * Format the time as the format of yyyy-mm-dd hh:mm:ss.xxx
	 * 
	 * @param time
	 * @return
	 */
	public static String formatTime( Object time ) {
		StringBuilder sb = new StringBuilder();
		Formatter fmt = new Formatter( sb, Locale.US );
		fmt.format( "%1$tY-%1$tm-%1$te %1$tH:%1$tM:%1$tS.%1$tL", time );
		fmt.close();
		return sb.toString();
	}
	
	/**
	 * Sequentially concat a series of tokens into one string with " " as the
	 * delimiter.
	 * 
	 * @param tokens
	 * @return
	 */
	public static String concat( String[] tokens ) {
		return concat( tokens, " " );
	}
	
	/**
	 * Sequentially concat a series of tokens into one string with the specified
	 * delimiter.
	 * 
	 * @param tokens
	 * @param delimiter
	 * @return
	 */
	public static String concat( String[] tokens, String delimiter ) {
		StringBuilder sb = new StringBuilder();
		if ( tokens.length > 0 ) {
			sb.append( tokens[0] );
		}
		for ( int ix = 1; ix < tokens.length; ix++ ) {
			sb.append( delimiter );
			sb.append( tokens[ix] );
		}
		return sb.toString();
	}
	
	/**
	 * <p>
	 * Parse a parameter string into an integer array. The format of the
	 * parameter string is:
	 * 
	 * <pre>
	 * [$bg]-[$ed],[$step]
	 * </pre>
	 * 
	 * It returns an array with the element value ranging from $bg (include) to
	 * $ed (exclude), with the specified step value.
	 * </p>
	 * <p>
	 * For example: 0-100,1 will generate an integer array {0, 1, ... , 99}.
	 * </p>
	 * 
	 * @param input
	 * @return
	 */
	public static int[] parseToIntArray( String input ) {
		Pattern p = Pattern.compile( "(.+?)\\-(.+?),(.+)" );
		Matcher m = p.matcher( input );
		int[] vals = null;
		if ( m.matches() ) {
			int bg = Integer.parseInt( m.group( 1 ) );
			int ed = Integer.parseInt( m.group( 2 ) );
			int step = Integer.parseInt( m.group( 3 ) );
			vals = ArrayUtils.createIntArray( bg, ed, step );
		}
		return vals;
	}
	
	/**
	 * <p>
	 * Parse a parameter string into a double array. The format of the parameter
	 * string is:
	 * 
	 * <pre>
	 * [$bg]-[$ed],[$step]
	 * </pre>
	 * 
	 * It returns an array with the element value ranging from $bg (include) to
	 * $ed (exclude), with the specified step value.
	 * </p>
	 * <p>
	 * For example: 0-1,0.01 will generate an integer array {0, 0.01, 0.02, ...
	 * , 0.99}.
	 * </p>
	 * 
	 * @param input
	 * @return
	 */
	public static double[] parseToDoubleArray( String input ) {
		Pattern p = Pattern.compile( "(.+?)\\-(.+?),(.+)" );
		Matcher m = p.matcher( input );
		if ( m.matches() ) {
			double bg = Double.parseDouble( m.group( 1 ) );
			double ed = Double.parseDouble( m.group( 2 ) );
			double step = Double.parseDouble( m.group( 3 ) );
			return ArrayUtils.createDoubleArray( bg, ed, step );
		}
		return null;
	}
	
	/**
	 * <p>
	 * Calculate the edit distance between two strings. I just copy & paste this
	 * implementation from this web. Read the definition of edit distance from:
	 * http://en.wikipedia.org/wiki/Edit_distance. In this implementation, the
	 * allowed operation includes replace, insertion, and deletion, but NOT
	 * transpose.
	 * </p>
	 * <p>
	 * For example, the edit distance for the following pairs are:
	 * <ul>
	 * <li>"abc" and "acc" (replace): 1</li>
	 * <li>"abc" and "abcd" (insertion): 1</li>
	 * <li>"abc" and "ab" (deletion): 1</li>
	 * <li>"abc" and "acb" (transpose): 2</li>
	 * <ul>
	 * </p>
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static int editDistance( String str1, String str2 ) {
		
		int d[][]; // matrix
		int n; // length of s
		int m; // length of t
		int i; // iterates through s
		int j; // iterates through t
		char s_i; // ith character of s
		char t_j; // jth character of t
		int cost; // cost
		
		// Step 1
		n = str1.length();
		m = str2.length();
		if ( n == 0 ) {
			return m;
		}
		if ( m == 0 ) {
			return n;
		}
		d = new int[n + 1][m + 1];
		// Step 2
		for ( i = 0; i <= n; i++ ) {
			d[i][0] = i;
		}
		for ( j = 0; j <= m; j++ ) {
			d[0][j] = j;
		}
		// Step 3
		for ( i = 1; i <= n; i++ ) {
			s_i = str1.charAt( i - 1 );
			// Step 4
			for ( j = 1; j <= m; j++ ) {
				t_j = str2.charAt( j - 1 );
				// Step 5
				if ( s_i == t_j ) {
					cost = 0;
				} else {
					cost = 1;
				}
				// Step 6
				d[i][j] = NumberUtils.min( d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost );
			}
		}
		// Step 7
		return d[n][m];
		
	}
	
	public static int editDistanceByWord( String[] str1, String[] str2 ) {
		
		int d[][]; // matrix
		int n; // length of s
		int m; // length of t
		int i; // iterates through s
		int j; // iterates through t
		String s_i; // ith word of s
		String t_j; // jth word of t
		int cost; // cost
		
		// Step 1
		n = str1.length;
		m = str2.length;
		if ( n == 0 ) {
			return m;
		}
		if ( m == 0 ) {
			return n;
		}
		d = new int[n + 1][m + 1];
		// Step 2
		for ( i = 0; i <= n; i++ ) {
			d[i][0] = i;
		}
		for ( j = 0; j <= m; j++ ) {
			d[0][j] = j;
		}
		// Step 3
		for ( i = 1; i <= n; i++ ) {
			s_i = str1[i - 1];
			// Step 4
			for ( j = 1; j <= m; j++ ) {
				t_j = str2[j - 1];
				// Step 5
				if ( s_i.equals( t_j ) ) {
					cost = 0;
				} else {
					cost = 1;
				}
				// Step 6
				d[i][j] = NumberUtils.min( d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost );
			}
		}
		// Step 7
		return d[n][m];
		
	}
	
	/**
	 * Decode the text that contains web content (e.g. encoded strings).
	 * 
	 * @param text
	 * @return
	 */
	public static String decodeWebcontent( String text ) {
		text = StringEscapeUtils.unescapeHtml3( text );
		text = StringEscapeUtils.unescapeHtml4( text );
		text = StringEscapeUtils.unescapeXml( text );
		text = StringEscapeUtils.unescapeHtml3( text );
		text = StringEscapeUtils.unescapeHtml4( text );
		text = StringEscapeUtils.unescapeXml( text );
		return text;
	}
	
	/**
	 * Match and extract from the source text all matched contents.
	 * 
	 * @param source
	 *            Source text
	 * @param pattern
	 *            Regular expression pattern
	 * @return Matched content
	 */
	public static List<String> extract( String source, Pattern pattern ) {
		List<String> matched = new ArrayList<String>();
		Matcher matcher = pattern.matcher( source );
		while ( matcher.find() ) {
			matched.add( matcher.group() );
		}
		return matched;
	}
	
	/**
	 * Match and extract from the source text all matched contents.
	 * 
	 * @param source
	 *            Source text
	 * @param pattern
	 *            Regular expression pattern
	 * @param match_group
	 *            Match group to be extracted
	 * @return Matched content
	 */
	public static List<String> extract( String source, Pattern pattern, int match_group ) {
		List<String> matched = new ArrayList<String>();
		Matcher matcher = pattern.matcher( source );
		while ( matcher.find() ) {
			matched.add( matcher.group( match_group ) );
		}
		return matched;
	}
	
	/**
	 * Match and extract from the source text all matched contents.
	 * 
	 * @param source
	 *            Source text
	 * @param pattern
	 *            Regular expression pattern
	 * @param match_groups
	 *            Match groups to be extracted
	 * @return Matched content
	 */
	public static List<String[]> extract( String source, Pattern pattern, int... match_groups ) {
		List<String[]> matched = new ArrayList<String[]>();
		Matcher matcher = pattern.matcher( source );
		while ( matcher.find() ) {
			String[] results = new String[match_groups.length];
			for ( int ix = 0; ix < match_groups.length; ix++ ) {
				results[ix] = matcher.group( match_groups[ix] );
			}
			matched.add( results );
		}
		return matched;
	}
	
	/**
	 * Match and extract from the source text all matched contents.
	 * 
	 * @param source
	 *            Source text
	 * @param pattern
	 *            Regular expression pattern
	 * @return Matched content
	 */
	public static List<String> extract( String source, String pattern ) {
		Pattern p = Pattern.compile( pattern, Pattern.DOTALL + Pattern.MULTILINE + Pattern.CASE_INSENSITIVE );
		return extract( source, p );
	}
	
	/**
	 * Match and extract from the source text all matched contents.
	 * 
	 * @param source
	 *            Source text
	 * @param pattern
	 *            Regular expression pattern
	 * @param match_group
	 *            Match group to be extracted
	 * @return Matched content
	 */
	public static List<String> extract( String source, String pattern, int match_group ) {
		Pattern p = Pattern.compile( pattern, Pattern.DOTALL + Pattern.MULTILINE + Pattern.CASE_INSENSITIVE );
		return extract( source, p, match_group );
	}
	
	/**
	 * Match and extract from the source text all matched contents.
	 * 
	 * @param source
	 *            Source text
	 * @param pattern
	 *            Regular expression pattern
	 * @param match_groups
	 *            Match groups to be extracted
	 * @return Matched content
	 */
	public static List<String[]> extract( String source, String pattern, int... match_groups ) {
		Pattern p = Pattern.compile( pattern, Pattern.DOTALL + Pattern.MULTILINE + Pattern.CASE_INSENSITIVE );
		return extract( source, p, match_groups );
	}
	
	/**
	 * Match and extract from the source text the first matched content.
	 * 
	 * @param source
	 *            Source text
	 * @param pattern
	 *            Regular expression pattern
	 * @return Matched content
	 */
	public static String extractFirst( String source, Pattern pattern ) {
		Matcher matcher = pattern.matcher( source );
		if ( matcher.find() ) {
			return matcher.group();
		}
		return null;
	}
	
	/**
	 * Match and extract from the source text the first matched content.
	 * 
	 * @param source
	 *            Source text
	 * @param pattern
	 *            Regular expression pattern
	 * @param match_group
	 *            Match group to be extracted
	 * @return Matched content
	 */
	public static String extractFirst( String source, Pattern pattern, int match_group ) {
		Matcher matcher = pattern.matcher( source );
		if ( matcher.find() ) {
			return matcher.group( match_group );
		}
		return null;
	}
	
	/**
	 * Match and extract from the source text the first matched content.
	 * 
	 * @param source
	 *            Source text
	 * @param pattern
	 *            Regular expression pattern
	 * @param match_groups
	 *            Match groups to be extracted
	 * @return Matched content
	 */
	public static String[] extractFirst( String source, Pattern pattern, int... match_groups ) {
		Matcher matcher = pattern.matcher( source );
		if ( matcher.find() ) {
			String[] results = new String[match_groups.length];
			for ( int ix = 0; ix < match_groups.length; ix++ ) {
				results[ix] = matcher.group( match_groups[ix] );
			}
			return results;
		}
		return null;
	}
	
	/**
	 * Match and extract from the source text the first matched content.
	 * 
	 * @param source
	 *            Source text
	 * @param pattern
	 *            Regular expression pattern
	 * @return Matched content
	 */
	public static String extractFirst( String source, String pattern ) {
		Pattern p = Pattern.compile( pattern, Pattern.DOTALL + Pattern.MULTILINE + Pattern.CASE_INSENSITIVE );
		return extractFirst( source, p );
	}
	
	/**
	 * Match and extract from the source text the first matched content.
	 * 
	 * @param source
	 *            Source text
	 * @param pattern
	 *            Regular expression pattern
	 * @param match_group
	 *            Match group to be extracted
	 * @return Matched content
	 */
	public static String extractFirst( String source, String pattern, int match_group ) {
		Pattern p = Pattern.compile( pattern, Pattern.DOTALL + Pattern.MULTILINE + Pattern.CASE_INSENSITIVE );
		return extractFirst( source, p, match_group );
	}
	
	/**
	 * Match and extract from the source text the first matched content.
	 * 
	 * @param source
	 *            Source text
	 * @param pattern
	 *            Regular expression pattern
	 * @param match_groups
	 *            Match groups to be extracted
	 * @return Matched content
	 */
	public static String[] extractFirst( String source, String pattern, int... match_groups ) {
		Pattern p = Pattern.compile( pattern, Pattern.DOTALL + Pattern.MULTILINE + Pattern.CASE_INSENSITIVE );
		return extractFirst( source, p, match_groups );
	}
	
	/**
	 * Output a string representing the content of the array.
	 * 
	 * @param values
	 * @return
	 */
	public static String toString( int[] values ) {
		StringBuilder sb = new StringBuilder();
		sb.append( "int[" + values.length + "]:" );
		for ( int val : values ) {
			sb.append( " " );
			sb.append( val );
		}
		return sb.toString();
	}
	
	/**
	 * Output a string representing the content of the array.
	 * 
	 * @param values
	 * @return
	 */
	public static String toString( long[] values ) {
		StringBuilder sb = new StringBuilder();
		sb.append( "long[" + values.length + "]:" );
		for ( long val : values ) {
			sb.append( " " );
			sb.append( val );
		}
		return sb.toString();
	}
	
	/**
	 * Output a string representing the content of the array.
	 * 
	 * @param values
	 * @return
	 */
	public static String toString( float[] values ) {
		StringBuilder sb = new StringBuilder();
		sb.append( "float[" + values.length + "]:" );
		for ( float val : values ) {
			sb.append( " " );
			sb.append( val );
		}
		return sb.toString();
	}
	
	/**
	 * Output a string representing the content of the array with the specified
	 * precision.
	 * 
	 * @param values
	 * @return
	 */
	public static String toString( float[] values, int precision ) {
		StringBuilder sb = new StringBuilder();
		sb.append( "float[" + values.length + "]:" );
		for ( float val : values ) {
			sb.append( " " );
			sb.append( formatDouble( val, precision ) );
		}
		return sb.toString();
	}
	
	/**
	 * Output a string representing the content of the array.
	 * 
	 * @param values
	 * @return
	 */
	public static String toString( double[] values ) {
		StringBuilder sb = new StringBuilder();
		sb.append( "double[" + values.length + "]:" );
		for ( double val : values ) {
			sb.append( " " );
			sb.append( val );
		}
		return sb.toString();
	}
	
	/**
	 * Output a string representing the content of the array with the specified
	 * precision.
	 * 
	 * @param values
	 * @return
	 */
	public static String toString( double[] values, int precision ) {
		StringBuilder sb = new StringBuilder();
		sb.append( "double[" + values.length + "]:" );
		for ( double val : values ) {
			sb.append( " " );
			sb.append( formatDouble( val, precision ) );
		}
		return sb.toString();
	}
	
	/**
	 * Output a string representing the content of the array.
	 * 
	 * @param values
	 * @return
	 */
	public static String toString( Collection<?> values ) {
		StringBuilder sb = new StringBuilder();
		sb.append( values.getClass().getName() + "[" + values.size() + "]:" );
		for ( Object val : values ) {
			sb.append( " " );
			sb.append( val.toString() );
		}
		return sb.toString();
	}
	
	/**
	 * Replace all xml/html tags by the specified replacement string.
	 * 
	 * @param input
	 * @param replacement
	 * @return
	 */
	public static String removeTags( String input, String replacement ) {
		Pattern ptag = Pattern.compile( "<[^>]+>", Pattern.DOTALL + Pattern.MULTILINE + Pattern.CASE_INSENSITIVE );
		Matcher m = ptag.matcher( input );
		input = m.replaceAll( replacement );
		return input;
	}
	
	/**
	 * Replace the specified xml/html tags by the specified replacement string.
	 * 
	 * @param input
	 * @param replacement
	 * @param tagnames
	 * @return
	 */
	public static String removeTags( String input, String replacement, String... tagnames ) {
		for ( String tagname : tagnames ) {
			Pattern ptag = Pattern.compile( "</?" + tagname + ">", Pattern.DOTALL + Pattern.MULTILINE + Pattern.CASE_INSENSITIVE );
			Matcher m = ptag.matcher( input );
			input = m.replaceAll( replacement );
		}
		return input;
	}
	
	/**
	 * Replace the specified xml/html tags by the specified replacement string.
	 * 
	 * @param input
	 * @param replacement
	 * @param tagnames
	 * @return
	 */
	public static String removeTags( String input, String replacement, Collection<String> tagnames ) {
		for ( String tagname : tagnames ) {
			Pattern ptag = Pattern.compile( "</?" + tagname + ">", Pattern.DOTALL + Pattern.MULTILINE + Pattern.CASE_INSENSITIVE );
			Matcher m = ptag.matcher( input );
			input = m.replaceAll( replacement );
		}
		return input;
	}
	
	/**
	 * Replace the specified xml/html tags (including the enclosed content) by
	 * the specified replacement string.
	 * 
	 * @param input
	 * @param replacement
	 * @param tagnames
	 * @return
	 */
	public static String removeTagsWithContents( String input, String replacement, String... tagnames ) {
		for ( String tagname : tagnames ) {
			Pattern ptag = Pattern.compile( "<" + tagname + ">.+?</" + tagname + ">", Pattern.DOTALL + Pattern.MULTILINE + Pattern.CASE_INSENSITIVE );
			Matcher m = ptag.matcher( input );
			input = m.replaceAll( replacement );
		}
		return input;
	}
	
	/**
	 * Replace the specified xml/html tags (including the enclosed content) by
	 * the specified replacement string.
	 * 
	 * @param input
	 * @param replacement
	 * @param tagnames
	 * @return
	 */
	public static String removeTagsWithContents( String input, String replacement, Collection<String> tagnames ) {
		for ( String tagname : tagnames ) {
			Pattern ptag = Pattern.compile( "<" + tagname + ">.+?</" + tagname + ">", Pattern.DOTALL + Pattern.MULTILINE + Pattern.CASE_INSENSITIVE );
			Matcher m = ptag.matcher( input );
			input = m.replaceAll( replacement );
		}
		return input;
	}
	
	/**
	 * Judge whether the character is a vowel character ('a', 'e', 'i', 'o', or
	 * 'u').
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isVowel( char ch ) {
		return ch == 'e' || ch == 'u' || ch == 'o' || ch == 'a' || ch == 'i';
	}
	
	/**
	 * Count the number of vowel characters in a word.
	 * 
	 * @param word
	 * @return
	 */
	public static int countVowel( String word ) {
		int count_vowel = 0;
		for ( int ix = 0; ix < word.length(); ix++ ) {
			if ( isVowel( word.charAt( ix ) ) ) {
				count_vowel++;
			}
		}
		return count_vowel;
	}
	
	/**
	 * Count the number of consonant characters in a word.
	 * 
	 * @param word
	 * @return
	 */
	public static int countConsonant( String word ) {
		return word.length() - countVowel( word );
	}
	
	/**
	 * Return the lowercase form of the input String if it is not null. If the
	 * input is null, just return null without any exception.
	 * 
	 * @param input
	 * @return
	 */
	public static String lowercase( String input ) {
		if ( input == null ) {
			return null;
		}
		return input.toLowerCase();
	}
	
	/**
	 * Return the uppercase form of the input String if it is not null. If the
	 * input is null, just return null without any exception.
	 * 
	 * @param input
	 * @return
	 */
	public static String uppercase( String input ) {
		if ( input == null ) {
			return null;
		}
		return input.toUpperCase();
	}
	
}
