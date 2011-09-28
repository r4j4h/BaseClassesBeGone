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
	private ArrayList<PatchEncloser> patches;
	URL jarURL = null;
	File patchesFolder;
	File confFolder;
	File[] stubs;
	String[] stubClasses;
	String[] stubClassTargets;
	
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
		}
		else if (confFolder.isFile()){
			throw new RuntimeException("please remove file " + confFolder);
		}
		System.out.println("Starting loading of MCP .csvs @ " + System.currentTimeMillis());
		McpCsvLoader.loadCsvs(confFolder);
		System.out.println("done " + System.currentTimeMillis());
		patches = new ArrayList<PatchEncloser>(patchesFolder.listFiles().length);
		goThroughDirTree(patchesFolder);
		try {
			addURL(patchesFolder.toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void goThroughDirTree(File f){
		if (f.isFile() && f.getName().endsWith("class")) {
			PatchEncloser p = generateEnclosingObject(f);
			if (p != null){
				patches.add(p);
			}
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
		//System.out.println("finding " + name);
		return super.findClass(name);
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		//System.out.println("loading " + name + " (1)");
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
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		//System.out.println("load " + name);
		try{
			if (stubs == null){
				stubs = getAllStubClasses();
			}
			if (stubClassTargets == null){
				stubClassTargets = new String[stubs.length];
				for (int i = 0; i < stubs.length; i++){
					stubClassTargets[i] = stubs[i].getName().replace("_Stub.class", "");
				}
			}
			if (stubClasses == null){
				stubClasses = new String[stubs.length];
				for (int i = 0; i < stubs.length; i++){
					stubClasses[i] = stubs[i].getName().replace(".class", "");
				}
			}
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
			return super.loadClass(name, resolve);	
		} catch (IOException e){
			e.printStackTrace();
		}
		System.err.println("returning null from ex");
		return null;
	}
	
	private File[] getAllStubClasses(){
		return patchesFolder.listFiles(new FileFilter() {		
			@Override
			public boolean accept(File f) {
				System.out.println("filter " + f + "" + (f.isFile() && f.getPath().endsWith("_Stub.class")));
				boolean b = f.isFile() && f.getPath().endsWith("_Stub.class");
				if (b && f.getParentFile().equals(patchesFolder)){
					System.err.println("Mod file " + f + " is contained in the top-level patches directory. This is bad practice. Please move it to a subfolder to prevent naming conflicts.");
				}
				return b;
			}
		});
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
