package utils;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.net.URLDecoder;

import java.util.TreeMap;
import java.util.Collection;

/**
 * KVPair is designed as a Map whose key is String type and value can be various types (integers, long integers, floats, doubles, Strings, and general objects).
 * 
 * @author Jiepu Jiang
 * @version Feb 27, 2013
 */
public class KVPair extends TreeMap<String, Object> {
	
	private static final long serialVersionUID = 35862219730209502L;
	
	/**
	 * Constructor.
	 */
	public KVPair() {
		super();
	}
	
	/**
	 * Get a stored integer value. If the key does not exist, return null. Note that an exception will be thrown if the stored value for that key is not an
	 * integer.
	 * 
	 * @param key
	 * @return
	 */
	public Integer getInteger( String key ) {
		return getInteger( key, null );
	}
	
	/**
	 * Get a stored integer value. If the key does not exist, return the default value. Note that an exception will be thrown if the stored value for that key
	 * is not an integer.
	 * 
	 * @param key
	 * @param default_val
	 * @return
	 */
	public Integer getInteger( String key, Integer default_val ) {
		Object val = get( key );
		if ( val == null ) {
			return default_val;
		}
		return (Integer) val;
	}
	
	/**
	 * Get a stored long integer value. If the key does not exist, return null. Note that an exception will be thrown if the stored value for that key is not a
	 * long integer.
	 * 
	 * @param key
	 * @return
	 */
	public Long getLong( String key ) {
		return getLong( key, null );
	}
	
	/**
	 * Get a stored long integer value. If the key does not exist, return the default value. Note that an exception will be thrown if the stored value for that
	 * key is not a long integer.
	 * 
	 * @param key
	 * @param default_val
	 * @return
	 */
	public Long getLong( String key, Long default_val ) {
		Object val = get( key );
		if ( val == null ) {
			return default_val;
		}
		return (Long) val;
	}
	
	/**
	 * Get a stored float value. If the key does not exist, return null. Note that an exception will be thrown if the stored value for that key is not a float.
	 * 
	 * @param key
	 * @return
	 */
	public Float getFloat( String key ) {
		return getFloat( key, null );
	}
	
	/**
	 * Get a stored float value. If the key does not exist, return the default value. Note that an exception will be thrown if the stored value for that key is
	 * not a float.
	 * 
	 * @param key
	 * @param default_val
	 * @return
	 */
	public Float getFloat( String key, Float default_val ) {
		Object val = get( key );
		if ( val == null ) {
			return default_val;
		}
		return (Float) val;
	}
	
	/**
	 * Get a stored double value. If the key does not exist, return null. Note that an exception will be thrown if the stored value for that key is not a
	 * double.
	 * 
	 * @param key
	 * @return
	 */
	public Double getDouble( String key ) {
		return getDouble( key, null );
	}
	
	/**
	 * Get a stored double value. If the key does not exist, return the default value. Note that an exception will be thrown if the stored value for that key is
	 * not a double.
	 * 
	 * @param key
	 * @param default_val
	 * @return
	 */
	public Double getDouble( String key, Double default_val ) {
		Object val = get( key );
		if ( val == null ) {
			return default_val;
		}
		return (Double) val;
	}
	
	/**
	 * Get the specified key values from an array of KVPairs.
	 * 
	 * @param pairs
	 * @param key
	 * @return
	 */
	public static double[] getDouble( KVPair[] pairs, String key ) {
		double[] values = new double[pairs.length];
		for ( int ix = 0 ; ix < pairs.length ; ix++ ) {
			values[ix] = pairs[ix].getDouble( key );
		}
		return values;
	}
	
	/**
	 * Get the specified key values from a collection of KVPairs.
	 * 
	 * @param pairs
	 * @param key
	 * @return
	 */
	public static double[] getDouble( Collection<KVPair> pairs, String key ) {
		double[] values = new double[pairs.size()];
		int ix = 0;
		for ( KVPair pair : pairs ) {
			values[ix] = pair.getDouble( key );
			ix++;
		}
		return values;
	}
	
