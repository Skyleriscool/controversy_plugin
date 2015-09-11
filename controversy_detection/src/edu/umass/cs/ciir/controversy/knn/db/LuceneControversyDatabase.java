package edu.umass.cs.ciir.controversy.knn.db;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;

import utils.ir.lucene.LuceneUtils;
import edu.umass.cs.ciir.controversy.knn.EntryValue;

public class LuceneControversyDatabase extends ControversyDatabase {
	
	protected Directory dir;
	protected IndexReader index;
	
	protected String field_key;
	protected String field_score;
	
	protected Set<String> fields;
	protected float unjudged;
	
	public LuceneControversyDatabase( String path, String field_key, String field_score ) throws IOException {
		this.dir = FSDirectory.open( new File( path ) );
		this.index = DirectoryReader.open( this.dir );
		this.field_key = field_key;
		this.field_score = field_score;
		this.fields = new HashSet<String>();
		this.fields.add( field_score );
		assignUnjudgedScore();
	}
	
	private void assignUnjudgedScore() {
		float sum = 0;
		try {
			for ( int docid = 0 ; docid < index.maxDoc() ; docid++ ) {
				Document doc = index.document( docid, fields );
				sum += doc.getField( field_score ).numericValue().floatValue();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		this.unjudged = sum / index.maxDoc();
	}
	
	public EntryValue getControversyScore( EntryValue entry ) {
		
		Float score = null;
		
		try {
			int docid = LuceneUtils.find( index, field_key, entry.getEntry() );
			if ( docid >= 0 ) {
				Document doc = index.document( docid, fields );
				score = doc.getField( field_score ).numericValue().floatValue();
			} else {
				score = unjudged;
			}
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		
		if ( score == null ) {
			return null;
		}
		
		return new EntryValue( entry.getEntry(), score );
		
	}
	
	public void close() throws IOException {
		index.close();
		dir.close();
	}
	
}
