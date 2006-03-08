/**
 * 
 */
package posemuckel.client.gui.browser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import posemuckel.client.gui.NotifyOthersDialog;
import posemuckel.client.model.MemberList;
import posemuckel.client.model.Model;
import posemuckel.client.model.Person;
import posemuckel.client.model.Project;
import posemuckel.common.Config;
import posemuckel.common.EnumsAndConstants;
import posemuckel.common.GetText;

/**
 * Enthält die Actions, die in der Toolbar für den Browser benötigt werden. Sowohl
 * LabBrowser als auch CreateBrowser brauchen dafür einen Zugang zu den Actions.
 * <br><br>
 * Das Menu enthält folgende Actions für den Browser:
 * <ul>
 * <li>back</li>
 * <li>forward</li>
 * <li>stop</li>
 * <li>refresh</li>
 * <li>hold up</li>
 * </ul>
 * 
 * Die hold-up-Action erzeugt einen Screenshot vom Browserinhalt und öffnet einen
 * Dialog, über den der Screenshot an andere Teilnehmer des Projektes gesendet werden
 * kann.
 *  
 * @author Posemuckel Team
 *
 */
class BrowserMenu {
	
	private Browser browser;
	private Composite browserComp;
	private String myname;
	private String url=null;
	
	private Action backAction;
	private Action forwardAction;
	
	/**
	 * Initialisiert das BrowserMenu. Damit die Actions korrekt funktionieren, 
	 * müssen noch über <code>setBrowser</code> und <code>setBrowserComposite</code>
	 * die von den Actions benötigten Attribute gesetzt werden. Bei Initialisierung
	 * von BrowserMenu existieren diese jedoch noch nicht.
	 *
	 */
	BrowserMenu() {
		Config config = Config.getInstance();
		url = config.getconfig("HOME_URL");
		if(url == null) {
			// Setze den Standard auf posemuckel.no-ip.org
			url = "http://posemuckel.no-ip.org";
			config.setconfig("HOME_URL",url);
			try {
				config.saveToFile(EnumsAndConstants.CLIENT_CONFIG_FILE);
			} catch (FileNotFoundException e) {
				// Also wenn hier ein Problem auftritt,
				// dann sollte das auch ausgegeben werden,
				// weil die Datei bereits existieren muss.
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		myname = Model.getModel().getUser().getNickname();
	}
	
	/**
	 * Initialisiert die Actions. Der Browser muss an dieser Stelle noch nicht 
	 * bekannt sein.
	 * @return die Actions, die der Browser benötigt
	 */
	IAction[] createMenuItems() {
		IAction[] actions = new IAction[6];
		actions[0] = createBackAction();
		actions[1] = createForwardAction();
		actions[2] = createRefreshAction();
		actions[3] = createStopAction();
		actions[4] = createHomeAction();
		actions[5] = createHoldUpAction();		
		//TODO wozu ist das gut?
		//ImageDescriptor id = ImageDescriptor.createFromFile(this.getClass(),"web.gif");
		
//		final ToolItem forwardToolItem = new ToolItem(bar, SWT.PUSH);
		return actions;
	}
	
	private IAction createHomeAction() {
		ImageDescriptor id = ImageDescriptor.createFromFile(this.getClass(),"home.gif");	
		Action action = new Action(GetText.gettext("HOMEPAGE") ,id) {
			
			public void run() {
				setURL(url);
			}
		};
		return action;
	}
	
	private IAction createRefreshAction() {
		ImageDescriptor id = ImageDescriptor.createFromFile(this.getClass(),"refresh.gif");
		Action action = new Action(GetText.gettext("RELOAD") ,id) {
			
			public void run() {
				browser.refresh();
			}
		};
		return action;
	}
	
	private IAction createStopAction() {
		ImageDescriptor id = ImageDescriptor.createFromFile(this.getClass(),"stop.gif");
		Action action = new Action(GetText.gettext("STOP") ,id) {
			
			public void run() {
				browser.stop();
			}
		};
		return action;
	}
	
	private IAction createHoldUpAction() {
		ImageDescriptor id = ImageDescriptor.createFromFile(this.getClass(),"cut_edit.gif");
		Action action = new Action( GetText.gettext("HOLD_UP"),id) {
			
			public void run() {
				final Image image=createHighlightImage(browser, browserComp);
				MemberList memberlist = Model.getModel().getOpenProject().getMemberList();
				ArrayList<Person> pariticipants = memberlist.getMembers();
				ArrayList<String> members = new ArrayList<String>();
				for( Person p : pariticipants ) {
					String buddyname = p.getNickname();
					if(!myname.equals(buddyname) && p.getState().equals(Person.BROWSING))
						members.add(buddyname);
				}
				Shell shell = Display.getCurrent().getActiveShell();
				if ( members.isEmpty() ) {
					MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK );
					mb.setText(GetText.gettext("NO_HOLD_UP_FUNCTION"));
					mb.setMessage(GetText.gettext("NO_OTHER_USER"));
					mb.open();
				} else {
					NotifyOthersDialog notify = new NotifyOthersDialog(shell,image,browser.getUrl(),members);
					notify.open();
				}
			}
		};
		return action;
	}
	
	private IAction createForwardAction() {
		ImageDescriptor id = ImageDescriptor.createFromFile(this.getClass(),"forward.gif");
		forwardAction = new Action(GetText.gettext("FORWARD"), id) {
			
			public void run() {
				Project project = Model.getModel().getOpenProject();
				if ( project != null ) {
					project.useViewing();
				}
				browser.forward();
			}
		};
		forwardAction.setEnabled(false);
		return forwardAction;
	}
	
	private IAction createBackAction() {
		ImageDescriptor id = ImageDescriptor.createFromFile(this.getClass(),"backward.gif");
		backAction = new Action(GetText.gettext("BACK"), id) {

			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				Project project = Model.getModel().getOpenProject();
				if ( project != null ) {
					project.useViewing();
				}
				browser.back();
			}
			
		};
		backAction.setEnabled(false);
		return backAction;
	}
	
