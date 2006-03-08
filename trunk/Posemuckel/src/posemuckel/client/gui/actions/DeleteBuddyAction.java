package posemuckel.client.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import posemuckel.client.model.Model;
import posemuckel.common.GetText;

public class DeleteBuddyAction extends Action
{
	private Table table;
	
	public DeleteBuddyAction(Table table) {
		super("&"+GetText.gettext("DELETE_BUDDY"), AS_PUSH_BUTTON);
		setToolTipText(GetText.gettext("DELETE_BUDDY"));
		setImageDescriptor(ImageDescriptor.createFromFile
				(this.getClass(),"icons/close.gif"));
		this.table = table;
	}
	
	
	public void run()
	{
		TableItem item = table.getSelection()[0];
		Model.getModel().getUser().getBuddyList().deleteMember(item.getText(1));
	}
}