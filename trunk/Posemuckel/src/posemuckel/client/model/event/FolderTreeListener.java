package posemuckel.client.model.event;

/**
 * Der FolderTreeListener informiert alle Beobachter �ber �nderungen am 
 * FolderTree. Details zu der jeweiligen �nderung sind im FolderEvent zu finden.
 * 
 * @author Posemuckel Team
 *
 */
public interface FolderTreeListener extends PosemuckelListener {
	
	/**
	 * Wird ausgel�st, wenn der FolderTree geladen wurde
	 * @param event Event mit einer Referenz auf den FolderTree
	 */
	public abstract void foldersLoaded(FolderEvent event);
	
	/**
	 * Wird ausgel�st, wenn die URLs f�r den FolderTree geladen wurden
	 * @param event Event mit einer Referenz auf den FolderTree
	 */
	public abstract void urlsLoaded(FolderEvent event);
	
	/**
	 * Wird ausgel�st, wenn eine URL den ParentFolder ge�ndert hat.
	 * @param event Event mit Referenzen auf die Webpage und die betroffenen Folder
	 */
	public abstract void urlChangedParent(FolderEvent event);
	
	/**
	 * Wird ausgel�st, wenn ein Folder seinen Parent ge�ndert hat.
	 * @param event Event mit Referenzen auf die betroffenen Folder
	 */
	public abstract void folderChangedParent(FolderEvent event);
	
	/**
	 * Wird ausgel�st, wenn ein neuer Folder erstellt wurde.
	 * @param event Event mit Referenzen auf die betroffenen Folder
	 */
	public abstract void newFolder(FolderEvent event);
	
	/**
	 * Wird ausgel�st, wenn ein Folder gel�scht wird
	 * @param event Event mit Referenzen auf die betroffenen Folder
	 */
	public abstract void deletedFolder(FolderEvent event);

}
