/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;
import java.util.HashMap;

import posemuckel.client.model.event.FolderEvent;
import posemuckel.client.model.event.FolderTreeListener;
import posemuckel.client.model.event.ListenerManagment;

/**
 * Ein FolderTree enth�lt s�mtliche Folder und hat die M�glichkeit, �ber IDs
 * auf diese Folder zuzugreifen. Jede FolderID kann in einem FolderTree nur
 * einmal vorkommen.</br>
 * 
 * Wenn Operationen auf dem FolderTree ausgef�hrt werden, muss zur Konsistenzsicherung
 * das L�schen von Ordnern aus dem Tree jederzeit r�ckg�ngig gemacht werden k�nnen.
 * Der FolderTree ist der 'Caretaker', der die Verwaltung der Undo-Operationen
 * �bernimmt. Alle Manipulationen der Folderstruktur m�ssen daher �ber den 
 * FolderTree erfolgen.
 * 
 * @author Posemuckel Team
 *
 */
public class FolderTree {

	
	static final String FOLDER_ROOT_ID = "0";
	private HashMap<String, Folder> folders;
	private ListenerManagment<FolderTreeListener> listenerManagment;
	private Webtrace trace;
	private boolean updateListener = true;
	private boolean loaded = false;
	
	/**
	 * Erstellt einen neuen FolderTree. Der Webtrace enth�lt die Webpages zu den
	 * URLs.
	 * @param trace mit den Webpages
	 */
	public FolderTree(Webtrace trace) {
		this.trace = trace;
		folders = new HashMap<String, Folder>();
		listenerManagment = new ListenerManagment<FolderTreeListener>();
		folders.put(FOLDER_ROOT_ID, new Folder(FOLDER_ROOT_ID,"project folder", null, trace));
	}
	
	/**
	 * Gibt die Wurzel f�r die Ordnerstruktur aus.
	 * @return Wurzel der Folderstruktur
	 */
	public Folder getFolderRoot() {
		return folders.get(FOLDER_ROOT_ID);		
	}
	
	/**
	 * F�gt einen neuen Folder in den Baum ein. 
	 * @param title des Folders
	 * @param id des Folders
	 * @param parentID des Folders oder der leere String
	 */
	protected void addFolder(String title, String id, String parentID) {
		if(parentID == null || parentID.equals("")) {
			parentID = FOLDER_ROOT_ID;
		}
		if(!folders.containsKey(parentID)) {
			folders.put(parentID, new Folder(parentID, "", FOLDER_ROOT_ID, trace));
		}
		if(!folders.containsKey(id)) {
			folders.put(id, new Folder(id, title, parentID, trace));
			folders.get(parentID).addSubfolder(id);
		} else {
			//wenn der parent nach dem Child gesendet wird
			folders.get(id).setName(title);
			folders.get(id).setParentFolderID(parentID);
			folders.get(parentID).addSubfolder(id);
		}
		if(updateListener)fireNewFolder(id, parentID);
	}
	
	/**
	 * F�gt eine Webpage in einen Folder ein. Wenn die Webpage bereits in einem
	 * Folder enthalten ist, wird sie wieder daraus entfernt.
	 * Wenn der leere String f�r die FolderID
	 * �bergeben wird, wird die Webpage lediglich aus ihrem alten Folder entfernt.
	 * @param url der Webpage
	 * @param parentFolder id des neuen Folders oder der leere String
	 */
	protected void addChildURLToFolder(String url, String parentFolder) {
		Folder parent = folders.get(parentFolder);
		if(parent != null) {
			parent.addChild(url);
		}
		Webpage page = trace.getPageForUrl(url);
		if(page == null) throw new IllegalArgumentException("unknown url " + url);
		Folder oldParent = page.getParentFolder();
		if(oldParent != null) {
			oldParent.removeChild(page.getURL());
		}
		page.setParentFolder(parent);	
		fireURLChangedParent(page, oldParent, parent);
	}
	
	/**
	 * L�d das gesamte Foldersystem von der Database.
	 *
	 */
	public void loadFolderStructure() {
		new FolderTask(this).execute(FolderTask.LOAD);
	}
	
	/**
	 * Wurde das Foldersystem bereits von der Database geladen?
	 * @return true, falls das Foldersystem bereits geladen wurde
	 */
	public boolean isLoaded() {
		return loaded;
	}
	
