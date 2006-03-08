package posemuckel.client.model.event;

import posemuckel.client.model.Folder;
import posemuckel.client.model.FolderTree;
import posemuckel.client.model.Webpage;

/**
 * Wird bei �nderungen im FolderTree ausgel�st. Es werden Referenzen auf den FolderTree
 * und die betroffenen Folder (sofern vorhanden) geliefert.
 * 
 * @see posemuckel.client.model.event.FolderTreeListener
 * @author Posemuckel Team
 *
 */
public class FolderEvent {
	
	//private String action;
	
	private FolderTree tree;
	
	private Folder parent;
	
	private Webpage page;
	
	private Folder newFolder;
	
	private Folder oldFolder;
	
	/**
	 * Erzeugt ein neues FolderEvent f�r das Verschieben einer Webpage in einen
	 * Folder
	 * @param tree FolderTree
	 * @param oldParent der vorherige Folder, <b>falls dieser vorhanden war</b>
	 * @param newParent der Folder, in dem die Webpage enthalten ist
	 * @param page Webpage, die in einen Folder verschoben wurde
	 */
	public FolderEvent(FolderTree tree, Folder oldParent, Folder newParent, Webpage page) {
		//page verschoben
		this.tree = tree;
		this.page = page;
		this.oldFolder = oldParent;
		this.parent = newParent;
	}
	
	/**
	 * Erzeugt ein neues FolderEvent f�r das Erstellen oder L�schen eines Folders.
	 * @param tree FolderTree
	 * @param folder Folder, der erzeugt oder gel�scht wurde
	 * @param parent der betroffene ParentFolder
	 */
	public FolderEvent(FolderTree tree, Folder folder, Folder parent) {
		//neuer Folder oder Folder gel�scht
		this.tree = tree;
		this.newFolder = folder;
		//f�r den neuen Folder
		this.parent = parent;
		//f�r den gel�schten Folder
		this.oldFolder = parent;
	}
	
	/**
	 * Erzeugt ein neues FolderEvent f�r das Verschieben eines Folders in einen
	 * anderen Folder
	 * @param tree FolderTree
	 * @param oldParent der vorherige ParentFolder, <b>falls dieser vorhanden war</b>
	 * @param newParent der Folder, in den der Folder verschoben wurde
	 * @param folder Folder, der verschoben wurde
	 */
	public FolderEvent(FolderTree tree, Folder oldParent, Folder newParent, Folder folder) {
		//Folder verschoben
		this(tree, folder, newParent);
		this.oldFolder = oldParent;
	}
	
	/**
	 * Erzeugt ein neues FolderEvent f�r das Laden des Foldersystems.
	 * @param tree FolderTree
	 */
	public FolderEvent(FolderTree tree) {
		//Daten geladen
		this.tree = tree;
	}
		
	/**
	 * Liefert die Webpage, die verschoben wurde, <b>falls eine Webpage von dem Event
	 * betroffen war</b>. Der R�ckgabewert kann <code>null</code> werden.
	 * @return Webpage, die verschoben wurde
	 */
	public Webpage getMovedPage() {
		return page;
	}
	
	/**
	 * Folder, der den Folder oder die Webpage vor dem Verschieben enthalten hatte.
	 * Der R�ckgabewert kann <code>null</code> werden.
	 * @return alter ParentFolder
	 */
	public Folder getOldParent() {
		return oldFolder;
	}
	
	/**
	 * Liefert den aktuellen ParentFolder des ver�nderten Folders oder der
	 * ver�nderten Webpage. Der R�ckgabewert kann <code>null</code> werden.
	 * @return der aktuelle ParentFolder
	 */
	public Folder getCurrentParent() {
		return parent;
	}
	
	/**
	 * Liefert den Folder, der in diesem Event ver�ndert wurde oder <code>null</code>.
	 * @return der Folder, der ver�ndert wurde
	 */
	public Folder getFolder() {
		return newFolder;
	}
	
	/**
	 * Liefert den betroffenen FolderTree. Der R�ckgabewert kann nicht <code>null</code>
	 * werden
	 * @return FolderTree
	 */
	public FolderTree getTree() {
		return tree;
	}
	
}
