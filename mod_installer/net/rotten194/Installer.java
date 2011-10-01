package net.rotten194;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
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
		new Gui(Installer.class.getResource("/mod/banner.png"), getResourceAsString("/mod/.title").replace("\n", ""), getResourceAsString("/mod/.desc"), getResourceAsString("/mod/.lic"));
	}
	
	private static String getResourceAsString(String resource) throws IOException{
		InputStream is = Installer.class.getResourceAsStream(resource);
		if (is == null){
			System.err.println(resource + " does not exist!");
			System.exit(1);
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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
		while (install.exists()){
			dirName += "_";
			install = new File(installDir, dirName);
		}
		if (!dirName.equals(modName)){
			logger.logError("You may have an old version of this mod installed, you should remove it (folder " + modName + " already exists). The mod will still be installed, but to " + dirName);
		}
		install.mkdir();
		for (String s : files){
			logger.setBarPoint(file);
			file++;
			try {
				logger.setBarString(s);
				logger.log("copy -> " + s);
				copy("/mod/" + s, s, install, logger);
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
		InputStream is = Installer.class.getResourceAsStream("/mod/bcbg.index");
		if (is == null){
			System.err.println("bcbg.index does not exist!!");
			System.exit(1);
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = reader.readLine()) != null){
			files.add(line.replace("\n", ""));
		}
		return files;
	}
	
	private static void copy(String resource, String create, File destination, InstallLogger logger) throws IOException{
		File copyTo = new File(destination, create);
		InputStream is = Installer.class.getResourceAsStream(resource);	
		if (is == null){
			logger.logError(resource + " does not exist");
			return;
		}
		long length = Files.copy(is, copyTo.toPath());
		logger.log("	Copied " + getSizeString(length));
	}
	
	private static String getSizeString(long size){
		if (size < 1024){
			return size + " byte(s)";
		}
		else if (size < 1024 * 1024){
			return size/1024 + " kilobyte(s)";
		}
		else if (size < 1024 * 1024 * 1024){
			return size/(1024*1024) + " megabyte(s)";
		}
		else if (size < 1024 * 1024 * 1024 * 1024){
			return size/(1024*1024*1024) + " gigabyte(s)";
		}
		return (size/1024 * 1024 * 1024 * 1024 * 1024) + " terrabyte(s). Seriously, this file worries me somewhat. Why are you installing a mod > 1 terrabyte? Reevaluate your life.";
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
