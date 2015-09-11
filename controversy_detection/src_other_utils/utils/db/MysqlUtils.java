package utils.db;

import java.sql.*;
import java.util.*;

import utils.KVPair;

/**
 * Utilities related to operating MySQL database.
 * 
 * @author Jiepu Jiang
 * @version Mar 1, 2013
 */
@SuppressWarnings( { "unchecked", "rawtypes" } )
public class MysqlUtils {
	
	public static final int DEFAULT_PORT = 3306;
	
	/**
	 * An enum for the supported MySQL data types.
	 */
	public static enum DataType {
		String, Integer, Long, Float, Double
	}
	
	/**
	 * Get a connection to a MySQL database.
	 * 
	 * @param host
	 * @param port
	 * @param scheme
	 * @param user
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection( String host, int port, String scheme, String user, String password ) throws SQLException {
		String connUrl = "jdbc:mysql://" + host + ":" + port + "/" + scheme + "?useUnicode=true&characterEncoding=UTF-8";
		DriverManager.setLoginTimeout( 0 );
		return DriverManager.getConnection( connUrl, user, password );
	}
	
	/**
	 * Get a connection to a MySQL database using default port.
	 * 
	 * @param host
	 * @param scheme
	 * @param user
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection( String host, String scheme, String user, String password ) throws SQLException {
		return getConnection( host, DEFAULT_PORT, scheme, user, password );
	}
	
	/**
	 * Get a connection to a MySQL database in the default scheme.
	 * 
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection( String host, int port, String user, String password ) throws SQLException {
		return getConnection( host, port, "", user, password );
	}
	
	/**
	 * Get a connection to a MySQL database using default port in the default scheme.
	 * 
	 * @param host
	 * @param user
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection( String host, String user, String password ) throws SQLException {
		return getConnection( host, DEFAULT_PORT, "", user, password );
	}
	
	/**
	 * Command the sql statement instance to use the specified database as default scheme.
	 * 
	 * @param st
	 * @param database
	 * @throws SQLException
	 */
	public static void useDatabase( Statement st, String database ) throws SQLException {
		st.execute( "USE `" + database + "`;" );
	}
	
	/**
	 * Get a set of databases (schemes) available in the current mysql server connection.
	 * 
	 * @param st
	 * @return
	 * @throws SQLException
	 */
	public static Set<String> getDatabases( Statement st ) throws SQLException {
		Set<String> set = new TreeSet<String>();
		ResultSet rs = st.executeQuery( "SHOW DATABASES;" );
		while ( rs.next() ) {
			set.add( rs.getString( 1 ) );
		}
		rs.close();
		return set;
	}
	
