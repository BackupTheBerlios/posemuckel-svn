package posemuckel.client.gui;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import posemuckel.common.Config;
import posemuckel.common.EnumsAndConstants;
import posemuckel.common.GetText;

/**
 * Dieser Login-Dialog basiert vom Layout her auf einer frühen
 * Version von Sandro. Sein Login-Fenster wurde in einen Dialog
 * umgeschrieben und dabei GridLayouts als Layout-Manager verwendet.
 * Die Buttons des Dialogs sind je nach aktivem Tab aktiv oder
 * eben nicht.
 */
public class LoginDialog extends Dialog {

	private String welcome;
	private String loginTab;
	private String regTab;
	private String serverTab;
	private String disclaimerTab;
	
	private TabItem tab_login;
	private TabFolder tabs;
	private TabItem tab_register;
	private TabItem tab_server;
	private TabItem tab_hints;
	private String close;
	private FontRegistry fontRegistry;
	private int selected = 0;
	
	private static final int LOGIN_BUTTON = IDialogConstants.NO_TO_ALL_ID +1;
	private static final int REGISTER_BUTTON = IDialogConstants.NO_TO_ALL_ID +2;
	
	public LoginDialog(Shell shell) {
		super(shell);
		getDescriptions();
		this.setBlockOnOpen(true);
		this.open();
	}
	
	/**
	 * Ruft von jedem Tab die Methode loadDefaults() auf.
	 */
	private void loadAllDefaults() {
		TabItem[] tabitems = tabs.getItems();
		for (TabItem ti : tabitems) {
			TabContent tc = (TabContent)ti.getControl();
			tc.loadDefaults();
		}		
	}

	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(welcome);
		//TODO in die anderen Klassen ebenfalls einfügen
		this.getShell().setImage(ImageManagment.getRegistry().get(
				ImageManagment.SHELL_ICON));
		Composite comp = (Composite)super.createDialogArea(parent);
		GridLayout gridlayout = new GridLayout(1,false);
		gridlayout.marginHeight = 10;
		gridlayout.marginWidth = 10;
		comp.setLayout(gridlayout);
		// Der TabFolder wird mit zwei Tabs gefüllt, dem Login-Tab...
		tabs = new TabFolder(comp, SWT.NONE);
		tabs.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Den Hinweistext im "alten" Tab löschen:
				TabContent tab = (TabContent)(tabs.getItem(selected)).getControl();
				if ( tab != null ) {
					tab.setUserFeedback("",-1);
				}
				selected = tabs.getSelectionIndex();
				switch(selected) {
				case 0: setButtonsEnabled(false,true);
						break;
				case 1: setButtonsEnabled(true,false);
						break;
				default:setButtonsEnabled(false,false);
				}
			}
		});

		tab_login = new TabItem(tabs,SWT.NONE);
		tab_login.setText(loginTab);
		tab_login.setControl(new LoginComposite(tab_login.getParent(),this));
		tab_register = new TabItem(tabs,SWT.NONE);
		tab_register.setText(regTab);
		tab_register.setControl(new RegisterComposite(tab_register.getParent(),this));
		tab_server = new TabItem(tabs,SWT.NONE);
		tab_server.setText(serverTab);
		tab_server.setControl(new ServerComposite(tab_server.getParent(),this));
		tab_hints = new TabItem(tabs,SWT.NONE);
		tab_hints.setText(disclaimerTab);
		tab_hints.setControl(new HintsComposite(tab_hints.getParent(),this));	
		loadAllDefaults();
