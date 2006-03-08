package posemuckel.client.gui;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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
import posemuckel.common.EnumsAndConstants;
import posemuckel.common.GetText;

/**
 * Erzeugt den Inhalt des Register-Tabs im Login-Dialog.
 * @author Posemuckel Team
 */
public class RegisterComposite extends Composite implements TabContent
{
	Label label_username;
	Label label_firstname;
	Label label_surname;
	Label label_gender;
	Label label_email;
	Label label_passwd;
	Label label_passwdconfirm;
	Label label_location;
	Label label_language;
	Label label_comment;
	Label label_progress;
	Label label_blank;
	
	Text text_username;
	Text text_firstname;
	Text text_surname;
	Text text_email;
	Text text_passwd;
	Text text_passwdconfirm;
	Text text_location;
	Text text_language;
	Text text_comment;
	
	Combo combo_gender;
	Combo combo_language;
	Image imageField;

	Button button_register;
	Button button_cancel;
		
	ProgressIndicator indicator;
	
	private Label label_feedback;	
	private String username;
	private String firstname;
	private String surname;
	private String email;
	private String passwd;
	private String location;
	private String comment;
	private String gender;
	private String male;
	private String female;
	private String language;
	private HashMap langs = new HashMap<String,String>();
	private String confirm;
	private String pwd_fail;
	protected String user_exists;
	private String give_me_a_username;
	private String give_me_a_password;
	private String give_me_an_email;
	//private String register_success;
	
	private UserListener listener;
	private Button checkbox_hints;
	private Config config;
	private LoginDialog dialog;
	private User user;
	private FontRegistry fonts;
	
