package posemuckel.client.gui;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.graphics.Color;

import posemuckel.common.Config;
import posemuckel.common.GetText;


public class ServerComposite extends Composite implements TabContent {

	private String hostname;
	private String port;
	private String hostError;
	private String portError;
	
	private Label label_serverhost;
	private Combo combo_servers;
	private Label label_serverport;
	private Combo combo_serverports;
	private Label label_feedback;
	private Config config = Config.getInstance();
	private int cnt = 0; // Anzahl der Server, aus der Config
	
	private Color white;
	private FontRegistry fonts;
	
	public ServerComposite(Composite parent, LoginDialog dialog) {
		super(parent, SWT.NONE);
		this.fonts = dialog.getFonts();
		getDescriptions();
		GridLayout gridlayout = new GridLayout(2,false);
		gridlayout.marginHeight = 30;
		gridlayout.marginWidth = 30;
		gridlayout.horizontalSpacing = 10;
		gridlayout.verticalSpacing = 10;
		this.setLayout(gridlayout);
		
		white = this.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		this.setBackground(white);

		label_serverhost = new Label(this, SWT.SHADOW_NONE);
		GridData data = new GridData( GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan=1;
		label_serverhost.setLayoutData(data);
		label_serverhost.setText(hostname + ":");
		label_serverhost.setBackground(white);
		
		combo_servers = new Combo(this, SWT.DROP_DOWN);
		data = new GridData( GridData.GRAB_HORIZONTAL );
		data.horizontalSpan=1;
		data.minimumWidth = 200;
		combo_servers.setLayoutData(data);
		combo_servers.setBackground(parent.getBackground());
		
		label_serverport = new Label(this, SWT.SHADOW_NONE);
		data = new GridData( GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan=1;
		label_serverport.setLayoutData(data);
		label_serverport.setText(port + ":");
		label_serverport.setBackground(white);
		
		combo_serverports = new Combo(this, SWT.DROP_DOWN);
				data = new GridData( GridData.GRAB_HORIZONTAL );
		data.horizontalSpan=1;
		data.minimumWidth = 200;
		combo_serverports.setLayoutData(data);
		combo_serverports.setBackground(parent.getBackground());
		combo_serverports.addModifyListener(
				// Dieser Listener soll "falsche" Einagben verhindern
				new ModifyListener()
				{
					public void modifyText(ModifyEvent event) {
						Combo combo = (Combo)event.getSource();
						String txt = combo.getText();
						if (!txt.equals("") ) {
							try {
								int port = Integer.valueOf(txt);
								if ( port == 0 ) {
									combo.setText("");
								}
							} catch (NumberFormatException e) {
								Messages.showError("Es sind nur Ganzzahlen zulässig!",GetText.gettext("ERROR_ON_INPUT_TITLE"));
								combo.setText("");
							}
						}
					}
				}
		);

		
		label_feedback = new Label(this, SWT.SHADOW_IN);
		data = new GridData( GridData.FILL_HORIZONTAL );
		label_feedback.setFont(fonts.get("failure"));
		data.horizontalSpan=2;		
		label_feedback.setLayoutData(data);
		label_feedback.setText("");
		label_feedback.setBackground(white);
	
		
	}
	
	private void getDescriptions() {
		hostname = GetText.gettext("HOSTNAME");
		port = GetText.gettext("PORT");
		hostError = GetText.gettext("HOST_ERROR");
		portError = GetText.gettext("PORT_ERROR");
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.gui.TabContent#setUserFeedback(java.lang.String, int)
	 */
	public void setUserFeedback(String userfeedback, int type){
		label_feedback.setText(userfeedback);
	}
	
	/* (non-Javadoc)
	 * @see posemuckel.client.gui.TabContent#loadDefaults()
	 */
	public void loadDefaults() {
		String host;
		String port;
		String num = config.getconfig("NUM_SERVERS"); // Anzahl der Server
		String def = config.getconfig("DEFAULT_SERVER"); // Default-Einstellung
		if (num == null) {
			cnt = 0;
			return;
		} else {
			int stdServer = 1;
			if (def != null)
				stdServer = Integer.valueOf(def);
			cnt = Integer.valueOf(num);
			
			// Jetzt hauen wir die Server-Hosts und Ports in die
			// Combo-Boxen rein. Den default-Server setzen wir
			// auch gleich als Standard.
			for (int i=1; i<=cnt; i++) {
				host = config.getconfig("SERVER_"+String.valueOf(i)+"_HOST");
				port = config.getconfig("SERVER_"+String.valueOf(i)+"_PORT");
				if (host != null)
					combo_servers.add(host);
				if (port != null)
					combo_serverports.add(port);
				if (i==stdServer) {
					combo_servers.setText(host);
					combo_serverports.setText(port);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.gui.TabContent#save2Config()
	 */
	public void save2Config() {
		String host = combo_servers.getText();
		String port = combo_serverports.getText();
		String oldhost;
		String oldport;
		boolean savethis = true;
		// Prüfe, ob der Server schon gespeichert ist:
		for (int i=1; i<=cnt; i++) {
			oldhost = config.getconfig("SERVER_"+String.valueOf(i)+"_HOST");
			oldport = config.getconfig("SERVER_"+String.valueOf(i)+"_PORT");
			if (oldhost != null && oldport != null)
				if( host.equals(oldhost) && port.equals(oldport)) {
					config.setconfig("DEFAULT_SERVER",String.valueOf(i));
					savethis = false;
				}	
		}
		// Wenn wir einen neuen Server haben, dann bitte speichern.
		if (savethis) {
			cnt++;
			config.setconfig("SERVER_"+cnt+"_HOST",host);
			config.setconfig("SERVER_"+cnt+"_PORT",port);
			config.setconfig("NUM_SERVERS",String.valueOf(cnt));
			config.setconfig("DEFAULT_SERVER",String.valueOf(cnt));
		}
	}

	public void performAction() {}
	
	public boolean validInput() { 
		String host = combo_servers.getText();
		String port = combo_serverports.getText();
		if ( host.equals("") || port.equals("") )
			return false;
		else
			return true; 
	}

	public void treatInputError() {
		String host = combo_servers.getText();
		String port = combo_serverports.getText();
		if (host.equals("")) {
			label_feedback.setForeground(Colors.getWarning());
			label_feedback.setText(hostError);
		}
		if (port.equals("")) {
			label_feedback.setForeground(Colors.getWarning());
			label_feedback.setText(portError);
		}	
	}

}
