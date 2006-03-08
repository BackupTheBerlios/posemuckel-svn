/**
 * 
 */
package posemuckel.client.gui;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import posemuckel.client.model.Chat;
import posemuckel.client.model.Model;
import posemuckel.client.net.Client;
import posemuckel.client.net.ReadingSender;
import posemuckel.client.net.ThreadLauncher;
import posemuckel.common.GetText;

/**
 * Das Eingabefeld eines Chat. Das Eingabefeld kann sich an die Gr&ouml;&szlig;e
 * des Fensters anpassen und ist vertikal scrollbar. Bei Betätigen der Enter-Taste
 * wird die Nachricht versendet.
 * 
 * @author Posemuckel Team
 *
 */
public class Chat_InputComposite extends Composite {
	
	// how many typing users to display?
	private final static int TYPING_USERS = 5;
	
	private Text inputText;
	private Chat chat;
	private Label namesLabel;
	private ReadingSender ts = null;
	
	// Dies sind die Namen der Leute, die gerade tippen
	private Vector<String> names;
	private String myname;
	
	private String isTyping;
	private String areTyping;
	
	/**
	 * Diese Klasse dient quasi als Semaphore für das
	 * Verzögerte Senden von READING-Nachrichten. Objekte dieser
	 * Klasse dienen dem Austausch von Infos zwischen der GUI und
	 * dem ReadingSender Thread.
	 * @author Posemuckel Team
	 */
	public class TypingSemaphor {
		// Zustand: Lesend (false), Schreibend (true)
		private boolean typing = false;
		// Zeitpunkt für das voraussichtlich nächste
		// Senden einer READING-Nachricht
		private long sendTime;
		
		public void setTyping(boolean typing) { this.typing = typing; }
		
		public boolean isTyping() { return typing; }

		public void sendTime(long sendTime) {
			this.sendTime = sendTime;
		}
		
		public long getSendTime() { return sendTime; }
	}
	
	TypingSemaphor typing = new TypingSemaphor();
	
