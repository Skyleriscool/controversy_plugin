package utils.crawl;

import java.io.*;
import java.net.*;
import java.util.*;

import utils.*;

import com.google.gson.stream.*;

/**
 * Directly request goolge search (webpage) and parse out the results.
 * 
 * <font color="red">I need to check whether this class can extract all the results returned in Google (seems some irregular results are missing).</font>
 * 
 * @author Jiepu Jiang
 * @date Jun 3, 2013
 */
public class GoogleCrawler {
	
	public static void main(String[] args) {
		try{
			
			System.out.println( getAutoCompletion( "china" ) );
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Stores information on a result page, including:
	 * 1. about 10 result items;
	 * 2. the estimated total number of result;
	 * 3. a list of query suggestions (sometimes unavailable).
	 * 
	 * @author Jiepu Jiang
	 * @date Jun 3, 2013
	 */
	public static class ResultPage {
		
		public String total_estimate;
		
		public List<Result> results;
		public List<Suggestion> suggestions;
		
		public ResultPage() {
			this.results = new ArrayList<Result>();
			this.suggestions = new ArrayList<Suggestion>();
		}
		
		public void writeJSON( JsonWriter writer ) throws IOException {
			writeJSON( writer, Integer.MAX_VALUE, Integer.MAX_VALUE );
		}
		
		public void writeJSON( JsonWriter writer, int top_result, int top_suggest ) throws IOException {
			
			writer.beginObject();
			
			writer.name("results");
			writer.beginArray();
			for( int ix=0;ix<results.size()&&ix<top_result;ix++ ) {
				Result result = results.get(ix);
				writer.beginObject();
				writer.name("title").value(result.title);
				writer.name("url").value(result.url);
				writer.name("snippet").value(result.snippet);
				writer.endObject();
			}
			writer.endArray();
			
			writer.name("suggests");
			writer.beginArray();
			for( int ix=0;ix<suggestions.size()&&ix<top_suggest;ix++ ) {
				Suggestion suggestion = suggestions.get(ix);
				writer.beginObject();
				writer.name("query").value(suggestion.query);
				writer.name("query_formatted").value(suggestion.query_formatted);
				writer.endObject();
			}
			writer.endArray();
			
			writer.endObject();
			
		}
		
	}
	
	public static class Result {
		
		public String title;
		public String title_formatted;
		public String url;
		public String url_formatted;
		public String snippet;
		public String snippet_original;
		public String snippet_formatted;
		
		public Result() {
			this.title = "";
			this.title_formatted = "";
			this.url = "";
			this.url_formatted = "";
			this.snippet = "";
			this.snippet_original = "";
			this.snippet_formatted = "";
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("--> "+title);
			sb.append("\n    "+title_formatted);
			sb.append("\n    "+url);
			sb.append("\n    "+url_formatted);
			sb.append("\n    "+snippet);
			sb.append("\n    "+snippet_original);
			sb.append("\n    "+snippet_formatted);
			return sb.toString();
		}
		
	}
	
	public static class Suggestion {
		
		public String query;
		public String query_formatted;
		
		public Suggestion() {
			this.query = "";
			this.query_formatted = "";
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("--> "+query);
			sb.append("\n    "+query_formatted);
			return sb.toString();
		}
		
	}
	
	// http://suggestqueries.google.com/complete/search?client=firefox&q=chia
	public static class AutoCompletion {
		
		public String input;
		public List<String> suggests;
		
		public static AutoCompletion parse( String webpage ) throws IOException {
			
			String[] extracts = StringUtils.extractFirst( webpage, "\\[\"(.*?)\",\\[(.*?)\\]\\]", 1, 2 );
			if( extracts!=null && extracts[0]!=null && extracts[1]!=null ) {
				AutoCompletion auto = new AutoCompletion();
				auto.input = extracts[0];
				extracts[1] = extracts[1] + ",";
				auto.suggests = StringUtils.extract( extracts[1], "\"(.+?)\",", 1 );
				return auto;
			}
			
			return null;
			
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("--> "+input+"\n");
			if( suggests!=null ) {
				for( String suggest:suggests ) {
					sb.append("    "+suggest);
				}
			}
			return sb.toString();
		}
		
	}
	
	public static ResultPage getResults( String webpage, int num ) throws IOException, URISyntaxException, InterruptedException {
		
		ResultPage page = new ResultPage();
		
		{
			page.total_estimate = StringUtils.extractFirst( webpage, "<div id=\"resultStats\">.*?([Aa]bout )?([,\\d]+) result", 2 );
			List<String> matches = StringUtils.extract( webpage, "<h3 class=\"r\">.+?(</div></div></li>|</li><li class=\"g\">)" );
			
			for( String oneresult:matches ) {
				
				if( oneresult.contains( "class=l onmousedown=" ) ) {
					continue;
				}
				
				Result result = new Result();
				
				result.url = StringUtils.extractFirst( oneresult, "<h3 class=\"r\"><a href=\"([^\"]+)\"", 1 );
				result.url_formatted = StringUtils.extractFirst( oneresult, "<div class=\"f kv\".*?><cite.*?>(.+?)</cite>", 1 );
				
				result.title_formatted = StringUtils.extractFirst( oneresult, "<h3 class=\"r\"><a.+?>(.+?)</a>", 1 );
				result.snippet_original = StringUtils.extractFirst( oneresult, "<span class=\"st\">(.+)</span>", 1 );
				
				result.url = result.url==null?"":result.url.trim();
				if( !result.url.startsWith("http://") && !result.url.startsWith("https://") && !result.url.startsWith("ftp://") ) {
					if( result.url.startsWith("/url?") ) {
						result.url = StringUtils.extractFirst( result.url, "q=([^&]+)", 1 );
					}else if( result.url.startsWith("/") ) {
						result.url = "http://www.google.com" + result.url;
					}
					result.url = "http://" + result.url;
				}
				
				result.url_formatted = result.url_formatted==null?"":result.url_formatted.trim();
				result.url_formatted = cleanFormattedText( result.url_formatted );
				
				result.title_formatted = result.title_formatted==null?"":result.title_formatted.trim();
				result.snippet_original = result.snippet_original==null?"":result.snippet_original.trim();
				
				result.title_formatted = cleanFormattedText( result.title_formatted );
				result.snippet_original = cleanFormattedText( result.snippet_original );
				
				result.title = result.title_formatted.replaceAll( "<[^>]+>", " ").replaceAll( "\\s+", " " ).trim();
				result.snippet = result.snippet_original.replaceAll( "<[^>]+>", " ").replaceAll( "\\s+", " " ).trim();
				result.snippet_formatted = result.snippet_original;
				
				page.results.add(result);
				// The following case is to deal with the cases that Google will at least return 10 results.
				// So, if you have a setting for num = 9 (for example), the results returned will still be 10
				if( page.results.size()>=num ) {
					break;
				}
				// System.out.println(result);
				
			}
		}
		
		{
			List<String> matches = StringUtils.extract( webpage, "<div class=\"brs_col\">(.+?)</div>", 1 );
			for( String match:matches ) {
				List<String> qsugs = StringUtils.extract( match, "<p><a .+?>(.+?)</a></p>", 1 );
				for( String qsug:qsugs ) {
					Suggestion sug = new Suggestion();
					sug.query_formatted = qsug;
					sug.query_formatted = sug.query_formatted==null?"":sug.query_formatted.trim();
					sug.query = sug.query_formatted.replaceAll( "<[^>]+>", " ").replaceAll( "\\s+", " " ).trim();
					page.suggestions.add(sug);
					// System.out.println(sug);
				}
			}
		}
		
		return page;
		
	}
	
	public static ResultPage getResults( String q, int start, int num ) throws IOException, URISyntaxException, InterruptedException {
		
		String url = "http://www.google.com/search?hl=en&q=" + URLEncoder.encode( q, "UTF-8" );
		if( start>0 ) {
			url = url + "&start=" + start;
		}
		if( num!=10 ) {
			url = url + "&num=" + num;
		}
		String webpage = CrawlerUtils.getContentStringAutoRetry( false, url, "UTF-8", 0, 0 );
		
		return getResults( webpage, num );
		
	}
	
	public static String cleanFormattedText( String text ) {
		text = text.replaceAll("<em>", "@@@em@@@").replaceAll("</em>", "@@@/em@@@").replaceAll("<b>", "@@@b@@@").replaceAll("</b>", "@@@/b@@@");
		text = text.replaceAll("<[^>]+>", " ");
		text = text.replaceAll("@@@em@@@", "<em>").replaceAll("@@@/em@@@", "</em>").replaceAll("@@@b@@@", "<b>").replaceAll("@@@/b@@@", "</b>");
		return text.replaceAll("\\s+", " ").trim();
	}
	
	public static AutoCompletion getAutoCompletion( String input ) throws URISyntaxException, IOException, InterruptedException {
		
		String url = "http://suggestqueries.google.com/complete/search?client=firefox&q=" + URLEncoder.encode( input, "UTF-8" );
		String webpage = CrawlerUtils.getContentStringAutoRetry( false, url, "UTF-8", 0, 0 );
		
		if( webpage==null || webpage.trim().length()==0 ) {
			return null;
		}
		
		return AutoCompletion.parse( webpage );
		
	}
	
}