	/**
	 * Get a stored String value. If the key does not exist, return null. Note that an exception will be thrown if the stored value for that key is not a
	 * String.
	 * 
	 * @param key
	 * @return
	 */
	public String getString( String key ) {
		return getString( key, null );
	}
	
	/**
	 * Get a stored String value. If the key does not exist, return the default value. Note that an exception will be thrown if the stored value for that key is
	 * not a String.
	 * 
	 * @param key
	 * @param default_val
	 * @return
	 */
	public String getString( String key, String default_val ) {
		Object val = get( key );
		if ( val == null ) {
			return default_val;
		}
		return (String) val;
	}
	
	/**
	 * Get a stored integer array value. If the key does not exist, return null. Note that an exception will be thrown if the stored value for that key is not
	 * an integer array.
	 * 
	 * @param key
	 * @return
	 */
	public int[] getIntArray( String key ) {
		return getIntArray( key, null );
	}
	
	/**
	 * Get a stored integer array value. If the key does not exist, return the default value. Note that an exception will be thrown if the stored value for that
	 * key is not an integer array.
	 * 
	 * @param key
	 * @param default_val
	 * @return
	 */
	public int[] getIntArray( String key, int[] default_val ) {
		Object val = get( key );
		if ( val == null ) {
			return default_val;
		}
		return (int[]) val;
	}
	
	/**
	 * Get a stored long integer array value. If the key does not exist, return null. Note that an exception will be thrown if the stored value for that key is
	 * not a long integer array.
	 * 
	 * @param key
	 * @return
	 */
	public long[] getLongArray( String key ) {
		return getLongArray( key, null );
	}
	
	/**
	 * Get a stored long integer array value. If the key does not exist, return the default value. Note that an exception will be thrown if the stored value for
	 * that key is not a long integer array.
	 * 
	 * @param key
	 * @param default_val
	 * @return
	 */
	public long[] getLongArray( String key, long[] default_val ) {
		Object val = get( key );
		if ( val == null ) {
			return default_val;
		}
		return (long[]) val;
	}
	
	/**
	 * Get a stored float array value. If the key does not exist, return null. Note that an exception will be thrown if the stored value for that key is not a
	 * float array.
	 * 
	 * @param key
	 * @return
	 */
	public float[] getFloatArray( String key ) {
		return getFloatArray( key, null );
	}
	
	/**
	 * Get a stored float array value. If the key does not exist, return the default value. Note that an exception will be thrown if the stored value for that
	 * key is not a float array.
	 * 
	 * @param key
	 * @param default_val
	 * @return
	 */
	public float[] getFloatArray( String key, float[] default_val ) {
		Object val = get( key );
		if ( val == null ) {
			return default_val;
		}
		return (float[]) val;
	}
	
	/**
	 * Get a stored double array value. If the key does not exist, return null. Note that an exception will be thrown if the stored value for that key is not a
	 * double array.
	 * 
	 * @param key
	 * @return
	 */
	public double[] getDoubleArray( String key ) {
		return getDoubleArray( key, null );
	}
	
	/**
	 * Get a stored double array value. If the key does not exist, return the default value. Note that an exception will be thrown if the stored value for that
	 * key is not a double array.
	 * 
	 * @param key
	 * @param default_val
	 * @return
	 */
	public double[] getDoubleArray( String key, double[] default_val ) {
		Object val = get( key );
		if ( val == null ) {
			return default_val;
		}
		return (double[]) val;
	}
	
	/**
	 * Get a stored object of arbitrary type. If the key does not exist, return null.
	 * 
	 * @param key
	 * @return
	 */
	public Object getObject( String key ) {
		return getObject( key, null );
	}
	
	/**
	 * Get a stored object of arbitrary type. If the key does not exist, return the default value.
	 * 
	 * @param key
	 * @param default_val
	 * @return
	 */
	public Object getObject( String key, Object default_val ) {
		Object val = get( key );
		if ( val == null ) {
			return default_val;
		}
		return val;
	}
	
