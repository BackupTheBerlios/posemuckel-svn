package posemuckel.client.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import posemuckel.common.GetText;


/**
 * This class provides the Dialog for invitations that are sent out
 * to users when a new project is created.
 * It is called by posemuckel.client.gui.InvitationInfo
 * 
 * @author Posemuckel Team
 *
 */
public class InvitationDialog extends Dialog {
	
	private final static int LABEL_WIDTH = 320;
	private final static int INDENT = 20;
	
	private String addressee;
	private String project;
	private String description;
	private String owner;
	
	private String title;
	private String hello;
	private Label label_hello;
	private String intro;
	private Label label_intro;
	private Label label_project;
	private String desc;
	private Label label_desc;
	private Label label_description;
	private String outro;
	private Label label_outro;
	private String greeting;
	private Label label_greeting;
	private String instructions;
	private Label label_instructions;
	
	private Color white;
	private Color yellow;
	private Color gray;
	
	private Composite comp;

	/**
	 * Constructor
	 * 
	 * @param shell			parent shell
	 * @param addressee		the name of the user who is being invited
	 * @param project		the project name the user is being invited to
	 * @param description	the description of the project
	 * @param owner			the owner of the project (and sender of the invitation)
	 * 
	 * @author Posemuckel Team
	 */
	public InvitationDialog(Shell shell, String addressee, String project, String description, String owner) {
		super(shell);
		getDescriptions();
		this.addressee = addressee;
		this.project = project;
		this.description = description;
		this.owner = owner;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(title + " \"" + project +"\"");
		getShell().setImage(ImageManagment.getRegistry().get(ImageManagment.SHELL_ICON));
		comp = (Composite)super.createDialogArea(parent);

		GridLayout gridlayout = new GridLayout(1,true);
		gridlayout.marginHeight = 15;
		gridlayout.marginWidth = 15;
		comp.setLayout(gridlayout);
		
		FontRegistry fr = JFaceResources.getFontRegistry();
		Font headFont = fr.get(JFaceResources.HEADER_FONT);
		Font bannerFont = fr.get(JFaceResources.BANNER_FONT);
		Font defaultFont = fr.get(JFaceResources.DEFAULT_FONT);
		Font dialogFont = fr.get(JFaceResources.DIALOG_FONT);
		Font textFont = fr.get(JFaceResources.TEXT_FONT);

		// TODO: dispose Colors, but where?
		yellow = new Color(comp.getDisplay().getCurrent(), 255, 255, 200);
		white = comp.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		gray = comp.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
		comp.setBackground(yellow);
		
		label_hello = new Label(comp, SWT.WRAP);
		label_intro = new Label(comp, SWT.WRAP);
		label_project = new Label(comp, SWT.WRAP);
		label_desc = new Label(comp, SWT.WRAP);
		label_description = new Label(comp, SWT.WRAP);
		label_outro = new Label(comp, SWT.WRAP);
		label_greeting = new Label(comp, SWT.WRAP);
		// for first horizontal line:
		final Canvas canvas = new Canvas(comp, SWT.NONE);
		canvas.setSize(LABEL_WIDTH, 10);
		label_instructions = new Label(comp, SWT.WRAP | SWT.CENTER);
		// for second horizontal line:
		final Canvas canvas2 = new Canvas(comp, SWT.NONE);
		canvas2.setSize(LABEL_WIDTH, 10);

		label_hello.setText(hello + " " + addressee + "!\n");
		label_intro.setText(intro);
		label_project.setText("\"" + project + "\"");
		label_desc.setText(desc);
		label_description.setText("- " + description + " -");
		label_outro.setText(outro);
		label_greeting.setText("\n" + greeting + ",\n" + owner + "\n");
    	label_instructions.setText(instructions);
 
    	label_hello.setFont(bannerFont);
     	label_greeting.setFont(bannerFont);
 
		label_hello.setBackground(comp.getBackground());
		label_intro.setBackground(comp.getBackground());
		//label_project.setBackground(white);
		label_project.setBackground(comp.getBackground());
		label_desc.setBackground(comp.getBackground());
		//label_description.setBackground(white);		
		label_description.setBackground(comp.getBackground());		
		label_outro.setBackground(comp.getBackground());
		label_greeting.setBackground(comp.getBackground());
		canvas.setBackground(comp.getBackground());
		label_instructions.setBackground(comp.getBackground());
		canvas2.setBackground(comp.getBackground());
			
		GridData data = new GridData( SWT.BEGINNING, SWT.CENTER, true, false);
		data.widthHint = LABEL_WIDTH;
		label_hello.setLayoutData(data);
		
		data = new GridData( SWT.BEGINNING, SWT.CENTER, true, false);
		data.widthHint = LABEL_WIDTH;
		label_intro.setLayoutData(data);
		
		data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false);
		data.widthHint = LABEL_WIDTH - INDENT;
		data.horizontalIndent = INDENT;
		label_project.setLayoutData(data);
		
		data = new GridData( SWT.BEGINNING, SWT.CENTER, true, false);
		data.widthHint = LABEL_WIDTH;
		label_desc.setLayoutData(data);

		data = new GridData( SWT.BEGINNING, SWT.CENTER, false, false);
		data.widthHint = LABEL_WIDTH - INDENT;
		data.horizontalIndent = INDENT;
		label_description.setLayoutData(data);
		
		data = new GridData( SWT.BEGINNING, SWT.CENTER, true, false);
		data.widthHint = LABEL_WIDTH;
		label_outro.setLayoutData(data);
		
		data = new GridData( SWT.BEGINNING, SWT.CENTER, true, false);
		data.widthHint = LABEL_WIDTH;
		label_greeting.setLayoutData(data);
		
		data = new GridData( SWT.BEGINNING, SWT.CENTER, true, false);
		data.widthHint = LABEL_WIDTH;
		data.heightHint = 10;
		canvas.setLayoutData(data);
		
		data = new GridData( SWT.BEGINNING, SWT.CENTER, true, false);
		data.widthHint = LABEL_WIDTH;
		label_instructions.setLayoutData(data);
		
		data = new GridData( SWT.BEGINNING, SWT.CENTER, true, false);
		data.widthHint = LABEL_WIDTH;
		data.heightHint = 10;
		canvas2.setLayoutData(data);
		
		final int y = canvas.getSize().y - 1 ;
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				gc.setForeground(gray);
		    	gc.drawLine(0, y, LABEL_WIDTH, y);
			}
		});
		
		canvas2.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				gc.setForeground(gray);
		    	gc.drawLine(0, 1, LABEL_WIDTH, 1);
			}
		});

		return comp;
	}
	

	private void getDescriptions() {
		title = GetText.gettext("INVITATION_TITLE");
		hello = GetText.gettext("HELLO");
		intro = GetText.gettext("INVITATION_INTRO");
		desc = GetText.gettext("INVITATION_DESC");
		outro = GetText.gettext("INVITATION_OUTRO");
		greeting = GetText.gettext("GREETING");
		instructions = GetText.gettext("INVITATION_INSTR");
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
        composite.setBackground(comp.getBackground());
        parent.setBackground(comp.getBackground());
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
				IDialogConstants.OK_LABEL, true);
	}
}


