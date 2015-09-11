package utils.db;

/**
 * SqlUtils is a class to automatically create database SQLs (some methods are designed for MYSQL SQL format).
 * It is extremely easy and light-weighted, but involves the most common database operations.
 * Currently, it does not support automatically reflect java beans to database storage.
 * 
 * @author Jiepu Jiang 
 * @version Sep 20, 2011
 */
public class SqlUtils {
	
	/***************************************************/
	/**************** SQL SELECT Clause ****************/
	/***************************************************/
	
	/**
	 * Create a SELECT SQL clause:
	 * <pre>
	 * SELECT $cols[0], $cols[1], ..., $cols[n-1]
	 * FROM $table
	 * </pre>
	 */
	public static String select(String table, String[] cols) {
		return select(table, cols, null, null, null);
	}
	
	/**
	 * Create a SELECT SQL clause:
	 * <pre>
	 * SELECT $cols[0], $cols[1], ..., $cols[n-1]
	 * FROM $table[0], $table[1], ... , $table[k-1]
	 * </pre>
	 */
	public static String select(String[] table, String[] cols) {
		return select(table, cols, null, null, null);
	}
	
	/**
	 * Create a SELECT SQL clause:
	 * <pre>
	 * SELECT $cols[0], $cols[1], ..., $cols[n-1]
	 * FROM $table
	 * WHERE $where
	 * </pre>
	 */
	public static String select(String table, String[] cols, String where) {
		return select(table, cols, where, null, null);
	}
	
	/**
	 * Create a SELECT SQL clause:
	 * <pre>
	 * SELECT $cols[0], $cols[1], ..., $cols[n-1]
	 * FROM $table[0], $table[1], ... , $table[k-1]
	 * WHERE $where
	 * </pre>
	 */
	public static String select(String[] table, String[] cols, String where) {
		return select(table, cols, where, null, null);
	}
	
	/**
	 * Create a SELECT SQL clause:
	 * <pre>
	 * SELECT $cols[0], $cols[1], ... , $cols[n-1]
	 * FROM $table
	 * ORDER BY $orderby[0], $orderby[1], ... , $orderby[m-1]
	 * </pre>
	 */
	public static String select(String table, String[] cols, String[] orderby) {
		return select(table, cols, null, orderby, null);
	}
	
	/**
	 * Create a SELECT SQL clause:
	 * <pre>
	 * SELECT $cols[0], $cols[1], ... , $cols[n-1]
	 * FROM $table[0], $table[1], ... , $table[k-1]
	 * ORDER BY $orderby[0], $orderby[1], ... , $orderby[m-1]
	 * </pre>
	 */
	public static String select(String[] table, String[] cols, String[] orderby) {
		return select(table, cols, null, orderby, null);
	}
	
	/**
	 * Create a SELECT SQL clause:
	 * <pre>
	 * SELECT $cols[0], $cols[1], ... , $cols[n-1]
	 * FROM $table
	 * ORDER BY $orderby[0]( $ranks[0]), $orderby[1]( $ranks[1]), ... , $orderby[m-1]( $ranks[m-1])
	 * </pre>
	 */
	public static String select(String table, String[] cols, String[] orderby, int[] ranks) {
		return select(table, cols, null, orderby, ranks);
	}
	
	/**
	 * Create a SELECT SQL clause:
	 * <pre>
	 * SELECT $cols[0], $cols[1], ... , $cols[n-1]
	 * FROM $table[0], $table[1], ... , $table[k-1]
	 * ORDER BY $orderby[0]( $ranks[0]), $orderby[1]( $ranks[1]), ... , $orderby[m-1]( $ranks[m-1])
	 * </pre>
	 */
	public static String select(String[] table, String[] cols, String[] orderby, int[] ranks) {
		return select(table, cols, null, orderby, ranks);
	}
	
