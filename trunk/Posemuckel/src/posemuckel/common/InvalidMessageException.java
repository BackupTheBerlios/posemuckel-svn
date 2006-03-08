package posemuckel.common;

/**
 * Diese Exception wird geworfen, wenn ein unbekanntes
 * oder ungültiges Paket empfangen wird.
 * @author Posemuckel Team
 *
 */
public class InvalidMessageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidMessageException() {
		super();
	}

	public InvalidMessageException(String message) {
		super(message);
	}

	public InvalidMessageException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidMessageException(Throwable cause) {
		super(cause);
	}

}
