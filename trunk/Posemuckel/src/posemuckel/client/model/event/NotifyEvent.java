package posemuckel.client.model.event;

import org.eclipse.swt.graphics.ImageData;


/**
 * Dieses Event dient der hochhalten-Funktion.
 * Hier werden die entsprechenden Daten drin geseichert.
 * 
 * @author Posemuckel Team
 *
 */
public class NotifyEvent {

	private String user;
	private String url;
	private String title;
	private String comment;
	private ImageData imagedata;
	
	public NotifyEvent(String user, String url, String title, String comment, ImageData imagedata) {
		this.user = user;
		this.url = url;
		this.title = title;
		this.comment = comment;
		this.imagedata = imagedata;
	}

	public String getUser() {
		return user;
	}
	
	public String getURL() {
		return url;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getComment() {
		return comment;
	}
	
	public ImageData getImageData() {
		return imagedata;
	}
}
