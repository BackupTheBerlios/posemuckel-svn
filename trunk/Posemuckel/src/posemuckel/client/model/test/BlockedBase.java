/**
 * 
 */
package posemuckel.client.model.test;

import posemuckel.client.model.Task;
import posemuckel.client.model.test.Mockbase;

/**
 * blockedRegistry hat eine ganz einfache Philosophie: jede Aufforderung, die Daten 
 * zu ändern, wird ignoriert: damit kann der Zwischenzustand modeliert werden, der entsteht, 
 * wenn derartige Änderungen über das Netzwerk an die DB geschickt werden und noch keine
 * Antwort da ist.
 * 
 * zu Testzwecken wird ein Anwender somebody mit Passwort pwd angelegt, der nicht eingeloggt ist
 * 
 * 
 */
public class BlockedBase extends Mockbase {
	
	/**
	 * Diese Instanz von Database antwortet auf die Anfragen einfach nicht.
	 *
	 */
	public BlockedBase() {
		super.overwriteUser("somebody", "pwd");
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.UserRegistry#add(java.lang.String, java.lang.String)
	 */
	@Override
	public void overwriteUser(String name, String pwd) {
		//tue nichts
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.UserRegistry#logIn(java.lang.String, java.lang.String)
	 */
	@Override
	public void login(String name, String pwd, Task task) {
		//tue nichts
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.UserRegistry#logOut(java.lang.String)
	 */
	@Override
	public void logout(String name, Task task) {		
//		tue nichts
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.UserRegistry#remove(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteUser(String name, String pwd) {
//		tue nichts
	}
	
	

}
