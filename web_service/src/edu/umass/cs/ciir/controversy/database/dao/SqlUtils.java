package edu.umass.cs.ciir.controversy.database.dao;

import java.io.IOException;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class SqlUtils {
	
	/**
	 * The URL encode & decode can deal with the special characters that should be replaced in sql commands.
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static String encodeString( String str ) throws IOException {
		return URLEncoder.encode( str, "UTF-8" );
	}
	
	/**
	 * The URL encode & decode can deal with the special characters that should be replaced in sql commands.
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static String decodeString( String str ) throws IOException {
		return URLDecoder.decode( str, "UTF-8" );
	}
	
}
