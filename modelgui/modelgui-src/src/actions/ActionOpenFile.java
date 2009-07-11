package actions;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import modelgui.ExampleFileFilter;
import modelgui.ModelguiErrorDialog;

import org.apache.batik.swing.JSVGCanvas;

/**
 * @author Steffen Jacobs
 * 
 */
public class ActionOpenFile extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6950966612206549415L;

	JSVGCanvas canvas;

	Container panel;
	String lastdir = ".";

	File file;

	/*
	 * public ActionOpenFile(JSVGScrollPane panel) { super(); try { file = new
	 * URI("localhost"); } catch (URISyntaxException e) { // TODO Auto-generated
	 * catch block //e.printStackTrace(); } this.panel = panel; // TODO
	 * Auto-generated constructor stub }
	 */

	/**
	 * @param name
	 * @param panel
	 */
	public ActionOpenFile(String name, Container panel) {
		super(name);
		this.panel = panel;
		for (int i = 0; i < panel.getComponentCount(); i++) {
			String compName = panel.getComponent(i).getClass().getName();
			if (compName.equals("org.apache.batik.swing.JSVGCanvas")) {
				canvas = (JSVGCanvas) panel.getComponent(i);
				break;
			}
		}
		try {
			if (canvas == null)
				throw new NullPointerException(
						"No JSVGCanvas component found! This should never occur!!!");
		}
		catch (NullPointerException e) {
			new ModelguiErrorDialog(e);
		}
	}

	/**
	 * @param name
	 * @param icon
	 * @param panel
	 */
	public ActionOpenFile(String name, Icon icon, Container panel) {
		super(name, icon);
		this.panel = panel;
		for (int i = 0; i < panel.getComponentCount(); i++) {
			if (panel.getComponent(i) instanceof JSVGCanvas) {
				canvas = (JSVGCanvas) panel.getComponent(i);
				break;
			}
		}
		try {
			if (canvas == null)
				throw new NullPointerException(
						"No JSVGCanvas component found! This should never occur!!!");
		}
		catch (NullPointerException e) {
			new ModelguiErrorDialog(e);
		}
	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser(lastdir);
		ExampleFileFilter filter = new ExampleFileFilter();
		filter.addExtension("svg");
		filter.setDescription("Scalable Vector Graphics");
		fc.setFileFilter(filter);
		int choice = fc.showOpenDialog(panel);
		if (choice == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			canvas.setURI(file.toURI().toString());
			lastdir = fc.getSelectedFile().getPath();
		}
	}

	/**
	 * @return file selected in JFileChooser
	 */
	public File getFile() {
		return file;
	}

	public URI getURI() {
		return file.toURI();
	}

	public void setFile(File file) {
		this.file = file;
	}
	

}
