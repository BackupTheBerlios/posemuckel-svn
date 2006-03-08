/**
 * 
 */
package posemuckel.client.gui;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import posemuckel.common.GetText;

/**
 * Dieser Dialog fordert den Anwender zu einer einzeiligen Eingabe auf. 
 * Alle Textangaben werden aus der Ressource f�r die jeweilige Sprache bezogen.
 * Es kann eine Mindestl�nge und eine Maximall�nge f�r die Eingabe angegeben werden
 * @author Posemuckel Team
 *
 */
public class MyInputDialog {
	
	private int minLenght;
	private int maxLenght;
	private InputDialog inputDialog;
	
	private String titleKey;
	private String promptKey;
	private String defaultText;
	private String errorKey;
	
	/**
	 * Erstellt den Eingabedialog. Der Dialog muss noch mit <code>openDialog()</code> 
	 * ge�ffnet werden. Die Schl�ssel beziehen sich auf die Textressourcen f�r
	 * die Mehrsprachigkeit.
	 * 
	 * @param min minimale Eingabel�nge
	 * @param max maximale Eingabel�nge
	 * @param titleKey Schl�ssel f�r den Titel
	 * @param promptKey Schl�ssel f�r die Eingabeaufforderung.
	 * @param defaultText der Standardtitel
	 * @param errorKey Hinweis, wenn die Eingabe die L�ngenangaben nicht einh�lt
	 */
	public MyInputDialog(int min, int max, String titleKey, 
			String promptKey, String defaultText, String errorKey) {
		this.titleKey = titleKey;
		this.promptKey = promptKey;
		this.defaultText = defaultText;
		this.errorKey = errorKey;
		minLenght = min;
		maxLenght = max;
	}
	
	/**
	 * �ffnet den Eingabedialog. Der Dialog ist modal.
	 *
	 */
	public void openDialog() {
		GetText.gettext(titleKey);
		Shell shell = Display.getCurrent().getActiveShell();
		inputDialog = new InputDialog( shell,
				GetText.gettext(titleKey), //dialog title
				GetText.gettext(promptKey), //dialog prompt
				defaultText, //default text
				getValidator() ); //validator to use
		inputDialog.open();
	}
	
	/**
	 * Gibt den zu verwendenden Validator aus. Wenn <code>null</code> ausgegeben
	 * wird, verwendet der InputDialog keinen Validator.
	 *  
	 * @return der Validator oder null
	 */
	protected IInputValidator getValidator() {
		IInputValidator validator = new IInputValidator() {
			public String isValid(String text) { //return an error message,
				if(text.length() < minLenght || text.length() > maxLenght) //or null for no error
					return GetText.gettext(errorKey);
				else
					return null;
			}
		};
		return validator;
	}
	
	/**
	 * Gibt die Eingabe des Anwenders aus. Wenn der Anwender die Eingabe nicht
	 * mit OK best�tigt hat, wird <code>null</code> ausgegeben.
	 * @return die Anwendereingabe oder <code>null</code>
	 */
	public String getUserInput() {
		return inputDialog.getValue();
	}
	
}
