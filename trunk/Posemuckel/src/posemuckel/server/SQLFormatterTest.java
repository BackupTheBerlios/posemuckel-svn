package posemuckel.server;

import junit.framework.TestCase;

public class SQLFormatterTest extends TestCase {

	/*
	 * Test method for 'posemuckel.server.SQLFormatter.insert(String, String[], String[])'
	 */
	public void testInsert() {
		String expected = "INSERT INTO url SET id='1', adress='http://dummy.de', title='no title';";
		String actual = SQLFormatter.insert("url", 
				new String[]{"id", "adress", "title"}, 
				new String[]{"1", "http://dummy.de", "no title"});
		assertEquals(expected, actual);
	}
	
	public void testSelect() {
		String expected = "SELECT adress,id FROM url WHERE title='no title';";
		String actual = SQLFormatter.select("url", 
				new String[]{"adress", "id"}, "title", "no title");
		assertEquals(expected, actual);
	}
	
	public void testSelectWithAnd() {
		String expected = "SELECT id FROM url WHERE title='no title' AND adress='http://dummy.de';";
		String actual = SQLFormatter.select("url", 
				new String[]{"id"}, new String[] {"title", "adress"}, new String[] {"no title", "http://dummy.de"});
		assertEquals(expected, actual);
	}
	
	public void testPreparedInsertStatement() {
		String expected = "INSERT INTO url SET id=?, adress=?, title=?;";
		String actual = SQLFormatter.preparedInsert("url", 
				new String[]{"id", "adress", "title"});
		assertEquals(expected, actual);
	}
	
	public void testPreparedSelectStatement() {
		String expected = "SELECT adress,id FROM url WHERE title=?;";
		String actual = SQLFormatter.preparedSelect("url", 
				new String[]{"adress", "id"}, "title");
		assertEquals(expected, actual);
	}
	
	public void testPreparedSelectWithAnd() {
		String expected = "SELECT id FROM url WHERE title=? AND adress=?;";
		String actual = SQLFormatter.preparedSelect("url", 
				new String[]{"id"}, new String[] {"title", "adress"});
		assertEquals(expected, actual);
	}

}
