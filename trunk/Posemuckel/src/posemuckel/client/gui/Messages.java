/**
 * 
 */
package posemuckel.client.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Gibt eine Fehlermeldung in einer MessageBox aus. 
 * 
 * @author Posemuckel Team
 *
 */

public class Messages {
	
	/**
	 * Zeigt eine Fehlermeldung an. Es wird eine Shell erzeugt, falls keine
	 * vorhanden ist.
	 * 
	 * @param warning Fehlermeldung
	 * @param title Titel der Meldung
	 */
	public static void showError(String warning, String title) {
		show(warning, title, SWT.ICON_ERROR);
	}
	
	/**
	 * Zeigt eine Information an. Es wird eine Shell erzeugt, falls keine
	 * vorhanden ist.
	 * 
	 * @param info Information
	 * @param title Titel der Meldung
	 */
	public static void showInfo(String info, String title) {
		show(info, title, SWT.ICON_INFORMATION);
	}
	
	private static void show(String mes, String title, int style) {
		Shell shell;
		if(Display.getCurrent() == null || Display.getCurrent().getActiveShell() == null) {
			shell = new Shell();
		} else {
			shell = Display.getCurrent().getActiveShell();
		}
  		MessageBox what_a_mess = new MessageBox( shell,style);
		what_a_mess.setMessage(mes);
		what_a_mess.setText(title);
		what_a_mess.open();
	}
	
	public static String getInput(String mes, String title) {
		Shell shell;
		if(Display.getCurrent() == null || Display.getCurrent().getActiveShell() == null) {
			shell = new Shell();
		} else {
			shell = Display.getCurrent().getActiveShell();
		}
		MessageBox what_a_mess = new MessageBox( shell, SWT.ICON_QUESTION);
  		what_a_mess.setMessage(mes);
  		what_a_mess.setText(title);
  		return what_a_mess.open() + "";
	}

}
