package posemuckel.client.gui;

import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import posemuckel.client.model.Model;
import posemuckel.client.model.User;
import posemuckel.client.model.event.UserEvent;
import posemuckel.client.model.event.UserListener;
import posemuckel.client.model.event.UserListenerAdapter;
import posemuckel.client.net.Client;
import posemuckel.common.Config;
import posemuckel.common.GetText;

/**
 * Erzeugt den Inhalt des Login-Tabs beim LoginDialog.
 * @author Posemuckel Team
 */
public class LoginComposite extends Composite implements TabContent {
	Text text_username;
	Text text_passwd;
	Label label_username;
	Label label_passwd;
	Label label_progress;
	Label label_feedback;
	Text feedback;
	Button button_Login;
	Button button_Cancel;
	ProgressIndicator indicator;
	Image image_logo;
	private String username;
	private String passwd;
	private String default_user;
	private String loginFailed;
	private UserListener listener;
	private User user;
	private Button checkbox_save;
	private LoginDialog dialog;
	private FontRegistry fonts;

/**
 * Diese Klasse erzeugt den Inhalt des Login-Tabs vom LoginDialog.
 * Dazu wird das Vater-Composite und eine Referenz auf den Dialog
 * selbst übergeben.
 * @param parent
 * @param dialog
 */
	public LoginComposite(Composite parent, LoginDialog dialog) {
		super(parent, SWT.NONE);
		addDisposeListener(new Cleaner());
		this.dialog = dialog;
		this.fonts = dialog.getFonts();
		this.user = Model.getModel().getUser();
		addUserListener();
		getDescriptions();
		
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		GridLayout gridlayout = new GridLayout(2,false);
		gridlayout.marginHeight = 30;
		gridlayout.marginWidth = 30;
		gridlayout.horizontalSpacing = 10;
		gridlayout.verticalSpacing = 10;
		this.setLayout(gridlayout);

		label_username = new Label(this, SWT.SHADOW_NONE);
		GridData data = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan=1;
		label_username.setLayoutData(data);
		label_username.setText(username + ":");
		label_username.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		Label label_unknown = new Label(this, SWT.SHADOW_IN);
		ImageDescriptor id = ImageDescriptor.createFromFile(this.getClass(),"Logo.jpg");
		final Image image = id.createImage(this.getDisplay()); 
		data = new GridData( GridData.HORIZONTAL_ALIGN_CENTER );
		data.horizontalSpan=1;
		data.verticalSpan=5;
		label_unknown.setLayoutData(data);
		label_unknown.setImage(image);
		
		text_username = new Text(this, SWT.SINGLE | SWT.BORDER);
		data = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING );
		data.horizontalSpan=1;
		text_username.setLayoutData(data);
		text_username.setBackground(parent.getBackground());
		text_username.setTextLimit(20);
		
