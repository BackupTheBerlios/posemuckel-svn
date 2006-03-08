/**
 * 
 */
package posemuckel.client.gui;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import posemuckel.client.model.Model;
import posemuckel.client.model.PersonsData;
import posemuckel.client.model.User;
import posemuckel.client.model.event.UserEvent;
import posemuckel.client.model.event.UserListener;
import posemuckel.client.model.event.UserListenerAdapter;
import posemuckel.common.EnumsAndConstants;
import posemuckel.common.GetText;

/**
 * Ein einfaches Dialogfenster zum Anzeigen des Profils
 * eines Benutzers. Je nachdem, ob es sich um den lokalen
 * Benutzer des Clients handelt, werden die Daten editierbar
 * angezeigt oder nur zum Lesen.
 * 
 * @author Posemuckel Team
 */
public class ProfileDialog extends Dialog {

	private Label label_firstname;
	private Label label_surname;
	private Label label_gender;
	private Label label_location;
	private Label label_language;
	private Label label_email;
	private Label label_username;
	private Label label_comment;
	
	private String firstname;
	private String surname;
	private String gender;
	private String location;
	private String language;
	private String email;
	private String username;
	private String comment;
	private String genderval;
	private String langval;
	private String nocomment;
	private String close;
	private String buddyadd;
	private String male;
	private String female;
	private String title;
	private String comment_set;
	private HashMap<String,String> langs = new HashMap<String,String>();
	
	
	private PersonsData person;
	private Label label_firstname2;
	private Label label_surname2;
	private Label label_gender2;
	private Label label_location2;
	private Label label_language2;
	private Label label_email2;
	private Label label_username2;
	private Text text_comment;
	private Label result;

	private boolean editable = false;
	private boolean active = true;
	private boolean hasFeedback = false;
	
	private Text text_firstname;
	private Text text_surname;
	private Text text_location;
	private Combo combo_language;
	private Text text_email;
	private Combo combo_gender;
	private String give_me_a_password;
	private String give_me_an_email;
	private Label label_passwd;
	private Text text_passwd;
	private String confirm;
	private Label label_passwdconfirm;
	private Text text_passwdconfirm;
	private String passwd;
	private String pwd_fail;
	private String input_error;
	private String profileButton;
	private String successMessage;
	private String errorMessage;
	
	private User user;
	private UserListener listener;
	private String nothingchanged;
	
	private static final int ADD_BUDDY_ID = IDialogConstants.NO_TO_ALL_ID +1;
	private static final int SET_PROFILE_BUTTON = IDialogConstants.NO_TO_ALL_ID +2;

	
	/**
	 * @param arg Die Shell, in der der Dialog läuft.
	 * @param person Die Person, deren Daten angezeigt werden sollen.
	 */
	public ProfileDialog(Shell arg, PersonsData person) {
		super(arg);
		this.person = person;
		user = Model.getModel().getUser();
		//wenn die Anwenderdaten angezeigt werden,
		//sollten die Felder editierbar sein
		editable = isUser();
		getDescriptions();
	}


