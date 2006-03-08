package posemuckel.client.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import posemuckel.client.model.Model;
import posemuckel.client.model.User;
import posemuckel.client.model.Visitor;
import posemuckel.client.model.Webpage;
import posemuckel.client.model.Webtrace;
import posemuckel.client.model.event.WebTraceAdapter;
import posemuckel.client.model.event.WebTraceEvent;
import posemuckel.common.EnumsAndConstants;
import posemuckel.common.GetText;

public class NoteDialog extends Dialog {

	private String url;
	private Text text_note;
	private Label notelabel;
	private Label result;
	private Webpage page;
	private Label allnoteslabel;
	private Text text_notes;
	private User user;
	private String myname;
	private Shell shell;
	private String mytext;
	private Label myratinglabel = null;
	private Label ratinglabel;
	private Visitor me;
	private Spinner voteWidget;
	private Label ratedlabel;
	
	private static final int SET_NOTE = IDialogConstants.NO_TO_ALL_ID +1;
	
	public NoteDialog(Shell shell, Webpage page) {
		super(shell);
		this.url = page.getURL();
		this.shell = shell;
		this.page = page;
		this.user = Model.getModel().getUser();
		this.myname = user.getNickname();
		this.me = page.getVisitorByName(myname);
		addTraceListener();
	}

	/**
	 * Hier wird eingefügt:
	 * - Bewertungs-Spinbox, fall man die Seite schon besucht hat
	 * - Ausgabe-Textfeld für Notizen anderer, falls überhaupt jemand
	 *   eine Notiz geschrieben hat.
	 * - Eingabe-Textfeld für Notizen, falls man selbst die Seite
	 *   schon besucht hat.
	 */
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText("Notizen zu "+url);
		Composite comp = (Composite)super.createDialogArea(parent);
		GridLayout gridlayout = new GridLayout(2,false);
		gridlayout.marginHeight = 10;
		gridlayout.marginWidth = 10;
		comp.setLayout(gridlayout);
		
		GridData data;		

		ratedlabel = new Label(comp, SWT.SHADOW_NONE);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 2;
		ratedlabel.setLayoutData(data);	
		
		if(page.getRating() != Visitor.NO_RATING) {
			float rated = Math.round(page.getRating() * 5);
			ratedlabel.setText("Die durchschnittliche Bewertung dieser Seite ist "+String.valueOf(rated));
		} else {
			//noch niemand hat die Seite bewertet
			ratedlabel.setText("Noch niemand hat diese Seite bewertet");
		}
		
		if( page.isVisitedBy(myname)) {
			// Wenn von mir besucht, dann kann ich auch bewerten:
			myratinglabel = new Label(comp, SWT.SHADOW_NONE);
			data = new GridData(SWT.FILL, SWT.CENTER, true, false);
			data.horizontalSpan = 2;
			myratinglabel.setLayoutData(data);

			if (me.getRating() == Visitor.NO_RATING) {
				myratinglabel.setText("Du hast diese Seite noch nicht bewertet!");				
			} else {
				myratinglabel.setText("Deine Bewertung ist: "+me.getRatingAsString());
			}

			ratinglabel = new Label(comp, SWT.SHADOW_NONE);
			data = new GridData(SWT.FILL, SWT.CENTER, true, false);
			data.horizontalSpan = 1;
			ratinglabel.setLayoutData(data);
			ratinglabel.setText("Neue Bewertung:");
			
			voteWidget = new Spinner(comp, SWT.NONE);
			voteWidget.setMaximum(5);
			voteWidget.setMinimum(0);
			if(me.getRating() != Visitor.NO_RATING) {
				voteWidget.setSelection(me.getRating());
			} else {
				//Standardwert setzen, bisher wurde 0 gesetzt
				voteWidget.setSelection(3);
			}
			data = new GridData(SWT.FILL, SWT.CENTER, true, false);
			data.horizontalSpan = 1;
			voteWidget.setLayoutData(data);
		}
		
