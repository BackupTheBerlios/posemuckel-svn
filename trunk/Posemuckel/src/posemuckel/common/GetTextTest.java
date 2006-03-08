package posemuckel.common;

import junit.framework.TestCase;

public class GetTextTest extends TestCase {

	/*
	 * Test method for 'posemuckel.common.GetText.gettext(String[])'
	 */
	public void testGettextString() {
		GetText.setResourceName("posemuckel.lang.Messages");
		String text = GetText.gettext("CANCEL");
		if ( !text.equals("Abbruch") ) {
			fail("Fehler beim Auffinden des Textes Abbruch in gettext.");
		}
	}

	/*
	 * Test method for 'posemuckel.common.GetText.gettext(String)'
	 */
	public void testGettextStringArray() {
		String[] query = { "CANCEL", "OK", "LOCATION" };
		GetText.setResourceName("posemuckel.lang.Messages");
		String[] res = GetText.gettext(query);
		if ( !(res[0]).equals("Abbruch") || !(res[2]).equals("Wohnort") ) {
			fail("Fehler in der Methode gettext(String[]).");
		}
	}

	public void testMacroreplaceString() {
		String res = GetText.macroreplace("Hello NAME!\nWelcome to our show.","NAME","Peter");
		res = GetText.macroreplace(res,"BLA","Peter");
		if ( !res.equals("Hello Peter!\nWelcome to our show.") ) {
			fail("Fehler in der Methode macroreplace(String,String,String).");
		}
	}
	
	
	public void testMacroreplaceRecursiveString() {
		String res = GetText.macroreplace_recursive("Hello NAME!\nWelcome NAME to NAME our show.","NAME","Peter");
		res = GetText.macroreplace_recursive(res,"BLA","Peter");
		if ( !res.equals("Hello Peter!\nWelcome Peter to Peter our show.") ) {
			fail("Fehler in der Methode macroreplace_recursive(String,String,String).");
		}
	}
	
	public void testFoldText() {
		int lastindex = -1;
		int newindex = 0;
		String tofold = "AAAA AAAAAAAA AAAAAAAAAAAAAAAAAA AAAAAAAAAAAA AAAAAA AAAAAAAA AAAAAA AAAAAAAAAAA AAAAAAAAAAAA AAAAAAA AA";
		tofold += "\n\nAAAA AAA\nBBBBBBBB BBBBBBBBB BBBB BBBBB BBBBBB BBBBB BBBBBBB BBBBBB BBB BB\n\nCCCCCC";
		tofold = GetText.foldtext(tofold,20);
		//System.out.println(tofold);
		while ( newindex != -1 ) {
			newindex = tofold.indexOf("\n",lastindex+1);
			if ( newindex - lastindex > 33 ) {
				fail("Fehler beim Falten des Textes in foldtext(String,int).");
			}
			lastindex = newindex;
		}
	}

	public void testEscapeSQL() {
		String teststring = "'Dies ist ein b?ser\\ String!";
		String string=GetText.escape_sql_write(teststring);
		if ( !string.equals("\\'Dies ist ein b\\?ser\\\\ String!") ) {
			fail("Fehler beim Ersetzen von Sonderzeichen in escape_sql.");
		}
	}
	
}