	private void getDescriptions() {
		title  = GetText.macroreplace(GetText.gettext("PROFILE_TITLE"),"USER",person.getNickname());
		username = GetText.gettext("USERNAME");
		firstname = GetText.gettext("FIRSTNAME");
		surname = GetText.gettext("SURNAME");
		pwd_fail = GetText.gettext("PWD_FAIL");
		input_error = GetText.gettext("ERROR_ON_INPUT_TITLE");
		email = GetText.gettext("EMAIL");
		location = GetText.gettext("LOCATION");
		comment = GetText.gettext("COMMENT");
		passwd = GetText.gettext("PASSWD");
		confirm = GetText.gettext("CONFIRM");
		gender = GetText.gettext("GENDER");
		genderval = GetText.gettext(person.getGender());
		language = GetText.gettext("LANG");
		langval = GetText.gettext(person.getLang());
		nocomment = GetText.gettext("NO_COMMENT_SET");
		close = GetText.gettext("CLOSE");
		buddyadd = GetText.gettext("ADD_BUDDY");
		male = GetText.gettext("MALE");
		female = GetText.gettext("FEMALE");
		give_me_a_password = GetText.gettext("GIVE_ME_PASSWORD");
		give_me_an_email = GetText.gettext("GIVE_ME_EMAIL");
		profileButton = GetText.gettext("PROFILE_BUTTON");
		successMessage = GetText.gettext("PROFILE_SUCCESS");
		errorMessage = GetText.gettext("PROFILE_ERROR");
		nothingchanged = GetText.gettext("PROFILE_NOTHING_CHANGED");
		// Für jede unterstützte Sprache eine vernünftige
		// Benennung holen: Also Deutsch statt DE bzw. german statt DE.
 		for (int i = 0 ; i < EnumsAndConstants.LANG.length; i++ ) {
			langs.put(GetText.gettext(EnumsAndConstants.LANG[i]), EnumsAndConstants.LANG[i]);
		}
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#close()
	 */
	@Override
	public boolean close() {
		if(listener != null)user.removeListener(listener);
		return super.close();
	}


	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(title);
		getShell().setImage(ImageManagment.getRegistry().get(ImageManagment.SHELL_ICON));
		Composite comp = (Composite)super.createDialogArea(parent);
		GridLayout gridlayout = new GridLayout(2,false);
		gridlayout.marginHeight = 20;
		gridlayout.marginWidth = 20;
		comp.setLayout(gridlayout);
		
		// Username field
		label_username = new Label(comp, SWT.SHADOW_NONE);
		label_username.setText(username+":");
		
		label_username2 = new Label(comp, SWT.SHADOW_NONE);
		label_username2.setText(person.getNickname());
	
		
		// Firstname field
		label_firstname = new Label(comp, SWT.SHADOW_NONE);
		label_firstname.setText(firstname+":");
		
		if( !editable ) {
			label_firstname2 = new Label(comp, SWT.SHADOW_NONE);
			label_firstname2.setText(person.getFirstName());
		} else {
			text_firstname = new Text(comp, SWT.SINGLE | SWT.BORDER);
			GridData data = new GridData( GridData.FILL_HORIZONTAL );
			data.minimumWidth = 200;
			text_firstname.setLayoutData(data);
			text_firstname.setText(person.getFirstName());
			text_firstname.setTextLimit(20);
			addVerifyListener(text_firstname);
		}
		// Surname field
		label_surname = new Label(comp, SWT.SHADOW_NONE);
		label_surname.setText(surname+":");

		if( !editable ) {		
			label_surname2 = new Label(comp, SWT.SHADOW_NONE);
			label_surname2.setText(person.getSurname());
		} else {
			text_surname = new Text(comp, SWT.SINGLE | SWT.BORDER);
			GridData data = new GridData( GridData.FILL_HORIZONTAL );
			data.minimumWidth = 200;
			text_surname.setLayoutData(data);
			text_surname.setText(person.getSurname());
			text_surname.setTextLimit(20);
			addVerifyListener(text_surname);
		}
		// Gender combo box
		label_gender = new Label(comp, SWT.SHADOW_NONE);
		label_gender.setText(gender+":");
		
		if( !editable ) {
			label_gender2 = new Label(comp, SWT.SHADOW_NONE);
			label_gender2.setText(genderval);
		} else {
			combo_gender = new Combo(comp, SWT.READ_ONLY);
			GridData data = new GridData( GridData.FILL_HORIZONTAL );
			combo_gender.setLayoutData(data);
			combo_gender.setItems(new String[] {male, female});
			combo_gender.setText(genderval);
			addSelectionListener(combo_gender);
		}	
			
		// Location field
		label_location = new Label(comp, SWT.SHADOW_NONE);
		label_location.setText(location+":");
	
		if( !editable ) {		
			label_location2 = new Label(comp, SWT.SHADOW_NONE);
			label_location2.setText(person.getLocation());
		} else {
			text_location = new Text(comp, SWT.SINGLE | SWT.BORDER);
			GridData data = new GridData( GridData.FILL_HORIZONTAL );
			data.minimumWidth = 200;
			text_location.setLayoutData(data);
			text_location.setText(person.getLocation());
			text_location.setTextLimit(20);
			addVerifyListener(text_location);
		}
		
		// Language field
		label_language = new Label(comp, SWT.SHADOW_NONE);
		label_language.setText(language + ":");

		if( !editable ) {		
			label_language2 = new Label(comp, SWT.SHADOW_NONE);
			label_language2.setText(langval);
		} else {
			combo_language = new Combo(comp, SWT.READ_ONLY);
			Set keys = langs.keySet();
			Object[] arr = keys.toArray();
			for ( Object lang : arr ) {
				combo_language.add((String)lang);
			}
			GridData data = new GridData( GridData.FILL_HORIZONTAL );
			data.minimumWidth = 200;
			combo_language.setLayoutData(data);
			combo_language.setText(langval);
			addSelectionListener(combo_language);
		}
		
		// Email field
		label_email = new Label(comp, SWT.SHADOW_NONE);
		label_email.setText(email+":");

		if( !editable ) {
			label_email2 = new Label(comp, SWT.SHADOW_NONE);
			label_email2.setText(person.getEmail());
		} else {
			text_email = new Text(comp, SWT.SINGLE | SWT.BORDER);
			GridData data = new GridData( GridData.FILL_HORIZONTAL );
			data.minimumWidth = 200;
			text_email.setLayoutData(data);
			text_email.setText(person.getEmail());
			text_email.setTextLimit(60);
			addVerifyListener(text_email);
		}
		
		// Passwort-Felder:
		if( editable ) {
			label_passwd = new Label(comp, SWT.SHADOW_NONE);
			label_passwd.setText(passwd+":");
		
			text_passwd = new Text(comp, SWT.PASSWORD | SWT.BORDER);
			GridData data = new GridData( GridData.FILL_HORIZONTAL );
			data.minimumWidth = 200;
			text_passwd.setText(user.getPassword());
			text_passwd.setLayoutData(data);
			text_passwd.setTextLimit(20);
			addVerifyListener(text_passwd);

			// Confirm password field
			label_passwdconfirm = new Label(comp, SWT.SHADOW_NONE);
			label_passwdconfirm.setText(confirm + ":");

			text_passwdconfirm = new Text(comp, SWT.PASSWORD | SWT.BORDER);
			GridData data2 = new GridData( GridData.FILL_HORIZONTAL );
			data.minimumWidth = 200;
			text_passwdconfirm.setText(user.getPassword());
			text_passwdconfirm.setLayoutData(data2);
			text_passwdconfirm.setTextLimit(20);
			addVerifyListener(text_passwdconfirm);
		}
		
		// Comment field
		label_comment = new Label(comp, SWT.SHADOW_NONE);
		label_comment.setText(comment+":");
		
		text_comment = new Text(comp, SWT.MULTI |SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		GridData data = new GridData( GridData.FILL_HORIZONTAL );
		data.horizontalSpan = 2;
		data.heightHint = 100;
		data.widthHint = 300;
		text_comment.setLayoutData(data);
		text_comment.setEditable(editable);
		if( !person.getComment().equals("") ) {
			comment_set = person.getComment();
			text_comment.setText(comment_set);
		} else {
			comment_set = nocomment;
			text_comment.setText(nocomment);
		}
		if(editable) {
			addVerifyListener(text_comment);
			result = new Label(comp, SWT.SHADOW_NONE);
			GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gridData.horizontalSpan = 2;
			result.setLayoutData(gridData);
		}
		return comp;
	}
	

	/**
	 * Stellt fest, ob der Dialog schon geschlossen wurde.
	 * @return true, falls der Dialog schon geschlossen wurde
	 */
	private boolean isDisposed() {
		return (getShell() == null) || this.getShell().isDisposed();
	}


	protected void createButtonsForButtonBar(Composite parent) {
		if(isUser()) {
			createButton(parent,SET_PROFILE_BUTTON,profileButton,true);
			addListener();
		}
		else if(isBuddy()) {
			//TODO Button zum Löschen eines Buddys einfügen
		} else {
			createButton(parent,ADD_BUDDY_ID,buddyadd,true);
		}
		createButton(parent,IDialogConstants.CLOSE_ID,close,true);
	}
	
	/**
	 * &Uuml;berpr&uuml;ft, ob die PersonData des Anwenders angezeigt werden
	 * soll.
	 * @return true, falls die Daten des Anwenders angezeigt werden sollen
	 */
	protected boolean isUser() {
		return user.getNickname().equals(person.getNickname());
	}
	
	/**
	 * &Uuml;berpr&uuml;ft, ob die PersonData eines Buddys angezeigt werden
	 * soll.
	 * @return true, falls die Daten eines Buddys angezeigt werden sollen
	 */
	protected boolean isBuddy() {
		/*
		 * das Profil darf nicht das vom Anwender sein und der Nickname
		 * darf nicht in der Buddyliste enthalten sein
		 */
		return (user.getBuddyList().getMember(person.getNickname())!= null);
	}
	
	protected void buttonPressed(int buttonID) {
		switch (buttonID) {
		case IDialogConstants.CLOSE_ID: {
			close();
			break;
		}
		case ADD_BUDDY_ID: {
			//wenn der Dialog offen bleibt, kann man auch zweimal auf den Button 
			//klicken: das führt aber zu einem Fehler
			if(active && !isBuddy()) {
				active = false;
				Model.getModel().getUser().getBuddyList().addBuddy(person.getNickname());
			} else if(active) {
				hasFeedback = true;
				result.setText(person.getNickname() +" "+ GetText.gettext("ALREADY_A_BUDDY"));
			}
			break;
		}
		case SET_PROFILE_BUTTON: {
			if(active) {
				String pass1 = text_passwd.getText();
				String pass2 = text_passwdconfirm.getText();
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
				
				// statt String passwd -> pass1 enthält das Passwort (s.o.)
				String firstname = text_firstname.getText();
				String surname = text_surname.getText();
				String email = text_email.getText(); 
				String location = text_location.getText();
				String comment = text_comment.getText();
				
				if( firstname.equals(person.getFirstName())
						&& surname.equals(person.getSurname())
						&& email.equals(person.getEmail())
						&& location.equals(person.getLocation())
						&& comment.equals(comment_set)
						&& lang.equals(person.getLang())
						&& gender.equals(person.getGender())
						&& pass1.equals(user.getPassword())
						&& pass2.equals(user.getPassword())			 
				) {
					// Es wurde nichts verändert, also gibts
					// auch nix zu tun.
					result.setText(nothingchanged);
					hasFeedback = true;
					return;
				}
				
				if (!pass1.equals(pass2)) {
					text_passwd.setText("");
					text_passwdconfirm.setText("");
					MessageBox mb = new MessageBox(this.getShell(), SWT.ICON_ERROR | SWT.OK );
					mb.setText(input_error);
					mb.setMessage(pwd_fail);
					mb.open();
					return;
				} else {
					
					comment = comment.replace('\n',' ');
					
					if ( !(email.length() > 0) ) {
						MessageBox mb = new MessageBox(this.getShell(), SWT.ICON_ERROR | SWT.OK );
						mb.setText(input_error);
						mb.setMessage(give_me_an_email);						
						mb.open();
						return;
					}
					if ( !(pass1.length() > 0) ) {
						MessageBox mb = new MessageBox(this.getShell(), SWT.ICON_ERROR | SWT.OK );
						mb.setText(input_error);
						mb.setMessage(give_me_a_password);
						mb.open();
						return;
					}						
					
					// Jetzt muss eine set_profile Nachricht
					//geschickt werden
					active = false;
					user.setProfile(pass1, 
							firstname,
							surname,
							email, 
							lang, 
							gender,
							location,
							comment);
				}
			}
			//close();
			break;
		}
		
		}
	}
	
	private void addVerifyListener(Text text) {
		text.addVerifyListener(new VerifyListener() {

			public void verifyText(VerifyEvent e) {
				// wird bei jeder Textänderung aufgerufen, darum sollte der Aufwand so
				//gering wie irgend möglich sein
				if(hasFeedback) {
					hasFeedback = false;
					result.setText("");
				}
			}
			
		});
	}

	private void addSelectionListener(Combo combo) {
		combo.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				hasFeedback = false;
				result.setText("");
			}
			
		});
	}


	private void addListener() {
		listener = new UserListenerAdapter() {
			/* (non-Javadoc)
			 * @see posemuckel.client.model.event.UserListenerAdapter#profileChanged(posemuckel.client.model.event.UserEvent)
			 */
			@Override
			public void profileChanged(final UserEvent event) {
				Runnable run = new Runnable() {

					public void run() {
						active = true;
						hasFeedback = true;
						if(event.isSucceeded()){
							result.setText(successMessage);
						} else {
							result.setText(errorMessage);
						}

					}
					
				};
				/*
				 * das Runnable darf nur ausgeführt werden, wenn der Dialog
				 * noch existiert: ein Dialog, der bereits geschlossen ist, 
				 * führt zu unerklärlichen Fehlern
				 */
				if(isDisposed()) {
					user.removeListener(listener);
				} else {
					Display.getDefault().asyncExec(run);
				}
			}
			
		};
		user.addListener(listener);
	}

}
