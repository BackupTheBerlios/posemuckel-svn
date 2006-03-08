package posemuckel.common;

public class Message {
	
	private static int lineEndCounter;

	/** Führt die Formatierung einer Nachricht durch, gemäß
	 *  der Spezifikation des posemuckel message protokolls (pmp) :)
	 * 
	 * @param hash ist der eindeutige Identifikator für den sendenden Partner.
	 * 				Für den Server bleibt dies leer.
	 * @param id Message-ID des Clients
	 * @param count Anzahl der Nachrichtenparameter
	 * @param cmd das Kommando, das ausgeführt werden soll
	 * @param messageData die Parameter, die auf das Kommando folgen. Diese müssen in
	 * der Reihenfolge in dem Array stehen, in der auch das pmp diese erwartet
	 * @return die fertig formatierte Nachricht
	 */
	  public static String format(String hash, String id, String count,
			  String cmd, String[] messageData) {
		Integer counter = new Integer(count);
		int value = counter.intValue();
		String message = new String();
		if(messageData != null) {
			for(int i=0;i<messageData.length;i++){
				message += handleLineEnds(messageData[i])+"\r\n";
			}
		}
		message = hash+"\r\n"+id+"\r\n"+(value+lineEndCounter)+"\r\n"+
		    cmd+"\r\n"+message;
		return message;
	  }
	  
		/** Führt die Formatierung einer Nachricht durch, gemäß
		 *  der Spezifikation des posemuckel message protokolls (pmp) :)
		 *  
		 *  die Anzahl der Nachrichtenparameter entspricht hier der Länge des Arrays messageData
		 * 
		 * @param hash ist der eindeutige Identifikator für den sendenden Partner.
		 * 				Für den Server bleibt dies leer.
		 * @param id Message-ID des Clients
		 * @param cmd das Kommando, das ausgeführt werden soll
		 * @param messageData die Parameter, die auf das Kommando folgen. Diese müssen in
		 * der Reihenfolge in dem Array stehen, in der auch das pmp diese erwartet
		 * @return die fertig formatierte Nachricht
		 */  
	public static String format(String hash, String id, String cmd, String[] messageData) {
		String count = (messageData == null) ? "0" : String.valueOf(messageData.length);
		return format(hash, id, count, cmd, messageData);
	}
	
	private static String handleLineEnds(String message) {
		lineEndCounter = 0;
		boolean skip = false;
		char c;
		StringBuffer copy = new StringBuffer(message);
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<copy.length(); i++) {
			c = copy.charAt(i);
			if (!skip) {
				if ((c == '\n')||(c == '\r')) {
					buf.append("\r\n");
					lineEndCounter++;
				} else {
					buf.append(c);
				}
				if ((i != (copy.length()-1))&&(copy.charAt(i+1) == '\n')
						&&(c == '\r')) {
					skip = true;
				}
			} else {
				skip = false;
			}			
		}
		return new String(buf);
	}
}