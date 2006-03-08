/**
 * 
 */
package posemuckel.client.gui.actions;

import org.eclipse.jface.action.Action;

import posemuckel.client.model.Folder;
import posemuckel.client.model.FolderTree;
import posemuckel.client.model.Model;
import posemuckel.common.GetText;


/**
 * Löscht einen Folder aus dem FolderTree.
 * @author Posemuckel Team
 *
 */
public class DeleteFolderAction extends Action{
	
	//der FolderBaum
	private FolderTree folderTree;
	private Folder folder;
	
	/**
	 * Diese Action löscht einen Folder aus dem FolderTree
	 * @param selectedFolder der Folder, der im Tree selektiert wurde
	 */
	public DeleteFolderAction(Folder selectedFolder) {
		//super(GetText.gettext("Ergebnisse zeigen") + "@Ctrl+R", AS_PUSH_BUTTON);
		super(GetText.gettext("DELETE_FOLDER"));
		folder = selectedFolder;
		folderTree = Model.getModel().getOpenProject().getFolderTree();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		//nur leere Folder dürfen gelöscht werden
		if(!folder.hasChildren()) {
			folderTree.requestDeleteFolder(folder.getID());
		}
	}
}
