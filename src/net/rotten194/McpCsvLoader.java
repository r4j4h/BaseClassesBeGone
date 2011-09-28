package net.rotten194;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import static net.rotten194.Constants.*;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */

public class McpCsvLoader {
	/*
	 * A utilities class for loading the 3 MCP .csv files (classes.csv, methods.csv, and fields.csv)
	 */
	static HashMap<String/*notch name*/, Object[]/*MCP name*/> classes = new HashMap<String, Object[]>(); //holy balls
	static HashMap<String, Object[]> methods = new HashMap<String, Object[]>(); //all together these will take
	static HashMap<String, Object[]> fields = new HashMap<String, Object[]>(5000); //~100 mb of memory
	
	public static String getClassNameFromMCP(String mcpName, String _package){
		mcpName = unStub(mcpName);
		Object[] o = classes.get(getClassHash(mcpName, _package, CLIENT));
		if (o != null)
			return (String)o[1];
		return null;
	}
	
	public static String getMethodNameFromMCP(String mcpName, String sig, String mcpClass){
		mcpClass = unStub(mcpClass);
		Object[] o = methods.get(getMethodHash(mcpName, sig, mcpClass, CLIENT));
		if (o != null)
			return (String)o[2];
		return null;
	}
	
	public static String getMethodNotchSigFromMCP(String mcpName, String sig, String mcpClass){
		mcpClass = unStub(mcpClass);
		Object[]o = methods.get(getMethodHash(mcpName, sig, mcpClass, CLIENT));
		if (o != null)
			return (String)o[4];
		return null;
	}
	
	public static String getFieldNameFromMCP(String mcpName, String mcpClass){
		mcpClass = unStub(mcpClass);
		Object[] o = fields.get(getFieldHash(mcpName, mcpClass, CLIENT));
		if (o != null)
			return (String)o[2];
		return null;
	}
	
	public static String getFieldName(String mcpName, String mcpClass){
		mcpClass = unStub(mcpClass);
		String s = getFieldNameFromMCP(mcpName, mcpClass);
		if (s == null) s = mcpName;
		return s;
	}
	
	public static String getMethodName(String mcpName, String sig, String mcpClass){
		mcpClass = unStub(mcpClass);
		String s = getMethodNameFromMCP(mcpName, sig, mcpClass);
		if (s == null) s = mcpName;
		return s;
	}
	
	public static String getMethodNotchSig(String mcpName, String mcpSig, String mcpClass){
		mcpClass = unStub(mcpClass);
		String s = getMethodNotchSig(mcpName, mcpSig, mcpClass);
		if (s == null) s = mcpSig;
		return s;
	}
	
	public static String getClassName(String mcpName, String _package){
		mcpName = unStub(mcpName);
		String s = getClassNameFromMCP(mcpName, _package);
		if (s == null) s = mcpName;
		return s;
	}
	
	public static String getClassName(String mcpName){
		mcpName = unStub(mcpName);
		String s = getClassNameFromMCP(mcpName, NET_MINECRAFT_SRC);
		if (s != null) return s;
		s = getClassNameFromMCP(mcpName, NET_MINECRAFT_CLIENT);
		if (s != null) return s;
		s = getClassNameFromMCP(mcpName, NET_MINECRAFT_SERVER);
		if (s != null) return s;
		return mcpName;
	}
	
	private static String unStub(String className){
		if (className.endsWith("_Stub")){
			return className.substring(0, className.length() - 5);
		}
		return className;
	}
	
	public static void loadCsvs(File dir){
		if (!dir.isDirectory())
			throw new IllegalArgumentException("loadCsvs requires a directory, " + dir + " is not one.");
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				System.out.println("filtering " + file + " " + name);
				return (matches(name));
			}
		});
		//TODO This doesn't seem to be loading the entire files, buffer issue? idk
		for (File f : files){
			if (f.getName().equals("classes.csv"))
				parseClassesCsv(f);
			else if (f.getName().equals("methods.csv"))
				parseMethodsCsv(f);
			else if (f.getName().equals("fields.csv"))
				parseFieldsCsv(f);
		}
	}
	
	public static void parseClassesCsv(File f){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line = "";
			reader.readLine(); //eat header
			while((line = reader.readLine()) != null){
				String[] temp = parseLine(line);
				String name = temp[0];
				String _package = temp[3];
				classes.put(getClassHash(name, _package, Integer.parseInt(temp[4])), temp);
			}
		} catch (FileNotFoundException e) {
			System.err.println("MCP .csvs gone?! Not on my watch!");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fail: fail! Oh no!");
			e.printStackTrace();
		}
		System.out.println("c" + classes.entrySet().size());
	}
	
	public static void parseMethodsCsv(File f){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line = "";
			reader.readLine(); //eat header
			while((line = reader.readLine()) != null){
				String[] temp = parseLine(line);
				String name = temp[1];
				String sig = temp[4];
				String clazz = temp[5];
				if (name.equals("func_35356_c")) System.out.println("Adding func_35356_c " + Arrays.toString(temp));
				methods.put(getMethodHash(name, sig, clazz, Integer.parseInt(temp[8])) , temp);
			}
		} catch (FileNotFoundException e) {
			System.err.println("MCP .csvs gone?! Not on my watch!");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fail: fail! Oh no!");
			e.printStackTrace();
		}
		System.out.println("m" + methods.entrySet().size());
	}
	
	public static void parseFieldsCsv(File f){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line = "";
			reader.readLine(); //eat header
			while((line = reader.readLine()) != null){
				String[] temp = parseLine(line);
				String name = temp[1];
				String clazz = temp[5];
				String hash = getFieldHash(name, clazz, Integer.parseInt(temp[8]));
				if (fields.get(hash) != null) System.err.println(Arrays.toString(fields.get(hash)) + "  " + Arrays.toString(temp));
				fields.put(hash, temp);
			}
		} catch (FileNotFoundException e) {
			System.err.println("MCP .csvs gone?! Not on my watch!");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fail: fail! Oh no!");
			e.printStackTrace();
		}
		System.out.println("f"+ fields.size());
	}
	
	public static String getFieldHash(String name, String clazz, int side){
		return (side > 0 ? "server " : "client ") + name + "|" + clazz;
	}
	
	public static String getMethodHash(String name, String sig, String clazz, int side){
		return (side > 0 ? "server" : "client") + name + "|" + sig + "|" + clazz;
	}
	
	public static String getClassHash(String name, String _package, int side){
		return (side > 0 ? "server" : "client") + name + "|" + _package;
	}
	
	public static Object[] getObjectArray(Object...args){
		return args;
	}
	
	private static String[] parseLine(String line){
		String[] temp = line.split(",");
		String[] ret = new String[temp.length];
		for(int i = 0; i < temp.length; i++){
			ret[i] = temp[i].substring(1, temp[i].length() - 1);
		}
		return ret;
	}
	
	private static boolean matches(String name){
		return (name.equals("classes.csv") ||
				name.equals("methods.csv") ||
				name.equals("fields.csv"));
	}
}