	/**
	 * Die Daten in der Liste enthalten alle Informationen, um eine Folderstruktur
	 * ohne die URLs aufzubauen. In der Liste geh�ren je drei aufeinanderfolgende
	 * Elemente zusammen: folderID, name, parentID. Diese Methode soll die
	 * Daten vom Netzwerk verarbeiten.
	 * 
	 * @param data Liste mit den Daten
	 */
	public void folderStructureLoaded(ArrayList<String> data) {
		updateListener = false;
		for (int i = 0; i < data.size(); i+=3) {
			addFolder(data.get(i +1), data.get(i), data.get(i+2));	
		}
		fireFoldersLoaded();
		updateListener = true;
	}
	
	/**
	 * Die Daten in der Liste enthalten alle Informationen, um die URLs in ihre
	 * Folder einzubinden. In der Liste geh�ren je zwei aufeinanderfolgende 
	 * Elemente zusammen: url und folderID. Diese Methode sollte die Daten vom 
	 * Netzwerk verarbeiten.
	 * 
	 * @param urls Liste mit den URLs und den ParentFolderIDs
	 */
	public void urlStructureLoaded(ArrayList<String> urls) {
		updateListener = false;
		//System.err.println(urls.size());
		for (int i = 0; i < urls.size(); i+=2) {
			addChildURLToFolder(urls.get(i+1), urls.get(i));
		}	
		fireURLStructureLoaded();
		updateListener = true;
		loaded = true;
	}
	
	/**
	 * �ndert den ParentFolder eines Folders. Der Folder wird aus seinem alten
	 * Parent ausgetragen.
	 * @param id des Folders
	 * @param parentFolderID ID des neuen ParentFolders
	 */
	protected void changeParentFolder(String id, String parentFolderID) {
		Folder folder = folders.get(id);
		String oldParent = folder.getParentFolderID();
		if(folders.get(parentFolderID) == null) {
			folders.put(parentFolderID, new Folder(parentFolderID, "", FOLDER_ROOT_ID, trace));
			getFolderRoot().addSubfolder(id);
		}
		if(folder != null) {
			folder.changeParentFolder(parentFolderID);
			if(updateListener)fireChangeParent(id, parentFolderID, oldParent);
		} 
	}
	
	/**
	 * L�scht den Folder aus dem Baum. Es erfolgt keine �berpr�fung mehr, ob
	 * der Folder noch Kindelemente enth�lt.
	 * 
	 * @param folderID des zu l�schenden Folders
	 */
	protected void deleteFolder(String folderID) {
		if(folders.containsKey(folderID)) {
			Folder folder = folders.get(folderID);
			String oldParent = folder.getParentFolderID();
			folder.getParentFolder().removeSubfolder(folderID);
			folders.remove(folderID);
			fireFolderDeleted(folderID, oldParent);
		} 
	}
	
	/**
	 * Gibt zu der ID den zugeh�rigen Folder aus.
	 * @param id des gesuchten Folders
	 * @return Folder
	 */
	public Folder getFolderForID(String id) {
		return folders.get(id);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString();
	}
	
	/**
	 * Fordert die Database auf, einen neuen Folder zu erstellen. Es gibt keine
	 * Garantie, dass der Folder auch erstellt werden kann.
	 * @param title des neuen Folders
	 * @param parentFolderId der Parentfolder
	 */
	public void requestNewFolder(String title, String parentFolderId) {
		new FolderTask(parentFolderId, "", title).execute(FolderTask.NEW_FOLDER);
	}
	
	/**
	 * Fordert die Database auf, den Parentfolder des Folders zu �ndern. Es gibt
	 * keine Garantie, dass die Operation auch durchf�hrbar ist.
	 * @param id des Folders
	 * @param parentFolderID der neue Parentfolder
	 */
	public void requestChangeParent(String id, String parentFolderID) {
		if(parentFolderID == null || parentFolderID.equals("")) {
			parentFolderID = FOLDER_ROOT_ID;
		}
		new FolderTask(parentFolderID, id, null).execute(FolderTask.CHANGE_PARENT);
	}
	
	/**
	 * Fordert die Database auf, den Parentfolder f�r die URL zu �ndern. Wenn
	 * als Parenfolder der leere String angegeben wird, wird die URL lediglich 
	 * aus ihrem alten Parentfolder entfernt. Es gibt keine Garantie, dass
	 * die Webpage auch verschoben werden kann.
	 * @param newParent der neue Parentfolder oder der leere String
	 * @param url der Webpage, die in den Folder verschoben werden soll
	 */
	public void requestChangeParentForURL(String newParent, String url) {
		String oldParent = trace.getPageForUrl(url).getParentFolderID() + "";
		if(newParent == null) {
			new FolderTask("", url, null).execute(FolderTask.CHANGE_PARENT_FOR_URL);
		} else if(!newParent.equals(oldParent)) {
			new FolderTask(newParent, url, null).execute(FolderTask.CHANGE_PARENT_FOR_URL);
		}
	}
	
