package posemuckel.client.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import posemuckel.client.gui.browser.HelpBrowser;
import posemuckel.common.GetText;

public class HelpContentsAction extends Action
{
	
	public HelpContentsAction()
	{
		super("&"+GetText.gettext("HELP_CONTENTS")+"@Ctrl+H", AS_PUSH_BUTTON);
		setToolTipText(GetText.gettext("HELP_CONTENTS"));
		setImageDescriptor(ImageDescriptor.createFromFile
				(this.getClass(),"blank.gif"));
	}
	public void run()
	{
//		 So kann man eine HTML-Seite öffnen, die sich im unterverzeichnis doc
//		 des aktuellen Arbeitsverzeichnisses befindet:
//		  System.getProperty("user.dir") und die Methode über ein file-Objekt
//			stehen zur Verfügung.
//				String fileURL = "./doc/allclasses-noframe.html";
//				File file = new File(fileURL);
//				System.out.println(System.getProperty("user.dir")+"/doc/allclasses-noframe.html");
//				System.out.println(file.getAbsolutePath());
//				new CreateStaticBrowser(file.getAbsolutePath(), "hilfe");
		new HelpBrowser(System.getProperty("user.dir")+"/doc/userdoc/index.htm", "Posemuckel-Hilfe");
	}
}