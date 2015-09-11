package edu.umass.cs.ciir.controversy.data;

import java.io.IOException;
import java.io.BufferedReader;
import java.sql.SQLException;

import java.util.Map;
import java.util.List;
import java.util.TreeMap;

import utils.IOUtils;
import edu.umass.cs.ciir.controversy.database.dao.URLRatingDAO;

public class DumpURLRating {
	
	public static void main( String[] args ) {
		try {
			
			String data = combineData( true, true, true );
			System.out.println( data );
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static String combineData( boolean use_shiri, boolean use_myungha, boolean use_database ) throws IOException, SQLException {
		String header = "source\tuser\ttime\turl\tselected text\trating\n";
		return header + ( use_shiri ? generateDataShiri() : "" ) + ( use_myungha ? generateDataMyungha() : "" ) + ( use_database ? generateDataDatabase() : "" );
	}
	
	public static String generateDataShiri() throws IOException {
		
		StringBuilder sb = new StringBuilder();
		
		for ( String path : new String[] { "shiri/judgments-topics-fullTask-2013-05-16.txt", "shiri/judgments-topics-lightTask-2013-05-16.txt" } ) {
			BufferedReader reader = IOUtils.getBufferedReader( new DumpURLRating().getClass().getResourceAsStream( path ) );
			String line = reader.readLine();
			while ( line != null ) {
				String[] splits = line.split( "\t" );
				String url = "http://en.wikipedia.org/wiki/" + splits[0].replaceAll( "\\s+", "_" );
				sb.append( "shiri\tunknown user\tunknown time\t" + url + "\t\t" + splits[1] + "\n" );
				line = reader.readLine();
			}
			reader.close();
		}
		
		Map<String, String> id_url = new TreeMap<String, String>();
		{
			BufferedReader reader = IOUtils.getBufferedReader( new DumpURLRating().getClass().getResourceAsStream( "shiri/page_table-2013-07-14.csv" ) );
			String line = reader.readLine();
			while ( line != null ) {
				String[] splits = line.split( "\t" );
				id_url.put( splits[3], splits[4] );
				line = reader.readLine();
			}
			reader.close();
		}
		
		for ( String path : new String[] { "shiri/judgments-pages-fullTask-2013-05-15.txt", "shiri/judgments-pages-lightTask-2013-05-15.txt" } ) {
			BufferedReader reader = IOUtils.getBufferedReader( new DumpURLRating().getClass().getResourceAsStream( path ) );
			String line = reader.readLine();
			while ( line != null ) {
				String[] splits = line.split( "\t" );
				String url = id_url.get( splits[0] );
				if ( url != null ) {
					sb.append( "shiri\tunknown user\tunknown time\t" + url + "\t\t" + splits[1] + "\n" );
				} else {
					System.out.println( splits[0] );
				}
				line = reader.readLine();
			}
			reader.close();
		}
		
		return sb.toString();
	}
	
	public static String generateDataMyungha() {
		return "";
	}
	
	public static String generateDataDatabase() throws SQLException {
		StringBuilder sb = new StringBuilder();
		List<Object[]> ratings = URLRatingDAO.dumpLastestRatings();
		for ( Object[] rating : ratings ) {
			sb.append( "database\t" + rating[0] + "\t" + rating[1] + "\t" + rating[2] + "\t" + ( (String) rating[4] ).replaceAll( "\\s+", " " ).trim() + "\t" + rating[3] + "\n" );
		}
		return sb.toString();
	}
	
}
