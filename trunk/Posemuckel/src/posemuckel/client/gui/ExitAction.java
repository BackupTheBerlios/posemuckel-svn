package posemuckel.client.gui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.ApplicationWindow;

import posemuckel.client.net.ThreadLauncher;

public class ExitAction extends Action
{
 ApplicationWindow window;

 public ExitAction(ApplicationWindow w)
 {
  window = w;
  setText("E&xit@Ctrl+W");
  setToolTipText("Exit the window");
 }

 public void run()
 {
  window.close();
  ThreadLauncher.interruptAll();
 }
}