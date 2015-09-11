package utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;

public class DumbOutputStream extends OutputStream {
	
	public void write( int b ) throws IOException {
		// do nothing
	}
	
	public static OutputStream get() {
		return new BufferedOutputStream( new DumbOutputStream() );
	}
	
}
