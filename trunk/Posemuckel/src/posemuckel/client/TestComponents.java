/**
 * 
 */
package posemuckel.client;

import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.TestCase;
import lib.RetriedAssert;
import posemuckel.client.model.Chat;
import posemuckel.client.model.Model;
import posemuckel.client.model.Project;
import posemuckel.client.model.User;
import posemuckel.client.model.Webtrace;
import posemuckel.client.model.event.WebTraceAdapter;
import posemuckel.client.model.event.WebTraceEvent;

/**
 * Hier sind Methoden enthalten, die öfter benötigt werden. 
 * Es werden folgende Module angeboten:
 * <ul>
 * <li>Login des Standardanwenders</li>
 * <li>Login mit wählbarem Benutzernamen und Passwort</li>
 * <li>Logout</li>
 * <li>Laden der Buddyliste</li>
 * <li>Laden aller Projekte</li>
 * <li>Laden der eigenen Projekte</li>
 * <li>Öffnen eines Projektes</li>
 * <li>Starten eines Chat mit allen Buddys</li>
 * <li>Laden des Webtrace eines geöffneten Webtrace</li>
 * <li>Senden einer Visiting-Nachricht (die benötigten Daten müssen im Projekt 
 * gesetzt sein!</li>
 * </ul>
 * 
 * Jedes dieser Module wartet auf die Antwort vom Server. Um das Ergebnis der
 * Aktion zu überprüfen, wird auf <code>RetriedAssert</code> zurückgegriffen. 
 * Die Module enthalten assert-Anweisungen, die Fehlschlagen, wenn der Server
 * nicht die erwartete Antwort liefert. Das verwendete TIMEOUT wird in Settings
 * festgelegt.
 * 
 * @see lib.RetriedAssert
 * @see posemuckel.client.Settings#TIMEOUT
 * @author Posemuckel Team
 *
 */
public class TestComponents extends TestCase {
	
