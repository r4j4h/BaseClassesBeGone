package net.rotten194;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */
public class CachingClassAdapter extends ClassAdapter{
	CallCache cache;
	ArrayList<CachingMethodAdapter> methods = new ArrayList<CachingMethodAdapter>();
	ArrayList<CachingFieldAdapter> fields = new ArrayList<CachingFieldAdapter>();
	String name;

	/**
	 * @param delegate
	 * @param isPatch
	 * @param cache
	 */
	public CachingClassAdapter(CallCache cache) {
		super(new ClassWriter(0));
		this.cache = cache;
	}

	/**
	 * @return the methods
	 */
	public ArrayList<CachingMethodAdapter> getMethods() {
		return methods;
	}

	/**
	 * @return the fields
	 */
	public ArrayList<CachingFieldAdapter> getFields() {
		return fields;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)  {	
		System.out.println("!!!" + name + " " + signature);
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitAnnotation(java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitAttribute(org.objectweb.asm.Attribute)
	 */
	@Override
	public void visitAttribute(Attribute attr) {
	}

	
	@Override
	public void visitEnd() {
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitField(int, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		CachingFieldAdapter fa = new CachingFieldAdapter(new CallCache(), access, name, desc, signature, value, this.name);
		fields.add(fa);
		return fa;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitInnerClass(java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		CachingMethodAdapter ma = new CachingMethodAdapter(new CallCache(), access, name, desc, signature, exceptions, this.name);
		methods.add(ma);
		return ma;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitOuterClass(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void visitOuterClass(String owner, String name, String desc) {
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitSource(java.lang.String, java.lang.String)
	 */
	@Override
	public void visitSource(String source, String debug) {
	}
}
