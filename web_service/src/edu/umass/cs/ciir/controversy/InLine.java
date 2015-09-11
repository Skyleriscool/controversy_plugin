package edu.umass.cs.ciir.controversy;

import java.io.File;

import utils.IOUtils;

public class InLine {
	
	public static void main( String[] args ) {
		try {
			
			String inline = new String( IOUtils.readBytes( new File( "WebContent/select.js" ) ), "UTF-8" ).replaceAll( "\\s+", " " );
			System.out.print( inline );
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