	public RegisterComposite(Composite parent, LoginDialog dialog)
	{
		super(parent, SWT.NONE);
		addDisposeListener(new Cleaner());
		this.dialog = dialog;
		this.fonts = dialog.getFonts();
		this.user = Model.getModel().getUser();
		getDescriptions();
		addUserListener();
		config = Config.getInstance();
		
		GridLayout gridlayout = new GridLayout(4,false);
		gridlayout.marginHeight = 10;
		gridlayout.marginWidth = 10;
		gridlayout.horizontalSpacing = 6;
		gridlayout.verticalSpacing = 6;
		this.setLayout(gridlayout);
		
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		// Firstname field
		label_firstname = new Label(this, SWT.SHADOW_NONE | SWT.COLOR_WHITE);
		label_firstname.setText(firstname+":");
		label_firstname.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		text_firstname = new Text(this, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData( GridData.FILL_HORIZONTAL );
		data.horizontalSpan=1;
		data.minimumWidth = 200;
		text_firstname.setLayoutData(data);
		text_firstname.setBackground(parent.getBackground());
		
		// Surname field
		label_surname = new Label(this, SWT.SHADOW_NONE);
		label_surname.setText(surname+":");
		label_surname.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		text_surname = new Text(this, SWT.SINGLE | SWT.BORDER);
		text_surname.setTextLimit(20);
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.horizontalSpan=1;
		data.minimumWidth = 200;
		text_surname.setLayoutData(data);
		text_surname.setBackground(parent.getBackground());

		// Gender combo box
		label_gender = new Label(this, SWT.SHADOW_NONE);
		label_gender.setText(gender + ":");
		label_gender.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		combo_gender = new Combo(this, SWT.READ_ONLY);
		combo_gender.setItems(new String[] {male, female});
		combo_gender.setText(male);
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.horizontalSpan=1;
		data.minimumWidth = 200;
		combo_gender.setLayoutData(data);
		combo_gender.setBackground(parent.getBackground());
		
		// Language field
		label_language = new Label(this, SWT.SHADOW_NONE);
		label_language.setText(language + ":");
		label_language.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		combo_language = new Combo(this, SWT.READ_ONLY);
		Set keys = langs.keySet();
		Object[] arr = keys.toArray();
		for ( Object lang : arr ) {
			combo_language.add((String)lang);
		}
		combo_language.setText((String)arr[0]);
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.horizontalSpan=1;
		data.minimumWidth = 200;
		combo_language.setLayoutData(data);
		combo_language.setBackground(parent.getBackground());
		
		// Email field
		label_email = new Label(this, SWT.SHADOW_NONE);
		label_email.setText(email+":");
		label_email.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		text_email = new Text(this, SWT.SINGLE | SWT.BORDER);
		text_email.setTextLimit(60);
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.horizontalSpan=1;
		data.minimumWidth = 200;
		text_email.setLayoutData(data);
		text_email.setBackground(parent.getBackground());
		
		// Location field
		label_location = new Label(this, SWT.SHADOW_NONE);
		label_location.setText(location+":");
		label_location.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		text_location = new Text(this, SWT.SINGLE | SWT.BORDER);
		text_location.setTextLimit(50);
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.horizontalSpan=1;
		data.minimumWidth = 200;
		text_location.setLayoutData(data);
		text_location.setBackground(parent.getBackground());

		// Username field
		label_username = new Label(this, SWT.SHADOW_NONE);
		label_username.setText(username+":");
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.horizontalSpan=1;
		data.verticalIndent = 15;
		label_username.setLayoutData(data);
		label_username.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		text_username = new Text(this, SWT.SINGLE | SWT.BORDER);
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.horizontalSpan=1;
		data.verticalIndent = 15;
		text_username.setLayoutData(data);
		text_username.setBackground(parent.getBackground());
		
		// Password field
		label_passwd = new Label(this, SWT.SHADOW_NONE);
		label_passwd.setText(passwd+":");
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.horizontalSpan=1;
		data.verticalIndent = 15;
		label_passwd.setLayoutData(data);
		label_passwd.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		text_passwd = new Text(this, SWT.PASSWORD | SWT.BORDER);
		text_passwd.setTextLimit(20);
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.horizontalSpan=1;
		data.verticalIndent = 15;
		text_passwd.setLayoutData(data);
		text_passwd.setBackground(parent.getBackground());
		
		// blank Label für Zwischenraum
		label_blank = new Label(this, SWT.SHADOW_NONE);
		label_blank.setText("");
		label_blank.setEnabled(false);
		label_blank.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.horizontalSpan=2;
		label_blank.setLayoutData(data);
		
		// Confirm password field
		label_passwdconfirm = new Label(this, SWT.SHADOW_NONE);
		label_passwdconfirm.setText(confirm + ":");		
		label_passwdconfirm.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				
		text_passwdconfirm = new Text(this, SWT.PASSWORD | SWT.BORDER);
		data = new GridData( GridData.FILL_HORIZONTAL );
		data.horizontalSpan=1;
		data.minimumWidth = 200;
		text_passwdconfirm.setLayoutData(data);
		text_passwdconfirm.setBackground(parent.getBackground());
		
		// Comment field
		label_comment = new Label(this, SWT.SHADOW_NONE);
		label_comment.setText(comment+":");
		data = new GridData( GridData.BEGINNING);
		data.horizontalSpan=1;
		data.verticalSpan=1;
		data.verticalIndent = 15;
		label_comment.setLayoutData(data);
		label_comment.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				
		text_comment = new Text(this, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		data = new GridData( GridData.FILL_BOTH);
		data.horizontalSpan=3;
		data.verticalSpan=3;
		data.verticalIndent = 15;
		text_comment.setLayoutData(data);
		text_comment.setBackground(parent.getBackground());
		
		label_feedback = new Label(this, SWT.SHADOW_IN);
		label_feedback.setText("");
		label_feedback.setFont(fonts.get("failure"));
		data = new GridData( GridData.FILL_BOTH);
		data.horizontalSpan=3;
		label_feedback.setLayoutData(data);
		label_feedback.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		checkbox_hints = new Button(this, SWT.CHECK | SWT.RIGHT);
		checkbox_hints.setText("Ich habe die Hinweise gelesen.");
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		checkbox_hints.setLayoutData(data);
		checkbox_hints.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	private void addUserListener() {
		listener = new UserListenerAdapter() {
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see posemuckel.client.model.event.UserListenerAdapter#login(posemuckel.client.model.event.UserEvent)
			 */
			@Override
			public void register(UserEvent event) {
				if (event.isSucceeded()) {
					registered();
				} else {
					userExists();
				}
			}
			
		};
		user.addListener(listener);
	}
	
	protected void _cleanUp() {
		
	}
	
	@SuppressWarnings("unchecked")
	private void getDescriptions() {
		username = GetText.gettext("USERNAME");
		firstname = GetText.gettext("FIRSTNAME");
		surname = GetText.gettext("SURNAME");
		email = GetText.gettext("EMAIL");
		passwd = GetText.gettext("PASSWD");
		location = GetText.gettext("LOCATION");
		comment = GetText.gettext("COMMENT");
		gender = GetText.gettext("GENDER");
		male = GetText.gettext("MALE");
		female = GetText.gettext("FEMALE");
		language = GetText.gettext("LANG");
		confirm = GetText.gettext("CONFIRM");
		pwd_fail = GetText.gettext("PWD_FAIL");
		user_exists = GetText.gettext("USER_EXISTS");
		give_me_a_username = GetText.gettext("GIVE_ME_USERNAME");
		give_me_a_password = GetText.gettext("GIVE_ME_PASSWORD");
		give_me_an_email = GetText.gettext("GIVE_ME_EMAIL");
		// Für jede unterstützte Sprache eine vernünftige
		// Benennung holen: Also Deutsch statt DE bzw. german statt DE.
 		for (int i = 0 ; i < EnumsAndConstants.LANG.length; i++ ) {
			langs.put(GetText.gettext(EnumsAndConstants.LANG[i]),EnumsAndConstants.LANG[i]);
		}
	}

	private void userExists() {
		Runnable run = new Runnable() {

			public void run() {
				text_username.setText("");
				text_passwd.setText("");
				text_passwdconfirm.setText("");
				label_feedback.setForeground(Colors.getWarning());
				label_feedback.setText(user_exists);
			}
		};
		Display.getDefault().asyncExec(run);
	}

	private void registered() {
		Runnable run = new Runnable() {

			public void run() {
				label_feedback.setForeground(Colors.getInfo());
				label_feedback.setText("");
				config.setconfig("DEFAULT_USER",text_username.getText());
				config.setconfig("DEFAULT_PASS",text_passwd.getText());
				dialog.focusOnLogin();
			}
		};
		Display.getDefault().asyncExec(run);
	}

	public void setUserFeedback(String userfeedback, int type) {
		label_feedback.setText(userfeedback);
	}

	public void loadDefaults() {}

	public void save2Config() {
		config.setconfig("LANG","DE");
	}

	/**
	 * Hier wird nun die Registrierung angestoßen.
	 */
	public void performAction() {
			if(!checkbox_hints.getSelection()) {
				dialog.focusOnHints("Sie sollten diese Hinweise lesen!",2);
				return;
			}

			String pass1 = text_passwd.getText();
			String pass2 = text_passwdconfirm.getText();
			if (!pass1.equals(pass2)) {
				text_passwd.setText("");
				text_passwdconfirm.setText("");
				label_feedback.setForeground(Colors.getWarning());
				label_feedback.setFont(dialog.getFonts().get("failure"));
				label_feedback.setText(pwd_fail);

			} else {
				// Das Feld für gender muss in ein für die
				// Datenbank verständliches Format umgesetzt
				// werden. Es sind nur "MALE" oder "FEMALE" erlaubt.
				// Jens
				String gender = combo_gender.getText();
				if ( gender.equals(male) )
					gender = EnumsAndConstants.GENDER[0];
				else if ( gender.equals(female) )
					gender = EnumsAndConstants.GENDER[1];
				
				// Hier muss man das passende Sprachkürzel
				// zu der gewählten Sprache holen.
				String lang = (String)langs.get(combo_language.getText());
				
				// Erstmal die Eingabedaten holen:
				String username = text_username.getText();
				// statt String passwd -> pass1 enthält das Passwort (s.o.)
				String firstname = text_firstname.getText();
				String surname = text_surname.getText();
				String email = text_email.getText(); 
				String location = text_location.getText();
				String comment = text_comment.getText();
				
				comment = GetText.replaceRN(comment);
				
				// Überprüfung der Eingaben
				if ( !(username.length() > 0) ) {
					label_feedback.setForeground(Colors.getWarning());
					label_feedback.setText(give_me_a_username);
					return;
				}
				if ( !(email.length() > 0) ) {
					label_feedback.setForeground(Colors.getWarning());
					label_feedback.setText(give_me_an_email);
					return;
				}
				if ( !(pass1.length() > 0) ) {
					label_feedback.setForeground(Colors.getWarning());
					label_feedback.setText(give_me_a_password);
					return;
				}						
				dialog.saveAll();
				Client.initBackend();	
				user.register(username,
							  pass1, 
							  firstname,
							  surname,
							  email, 
							  lang, 
							  gender,
							  location,
							  comment);
			}
	}
	
	private class Cleaner implements DisposeListener {

		public void widgetDisposed(DisposeEvent e) {
			user.removeListener(listener);
		}
		
	}

	public boolean validInput() {
		return true;
	}

	public void treatInputError() {
		// TODO Auto-generated method stub
		
	}

}