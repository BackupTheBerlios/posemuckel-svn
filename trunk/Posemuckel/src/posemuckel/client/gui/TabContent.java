package posemuckel.client.gui;

/**
 * Ein Interface für die Composites in einem TabFolder
 */
public interface TabContent {

	/**
	 * Schreibt einen Text in den Tab-Inhalt hinein
	 * @param userfeedback Der Text.
	 * @param type Ist ein Integer, der angibt, ob es sich um einen 
	 *        Fehler (0), eine Erfolgsmeldung (1), oder eine Info (2)
	 *        handelt. Standard ist Info.
	 */
	public abstract void setUserFeedback(String userfeedback, int type);

	/**
	 * Weist den Tab an, sich die Standard-EInstellungen aus der
	 * Konfiguration zu holen.
	 */
	public abstract void loadDefaults();

	/**
	 * Speichert die eingegebenen Werte in der Konfiguration.
	 */
	public abstract void save2Config();

	
	/**
	 * Führt eine Tab-spezifische Aktion aus
	 */
	public abstract void performAction();
	
	/**
	 * Liefert true, wenn die Eingaben des Benutzers in
	 * Ordnung sind.
	 * @return true, wenn die Eingabe OK ist.
	 */
	public abstract boolean validInput();
	
	/**
	 * Stösst die Bearbeitung eines Eingabefehlers an.
	 */
	public abstract void treatInputError();
}