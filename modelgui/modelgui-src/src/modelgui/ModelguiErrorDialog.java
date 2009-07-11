package modelgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.SplashScreen;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.ImageObserver;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import com.sun.java.swing.plaf.motif.MotifBorders.BevelBorder;

/**
 * @author Steffen Jacobs
 * 
 */
public class ModelguiErrorDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7249263455231733854L;

	
	/**
	 * @param t
	 */
	public ModelguiErrorDialog(Throwable t) {
		show(t, null, null, null);
	}

	/**
	 * @param msg
	 */
	public ModelguiErrorDialog(String msg) {
		show(null, msg, null, null);
	}

	/**
	 * @param t
	 * @param msg
	 */
	public ModelguiErrorDialog(Throwable t, String msg) {
		show(t, msg, null, null);
	}
	
	public ModelguiErrorDialog(Throwable t, String msg, String title){
		show(t, msg, null, title);
	}
	
	public ModelguiErrorDialog(String msg, String detailMsg, String title){
		show(null, msg, detailMsg, title);
	}
	
	private static void show(Throwable t, String msg, String detailMsg, String title) {
		final JButton detailButton = new JButton("More...");
		final JButton okButton = new JButton("OK");
		final JPanel shortMessagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JPanel detailMessagePanel = new JPanel(new BorderLayout());
		final JPanel okPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		final JTextArea textArea = new JTextArea();
		final JScrollPane textPane = new JScrollPane(textArea);; 
		final JPanel panel = new JPanel(new BorderLayout());
		final JDialog dialog = new JDialog(MainClass.getFrame());
		dialog.setResizable(false);
		dialog.setModal(true);
		dialog.setAlwaysOnTop(true);
		textArea.setEditable(false);
		textArea.setFocusable(false);
		textArea.setForeground(Color.BLUE);
		//panel.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		
		textPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		textPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textPane.setVisible(false);
		detailButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				if (textPane.isVisible()){
					detailButton.setText("More...");
					textPane.setVisible(false);
					//System.out.println(shortMessagePanel.getSize().getHeight());
					//System.out.println(okPanel.getSize().getHeight());
					dialog.setSize(dialog.getSize().width, shortMessagePanel.getSize().height+ okPanel.getSize().height + 50);
					dialog.getContentPane().validate();
				}
				else {
					//textPane.setSize((int)dialog.getSize().getWidth(), (int)dialog.getMaximumSize().getHeight());
					detailButton.setText("Less...");
					textPane.setVisible(true);	
					dialog.getContentPane().validate();		
					dialog.pack();
				}
				
			}			
		});
		
		okButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				
				dialog.dispose();
			}
			
		});
		
		if (title != null)
			dialog.setTitle(title);
		else
			dialog.setTitle("Error");
		
		// create short message label
		// wrap labels at 80 characters!
		String tempMsg = "  ";
		String[] splittedMsg = {""};
		if(msg != null) splittedMsg = msg.split(" ");
		int rows = 2;
		shortMessagePanel.add(new JLabel(" ")); // empty label at start to force a margin
		for (String msgWord : splittedMsg) {
			String oldtempMsg = tempMsg;
			tempMsg += msgWord+" ";
			if(tempMsg.length() > 80){
				shortMessagePanel.add(new JLabel(oldtempMsg));
				rows++;
				tempMsg = "  "+msgWord+" ";
			}
		}	
		shortMessagePanel.add(new JLabel(tempMsg));
		shortMessagePanel.setLayout(new GridLayout(rows,1));
		//shortMessagePanel.setAlignmentX(1.5f);
		
				
		if (detailMsg != null)
			textArea.setText(detailMsg + "\n\n");		
		okPanel.add(detailButton);
		okPanel.add(okButton);
		if (t == null && detailMsg == null){
			detailButton.setEnabled(false);
			detailButton.setVisible(false);
		}
		if (t != null) {
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			textArea.append(sw.toString());
			//Logger.getLogger(SVGApplication.LOGGER_NAME).log(Level.SEVERE, msg, t);
		}
		detailMessagePanel.add(BorderLayout.CENTER, textPane);
		detailMessagePanel.validate();
		detailMessagePanel.setMinimumSize(new Dimension(600, 150));
		detailMessagePanel.setMaximumSize(new Dimension(600, 500));
		detailMessagePanel.setPreferredSize(detailMessagePanel.getMinimumSize());
		BorderLayout bl = new BorderLayout();
		bl.setVgap(10);
		panel.setLayout(bl);
		panel.add(BorderLayout.NORTH, shortMessagePanel);
		panel.add(BorderLayout.CENTER, detailMessagePanel);
		panel.add(BorderLayout.SOUTH, okPanel);
		panel.validate();
		//panel.setPreferredSize(dialog.getPreferredSize());
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(BorderLayout.CENTER, panel);
		dialog.getContentPane().validate();
		int shortMessageHeight = shortMessagePanel.getPreferredSize().height;
		int okHeight = okPanel.getPreferredSize().height;
		dialog.pack();
		dialog.setSize(new Dimension(dialog.getSize().width, okHeight + shortMessageHeight + 50));
		

		/*Dimension screensize = dialog.getToolkit().getScreenSize();
		Dimension framesize = dialog.getSize();
		int x = (int) ((screensize.getWidth() - framesize.getWidth()) / 2);
		int y = (int) ((screensize.getHeight() - framesize.getHeight()) / 2);
		dialog.setLocation(x, y);*/
		dialog.setLocationRelativeTo(MainClass.getFrame());
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		dialog.toFront();
		dialog.requestFocus();
		//show(panel, title);
	}
/*
	public static void main(String[] args) {
		Exception e = new Exception("<html>Test<br><br>Test<br><br><br<br><br><br>fwefweffef<br>Test<br><br><br>fwefweffef<br>Test<br><br><br<br><br><br>fwefweffef<br>Test<br><br><br>fwefweffef<br>Test<br><br><br<br><br><br>fwefweffef<br>Test<br><br><br>fwefweffef<br><br>fwefweffef<br>Test<br>Test<br>");
		new ModelguiErrorDialog(e, "test 345g fuewf 789789 8789789 789789789 789789712 9087908098908908908098098098908098908908908908908908908908098908908908908908908098098089089089089080980980 1230´ß0 ß´0´ß0´ß 0´ß0ß´ 0ß´0ß´0´ß 0ß´0´ß0´ß hwu hwf hweu fhweu fhwufh wuef hwufhwuefh u fwueh ue uf hweu h egeg");
		new ModelguiErrorDialog(e, "hallo", "Dies ist ein Test");
		new ModelguiErrorDialog("test");
		new ModelguiErrorDialog(e);
		new ModelguiErrorDialog("Meine Message", "Detailliertere Message", "Titel");
	}
*/	
	
}
