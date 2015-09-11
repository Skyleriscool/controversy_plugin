package utils.ir.dataset.topic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;

import java.util.Map;
import java.util.List;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import utils.IOUtils;

/**
 * <p>
 * TRECTopic stores a TREC style adhoc search topic. Typically, a topic will provide title (short query), description (verbose query), and narrative. A search
 * topic can include multiple metadata fields and is stored as a String to String map. The fields and the corresponding keys are:
 * </p>
 * <ul>
 * <li>Topic id: get("id"), get("qid"), or get("topicid")</li>
 * <li>Title (short query): get("ti"), get("title"), or get("query")</li>
 * <li>Description (verbose query): get("desc"), get("description"), or get("des")</li>
 * <li>Narrative: get("narr"), get("narrative"), or get("nar")</li>
 * </ul>
 * </p>
 * 
 * @author Jiepu Jiang
 * @version Feb 11, 2015
 */
public class TRECTopic {
	
	/**
	 * Stores the metadata of the topic. Topic id is stored as "id"; title is stored as "ti"; description is stored as "desc"; narrative is stored as "narr".
	 */
	private Map<String, String> data;
	
	private TRECTopic() {
		data = new TreeMap<String, String>();
	}
	
	/**
	 * Get the topic's metadata value:
	 * <ul>
	 * <li>Topic id: get("id"), get("qid"), or get("topicid")</li>
	 * <li>Title (short query): get("ti"), get("title"), or get("query")</li>
	 * <li>Description (verbose query): get("desc"), get("description"), or get("des")</li>
	 * <li>Narrative: get("narr"), get("narrative"), or get("nar")</li>
	 * </ul>
	 * 
	 * @param key
	 *            A metadata key
	 * @return The requested metadata value
	 */
	public String get( String key ) {
		String value = null;
		if ( key.equalsIgnoreCase( "id" ) || key.equalsIgnoreCase( "qid" ) || key.equalsIgnoreCase( "topicid" ) || key.equalsIgnoreCase( "topic id" ) ) {
			value = data.get( "id" );
		} else if ( key.equalsIgnoreCase( "ti" ) || key.equalsIgnoreCase( "title" ) || key.equalsIgnoreCase( "query" ) ) {
			value = data.get( "ti" );
		} else if ( key.equalsIgnoreCase( "desc" ) || key.equalsIgnoreCase( "des" ) || key.equalsIgnoreCase( "description" ) ) {
			value = data.get( "desc" );
		} else if ( key.equalsIgnoreCase( "narr" ) || key.equalsIgnoreCase( "narrative" ) || key.equalsIgnoreCase( "nar" ) ) {
			value = data.get( "narr" );
		}
		return value;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "Topic No." + data.get( "id" ) + ": " + data.get( "ti" ) + "\n" );
		sb.append( " >> Description:" + data.get( "desc" ) + "\n" );
		sb.append( " >> Narrative:" + data.get( "narr" ) );
		return sb.toString();
	}
	
	/**
	 * Parse old TREC adhoc topics.
	 * 
	 * @param path
	 *            Path of a file that stores topic information
	 * @return A list of topics
	 * @throws IOException
	 */
	public static List<TRECTopic> parseTrecAdhoc( String path ) throws IOException {
		return parseTrecAdhoc( new File( path ) );
	}
	
	/**
	 * Parse old TREC adhoc topics.
	 * 
	 * @param f
	 *            A file that stores topic information
	 * @return A list of topics
	 * @throws IOException
	 */
	public static List<TRECTopic> parseTrecAdhoc( File f ) throws IOException {
		InputStream instream = new FileInputStream( f );
		if ( f.getName().endsWith( ".gz" ) ) {
			instream = new GZIPInputStream( instream );
		}
		List<TRECTopic> topics = parseTrecAdhoc( instream );
		instream.close();
		return topics;
	}
	
	/**
	 * Parse old TREC adhoc topics.
	 * 
	 * @param instream
	 *            An inputstream
	 * @return A list of topics
	 * @throws IOException
	 */
	public static List<TRECTopic> parseTrecAdhoc( InputStream instream ) throws IOException {
		List<TRECTopic> topics = new ArrayList<TRECTopic>();
		BufferedReader reader = IOUtils.getBufferedReader( instream );
		String id = null;
		StringBuilder ti = null;
		StringBuilder des = null;
		StringBuilder nar = null;
		boolean following_ti = false;
		boolean following_des = false;
		boolean following_nar = false;
		String line = reader.readLine();
		while ( line != null ) {
			line = line.trim();
			if ( line.equalsIgnoreCase( "<top>" ) ) {
				id = null;
				ti = null;
				des = null;
				nar = null;
				following_ti = false;
				following_des = false;
				following_nar = false;
			} else if ( line.equals( "</top>" ) ) {
				TRECTopic topic = new TRECTopic();
				if ( id != null && id.matches( "\\d+" ) ) {
					id = Integer.toString( Integer.parseInt( id ) );
				}
				topic.data.put( "id", id );
				topic.data.put( "ti", ti == null ? null : ti.toString().replaceAll( "Topic:", " " ).replaceAll( "\\s+", " " ).trim() );
				topic.data.put( "desc", des == null ? null : des.toString().replaceAll( "Description:", " " ).replaceAll( "\\s+", " " ).trim() );
				topic.data.put( "narr", nar == null ? null : nar.toString().replaceAll( "Narrative:", " " ).replaceAll( "\\s+", " " ).trim() );
				topics.add( topic );
			} else {
				if ( line.matches( "<num>\\s*Number:.*" ) ) {
					id = line.replaceAll( "<num>\\s*Number:", " " ).replaceAll( "\\s+", " " ).trim();
				} else if ( line.startsWith( "<title>" ) ) {
					ti = new StringBuilder();
					following_ti = true;
					following_des = false;
					following_nar = false;
					ti.append( line.replace( "<title>", " " ) + " " );
				} else if ( line.startsWith( "<desc>" ) ) {
					des = new StringBuilder();
					following_ti = false;
					following_des = true;
					following_nar = false;
					des.append( line.replace( "<desc>", " " ) + " " );
				} else if ( line.startsWith( "<narr>" ) ) {
					nar = new StringBuilder();
					following_ti = false;
					following_des = false;
					following_nar = true;
					nar.append( line.replace( "<narr>", " " ) + " " );
				} else {
					if ( following_ti ) {
						if ( !line.startsWith( "<" ) ) {
							ti.append( line + " " );
						} else {
							following_ti = false;
						}
					}
					if ( following_des ) {
						if ( !line.startsWith( "<" ) ) {
							des.append( line + " " );
						} else {
							following_des = false;
						}
					}
					if ( following_nar ) {
						if ( !line.startsWith( "<" ) ) {
							nar.append( line + " " );
						} else {
							following_nar = false;
						}
					}
				}
			}
			line = reader.readLine();
		}
		reader.close();
		return topics;
	}
	
	/**
	 * Parse new TREC web XML format topcis (e.g. trec web 2009 & 2010). Note that in recent years' web track, there's no narratives in topic statements.
	 * 
	 * @param path
	 *            Path of a file that stores topic information
	 * @return A list that stores topics
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static List<TRECTopic> parseTrecWebNew( String path ) throws IOException, ParserConfigurationException, SAXException {
		return parseTrecWebNew( new File( path ) );
	}
	
	/**
	 * Parse new TREC web XML format topcis (e.g. trec web 2009 & 2010). Note that in recent years' web track, there's no narratives in topic statements.
	 * 
	 * @param f
	 *            A file that stores topic information
	 * @return A list that stores topics
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static List<TRECTopic> parseTrecWebNew( File f ) throws IOException, ParserConfigurationException, SAXException {
		InputStream instream = new FileInputStream( f );
		List<TRECTopic> topics = parseTrecWebNew( instream );
		instream.close();
		return topics;
	}
	
	/**
	 * Parse new TREC web XML format topcis (e.g. trec web 2009 & 2010). Note that in recent years' web track, there's no narratives in topic statements.
	 * 
	 * @param instream
	 *            An inputstream
	 * @return A list that stores topics
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static List<TRECTopic> parseTrecWebNew( InputStream instream ) throws IOException, ParserConfigurationException, SAXException {
		List<TRECTopic> topics = new ArrayList<TRECTopic>();
		DocumentBuilder docbuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = docbuild.parse( instream );
		NodeList nodelist = doc.getElementsByTagName( "topic" );
		for ( int ix = 0 ; ix < nodelist.getLength() ; ix++ ) {
			Node nodetopic = nodelist.item( ix );
			NodeList childnodes = nodetopic.getChildNodes();
			String id = nodetopic.getAttributes().getNamedItem( "number" ).getNodeValue();
			String ti = null;
			String des = null;
			for ( int childix = 0 ; childix < childnodes.getLength() ; childix++ ) {
				Node child = childnodes.item( childix );
				if ( child.getNodeName().equalsIgnoreCase( "query" ) ) {
					ti = child.getTextContent().replaceAll( "\\s+", " " ).trim();
				} else if ( child.getNodeName().equalsIgnoreCase( "description" ) ) {
					des = child.getTextContent().replaceAll( "\\s+", " " ).trim();
				}
			}
			TRECTopic topic = new TRECTopic();
			topic.data.put( "id", id );
			topic.data.put( "title", ti );
			topic.data.put( "description", des );
			topics.add( topic );
		}
		return topics;
	}
	
}
