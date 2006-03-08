/**
 * 
 */
package posemuckel.client.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import posemuckel.client.model.Chat;
import posemuckel.client.model.Model;
import posemuckel.client.model.event.ChatEvent;
import posemuckel.client.model.event.ChatListener;
import posemuckel.common.GetText;

/**
 * Ein ChatTab enth&auml;lt alle f&uuml;r einen Chat n&ouml;tigen GUI-Elemente.
 * Die GUI-Elemente sind in SashForms eingebettet, so dass sich die Grenzen
 * zwischen den Elementen verschieben lassen. Die Textfelder und die Teilnehmerliste
 * wurden so konstruiert, dass sie sich dem zur Verf&uuml;gung stehenden Platz
 * anpassen k&ouml;nnen.
 * 
 * Das ChatTab verf&uuml;gt &uuml;ber eine ID des Chat.
 * 
 * @author Posemuckel Team
 *
 */
public class ChatTab extends MyTab {
	
	private TabItem chatItem;
	private String id;
	
	/**
	 * Das ChatTab managet die Darstellung dieses Chat in der GUI.
	 */
	private Chat chat;
	
	//private GUI_MemberList_Composite chatMembers;
	private Chat_MessagesComposite messages;
	private Chat_InputComposite typingComp;
	
	private MyChatListener listener;

	/**
	 * Erstellt einen ChatTab zu dem Chat mit der angegebenen ID.
	 * 
	 * @param id chatID
	 */
	public ChatTab(String id) {
		this.id = id;
		chat = Model.getModel().getChat(id);
	}
	
	/**
	 * Erzeugt das ChatTab in dem angegebenen Folder. 
	 * @param parent TabFolder
	 */
	public void createContent(TabFolder parent) {
		//if(Client.getConnection() != null) {
			listener = new MyChatListener();
			chat.addListener(listener);
		//}
		chatItem = getTab(parent, SWT.NONE);
		chatItem.setText(chat.getTitle());			
		SashForm mainContainer = new SashForm(parent, SWT.HORIZONTAL);
		mainContainer.setBackground(Colors.getSashBackground());
		chatItem.setControl(mainContainer);
		/*
		 *den Inhalt des Tabs definieren
		 */
		SashForm textContainer = new SashForm(mainContainer, SWT.VERTICAL);
		
		messages = new Chat_MessagesComposite(textContainer, SWT.SHADOW_ETCHED_IN, "Nachrichten");
		textContainer.setBackground(Colors.getSashBackground());
		typingComp = new Chat_InputComposite(textContainer, SWT.SHADOW_ETCHED_IN, GetText.gettext("INPUT_FIELD"), id);
		Composite rightComp = new Composite(mainContainer, SWT.NONE);
		rightComp.setLayout(MyLayoutFactory.createGrid(1, false));
		Composite members = new GUI_MemberList_Composite(rightComp, chat.getChatMembers());
		members.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		typingComp.createButtons(rightComp);
		/*
		 * nachdem die CompositeObjekte in die Sashformen eingesetzt wurden, wird
		 * ihr Anteil an der Gesamtflaeche festgelegt
		 */
		mainContainer.setWeights(new int[]{2, 1});
		textContainer.setWeights(new int[]{3, 1});
	}
		
	/**
	 * Empfängt die ChatEreignisse von dem Chat der umgebenden Klasse. Es werden
	 * die eingehenden Nachrichten angezeigt und die Liste der Anwender, die am 
	 * Tippen sind, wird aktualisiert.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class MyChatListener implements ChatListener {
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.ChatListener#typing(posemuckel.client.model.event.ChatEvent)
		 */
		public void typing(ChatEvent event) {
			if(isDisposed()) {
				//der ChatListener muss entfernt werden
				chat.removeListener(this);
			} else {
				typingComp.updateNamesInRunnable(event.getAuthor(), true);
			}
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.ChatListener#reading(posemuckel.client.model.event.ChatEvent)
		 */
		public void reading(ChatEvent event) {
			if(isDisposed()) {
				//der ChatListener muss entfernt werden
				chat.removeListener(this);
			} else {
				typingComp.updateNamesInRunnable(event.getAuthor(), false);
			}
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.ChatListener#chatting(posemuckel.client.model.event.ChatEvent)
		 */
		public void chatting(final ChatEvent event) {
			if(isDisposed()) {
				//der ChatListener muss entfernt werden
				chat.removeListener(this);
			} else {
				typingComp.updateNamesInRunnable(event.getAuthor(), false);
				messages.updateTextInRunnable(event.getAuthor(), event.getMessage());
		  		Runnable run = new Runnable() {

					public void run() {
						if(typingComp.isDisposed())return;
						if(!isSelected()) {
							System.out.println(" start blinking for chat no " + event.getSource().getID());
							startBlinking();
						}
					}  			
		  		};
		  		Display.getDefault().asyncExec(run);			
			}
		}
		
		/**
		 * Gibt an, ob die SWT-GUI bereits disposed() ist.
		 * @return true, falls die GUI nicht mehr existiert
		 */
		private boolean isDisposed() {
			return chatItem.isDisposed();
		}
		
	}
	
	
	
}
