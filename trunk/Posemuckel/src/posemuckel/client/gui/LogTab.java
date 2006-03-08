package posemuckel.client.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import posemuckel.client.model.Chat;
import posemuckel.client.model.Model;
import posemuckel.client.model.event.ChatAdapter;
import posemuckel.client.model.event.ChatEvent;

public class LogTab extends MyTab {
	
	private TabItem chatItem;
	
	private Chat_MessagesComposite messages;
	
	private Chat logChat;
	
	private MyChatListener listener;
	
	
	
	public void createContent(TabFolder parent) {
		logChat = Model.getModel().getLogchat();
		//das Tab erstellen
		chatItem = getTab(parent, SWT.NONE);
		chatItem.setText(logChat.getTitle());
		//die Control setzen
		SashForm mainContainer = new SashForm(parent, SWT.HORIZONTAL);
		mainContainer.setBackground(Colors.getSashBackground());
		//das Tab mit der Control verbinden
		chatItem.setControl(mainContainer);

		listener = new MyChatListener();
		logChat.addListener(listener);
		messages = new Chat_MessagesComposite(mainContainer, SWT.SHADOW_ETCHED_IN, null);
	}
	
	private class MyChatListener extends ChatAdapter {
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.ChatListener#chatting(posemuckel.client.model.event.ChatEvent)
		 */
		public void chatting(final ChatEvent event) {
			if(isDisposed()) {
				//der ChatListener muss entfernt werden
				logChat.removeListener(this);
			} else {
				messages.updateTextInRunnable(event.getAuthor(), event.getMessage());		
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