	/**
	 * Fordert die Database auf, den Folder zu l�schen. 
	 * @param id
	 */
	public void requestDeleteFolder(String id) {
		new FolderTask("", id, null).execute(FolderTask.DELETE_FOLDER);
	}

	/**
	 * Teilt allen Listenern mit, dass ein neuer Folder in den Baum eingef�gt wurde.
	 * @param id des Folders
	 * @param parentID ID des Parentfolders
	 */
	private void fireNewFolder(String id, String parentID) {
		ArrayList<FolderTreeListener> listener = getListener();
		FolderEvent event = new FolderEvent(this, getFolderForID(id), getFolderForID(parentID));
		for (FolderTreeListener folderListener : listener) {
			folderListener.newFolder(event);
		}
	}
	
	/**
	 * Teilt allen Listenern mit, dass ein Folder gel�scht wurde.
	 * 
	 * @param id des Folders
	 * @param oldParent ID des ehemaligen Parentfolders
	 */
	private void fireFolderDeleted(String id, String oldParent) {
		ArrayList<FolderTreeListener> listener = getListener();
		FolderEvent event = new FolderEvent(this, getFolderForID(id), getFolderForID(oldParent));
		for (FolderTreeListener folderListener : listener) {
			folderListener.deletedFolder(event);
		}
	}
	
	/**
	 * Teilt allen Listenern mit, dass sich der Parentfolder eines Folders ge�ndert
	 * hat.
	 * @param id des Folders
	 * @param parentID ID des neuen Parentfolders
	 * @param oldParent ID des alten Parentfolders
	 */
	private void fireChangeParent(String id, String parentID, String oldParent) {
		ArrayList<FolderTreeListener> listener = getListener();
		FolderEvent event = new FolderEvent(this, 
				getFolderForID(oldParent), getFolderForID(parentID), getFolderForID(id));
		for (FolderTreeListener folderListener : listener) {
			folderListener.folderChangedParent(event);
		}
	}
	
	/**
	 * Teilt allen Listenern mit, dass die Folderstruktur geladen wurde (ohne die
	 * URLs!).
	 *
	 */
	private void fireFoldersLoaded() {
		ArrayList<FolderTreeListener> listener = getListener();
		FolderEvent event = new FolderEvent(this);
		for (FolderTreeListener folderListener : listener) {
			folderListener.foldersLoaded(event);
		}
	}	
	
	/**
	 * Teilt allen Listenern mit, dass die Einordnung der URLs in
	 * die Folder von der Database geladen wurde. 
	 *
	 */
	private void fireURLStructureLoaded() {
		ArrayList<FolderTreeListener> listener = getListener();
		FolderEvent event = new FolderEvent(this);
		for (FolderTreeListener folderListener : listener) {
			folderListener.urlsLoaded(event);
		}
	}
	
	/**
	 * Teilt allen Listenern mit, dass eine Webpage den Parentfolder ge�ndert hat.
	 * @param page betroffene Webpage
	 * @param oldParent alter Parentfolder
	 * @param parent neuer Parentfolder, kann auch der leere String sein
	 */
	private void fireURLChangedParent(Webpage page, Folder oldParent, Folder parent) {
		ArrayList<FolderTreeListener> listener = getListener();
		FolderEvent event = new FolderEvent(this, oldParent, parent, page);
		for (FolderTreeListener folderListener : listener) {
			folderListener.folderChangedParent(event);
		}
		trace.fireFolderChanged(page);
	}

	/**
	 * Registriert einen FolderTreeListener, der �ber Ereignisse, die diese Instanz
	 * betreffen, informiert werden m�chte. 
	 * @param listener der zu registrierende FolderTreeListener
	 */
	public void addListener(FolderTreeListener listener) {
		listenerManagment.addListener(listener);
	}

	/**
	 * Gibt eine Kopie der Liste mit allen FolderTreeListenern aus.
	 * @return Kopie der Liste mit allen FolderTreeListenern
	 */
	public ArrayList<FolderTreeListener> getListener() {
		return listenerManagment.getListener();
	}

	/**
	 * Entfernt den FolderTreeListener aus der Liste der FolderTreeListener.
	 * @param listener der zu entfernende FolderTreeListener
	 */
	public void removeListener(FolderTreeListener listener) {
		listenerManagment.removeListener(listener);
	}

}
