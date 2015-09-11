package edu.umass.cs.ciir.controversy.index;

import java.io.File;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Test {
	
	public static void main( String[] args ) {
		try {
			
			String path_index = "C:/wikidump_index_random10";
			Directory dir = FSDirectory.open( new File( path_index ) );
			IndexReader index = DirectoryReader.open( dir );
			System.out.println( index.numDocs() );
			index.close();
			dir.close();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
