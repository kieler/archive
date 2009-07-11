package modelgui;

import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.Frame;
import java.io.IOException;
import java.net.URL;

public class Splash {
	
	public static Frame splashFrame = null;
	
	public static void main(String[] args) {
		URL imageURL = Splash.class.getResource("/resources/splashscreen.png");
		if (imageURL != null) {
			//TODO: uncomment this line to show a SplashScreen on startup. At the moment
			//      SplashWindow is buggy since it prevents error dialogs from being focused.
			//SplashWindow.splash(imageURL);
			SplashWindow.invokeMain("modelgui.MainClass", args);
		}
		else {
			System.err.println("Splash image not found");
		}		
	}
}
