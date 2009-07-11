package modelgui;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Steffen Jacobs
 * 
 */
public class KeyValuePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1560156933635327375L;
	JLabel label;
	JTextField textfield;

	/**
	 * @param labeltext
	 */
	public KeyValuePanel(String labeltext) {
		init(labeltext, "", 20);
	}

	/**
	 * @param labeltext
	 * @param defaulttext
	 * @param columns
	 */
	public KeyValuePanel(String labeltext, String defaulttext, int columns) {
		init(labeltext, defaulttext, columns);
	}

	/**
	 * @param labeltext
	 * @param defaulttext
	 */
	public KeyValuePanel(String labeltext, String defaulttext) {
		init(labeltext, defaulttext, 0);
	}

	private void init(String labeltext, String defaulttext, int columns) {
		this.label = new JLabel(labeltext);
		if (columns != 0)
			this.textfield = new JTextField(defaulttext, columns);
		else
			this.textfield = new JTextField(defaulttext);
		this.setLayout(new FlowLayout(10));
		this.add(label);
		this.add(textfield);
	}

	/**
	 * @return
	 */
	public JLabel getLabel() {
		return label;
	}

	/**
	 * @return
	 */
	public JTextField getTextfield() {
		return textfield;
	}

}
