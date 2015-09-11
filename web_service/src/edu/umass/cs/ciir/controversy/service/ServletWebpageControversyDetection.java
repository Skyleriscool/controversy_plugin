package edu.umass.cs.ciir.controversy.service;

import java.util.Map;
import java.net.URLDecoder;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.examples.HtmlToPlainText;

import com.google.gson.Gson;

import utils.crawl.CrawlerUtils;
import edu.umass.cs.ciir.controversy.LuceneWikiIndexSettings;
import edu.umass.cs.ciir.controversy.LuceneWikiScoreSettings;
import edu.umass.cs.ciir.controversy.ControversyScorerSettings;
import edu.umass.cs.ciir.controversy.database.dao.RequestDAO;
import edu.umass.cs.ciir.controversy.database.dao.URLRatingDAO;
import edu.umass.cs.ciir.controversy.database.dao.WikiEntryRatingDAO;
import edu.umass.cs.ciir.controversy.database.entity.Request;
import edu.umass.cs.ciir.controversy.knn.KNNScorer;
import edu.umass.cs.ciir.controversy.knn.ObjectInfo;
import edu.umass.cs.ciir.controversy.knn.aggregation.Generative;
import edu.umass.cs.ciir.controversy.knn.aggregation.KNNAggregation;
import edu.umass.cs.ciir.controversy.knn.db.ControversyDatabase;
import edu.umass.cs.ciir.controversy.knn.db.LuceneControversyDatabase;
import edu.umass.cs.ciir.controversy.knn.sim.LuceneQLSearcher;
import edu.umass.cs.ciir.controversy.knn.sim.LuceneTopWordsQuery;

