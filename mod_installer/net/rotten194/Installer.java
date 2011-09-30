package net.rotten194;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */
public class Installer {
	static boolean config = false;
	static File installDir;
	static final String TARGET_VERSION = "0.01";
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new Gui(Installer.class.getResource("banner.png"), getResourceAsString(".title").replace("\n", ""), getResourceAsString(".desc"), getResourceAsString(".lic"));
	}
	
	private static String getResourceAsString(String resource) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(Installer.class.getResourceAsStream(resource)));
		String line;
		String all = "";
		while ((line = reader.readLine()) != null){
			all += line;
			all += "\n";
		}
		return all;
	}
	
	public static void startInstall(InstallLogger logger, String modName){
		logger.log("Counting files...");
		ArrayList<String> files;
		try{
			files = readIndex();
		} catch (IOException e){
			logger.logError("Could not read index: " + e);
			return;
		}
		logger.setBarRange(0, files.size() + 10);
		logger.log("Copying...");
		int file = 0;
		StringBuilder str = new StringBuilder(modName);
		for (int i = 0; i < str.length(); i++){
			if (!Character.isLetterOrDigit(str.charAt(i))){
				str.setCharAt(i, '_');
			}
		}
		modName = str.toString();
		String dirName = modName;
		File install = new File(installDir, dirName);
		if (install.exists()){
			dirName += "_";
			install = new File(installDir, dirName);
		}
		if (!dirName.equals(modName)){
			logger.logError("You may have an old version of this mod installed, you should remove it. The mod will still be installed with a different name.");
		}
		install.mkdir();
		for (String s : files){
			logger.setBarPoint(file);
			file++;
			try {
				logger.setBarString(s);
				copy(s, install);
			} catch (IOException e){
				logger.logError("Could not copy file " + s + ", " + e);
			}
		}
		logger.log("Copied " + files.size() + " files to " + install.getAbsolutePath());
		logger.setBarPoint(files.size() + 10);
		logger.log("Done! You can safely close this window.");
	}
	
	private static ArrayList<String> readIndex() throws IOException {
		ArrayList<String> files = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(Installer.class.getResourceAsStream("bcbg.index")));
		String line;
		while ((line = reader.readLine()) != null){
			files.add(line);
		}
		return files;
	}
	
	private static void copy(String resource, File destination) throws IOException{
		File copyTo = new File(destination, resource);
		copyTo.createNewFile();
		BufferedInputStream buf = new BufferedInputStream(Installer.class.getResourceAsStream(resource));
		int i;
		FileWriter writer = new FileWriter(copyTo);
		while ((i = buf.read()) != -1){
			writer.write(i);
		}
		writer.close();
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
