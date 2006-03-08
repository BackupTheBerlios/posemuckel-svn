package posemuckel.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

/**
 * Dient zum Erstellen des Client-Hash.
 * @author Posemuckel Team
 *
 */
public class ClientHash {
	
	/**
	 * Verschlüsselt den übergebenen String nach MD5 und gibt diesen
	 * zurück.
	 * @param toEncode String, der verschlüsselt werden soll
	 * @return der verschlüsselte String
	 */
	public static String getClientHash(String toEncode) {
		StringBuffer st = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(toEncode.getBytes());
			for (byte d : digest)
			    st.append(Integer.toHexString(d & 0xFF));
		} catch (NoSuchAlgorithmException e) {
			Logger.getLogger(ClientHash.class).error(e.getMessage(), e);
		}
		return new String(st);
	}
}
