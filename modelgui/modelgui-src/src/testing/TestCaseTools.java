package testing;

import java.io.File;

import modelgui.Tools;
import junit.framework.TestCase;

/**
 * @author Steffen Jacobs
 * 
 */
public class TestCaseTools extends TestCase {

	/**
	 * 
	 */
	public void testIsValidXml() {
		File xml, xsd;
		try {
			xml = new File(
					"file://home/haf/shared/modelgui/trunk/examples/elevator2.map");
			xsd = new File(ClassLoader.getSystemResource("config/mapping.xsd")
					.getPath());

			Tools.isValidXml(xml, xsd);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

}
