/**
 * 
 */
package posemuckel.client.gui.resultsviewer;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import posemuckel.client.gui.actions.DeleteFolderAction;
import posemuckel.client.gui.actions.NewFolderAction;
import posemuckel.client.gui.browser.TreeIcons;
import posemuckel.client.model.Folder;
import posemuckel.client.model.Model;
import posemuckel.client.model.Webpage;
import posemuckel.common.GetText;

/**
 * Der ResultTree stellt die Baumstruktur 
 * der einsortierten Webseiten dar. Innerhalb des Baumes
 * können die Webseiten per Drag and Drop verschoben werden.
 * 
 * @author Posemuckel Team
 *
 */
public class ResultTree {
	//durch Refactoring aus der Klasse ResultDialog herausgezogen
	
	private TreeViewer resultsViewer;
	private ResultsViewerLabelProvider labelProvider;
	private Model model;
	
	
	/**
	 * Erstellt eine GUI für das Foldersystem.
	 * @param parent in dem der ResultTree dargestellt werden soll
	 */
	public ResultTree(Composite parent) {
		model = Model.getModel();
		buildAndLayoutTree(parent);
	}
	
	/**
	 * Setzt den Folder, der den Einstieg in das Foldersystem repräsentiert.
	 * 
	 * @param root Wurzelfolder
	 */
	public void setInput(Folder root) {
		resultsViewer.setInput(root);
		resultsViewer.expandAll();
	}
	
	/**
	 * Setzt den ResultTree zusammen. Für den Baum wird Hashlookup verwendet. 
	 * Das Kontextmenü des Baumes bezieht ein, welches Element selektiert wurde.
	 * @param parent
	 */
	private void buildAndLayoutTree(Composite parent) {
		resultsViewer = new TreeViewer(parent);
		resultsViewer.setContentProvider(new ResultsViewerContentProvider());
		//labelProvider = new ResultsViewerLabelProvider();
		labelProvider = new ResultsViewerLabelProvider(TreeIcons.getIcons());
		resultsViewer.setLabelProvider(labelProvider);
		resultsViewer.setUseHashlookup(true);
		
		// layout the tree viewer
		GridData layoutData = new GridData();
		layoutData.minimumWidth = 300;
		layoutData.minimumHeight = 300;		
		resultsViewer.getControl().setLayoutData(layoutData);
		createContextMenu(resultsViewer);
	}
	
	/**
	 * Erstellt einen Listener, der auf Klicks mit der rechten Maustaste reagiert.
	 * Es wird ein Kontextmenü, welches sich nach der Selektion richtet, erstellt.
	 * 
	 * @param treeViewer, dessen Control das Kontextmenü erhalten soll
	 */
	private void createContextMenu(final TreeViewer treeViewer) {
		treeViewer.getTree().addListener(SWT.MouseDown, new Listener() {

			public void handleEvent(Event event) {
				Point point = new Point(event.x, event.y);
				if (treeViewer.getTree().getMenu()!=null) {
					treeViewer.getTree().getMenu().dispose();
				}
				TreeItem item= treeViewer.getTree().getItem(point);
				if (event.button==3) {
					addContextMenu(item);
				}
			}
			
		});
	}
	
	/**
	 * Erstellt ein Kontextmenü, dessen Inhalt sich nach dem angegebenen TreeItem richtet.
	 * @param item bestimmt, was für Optionen der Anwender hat
	 */
	private void addContextMenu(TreeItem item) {
		MenuManager menu = new MenuManager();
		//die Actions erstellen und einfügen
		
		//wurde ein Folder selektiert?
		Folder selected = null;//wenn der Wert null bleibt, dann wurde kein Ordner selektiert
		if(item != null && item.getData() != null && item.getData() instanceof Folder) {
			selected = (Folder)item.getData();
		}		
		//kann ein neuer Unterordner erstellt werden?
		if(selected != null) {//ja
			menu.add(new NewFolderAction(selected, GetText.gettext("NEW_SUBFOLDER")));
		} else {//nein
			menu.add(new NewFolderAction(GetText.gettext("NEW_FOLDER")));
		}

		//kann der selektierte Folder gelöscht werden?
		if(selected != null && (!selected.hasChildren())) {
			menu.add(new DeleteFolderAction(selected));
		}
		Menu contextMenu = menu.createContextMenu(resultsViewer.getTree());
		//resultsViewer.getTree().setMenu(contextMenu);
		contextMenu.setVisible(true);
	}	
	