	/**
	 * Generate a string representation of a key-value pair. For example: [doc,93972] will be "doc:I:93972".
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String toString( String key, int value ) throws UnsupportedEncodingException {
		return URLEncoder.encode( key, "UTF-8" ) + ":I:" + value;
	}
	
	/**
	 * Generate a string representation of a key-value pair. For example: [doc,93972] will be "doc:L:93972".
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String toString( String key, long value ) throws UnsupportedEncodingException {
		return URLEncoder.encode( key, "UTF-8" ) + ":L:" + value;
	}
	
	/**
	 * Generate a string representation of a key-value pair. For example: [score,0.388164] will be "score:F:0.388164".
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String toString( String key, float value ) throws UnsupportedEncodingException {
		return URLEncoder.encode( key, "UTF-8" ) + ":F:" + value;
	}
	
	/**
	 * Generate a string representation of a key-value pair. For example: [score,0.388164] will be "score:D:0.388164".
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String toString( String key, double value ) throws UnsupportedEncodingException {
		return URLEncoder.encode( key, "UTF-8" ) + ":D:" + value;
	}
	
	/**
	 * Generate a string representation of a key-value pair. For example: [docno,clueweb09-939271-8832] will be "docno:S:clueweb09-939271-8832".
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String toString( String key, String value ) throws UnsupportedEncodingException {
		return URLEncoder.encode( key, "UTF-8" ) + ":S:" + URLEncoder.encode( value, "UTF-8" );
	}
	
	/**
	 * Generate a string representation of a key-value pair. For example: [docs,{1,3,6,10}] will be "docs:Is:4,1,3,6,10".
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String toString( String key, int[] value ) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append( URLEncoder.encode( key, "UTF-8" ) + ":Is:" + value.length );
		for ( int ix = 0 ; ix < value.length ; ix++ ) {
			sb.append( "," + value[ix] );
		}
		return sb.toString();
	}
	
	/**
	 * Generate a string representation of a key-value pair. For example: [docs,{1,3,6,10}] will be "docs:Ls:4,1,3,6,10".
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String toString( String key, long[] value ) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append( URLEncoder.encode( key, "UTF-8" ) + ":Ls:" + value.length );
		for ( int ix = 0 ; ix < value.length ; ix++ ) {
			sb.append( "," + value[ix] );
		}
		return sb.toString();
	}
	
	/**
	 * Generate a string representation of a key-value pair. For example: [scores,{0.43,0.112}] will be "scores:Fs:2,0.43,0.112".
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String toString( String key, float[] value ) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append( URLEncoder.encode( key, "UTF-8" ) + ":Fs:" + value.length );
		for ( int ix = 0 ; ix < value.length ; ix++ ) {
			sb.append( "," + value[ix] );
		}
		return sb.toString();
	}
	
	/**
	 * Generate a string representation of a key-value pair. For example: [scores,{0.43,0.112}] will be "scores:Ds:2,0.43,0.112".
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String toString( String key, double[] value ) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append( URLEncoder.encode( key, "UTF-8" ) + ":Ds:" + value.length );
		for ( int ix = 0 ; ix < value.length ; ix++ ) {
			sb.append( "," + value[ix] );
		}
		return sb.toString();
	}
	
	/**
	 * Generate a string representation of a key-value pair if the value object is one of the following type: integer, long integer, float, double, string, or
	 * the 5 type's array.
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String toString( String key, Object value ) throws UnsupportedEncodingException {
		if ( value instanceof Integer ) {
			return toString( key, ( (Integer) value ).intValue() );
		} else if ( value instanceof Long ) {
			return toString( key, ( (Long) value ).longValue() );
		} else if ( value instanceof Float ) {
			return toString( key, ( (Float) value ).floatValue() );
		} else if ( value instanceof Double ) {
			return toString( key, ( (Double) value ).doubleValue() );
		} else if ( value instanceof String ) {
			return toString( key, (String) value );
		} else if ( value instanceof int[] ) {
			return toString( key, (int[]) value );
		} else if ( value instanceof long[] ) {
			return toString( key, (long[]) value );
		} else if ( value instanceof float[] ) {
			return toString( key, (float[]) value );
		} else if ( value instanceof double[] ) {
			return toString( key, (double[]) value );
		}
		return null;
	}
	
	/**
	 * Return a string representation of the KVPair. Note that only integer, long, float, double, String, int[], long[], float[], double[] will be included into
	 * the string. Each key-value pair will be separated by ";".
	 * 
	 * @return
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean isfirst = true;
		for ( String key : keySet() ) {
			Object val = get( key );
			if ( val instanceof Integer || val instanceof Long || val instanceof Float || val instanceof Double || val instanceof String ||
					val instanceof int[] || val instanceof long[] || val instanceof float[] || val instanceof double[] ) {
				if ( !isfirst ) {
					sb.append( ";" );
				}
				String str = null;
				try {
					str = toString( key, val );
				} catch ( UnsupportedEncodingException e ) {
					e.printStackTrace();
				}
				if ( str != null ) {
					sb.append( str );
				}
				isfirst = false;
			}
		}
		return sb.toString();
	}
	
	/**
	 * Parse a string representation (consistent with the format generated by toString()) into a KVPair.
	 * 
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static KVPair parse( String text ) throws UnsupportedEncodingException {
		KVPair pair = new KVPair();
		String[] splits = text.split( ";" );
		if ( splits != null && splits.length > 0 ) {
			for ( String split : splits ) {
				Object[] kv = parsePair( split );
				pair.put( (String) kv[0], kv[1] );
			}
		}
		return pair;
	}
	
	/**
	 * Parse s string representation of one key-value pair.
	 * 
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Object[] parsePair( String text ) throws UnsupportedEncodingException {
		String[] splits = text.split( ":" );
		if ( splits.length < 2 ) {
			return null;
		}
		String key = URLDecoder.decode( splits[0], "UTF-8" );
		Object value = null;
		if ( splits[1].equals( "I" ) ) {
			value = Integer.parseInt( splits[2] );
		} else if ( splits[1].equals( "L" ) ) {
			value = Long.parseLong( splits[2] );
		} else if ( splits[1].equals( "F" ) ) {
			value = Float.parseFloat( splits[2] );
		} else if ( splits[1].equals( "D" ) ) {
			value = Double.parseDouble( splits[2] );
		} else if ( splits[1].equals( "S" ) ) {
			if ( splits.length == 2 || splits[2] == null ) {
				value = "";
			} else {
				value = URLDecoder.decode( splits[2], "UTF-8" );
			}
		} else if ( splits[1].equals( "Is" ) ) {
			String[] vals = splits[2].split( "," );
			int[] array = new int[Integer.parseInt( vals[0] )];
			for ( int ix = 1 ; ix < vals.length ; ix++ ) {
				array[ix - 1] = Integer.parseInt( vals[ix] );
			}
			value = array;
		} else if ( splits[1].equals( "Ls" ) ) {
			String[] vals = splits[2].split( "," );
			long[] array = new long[Integer.parseInt( vals[0] )];
			for ( int ix = 1 ; ix < vals.length ; ix++ ) {
				array[ix - 1] = Long.parseLong( vals[ix] );
			}
			value = array;
		} else if ( splits[1].equals( "Fs" ) ) {
			String[] vals = splits[2].split( "," );
			float[] array = new float[Integer.parseInt( vals[0] )];
			for ( int ix = 1 ; ix < vals.length ; ix++ ) {
				array[ix - 1] = Float.parseFloat( vals[ix] );
			}
			value = array;
		} else if ( splits[1].equals( "Ds" ) ) {
			String[] vals = splits[2].split( "," );
			double[] array = new double[Integer.parseInt( vals[0] )];
			for ( int ix = 1 ; ix < vals.length ; ix++ ) {
				array[ix - 1] = Double.parseDouble( vals[ix] );
			}
			value = array;
		}
		return new Object[] { key, value };
	}
	
}
