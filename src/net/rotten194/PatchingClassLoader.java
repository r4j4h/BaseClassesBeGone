package net.rotten194;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.minecraft.Util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import sun.misc.IOUtils;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */

public class PatchingClassLoader extends URLClassLoader {
	AccessControlContext acc;
	//private ArrayList<PatchEncloser> patches;
	URL jarURL = null;
	File patchesFolder;
	File confFolder;
	File[] stubs;
	String[] stubClasses;
	String[] stubClassTargets;
	File[] otherClasses;
	String[] otherClassesNames;
	File[] resources;
	
	public PatchingClassLoader(URL[] urls) {
		super(urls);
		//for(URL u : urls) System.out.println(u);
		for (URL u : urls){
			if (u.toString().endsWith("minecraft.jar")){
				jarURL = u;
				break;
			}
		}
		if (jarURL == null){
			throw new RuntimeException("Cannot find minecraft.jar!");
		}
		this.acc = AccessController.getContext();
		patchesFolder = new File(Util.getWorkingDirectory(), "patches");
		if (!patchesFolder.exists()){
			patchesFolder.mkdir();
		}
		else if (patchesFolder.isFile()){
			throw new RuntimeException("please remove file " + patchesFolder);
		}
		confFolder = new File(Util.getWorkingDirectory(), ".bcbg_conf");
		if (!confFolder.exists()){
			confFolder.mkdir();
			copyCsv("classes.csv", confFolder);
			copyCsv("methods.csv", confFolder);
			copyCsv("fields.csv", confFolder);
			System.out.println("Added MCP csv files, courtesy of Searge and the other contributors to the excellent Minecraft Coder Pack. Cheers!");
		}
		else if (confFolder.isFile()){
			throw new RuntimeException("please remove file " + confFolder);
		}
		System.out.println("Starting loading of MCP .csvs @ " + System.currentTimeMillis());
		McpCsvLoader.loadCsvs(confFolder);
		System.out.println("done " + System.currentTimeMillis());
		//patches = new ArrayList<PatchEncloser>(patchesFolder.listFiles().length);
		//goThroughDirTree(patchesFolder);	
		stubs = getAllStubClasses();
		stubClassTargets = new String[stubs.length];
		for (int i = 0; i < stubs.length; i++){
			stubClassTargets[i] = stubs[i].getName().replace("_Stub.class", "");
		}
		stubClasses = new String[stubs.length];
		for (int i = 0; i < stubs.length; i++){
			stubClasses[i] = stubs[i].getName().replace(".class", "");
		}
		otherClasses = getAllOtherClasses();
		otherClassesNames = new String[otherClasses.length];
		for (int i = 0; i < otherClasses.length; i++){
			otherClassesNames[i] = otherClasses[i].getName().replace(".class", "");
		}
		resources = getAllResources();
	}
	
	private void copyCsv(String name, File to){
		Path p = to.toPath();
		try {
			Files.copy(getClass().getResourceAsStream(name), p);
		} catch (IOException e){
			System.err.println("Could not copy " + name + " from .jar to " + to.getAbsolutePath());
			e.printStackTrace();
		} catch (NullPointerException e){
			System.err.println("Could not copy " + name + " from .jar to " + to.getAbsolutePath() + "(" + name + " does not exist)");
			e.printStackTrace();
		}
	}
	
	public void goThroughDirTree(File f){
		if (f.isFile()) {
			//later
		} 
		else if (f.isDirectory()){
			for (File f2 : f.listFiles())
				goThroughDirTree(f2);
		}
		
	}
	
	public PatchEncloser generateEnclosingObject(File patchFile){
		try {
			byte[] b = getByteArrayFromFile(patchFile);
			/*Class<?> classy /* that's me! * / = defineClass(b);
			if (Patch.class.isAssignableFrom(classy)){
				Annotation[] annots = classy.getAnnotations();
				String s1 = null;
				String s2 = null;
				for (Annotation a : annots){
					if (a instanceof ClassNameTarget)
						s1 = ((ClassNameTarget) a).value();
					else if (a instanceof MCPClassNameTarget)
						s2 = ((MCPClassNameTarget) a).value();
				}
			}*/
			return PatchEncloser.generateEncloser(b);
		} catch (FileNotFoundException e) {
			System.err.println("Could not load patch file " + patchFile + ", as it does not exist. Details: ");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Could not load patch file " + patchFile + ", is it corrupted? Details: ");
			e.printStackTrace();
		}
		return null;
	}
	
	private byte[] getByteArrayFromFile(File f) throws IOException, FileNotFoundException{
		byte[] b = new byte[(int)f.length()];
		new FileInputStream(f).read(b);
		return b;
	}
	
