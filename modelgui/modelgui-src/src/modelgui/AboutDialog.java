package modelgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class AboutDialog extends JWindow {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8194609102118169642L;

	/**
	 * Creates a new AboutDialog.
	 */
	public AboutDialog() {
		buildGUI();
	}

	/**
	 * Creates a new AboutDialog with a given owner.
	 */
	public AboutDialog(Frame owner) {
		super(owner);
		buildGUI();
	}

	public void setLocationRelativeTo(Frame f) {
		Dimension invokerSize = f.getSize();
		Point loc = f.getLocation();
		Point invokerScreenLocation = new Point(loc.x, loc.y);

		Rectangle bounds = getBounds();
		int dx = invokerScreenLocation.x
				+ ((invokerSize.width - bounds.width) / 2);
		int dy = invokerScreenLocation.y
				+ ((invokerSize.height - bounds.height) / 2);
		Dimension screenSize = getToolkit().getScreenSize();

		if (dy + bounds.height > screenSize.height) {
			dy = screenSize.height - bounds.height;
			dx = invokerScreenLocation.x < (screenSize.width >> 1) ? invokerScreenLocation.x
					+ invokerSize.width
					: invokerScreenLocation.x - bounds.width;
		}
		if (dx + bounds.width > screenSize.width) {
			dx = screenSize.width - bounds.width;
		}

		if (dx < 0)
			dx = 0;
		if (dy < 0)
			dy = 0;
		setLocation(dx, dy);
	}

	/**
	 * Populates this window.
	 */
	protected void buildGUI() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					setVisible(false);
					dispose();
				}
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setVisible(false);
				dispose();
			}
		});
		getContentPane().setBackground(Color.white);

		URL url = getClass().getResource("/resources/splashscreen.png");
		ImageIcon icon = new ImageIcon(url);
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();

		JLayeredPane p = new JLayeredPane();
		p.setSize(w, h);
		getContentPane().add(p);

		JLabel l = new JLabel(icon);
		l.setBounds(0, 0, w, h);
		p.add(l, new Integer(0));

		JLabel l2 = new JLabel("ModelGUI " + Version.getVersionString());
		l2.setForeground(new Color(0, 31, 75, 255));
		l2.setOpaque(false);
		l2.setBackground(new Color(0, 0, 0, 0));
		l2.setHorizontalAlignment(SwingConstants.LEFT);
		l2.setVerticalAlignment(SwingConstants.TOP);
		l2.setBounds(0, 0, 300, 100);
		p.add(l2, new Integer(2));

		((JComponent) getContentPane())
				.setBorder(BorderFactory
						.createCompoundBorder(
								BorderFactory.createBevelBorder(
										BevelBorder.RAISED, Color.gray,
										Color.black),
								BorderFactory
										.createCompoundBorder(
												BorderFactory
														.createCompoundBorder(
																BorderFactory
																		.createEmptyBorder(
																				3,
																				3,
																				3,
																				3),
																BorderFactory
																		.createLineBorder(Color.black)),
												BorderFactory
														.createEmptyBorder(10,
																10, 10, 10))));
	}
}
