package utils.ir.dataset.topic.session;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.jsoup.nodes.Document;

import utils.ir.lm.unigram.TreeMapSample;
import utils.ir.lm.unigram.UnigramSample;

/**
 * A result link returned for a query. Each result link includes a url, a title, a snippet, the rank of the result in the result list, the TREC docno of the
 * result, as well as the click information (if available).
 * 
 * @author Jiepu Jiang
 * @version Nov 18, 2014
 */
public class TRECResultSummary {
	
	protected TRECSessionQuery query;
	protected String url;
	protected String docno;
	protected String title;
	protected String snippet;
	protected TRECResultClick click;
	
	protected UnigramSample docsample;
	
	protected String doccontent;
	protected String doccontent_notag;
	protected Document dochtml;
	protected List<String> doctokens;
	protected List<String> doctokens_notag;
	protected List<String> doctokens_index;
	
	/**
	 * The position of the result in the current SERP's result list. It always starts from 0.
	 */
	protected int position;
	
	/**
	 * The rank of the result in the query's result list. The rank of the first result is 1. If the current SERP is not the first page of a query's result, the
	 * top result's rank is not 1 but the rank of the result in the whole result list (e.g. the rank of the top result is 11 if it is the second page and the
	 * first page includes 10 results).
	 */
	protected int rank;
	
	/**
	 * Whether the result was clicked by the user.
	 */
	protected boolean clicked;
	
	/**
	 * Get the query that retrieves this result.
	 */
	public TRECSessionQuery getQuery() {
		return this.query;
	}
	
	/**
	 * Get the URL of the result.
	 */
	public String getURL() {
		return this.url;
	}
	
	/**
	 * Get the docno of the result. Docnos are normalized into uppercase letters.
	 */
	public String getDocno() {
		return this.docno.trim().toLowerCase();
	}
	
	/**
	 * Get the title of the result.
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Get the snippet of the result.
	 */
	public String getSnippet() {
		return this.snippet;
	}
	
	/**
	 * Get the position of the result in the current SERP (starts from 0, do not consider multiple result pages).
	 */
	public int getPosition() {
		return this.position;
	}
	
	/**
	 * Get the rank of the result in the query's whole result list (starts from 1).
	 */
	public int getRank() {
		return this.rank;
	}
	
	public boolean clicked() {
		return this.clicked;
	}
	
	public UnigramSample getDocSample() {
		return this.docsample;
	}
	
	public UnigramSample getSnippetSample( Analyzer analyzer ) throws IOException {
		return getSample( this.snippet, analyzer );
	}
	
	public UnigramSample getTitleSample( Analyzer analyzer ) throws IOException {
		return getSample( this.title, analyzer );
	}
	
	private UnigramSample getSample( String text, Analyzer analyzer ) throws IOException {
		TreeMapSample sample = new TreeMapSample();
		sample.update( text, analyzer );
		sample.setLength();
		return sample;
	}
	
	public TRECResultClick getClickInfo() {
		return this.click;
	}
	
	public String getDocContent() {
		return this.doccontent;
	}
	
	public String getDocContentNotag() {
		return this.doccontent_notag;
	}
	
	public Document getDocHTML() {
		return this.dochtml;
	}
	
	public List<String> getDocTokens() {
		return this.doctokens;
	}
	
	public List<String> getDocTokensNoTag() {
		return this.doctokens_notag;
	}
	
	public List<String> getDocTokensIndex() {
		return this.doctokens_index;
	}
	
	protected int spamrank;
	protected double pagerank;
	protected boolean redirect;
	protected long domainfreq;
	protected List<String> anchors;
	
	public int getSpamrankScore() {
		return this.spamrank;
	}
	
	public double getPagerank() {
		return this.pagerank;
	}
	
	public boolean isRedirect() {
		return this.redirect;
	}
	
	public long getDomainFreq() {
		return this.domainfreq;
	}
	
	public List<String> getAnchors() {
		return this.anchors;
	}
	
}
