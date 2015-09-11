package utils.ir.eval;

import java.util.Map;
import java.util.Set;
import java.io.IOException;

import org.apache.lucene.index.IndexReader;

import utils.KVPair;
import utils.ir.lucene.LuceneUtils;
import lemurproject.indri.QueryEnvironment;

/**
 * SearchResult stores information of a result item, including: an internal docid, an external docno, and the relevance score.
 * 
 * @author Jiepu Jiang
 * @version Feb 17, 2015
 */
public class SearchResult implements Comparable<SearchResult> {
	
	/** Internal id of the item. */
	protected int docid;
	
	/** Universal docno of the item. */
	protected String docno;
	
	/** Relevance score of the item. */
	protected double score;
	
	/** Stores information related to the result item (e.g. factor scores etc). */
	protected KVPair info;
	
	/**
	 * Constructor.
	 * 
	 * @param docid
	 * @param docno
	 * @param score
	 */
	public SearchResult( int docid, String docno, double score ) {
		this.docid = docid;
		this.docno = docno;
		this.score = score;
		this.info = new KVPair();
	}
	
	/**
	 * Constructor. By default, it will set docid as -1.
	 * 
	 * @param docno
	 * @param score
	 */
	public SearchResult( String docno, double score ) {
		this( -1, docno, score );
	}
	
	/**
	 * Constructor. By default, it will set docno as null.
	 * 
	 * @param docid
	 * @param score
	 */
	public SearchResult( int docid, double score ) {
		this( docid, null, score );
	}
	
	/**
	 * Constructor for an empty SearchResult.
	 */
	public SearchResult() {
		this( -1, null, 0 );
	}
	
	/**
	 * Two search result will be ranked according to its relevance score (the larger the relevance score, the higher it will be ranked). In case two documents
	 * have the same relevance score, it will be ranked by its document number literally, or its docid in index (if docno is not assigned).
	 */
	public int compareTo( SearchResult result ) {
		int val = new Double( result.score ).compareTo( this.score );
		if ( val == 0 ) {
			if ( docno != null && result.docno != null ) {
				val = docno.compareTo( result.docno );
			}
			if ( val == 0 && docid != -1 && result.docid != -1 ) {
				val = docid - result.docid;
			}
		}
		return val;
	}
	
	public boolean equals( Object obj ) {
		if ( obj != null && obj instanceof SearchResult ) {
			SearchResult result = (SearchResult) obj;
			return compareTo( result ) == 0;
		}
		return false;
	}
	
	/**
	 * @return The internal id of the document in index.
	 */
	public int getDocid() {
		return this.docid;
	}
	
	/**
	 * @param docid
	 *            The internal id of the document in index.
	 * @return
	 */
	public SearchResult setDocid( int docid ) {
		this.docid = docid;
		return this;
	}
	
	/**
	 * If a docno has been set, this method will set the internal docid by looking for documents matching the docno in index.
	 * 
	 * @param index
	 * @param field_docno
	 * @param reuse
	 *            If not null, it will be used for buffering docno-docid mapping.
	 * @return
	 * @throws IOException
	 */
	public SearchResult setDocid( IndexReader index, String field_docno, Map<String, Integer> reuse ) throws IOException {
		if ( reuse != null && ( docid = reuse.getOrDefault( docno, -1 ) ) >= 0 ) {
			return this;
		}
		docid = LuceneUtils.find( index, field_docno, docno );
		if ( docid == -1 ) {
			throw new IOException( "Cannot locate internal document id. Squirrel docno field name: " + field_docno + ", document docno: " + docno );
		}
		if ( reuse != null ) {
			reuse.put( docno, docid );
		}
		return this;
	}
	
	/**
	 * If a docno has been set, this method will set the internal docid by looking for documents matching the docno in index.
	 * 
	 * @param index
	 * @param field_docno
	 * @param reuse
	 *            If not null, it will be used for buffering docno-docid mapping.
	 * @return
	 * @throws Exception
	 */
	public SearchResult setDocid( QueryEnvironment index, String field_docno, Map<String, Integer> reuse ) throws Exception {
		if ( reuse != null && ( docid = reuse.getOrDefault( docno, -1 ) ) >= 0 ) {
			return this;
		}
		int[] ids = index.documentIDsFromMetadata( field_docno, new String[] { docno } );
		if ( ids == null || ids.length == 0 ) {
			throw new IOException( "Cannot locate internal document id. Indri docno field name: " + field_docno + ", document docno: " + docno );
		}
		docid = ids[0];
		if ( reuse != null ) {
			reuse.put( docno, docid );
		}
		return this;
	}
	
	/**
	 * @return The external docno of document
	 */
	public String getDocno() {
		return this.docno;
	}
	
	/**
	 * @param docno
	 *            The external docno of document
	 * @return
	 */
	public SearchResult setDocno( String docno ) {
		this.docno = docno;
		return this;
	}
	
