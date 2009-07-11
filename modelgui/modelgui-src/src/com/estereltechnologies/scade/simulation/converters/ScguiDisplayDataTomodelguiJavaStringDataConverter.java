package com.estereltechnologies.scade.simulation.converters;

import modelgui.JavaStringData;

/**
 * Class with static methods to convert a SCADE complex type to a Java complex
 * Type. Information is passed between Java and SCADE only with a string.
 * Therefore the Data coming from SCADE is simply the string representation of
 * the whole data.
 * 
 * @author haf
 * 
 */
public class ScguiDisplayDataTomodelguiJavaStringDataConverter {

	/**
	 * Takes a JavaScadeData object and passes its string-representation back to
	 * SCADE. Required Method according to SCADE manual.
	 * 
	 * @param data
	 * @return String that is to be passed to SCADE
	 */
	static public String FrommodelguiJavaStringData(JavaStringData data) {
		return data.toString();
	}

	/**
	 * Takes the String-representation of data from SCADE and creates a new
	 * JavaScadeData object from it. Required Method according to SCADE manual.
	 * 
	 * @param strGuiDisplayData
	 * @return
	 */
	static public JavaStringData TomodelguiJavaStringData(
			String strGuiDisplayData) {
		return new JavaStringData(strGuiDisplayData);
	}

}
