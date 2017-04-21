package odd.views;


import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;



public class OddView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "odd.views.SampleView";
	public static final MainPanel mainPanel = new MainPanel();
	static Frame frame;



	/**
	 * The constructor.
	 */
	public OddView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	//public static OddApplet applet = new OddApplet();
	public synchronized void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.EMBEDDED);

		frame = SWT_AWT.new_Frame(composite);
        frame.add(mainPanel);
        mainPanel.setVisible(true);
        frame.setBounds(100,100,1030,679);
        composite.addControlListener(new ControlListener() {
      	  public void controlMoved(ControlEvent e){
      	  }
      	  public void controlResized(ControlEvent e){
      		  mainPanel.changeFrameSize(composite.getSize().x, composite.getSize().y);
      	  }
      	});

        frame.setVisible(true);
	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}

	public static MainPanel getMainPanel(){return mainPanel;}
}
