package modelgui;

public class Version {

	private static String mainVersion = "0";
	private static String revisionString = "$Revision: 263 $";

	public static float getVersionNumber() {
		float version = 0.0f;
		if (revisionString.startsWith("$Revision: ")) {
			String temp = mainVersion
					+ "."
					+ revisionString.substring(11, revisionString.length() - 1)
							.trim();
			version = Float.parseFloat(temp);
		}
		return version;
	}

	public static String getVersionString() {
		float version = Version.getVersionNumber();
		String versionString = "v" + Float.toString(version);
		return versionString;

	}
}
