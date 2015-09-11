package utils;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * <p>
 * Sheet file is a simple implementation of spreadsheet file, in which, 
 * each row stores a record and each column is separated by the "same" 
 * characters (or the characters can be recognized by the same pattern).
 * It supports input & output of sheet data by row and by column.
 * </p>
 * <p>
 * Note that we will use java's split to separate each column's data, 
 * which is a little bit tricky sometimes. Note that if the text ends with
 * the separator, the separator at the end of the text will be excluded
 * from the splited groups. For example, spliting "a\t\tb\t\t" by "\t" 
 * will result in only three splitted groups of texts: { "a", "", "b" }, 
 * instead of { "a", "", "b", "", "" }.
 * </p>
 * 
 * @author Jiepu Jiang
 * @date Sep 7, 2013
 */
public class SheetFile {
	
	protected Pattern separator;
	protected BufferedReader reader;
	protected Map<String,Integer> colname_index;
	protected Map<Integer,String> colindex_name;
	
	private SheetFile() {
		this.colindex_name = new TreeMap<Integer,String>();
		this.colname_index = new TreeMap<String,Integer>();
	}
	
	/**
	 * Constructor.
	 * You can specify the mapping between columns' ids and names by yourself.
	 * In such case, the constructor will not attemp to read colname names from the first row. 
	 * 
	 * @param reader
	 * @param separator
	 * @param colname_index
	 * @param colindex_name
	 */
	public SheetFile( BufferedReader reader, Pattern separator, Map<String,Integer> colname_index, Map<Integer,String> colindex_name ) {
		this.reader = reader;
		this.separator = separator;
		this.colindex_name = colindex_name;
		this.colname_index = colname_index;
	}
	
	/**
	 * Constructor.
	 * You can specify the mapping between columns' ids and names by yourself.
	 * In such case, the constructor will not attemp to read colname names from the first row. 
	 * 
	 * @param reader
	 * @param regex
	 * @param colname_index
	 * @param colindex_name
	 */
	public SheetFile( BufferedReader reader, String regex, Map<String,Integer> colname_index, Map<Integer,String> colindex_name ) {
		this( reader, Pattern.compile(regex), colname_index, colindex_name );
	}
	
	/**
	 * Constructor. You can specify the name of the columns by yourself.
	 * In such case, the constructor will not attemp to read colname names from the first row. 
	 * 
	 * @param reader
	 * @param separator
	 * @param colnames
	 */
	public SheetFile( BufferedReader reader, Pattern separator, String[] colnames ) {
		this();
		this.reader = reader;
		this.separator = separator;
		for( int ix=0;ix<colnames.length;ix++ ) {
			this.colindex_name.put(ix, colnames[ix]);
			this.colname_index.put(colnames[ix], ix);
		}
	}
	
	/**
	 * Constructor. You can specify the name of the columns by yourself.
	 * In such case, the constructor will not attemp to read colname names from the first row.
	 * 
	 * @param reader
	 * @param regex
	 * @param colnames
	 */
	public SheetFile( BufferedReader reader, String regex, String[] colnames ) {
		this( reader, Pattern.compile(regex), colnames );
	}
	
	/**
	 * Constructor. It will attempt to read the name of the colnames from the first row.
	 * 
	 * @param reader
	 * @param separator
	 */
	public SheetFile( BufferedReader reader, Pattern separator ) throws IOException {
		this( reader, separator, separator.split(reader.readLine()) );
	}
	
	/**
	 * Constructor. It will attempt to read the name of the colnames from the first row.
	 * 
	 * @param reader
	 * @param regex
	 * @throws IOException
	 */
	public SheetFile( BufferedReader reader, String regex ) throws IOException {
		this( reader, Pattern.compile(regex), reader.readLine().split(regex) );
	}
	
	/**
	 * Constructor by specifying mapping of columns' ids and names by yourself. By default, the separator is '\t'.
	 * 
	 * @param reader
	 * @param colname_index
	 * @param colindex_name
	 */
	public SheetFile( BufferedReader reader, Map<String,Integer> colname_index, Map<Integer,String> colindex_name ) {
		this( reader, "\t", colname_index, colindex_name );
	}
	
