package posemuckel.client;

import java.util.ArrayList;

import lib.RetriedAssert;

import posemuckel.client.model.Chat;
import posemuckel.client.model.DatabaseFactory;
import posemuckel.client.model.Model;
import posemuckel.client.model.User;
import posemuckel.client.model.event.ChatEvent;
import posemuckel.client.model.event.ChatListener;
import posemuckel.client.model.event.UserListenerAdapter;


/**
 * In diesem Test wird die Funktionalität eines <code>Chat</code>
 * getestet. Der Test umfasst den gesamten 
 * Ablauf der Kommunikation zwischen Client und Server:<br\>
 * <ul>
 * <li>Modelklasse (Anforderung eines Update über das Netz)</li>
 * <li>Client (senden)</li>
 * <li>Server (empfangen, verarbeiten und senden)</li>
 * <li>Client (empfangen und parsen)</li>
 * <li>Modelklasse (update der Klasse)</li>
 * <li>Listener (benachrichtigen)</li>
 * </ul>
 * @author Posemuckel Team
 *
 */
public class ChatTest extends TestComponents {

	private Model model;
	private User user;
	private TestListener listener;
	private MyChatListener chatListener;
	private ConnectionHelper connection;
	
	/*
	 *  (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		/*
		 * f&uuml; jeden Test einzeln an und abschalten!
		 * damit lassen sich Probleme selektiv beheben
		 */
		Settings.setDubuggingMode(false, false, false, true);
		connection = new ConnectionHelper();
		connection.startClient();
		model = new Model();
		listener = new TestListener();
		chatListener = new MyChatListener();
		DatabaseFactory.getRegistry().setReceiver(model);
		user = model.getUser();
		if(Settings.debug)System.out.println("setUp finished");
	}
	
	/*
	 *  (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		if(Settings.debug)System.out.println("tearDown started");
		super.tearDown();
		connection.stopClient();
		Settings.resetDebuggingMode();
	} 
	
	/**
	 * In diesem Test wird ein erfolgreiches Starten des Chat und das Senden einer
	 * Chatnachricht getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der Buddyliste</li>
	 * <li>Starten eines privaten Chat mit allen Buddys als 
	 *     eingeladenen Teilnehmern</li>
	 * <li>Senden einer Typing-Nachricht</li>
	 * <li>Senden einer Chatnachricht</li>
	 * <li>Logout</li>
	 * </ul>
	 */
	public void testChat() {
		login(user);
		loadBuddys(user);
		user.addListener(listener);
		assertTrue("too many chats already exist", model.getChatCount() == 1);
		//lade alle Buddys zum Chatten ein
		//final int size = 1 + user.getBuddyList().size();
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
					//der private Chat und der allgemeine Chat ergeben zwei Chats
					assertEquals("User could not start a private chat", 
							model.getChatCount(), 2);
					assertTrue("Listener was not notified", listener.notified);
					//es ist nur einer der Chatteilnehmer online
					assertEquals("members of chat not set", 1, listener.chat.getChatMembers().size());
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Chat chat = listener.getChat();
		chat.addListener(chatListener);
		chat.userIsTyping();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue("typing was not received", 
							chatListener.typing);
					assertEquals("author was not received", 
							user.getNickname(), chatListener.author);
					
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		chat.userIsChatting("a chat message");
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertEquals("chat message was not received", 
							"a chat message", chatListener.message);
					assertEquals("author was not received", 
							user.getNickname(), chatListener.author);
				}			
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		logout(user);
	}
	
	/**
	 * In diesem Test werden die Nachricht TYPING und READING (im öffentlichen
	 * Chat) getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Senden einer Typing-Nachricht</li>
	 * <li>Senden einer Reading-Nachricht</li>
	 * <li>Logout</li>
	 * </ul>
	 */
	public void testReading() {
		login(user);
		//der öffentliche Chat hat die ID 0
		Chat chat = model.getChat("0");
		chat.addListener(chatListener);
		chat.userIsTyping();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(chatListener.typing);
					assertEquals("wrong author", 
							user.getNickname(), chatListener.author);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		chatListener.author = null;
		chat.userIsReading();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertFalse(chatListener.typing);
					assertEquals("wrong author", 
							user.getNickname(), chatListener.author);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logout(user);
	}
	
	/**
	 * In diesem Test werden die Nachricht TYPING und READING (im öffentlichen
	 * Chat) getestet. 
	 * Der Ablauf des Testes ist:<br/>
	 * <ul>
	 * <li>Login</li>
	 * <li>Laden der Buddyliste</li>
	 * <li>Starten eines privaten Chat mit allen Buddys als 
	 *     eingeladenen Teilnehmern</li>
	 * <li>Senden einer Typing-Nachricht</li>
	 * <li>Senden einer Reading-Nachricht</li>
	 * <li>Logout</li>
	 * </ul>
	 */
	public void testReadingInPrivateChat() {
		login(user);
		Chat chat = startBuddyChat(user, model);
		
		chat.addListener(chatListener);
		chat.userIsTyping();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertTrue(chatListener.typing);
					assertEquals("wrong author", 
							user.getNickname(), chatListener.author);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		chatListener.author = null;
		chat.userIsReading();
		try {
			new RetriedAssert(Settings.TIMEOUT, Settings.INTERVALL) {
				@Override
				public void run() throws Exception {
					assertFalse(chatListener.typing);
					assertEquals("wrong author", 
							user.getNickname(), chatListener.author);
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logout(user);
	}
	
	/**
	 * Lauscht auf das Erzeugen von neuen Chats, bei denen der User eingeladen
	 * ist.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class TestListener extends UserListenerAdapter {
		
		boolean notified;
		private Chat chat;
		
		/**
		 * Gibt die Quelle des letzten Event aus.
		 * @return die Quelle des letzten Event
		 */
		Chat getChat() {
			return chat;
		}

		/* (non-Javadoc)
		 * @see posemuckel.client.model.event.UserListenerAdapter#newChat(posemuckel.client.model.event.ChatEvent)
		 */
		@Override
		public void newChat(ChatEvent event) {
			notified = true;
			chat = event.getSource();
		}		
	}
	
	/**
	 * Lauscht auf Ereignisse zu einem Chat.
	 * 
	 * @author Posemuckel Team
	 *
	 */
	private class MyChatListener implements ChatListener {
		
		String message = "";
		String author = "";
		boolean typing = false;
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.ChatListener#typing(posemuckel.client.model.event.ChatEvent)
		 */
		public void typing(ChatEvent event) {
			typing = true;
			author = event.getAuthor();
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.ChatListener#reading(posemuckel.client.model.event.ChatEvent)
		 */
		public void reading(ChatEvent event) {
			typing = false;
			author = event.getAuthor();
		}
		
		/*
		 *  (non-Javadoc)
		 * @see posemuckel.client.model.event.ChatListener#chatting(posemuckel.client.model.event.ChatEvent)
		 */
		public void chatting(ChatEvent event) {
			message = event.getMessage();
			author = event.getAuthor();
			typing = false;
		}
		
	}

}