	/**
	 * Create a SELECT SQL clause:
	 * <pre>
	 * SELECT $cols[0], $cols[1], ... , $cols[n-1]
	 * FROM $table
	 * WHERE $where
	 * ORDER BY $orderby[0]( $ranks[0]), $orderby[1]( $ranks[1]), ... , $orderby[m-1]( $ranks[m-1])
	 * </pre>
	 */
	public static String select(String table, String[] cols, String where, String[] orderby, int[] ranks) {
		return select(new String[]{table}, cols, where, orderby, ranks);
	}
	
	/**
	 * Create a SELECT SQL clause:
	 * <pre>
	 * SELECT $cols[0], $cols[1], ... , $cols[n-1]
	 * FROM $table[0], $table[1], ... , $table[k-1]
	 * WHERE $where
	 * ORDER BY $orderby[0]( $ranks[0]), $orderby[1]( $ranks[1]), ... , $orderby[m-1]( $ranks[m-1])
	 * </pre>
	 */
	public static String select(String[] table, String[] cols, String where, String[] orderby, int[] ranks) {
		return select(table, cols, where, orderby, ranks, 0, 0);
	}
	
	/**
	 * Create a SELECT SQL clause:
	 * <pre>
	 * SELECT $cols[0], $cols[1], ... , $cols[n-1]
	 * FROM $table[0], $table[1], ... , $table[k-1]
	 * WHERE $where
	 * ORDER BY $orderby[0]( $ranks[0]), $orderby[1]( $ranks[1]), ... , $orderby[m-1]( $ranks[m-1])
	 * LIMIT $limitMaxRec
	 * </pre>
	 */
	public static String select(String[] table, String[] cols, String where, String[] orderby, int[] ranks, int limitMaxRec) {
		return select(table, cols, where, orderby, ranks, 0, limitMaxRec);
	}
	