		label_passwd = new Label(this, SWT.SHADOW_NONE);
		label_passwd.setText(passwd + ":");
		data = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan=1;
		label_passwd.setLayoutData(data);
		label_passwd.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		text_passwd = new Text(this, SWT.PASSWORD | SWT.BORDER);
		data = new GridData( GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan=1;
		text_passwd.setLayoutData(data);
		text_passwd.setBackground(parent.getBackground());
		text_passwd.setTextLimit(20);
		
		checkbox_save = new Button(this, SWT.CHECK);
		checkbox_save.setText("Passwort lokal speichern");
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		checkbox_save.setLayoutData(data);
		checkbox_save.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		initTextFields(default_user);

		label_feedback = new Label(this, SWT.SHADOW_IN);
		label_feedback.setText("");
		label_feedback.setFont(fonts.get("failure"));
		data = new GridData( GridData.FILL_BOTH);
		data.horizontalSpan=2;
		label_feedback.setLayoutData(data);
		label_feedback.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		// Hier wird die Login-Funktionalität bei Drücken
		// von ENTER implementiert:
		KeyAdapter login_keyListener = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.CR:
					login(text_username.getText(), text_passwd.getText());
					break;
				}
			}
		};
		text_username.addKeyListener(login_keyListener);
		text_passwd.addKeyListener(login_keyListener);
	}
	
	private void addUserListener() {
		listener = new UserListenerAdapter() {		
			/*
			 * (non-Javadoc)
			 * 
			 * @see posemuckel.client.model.event.UserListenerAdapter#login(posemuckel.client.model.event.UserEvent)
			 */
			@Override
			public void login(final UserEvent event) {
				Runnable run = new Runnable() {
					
					public void run() {
						if (event.isSucceeded() && !isDisposed()) {
							dialog.saveAll();
							Model.load(false);
							dialog.close();
						} else if(!isDisposed()){
							getParent().setCursor(
									Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));
							text_username.setText("");
							text_passwd.setText("");
							label_feedback.setForeground(Colors.getWarning());
							label_feedback.setText(loginFailed);
						}
					}
					
				};
				Display.getDefault().asyncExec(run);
			}
			
		};
		user.addListener(listener);
	}

	/**
	 * Loggt den User ein.
	 * 
	 * @param nickname Nickname des Anwenders
	 * @param password Passwort des Anwenders
	 */
	private void login(String nickname, String password) {
		// Alle Tabs sollen die Daten speichern,
		// damit zum Beispiel auch die Server-Einstellung
		// benutzt werden kann.
		dialog.saveAll();
		Client.initBackend();			
		/*
		 * der User sorgt selber dafür, dass er eingeloggt wird; wie das genau
		 * funktioniert, interessiert die GUI nicht
		 */
		user.login(text_username.getText(), text_passwd.getText());
	}

	private void initTextFields(String username) {
		text_username.setText(username);
		text_username.setFocus();
		text_username.selectAll();
		text_passwd.setText("");
		getParent().setCursor(
				Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));
	}

	private void getDescriptions() {
		username = GetText.gettext("USERNAME");
		default_user = GetText.gettext("DEFAULT_USER");
		passwd = GetText.gettext("PASSWORD");
		loginFailed = GetText.gettext("LOGINFAIL");
	}

	public void setUser(String user, String pwd) {
		text_username.setText(user);
		text_passwd.setText(pwd);
	}

	public void setUserFeedback(String userfeedback, int type) {
		label_feedback.setText(userfeedback);
	}

	/**
	 * Hole die Datenfelder aus der globalen Konfiguration.
	 */
	public void loadDefaults() {
		Config config = Config.getInstance();
		String user = config.getconfig("DEFAULT_USER");
		String pass = config.getconfig("DEFAULT_PASS");
		if (user == null)
			user = "";
		if (pass == null)
			pass = "";
		if (pass.equals(""))
			checkbox_save.setSelection(false);
		else
			checkbox_save.setSelection(true);
		setUser(user,pass);
	}

	/**
	 * Speichert die Daten aus dem Login-Tab des Dialoges
	 * in dem globalen Konfigurations-Objekt.
	 */
	public void save2Config() {
		Config config = Config.getInstance();
		// Den Benutzernamen speichern:
		config.setconfig("DEFAULT_USER",text_username.getText());
		// Das Passwort nur speichern, wenn gewünscht,
		// andernfalls das entsprechende Feld leeren,
		// damit kein Passwort mehr sichbar ist.
		if(checkbox_save.getSelection())
			config.setconfig("DEFAULT_PASS",text_passwd.getText());
		else
			config.setconfig("DEFAULT_PASS","");
	}

	public void performAction() {
		login(text_username.getText(), text_passwd.getText());
	}
	
	private class Cleaner implements DisposeListener {

		public void widgetDisposed(DisposeEvent e) {
			System.out.println("remove user listener in LoginComposite");
			user.removeListener(listener);
		}
		
	}

	public boolean validInput() {
		String user = text_username.getText();
		String pass = text_passwd.getText();
		if ( user.equals("") || pass.equals("") )
			return false;
		else
			return true; 
	}

	public void treatInputError() {
		String user = text_username.getText();
		String pass = text_passwd.getText();
		if ( user.equals("") ){
			label_feedback.setForeground(Colors.getWarning());
			label_feedback.setText("Sie sollten schon einen Benutzernamen eingeben!");
		}
			
		if ( pass.equals("") ){
			label_feedback.setForeground(Colors.getWarning());
			label_feedback.setText("Geben Sie Ihr Passwort ein!");
		}
	}

}