package utils;

import java.io.IOException;

import com.google.gson.stream.*;

public class JsonUtils {
	
	
	public static String getNextString( JsonReader json ) throws IOException {
		JsonToken tk = json.peek();
		if( tk==JsonToken.NULL ) {
			json.skipValue();
			return null;
		}else{
			return json.nextString(); 
		}
	}
	
	
	
	
	
	
	
	
}
