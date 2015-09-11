package edu.umass.cs.ciir.controversy.knn.sim;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;

import edu.umass.cs.ciir.controversy.knn.EntryValue;
import utils.ir.lucene.similarity.QLDirichletSmoothing;

public class LuceneQLSearcher implements IndexSearcher {
	
	protected Directory dir;
	protected IndexReader index;
	protected org.apache.lucene.search.IndexSearcher searcher;
	
	protected String field_key;
	protected String field_title;
	protected String field_text;
	
	protected Set<String> fields;
	
	public LuceneQLSearcher( String path, double mu, String field_key, String field_title, String field_text ) throws IOException {
		this.dir = FSDirectory.open( new File( path ) );
		this.index = DirectoryReader.open( this.dir );
		this.searcher = new org.apache.lucene.search.IndexSearcher( this.index );
		searcher.setSimilarity( new QLDirichletSmoothing( mu ) );
		this.field_key = field_key;
		this.field_title = field_title;
		this.field_text = field_text;
		this.fields = new HashSet<String>();
		this.fields.add( field_key );
		this.fields.add( field_title );
	}
	
	public List<EntryValue> search( Object query, int topentries, Map<String, Object> info ) {
		long timestamp = System.currentTimeMillis();
		Object[] qinfo = (Object[]) query;
		TopDocs hits = null;
		try {
			hits = this.searcher.search( (Query) qinfo[0], topentries );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		List<EntryValue> entries = new ArrayList<EntryValue>();
		List<EntryValue> entries_title = new ArrayList<EntryValue>();
		if ( hits != null && hits.scoreDocs != null ) {
			try {
				double norm = QLDirichletSmoothing.scoreNormalizer( index, (String) qinfo[1], (String[]) qinfo[2], (double[]) qinfo[3] );
				for ( ScoreDoc doc : hits.scoreDocs ) {
					Document d = index.document( doc.doc, fields );
					String entry = d.get( field_key );
					String title = d.get( field_title );
					entries.add( new EntryValue( entry, doc.score + norm ) ); // transform into standard QL scores
					entries_title.add( new EntryValue( title, doc.score + norm ) );
				}
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		if ( info != null ) {
			info.put( "top_wikientries", topentries );
			info.put( "top_wikientries_QL", entries );
			info.put( "top_wikientries_QL_title", entries_title );
			info.put( "time_search_wiki_entries", ( System.currentTimeMillis() - timestamp ) / 1000.0 );
		}
		return entries;
	}
	
	public void close() throws IOException {
		this.index.close();
		this.dir.close();
	}
	
}
