package posemuckel.common;

/**
 * Diese Exception wird geworfen, wenn ein unbekanntes
 * oder ungültiges Paket empfangen wird.
 * @author Posemuckel Team
 *
 */
public class InvalidClientHashException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidClientHashException() {
		super();
	}

	public InvalidClientHashException(String message) {
		super(message);
	}

	public InvalidClientHashException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidClientHashException(Throwable cause) {
		super(cause);
	}

}
