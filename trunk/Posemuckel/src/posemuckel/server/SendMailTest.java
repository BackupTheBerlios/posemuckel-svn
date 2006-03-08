package posemuckel.server;

import java.io.FileNotFoundException;
import java.io.IOException;

import posemuckel.common.Config;
import junit.framework.TestCase;

public class SendMailTest extends TestCase {

	/*
	 * Test method for 'posemuckel.server.SendMail.send_invitation(String, ClientInfo, ClientInfo, ProjectInfo)'
	 */
	public void testSend_invitation() {
		// Konfiguration laden.
		try {
			Config cfg = Config.getInstance();
			// Dieser Pfad ist für mich zum Testen wichtig.
			// Bitte nicht löschen! - Jens
			//cfg.setFilename("/home/jens/workspace/cscw2/src/posemuckel/chat/server/posemuckel_server.cfg");
			cfg.loadFromFile("c:\\java\\lib\\posemuckel_server.cfg");
		} catch (FileNotFoundException e) {
			fail("Die Konfigurationsdatei konnte nicht gefunden werden.");
		} catch (IOException e) {
			fail("Fehler beim Lesen der Konfigurationsdatei.");
		}
		ClientInfo inviting = new ClientInfo(null, "rosi");
		inviting.setName("Rosa");
		inviting.setSurname("Schlüpfer");
		inviting.seteMail("posemuckel@gmx.net");
		ClientInfo invited = new ClientInfo(null, "axels");
		invited.setName("Axel");
		invited.setSurname("Schweiss");
		invited.seteMail("posemuckel@gmx.net");
		invited.setLanguage("DE");
		ProjectInfo project = new ProjectInfo("0");
		project.setTopic("Das Leben der Feldmaus");
		project.setDescription("Hier sollen Informationen zum Leben der gemeinen Feldmaus (Microtus arvalis) gesucht und archiviert werden, damit spätere Generationen auch noch an diese Lebensform erinnert werden, wenn das Land schon zubetoniert ist.");
		project.setOwner(inviting);
		SendMail.send_invitation(invited,project);
	}

}
