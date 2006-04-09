package posemuckel.client.gui;

import java.util.Vector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import posemuckel.client.model.Model;
import posemuckel.client.model.Project;

/**
 * Dieser Dialog ist für die Hochhalten-Funktion.
 * @author Posemuckel Team
 */
public class NotificationDialog extends Dialog {

	private ImageData imgdata;
	private String url;
	private String comment;
	private Text text_comment;
	private Label commentlabel;
	private Label screenshotlabel;
		
	
	Vector<ImageData> decoded = new Vector<ImageData>();
	private String sender;
	private Shell shell;
	

	public NotificationDialog(Shell shell, ImageData imgdata, String user, String url, String comment) {
		super(shell);
		this.imgdata = imgdata;
		this.url = url;
		this.comment = comment;
		this.sender = user;
		this.shell = shell;
	}

	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(sender+" empfiehlt dir diese Seite");
		getShell().setImage(ImageManagment.getRegistry().get(ImageManagment.SHELL_ICON));
		Composite comp = (Composite)super.createDialogArea(parent);
		GridLayout gridlayout = new GridLayout(1,false);
		gridlayout.marginHeight = 20;
		gridlayout.marginWidth = 20;
		comp.setLayout(gridlayout);
		
		GridData data;
		
		screenshotlabel = new Label(comp, SWT.SHADOW_NONE);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 1;
		screenshotlabel.setLayoutData(data);
		screenshotlabel.setText("Screenshot:");
				
		final Label screenshot = new Label(comp, SWT.BORDER);
		data = new GridData(GridData.FILL_BOTH);
		data.verticalSpan = 1;
		screenshot.setLayoutData(data);
		screenshot.setToolTipText("Diese Seite im Browser laden");
		Image newimage = new Image(comp.getDisplay(),imgdata);
		screenshot.setImage(newimage);
		screenshot.addMouseListener(new MouseListener() {
			// Dieser Listener öffnet eine URL im Browser
			// beim Klicken auf das Bild.
			public void mouseDown(MouseEvent arg0) {}
			public void mouseDoubleClick(MouseEvent arg0) {}
			public void mouseUp(MouseEvent arg0) {
				Project proj = Model.getModel().getOpenProject();
				proj.fireNewURL(url);
			}
		});
		
		screenshot.addMouseTrackListener(new MouseTrackListener() {
			// Dieser Listener zeigt einen schicken
			// Hand-Cursor an, wenn der Mauszeiger innerhalb
			// des Screenshot-Bereiches ist.
			public void mouseEnter(MouseEvent arg0) {
				Cursor cur = new Cursor(shell.getDisplay(),SWT.CURSOR_HAND);
				screenshot.setCursor(cur);
			}
			public void mouseExit(MouseEvent arg0) {
				Cursor cur = new Cursor(shell.getDisplay(),SWT.CURSOR_APPSTARTING);
				screenshot.setCursor(cur);
			}
			public void mouseHover(MouseEvent arg0) {}
			
		});
		
		
		commentlabel = new Label(comp, SWT.SHADOW_NONE);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 1;
		commentlabel.setLayoutData(data);
		commentlabel.setText("Kommentar zu: "+url);
		commentlabel.setEnabled(true);
		
		text_comment = new Text(comp, SWT.MULTI |SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		data = new GridData( GridData.FILL_BOTH );
		data.verticalSpan = 1;
		data.horizontalSpan = 1;
		data.heightHint = 100;
		text_comment.setLayoutData(data);
		text_comment.setEditable(false);
		text_comment.setText(comment);

		return comp;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent,IDialogConstants.CLOSE_ID,"Schließen",true);
	}
	
	protected void buttonPressed(int buttonID) {
		switch (buttonID) {
			case IDialogConstants.CLOSE_ID: {
				close();
				break;
			}
		}
	}
	
}
