package posemuckel.client.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import posemuckel.client.model.Model;
import posemuckel.client.model.User;
import posemuckel.common.GetText;


public class AddBuddyAction extends Action
{
	
	private Table table;

	public AddBuddyAction()
	{
		super("&"+GetText.gettext("ADD_BUDDY")+"@Ctrl+A", AS_PUSH_BUTTON);
		setToolTipText(GetText.gettext("ADD_BUDDY"));
		setImageDescriptor(ImageDescriptor.createFromFile
				(this.getClass(),"icons/add.gif"));
	}
	
	public AddBuddyAction(Table table)
	{
		this();
		this.table = table;
	}
	
	public void run()
	{
		User myself = Model.getModel().getUser();
		TableItem item = table.getSelection()[0];
		myself.getBuddyList().addBuddy(item.getText(1));

	}
}
