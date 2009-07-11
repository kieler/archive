package actions;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.batik.util.gui.resource.JToolbarButton;

/**
 * @author sja
 * 
 */
public class ActionPlayPause extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7189313198158891050L;
	private boolean pausemode = false;

	/**
	 * Default constructor. It just calls its super constructor
	 */
	public ActionPlayPause() {
		super();
	}

	/**
	 * @param arg0
	 */
	public ActionPlayPause(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ActionPlayPause(String arg0, Icon arg1) {
		super(arg0, arg1);
	}

	public void actionPerformed(ActionEvent arg0) {
		if (pausemode == true) {
			JToolbarButton button = (JToolbarButton) arg0.getSource();
			URL icon = ClassLoader
					.getSystemResource("toolbarButtonGraphics/media/Play16.gif");
			button.setIcon(new ImageIcon(icon));
		}
		else if (pausemode == false) {
			JToolbarButton button = (JToolbarButton) arg0.getSource();
			URL icon = ClassLoader
					.getSystemResource("toolbarButtonGraphics/media/Pause16.gif");
			button.setIcon(new ImageIcon(icon));
		}
		pausemode = !pausemode;
	}
}