		if( page.hasNote() ) {
			allnoteslabel = new Label(comp, SWT.SHADOW_NONE);
			data = new GridData(SWT.FILL, SWT.CENTER, true, false);
			data.horizontalSpan = 2;
			allnoteslabel.setLayoutData(data);
			allnoteslabel.setText("Notizen anderer hierzu:");
			
			text_notes = new Text(comp, SWT.MULTI |SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
			data = new GridData( GridData.FILL_BOTH );
			data.verticalSpan = 1;
			data.horizontalSpan = 2;
			data.heightHint = 100;
			data.widthHint = 300;
			text_notes.setLayoutData(data);
			text_notes.setEditable(false);
			Visitor[] visitors = page.getVisitors();
			for ( Visitor v : visitors ) {
				String visitor = v.getName();
				if( !visitor.equals(myname) )
					text_notes.append(v.getName()+":"+EnumsAndConstants.LS+v.getComment()+EnumsAndConstants.LS);
			}			
		}
		if( page.isVisitedBy(myname)) {
			notelabel = new Label(comp, SWT.SHADOW_NONE);
			data = new GridData(SWT.FILL, SWT.CENTER, true, false);
			data.horizontalSpan = 2;
			notelabel.setLayoutData(data);
			notelabel.setText("Meine Notiz zu dieser Seite:");
		
			text_note = new Text(comp, SWT.MULTI |SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
			data = new GridData( GridData.FILL_BOTH );
			data.verticalSpan = 1;
			data.horizontalSpan = 2;
			data.heightHint = 100;
			data.widthHint = 300;
			text_note.setLayoutData(data);
			text_note.setEditable(true);
			Visitor[] visitors = page.getVisitors();
			for ( Visitor v : visitors ) {
				if( v.getName().equals(myname) )
					text_note.append(v.getComment());
			}
			mytext = text_note.getText();
		}
		
		result = new Label(comp, SWT.SHADOW_NONE);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 2;
		result.setLayoutData(data);
		getShell().setImage(ImageManagment.getRegistry().get(ImageManagment.SHELL_ICON));
		return comp;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		if( page.isVisitedBy(myname))
			createButton(parent,SET_NOTE,"Übernehmen",true);		
		createButton(parent,IDialogConstants.CLOSE_ID,"Schließen",false);
	}
	
	protected void buttonPressed(int buttonID) {
		switch (buttonID) {
		case IDialogConstants.CLOSE_ID: {
			close();
			break;
		}
		case SET_NOTE: {
			String input = text_note.getText();
			int rating = voteWidget.getSelection();
			boolean rating_changed = (rating != me.getRating());
			if ( input.equals(mytext) && !rating_changed ) {
				result.setText("Es wurde nichts verändert");
				return;
			}
			mytext=input;
			if(rating_changed) {
				page.addNote(GetText.replaceRN(input),rating);
			} else {
				//page.addNote(GetText.replaceRN(input),-1);
				//TODO Quickfix durch ordentliche Lösung ersetzen
				page.addNote(GetText.replaceRN(input),rating);
			}
			break;
		}
		}
	}
	
	private void addTraceListener() {
		final Webtrace trace = Model.getModel().getOpenProject().getWebtrace();
		WebTraceAdapter adapter = new WebTraceAdapter() {
			public void newNote(final WebTraceEvent event) {
				Webpage wp = (Webpage)event.getRoot();
				Runnable run = new Runnable() {
					public void run() {
						result.setText("Die Daten wurden erfolgreich übertragen.");
						if( myratinglabel != null )
							myratinglabel.setText("Deine Bewertung ist: "+me.getRatingAsString());
					}
				};		
				if(shell.isDisposed()) {
					trace.removeListener(this);
				} else if ( url.equals(wp.getURL()) ) {
					Display.getDefault().asyncExec(run);
				}
			}
		};
		trace.addListener(adapter);
	}
	
}
