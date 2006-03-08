package posemuckel.client.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import posemuckel.common.GetText;
import posemuckel.client.net.Client;

public class AboutPosemuckelDialog extends Dialog {
	
	private static final String TUTOR = new String(
			"     Stephan Lukosch"
	);
	private static final String MENTOR = new String(
			"     Jens-Dietrich Neppe (jn@linux-fuer-alle.de)"
			);
	private static final String PROJECT_MANAGER = new String(
			"     Lars Michler (michlerl@tiscali.de)"
			);
	private static final String DEVELOPERS = new String(
			"     Tanja Buttler (tanja.buttler@t-online.de)\n"+
			"     Christian Wiedemann (email?)\n"+
			"     Holger Bach (bach@gmx.li)\n"+
			"     Sandro Scaiano (sansca@web.de)"
	);
	
	private String title;
	private String version;
	private String name;
	private String buttonLabel;
	private Label label_logo;
	private Label label_version;
	private Label label_text;

	private final ImageRegistry imageRegistry = new ImageRegistry();
	private FontRegistry fontRegistry;
	private Color white;
	
	public AboutPosemuckelDialog(Shell shell) {
		super(shell);
		getDescriptions();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(title);
		getShell().setImage(ImageManagment.getRegistry().get(ImageManagment.SHELL_ICON));
		Composite comp = (Composite)super.createDialogArea(parent);

		white = comp.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		comp.setBackground(white);

		GridLayout gridlayout = new GridLayout(1,false);
		gridlayout.marginHeight = 10;
		gridlayout.marginWidth = 0;
		comp.setLayout(gridlayout);

		label_version = new Label(comp, SWT.SHADOW_NONE);
		label_version.setText(version+" "+Client.getVersion()+" \""+GetText.gettext("VERSION_NAME")+"\"");
		label_version.setBackground(comp.getBackground());
		GridData data = new GridData( SWT.CENTER, SWT.CENTER, false, false);
		label_version.setLayoutData(data);
		
		imageRegistry.put("Logo", ImageDescriptor.createFromFile(
				this.getClass(), "Logo.jpg")
				);
		label_logo = new Label(comp, SWT.SHADOW_IN);
		label_logo.setImage(imageRegistry.get("Logo"));
		data = new GridData( SWT.CENTER, SWT.CENTER, false, false );
		label_logo.setLayoutData(data);
		
		label_text = new Label(comp, SWT.SHADOW_NONE);
		label_text.setText(
				"\n\n" + GetText.gettext("ABOUT_BACKGROUND") + "\n\n"
				+ GetText.gettext("ABOUT_TUTOR") + ":\n"
				+ TUTOR + "\n\n"
				+ GetText.gettext("ABOUT_MENTOR") + ":\n"
				+ MENTOR + "\n\n"
				+ GetText.gettext("ABOUT_PROJECT_MANAGER") + ":\n"
				+ PROJECT_MANAGER + "\n\n"
				+ GetText.gettext("ABOUT_DEVELOPERS") + ":\n"
				+ DEVELOPERS + "\n\n"
				);
		label_text.setBackground(comp.getBackground());
		data = new GridData( SWT.CENTER, SWT.CENTER, false, false );
		label_text.setLayoutData(data);
		
		return comp;
	}
	
	private void getDescriptions() {
		title = GetText.gettext("ABOUT_POSEMUCKEL");
		version = GetText.gettext("VERSION_TEXT");
		buttonLabel = GetText.gettext("ABOUT_BUTTON");
	}
	
	private void createFontRegistry() {
		fontRegistry = new FontRegistry();
		fontRegistry.put("failure", new FontData[]{new FontData("arial", 13, SWT.BOLD)});
	}
	
	FontRegistry getFonts() {
		//die fontRegistry darf erst initialisiert werden, wenn das Display bereits existiert
		if(fontRegistry == null)createFontRegistry();
		return fontRegistry;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs#createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
    protected Control createButtonBar(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        // create a layout with spacing and margins appropriate for the font
        // size.
        GridLayout layout = new GridLayout();
        layout.numColumns = 0; // this is incremented by createButton
        layout.makeColumnsEqualWidth = true;
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        composite.setLayout(layout);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER);
        composite.setLayoutData(data);
        composite.setFont(parent.getFont());
        composite.setBackground(white);
        parent.setBackground(white);
        // Add the buttons to the button bar.
        createButtonsForButtonBar(composite);
        return composite;
    }

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create only OK button with Cancel function
		createButton(parent, IDialogConstants.CANCEL_ID,
				//IDialogConstants.OK_LABEL, 
				buttonLabel,
				true);
	}
	
}
