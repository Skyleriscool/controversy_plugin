package utils.crawl;

import java.io.*;
import java.net.*;
import java.util.*;

import utils.*;

import com.google.gson.stream.*;

/**
 * Request speech reconigtion results from Google speech API.
 * 
 * @author Jiepu Jiang
 * @date Jun 3, 2013
 */
public class GoogleSpeechAPI {
	
	/**
	 * A result record.
	 * 
	 * @author Jiepu Jiang
	 * @date Jun 3, 2013
	 */
	public static class Result {
		
		/** Storing the original returned json-format text. */
		public String org_result;
		
		/** Status code. */
		public int status;
		
		/** An id returned for unknown use. */
		public String id;
		
		/** The best candidate returned. */
		public String can_best;
		
		/** Confidence score of the best candidate. */
		public double can_best_confidence;
		
		/** A list of other candidates (it is currently unknown whether they were ranked by confidence scores). */
		public List<String> other_candidates;
		
		/**
		 * Constructor.
		 */
		public Result() {
			this.other_candidates = new ArrayList<String>();
		}
		
		/**
		 * Constructor.
		 * 
		 * @param f_result						A file storing the returned jason-format results.
		 * @throws IOException
		 */
		public Result( File f_result ) throws IOException {
			this();
			InputStream instream = new FileInputStream( f_result );
			byte[] bytes = IOUtils.readBytes( instream );
			instream.close();
			parse( this, new String( bytes, "UTF-8" ) );
		}
		
		/**
		 * Constructor.
		 * 
		 * @param json_text						The jason-format results.
		 * @throws IOException
		 */
		public Result( String json_text ) throws IOException {
			this();
			parse( this, json_text );
		}
		
		/**
		 * Parse a jason-format results.
		 * 
		 * @param result
		 * @param json_text
		 * @return
		 * @throws IOException
		 */
		public static Result parse( Result result, String json_text ) throws IOException {
			
			JsonReader json = new JsonReader( new StringReader( json_text ) );
			
			result.org_result = json_text;
			
			json.beginObject();
			
			if( json.hasNext() ) {
				
				json.nextName();
				result.status = json.nextInt();
				
				if( json.hasNext() ) {
					
					json.nextName();
					result.id = json.nextString();
					
					if( json.hasNext() ) {
						
						json.nextName(); // hypotheses
						json.beginArray();
						
						if( json.hasNext() ) {
							json.beginObject();
							json.nextName();
							result.can_best = json.nextString();
							json.nextName();
							result.can_best_confidence = json.nextDouble();
							json.endObject();
						}
						
						while( json.hasNext() ) {
							json.beginObject();
							json.nextName();
							result.other_candidates.add( json.nextString() );
							json.endObject();
						}
						
					}
					
				}
				
			}
			
			json.close();
			
			return result;
			
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append( "status: "+status+"\n" );
			sb.append( "id: "+id+"\n" );
			sb.append( "best candidate: "+can_best+", confidence: "+can_best_confidence+"\n" );
			for( int ix=0;ix<other_candidates.size();ix++ ) {
				sb.append( "other candidate["+ix+"]: "+other_candidates.get(ix)+"\n" );
			}
			return sb.toString();
		}
		
	}
	
	/**
	 * Request speech recognition candidates from Google.
	 * 
	 * @param path_audio						The path of the file storing the audio.
	 * @param audio_rate						Sampling rate of the audio.
	 * @param max_candidate						The maximum candidate to return (currently the maximum can be returned is 30).
	 * @return									The json-format result.
	 * @throws IOException
	 */
	public static String request( String path_audio, String audio_rate, int max_candidate ) throws IOException {
		return request( new File( path_audio ), audio_rate, max_candidate );
	}
	
	/**
	 * Request speech recognition candidates from Google.
	 * 
	 * @param f_audio							A file storing the audio.
	 * @param audio_rate						Sampling rate of the audio.
	 * @param max_candidate						The maximum candidate to return (currently the maximum can be returned is 30).
	 * @return									The json-format result.
	 * @throws IOException
	 */
	public static String request( File f_audio, String audio_rate, int max_candidate ) throws IOException {
		FileInputStream instream = new FileInputStream( f_audio );
		String result = request( instream, audio_rate, max_candidate );
		instream.close();
		return result;
	}
	
	/**
	 * Request speech recognition candidates from Google.
	 * 
	 * @param instream							An inputstream for the audio.
	 * @param audio_rate						Sampling rate of the audio.
	 * @param max_candidate						The maximum candidate to return (currently the maximum can be returned is 30).
	 * @return									The json-format result.
	 * @throws IOException
	 */
	public static String request( InputStream instream, String audio_rate, int max_candidate ) throws IOException {
		
		byte[] bytes = IOUtils.readBytes( instream );
		
		if( max_candidate<=0 || max_candidate>30 ) {
			max_candidate = 30;
		}
		
		URL url = new URL( "http", "www.google.com", "/speech-api/v1/recognize?xjerr=1&client=chromium&lang=en-US&maxresults=" + max_candidate );
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		int contentLength = bytes.length;
		
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setUseCaches(false);
		
		conn.setRequestMethod("POST");
		conn.setRequestProperty( "Content-Type", "audio/x-flac; rate=" + audio_rate );
		conn.setRequestProperty( "Content-Length", Integer.toString(contentLength) );
		
		conn.getOutputStream().write( bytes );
		conn.getOutputStream().flush();
		
		InputStream in_returned = conn.getInputStream();
		byte[] bytes_returned = IOUtils.readBytes( in_returned );
		in_returned.close();
		
		return new String( bytes_returned, "UTF-8" );
		
	}
	
}
