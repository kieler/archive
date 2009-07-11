package modelgui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import events.SimulationEvent;

/**
 * @author Steffen Jacobs
 * 
 */
public class ModelguiMenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -659607725696653674L;

	static String wikiSite = "http://rtsys.informatik.uni-kiel.de/wiki/index.php/Modelgui";
	private JMenu menuFile;
	private JMenuItem closeItem;

	private JMenuItem menuPreferences;
	private JMenu menuHelp;
	private JMenuItem aboutItem;
	private JMenuItem wikiItem;
	private PreferencesDialog preferencesDialog;
	private JFrame rootFrame;

	/**
	 * 
	 */
	public ModelguiMenuBar(PreferencesDialog prefsDialog) {
		super();
		this.preferencesDialog = prefsDialog;
		menuFile = new JMenu("File");

		menuPreferences = new JMenuItem("Preferences...");
		menuPreferences.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				preferencesDialog.loadPreferences();
				preferencesDialog.setLocationRelativeTo(rootFrame);
				preferencesDialog.setVisible(true);
			}
		});
		menuFile.add(menuPreferences);
		closeItem = new JMenuItem("Exit");
		menuFile.add(closeItem);

		menuHelp = new JMenu("Help");
		wikiItem = new JMenuItem("RTwiki help");
		wikiItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Properties props = System.getProperties();
					String osname = props.getProperty("os.name");
					if (osname.equals("Linux")) {
						Runtime.getRuntime().exec("firefox " + wikiSite);
					}
					else { // otherwise assume Windows compatibilty
						Runtime.getRuntime().exec("cmd /c start " + wikiSite);
					}
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		});
		menuHelp.add(wikiItem);
		aboutItem = new JMenuItem("About...");
		menuHelp.add(aboutItem);
		final AboutDialog dlg = new AboutDialog(this.rootFrame);

		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dlg.setSize(dlg.getPreferredSize());
				dlg.setLocationRelativeTo(rootFrame);
				dlg.setVisible(true);
				dlg.toFront();
			}
		});

		this.add(menuFile);
		this.add(menuHelp);
		this.setVisible(true);

		// finally, search for root JFrame container and add preferences dialog.
		Frame[] frames = Frame.getFrames();
		for (int i = 0; i < frames.length; i++) {
			if (frames[i] instanceof JFrame) {
				rootFrame = (JFrame) frames[i];
				if (rootFrame.getTitle().startsWith(MainClass.TITLE)) {
					break;
				}
				else {
					rootFrame = null;
				}
			}
		}
	}

	/**
	 * @return
	 */
	public JMenu getFileMenu() {
		return menuFile;
	}

	public Frame getMainFrame() {
		return rootFrame;
	}

	@Override
	public JMenu getHelpMenu() {
		return menuHelp;
	}

	public JMenuItem getExitItem() {
		return closeItem;
	}

	public PreferencesDialog getPreferencesDialog() {

		return preferencesDialog;
	}
}