	/**
	 * Fügt einen ISelectionChangedListener für die Baumdarstellung des Foldersystems
	 * hinzu.
	 * @param listener
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		resultsViewer.addSelectionChangedListener(listener);
	}
	
	/**
	 * Aktiviert das DND für den ResultTree. Es werden die TransferTypes
	 * ResultTransferType.FOLDER_TYPE und ResultTransferType.URL_TYPE
	 * unterstützt.
	 *
	 */
	protected void enableDND() 
	{
		int operations = DND.DROP_MOVE | DND.DROP_DEFAULT ;
		ResultTransferType folderType = new ResultTransferType(ResultTransferType.FOLDER_TYPE);
		ResultTransferType urlType = new ResultTransferType(ResultTransferType.URL_TYPE);
		//Text wird zum Herausziehen erlaubt
		//die Reihenfolge ist wichtig! 
		Transfer[] types = new Transfer[] {folderType, urlType, TextTransfer.getInstance()};
		final Tree tree = resultsViewer.getTree();
		final Object[] dragItem = new Object[1];
		final TreeItem[] dragSourceItem = new TreeItem[1];

		resultsViewer.addDragSupport(operations, types, new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				//TODO das brauchen wir doch nicht, da sowieso nur Webpage und Folder 
				//enthalten sein können
				//dragItem hat ist eine Instanz von DragSource
				dragItem[0] = event.getSource();
				/*if (dragItem[0] instanceof Webpage) {
//					event.doit = true;
//				} else if(dragItem[0] instanceof Folder) {
//					//Folder können innerhalb des Baumes verschoben werden
//					event.doit = true;
//				} else {
//					System.err.println("unknown type " + dragItem[0].getClass());
//					event.doit = false;
//				}				
				/*TreeItem[] selection = tree.getSelection();
				if (selection.length > 0) //&& selection[0].getItemCount() == 0) 
				{
					event.doit = true;
					dragSourceItem[0] = selection[0];
				}*/ 
			};
			public void dragSetData (DragSourceEvent event) {
				//unser eingener TransferType wird bevorzugt behandelt
				if(ResultTransferType.supports(event.dataType)) {
					IStructuredSelection selection = (IStructuredSelection)resultsViewer.getSelection();
					Object selected = selection.getFirstElement();
					if(selected == null) return;
					if(selected instanceof Folder) {
						event.data = new String[] {ResultTransferType.FOLDER_TYPE, 
								((Folder)selected).getID()};
					} else {//instanceof Webpage!
						event.data = new String[] {ResultTransferType.URL_TYPE,
								((Webpage)selected).getURL()};
					}
				} else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					event.data = resultsViewer.getTree().getSelection()[0].getText();
				}

			}
			public void dragFinished(DragSourceEvent event) {
				if (event.detail == DND.DROP_MOVE) {
					//die Bearbeitung wird vom Ziel vorgenommen
					
					//dragSourceItem[0].dispose();
					dragSourceItem[0] = null;

				}
			}
		});
		
		resultsViewer.addDropSupport(operations, types, new DropTargetAdapter() {
			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
				if (event.item != null) {
					TreeItem item = (TreeItem)event.item;
					Point pt = Display.getCurrent().map(null, tree, event.x, event.y);
					Rectangle bounds = item.getBounds();
					if (pt.y < bounds.y + bounds.height/3) {
						event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
					} else if (pt.y > bounds.y + 2*bounds.height/3) {
						event.feedback |= DND.FEEDBACK_INSERT_AFTER;
					} else {
						event.feedback |= DND.FEEDBACK_SELECT;
					}
				}
			}
			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					event.detail = DND.DROP_NONE;
					return;
				}
				String parentFolderID = "";
				//String text = (String)event.data;
				if (event.item == null) {
					//TreeItem item = new TreeItem(tree, SWT.NONE);
					//item.setText(text);
					//die ParentFolderID nicht ändern
				} else {
					// get item the cursor is hovering over
					TreeItem item = (TreeItem)event.item;
					Folder currentFolder;
					if(item.getData() instanceof Folder) {
						currentFolder = (Folder)item.getData();
					} else {//das Item ist eine Webpage
						currentFolder = (Folder)item.getParentItem().getData();
					}
					 
					// get cursor position
					Point pt = Display.getCurrent().map(null, tree, event.x, event.y);
					// get item bounds
					Rectangle bounds = item.getBounds();
					// get parent of item
					//ist immer ein Folder, falls existent
					TreeItem parent = item.getParentItem();
					// wenn item, auf das cursor zeigt, einen parent hat:
					if (parent != null) {
						// get all items in parent
						//TreeItem[] items = parent.getItems();
						Folder parentFolder = (Folder) parent.getData();
						/*
						 * es kann erst eingefügt werden, wenn der Server zugestimmt hat!
						 * deshalb scheint der Index nicht gebraucht zu werden
						 */
//						int index = 0;
//						// get item index
//						for (int i = 0; i < items.length; i++) {
//							if (items[i] == item) {
//								index = i;
//								break;
//							}
//						}
//						// if in upper third of item insert source before item
						if (pt.y < bounds.y + bounds.height/3) {
//							TreeItem newItem = new TreeItem(parent, SWT.NONE, index);
//							newItem.setText(text);
							parentFolderID = parentFolder.getID();
							// if in lower third of item insert source after item
						} else if (pt.y > bounds.y + 2*bounds.height/3) {
//							TreeItem newItem = new TreeItem(parent, SWT.NONE, index+1);
//							newItem.setText(text);
							parentFolderID = parentFolder.getID();
							// if on item create subitem of item
						} else {
//							TreeItem newItem = new TreeItem(item, SWT.NONE);
//							newItem.setText(text);
							parentFolderID = currentFolder.getID();
						}
						
						// wenn item, auf das cursor zeigt, kein parent hat (in root liegt):
					} else {
						/*
						 * erst den Server fragen
						 *
						TreeItem[] items = tree.getItems();
						int index = 0;
						for (int i = 0; i < items.length; i++) {
							if (items[i] == item) {
								index = i;
								break;
							}
						}
						*/
						if (pt.y < bounds.y + bounds.height/3) {
//							TreeItem newItem = new TreeItem(tree, SWT.NONE, index);
//							newItem.setText(text);
						} else if (pt.y > bounds.y + 2*bounds.height/3) {
//							TreeItem newItem = new TreeItem(tree, SWT.NONE, index+1);
//							newItem.setText(text);
						} else {
//							TreeItem newItem = new TreeItem(item, SWT.NONE);
//							newItem.setText(text);
							parentFolderID = currentFolder.getID();		
						}
						
					}				
				}
				//die Nachrichten an den Server senden
				if(ResultTransferType.supports(event.currentDataType)) {
					String[] data = (String[]) event.data;
					String type = data[0];
					if(type.equals(ResultTransferType.FOLDER_TYPE)) {
						for (int i = 1; i < data.length; i++) {
							model.getOpenProject().getFolderTree().requestChangeParent(data[i] , parentFolderID);
						}
					} else if(type.equals(ResultTransferType.URL_TYPE)) {
						//die Wurzel sollte keine URLs erhalten
						String rootID = model.getOpenProject().getFolderTree().getFolderRoot().getID();
						if(rootID.equals(parentFolderID) || parentFolderID == null
								|| parentFolderID.equals("")) return;
						for (int i = 1; i < data.length; i++) {
							model.getOpenProject().getFolderTree()
								.requestChangeParentForURL(parentFolderID, data[i]);
						}
					}
				}
			}
		});
	}

}
