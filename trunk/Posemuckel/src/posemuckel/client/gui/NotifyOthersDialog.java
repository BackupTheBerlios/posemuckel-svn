package posemuckel.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import posemuckel.client.model.Model;
import posemuckel.client.model.Project;
import posemuckel.client.model.event.NotifyEvent;
import posemuckel.client.model.event.NotifyListener;
import posemuckel.common.GetText;

/**
 * Dieser Dialog ist für die Hochhalten-Funktion.
 * @author Posemuckel Team
 */
public class NotifyOthersDialog extends Dialog {

	private Image image;
	private String url;
	private List members;
	private Button button_select;
	private List selectedList;
	private Button button_deselect;
	private Text text_comment;
	private Label commentlabel;
	private Label screenshotlabel;
	private Label participantslabel;
	private Label recieverslabel;
	private Label response;
	private Shell shell;
	private ArrayList<String> participants;
	
	private boolean active = true;
	
	Vector<String> encoded = new Vector<String>();

	
	private static final int SEND_BUTTON = IDialogConstants.NO_TO_ALL_ID +1;

	public NotifyOthersDialog(Shell shell, Image image, String url, ArrayList<String> participants) {
		super(shell);
		this.shell = shell;
		this.participants = participants; 
		this.image = image;
		this.url = url;
		addListener();
		Image2String convert = new Image2String(image,encoded);
		convert.start();	
		
	}

