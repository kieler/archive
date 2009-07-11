package modelgui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class PreferencesDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 207540926397179737L;

	public static final String FILE_SEPARATOR = System
			.getProperty("file.separator");
	public static final String DEFAULT_FILENAME = "modelgui.preferences";
	public static final String TEMP_DIRECTORY = System
			.getProperty("java.io.tmpdir");
	public static final String USER_HOME = System.getProperty("user.home");
	public static final String USER_NAME = System.getProperty("user.name");
	public static final String USER_DIR = System.getProperty("user.dir");
	public static final String DEFAULT_FILE = USER_DIR + FILE_SEPARATOR
			+ DEFAULT_FILENAME;

	public static final String XMLVALIDATION_PREF_NAME = "xml-validation";
	public static final String PORT_PREF_NAME = "port";
	public static final String HOST_PREF_NAME = "host";
	public static final String INPUT_PREF_NAME = "input-path";
	public static final String OUTPUT_PREF_NAME = "output-path";
	public static final String DEFAULT_PREF_NAME = "default-svg";
	public static final String SCADE_PREF_NAME = "scade-mode";
	public static final String SIMULINK_PREF_NAME = "simulink-mode";
	
	private JRadioButton xmlValidation;
	private JRadioButton showElementInformation;
	private JRadioButton scadeButton;
	private JRadioButton simulinkButton;
	private JTextField clientPort;
	private JTextField host;
	private JTextField inputPath;
	private JTextField outputPath;
	private JTextField defaultSvg;
	private JButton helpButton;
	private JButton okButton;
	private JButton cancelButton;
	private JButton applyButton;
	private JButton loadDefaultsButton;
	private JPanel regularPanel;
	private JPanel debuggingPanel;
	private JPanel buttonPanel;
	private URI file;
	private Hashtable<String, JComponent> components = new Hashtable<String, JComponent>();
	private Properties properties = new Properties();
	private boolean hasChanged = false;
	private SVGApplication svgApp;

	public PreferencesDialog(SVGApplication app) {		
		super(MainClass.getFrame(), "Preferences");
		this.svgApp = app;
		this.setLayout(new BorderLayout());
		// this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		this.setModal(true);
		// instantiate buttons and panels
		debuggingPanel = new JPanel(new GridLayout(0, 1));
		debuggingPanel.setBorder(BorderFactory.createTitledBorder("Debugging"));
		regularPanel = new JPanel(new GridLayout(0, 1));
		regularPanel.setBorder(BorderFactory.createTitledBorder("Common"));
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		applyButton = new JButton("Apply");
		
		helpButton = new JButton("Help");
		helpButton.setToolTipText("See tooltips for help!");
		
		loadDefaultsButton = new JButton("Load Defaults");
		loadDefaultsButton.setToolTipText("Sets all preferences to default values. Overrides your current settings!");

		// instantiate properties components
		xmlValidation = new JRadioButton("Enable XML validation (recommended)",
				true);
		// new components MUST have a name!
		xmlValidation.setName(XMLVALIDATION_PREF_NAME);
		xmlValidation.setToolTipText("If checked the mapping file (<svgfilenamebasename>.map) will be validated when loaded.");
		// showElementInformation = new JRadioButton("Render additional SVG
		// information", false);
		// showElementInformation.setName("render-additional-info");

		host = new JTextField();
		host.setToolTipText("Symbolic Hostname or IP Address of the communication partner that sends and receives data.");
		host.setName(HOST_PREF_NAME);

		clientPort = new JTextField(5);
		clientPort.setToolTipText("TCP Port number of the communication partner that sends and receives data.");
		clientPort.setName(PORT_PREF_NAME);
		
		// add preference components
		this.addPreference(xmlValidation, true);
		// this.addPreference(showElementInformation, true);
		this.addPreference("Hostname", host, false);
		this.addPreference("Port", clientPort, false);

		// SCADE needs paths to identify inputs and outputs to be used with the interface
		inputPath = new JTextField();
		inputPath.setName(INPUT_PREF_NAME);
		inputPath.setToolTipText("Information for the Connection Interface: Input Path of the incoming data");
		this.addPreference("Input Array Path", inputPath, false);
		
		outputPath = new JTextField();
		outputPath.setName(OUTPUT_PREF_NAME);
		outputPath.setToolTipText("Information for the Connection Interface: Output Path of the outgoing data");
		this.addPreference("Output Array Path", outputPath, false);
		
		defaultSvg = new JTextField();
		defaultSvg.setName(DEFAULT_PREF_NAME);
		defaultSvg.setToolTipText("Default SVG file that will be loaded at startup, if set. Leave blank for standard logo. Enter in URL syntax, e.g. file:///home/me/mysvg.svg");
		this.addPreference("Default SVG URL", defaultSvg, false);
		
		// SCADE or Simulink? (in Simulink we are a server, for SCADE, SCADE is
		// the server!)
		
		scadeButton = new JRadioButton("Esterel SCADE");
		scadeButton.setName(SCADE_PREF_NAME);
		simulinkButton = new JRadioButton("Matlab / Simulink");
		simulinkButton.setName(SIMULINK_PREF_NAME);
		
		//clientMode = new JRadioButton(
		//		"Client Mode (SCADE, selected), Server Mode (Simulink, not selected)");
		
		//clientMode.setSelected(true);
		ButtonGroup buttonGroup = new ButtonGroup();
		//buttonGroup.setSelected(, b)
		buttonGroup.add(scadeButton);
		buttonGroup.add(simulinkButton);
		this.addPreference(scadeButton, false);
		this.addPreference(simulinkButton, false);

		// add buttons and panels
		buttonPanel.add(okButton);
		buttonPanel.add(applyButton);
		buttonPanel.add(loadDefaultsButton);
		buttonPanel.add(helpButton);
		buttonPanel.add(cancelButton);
		this.add(debuggingPanel, BorderLayout.NORTH);
		this.add(regularPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);

		Enumeration<JComponent> elements = components.elements();
		while (elements.hasMoreElements()) {
			JComponent current = elements.nextElement();
			if (current instanceof AbstractButton) {
				final AbstractButton ab = (AbstractButton) current;
				ab.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						hasChanged = true;
						
					}

				});
			}
			else if (current instanceof JTextComponent) {
				final JTextComponent tc = (JTextComponent) current;
				tc.addFocusListener(new FocusListener() {
					String value = "";

					public void focusGained(FocusEvent e) {
						value = tc.getText();
					}

					public void focusLost(FocusEvent e) {
						if (!tc.getText().equals(value)) {
							hasChanged = true;
						}
					}

				});
			}
		}

		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (hasChanged)
					savePreferences();
				hasChanged = false;
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dispose();
				hasChanged = false;
			}
		});

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (hasChanged) {
					savePreferences();
					svgApp.restart();
				}
				dispose();
				hasChanged = false;

			}
		});
		
		loadDefaultsButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				loadPreferencesDefaults();
			}
		}
		);
		
		helpButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				new ModelguiErrorDialog(null,"In the preferences dialog see tooltips for help! To see a tooltip, hover with the mouse pointer a few seconds over the corresponding item. A little text should pop up and give some explanations about the item.");
			}
		}
		);
		
		
		this.loadPreferences();
		this.pack();
	}

	private void loadFromFile(InputStream file) throws FileNotFoundException {
		try {
			properties.loadFromXML(file);
			Enumeration<Object> componentNames = properties.keys();
			while (componentNames.hasMoreElements()) {
				String currentName = (String) componentNames.nextElement();
				Component aComponent = this.getPreference(currentName);
				if (aComponent == null) {
					properties.remove(currentName);
					
					new ModelguiErrorDialog(new Exception(), "The property '" + currentName
							+ "' does not match to a graphical component!");
					break;
				}
				if (aComponent instanceof AbstractButton) {
					AbstractButton button = (AbstractButton) aComponent;
					// System.out.println(properties.getProperty(currentName));
					button.setSelected(Boolean.parseBoolean(properties
							.getProperty(currentName)));
				}
				else if (aComponent instanceof JTextComponent) {
					JTextComponent textComponent = (JTextComponent) aComponent;
					textComponent.setText(properties.getProperty(currentName));
				}
			}

		}
		catch (InvalidPropertiesFormatException e) {
			new ModelguiErrorDialog(
					"Could not read preferences! File '"
							+ file
							+ "' seems to be invalid! Try to delete it manually and relaunch appplication!");
		}
		catch (IOException e) {
			new ModelguiErrorDialog("Some error occured while reading '" + file
					+ "'!");
		}
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener){
		Enumeration<Object> componentNames = properties.keys();
		while (componentNames.hasMoreElements()) {
			String currentName = (String) componentNames.nextElement();
			Component aComponent = this.getPreference(currentName);
			aComponent.addPropertyChangeListener(listener);
		}
	}

	public void loadPreferences() {
		// look for preference file in current user directory
		File f = new File(PreferencesDialog.DEFAULT_FILE);
		try {
			loadFromFile(new FileInputStream(f));
			//System.out.println("Preferences loaded from file '" + file +
			 //"'!");
		}
		catch (FileNotFoundException e) {
			f = new File(PreferencesDialog.TEMP_DIRECTORY + FILE_SEPARATOR
					+ DEFAULT_FILENAME);
			// file = URI.create(PreferencesDialog.TEMP_DIRECTORY +
			// FILE_SEPARATOR + DEFAULT_FILENAME);
			try {
				loadFromFile(new FileInputStream(f));
				// System.out.println("Preferences loaded from file '" + file +
				// "'!");
			}
			catch (FileNotFoundException e1) {
				loadPreferencesDefaults();
			}
		}
	}

	private void loadPreferencesDefaults() {
		try {
			// load default values
			URL defaultfile = getClass().getResource(
					"/resources/config/modelgui.preferences");
			loadFromFile(defaultfile.openStream());
			JOptionPane
					.showMessageDialog(this.getOwner(),
							"Default preferences loaded!");
			// save default preferences file;
			savePreferences();
		}
		catch (Exception e2) {
			new ModelguiErrorDialog(e2,
					"Could not load preferences file!");
			// create an empty preferences file;
			savePreferences();
		}
	}

	private void saveToFile(URI file) throws Exception {
		FileOutputStream fo = new FileOutputStream(file.getPath());

		Enumeration<String> comps = components.keys();
		while (comps.hasMoreElements()) {
			String currentName = comps.nextElement();
			JComponent c = components.get(currentName);
			if (c instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) c;
				properties.setProperty(button.getName(), Boolean
						.toString(button.isSelected()));

			}
			else if (c instanceof JTextComponent) {
				JTextComponent tc = (JTextComponent) c;
				properties.setProperty(tc.getName(), tc.getText());
			}
		}
		properties.storeToXML(fo, "Preferences for ModelGUI");
		fo.close();
	}

	public void savePreferences() {
		File f = new File(PreferencesDialog.DEFAULT_FILE);
		file = f.toURI();
		// file = URI.create(PreferencesDialog.DEFAULT_FILE);
		try {
			saveToFile(file);
			// System.out.println("Preferences saved to file '" + file + "'!");
		}
		catch (Exception e) {
			f = new File(PreferencesDialog.TEMP_DIRECTORY + FILE_SEPARATOR
					+ DEFAULT_FILENAME);
			file = f.toURI();
			// file = URI.create(PreferencesDialog.TEMP_DIRECTORY +
			// FILE_SEPARATOR + DEFAULT_FILENAME);
			try {
				saveToFile(file);
				// System.out.println("Preferences saved to file '" + file +
				// "'!");
			}
			catch (Exception e1) {
				new ModelguiErrorDialog("Could not save preferences file to "
						+ file + "'!");
			}
		}
	}

	/**
	 * Adds a new component to the preferences dialog. A component can either be
	 * added to the debugging panel or the regular panel. Using this method to
	 * add components, any layout relating stuff or saving/loading issue should
	 * be done automatically by this class
	 * 
	 * @param c
	 *            component to be added as a graphical component
	 * @param debug
	 *            specifies whether the component is only a preference for
	 *            debugging purposes or a regular one
	 * @see
	 */
	public void addPreference(JComponent c, boolean debug) {
		if (!components.contains(c)) {
			if (c.getName() != null) {
				if (debug) {
					c.setSize(this.getDebuggingPanel().getWidth(), c
							.getHeight());
					debuggingPanel.add(c);
					components.put(c.getName(), c);
				}
				else {
					regularPanel.add(c);
					components.put(c.getName(), c);
				}
			}
			else {
				new ModelguiErrorDialog(
						"An instance of class '"
								+ c.getClass().getName()
								+ "' does not have a name! Use setName(String name) to solve this problem!");
			}
		}
	}

	public void addPreference(String label, JComponent c, boolean debug) {
		if (!components.contains(c)) {
			if (c.getName() != null) {
				if (debug) {
					c.setSize(this.getDebuggingPanel().getWidth(), c
							.getHeight());
					JLabel lbl = new JLabel(label);
					debuggingPanel.add(lbl);
					lbl.setLabelFor(c);
					debuggingPanel.add(c);
					components.put(c.getName(), c);
				}
				else {
					JLabel lbl = new JLabel(label);
					regularPanel.add(lbl);
					lbl.setLabelFor(c);
					regularPanel.add(c);
					components.put(c.getName(), c);
				}
			}
			else {
				new ModelguiErrorDialog(
						"An instance of class '"
								+ c.getClass().getName()
								+ "' does not have a name! Use setName(String name) to solve this problem!");
			}
		}
	}

	public JPanel getRegularPanel() {
		return regularPanel;
	}

	public JPanel getDebuggingPanel() {
		return debuggingPanel;
	}

	public JRadioButton getElementInformationButton() {
		return this.showElementInformation;
	}

	public JButton getApplyButton() {
		return this.applyButton;
	}

	public JButton getOkButton() {
		return this.okButton;
	}

	public JButton getCancelButton() {
		return this.cancelButton;
	}

	public JComponent getPreference(String name) {
		return components.get(name);
	}

	public Object getPreferenceValue(String name) {
		JComponent c = components.get(name);
		if (c instanceof AbstractButton) {
			return ((AbstractButton) c).isSelected();
		}
		else if (c instanceof JTextComponent) {
			return ((JTextComponent) c).getText();
		}
		else
			return null;
	}

	public void setPreference(String name, Object value) {
		JComponent comp = getPreference(name);
		if (comp instanceof JTextComponent) {
			((JTextComponent) comp).setText(value.toString());
		}
		else if (comp instanceof AbstractButton) {
			((AbstractButton) comp).setSelected(Boolean.getBoolean(value
					.toString()));
		}
		else {
			new ModelguiErrorDialog("No such preference object available!");
			return;
		}
		properties.setProperty(name, value.toString());
	}

	public Enumeration<String> getPreferencesNames() {
		return components.keys();
	}
}