	/**
	 * Get a set of tables available in the specified databse.
	 * 
	 * @param st
	 * @param database
	 * @return
	 * @throws SQLException
	 */
	public static Set<String> getTables( Statement st, String database ) throws SQLException {
		String sql = "SHOW TABLES";
		if ( database != null ) {
			sql = sql + " FROM " + database;
		}
		sql = sql + ";";
		Set<String> tables = new TreeSet<String>();
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			tables.add( rs.getString( 1 ) );
		}
		rs.close();
		return tables;
	}
	
	/**
	 * Get a set of columns in the specified database table.
	 * 
	 * @param st
	 * @param database
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public static Set<String> getColumns( Statement st, String database, String table ) throws SQLException {
		String sql = "SHOW COLUMNS FROM " + table;
		if ( database != null ) {
			sql = sql + " FROM " + database;
		}
		Set<String> columns = new TreeSet<String>();
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			columns.add( rs.getString( 1 ) );
		}
		rs.close();
		return columns;
	}
	
	/**
	 * Get a columne from current record as the specified data type.
	 * 
	 * @param rs
	 * @param colix
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public static Object getData( ResultSet rs, int colix, DataType type ) throws SQLException {
		Object data = null;
		switch ( type ) {
			case Double:
				data = rs.getDouble( colix );
				break;
			case Float:
				data = rs.getFloat( colix );
				break;
			case Integer:
				data = rs.getInt( colix );
				break;
			case Long:
				data = rs.getLong( colix );
				break;
			case String:
				data = rs.getString( colix );
				break;
		}
		return data;
	}
	
	/**
	 * Get a columne from current record as the specified data type.
	 * 
	 * @param rs
	 * @param colname
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public static Object getData( ResultSet rs, String colname, DataType type ) throws SQLException {
		Object data = null;
		switch ( type ) {
			case Double:
				data = rs.getDouble( colname );
				break;
			case Float:
				data = rs.getFloat( colname );
				break;
			case Integer:
				data = rs.getInt( colname );
				break;
			case Long:
				data = rs.getLong( colname );
				break;
			case String:
				data = rs.getString( colname );
				break;
		}
		return data;
	}
	
	/**
	 * Store all data from the specified column into the provided collection object.
	 * 
	 * @param st
	 * @param sql
	 * @param colix
	 * @param type
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataOneColumn( Statement st, String sql, int colix, DataType type, Collection data ) throws SQLException {
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			Object obj = getData( rs, colix, type );
			if ( obj != null ) {
				data.add( obj );
			}
		}
		rs.close();
	}
	
	/**
	 * Store all data from the specified column into the provided collection object.
	 * 
	 * @param st
	 * @param sql
	 * @param colix
	 * @param type
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataOneColumn( Statement st, String sql, String colname, DataType type, Collection data ) throws SQLException {
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			Object obj = getData( rs, colname, type );
			if ( obj != null ) {
				data.add( obj );
			}
		}
		rs.close();
	}
	
	/**
	 * Store all data from the first column (by default) into the provided collection object.
	 * 
	 * @param st
	 * @param sql
	 * @param colix
	 * @param type
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataOneColumn( Statement st, String sql, DataType type, Collection data ) throws SQLException {
		getDataOneColumn( st, sql, 1, type, data );
	}
	
	/**
	 * Store all data from the specified two columns into a data map (col1 is key; col2 is value).
	 * 
	 * @param st
	 * @param sql
	 * @param col1
	 * @param col2
	 * @param type1
	 * @param type2
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataTwoColumnMap( Statement st, String sql, int col1, int col2, DataType type1, DataType type2, Map data ) throws SQLException {
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			Object obj1 = getData( rs, col1, type1 );
			Object obj2 = getData( rs, col2, type2 );
			if ( obj1 != null && obj2 != null ) {
				data.put( obj1, obj2 );
			}
		}
		rs.close();
	}
	
	/**
	 * Store all data from the specified two columns into a data map (col1 is key; col2 is value).
	 * 
	 * @param st
	 * @param sql
	 * @param col1
	 * @param col2
	 * @param type1
	 * @param type2
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataTwoColumnMap( Statement st, String sql, String col1, String col2, DataType type1, DataType type2, Map data ) throws SQLException {
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			Object obj1 = getData( rs, col1, type1 );
			Object obj2 = getData( rs, col2, type2 );
			if ( obj1 != null && obj2 != null ) {
				data.put( obj1, obj2 );
			}
		}
		rs.close();
	}
	
	/**
	 * Store all data from the first two columns into a data map (1st column is key; 2nd column is value).
	 * 
	 * @param st
	 * @param sql
	 * @param type1
	 * @param type2
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataTwoColumnMap( Statement st, String sql, DataType type1, DataType type2, Map data ) throws SQLException {
		getDataTwoColumnMap( st, sql, 1, 2, type1, type2, data );
	}
	
	/**
	 * Store all data from the specified two columns into a data map whose value is stored as a list so that the key and value can be 1:m relation (col1 is key;
	 * col2 is value).
	 * 
	 * @param st
	 * @param sql
	 * @param col1
	 * @param col2
	 * @param type1
	 * @param type2
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataTwoColumnMapList( Statement st, String sql, int col1, int col2, DataType type1, DataType type2, Map data ) throws SQLException {
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			Object obj1 = getData( rs, col1, type1 );
			Object obj2 = getData( rs, col2, type2 );
			if ( obj1 != null && obj2 != null ) {
				if ( !data.containsKey( obj1 ) ) {
					data.put( obj1, new ArrayList() );
				}
				( (ArrayList) ( data.get( obj1 ) ) ).add( obj2 );
			}
		}
		rs.close();
	}
	
	/**
	 * Store all data from the specified two columns into a data map whose value is stored as a list so that the key and value can be 1:m relation (col1 is key;
	 * col2 is value).
	 * 
	 * @param st
	 * @param sql
	 * @param col1
	 * @param col2
	 * @param type1
	 * @param type2
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataTwoColumnMapList( Statement st, String sql, String col1, String col2, DataType type1, DataType type2, Map data ) throws SQLException {
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			Object obj1 = getData( rs, col1, type1 );
			Object obj2 = getData( rs, col2, type2 );
			if ( obj1 != null && obj2 != null ) {
				if ( !data.containsKey( obj1 ) ) {
					data.put( obj1, new ArrayList() );
				}
				( (ArrayList) ( data.get( obj1 ) ) ).add( obj2 );
			}
		}
		rs.close();
	}
	
	/**
	 * Store all data from the first two columns into a data map whose value is stored as a list so that the key and value can be 1:m relation (1st is key; 2nd
	 * is value).
	 * 
	 * @param st
	 * @param sql
	 * @param type1
	 * @param type2
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataTwoColumnMapList( Statement st, String sql, DataType type1, DataType type2, Map data ) throws SQLException {
		getDataTwoColumnMapList( st, sql, 1, 2, type1, type2, data );
	}
	
	/**
	 * Store all data from the specified two columns into a data map whose value is stored as a set so that the key and value can be 1:m relation (col1 is key;
	 * col2 is value).
	 * 
	 * @param st
	 * @param sql
	 * @param col1
	 * @param col2
	 * @param type1
	 * @param type2
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataTwoColumnMapSet( Statement st, String sql, int col1, int col2, DataType type1, DataType type2, Map data ) throws SQLException {
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			Object obj1 = getData( rs, col1, type1 );
			Object obj2 = getData( rs, col2, type2 );
			if ( obj1 != null && obj2 != null ) {
				if ( !data.containsKey( obj1 ) ) {
					data.put( obj1, new TreeSet() );
				}
				( (TreeSet) ( data.get( obj1 ) ) ).add( obj2 );
			}
		}
		rs.close();
	}
	
	/**
	 * Store all data from the specified two columns into a data map whose value is stored as a set so that the key and value can be 1:m relation (col1 is key;
	 * col2 is value).
	 * 
	 * @param st
	 * @param sql
	 * @param col1
	 * @param col2
	 * @param type1
	 * @param type2
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataTwoColumnMapSet( Statement st, String sql, String col1, String col2, DataType type1, DataType type2, Map data ) throws SQLException {
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			Object obj1 = getData( rs, col1, type1 );
			Object obj2 = getData( rs, col2, type2 );
			if ( obj1 != null && obj2 != null ) {
				if ( !data.containsKey( obj1 ) ) {
					data.put( obj1, new TreeSet() );
				}
				( (TreeSet) ( data.get( obj1 ) ) ).add( obj2 );
			}
		}
		rs.close();
	}
	
	/**
	 * Store all data from the first two columns into a data map whose value is stored as a set so that the key and value can be 1:m relation (1st col is key;
	 * 2nd col is value).
	 * 
	 * @param st
	 * @param sql
	 * @param type1
	 * @param type2
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataTwoColumnMapSet( Statement st, String sql, DataType type1, DataType type2, Map data ) throws SQLException {
		getDataTwoColumnMapSet( st, sql, 1, 2, type1, type2, data );
	}
	
	/**
	 * Store all data from the specified three columns into a data map (key is col1) whose value is stored as a map (key is col2; value is col3).
	 * 
	 * @param st
	 * @param sql
	 * @param col1
	 * @param col2
	 * @param col3
	 * @param type1
	 * @param type2
	 * @param type3
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataThreeColumnMapMap( Statement st, String sql, int col1, int col2, int col3, DataType type1, DataType type2, DataType type3, Map data ) throws SQLException {
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			Object obj1 = getData( rs, col1, type1 );
			Object obj2 = getData( rs, col2, type2 );
			Object obj3 = getData( rs, col3, type3 );
			if ( obj1 != null && obj2 != null && obj3 != null ) {
				if ( !data.containsKey( obj1 ) ) {
					data.put( obj1, new TreeMap() );
				}
				( (TreeMap) ( data.get( obj1 ) ) ).put( obj2, obj3 );
			}
		}
		rs.close();
	}
	
	/**
	 * Store all data from the specified three columns into a data map (key is col1) whose value is stored as a map (key is col2; value is col3).
	 * 
	 * @param st
	 * @param sql
	 * @param col1
	 * @param col2
	 * @param col3
	 * @param type1
	 * @param type2
	 * @param type3
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataThreeColumnMapMap( Statement st, String sql, String col1, String col2, String col3, DataType type1, DataType type2, DataType type3, Map data ) throws SQLException {
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			Object obj1 = getData( rs, col1, type1 );
			Object obj2 = getData( rs, col2, type2 );
			Object obj3 = getData( rs, col3, type3 );
			if ( obj1 != null && obj2 != null && obj3 != null ) {
				if ( !data.containsKey( obj1 ) ) {
					data.put( obj1, new TreeMap() );
				}
				( (TreeMap) ( data.get( obj1 ) ) ).put( obj2, obj3 );
			}
		}
		rs.close();
	}
	
	/**
	 * Store all data from the first three columns into a data map (key is 1st col) whose value is stored as a map (key is 2nd col; value is 3rd col).
	 * 
	 * @param st
	 * @param sql
	 * @param type1
	 * @param type2
	 * @param type3
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataThreeColumnMapMap( Statement st, String sql, DataType type1, DataType type2, DataType type3, Map data ) throws SQLException {
		getDataThreeColumnMapMap( st, sql, 1, 2, 3, type1, type2, type3, data );
	}
	
	/**
	 * Store all data from the specified three columns into a data map (key is col1) whose value is stored as a map (key is col2; value is a set of col3).
	 * 
	 * @param st
	 * @param sql
	 * @param col1
	 * @param col2
	 * @param col3
	 * @param type1
	 * @param type2
	 * @param type3
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataThreeColumnMapMapSet( Statement st, String sql, int col1, int col2, int col3, DataType type1, DataType type2, DataType type3, Map data ) throws SQLException {
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			Object obj1 = getData( rs, col1, type1 );
			Object obj2 = getData( rs, col2, type2 );
			Object obj3 = getData( rs, col3, type3 );
			if ( obj1 != null && obj2 != null && obj3 != null ) {
				if ( !data.containsKey( obj1 ) ) {
					data.put( obj1, new TreeMap() );
				}
				if ( ! ( (TreeMap) ( data.get( obj1 ) ) ).containsKey( obj2 ) ) {
					( (TreeMap) ( data.get( obj1 ) ) ).put( obj2, new TreeSet() );
				}
				( (TreeSet) ( ( (TreeMap) ( data.get( obj1 ) ) ).get( obj2 ) ) ).add( obj3 );
			}
		}
		rs.close();
	}
	
	/**
	 * Store all data from the specified three columns into a data map (key is col1) whose value is stored as a map (key is col2; value is a set of col3).
	 * 
	 * @param st
	 * @param sql
	 * @param col1
	 * @param col2
	 * @param col3
	 * @param type1
	 * @param type2
	 * @param type3
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataThreeColumnMapMapSet( Statement st, String sql, String col1, String col2, String col3, DataType type1, DataType type2, DataType type3, Map data ) throws SQLException {
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			Object obj1 = getData( rs, col1, type1 );
			Object obj2 = getData( rs, col2, type2 );
			Object obj3 = getData( rs, col3, type3 );
			if ( obj1 != null && obj2 != null && obj3 != null ) {
				if ( !data.containsKey( obj1 ) ) {
					data.put( obj1, new TreeMap() );
				}
				if ( ! ( (TreeMap) ( data.get( obj1 ) ) ).containsKey( obj2 ) ) {
					( (TreeMap) ( data.get( obj1 ) ) ).put( obj2, new TreeSet() );
				}
				( (TreeSet) ( ( (TreeMap) ( data.get( obj1 ) ) ).get( obj2 ) ) ).add( obj3 );
			}
		}
		rs.close();
	}
	
	/**
	 * Store all data from the specified three columns into a data map (key is col1) whose value is stored as a map (key is col2; value is a set of col3).
	 * 
	 * @param st
	 * @param sql
	 * @param type1
	 * @param type2
	 * @param type3
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataThreeColumnMapMapSet( Statement st, String sql, DataType type1, DataType type2, DataType type3, Map data ) throws SQLException {
		getDataThreeColumnMapMapSet( st, sql, 1, 2, 3, type1, type2, type3, data );
	}
	
	/**
	 * Store all data from the specified three columns into a data map (key is col1) whose value is stored as a map (key is col2; value is a set of col3).
	 * 
	 * @param st
	 * @param sql
	 * @param col1
	 * @param col2
	 * @param col3
	 * @param type1
	 * @param type2
	 * @param type3
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataThreeColumnMapMapList( Statement st, String sql, int col1, int col2, int col3, DataType type1, DataType type2, DataType type3, Map data ) throws SQLException {
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			Object obj1 = getData( rs, col1, type1 );
			Object obj2 = getData( rs, col2, type2 );
			Object obj3 = getData( rs, col3, type3 );
			if ( obj1 != null && obj2 != null && obj3 != null ) {
				if ( !data.containsKey( obj1 ) ) {
					data.put( obj1, new TreeMap() );
				}
				if ( ! ( (TreeMap) ( data.get( obj1 ) ) ).containsKey( obj2 ) ) {
					( (TreeMap) ( data.get( obj1 ) ) ).put( obj2, new ArrayList() );
				}
				( (ArrayList) ( ( (TreeMap) ( data.get( obj1 ) ) ).get( obj2 ) ) ).add( obj3 );
			}
		}
		rs.close();
	}
	
	/**
	 * Store all data from the specified three columns into a data map (key is col1) whose value is stored as a map (key is col2; value is a set of col3).
	 * 
	 * @param st
	 * @param sql
	 * @param col1
	 * @param col2
	 * @param col3
	 * @param type1
	 * @param type2
	 * @param type3
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataThreeColumnMapMapList( Statement st, String sql, String col1, String col2, String col3, DataType type1, DataType type2, DataType type3, Map data ) throws SQLException {
		ResultSet rs = st.executeQuery( sql );
		while ( rs.next() ) {
			Object obj1 = getData( rs, col1, type1 );
			Object obj2 = getData( rs, col2, type2 );
			Object obj3 = getData( rs, col3, type3 );
			if ( obj1 != null && obj2 != null && obj3 != null ) {
				if ( !data.containsKey( obj1 ) ) {
					data.put( obj1, new TreeMap() );
				}
				if ( ! ( (TreeMap) ( data.get( obj1 ) ) ).containsKey( obj2 ) ) {
					( (TreeMap) ( data.get( obj1 ) ) ).put( obj2, new ArrayList() );
				}
				( (ArrayList) ( ( (TreeMap) ( data.get( obj1 ) ) ).get( obj2 ) ) ).add( obj3 );
			}
		}
		rs.close();
	}
	
	/**
	 * Store all data from the specified three columns into a data map (key is col1) whose value is stored as a map (key is col2; value is a set of col3).
	 * 
	 * @param st
	 * @param sql
	 * @param type1
	 * @param type2
	 * @param type3
	 * @param data
	 * @throws SQLException
	 */
	public static void getDataThreeColumnMapMapList( Statement st, String sql, DataType type1, DataType type2, DataType type3, Map data ) throws SQLException {
		getDataThreeColumnMapMapList( st, sql, 1, 2, 3, type1, type2, type3, data );
	}
	
	public static List<KVPair> loadTableAsList( Statement st, String table ) throws SQLException {
		
		Map<String, String> field_type = new TreeMap<String, String>();
		{
			ResultSet rs = st.executeQuery( "DESC " + table );
			while ( rs.next() ) {
				String field = rs.getString( "Field" );
				String type = rs.getString( "Type" );
				field_type.put( field, type );
			}
			rs.close();
		}
		
		List<KVPair> list = new ArrayList<KVPair>();
		{
			ResultSet rs = st.executeQuery( "SELECT * FROM " + table );
			while ( rs.next() ) {
				KVPair record = new KVPair();
				for ( String field : field_type.keySet() ) {
					String type = field_type.get( field );
					if ( isTypeInteger( type ) ) {
						record.put( field, rs.getInt( field ) );
					} else if ( isTypeDouble( type ) ) {
						record.put( field, rs.getDouble( field ) );
					} else if ( isTypeString( type ) ) {
						record.put( field, rs.getString( field ) );
					}
				}
				list.add( record );
			}
			rs.close();
		}
		return list;
		
	}
	
	public static boolean isTypeInteger( String type ) {
		type = type.toLowerCase();
		return type.startsWith( "int" ) || type.startsWith( "bigint" );
	}
	
	public static boolean isTypeDouble( String type ) {
		type = type.toLowerCase();
		return type.contains( "double" ) || type.contains( "decimal" );
	}
	
	public static boolean isTypeString( String type ) {
		type = type.toLowerCase();
		return type.startsWith( "varchar" ) || type.startsWith( "char" ) || type.contains( "text" );
	}
	
}
