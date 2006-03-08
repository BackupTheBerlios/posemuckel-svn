/**
 * 
 */
package posemuckel.server;

/**
 * 
 * SQL_Formatter bietet Hilfsmethoden zum Formatieren von einfachen SQL_Nachrichten.
 * Die erstellten SQL-Nachrichten werden in DB verwendet.
 * @author Posemuckel Team
 *
 */
public class SQLFormatter {
	
	static final String INSERT = "INSERT INTO ";
	static final String SELECT = "SELECT ";
	static final String FROM = " FROM ";
	static final String WHERE = " WHERE ";
	static final String AND = "' AND ";
	static final String SET = " SET ";
	static final String EQUALS = "='";
	static final String EQUEST = "=?";
	static final String COMMA = "', "; 
	static final String FINAL = "';";
	
	static final String PAND = " AND ";
	static final String PFINAL = ";";
	static final String PCOMMA = ", ";
	
	/*
	 * Für die normalen Statements in einer DB 
	 */
	
	public static String insert(String table, String[] column, String[] param) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(INSERT);
		buffer.append(table);
		buffer.append(SET);
		query(buffer, column, param, COMMA);
		buffer.append(FINAL);
		return buffer.toString();
	}
	
	public static String select(String table, String[] resultColumn, String column, String param) {
		StringBuffer buffer = new StringBuffer();
		headerSelect(buffer, table, resultColumn);
		buffer.append(column);
		buffer.append(EQUALS);
		buffer.append(param);
		buffer.append(FINAL);
		return buffer.toString();
	}
	
	public static String select(String table, String[] resultColumn, String[] column, String[] param) {
		StringBuffer buffer = new StringBuffer();
		headerSelect(buffer, table, resultColumn);
		query(buffer, column, param, AND);
		buffer.append(FINAL);
		return buffer.toString();
	}
	
	/*
	 * Für preparedStatements: werden diese Statements überhaupt von MySQL unterstützt?
	 */
	
	public static String preparedInsert(String table, String[] column) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(INSERT);
		buffer.append(table);
		buffer.append(SET);
		preparedQuery(buffer, column, PCOMMA);
		buffer.append(PFINAL);
		return buffer.toString();
	}

	public static String preparedSelect(String table, String[] resultColumn, String column) {
		StringBuffer buffer = new StringBuffer();
		headerSelect(buffer, table, resultColumn);
		buffer.append(column);
		buffer.append(EQUEST);
		buffer.append(PFINAL);
		return buffer.toString();
	}
	
	public static String preparedSelect(String table, String[] resultColumn, String[] column) {
		StringBuffer buffer = new StringBuffer();
		headerSelect(buffer, table, resultColumn);
		preparedQuery(buffer, column, PAND);
		buffer.append(PFINAL);
		return buffer.toString();
	}
	
	private static void headerSelect(StringBuffer buffer, String table, String[] resultColumn) {
		buffer.append(SELECT);
		//resultColumn
		for (int i = 0; i < resultColumn.length; i++) {
			buffer.append(resultColumn[i]);
			if(i < (resultColumn.length-1)) {
				buffer.append(",");
			}
		}
		buffer.append(FROM);
		buffer.append(table);
		buffer.append(WHERE);
	}
	
	private static void query(StringBuffer buffer, String[] column, String[] param, String seperator) {
		for (int i = 0; i < column.length; i++) {
			buffer.append(column[i]);
			buffer.append(EQUALS);
			buffer.append(param[i]);
			if(i < (column.length-1)) {
				buffer.append(seperator);
			}
		}
	}
	
	private static void preparedQuery(StringBuffer buffer, String[] column, String seperator) {
		for (int i = 0; i < column.length; i++) {
			buffer.append(column[i]);
			buffer.append(EQUEST);
			if(i < (column.length-1)) {
				buffer.append(seperator);
			}
		}
	}
}