	/**
	 * Setzt den Browser, auf den sich die Actions beziehen.
	 * @param browser Browser
	 */
	void setBrowser(Browser browser) {
		this.browser = browser;
	}
	
	/**
	 * Setzt das Composite, in dem der Browser enthalten ist. Das Composite wird
	 * bei der Hochhaltefunktion zur Bestimmung der Größe des Bildes benötigt.
	 * @param comp Composite, in dem der Browser enthalten ist
	 */
	void setBrowserComposite(Composite comp) {
		browserComp = comp;
	}
	
	/**
	 * Erzeugt einen Screenshot des Browsers und gibt ihn zurück.
	 * @param browser: Control von dem ein Screenshot erstellt werden soll
	 * @param browserComp: Composite in dem sich das Control befindet zur festlegung der Grösse des Screenshots
	 * @return: Screenshot des Browsers	
	 */
	Image createHighlightImage(Browser browser, Composite browserComp) {
		Point browserCompSize = browserComp.getSize();
		GC gc = new GC(browser);
		final Image image =
			new Image(browser.getDisplay(), browserCompSize.x-40, browserCompSize.y-40);
		gc.copyArea(image, 20, 20);
		gc.dispose();
		return image;
	}
	
	/**
	 * Lädt die URL im Browser.
	 * @param url die URL
	 */
	void setURL(String url) {
		if(url != null) {
			Model.getModel().getOpenProject().jumpToURL(url);
			//System.out.println("set url " + url);
			browser.setUrl(url);
		}
	}
	
	/**
	 * (De-)Aktiviert den BackButton.
	 * @param enabled true, falls der Button aktiviert werden soll
	 */
	void setBackEnabled(boolean enabled) {
		backAction.setEnabled(enabled);
	}
	
	/**
	 * (De-)Aktiviert den ForwardButton.
	 * @param enabled true, falls der Button aktiviert werden soll
	 */
	void setForwardEnabled(boolean enabled) {
		forwardAction.setEnabled(enabled);
	}
	
	
}