	/**
	 * If the internal docid has been set, this method will automatically access the index field that stored the document's docno.
	 * 
	 * @param index
	 * @param field_docno
	 * @param reuse
	 *            If not null, it will be used for buffering docid-docno mapping.
	 * @return
	 * @throws IOException
	 */
	public SearchResult setDocno( IndexReader index, String field_docno, Map<Integer, String> reuse ) throws IOException {
		if ( reuse != null && ( docno = reuse.getOrDefault( docid, null ) ) != null ) {
			return this;
		}
		docno = LuceneUtils.getDocFieldStringValue( index, docid, field_docno );
		if ( docno == null ) {
			throw new IOException( "Cannot locate external document no. Lucene docno field name: " + field_docno + ", internal document id: " + docid );
		}
		if ( reuse != null ) {
			reuse.put( docid, docno );
		}
		return this;
	}
	
	/**
	 * If the internal docid has been set, this method will automatically access the index field that stored the document's docno.
	 * 
	 * @param index
	 * @param field_docno
	 * @param reuse
	 *            If not null, it will be used for buffering docid-docno mapping.
	 * @return
	 * @throws IOException
	 */
	public SearchResult setDocno( QueryEnvironment index, String field_docno, Map<Integer, String> reuse ) throws Exception {
		if ( reuse != null && ( docno = reuse.getOrDefault( docid, null ) ) != null ) {
			return this;
		}
		String[] docnos = index.documentMetadata( new int[] { docid }, field_docno );
		if ( docnos == null || docnos.length == 0 ) {
			throw new IOException( "Cannot locate external document no. Indri docno field name: " + field_docno + ", internal document id: " + docid );
		}
		this.docno = docnos[0];
		if ( reuse != null ) {
			reuse.put( docid, docno );
		}
		return this;
	}
	
	/**
	 * @return Relevance score of the result
	 */
	public double getScore() {
		return score;
	}
	
	/**
	 * @param score
	 *            Relevance score of the result
	 * @return
	 */
	public SearchResult setScore( double score ) {
		this.score = score;
		return this;
	}
	
	public KVPair getInfo() {
		return this.info;
	}
	
	public SearchResult setInfo( KVPair info ) {
		this.info = info;
		return this;
	}
	
	/** Default TREC format result separator "Q0". */
	public static final String DEFAULT_TREC_RESULT_SEPARATOR = "Q0";
	
	public String toString() {
		return toString( 0, null );
	}
	
	public String toString( Set<String> paras ) {
		return toString( 0, paras );
	}
	
	public String toString( int rank ) {
		return toString( rank, null );
	}
	
	public String toString( int rank, Set<String> paras ) {
		StringBuilder sb = new StringBuilder();
		sb.append( rank );
		sb.append( "\t" );
		sb.append( docid );
		sb.append( "\t" );
		sb.append( docno );
		sb.append( "\t" );
		sb.append( score );
		if ( paras == null && this.info != null ) {
			paras = this.info.keySet();
		}
		if ( paras != null ) {
			for ( String para : paras ) {
				sb.append( "\t" );
				if ( this.info != null ) {
					sb.append( this.info.get( para ) );
				} else {
					sb.append( "null" );
				}
			}
		}
		return sb.toString();
	}
	
	public void toString( StringBuilder sb, int rank, Set<String> paras ) {
		sb.append( rank );
		sb.append( "\t" );
		sb.append( docid );
		sb.append( "\t" );
		sb.append( docno );
		sb.append( "\t" );
		sb.append( score );
		if ( paras == null && this.info != null ) {
			paras = this.info.keySet();
		}
		if ( paras != null ) {
			for ( String para : paras ) {
				sb.append( "\t" );
				if ( this.info != null ) {
					sb.append( this.info.get( para ) );
				} else {
					sb.append( "null" );
				}
			}
		}
	}
	
	/**
	 * Generate a TREC format result String as follows:
	 * 
	 * <PRE>
	 * 	<qid> <separator> <docno> <rank> <score> <runname>
	 * 	51 $separator clueweb09-enwp00-04-09625 10 -4.522086130068181 QL
	 * </PRE>
	 * 
	 * @param qid
	 * @param runname
	 * @param rk
	 * @param separator
	 * @return A TREC format result line.
	 */
	public String toStringTrecFormat( String qid, String runname, int rk, String separator ) {
		StringBuilder sb = new StringBuilder();
		// sb.append(qid+" "+separator+" "+result.docno()+" "+rank+" "+result.score()+" "+runname);
		sb.append( qid );
		sb.append( " " );
		sb.append( separator );
		sb.append( " " );
		sb.append( getDocno() );
		sb.append( " " );
		sb.append( rk );
		sb.append( " " );
		sb.append( getScore() );
		sb.append( " " );
		sb.append( runname );
		return sb.toString();
	}
	
	/**
	 * Generate a TREC format result String as follows:
	 * 
	 * <PRE>
	 * 	<qid> <separator> <docno> <rank> <score> <runname>
	 * 	51 Q0 clueweb09-enwp00-04-09625 10 -4.522086130068181 QL
	 * </PRE>
	 * 
	 * @param qid
	 * @param runname
	 * @param rk
	 * @return
	 */
	public String toStringTrecFormat( String qid, String runname, int rk ) {
		return toStringTrecFormat( qid, runname, rk, DEFAULT_TREC_RESULT_SEPARATOR );
	}
	
}
