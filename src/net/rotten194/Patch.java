package net.rotten194;

import java.util.ArrayList;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodHandle;
import org.objectweb.asm.MethodVisitor;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */

public abstract class Patch {
	public final PatchEncloser metadata;
	final ClassReader reader;
	
	public Patch(PatchEncloser metadata, ClassReader reader) {
		this.metadata = metadata;
		this.reader = reader;
	}

	public boolean patch(){
		return false;
	}	
	
	//addMethod(String name, ..., String takeFrom)
	//addMethd(String name, ..., String[] bytecode)
	//addField(String name, Type type, boolean static, Object value)
	//replaceMethod
	//addHook(String method, String hooked, String[] pass, String return, int location/*BEGINNING, END, ALLEXIT, linenum*/)
	//invokeMethod
	//addGetters
	//addSetters
	//changeMethodConstant(int location, Object value, Object newValue)
}
