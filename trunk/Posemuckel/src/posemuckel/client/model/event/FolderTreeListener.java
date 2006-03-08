package posemuckel.client.model.event;

/**
 * Der FolderTreeListener informiert alle Beobachter über Änderungen am 
 * FolderTree. Details zu der jeweiligen Änderung sind im FolderEvent zu finden.
 * 
 * @author Posemuckel Team
 *
 */
public interface FolderTreeListener extends PosemuckelListener {
	
	/**
	 * Wird ausgelöst, wenn der FolderTree geladen wurde
	 * @param event Event mit einer Referenz auf den FolderTree
	 */
	public abstract void foldersLoaded(FolderEvent event);
	
	/**
	 * Wird ausgelöst, wenn die URLs für den FolderTree geladen wurden
	 * @param event Event mit einer Referenz auf den FolderTree
	 */
	public abstract void urlsLoaded(FolderEvent event);
	
	/**
	 * Wird ausgelöst, wenn eine URL den ParentFolder geändert hat.
	 * @param event Event mit Referenzen auf die Webpage und die betroffenen Folder
	 */
	public abstract void urlChangedParent(FolderEvent event);
	
	/**
	 * Wird ausgelöst, wenn ein Folder seinen Parent geändert hat.
	 * @param event Event mit Referenzen auf die betroffenen Folder
	 */
	public abstract void folderChangedParent(FolderEvent event);
	
	/**
	 * Wird ausgelöst, wenn ein neuer Folder erstellt wurde.
	 * @param event Event mit Referenzen auf die betroffenen Folder
	 */
	public abstract void newFolder(FolderEvent event);
	
	/**
	 * Wird ausgelöst, wenn ein Folder gelöscht wird
	 * @param event Event mit Referenzen auf die betroffenen Folder
	 */
	public abstract void deletedFolder(FolderEvent event);

}
