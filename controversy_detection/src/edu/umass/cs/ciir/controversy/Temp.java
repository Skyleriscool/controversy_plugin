package edu.umass.cs.ciir.controversy;

import java.util.Map;
import java.util.TreeMap;

import com.google.gson.Gson;

import edu.umass.cs.ciir.controversy.knn.EntryValue;

public class Temp {
	
	public static void main( String[] args ) {
		
		try {
			
			Gson gson = new Gson();
			Map<String, Object> map = new TreeMap<String, Object>();
			map.put( "score", new EntryValue( "kds", 1.0 ) );
			
			System.out.println( gson.toJson( map ) );
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
	}
	
}
