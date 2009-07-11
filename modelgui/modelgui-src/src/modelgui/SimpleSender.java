package modelgui;

/**
 * @author Steffen Jacobs
 * 
 */
public class SimpleSender extends Thread {

	JavaStringData dataToSend;

	/**
	 * @param dataToSend
	 */
	public SimpleSender(JavaStringData dataToSend) {
		this.dataToSend = dataToSend;
		// initialize the data with some example data
		dataToSend.getData().removeAllElements();
		dataToSend.addData(new Integer(0));
		dataToSend.addData(new Float(0));
		dataToSend.addData(new Boolean(false));
		// dataToSend.addData(new String("state0"));
	}

	@Override
	public void run() {
		try {
			if (dataToSend != null) {
				// send some default testing data from time to time
				while (true) {
					for (int i = 0; i < 1000; i++) {
						Thread.sleep(1000); // only update every 1s
						dataToSend.setData(0, new Integer(i));
						dataToSend.setData(1, new Float(0.1 * i));
						dataToSend.setData(2, new Boolean(true));
						// dataToSend.setData(3, new String("state0"));
					}
				}
			}
		}
		catch (Exception e) {
			new ModelguiErrorDialog(e);
		}
	}

}
