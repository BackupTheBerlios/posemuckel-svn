/**
 * 
 */
package posemuckel.client.gui.actions;

import org.eclipse.jface.action.Action;

import posemuckel.client.gui.MyInputDialog;
import posemuckel.client.model.Folder;
import posemuckel.client.model.FolderTree;
import posemuckel.client.model.Model;


/**
 * Erstellt einen neuen Ordner und fügt ihn in den FolderTree ein.
 * @author Posemuckel Team
 *
 */
public class NewFolderAction extends Action {
	
	private FolderTree folderTree;
	private Folder selected;
	
	/**
	 * Mit dieser Action wird ein neuer Ordner erstellt und als Wurzel in den 
	 * FolderTree eingefügt.
	 * 
	 * @param text Text der Action
	 */
	public NewFolderAction(String text) {
		super(text);
		folderTree = Model.getModel().getOpenProject().getFolderTree();
	}
	
	/**
	 * Mit dieser Action wird ein neuer Ordner als Unterordner des übergebenen
	 * Folders erstellt. 
	 * 
	 * @param selectedFolder der selektierte Folder
	 * @param text Text dieser Action
	 */
	public NewFolderAction(Folder selectedFolder, String text) {
		this(text);
		this.selected = selectedFolder;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		String parentID = "";
		if(selected != null) {
			parentID = selected.getID();
		}
		String title = promptTitle();
		if(title != null) {
			folderTree.requestNewFolder(title, parentID);
		}
	}
	
	/**
	 * Öffnet einen Eingabedialog und fragt den Anwender nach dem Namen für den
	 * neuen Folder.
	 * @return der Titel des Folders oder null, wenn der Anwender die Eingabe abgebrochen
	 * hat.
	 */
	protected String promptTitle() {
		MyInputDialog dialog = new MyInputDialog(2, 30, "TITLE_FOLDER_NAME", 
				"PROMPT_FOLDER_NAME", "", "EMPTY");
		dialog.openDialog();
		return dialog.getUserInput();
	}

}
