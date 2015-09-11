package edu.umass.cs.ciir.controversy;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

import java.net.URI;
import java.util.Properties;

public class ControversyScorerSettings {
	
	public static final String path_config = "../../config_controversy_scorer";
	
	public static int default_topwords;
	public static int default_topentries;
	
	static {
		try {
			
			URI uri = new ControversyScorerSettings().getClass().getResource( "/" ).toURI().resolve( path_config );
			File file = new File( uri.getPath() );
			Properties props = new Properties();
			InputStream instream = new FileInputStream( file );
			props.load( instream );
			instream.close();
			
			default_topwords = Integer.parseInt( props.getProperty( "default_topwords" ).trim() );
			default_topentries = Integer.parseInt( props.getProperty( "default_topentries" ).trim() );
			
			System.out.println( " >> load controversy scorer settings from " + file.getAbsolutePath() );
			System.out.println( "   --> default_topwords = " + default_topwords );
			System.out.println( "   --> default_topentries = " + default_topentries );
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
