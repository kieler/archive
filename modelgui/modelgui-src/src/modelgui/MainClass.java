package modelgui;

import javax.swing.JFrame;

/**
 * @author Steffen Jacobs
 * 
 */
public class MainClass {

	/**
	 * @param args
	 */

	public static final String TITLE = "ModelGUI";
	private static boolean java_version_ok = false;
	private static SVGApplication app = null;
	static JFrame f = null;

	public static void main(String[] args) {
		java_version_ok = true;
		//TODO: check java version
		if (java_version_ok) {
			f = getFrame();
			if (app == null)
				app = new SVGApplication(f);
		}
	}
	
	/*public static SVGApplication getApplication(){
		if (app == null){
			app = new SVGApplication(getFrame());
		}
		return app;
	}*/
	
	public static JFrame getFrame(){
		if (f == null)
			f = new JFrame(TITLE);
		return f;
	}
}