	/**
	 * Logt den User mit dem Benutzernamen und Passwort des Standardanwenders ein.
	 * Es wird auf die Bestätigung vom Server gewartet. Wenn der Anwender nicht
	 * innerhalb des TIMEOUT eingeloggt ist, schlägt die enthaltene assert-Anweisung
	 * fehl.
	 * @param user Anwender, der eingeloggt werden soll
	 */
	protected void login(final User user) {
		user.login("tiger", "tiger");
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL/2) {
				@Override
				public void run() throws Exception {
					assertTrue("User could not login", user.isLoggedIn());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Logt den User mit dem gewählten Benutzernamen und Passwort ein.
	 * Es wird auf die Bestätigung vom Server gewartet. Wenn der Anwender nicht
	 * innerhalb des TIMEOUT eingeloggt ist, schlägt die enthaltene assert-Anweisung
	 * fehl.
	 * @param user Anwender, der eingeloggt werden soll
	 * @param name Benutzername
	 * @param pwd Passwort
	 */
	protected void login(final User user, String name, String pwd) {
		user.login(name, pwd);
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL/2) {
				@Override
				public void run() throws Exception {
					assertTrue("User could not login", user.isLoggedIn());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Loggt den Anwender aus.
	 * Es wird auf die Bestätigung vom Server gewartet. Wenn der Anwender nicht
	 * innerhalb des TIMEOUT ausgeloggt ist, schlägt die enthaltene assert-Anweisung
	 * fehl.
	 * @param user der Anwender
	 */
	protected void logout(final User user) {
		user.logout();;
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL/2) {
				@Override
				public void run() throws Exception {
					assertFalse("no ack from server for logout", user.isLoggedIn());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Läd die Buddyliste des Standardanwenders vom Server. Die Buddyliste
	 * enthält einen Buddy mit Benutzernamen niko.
	 * Es wird auf die Liste vom Server gewartet. Wenn die Liste nicht
	 * innerhalb des TIMEOUT geladen ist, schlägt die enthaltene assert-Anweisung
	 * fehl.
	 * @param user der Anwender
	 */
	protected void loadBuddys(final User user) {
		user.getBuddyList().load();
		try {
			new RetriedAssert(2*Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertFalse(user.getBuddyList().isEmpty());
					assertNotNull(user.getBuddyList().getMember("niko"));
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Startet einen Chat mit allen Buddys des Anwenders. Die Buddyliste
	 * sollte mindestens einen Buddy enthalten und es sollte kein weiterer 
	 * Chat (außer dem öffentlichen Chat) laufen.
	 * Es wird auf die Antwort vom Server gewartet. Wenn die Antwort nicht
	 * innerhalb des TIMEOUT angekommen ist, schlägt die enthaltene assert-Anweisung
	 * fehl.
	 * @param user der Anwender
	 * @param model das aktuelle Model
	 * @return der gestartete Chat
	 */
	protected Chat startBuddyChat(final User user, final Model model) {
		loadBuddys(user);
		String[] buddys = user.getBuddyList().getNicknames();
		ArrayList<String> names = new ArrayList<String>(buddys.length);
		for (String nickname : buddys) {
			names.add(nickname);
		}
		user.startChatWith(names);
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertEquals(2, model.getChatCount());
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Iterator<Chat> iterator = model.getAllChats().iterator(); iterator.hasNext();) {
			Chat chat = iterator.next();
			if(!chat.getID().equals("0")) return chat;
		}
		return null;
	}
	
	/**
	 * Läd die Liste mit allen Projekten vom Server. Die Liste enthält mindestens
	 * ein Projekt.
	 * Es wird auf die Liste vom Server gewartet. Wenn die Liste nicht
	 * innerhalb des TIMEOUT geladen ist, schlägt die enthaltene assert-Anweisung
	 * fehl.
	 * @param model das aktuelle Model
	 */
	protected void getAllProjects(final Model model) {
		model.getAllProjects().load();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertFalse(model.getAllProjects().isEmpty());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Läd die Liste mit den Projekten des Anwenders vom Server. Die Liste enthält mindestens
	 * ein Projekt.
	 * Es wird auf die Liste vom Server gewartet. Wenn die Liste nicht
	 * innerhalb des TIMEOUT geladen ist, schlägt die enthaltene assert-Anweisung
	 * fehl.
	 * @param user der Anwender
	 */
	protected void getMyProjects(final User user) {
		user.getProjects().load();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertFalse(user.getProjects().isEmpty());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Öffnet ein Projekt. Das Projekt muss in der Liste 'MyProjects' des
	 * Anwenders enthalten sein.
	 * Es wird auf die Antwort vom Server gewartet. Wenn die Antwort nicht
	 * innerhalb des TIMEOUT geladen ist, schlägt die enthaltene assert-Anweisung
	 * fehl.
	 * @param project das zu öffnende Projekt
	 * @param model das aktuelle Model
	 */
	protected void openProject(final Project project, final Model model) {
		final WebtraceListener listener = new WebtraceListener();
		if(project.equals(model.getOpenProject())) {
			//die Mitgliedernamen sind bekannt und der Webtrace kann initialisiert
			//werden
			project.getWebtrace().addListener(listener);
		} else {
			listener.loaded = true;
		}
		project.open();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertEquals(project, model.getOpenProject());
					assertTrue(listener.loaded);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Wartet, bis der Webtrace für das geöffnete Projekt geladen ist. Der
	 * Webtrace wird automatisch geladen, wenn das Projekt geöffnet wird.
	 * Es wird auf den Webtrace vom Server gewartet. Wenn der Webtrace nicht
	 * innerhalb des TIMEOUT geladen ist, schlägt die enthaltene assert-Anweisung
	 * fehl.
	 * @param project das geöffnete Projekt
	 */
	protected void waitForWebtrace(Project project) {
		final WebtraceListener listener = new WebtraceListener();
		project.getWebtrace().addListener(listener);
		//wenn der Server schnell war, lohnt das Warten nicht
		if(project.getWebtrace().isLoaded()) return;
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.loaded);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sendet eine Visiting-Nachricht an den Server. 
	 * Es wird auf die Antwort vom Server gewartet. Wenn die Antwort nicht
	 * innerhalb des TIMEOUT geladen ist, schlägt die enthaltene assert-Anweisung
	 * fehl.
	 * @param project das geöffnete Projekt
	 */
	protected void visit(final Project project) {
		final WebtraceListener listener = new WebtraceListener();
		project.getWebtrace().addListener(listener);
		project.visiting();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(listener.succeeded);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		project.getWebtrace().removeListener(listener);
	}
	
	/**
	 * Dieser Listener lauscht auf das Laden des Webtrace und die Bestätigung für
	 * Visiting-Nachrichten. 
	 * 
	 * @see posemuckel.client.TestComponents#waitForWebtrace(Project)
	 * @see posemuckel.client.TestComponents#visit(Project)
	 * @author Posemuckel Team
	 */
	private class WebtraceListener extends WebTraceAdapter {
		
		boolean succeeded = false;
		boolean loaded = false;
		
		/**
		 * Setzt die Flags zurück.
		 *
		 */
		void reset() {
			succeeded = false;
			loaded = false;
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#visiting(posemuckel.client.model.event.WebTraceEvent)
		 */
		@Override
		public void visiting(WebTraceEvent event) {
			succeeded = true;
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.WebTraceAdapter#traceLoaded(posemuckel.client.model.Webtrace)
		 */
		@Override
		public void traceLoaded(Webtrace webtrace) {
			loaded = true;
		}		
	}



}