	/**
	 * Constructor by specifying columns' names by yourself. By default, the separator is '\t'.
	 * 
	 * @param reader
	 * @param colnames
	 */
	public SheetFile( BufferedReader reader, String[] colnames ) {
		this( reader, "\t", colnames );
	}
	
	/**
	 * Constructor. By default, the separator is '\t'.
	 * It will attempt to read the name of the columns from the first line.
	 * 
	 * @param reader
	 * @throws IOException
	 */
	public SheetFile( BufferedReader reader ) throws IOException {
		this( reader, "\t" );
	}
	
	/**
	 * A record of the sheet (a non-empty row).
	 */
	public static class Record {
		
		protected String[] data;
		protected SheetFile meta;
		
		/**
		 * Constructor based on the SheetFile object and a line of record data.
		 * 
		 * @param meta
		 * @param linedata
		 */
		public Record( SheetFile meta, String linedata ) {
			this.meta = meta;
			this.data = new String[meta.colname_index.size()];
			String[] splits = meta.separator.split(linedata);
			for( int ix=0;ix<this.data.length;ix++ ) {
				if( ix<splits.length ) {
					this.data[ix] = splits[ix];
				}else{
					this.data[ix] = "";
				}
			}
		}
		
		/**
		 * Get the specified column's data as String.
		 * 
		 * @param colname
		 * @return
		 */
		public String getString( String colname ) {
			return data[meta.colname_index.get(colname)];
		}
		
		/**
		 * Get the specified column's data as String.
		 * 
		 * @param colname
		 * @return
		 */
		public String getString( int colindex ) {
			return data[colindex];
		}
		
		/**
		 * Get the specified column's data as an integer.
		 * 
		 * @param colname
		 * @return
		 */
		public int getInteger( String colname ) {
			return Integer.parseInt(getString(colname));
		}
		
		/**
		 * Get the specified column's data as an integer.
		 * 
		 * @param colname
		 * @return
		 */
		public int getInteger( int colindex ) {
			return Integer.parseInt(getString(colindex));
		}
		
		/**
		 * Get the specified column's data as a double.
		 * 
		 * @param colname
		 * @return
		 */
		public double getDouble( String colname ) {
			return Double.parseDouble(getString(colname));
		}
		
		/**
		 * Get the specified column's data as a double.
		 * 
		 * @param colname
		 * @return
		 */
		public double getDouble( int colindex ) {
			return Double.parseDouble(getString(colindex));
		}
		
	}
	
	/**
	 * Read the next record (a row that contains non-whitespace characters).
	 * Or return null if it is the end of the sheet file.
	 * 
	 * @return
	 * @throws IOException
	 */
	public Record nextRecord() throws IOException {
		String line = reader.readLine();
		while( line!=null && line.trim().length()==0 ) {
			line = reader.readLine();
		}
		return line==null?null:new Record( this, line );
	}
	
	/**
	 * Get a set of the columns.
	 * 
	 * @return
	 */
	public Set<String> getColumns() {
		return this.colname_index.keySet();
	}
	
	/**
	 * Get the index of the specified column name.
	 * Or return -1 if there's no column with the name.
	 * 
	 * @param colname
	 * @return
	 */
	public int getColumnIndex( String colname ) {
		Integer colindex = this.colname_index.get(colname);
		return colindex==null?-1:colindex;
	}
	
	/**
	 * Get the name of the column with the specified index.
	 * Or return null of the specified index does not exist.
	 * 
	 * @param colindex
	 * @return
	 */
	public String getColumnName( int colindex ) {
		return this.colindex_name.get(colindex);
	}
	
	/**
	 * Whether does the sheet file contain a column with the specified name.
	 * 
	 * @param colname
	 * @return
	 */
	public boolean hasColumn( String colname ) {
		return this.colname_index.containsKey(colname);
	}
	
	/**
	 * Get the total number of columns in this sheet file.
	 * 
	 * @return
	 */
	public int numColumns() {
		return this.colname_index.size();
	}
	
}
