package posemuckel.client.gui;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Ein Hinweis-Tab für den Login-Dialog
 * @author Posemuckel Team
 *
 */
public class HintsComposite extends Composite implements TabContent {

	private Label label_feedback;
	private Label text_comment;
	private FontRegistry fonts;
	private GridLayout gridlayout;

	public HintsComposite(Composite parent, LoginDialog dialog) {
		super(parent, SWT.NONE);
		this.fonts = dialog.getFonts();
		gridlayout = new GridLayout(1,false);
		gridlayout.marginHeight = 30;
		gridlayout.marginWidth = 30;
		gridlayout.horizontalSpacing = 10;
		gridlayout.verticalSpacing = 10;
		this.setLayout(gridlayout);
		
		this.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		text_comment = new Label(this, SWT.SHADOW_IN | SWT.WRAP);
		GridData data = new GridData( GridData.CENTER );
		data.horizontalSpan=1;
		data.widthHint = 500;
		text_comment.setLayoutData(data);
		text_comment.setText("Liebe Benutzer!\n\nDie Nutzung dieser Software dient dem Zweck, gemeinschaftlich durch das Internet zu surfen. Dies bedeutet, dass andere jederzeit einen Einblick in das eigene Surfverhalten erhalten. Wenn Sie das nicht wollen, sollten Sie diese Software nicht benutzen!\n\nDas Posemuckel-Team");
		text_comment.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		label_feedback = new Label(this, SWT.SHADOW_IN);
		data = new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.FILL_BOTH );
		data.horizontalSpan=1;		
		label_feedback.setLayoutData(data);
		label_feedback.setFont(fonts.get("failure"));
		label_feedback.setText("");
		label_feedback.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	public void setUserFeedback(String userfeedback, int type) {
		switch(type) {
		case 0: label_feedback.setForeground(Colors.getWarning());
			break;
		case 1: label_feedback.setForeground(Colors.getSuccess());
			break;
		default: label_feedback.setForeground(Colors.getInfo());
		}
		label_feedback.setText(userfeedback);
	}

	public void loadDefaults() {}

	public void save2Config() {}

	public void performAction() {}

	public boolean validInput() {
		return false;
	}

	public void treatInputError() {
		// TODO Auto-generated method stub
		
	}

}
