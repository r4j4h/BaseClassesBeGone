package net.rotten194;

import java.util.ArrayList;

import org.objectweb.asm.AnnotationVisitor;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */
public class CachingAnnotationAdapter implements AnnotationVisitor {
	CallCache cache;
	String method;
	String name;
	boolean visible;
	
	public CachingAnnotationAdapter(CallCache cache, String name, boolean visible){
		this.cache = cache;
		this.name = name;
		this.visible = visible;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.AnnotationVisitor#visit(java.lang.String, java.lang.Object)
	 */
	@Override
	public void visit(String arg0, Object arg1) {
		cache.createEvent("visit", "", arg0, arg1);
	}

	/* 
	 * Sub annotations are not supported.
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String arg0, String arg1) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.AnnotationVisitor#visitArray(java.lang.String)
	 */
	@Override
	public AnnotationVisitor visitArray(String arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.AnnotationVisitor#visitEnd()
	 */
	@Override
	public void visitEnd() {
		cache.createEvent("visitEnd", "", (Object[])null);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.AnnotationVisitor#visitEnum(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void visitEnum(String arg0, String arg1, String arg2) {
		cache.createEvent("visitEnum", "", arg0, arg1, arg2);
	}

}