public class ServletWebpageControversyDetection extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	public ServletWebpageControversyDetection() {
		super();
	}
	
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		doPost( request, response );
	}
	
	private static HtmlToPlainText formatter = new HtmlToPlainText();
	
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		
		LuceneTopWordsQuery qc = new LuceneTopWordsQuery( LuceneWikiIndexSettings.analyzer, LuceneWikiIndexSettings.field_text );
		LuceneQLSearcher searcher = new LuceneQLSearcher( LuceneWikiIndexSettings.path_index, LuceneWikiIndexSettings.smoothing_dir_mu, LuceneWikiIndexSettings.field_key, LuceneWikiIndexSettings.field_title, LuceneWikiIndexSettings.field_text );
		ControversyDatabase db = new LuceneControversyDatabase( LuceneWikiScoreSettings.path_index, LuceneWikiScoreSettings.field_key, LuceneWikiScoreSettings.field_score );
		KNNAggregation aggregation = new Generative();
		KNNScorer scorer = new KNNScorer( qc, searcher, db, aggregation );
		
		long user_request_timestamp = System.currentTimeMillis();
		
		int topwords = ControversyScorerSettings.default_topwords;
		int topentries = ControversyScorerSettings.default_topentries;
		boolean debug = false;
		
		if ( request.getParameter( "topwords" ) != null && request.getParameter( "topwords" ).trim().matches( "\\d+" ) ) {
			topwords = Integer.parseInt( request.getParameter( "topwords" ) );
		}
		if ( request.getParameter( "topentries" ) != null && request.getParameter( "topentries" ).trim().matches( "\\d+" ) ) {
			topentries = Integer.parseInt( request.getParameter( "topentries" ) );
		}
		if ( request.getParameter( "debug" ) != null && request.getParameter( "debug" ).trim().matches( "\\d+" ) && Integer.parseInt( request.getParameter( "debug" ).trim() ) > 0 ) {
			debug = true;
		}
		
		boolean success = false;
		StringBuilder errmsg = new StringBuilder();
		
		String url = request.getParameter( "url" );
		String userid = request.getParameter( "userid" );
		String text = request.getParameter( "text" );
		
		ObjectInfo<Double> score = null;
		
		if ( url != null ) {
			
			try {
				url = URLDecoder.decode( url, "UTF-8" ).trim().toLowerCase();
				if ( !url.startsWith( "http://" ) && !url.startsWith( "https://" ) && !url.startsWith( "ftp://" ) ) {
					url = "http://" + url;
				}
			} catch ( Exception e ) {
				errmsg.append( "Invalid url: " + url + ". " );
				e.printStackTrace();
			}
			
			if ( text == null ) {
				text = "";
			}
			text = StringEscapeUtils.unescapeXml( text );
			text = text.trim();
			
			double fetchtime = 0;
			String webpage = "";
			if ( text.length() == 0 ) {
				try {
					long timestamp = System.currentTimeMillis();
					webpage = CrawlerUtils.getContentString( url, "UTF-8" );
					Document htmldoc = Jsoup.parse( webpage );
					webpage = formatter.getPlainText( htmldoc );
					fetchtime = ( System.currentTimeMillis() - timestamp ) / 1000.0;
				} catch ( Exception e ) {
					e.printStackTrace();
					errmsg.append( "Server cannot get access to the requested url: " + url + ". " );
				}
			}
			
			Request user_request = null;
			Integer prev_rating = null;
			Map<String, Integer> prev_rating_entries = null;
			try {
				StringBuilder param = new StringBuilder();
				boolean first = true;
				Map<String, String[]> keyvalues = request.getParameterMap();
				for ( String key : keyvalues.keySet() ) {
					for ( String value : keyvalues.get( key ) ) {
						if ( !first ) {
							param.append( "&" );
						} else {
							first = false;
						}
						param.append( key + "=" + value );
					}
				}
				user_request = RequestDAO.add( new Request( userid, request.getRemoteAddr(), url, text, webpage, param.toString(), user_request_timestamp ) );
				if ( text.length() == 0 ) {
					// if requesting a trunk of text, ignore previous rating
					prev_rating = URLRatingDAO.searchLastRating( user_request.getUserid(), user_request.getUrl() );
					prev_rating_entries = WikiEntryRatingDAO.searchLastRating( user_request.getUserid(), user_request.getUrl() );
				} else {
					prev_rating = URLRatingDAO.searchLastRating( user_request.getUserid(), user_request.getUrl(), text );
					prev_rating_entries = WikiEntryRatingDAO.searchLastRating( user_request.getUserid(), user_request.getUrl(), text );
				}
			} catch ( SQLException e ) {
				e.printStackTrace();
				errmsg.append( "Server error: cannot log requests into database." );
			}
			
			if ( webpage.length() > 0 || text.length() > 0 ) {
				try {
					score = text.length() > 0 ? scorer.getScore( text, topwords, topentries ) : scorer.getScore( webpage, topwords, topentries );
					if ( text.length() > 0 ) {
						score.setInfo( "request_type", "text" );
					} else {
						score.setInfo( "request_type", "url" );
					}
					score.setInfo( "time_fetch_webpage", fetchtime );
					if ( user_request != null ) {
						user_request.setWebpage( "" ); // do not send crawled webpage information back to the client
						score.setInfo( "user_request", user_request );
					}
					if ( prev_rating != null ) {
						score.setInfo( "prev_rating", prev_rating );
					}
					if ( prev_rating_entries != null ) {
						score.setInfo( "prev_rating_entries", prev_rating_entries );
					}
					success = true;
				} catch ( Exception e ) {
					e.printStackTrace();
					errmsg.append( "Server error: cannot compute controversy score for this webpage." );
				}
			}
			
		} else {
			// generate some error message
			errmsg.append( "Missing request parameter: url. " );
		}
		
		scorer.close();
		
		String json = null;
		if ( !debug ) {
			json = "{\"success\": " + success + ", \"controversy\":" + score.getObject() + ", \"errmsg\":\"" + errmsg.toString() + "\"}";
		} else {
			json = "{\"success\": " + success + ", \"controversy\":" + score.getObject() + ", \"errmsg\":\"" + errmsg.toString() + "\"" + ", \"info\":" + new Gson().toJson( score.getInfo() ) + "}";
		}
		response.setCharacterEncoding( "UTF-8" );
		response.getWriter().write( json );
		
	}
	
}
