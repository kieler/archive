package modelgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;
import org.apache.batik.util.gui.resource.JToolbarButton;
import org.apache.batik.util.gui.resource.JToolbarSeparator;
//import org.apache.batik.util.gui.resource.JToolbarSeparator;

import events.SimulationEvent;

/**
 * @author Steffen Jacobs
 * 
 */
public class ModelguiToolBar extends JToolBar implements Observer, SimulationListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8046777442567543543L;

	public static final int DEFAULT_PORT = 1234;

	/**
	 * remove suffix "-32" to get small icons (16x16)
	 */
	public static final String ICON_RESOURCE_OPENFILE = "/resources/graphics/fileopen-32.png";
	public static final String ICON_RESOURCE_STOP = "/resources/graphics/media-playback-stop-32.png";
	public static final String ICON_RESOURCE_PAUSE = "/resources/graphics/media-playback-pause-32.png";
	public static final String ICON_RESOURCE_START = "/resources/graphics/media-playback-start-32.png";
	public static final String ICON_RESOURCE_STEP = "/resources/graphics/media-skip-forward-32.png";
	public static final String ICON_RESOURCE_EXPORT = "/resources/graphics/filesaveas-32.png";
	public static final String ICON_RESOURCE_IMPORTANT = "/resources/graphics/emblem-important.png";
	public static final String ICON_RESOURCE_REFRESH = "/resources/graphics/reload.png";
	JButton startButton;
	JToolbarButton button0;
	JToolbarButton button1;
	JToolbarButton button2;
	JToolbarButton button3;
	JToolbarButton button4;
	JToolbarButton button5;
	JTextField portTextField;
	JTextField delayText;
	JPanel textPanel;
	int port = DEFAULT_PORT;
	private boolean paused = true;
	JLabel icon;
	private int delay = 50;
	ExportManager exportManager;
	Component parent;
	ModelguiToolBar me;

	/**
	 * 
	 */
	public ModelguiToolBar(Component parent) {
		super();
		me = this;
		this.parent = parent;
		URL urlButton0 = getClass().getResource(ICON_RESOURCE_OPENFILE);
		URL urlButton1 = getClass().getResource(ICON_RESOURCE_STOP);
		URL urlButton2 = getClass().getResource(ICON_RESOURCE_START);
		URL urlButton3 = getClass().getResource(ICON_RESOURCE_STEP);
		URL urlButton4 = getClass().getResource(ICON_RESOURCE_EXPORT);
		URL urlButton5 = getClass().getResource(ICON_RESOURCE_REFRESH);
		
		
		button0 = new JToolbarButton();
		if (urlButton0 != null)
			button0.setIcon(new ImageIcon(urlButton0));
		button1 = new JToolbarButton();
		if (urlButton1 != null)
			button1.setIcon(new ImageIcon(urlButton1));
		button2 = new JToolbarButton();
		if (urlButton2 != null)
			button2.setIcon(new ImageIcon(urlButton2));
		button3 = new JToolbarButton();
		if (urlButton3 != null)
			button3.setIcon(new ImageIcon(urlButton3));
		button4 = new JToolbarButton();
		if (urlButton4 != null)
			button4.setIcon(new ImageIcon(urlButton4));
		button5 = new JToolbarButton();
		if (urlButton5 != null)
			button5.setIcon(new ImageIcon(urlButton5));
		final Component finalParent = parent;
		
		button4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (exportManager != null) {
					exportManager.showSaveDialog(finalParent);
				}
			}
		});

		delayText = new JTextField(Integer.toString(delay), 5);
		JLabel delayTextLabel = new JLabel("Delay simulation (ms)");
		icon = new JLabel(new ImageIcon(getClass().getResource(
				ICON_RESOURCE_IMPORTANT)));
		// the delay label should only be visible when changing the delay value
		icon.setVisible(false);
		this.add(button0);
		button0.setToolTipText("Open SVG");
		button4.setEnabled(false);
		this.add(button4);
		button4.setToolTipText("SVG Export to file");
		this.add(button5);
		button5.setToolTipText("Reload SVG");
		JToolbarSeparator sep = new JToolbarSeparator();
		sep.setPreferredSize(new Dimension(10,40));
		this.add(sep);
		this.add(button1);
		button1.setToolTipText("Stop simulation");
		this.add(button2);
		button2.setToolTipText("Start/Pause simulation");
		this.add(button3);
		button3.setToolTipText("Step");
		delayText.setToolTipText("Delay time in ms");
		JPanel panel = buildTextPanel(delayText, delayTextLabel, icon);
		panel.setBackground(new Color(0, 0, 0, 0.5f));
		this.add(panel);
		this.setVisible(true);
		this.setFloatable(false);
		this.setEnableControls(false);
		this.getPlayButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				me.setPaused(!me.isPaused());			
			}
		});
	}

	public JPanel buildTextPanel(JTextField tfield, JLabel label, JLabel icon) {
		JPanel mainpanel = new JPanel(new BorderLayout());
		mainpanel.setOpaque(false);
		JPanel toppanel = new JPanel(new BorderLayout());
		toppanel.setOpaque(false);
		JPanel bottompanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		bottompanel.setOpaque(false);
		mainpanel.add("North", toppanel);
		mainpanel.add("South", bottompanel);
		bottompanel.add(tfield);
		bottompanel.add(icon);
		// mainpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(new
		// Color(255,255,255), new Color(133,154,174)), "Delay simulation",
		// TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, this.getFont(),new
		// Color(163,184,204)));
		mainpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED, new Color(255, 255,
						255), new Color(133, 154, 174)), "Delay simulation"));
		mainpanel.setPreferredSize(new Dimension(160, 50));
		return mainpanel;
	}

	/**
	 * @return
	 */
	public JTextField getDelayTextField() {
		return this.delayText;
	}

	/**
	 * @param t
	 */
	public void setDelayTextFieldText(String t) {
		this.delayText.setText(t);
	}

	/**
	 * @return
	 */
	public String getDelayTextFieldText() {
		return delayText.getText();
	}

	/**
	 * @return
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * @param value
	 */
	private void setPaused(boolean value) {
		if (value == false) {
			URL icon = getClass().getResource(ICON_RESOURCE_PAUSE);
			this.getPlayButton().setIcon(new ImageIcon(icon));
			this.getStepForwardButton().setEnabled(false);
			this.getDelayTextField().setEnabled(false);
		}
		else {
			URL icon = getClass().getResource(ICON_RESOURCE_START);
			this.getPlayButton().setIcon(new ImageIcon(icon));
			this.getStepForwardButton().setEnabled(true);
			this.getDelayTextField().setEnabled(true);
		}
		paused = value;
	}
	
	/**
	 * @param orientation
	 */
	public ModelguiToolBar(int orientation) {
		super(orientation);
	}

	/**
	 * @param name
	 */
	public ModelguiToolBar(String name) {
		super(name);
	}

	/**
	 * @param name
	 * @param orientation
	 */
	public ModelguiToolBar(String name, int orientation) {
		super(name, orientation);
	}

	/**
	 * @param flag
	 */
	public void setEnableControls(boolean flag) {
		this.getPlayButton().setEnabled(flag);
		this.getStopButton().setEnabled(flag);
		this.getStepForwardButton().setEnabled(flag);
		// this.getDelayTextField().setEnabled(flag);
	}

	/**
	 * @return
	 */
	public JToolbarButton getOpenFileButton() {
		return this.button0;
	}

	/**
	 * @return
	 */
	public JToolbarButton getStopButton() {
		return this.button1;
	}

	/**
	 * @return
	 */
	public JToolbarButton getPlayButton() {
		return this.button2;
	}

	public JToolbarButton getReloadButton() {
		return this.button5;
	}
	
	/**
	 * @return
	 */
	public JToolbarButton getStepForwardButton() {
		return this.button3;
	}

	/**
	 * @return
	 */
	public JTextField getPortTextField() {
		return portTextField;
	}

	/**
	 * @return
	 */
	public JPanel getPanel() {
		return textPanel;
	}

	/**
	 * @param port
	 */
	public void setPort(int port) {
		// this.portTextField.setText(Integer.toString(port));
		this.port = port;
	}

	/**
	 * 
	 */
	public void enableConnect() {
		startButton.setText("Start");
		portTextField.setEnabled(true);
	}

	/**
	 * @param d
	 */
	public void setDelay(int d) {
		delay = d;
		this.getDelayTextField().setText(Integer.toString(d));
	}

	/**
	 * @return
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * 
	 */
	public void disableConnect() {
		startButton.setText("Stop");
		portTextField.setEnabled(false);
	}

	/*
	 * public int getPort() { try { int tempPort =
	 * Integer.parseInt(this.portTextField.getText()); if (tempPort <= 65000 &&
	 * tempPort >= 1){ this.setPort(tempPort); return tempPort; } else if
	 * (tempPort > 65000){ System.out.println("Error: Port value out of range: " +
	 * tempPort); this.setPort(port); return port; } }
	 * catch(NumberFormatException e){ //nothing }
	 * this.portTextField.setText(Integer.toString(port)); this.setPort(port);
	 * return port; }
	 */

	public void update(Observable o, Object arg) {
		if (o instanceof SVGApplication && arg instanceof String) {
			String msg = (String) arg;
			SVGApplication app = (SVGApplication) o;
			if (msg.equals("rendering done")) {
				if (button4 != null)
					exportManager = new ExportManager(app);
				this.button4.setEnabled(true);
				this.setEnableControls(true);
			}
		}
		if (arg instanceof String && o instanceof ControlAnimationServer) {
			String msg = (String) arg;
			if (msg.equals("timeout")) {
				// this.enableConnect();
			}
			if (msg.startsWith("delay ")) {
				msg = msg.substring(6);
				try {
					int delay = Integer.parseInt(msg);
					this.setDelay(delay);
				}
				catch (NumberFormatException err) {

				}
			}
		}
		if (arg instanceof Integer) {
			this.setPort(((Integer) arg).intValue());
		}
	}

	public void simulationStarted(SimulationEvent evt) {
		this.setPaused(false);
		
	}

	public void simulationStepped(SimulationEvent evt) {
				
	}

	public void simulationStopped(SimulationEvent evt) {
		this.setPaused(true);
	}
}
