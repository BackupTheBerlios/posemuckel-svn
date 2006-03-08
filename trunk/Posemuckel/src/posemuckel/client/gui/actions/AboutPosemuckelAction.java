package posemuckel.client.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import posemuckel.client.gui.AboutPosemuckelDialog;
import posemuckel.common.GetText;

public class AboutPosemuckelAction extends Action
{
	
	public AboutPosemuckelAction()
	{
		super("&"+GetText.gettext("ABOUT_POSEMUCKEL"), AS_PUSH_BUTTON);
		setToolTipText(GetText.gettext("ABOUT_POSEMUCKEL"));
		setImageDescriptor(ImageDescriptor.createFromFile
				(this.getClass(),"blank.gif"));
	}
	public void run()
	{
		AboutPosemuckelDialog about = new AboutPosemuckelDialog(Display.getCurrent().getActiveShell());
		about.open();
	}
}