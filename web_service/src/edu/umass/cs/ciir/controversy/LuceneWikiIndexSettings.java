package edu.umass.cs.ciir.controversy;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

import java.net.URI;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;

import utils.ir.analysis.TextAnalyzer;

public class LuceneWikiIndexSettings {
	
	public static final String path_config = "../../config_lucene_wiki_index";
	
	public static String path_index;
	public static String field_key; // this is the field storing case-insensitive and normalized Wikipedia titles
	public static String field_title; // this is the field storing original Wikipedia titles: use this field to generate URL to Wikipedia entries
	public static String field_text;
	public static double smoothing_dir_mu = 1000;
	
	public static String tokenization = "alpha";
	public static String casing = "lc";
	public static String stopwords = "indri stop";
	public static String stemming = "kstem";
	public static String oov = "no oov";
	
	public static Analyzer analyzer;
	
	static {
		try {
			
			URI uri = new LuceneWikiIndexSettings().getClass().getResource( "/" ).toURI().resolve( path_config );
			File file = new File( uri.getPath() );
			Properties props = new Properties();
			InputStream instream = new FileInputStream( file );
			props.load( instream );
			instream.close();
			
			path_index = props.getProperty( "path_index" ).trim();
			field_key = props.getProperty( "field_key" ).trim();
			field_title = props.getProperty( "field_title" ).trim();
			field_text = props.getProperty( "field_text" ).trim();
			
			if ( props.getProperty( "smoothing_dir_mu" ) != null ) {
				smoothing_dir_mu = Double.parseDouble( props.getProperty( "smoothing_dir_mu" ).trim() );
			}
			
			if ( props.getProperty( "tokenization" ) != null ) {
				tokenization = props.getProperty( "tokenization" ).trim();
			}
			
			if ( props.getProperty( "casing" ) != null ) {
				casing = props.getProperty( "casing" ).trim();
			}
			
			if ( props.getProperty( "stopwords" ) != null ) {
				stopwords = props.getProperty( "stopwords" ).trim();
			}
			
			if ( props.getProperty( "stemming" ) != null ) {
				stemming = props.getProperty( "stemming" ).trim();
			}
			
			analyzer = TextAnalyzer.get( tokenization, casing, stopwords, stemming, oov );
			
			System.out.println( " >> load lucene wiki index settings from " + file.getAbsolutePath() );
			System.out.println( "   --> path_index = " + path_index );
			System.out.println( "   --> field_key = " + field_key );
			System.out.println( "   --> field_title = " + field_title );
			System.out.println( "   --> field_text = " + field_text );
			System.out.println( "   --> smoothing_dir_mu = " + smoothing_dir_mu );
			System.out.println( "   --> tokenization = " + tokenization );
			System.out.println( "   --> casing = " + casing );
			System.out.println( "   --> stopwords = " + stopwords );
			System.out.println( "   --> stemming = " + stemming );
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
