package edu.umass.cs.ciir.controversy.data;

import java.io.IOException;
import java.io.BufferedReader;
import java.sql.SQLException;
import java.util.List;

import utils.IOUtils;
import edu.umass.cs.ciir.controversy.database.dao.WikiEntryRatingDAO;

public class DumpWebpageRelevance {
	
	public static void main( String[] args ) {
		try {
			
			String data = combineData( true, true, true );
			System.out.println( data );
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static String combineData( boolean use_shiri, boolean use_myungha, boolean use_database ) throws IOException, SQLException {
		String header = "source\tuser\ttime\twiki entry\turl\tselected text\trating\n";
		return header + ( use_shiri ? generateDataShiri() : "" ) + ( use_myungha ? generateDataMyungha() : "" ) + ( use_database ? generateDataDatabase() : "" );
	}
	
	public static String generateDataShiri() throws IOException {
		return "";
	}
	
	public static String generateDataMyungha() throws IOException {
		
		StringBuilder sb = new StringBuilder();
		
		BufferedReader reader = IOUtils.getBufferedReader( new DumpWebpageRelevance().getClass().getResourceAsStream( "myungha/wikipedia_relevance_06062015.csv" ) );
		String line = reader.readLine();
		while ( line != null ) {
			String[] splits = line.split( "," );
			String user = splits[0];
			String url = splits[1];
			String entry = splits[2].replaceAll( "_", " " );
			String rating = splits[3];
			sb.append( "myungha\t" + user + "\tunknown time\t" + entry + "\t" + url + "\t\t" + rating + "\n" );
			line = reader.readLine();
		}
		reader.close();
		return sb.toString();
	}
	
	public static String generateDataDatabase() throws SQLException, IOException {
		// ratings.add( new Object[] { user, Settings.TIME_FORMAT_MS.format( new Date( timestamp ) ), url, text, entry, rating } );
		StringBuilder sb = new StringBuilder();
		List<Object[]> ratings = WikiEntryRatingDAO.dumpLastestRatings();
		for ( Object[] rating : ratings ) {
			sb.append( "database\t" + rating[0] + "\t" + rating[1] + "\t" + rating[4] + "\t" + rating[2] + "\t" + ( (String) rating[3] ).replaceAll( "\\s+", " " ) + "\t" + rating[5] + "\n" );
		}
		return sb.toString();
	}
	
}
