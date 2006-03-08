/**
 * 
 */
package posemuckel.client.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import posemuckel.common.GetText;

/**
 * Das Ausgabefeld eines Chat. Das Ausgabefeld kann sich an die Gr&ouml;&szlig;e
 * des Fensters anpassen und ist vertikal scrollbar. Die Autoren einer Nachricht
 * werden optisch von den Nachrichten abgehoben.
 * 
 * @author Posemuckel Team
 *
 */
public class Chat_MessagesComposite extends Composite {
	
	/**
	 * stellt die Nachrichten dar
	 */
	private StyledText chatMessages;
	
	/**
	 * Index des letzten Zeichens in der Ausgabe
	 */
	private int endText = 0;
	
	/**
	 * Erstellt ein Composite, welches ein Ausgabefeld f&uuml;r die Chatnachrichten,
	 * das sich an die zur
	 * verf&uuml;gung stehende Fl&auml;che anpasst, enth&auml;lt.
	 * Das Ausgabefeld wird in eine <code>Group</code> mit dem angegebenen Style und 
	 * dem angegebenen Titel eingesetzt.
	 * 
	 * @param parent Composite, in das das Ausgabefeld eingebettet wird
	 * @param style mit dem die <code>Group</code> angezeigt wird 
	 * @param title Titel der <code>Group</code>
	 */
	public Chat_MessagesComposite(Composite parent, int style, String title) {
		super(parent, style);
		setLayout(MyLayoutFactory.createGrid(1, false, 2));
		//die Titelzeile
		if(title != null) {
			Label titleLabel = new Label(this, SWT.SHADOW_NONE);
			titleLabel.setText(GetText.gettext("MESSAGES"));
		}
		//setze die Anzeige der Chatnachrichten zusammen
		chatMessages = new StyledText(this, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
		chatMessages.setLayoutData(fillingArea());
	}
	
	/**
	 * H&auml;gt die Nachricht an die anderen Nachrichten an.
	 * 
	 * @param user Autor der Nachricht
	 * @param message Nachricht
	 */
	public void setMessage(String user, String message) {		
		String us = "";		
		if (chatMessages.getText()=="") {
			us = "  "+user+":  ";
		}
		else {
			us = "\n"+"  "+user+":  ";
		}
		//den Teilnehmer schreiben
		//erst den Text anhängen
		chatMessages.append(us);
		//dann den Style für die Linie setzen
		StyleRange styleUS = new StyleRange(endText, us.length(),Colors.getChatUserColor(), Colors.getChatUserBackground());
		styleUS.fontStyle = SWT.BOLD;
		endText+=us.length();
		chatMessages.setStyleRange(styleUS);
		chatMessages.setLineBackground(chatMessages.getLineCount()-1, 1, Colors.getChatUserLine());
		//die Nachricht schreiben
		String ms = "\n"+message;
		StyleRange styleM = new StyleRange(endText, ms.length(), Colors.getChatTextColor(), Colors.getChatTextBackground());
		endText+=ms.length();
		chatMessages.append(ms);
		chatMessages.setStyleRange(styleM);
		chatMessages.setTopIndex(chatMessages.getLineCount()-1);
	}
	
	/**
	 * H&auml;gt die Nachricht an die anderen Nachrichten an. Der Code wird mit 
	 * einem Runnable in den GUI-Thread eingeh&auml;ngt.
	 * 
	 * @param user Autor der Nachricht
	 * @param message Nachricht
	 */
	protected void updateTextInRunnable(final String user, final String message) {
  		Runnable run = new Runnable() {

			public void run() {
				if(chatMessages.isDisposed())return;
				setMessage(user, message);
			}  			
  		};
  		Display.getDefault().asyncExec(run);			
	}

	
	/**
	 * Erstellt das GridData f&uuml;r das Ausgabefeld.
	 * 
	 * @return GridData des Eingabefeldes
	 */
	protected GridData fillingArea() {
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		return data;
	}		


}
