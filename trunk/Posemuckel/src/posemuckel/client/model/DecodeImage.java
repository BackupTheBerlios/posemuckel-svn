package posemuckel.client.model;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

import posemuckel.client.model.event.NotifyEvent;
import posemuckel.client.model.event.NotifyListener;
	
	/**
	 * Diese Klasse dient einzig und allein der Decodierung
	 * des Base64-Konformen Strings in
	 * ein ImageData Object. Dies geschieht als Thread, 
	 * damit die GUI nicht so leidet. Wenn die Decodierung fertig ist,
	 * wird ein notify-Event abgefeuert.
	 * @author Posemuckel Team
	 */
	public class DecodeImage implements Runnable {
		private String pic;
		private String user;
		private String url;
		private String title;
		private String comment;
		
		/**
		 * Konstruktor, der erstmal die globalen Werte übernimmt.
		 * @param user Benutzername des sendenden Benutzers
		 * @param url Die hochgehaltene URL.
		 * @param title Der Titel der hochgehaltenen Seite.
		 * @param comment Der gesendete Kommentar.
		 * @param pic_data_length Die Länge der codierten Bilddaten (ungenutzt).
		 * @param pic Die codierten Bilddaten.
		 */
		public DecodeImage(String user, String url, String title, String comment, String pic_data_length, String pic) {
			this.user = user;
			this.url = url;
			this.title = title;
			this.comment = comment;
			this.pic = pic;
		}

		public void run() {
			try {
				// Hier wird das Bild nun von Base64 wieder
				// nach Jpeg gewandelt.
				Base64 codec = new Base64();
				byte[] encoded = pic.getBytes("UTF-8");
				byte[] decoded = codec.decode(encoded);
				ByteArrayInputStream in = new ByteArrayInputStream(decoded);
				ImageLoader imageLoader = new ImageLoader();
				imageLoader.load(in);
				ImageData[] imgdata = imageLoader.data;
				NotifyEvent ne = new NotifyEvent(user, url, title, comment, imgdata[0]);
				Project proj = Model.getModel().getOpenProject();
				ArrayList<NotifyListener> listener = proj.getListener();
				for (NotifyListener notifyListener : listener) {
					notifyListener.notify(ne);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

