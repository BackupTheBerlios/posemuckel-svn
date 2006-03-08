package posemuckel.client.gui;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import posemuckel.common.GetText;

public class GUI_Project_Window extends ApplicationWindow
{
	
	public GUI_Project_Window()
	{
		super(null);
		System.out.println("Window: " + this);
		this.setBlockOnOpen(true);
		this.open();
	}
	
	protected Control createContents(Composite parent)
	{
		Group projectGroup = new Group(parent, SWT.NONE);
		projectGroup.setText(GetText.gettext("PROJECT_OVERVIEW"));
		Label label_description = new Label(projectGroup, SWT.NONE);
		label_description.setText(GetText.gettext("PROJECT_DESCRIPTION"));
		label_description.setLocation(10, 30);
		label_description.pack();
		getShell().setText(GetText.gettext("PROJECT_OVERVIEW"));
		parent.setSize(600,400);
		return parent;
	}
}