	/*Notch code- seems like a hack to get net & file permissions... why?*/
	protected PermissionCollection getPermissions(CodeSource codesource) {
		PermissionCollection perms = null;
		try
		{
			Method method = SecureClassLoader.class.getDeclaredMethod("getPermissions", new Class[] { CodeSource.class });
			method.setAccessible(true);
			perms = (PermissionCollection)method.invoke(getClass().getClassLoader(), new Object[] { codesource });
			String host = "www.minecraft.net";
			if ((host != null) && (host.length() > 0))
			{
				perms.add(new SocketPermission(host, "connect,accept"));
			} 
			else codesource.getLocation().getProtocol().equals("file");
			perms.add(new FilePermission("<<ALL FILES>>", "read"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return perms;
	}
	
	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {
		return super.findClass(name);
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return super.loadClass(name);
	}
	
	public byte[] getClassFromJar(String name) throws IOException{
		JarFile jar;
		try {
			jar = new JarFile(jarURL.toURI().toString().substring(6));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			return null;
		}
		Enumeration<JarEntry> e = jar.entries();
		JarEntry entry = null;
		while(e.hasMoreElements()){
			JarEntry j = e.nextElement();
			//System.out.println(j.getName());
			if (j.getName().equals(name + ".class")){
				entry = j;
				break;
			}
		}
		if (entry == null){
			throw new RuntimeException("Could not find " + name + " in minecraft.jar!");
		}
		return IOUtils.readFully(jar.getInputStream(entry), -1, true);
	}
	
	@Override
	public URL getResource(String name) {
		for (File f : resources){
			if (f.getPath().replace("\\", "/").endsWith(name)){
				try {
					return f.toURI().toURL();
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return super.getResource(name);
				}
			}
		}
		return super.getResource(name);
	}
	
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		//System.out.println("load " + name);
		try{
			
			for (int i = 0; i < stubs.length; i++){
				String s = stubClassTargets[i];
				String s2 = stubClasses[i];
				//System.out.println(s + ", " + s2);
				String classString = McpCsvLoader.getClassName(s);
				if (classString.equals(name)){
					System.out.printf("mcp matched: %s to %s", s, classString);
					byte[] clazz = getClassFromJar(classString);
					byte[] stub = getByteArrayFromFile(stubs[i]);
					ClassReader source = new ClassReader(clazz);
					ClassReader patch = new ClassReader(stub);
					ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
					CachingClassAdapter cache = new CachingClassAdapter(new CallCache());
					McpClassAdapter mcp = new McpClassAdapter(cache, classString);
					patch.accept(mcp, 0);
					System.out.println(name + " " + classString);
					PostStubClassAdapter ps = new PostStubClassAdapter(cw);
					ForkClassAdapter fork = new ForkClassAdapter(ps, classString, s2, cache);
					source.accept(fork, 0);
					return defineClass(cw.toByteArray());
				}
			}
			for (int i = 0; i < otherClasses.length; i++){
				String s = otherClassesNames[i];
				if (name.equals(s)){
					return defineClass(getByteArrayFromFile(otherClasses[i]));
				}
			}
			return super.loadClass(name, resolve);	
		} catch (IOException e){
			e.printStackTrace();
		}
		System.err.println("returning null from ex");
		return null;
	}
	
	private File[] getAllStubClasses(){
		FileFilter f = new FileFilter() {		
			@Override
			public boolean accept(File f) {
				System.out.println("filter " + f + "" + (f.isFile() && f.getPath().endsWith("_Stub.class")));
				boolean b = f.isFile() && f.getPath().endsWith("_Stub.class");
				if (b && f.getParentFile().equals(patchesFolder)){
					System.err.println("Mod file " + f + " is contained in the top-level patches directory. This is bad practice. Please move it to a subfolder to prevent naming conflicts.");
				}
				return b;
			}
		};
		ArrayList<File> list = new ArrayList<File>();
		walkDirTree(patchesFolder, f, list);
		File[] ret = new File[list.size()];
		for (int i = 0; i < list.size(); i++){
			ret[i] = list.get(i);
		}
		return ret;
	}
	
	private File[] getAllOtherClasses(){
		FileFilter f = new FileFilter() {		
			@Override
			public boolean accept(File f) {
				boolean b = f.isFile() && (f.getPath().endsWith(".class") && !(f.getPath().endsWith("_Stub.class") || f.getPath().endsWith("Patch.class")));
				if (b && f.getParentFile().equals(patchesFolder)){
					System.err.println("Mod file " + f + " is contained in the top-level patches directory. This is bad practice. Please move it to a subfolder to prevent naming conflicts.");
				}
				return b;
			}
		};
		ArrayList<File> list = new ArrayList<File>();
		walkDirTree(patchesFolder, f, list);
		File[] ret = new File[list.size()];
		for (int i = 0; i < list.size(); i++){
			ret[i] = list.get(i);
		}
		return ret;
	}
	
	private File[] getAllResources(){
		FileFilter f = new FileFilter() {		
			@Override
			public boolean accept(File f) {
				System.out.println("RSRC" + f.getPath());
				boolean b = f.isFile() && !(f.getPath().endsWith(".class"));
				if (b && f.getParentFile().equals(patchesFolder)){
					System.err.println("Mod file " + f + " is contained in the top-level patches directory. This is bad practice. Please move it to a subfolder to prevent naming conflicts.");
				}
				return b;
			}
		};
		ArrayList<File> list = new ArrayList<File>();
		walkDirTree(patchesFolder, f, list);
		File[] ret = new File[list.size()];
		for (int i = 0; i < list.size(); i++){
			ret[i] = list.get(i);
		}
		return ret;
	}
	
	private void walkDirTree(File dir, FileFilter filter, ArrayList<File> list){
		assert dir.isDirectory() && dir.exists();
		for (File f : dir.listFiles()){
			if (f.isFile() && filter.accept(f)){
				list.add(f);
			}
			else if (f.isDirectory()){
				walkDirTree(f, filter, list);
			}
		}
	}
	
	/*public byte[] handleStubClasses(byte[] classy, byte[] patch){
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassVisitor cv = new ClassAdapter(null){
			
		}
	}*/
	
	@Override
	protected void addURL(URL url) {
		System.out.println("adding url " + url);
		super.addURL(url);
	}
	
	public Class<?> defineClass(byte[] b){
		return defineClass(null, b, 0, b.length);
	}
	
	

}
