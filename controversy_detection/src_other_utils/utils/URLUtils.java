package utils;

import java.io.InputStream;
import java.io.BufferedReader;

import java.util.Map;
import java.util.TreeMap;

public class URLUtils {
	
	public static Map<String, String> domain_country = new TreeMap<String, String>();
	
	static {
		try {
			InputStream instream = new URLUtils().getClass().getResourceAsStream( "data/domain_country" );
			BufferedReader reader = IOUtils.getBufferedReader( instream );
			String line = reader.readLine();
			while ( line != null ) {
				String[] splits = line.split( "=" );
				domain_country.put( splits[0].trim().toLowerCase(), splits[1].trim() );
				line = reader.readLine();
			}
			reader.close();
			instream.close();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static boolean isCountryDomain( String str ) {
		return domain_country.containsKey( str );
	}
	
}
