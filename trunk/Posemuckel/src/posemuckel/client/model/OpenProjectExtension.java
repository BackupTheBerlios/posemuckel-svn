/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;

import posemuckel.client.model.event.ListenerManagment;
import posemuckel.client.model.event.NotifyListener;

/**
 * Die OpenProjectExtension enthält die Attribute und die Funktionalität,
 * die für ein geöffnetes Projekt benötigt werden. Da das Projekt zwar beim
 * Öffnen seinen Zustand ändert, aber ansonsten im Wesentlichen das gleiche Objekt
 * bleibt, wird die OpenProjectExtension als komplexes Attribut eines Project
 * verwendet.
 * 
 * @author Posemuckel Team
 *
 */
public class OpenProjectExtension {
	
	private Webtrace webtrace;
	private String currentURL = "";
	private String previousURL = "";
	private String urlTitle = "";
	private boolean useViewing;
	private String[] notifydata;
	//TODO ListenerManagment übernehmen
	private ListenerManagment<NotifyListener> listenerManagment;
	private FollowMeManager followMe;
	
	private Project project;
	
	public OpenProjectExtension(Project basis) {
		project = basis;
	}
	/**
	 * Gibt den Webtrace des Projektes aus. Idealerweise sollte nur f&uuml;r 
	 * das geöffnete Projekt ein Webtrace vorhanden sein.
	 * 
	 * @return der Webtrace des Projektes
	 */
	public Webtrace getWebtrace() {
		if(webtrace == null) {
			//TODO Webtrace mit der Extension arbeiten lassen?
			webtrace = new Webtrace(project);
			String[] names = project.getMembers();
			for (String name : names) {
				webtrace.addUser(name);
			}
		}
		return webtrace;
	}
	
	/**
	 * Setzt die URL, die der Anwender gerade besucht.
	 * @param url , die der Anwender gerade besucht
	 */
	public void setCurrentURL(String url){
		currentURL = url;
	}
	
	/**
	 * Gibt die zuletzt besuchte URL aus.
	 * @return zuletzt besuchte URL
	 */
	public String getCurrentURL(){
		return currentURL;
	}

	/**
	 * Setzt die URL, die der Anwender vor der aktuellen URL besucht hat.
	 * @param url 
	 */
	public void setPreviousURL(String url){
		previousURL = url;
	}

	/**
	 * Gibt die vorletzte besuchte URL von diesem Projekt aus.
	 * @return vorletzte besuchte URL
	 */
	public String getPreviousURL(){
		return previousURL;
	}
	
	/**
	 * Setzt den Titel der zuletzt besuchten Webseite.
	 * @param title der zuletzt besuchten Webseite
	 */
	public void setUrlTitle(String title){
		urlTitle =title;
	}
	
	/**
	 * Gibt den Titel der zuletzt besuchten Webseite aus.
	 * @return Titel der zuletzt besuchten Webseite
	 */
	public String getUrlTitle(){
		return urlTitle;
	}
	
	/**
	 * Teilt dem Projekt mit, dass der Browser vom Anwender gezwungen wird, zu einer 
	 * URL zu springen. Das kann durch Setzen einer URL in der Adresszeile, über den
	 * Webtrace oder durch andere Buttons geschehen.
	 * @param url , zu der gesprungen wird
	 */
	public void jumpToURL(String url) {
		if(getWebtrace().getPageForUrl(url, getModel().getUser().getNickname()) == null) {
			//der Anwender hat die Seite noch nicht besucht, also eine neue Wurzel
			setCurrentURL("");
		} else {
			//der Anwender kennt die Seite schon
			useViewing = true;
			setCurrentURL("");
		}
	}
	
	/**
	 * Teilt dem Projekt mit, dass nach Möglichkeit eine Viewing-Nachricht anstelle
	 * einer Visiting-Nachricht verwendet werden soll, wenn die nächste URL an
	 * die Database versendet werden soll. Es ist nicht möglich, eine
	 * Viewing-Nachricht zu verwenden, wenn der Anwender die URL noch nicht
	 * besucht hat.
	 *
	 */
	public void useViewing() {
		setCurrentURL("");
		useViewing = true;
	}
	
	/**
	 * Gibt den FollowMeManager des Projektes aus. Der FollowMeManager sollte nur 
	 * für geöffnete Projekte verwendet werden.
	 * @return FollowMeManager
	 */
	public FollowMeManager getFollowMeManager()  {
		if(followMe == null) {
			followMe = new FollowMeManager(getModel().getLogchat(),
					getModel().getUser().getNickname());
		}
		return followMe;
	}
	
	/**
	 * Sendet eine Visiting- oder eine Viewing-Nachricht an die Database.
	 *
	 */
	public void visiting() {
		//wenn die URLs gleich sind, wird visiting nicht ausgeführt
		boolean doit = !getCurrentURL().equals(getPreviousURL());
		if(doit) {
			//wenn der Webtrace beide URLs für den Anwender 
			//schon gespeichert hat, wird visiting nicht ausgeführt 
			//das hat vor allem Einfluss auf den Back-und Forward-Button
			doit = !webtrace.hasURL(getModel().getUser().getNickname(), getCurrentURL(), getPreviousURL());
		}
		//darf nur verwendet werden, wenn die URL auch tatsächlich benutzt wurde
		if(useViewing) {
			useViewing = (null != webtrace.getPageForUrl(
					getCurrentURL(),getModel().getUser().getNickname()));
		}
		if(doit && !useViewing) {
			new ProjectTask(getModel().getUser().getProjects(), project).execute(ProjectTask.VISITING);		
		} else if(getModel().getUser().getURL() != getCurrentURL()){
			//die Anzeige im Browser wurde geändert
			useViewing = false;
			new WebTraceTask(getWebtrace()).execute(WebTraceTask.VIEWING);
		}
	}
	
	private Model getModel() {
		return project.getModel();
	}
	/**
	 * Holt die Daten für die Natifikation.
	 * 
	 * @return Die Notifikationsdaten als String-Array.
	 */
	public String[] getNotify() {
		return notifydata;
	}
	public void notify(String url, String title, String comment, String datalen, String imagedata, String[] user) {
		notifydata = new String[user.length+5];
		notifydata[0] = url;
		notifydata[1] = title;
		notifydata[2] = comment;
		notifydata[3] = datalen;
		notifydata[4] = imagedata;
		for (int i=5; i < notifydata.length; i++)
			notifydata[i] = user[i-5];
	
		new ProjectTask(null, project).execute(ProjectTask.NOTIFY);
	}
	/**
	 * Informiert die Listener, dass eine Notify-Nachricht von der Database an die
	 * Empfänger weitergeleitet wurde.
	 * @param project TODO
	 *
	 */
	protected void fireNotifyConfirmation() {
		ArrayList<NotifyListener> listener = project.getListener();
		for (NotifyListener notifyListener : listener) {
			notifyListener.ack();
		}
	}
	/**
	 * Informiert die Listener, dass der Anwender eine neue URL angeklickt hat.
	 * @param url die neue URL
	 */
	public void fireNewURL(String url) {
		//TODO warum sollte das über den NotifyListener laufen?
		ArrayList<NotifyListener> listener = project.getListener();		
		for (NotifyListener notifyListener : listener) {
			notifyListener.newurl(url);
		}
	}

}