	/**
	 * Erstellt ein Composite, welches ein Eingabefeld, das sich an die zu
	 * verf&uuml;gung stehende Fl&auml;che anpasst, enth&auml;lt.
	 * Das Eingabefeld wird in eine <code>Group</code> mit dem angegebenen Style und 
	 * dem angegebenen Titel eingesetzt.
	 * 
	 * @param parent Composite, in das das Eingabefeld eingebettet wird
	 * @param style mit dem die <code>Group</code> angezeigt wird 
	 * @param title Titel der <code>Group</code>
	 * @param id die ID des Chat
	 */
	public Chat_InputComposite(Composite parent, int style, String title, String id) {
		super(parent, SWT.NONE);
		Model model = Model.getModel();
		chat = model.getChat(id);
		myname = model.getUser().getNickname();
		names = new Vector<String>();
		ts = new ReadingSender(typing,chat);
		getDescriptions();
		ThreadLauncher.getInstance().startThread(ts);
		//die Titelzeile
		//setLayout(new GridLayout(5, false));
		setLayout(MyLayoutFactory.createGrid(5, false));
		Label titleLabel = new Label(this, SWT.SHADOW_NONE);
		titleLabel.setText(title);		
		
		new Label(this, SWT.NONE);
		namesLabel = new Label(this, SWT.SHADOW_NONE | SWT.RIGHT);
		namesLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		namesLabel.setText("");
		Label typingLabel = new Label(this, SWT.SHADOW_NONE);
		//typingLabel.setLayoutData(new GridData(SWT.END, SWT.NONE, false, false));
		//typingLabel.setText(GetText.gettext("TYPING"));

		inputText = new Text(this, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		inputText.setLayoutData(fillingArea());
		inputText.setText("");
		addKeyListener(inputText);
	}
	
	private void getDescriptions() {
		isTyping = GetText.gettext("IS_TYPING");
		areTyping = GetText.gettext("ARE_TYPING");
	}
	
	/**
	 * Erstellt den 'Senden'-Button und fügt ihn in das Composite ein.
	 * @param parent , der den Button enthalten soll
	 */
	protected void createButtons(Composite parent) {
		Button sendButton=new Button(parent, SWT.PUSH);
		sendButton.setText(GetText.gettext("SEND"));
		addSelectionListener(sendButton);
		GridData data = new GridData(SWT.LEFT, SWT.TOP, false, false);
		data.minimumHeight = 20;
		sendButton.setLayoutData(data);
	}
	
	/**
	 * Der KeyListener sorgt daf&uuml;r, dass die Benutzereingabe gesendet wird.
	 * 
	 * @param text Textfeld
	 */
	protected void addKeyListener(Text text) {
		text.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (!Model.getModel().getUser().isLoggedIn()) {
					// Eine entsprechende Meldung anzeigen...
					Messages.showInfo(GetText.gettext("NOT_LOGGED_IN_MESSAGE"),GetText.gettext("NOT_LOGGED_IN_TITLE"));
					// LoginDialog andrehen
					new LoginDialog(Display.getDefault().getActiveShell());
					return;
				}
				
				if ((e.keyCode==SWT.LF) | (e.keyCode==SWT.CR))  {
					send();
					synchronized(typing) {
						// Den Status muss man jetzt auf nicht-
						// schreibend setzen, damit keine unnötige
						// READING Nachricht verschickt wird.
						typing.setTyping(false);					
					}
				}
				else {
					synchronized(typing) {
						// Neue Sende-Zeit festlegen.
						typing.sendTime(System.currentTimeMillis()+5000);
						// Nur, wenn ich nicht schon eine TYPING-Nachricht
						// verschickt habe, dann muss ich hier die
						// entsprechenden Parameter setzen und den
						// Thread aus dem Tiefschlaf aufwecken.
						if(!typing.isTyping()) {
							typing.setTyping(true);
							if(Client.getConnection() != null) {
								System.out.println("Thread aufwecken.");
								typing.notify();
								chat.userIsTyping();
							}
						}
					}
				}
			}
			public void keyReleased(KeyEvent e) {
				if ((e.keyCode==SWT.LF) | (e.keyCode==SWT.CR))  {;
				inputText.setText("");	
				}
			}
		});
	}
	
	/**
	 * Sendet die Benutzereingabe an den Server
	 * 
	 * @param button Sendebutton
	 */
	protected void addSelectionListener(Button button) {
		button.addSelectionListener(new SelectionAdapter() {			
			
			public void widgetSelected(SelectionEvent event) {
				send();
			}
		});
	}
	
	/**
	 * Sendet die Benutzereingabe an den Server. Au&szlig;erdem wird der Server
	 * benachrichtigt, wenn der Anwender zu tippen beginnt.
	 *
	 */
	private void send() {
		if(!(inputText.getText().compareTo("") == 0)){		
			if(Client.getConnection() != null) {
				chat.userIsChatting(inputText.getText());
			}
			inputText.setText("");
		}
	}
	
	/**
	 * Setzt die Namen derjenigen, die am Tippen sind.
	 * 
	 * @param name der Name
	 * @param isTyping true, wenn am tippen, false, wenn wieder am Lesen
	 */
	protected void updateNamesInRunnable(final String name, final boolean isTyping) {
		Runnable run = new Runnable() {
			
			public void run() {
				if(inputText.isDisposed())return;
				updateNames(name, isTyping);
			}
		};
		Display.getDefault().asyncExec(run);			
	}
	
	/**
	 * Aktualisiert die Liste mit den Namen der Anwender, die gerade am Tippen sind und
	 * und sorgt für eine Aktualisierung der Darstellung.
	 * @param name Benutzername
	 * @param isTyping true, wenn der Anwender am Tippen ist
	 */
	private void updateNames(String name, boolean isTyping) {
		if(isTyping && !name.equals(myname)) {
		//if(isTyping) {
			names.add(name);
		} else {
			names.remove(name);
		}
		setNames();
	}  	
	
	/**
	 * Aktualisiert die Namen der Anwender, die gerade am Tippen sind.
	 *
	 */
	private void setNames() {
		String text = "";
		int i = 0;
		while (i < names.size() && i < TYPING_USERS) {
			if(i != 0) {
				text += ", ";
			}
			text += names.get(i);
			i++;
		}
		if(names.size() > TYPING_USERS)
			text += ", ... " + areTyping;
		else if(names.size() > 1)
			text += " " + areTyping;
		else if(names.size() == 1)
			text += " " + isTyping;
		else text = "";
		
		namesLabel.setText(text);
	}
	
	
	/**
	 * Erstellt das GridData f&uuml;r das Eingabefeld.
	 * 
	 * @return GridData des Eingabefeldes
	 */
	protected GridData fillingArea() {
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 4;
		return data;
	}		
	
	
}
