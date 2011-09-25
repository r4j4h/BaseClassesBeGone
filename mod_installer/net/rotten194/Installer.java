/**
 * 
 */
package net.rotten194;

import java.io.File;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */
public class Installer {
	static boolean config = false;
	static File installDir;
	static final String TARGET_VERSION = "0.01";
	static String longs = "<html>lol <b>test</b></html><html>lol <b>test</b></html><html>lol <b>test</b></html><html>lol <b>test</b></html><html>lol <b>test</b></html><html>lol <b>test</b></html><html>lol <b>test</b></html>\n\n\n<html>lol <b>test</b>\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\\n\n\n\n\n\n\n\n\n\n\n</html>";
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		Gui g = new Gui(Installer.class.getResource("banner.png"), "TestMod", longs, longs);
	}
	
	public static File getDefaultDirectory(String applicationName) {
		String userHome = System.getProperty("user.home", ".");
		File workingDirectory;
		System.out.println(getPlatform() + "" +  getPlatform().ordinal());
		switch (getPlatform().ordinal()) {
		case 0:
		case 1:
			workingDirectory = new File(userHome, '.' + applicationName + '/');
			break;
		case 2:
			String applicationData = System.getenv("APPDATA");
			if (applicationData != null)
				workingDirectory = new File(applicationData, "." + applicationName + '/'); 
			else
				workingDirectory = new File(userHome, '.' + applicationName + '/');
			break;
		case 3:
			workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
			break;
		default:
			workingDirectory = new File(userHome, applicationName + '/');
		}
		if ((!workingDirectory.exists()) && (!workingDirectory.mkdirs()))
			throw new RuntimeException("The working directory could not be created: " + workingDirectory);
		return workingDirectory;
	}
 
	private static OS getPlatform() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) return OS.windows;
		if (osName.contains("mac")) return OS.macos;
		if (osName.contains("solaris")) return OS.solaris;
		if (osName.contains("sunos")) return OS.solaris;
		if (osName.contains("linux")) return OS.linux;
		if (osName.contains("unix")) return OS.linux;
		return OS.unknown;
	}
	
	public enum OS {
		linux, solaris, windows, macos, unknown;
	}

}
