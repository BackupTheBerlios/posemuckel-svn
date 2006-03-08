package posemuckel.client.gui.actions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Text;

import posemuckel.client.gui.browser.CreateBrowser;

/**
 * Diese Action liest aus einem vorher definierten Text-Feld (org.eclipse.swt.widgets.Text)
 * Suchparameter aus und setzt sie zu einer gültigen URL, mit der sich 
 * bei Google suchen läßt, zusammen. Die URL wird vom angegebenen Browser
 * geladen.<br>
 * Die Zusammensetzung der Google-URL wird unter 
 * <a href="http://www.google.com/apis/reference.html" >GoogleReference</a>
 * beschrieben.
 *  
 * 
 * @author Tanja Buttler
 *
 */
public class GoogleAction extends Action {
	
	private Text input;
	private CreateBrowser browser;
	
	public GoogleAction() {
		super("Suchen");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		if(input == null) return;
		String raw = input.getText();
		try {
			raw = processInput(raw);
			browser.setURL(raw);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Hier wird die Benutzereingabe in eine gültige URL umgewandelt. Diese URL
	 * wird vom Browser geladen
	 * @param raw Benutzereingabe
	 * @return gültige url
	 * @throws UnsupportedEncodingException
	 */
	protected String processInput(String raw) throws UnsupportedEncodingException {
		if(raw == null || raw.equals("")) 
			return "http://www.google.de";
			String query = URLEncoder.encode(raw, "UTF-8");
			query = "http://www.google.de/search?q=" + query
					+"&ie=UTF-8&oe=UTF-8";
		return query;
	}
	
	/**
	 * Setzt das Textfeld, aus dem die Suchparameter gelesen werden sollen.
	 * @param inputLabel Textfeld für Benutzereingaben
	 */
	public void setInputField(Text inputLabel) {
		input = inputLabel;
	}
	
	/**
	 * Setzt den Browser, in dem die Suchergebnisse angezeigt werden sollen.
	 * @param browser Browser, in dem die Suchergebnisse angezeigt werden sollen
	 */
	public void setBrowser(CreateBrowser browser) {
		this.browser = browser;
	}
	
	

}
