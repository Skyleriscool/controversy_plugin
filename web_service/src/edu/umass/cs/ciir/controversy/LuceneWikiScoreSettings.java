package edu.umass.cs.ciir.controversy;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Properties;

public class LuceneWikiScoreSettings {
	
	public static final String path_config = "../../config_lucene_wiki_score";
	
	public static String path_index;
	public static String field_key;
	public static String field_score;
	
	static {
		try {
			
			URI uri = new LuceneWikiScoreSettings().getClass().getResource( "/" ).toURI().resolve( path_config );
			File file = new File( uri.getPath() );
			Properties props = new Properties();
			InputStream instream = new FileInputStream( file );
			props.load( instream );
			instream.close();
			
			path_index = props.getProperty( "path_index" ).trim();
			field_key = props.getProperty( "field_key" ).trim();
			field_score = props.getProperty( "field_score" ).trim();
			
			System.out.println( " >> load lucene wiki controversy score settings from " + file.getAbsolutePath() );
			System.out.println( "   --> path_index = " + path_index );
			System.out.println( "   --> field_key = " + field_key );
			System.out.println( "   --> field_score = " + field_score );
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
