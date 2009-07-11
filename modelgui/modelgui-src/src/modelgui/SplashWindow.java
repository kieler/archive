package modelgui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

public class SplashWindow extends Window {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image splashImage;
	private boolean paintCalled = false;
	static SplashWindow me;

	public SplashWindow(Frame owner, Image splashImage) {
		super(owner);
		me = this;
		this.splashImage = splashImage;
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(splashImage, 0);
		try {
			mt.waitForID(0);
		}
		catch (InterruptedException ie) {
		}
		int imgWidth = splashImage.getWidth(this);
		int imgHeight = splashImage.getHeight(this);
		setSize(imgWidth, imgHeight);
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenDim.width - imgWidth) / 2,
				(screenDim.height - imgHeight) / 2);
	}
	
	
	public static void invokeMain(String className, String[] args) {
	    try {
	        Class.forName(className)
	            .getMethod("main", new Class[] {String[].class})
	            .invoke(null, new Object[] {args});
	    } catch (Exception e) {
	        InternalError error =

	            new InternalError("Failed to invoke main method");
	        error.initCause(e);
	        throw error;
	    }
	}

	@Override
	public void update(Graphics g) {
		g.setColor(getForeground());
		paint(g);
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(splashImage, 0, 0, this);
		if (!paintCalled) {
			paintCalled = true;
			synchronized (this) {
				notifyAll();
			}
		}
	}
	
	public static void splash(URL imageURL) {
	    if (imageURL != null) {
	        splash(Toolkit.getDefaultToolkit().createImage(imageURL));
	    }
	}

	public static void splash(Image image) {
	    if (me == null && image != null) {
	        Frame f = new Frame();

	        me = new SplashWindow(f, image);
	        me.setVisible(true);
	        if (! EventQueue.isDispatchThread()
	        && Runtime.getRuntime().availableProcessors() == 1) {
		    	

	            synchronized (me) {
	                while (! me.paintCalled){
	                    try {

	                        me.wait();


	                    } catch (InterruptedException e) {}
	                }
	            }
	        }
	    }
	}
	
	public static void disposeSplash(){
		if (me != null){
			me.setVisible(false);
			me.dispose();
			me = null;
			System.gc();
		}
	}
}