	/**
	 * Create a SELECT SQL clause:
	 * <pre>
	 * SELECT $cols[0], $cols[1], ... , $cols[n-1]
	 * FROM $table[0], $table[1], ... , $table[k-1]
	 * WHERE $where
	 * ORDER BY $orderby[0]( $ranks[0]), $orderby[1]( $ranks[1]), ... , $orderby[m-1]( $ranks[m-1])
	 * LIMIT $limitOffset, $limitMaxRec
	 * </pre>
	 * 
	 * @param table A list of tables from which to select data.
	 * @param cols A list of columns to be selected.
	 * @param where WHERE clause.
	 * @param orderby A list of colums to rank results.
	 * @param ranks Indicating ranking parameters for ranked colums (0 for ASC, 1 for DESC).
	 * @param limitOffset Limit clause parameter, offset.
	 * @param limitMaxRec Limit clause parameter, maximum record return.
	 */
	public static String select(String[] table, String[] cols, String where, String[] orderby, int[] ranks, int limitOffset, int limitMaxRec) {
		if(cols==null||cols.length==0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append(cols[0]);
		for(int i=1;i<cols.length;i++){
			sb.append(", ");
			sb.append(cols[i]);
		}
		sb.append(" FROM ");
		sb.append(table[0]);
		for(int i=1;i<table.length;i++){
			sb.append(", ");
			sb.append(table[i]);
		}
		if(where!=null){
			sb.append(" WHERE ");
			sb.append(where);
		}
		if(orderby!=null&&orderby.length>0){
			sb.append(" ORDER BY ");
			sb.append(orderby[0]);
			if(ranks!=null&&ranks.length>0&&ranks[0]==1){
				sb.append(" DESC");
			}
			for(int i=1;i<orderby.length;i++){
				sb.append(", ");
				sb.append(orderby[i]);
				if(ranks!=null&&ranks.length>i&&ranks[i]==1){
					sb.append(" DESC");
				}
			}
		}
		if(limitMaxRec>0){
			sb.append(" LIMIT ");
			if(limitOffset!=0){
				sb.append(limitOffset);
				sb.append(", ");
			}
			sb.append(limitMaxRec);
		}
		return sb.toString();
	}
	
	/**
	 * Create an INSERT SQL clause:
	 * <pre>
	 * INSERT INTO $table($cols[0], $cols[1], ... , $cols[n-1])
	 * VALUES($vals[0], &vals[1], ... , $vals[n-1])
	 * </pre>
	 */
	public static String insert(String table, String[] cols, String[] vals, DataType[] types) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(table);
		sb.append("(");
		sb.append(cols[0]);
		for(int i=1;i<cols.length;i++){
			sb.append(", ");
			sb.append(cols[i]);
		}
		sb.append(") VALUES(");
		if(types[0]==DataType.Num){
			sb.append(vals[0]);
		}else if(types[0]==DataType.Text){
			sb.append("'");
			sb.append(format(vals[0]));
			sb.append("'");
		}
		for(int i=1;i<cols.length;i++){
			sb.append(", ");
			if(types[i]==DataType.Num){
				sb.append(vals[i]);
			}else if(types[i]==DataType.Text){
				sb.append("'");
				sb.append(format(vals[i]));
				sb.append("'");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	/**
	 * Replace the following special characters in MYSQL clause:
	 * <ul>
	 * <li>\ --> \\</li>
	 * <li>' --> \'</li>
	 * <li>" --> \"</li>
	 * </ul>
	 */
	public static String format(String val) {
		if( val==null ) {
			return "";
		}
		val = val.replaceAll("(\\\\|'|\")", "\\\\$1");
		return val;
	}
	
	public enum DataType {
		Num, Text
	}
	
	/**
	 * <p>
	 * Make a sql select command using the templace.
	 * The template should have finished all other fields except the select fields (which can be indicated as a parameter with the name "$1" in the template).
	 * </p>
	 * <p>For example, the template sql string can be:</p>
	 * <pre>
	 * 
	 * SELECT $1 FROM table WHERE field = 'value'
	 * </pre>
	 * <p>Then, if an array of fields to be selected is provided, e.g. { "fd1", "fd2", "fd3", "fd4" }, it will return the following sql string:</p>
	 * <pre>
	 * 
	 * SELECT fd1, fd2, fd3, fd4 FROM table WHERE field = 'value'
	 * </pre>
	 * <p>It is supposed to be used for cases that a lot of fields are to be operated automatically.</p>
	 * 
	 * @param template
	 * @param select_fields
	 * @return
	 */
	public static String makeSqlSelect(String template, String[] select_fields) {
		StringBuilder sb = new StringBuilder();
		sb.append(select_fields[0]);
		for(int ix=1;ix<select_fields.length;ix++){
			sb.append(","+select_fields[ix]);
		}
		return template.replace("$1", sb.toString());
	}
	
	/**
	 * <p>
	 * Make a sql insert command using the templace.
	 * The template should have finished all other fields except the inserted fields and values (which can be indicated as a parameter with the name "$1" and "$2" in the template).
	 * </p>
	 * <p>For example, the template sql string can be:</p>
	 * <pre>	INSERT INTO table($1) VALUES($2)</pre>
	 * <p>Then, if arrays of fields and values to be inserted are provided, e.g. { "fd1", "fd2", "fd3", "fd4" } and { 1, 2.2, 3.94, 'textual information' }, it will return the following sql string:</p>
	 * <pre>	SELECT fd1, fd2, fd3, fd4 FROM table WHERE field = 'value'</pre>
	 * <p>It is supposed to be used for cases that a lot of fields are to be operated automatically.</p>
	 * 
	 * @param template
	 * @param select_fields
	 * @return
	 */
	public static String makeSqlInsert(String template, String[] insert_fields, Object[] insert_values) {
		StringBuilder sb_fields = new StringBuilder();
		StringBuilder sb_values = new StringBuilder();
		sb_fields.append(insert_fields[0]);
		sb_values.append(strValue(insert_values[0]));
		for(int ix=1;ix<insert_fields.length;ix++){
			sb_fields.append(","+insert_fields[ix]);
			sb_values.append(","+strValue(insert_values[ix]));
		}
		return template.replace("$1", sb_fields.toString()).replace("$2", sb_values.toString());
	}
	
	public static String strValue(Object value) {
		if(value instanceof String){
			return "'" + SqlUtils.format((String)value) + "'";
		}else{
			return value.toString();
		}
	}
	
}
