package edu.umass.cs.ciir.controversy.database;

import edu.umass.cs.ciir.controversy.database.dao.RequestDAO;
import edu.umass.cs.ciir.controversy.database.dao.URLRatingDAO;
import edu.umass.cs.ciir.controversy.database.dao.WikiEntryRatingDAO;

/**
 * Recreate all database tables. It will remove all existing tables. Use with caution.
 * 
 * @author Jiepu Jiang
 * @version May 30, 2015
 */
public class RecreateTables {
	
	public static void main( String[] args ) {
		try {
			
			RequestDAO.dropTable();
			URLRatingDAO.dropTable();
			WikiEntryRatingDAO.dropTable();
			
			RequestDAO.createTable();
			URLRatingDAO.createTable();
			WikiEntryRatingDAO.createTable();
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
}
