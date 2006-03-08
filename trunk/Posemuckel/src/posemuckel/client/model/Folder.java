package posemuckel.client.model;

import java.util.ArrayList;

/**
 * Ein Folder dient als Container für die URLs, die die Projektmitglieder 
 * gesammelt haben und in ihre finale Datenstruktur übernehmen wollen. Jeder
 * Folder kann einen ParentFolder und mehrere Unterordner haben. Des weiteren 
 * kann jeder Folder mehrere Webpages enthalten.</br>
 * 
 * Wenn für einen Folder kein ParentFolder definiert wird, wird standardmäßig
 * der virtuelle Folder mit der ID FolderTree.FOLDER_ROOT_ID verwendet.
 * Für den Folder mit der ID FolderTree.FOLDER_ROOT_ID ist kein ParentFolder
 * definiert, so dass von Folder#getParentFolder() <code>null</code>
 * ausgegeben wird.
 * 
 * @see posemuckel.client.model.FolderTree
 * 
 * @author Posemuckel Team
 *
 */
public class Folder extends Root {

	
	/**
	 * Enthält die IDs der Unterordner. Über FolderTree#getFolderForID(String ID)
	 * kann auf den Folder zugegriffen werden.
	 */
	private ArrayList<String> subfolderIDs;
	//der Trace kann die IDs den Foldern zuordnen
	//er ist über getTrace() erhältlich (siehe Root)
	
	/**
	 * Die Id des ParentFolders oder 0, falls kein ParentFolder definiert wurde.
	 * Über FolderTree#getFolderForID(String ID)
	 * kann auf den Folder zugegriffen werden.
	 */
	private String parentFolderID;
	private String myID;
	
	/**
	 * Erzeugt einen neuen Folder mit der angegebenen ID.
	 * @param id id des Folders
	 * @param title Titel des Folders
	 * @param parentID ID des ParentFolders
	 * @param trace , der einen Zugriff auf Webpages und den FolderTree bietet
	 */
	public Folder(String id, String title, String parentID, Webtrace trace) {
		super(title, Root.CATEGORY_TYPE, trace);
		parentFolderID = parentID;
		myID = id;
		subfolderIDs = new ArrayList<String>();
	}
	
	/**
	 * Findet den ParentFolder von this
	 * 
	 * @param folderList Die Folder-Liste, in der der ParentFolder gefunden werden muß
	 * @return den ParentFolder von this
	 */
	@Deprecated()
	public Folder getParentFolder(Folder[] folderList)
	{
		for (int i = 0; i < folderList.length; i++) {
			if(folderList[i].getMyID() == this.getMyID())
				return folderList[i];
		}
		// Error handling ?
		return null;
	}
	
	/**
	 * Gibt die ID dieses Folders als int aus.
	 * @return ID dieser Folder-Instanz
	 */
	public int getMyID() {
		return Integer.parseInt(myID);
	}
	
	/**
	 * Gibt die ID dieses Folders als String aus.
	 * @return ID dieser Folder-Instanz
	 */
	public String getID() {
		return myID;
	}
	
	/**
	 * Gibt die ID des ParentFolders als String aus. Wenn kein ParentFolder 
	 * definiert ist, wird <code>null</code> ausgegeben.
	 * @return ID des ParentFolders
	 */
	public String getParentFolderID() {
		return parentFolderID;
	}
	
	/**
	 * Setzt die ID des ParentFolders. 
	 * @param pfid ID des ParentFolders
	 */
	protected void setParentFolderID(String pfid) {
		parentFolderID = pfid;
	}
	
	/**
	 * Gibt den ParentFolder aus.
	 * @return ParentFolder 
	 */
	public Folder getParentFolder() {
		return getTrace().getFolderTree().getFolderForID(parentFolderID);
	}
	
	/**
	 * Gibt eine Liste mit den Unterordnern dieser Instanz aus. Die Liste ist leer 
	 * (aber definiert), wenn keine Unterordner enthalten sind.
	 * @return Liste mit den Unterordnern dieses Folders. <code>null</code> ist kein
	 * gültiger Rückgabewert
	 */
	public ArrayList getSubfolders() {
		ArrayList<Folder> list = new ArrayList<Folder>();
		for (String id : subfolderIDs) {
			list.add(getTrace().getFolderTree().getFolderForID(id));
		}
		return list;
	}
	
	
	/**
	 * Ein Folder hat Kinder, wenn Unterordner oder Webpages enthalten sind.
	 */
	public boolean hasChildren() {
		return (!subfolderIDs.isEmpty()) || (super.hasChildren());
	}
	
	/**
	 * Ist der Folder mit der Id 'id' ein Unterordner dieser Instanz von 
	 * Folder.
	 * @param id des möglichen Unterordners 
	 * @return true, falls der Folder mit der ID ein Unterordner dieses Folders ist
	 */
	public boolean hasSubfolder(String id) {
		if(id == null) return false;
		for (String folder : subfolderIDs) {
			if(id.equals(folder)) return true;
		}
		return false;
	}
	
	/**
	 * Gibt den Unterordner mit der angegebenen ID aus. Wenn kein Unterordner
	 * mit der ID vorhanden ist, wird <code>null</code> zurückgegeben.
	 * @param id des Unterordners
	 * @return der Unterordner oder <code>null</code>
	 */
	public Folder getSubfolder(String id) {
		if(id == null) return null;
		for (String folder : subfolderIDs) {
			if(id.equals(folder)) return getTrace().getFolderTree().getFolderForID(id);
		}
		return null;
	}
	
	/**
	 * Fügt einen neuen Unterordner zu diesem Folder hinzu.
	 * @param id des Unterordners
	 */
	protected void addSubfolder(String id) {
		if(id != null) {
			subfolderIDs.add(id);
		}
	}
	
	/**
	 * Ändert den ParentFolder dieses Folders. Der ParentFolder dieses Folders
	 * wird ebenfalls aktualisiert.
	 * @param parentFolderID
	 */
	protected void changeParentFolder(String parentFolderID) {
		//gibt NullPointer, falls kein ParentFolder definiert ist
		getParentFolder().removeSubfolder(myID);
		this.parentFolderID = parentFolderID;
		getParentFolder().addSubfolder(myID);		
	}
	
	/**
	 * Entfernt einen Unterordner aus diesem Folder.
	 * @param id des Unterordners
	 */
	protected void removeSubfolder(String id) {
		subfolderIDs.remove(id);
	}

}
