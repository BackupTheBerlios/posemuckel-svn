/**
 * 
 */
package posemuckel.client.model;

import java.util.ArrayList;


/**
 * Regelt die Zugriffe auf die Database, die einen Bezug zum FolderTree haben.
 * Konkret werden die folgenden Nachrichten (nach RFC0815) bearbeitet:
 * 
 * <ul>
 * <li>NEW_FOLDER</li>
 * <li>DELETE_FOLDERE</li>
 * <li>MOVE_FOLDER</li>
 * <li>PARENTFOLDER_CHANGED</li>
 * <li>GET_FOLDERSYSTEM</li>
 * </ul>
 * 
 * Mit Ausnahme der letzten Aufgabe werden von der Database keine Antworten an
 * die Task weitergeleitet.
 *
 * @author Posemuckel Team
 *
 */
class FolderTask extends TaskAdapter {
	
	static final int NEW_FOLDER = 100;
	static final int CHANGE_PARENT = 200;
	static final int DELETE_FOLDER = 300;
	static final int CHANGE_PARENT_FOR_URL = 400;
	static final int LOAD = 500;
	
	private String parentID;
	private String objectID;
	private String title;
	
	private FolderTree tree;
	private int answers;
	
	/**
	 * Diese Instanz von FolderTask kann alle Aufgaben bis auf das Laden des 
	 * Foldersystems erledigen.
	 * @param parent ID des betroffenen ParentFolders oder <code>null</code>, wenn 
	 * ein solcher nicht existiert
	 * @param objectID die URL oder die ID des Folders, die von der Aufgabe betroffen sind
	 * @param title Titel des Folders - wird nur zur Erzeugung eines neuen Folders benötigt
	 */
	FolderTask(String parent, String objectID, String title) {
		this.parentID = parent;
		this.objectID = objectID;
		this.title = title;
	}
	
	/**
	 * Diese Instanz von FolderTask kann das Laden des Foldersystems verarbeiten.
	 * 
	 * @param tree der von der Database geladen werden soll.
	 */
	FolderTask(FolderTree tree) {
		this.tree = tree;
		answers = 0;
	}
	
	/* (non-Javadoc)
	 * @see posemuckel.client.model.TaskAdapter#work(int)
	 */
	@Override
	protected void work(int task) {
		switch (task) {
		case NEW_FOLDER:
			if(parentID == null) {
				parentID = "0";
			}
			DatabaseFactory.getRegistry().addFolder(title, parentID);
			break;
		case CHANGE_PARENT:
			if(parentID == null) {
				parentID = "0";
			}
			DatabaseFactory.getRegistry().changeParentFolder(objectID, parentID);
			break;
		case DELETE_FOLDER:
			DatabaseFactory.getRegistry().deleteFolder(objectID);
			break;
		case CHANGE_PARENT_FOR_URL:
			if(parentID == null) {
				parentID = "";
			}
			DatabaseFactory.getRegistry().changeParentFolderForURL(objectID, parentID);
			break;
		case LOAD:
			DatabaseFactory.getRegistry().loadFolderStructure(this);
			break;
		default:
			break;
		}		
	}

	/* (non-Javadoc)
	 * @see posemuckel.client.model.TaskAdapter#update(java.util.ArrayList)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void update(ArrayList list) {
		//die Liste aus FOLDERSYSTEM wird bereits beim Parsen in zwei Teile zerlegt
		if(answers == 0) {
			tree.folderStructureLoaded(list);
			answers++;
		} else if(answers == 1) {
			tree.urlStructureLoaded(list);
		}
	}	

}