	protected Control createDialogArea(Composite parent) {
		//TODO Mehrsprachigkeit
		this.getShell().setText("Diese Seite hochhalten");
		getShell().setImage(ImageManagment.getRegistry().get(ImageManagment.SHELL_ICON));
		Composite comp = (Composite)super.createDialogArea(parent);
		GridLayout gridlayout = new GridLayout(4,false);
		gridlayout.marginHeight = 20;
		gridlayout.marginWidth = 20;
		comp.setLayout(gridlayout);
		
		GridData data;
		
		screenshotlabel = new Label(comp, SWT.SHADOW_NONE);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 1;
		screenshotlabel.setLayoutData(data);
		screenshotlabel.setText("Screenshot (skaliert):");
		
		participantslabel = new Label(comp, SWT.SHADOW_NONE);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 1;
		participantslabel.setLayoutData(data);
		participantslabel.setText("Mögliche\nEmpfänger:");
		
		@SuppressWarnings("unused") Label do_not_delete = new Label(comp, SWT.SHADOW_NONE);
		
		recieverslabel = new Label(comp, SWT.SHADOW_NONE);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 1;
		recieverslabel.setLayoutData(data);
		recieverslabel.setText("Empfänger:");

		
		Label screenshot = new Label(comp, SWT.BORDER);
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		data.verticalSpan = 2;
		ImageData imgdata = image.getImageData();
		int width = 300;
		int height = (imgdata.height*width)/imgdata.width;
		ImageData newimgdata = imgdata.scaledTo(width,height);
		data.heightHint = height;
		data.widthHint = width;
		screenshot.setLayoutData(data);
		Image newimage = new Image(comp.getDisplay(),newimgdata);
		screenshot.setImage(newimage);
				
		members = new List(comp, SWT.MULTI | SWT.BORDER |SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 200;
		data.verticalSpan = 2;
		members.setLayoutData(data);
		for( String p : participants ) {
			members.add(p);
		}
		
		button_select = new Button(comp, SWT.ARROW | SWT.RIGHT);
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		data.horizontalSpan = 1;
		button_select.setLayoutData(data);
				
		selectedList = new List(comp, SWT.MULTI | SWT.BORDER |SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 200;
		data.widthHint = 60;
		data.verticalSpan = 2;
		selectedList.setLayoutData(data);
		
		button_deselect = new Button(comp, SWT.ARROW | SWT.LEFT);
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		data.horizontalSpan = 1;
		button_deselect.setLayoutData(data);
		
		// Hier ist das Verhalten des Buttons zum Deselektieren
		// implementiert.
		button_deselect.addListener( SWT.Selection,
				 new Listener() {
					public void handleEvent(Event arg0) {
						response.setText("");
						String[] selected = selectedList.getSelection();
						if ( selected.length != 0 ) {
							for ( String buddy : selected ) {
								selectedList.remove(buddy);
								members.add(buddy);
							}
						}
					}
				 }
		);
		
		// Hier ist das Verhalten des Buttons zum Selektieren implementiert.
		button_select.addListener( SWT.Selection,
				 new Listener() {
					public void handleEvent(Event arg0) {
						response.setText("");
						String[] selected = members.getSelection();
						if ( selected.length != 0 ) {
							for ( String buddy : selected ) {
								members.remove(buddy);
								selectedList.add(buddy);
							}
						}
					}
				 }
		);
		
		commentlabel = new Label(comp, SWT.SHADOW_NONE);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 4;
		commentlabel.setLayoutData(data);
		commentlabel.setText("Mein Kommentar zu dieser Seite:");
		
		text_comment = new Text(comp, SWT.MULTI |SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		data = new GridData( GridData.FILL_BOTH );
		data.verticalSpan = 1;
		data.horizontalSpan = 4;
		data.heightHint = 100;
		text_comment.setLayoutData(data);
		text_comment.setEditable(true);
		text_comment.setText("");

		response = new Label(comp, SWT.SHADOW_NONE);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 4;
		response.setLayoutData(data);
		
		return comp;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent,SEND_BUTTON,"Abschicken",true);
		createButton(parent,IDialogConstants.CLOSE_ID,"Schließen",true);
	}
	
	protected void buttonPressed(int buttonID) {
		switch (buttonID) {
		case IDialogConstants.CLOSE_ID: {
			close();
			break;
		}
		case SEND_BUTTON: {
			//////// Wenn ich nicht aktiv bin, dann hau ich ab.
			if(!active)
				return;
			// Wenn keine Empfänger ausgewählt wurden, dann
			// kommt eine doofe Nachricht
			if(selectedList.getItemCount() == 0) {
				response.setText("Es wurden keine Empänger ausgewählt.");
				return;
			}
			String comment = GetText.replaceRN(text_comment.getText());
			active=false;
			response.setText("Sende die Nachricht...");
			synchronized(encoded) {
				if (encoded.isEmpty() ) {
					try {
						encoded.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Model.getModel().getOpenProject().notify(url, "Titel", comment, 
						String.valueOf(encoded.firstElement().length()), 
						encoded.firstElement(), selectedList.getItems());
			}
			break;
		}
		}
	}
	
	/**
	 * Diese Klasse dient einzig und allein der Codierung des Bildes in
	 * einen Base64-Konformen String. Dies geschieht als Thread, damit die
	 * Gui nicht so leidet.
	 * @author Posemuckel Team
	 */
	private class Image2String extends Thread {
		private Image image;
		private Vector<String> output;
		
		/**
		 * Setzt die Ein- und Ausgabe.
		 * @param image Das zu codierende Bild.
		 * @param encoded Der Vector, in dem das Ergebnis stehen wird.
		 */
		public Image2String(Image image, Vector<String> encoded) {
			this.image = image;
			this.output = encoded;
		}

		public void run() {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageData imgdata = image.getImageData();
			ImageLoader imageLoader = new ImageLoader();
			imageLoader.data = new ImageData[] {imgdata};
			imageLoader.save(out,SWT.IMAGE_JPEG);
			Base64 codec = new Base64();
			synchronized(output) {
				try {
					byte[] enc = codec.encode(out.toByteArray());
					String data = new String(enc,"UTF-8");
					output.add(data);
					output.notify();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void addListener() {
		/**
		 * Dieser Listener wartet auf das ACK vom Server
		 */
		final Project proj = Model.getModel().getOpenProject();
		if (proj == null)
			return;
		
		NotifyListener adapter = new NotifyListener() {

			public void ack() {
				Runnable run = new Runnable() {
					public void run() {
						response.setText("Die Nachricht wurde erfolgreich gesendet.");
						active=true;
					}
				};				
				if(shell.isDisposed()) {
					proj.removeListener(this);
				} else {
					Display.getDefault().asyncExec(run);
				}
			}

			public void notify(NotifyEvent event) {}

			public void newurl(String url) {}
			
		};
		proj.addListener(adapter);
	}
	
}
