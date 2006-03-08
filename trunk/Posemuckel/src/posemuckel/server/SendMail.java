package posemuckel.server;

import java.io.OutputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import posemuckel.common.Config;
import posemuckel.common.GetText;

/**
 * Diese Klasse dient ausschliesslich dem Senden von Mails.
 * Dabei wird ein Kommando ausgeführt, welches der Server-
 * Konfuguration entnommen wird.
 * 
 * @author Posemuckel Team
 *
 */
public class SendMail {
	
	/**
	 * Verschickt eine Einladuns-eMail durch Ausführen eines Kommandos.
	 * Dabei sollte die Mail in der Warteschlange des lokalen MTA
	 * landen. Dieser leitet die Nachricht dann weiter.
	 * @param to_invite Der Benutzer, der eingeladen werden soll.
	 * @param proj Das Projekt, zu dem der Benutzer eingeladen werden soll.
	 */
	public static void send_invitation(ClientInfo to_invite, ProjectInfo proj) {
		String message;
		String subject;
		String cmd;
		Config config = Config.getInstance();
		ClientInfo inviting = proj.getOwner();
		// Hier muss man den Text in der Sprache des Adressaten holen!
		try {
			Locale loc = new Locale((to_invite.getLanguage()).toLowerCase(),"");
			ResourceBundle messages = PropertyResourceBundle.getBundle("posemuckel.server.Messages",loc);
			message = messages.getString("INVITATION_MESSAGE");
			subject = messages.getString("INVITATION_SUBJECT");
		} catch (MissingResourceException e) {
			// Wenn die Ressource nicht gefunden wird, gibt es eine englische Nachricht.
			message="Dear INVITED_NAME!\n\nI'd like to invite you to join my project with the topic \"TOPIC\". This is a short description of it:\n\nDESCRIPTION\n\n\nIf you are interested, feel free to login at your Posemuckel-Server as usual.\n\nSincerely,\nINVITING_NAME INVITING_SURNAME";
			subject="Invitation to a corporate Posemuckel-Project";
		}
		// Ersetze die Makros durch die passenden Werte:
		message = GetText.macroreplace(message,"INVITED_NAME",to_invite.getName());
		message = GetText.macroreplace(message,"TOPIC",proj.getTopic());
		message = GetText.macroreplace(message,"DESCRIPTION",proj.getDescription());
		message = GetText.macroreplace(message,"INVITING_NAME",inviting.getName());
		message = GetText.macroreplace(message,"INVITING_SURNAME",inviting.getSurname());		
		// Den Text noch falten, damit das schön aussieht.
		message = GetText.foldtext(message,70);
		// Hole das Mail-Kommando aus der Konfiguration:
		cmd = config.getconfig("SENDMAIL_CMD");
		// Jetzt die Makros im Kommando ersetzen:
		cmd = GetText.macroreplace(cmd,"SUBJECT",subject);
		cmd = GetText.macroreplace(cmd,"REPLYTOADDRESS",inviting.geteMail());
		cmd = GetText.macroreplace(cmd,"TOADDRESS",to_invite.geteMail());
		System.out.println("Sende eMail an ("+to_invite.getUserName()+") "+to_invite.geteMail());
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			OutputStream ostream = proc.getOutputStream();
			ostream.write(message.getBytes());
			ostream.close();
		} catch (Exception e) {
			// Wird geworfen, wenn der Aufruf des Kommandos fehlschlägt.
			GetText.setResourceName("posemuckel.server.Messages");
			System.out.println(GetText.gettext("CMD_EXEC_ERROR")+cmd);
			System.out.println(GetText.gettext("CMD_CHECK"));
		}

	}


}