//		Config config = Config.getInstance();
//		if (config.getconfig("DEFAULT_USER") == null) {
//			setButtonsEnabled(true,false);
//			setTab(1);
//		}
		return comp;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		// hier ist kein Default-Button eingestellt, weil sonst die
		// Textbox-Listener in den Composites nicht funktionieren!
		createButton(parent,REGISTER_BUTTON,regTab,false);		
		createButton(parent,LOGIN_BUTTON,loginTab,false);		
		createButton(parent,IDialogConstants.CLOSE_ID,close,false);
		setButtonsEnabled(false,true);
	}
	
	private void getDescriptions() {
		welcome=GetText.gettext("LOGINWINDOW");
		loginTab=GetText.gettext("LOGIN");
		regTab=GetText.gettext("REGISTER");
		serverTab=GetText.gettext("SERVER");
		disclaimerTab=GetText.gettext("DISCLAIMER_TITLE");
		close = GetText.gettext("CLOSE");
	}

	/**
	 * Diese Methode aktiviert bzw. deaktiviert die einzelnen Buttons.
	 * Ist der entsprechende Parameter auf true gesetzt, dann kann
	 * der Button vom Benutzer angeklickt werden und sonst eben nicht.
	 * @param loginenabled true, wenn der Login-Button aktiv sein soll.
	 * @param regenabled true, wenn der Registrieren-Button aktiv sein soll.
	 */
	protected void setButtonsEnabled(boolean regenabled, boolean loginenabled) {
		Button regbutton = null;
		Button loginbutton = null;
		regbutton = getButton(REGISTER_BUTTON);
		if(regbutton!=null)
			regbutton.setEnabled(regenabled);
		loginbutton = getButton(LOGIN_BUTTON);
		if(loginbutton!=null) 
			loginbutton.setEnabled(loginenabled);
	}

	protected void buttonPressed(int buttonID) {
		switch (buttonID) {
		case IDialogConstants.CLOSE_ID: {
			close();
			break;
		}
		case REGISTER_BUTTON: {
			TabContent regtab = (TabContent)tab_register.getControl();
			TabContent servertab = (TabContent)tab_server.getControl();
			if(!servertab.validInput()) {
				servertab.treatInputError();
				setTab(2);
				break;
			}
			if(!regtab.validInput()) {
				regtab.treatInputError();
				setTab(1);
				break;
			}
			regtab.performAction();
			break;
		}
		case LOGIN_BUTTON: {
			TabContent logintab = (TabContent)tab_login.getControl();
			TabContent servertab = (TabContent)tab_server.getControl();
			if(!servertab.validInput()) {
				servertab.treatInputError();
				setTab(2);
				break;
			}
			if(!logintab.validInput()) {
				logintab.treatInputError();
				setTab(0);
				break;
			}
			logintab.performAction();
			break;
		}
		}
	}

	private void createFontRegistry() {
		fontRegistry = new FontRegistry();
		fontRegistry.put("failure", new FontData[]{new FontData("arial", 13, SWT.BOLD)});
	}
	
	protected FontRegistry getFonts() {
		//die fontRegistry darf erst initialisiert werden, wenn das Display bereits existiert
		if(fontRegistry == null)createFontRegistry();
		return fontRegistry;
	}

	public void focusOnLogin() {
		TabContent logintab = (TabContent)tab_login.getControl();
		logintab.loadDefaults();
		setTab(0);
	}

	/**
	 * Öffnet den Tab mit den Hinweisen.
	 * @param message
	 * @param type
	 */
	public void focusOnHints(String message,int type) {
		TabContent hintstab = (TabContent)tab_hints.getControl();
		hintstab.setUserFeedback(message,type);
		setTab(3);
	}

	/**
	 * Ruft für alle Tabs die Methode save2Config() auf
	 * und veranlasst ein Speichern in der lokalen
	 * Konfigurationsdatei.
	 */
	public void saveAll() {
		
		TabContent tc = (TabContent)tab_login.getControl();
		tc.save2Config();
		tc = (TabContent)tab_register.getControl();
		tc.save2Config();
		tc = (TabContent)tab_server.getControl();
		tc.save2Config();
		Config config = Config.getInstance();
		try {
			config.saveToFile(EnumsAndConstants.CLIENT_CONFIG_FILE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setTab(int num) {
		switch(num) {
		case 0: setButtonsEnabled(false,true);
				break;
		case 1: setButtonsEnabled(true,false);
				break;
		default:setButtonsEnabled(false,false);
		}
		selected = num;
		tabs.setSelection(num);
	}
	
}

