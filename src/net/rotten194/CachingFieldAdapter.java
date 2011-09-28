package net.rotten194;

import java.util.List;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */
public class CachingFieldAdapter implements FieldVisitor {
	CallCache cache;
	int access;
	String name;
	String desc;
	String signature;
	Object value;
	String className;
	boolean patch = true;
	
	public CachingFieldAdapter(CallCache cache, int access, String name, String desc, String signature, Object value, String className) {
		super();
		this.cache = cache;
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.value = value;
		this.className = className;
		System.out.println("Initalizing cache for " + name + " (would be " + McpCsvLoader.fields.get(name) + ")");
	}

	public void unCache(ClassVisitor cw){
		FieldVisitor fv = cw.visitField(access, name, desc, signature, value);
		List<Object[]> events = cache.getEvents();
		for (Object[] event : events){
			String md = (String)event[0];
			if (md.equals("visitAttribute")){
				fv.visitAttribute((Attribute)event[2]);
			}
			else if (md.equals("visitEnd")){
				fv.visitEnd();
			}
			else{
				System.err.println("No such method: " + md + " d: " + event);
			}
		}
	}
	
	public boolean matches(String name){
		return this.name.equals(name);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.FieldVisitor#visitAnnotation(java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
		System.out.println("Annotation: " + arg0);
		if (arg0.equals("Lnet/minecraft/rotten194/Annotations$noPatch;")){
			this.patch = false;
			System.out.println("Skipping field " + name);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.FieldVisitor#visitAttribute(org.objectweb.asm.Attribute)
	 */
	@Override
	public void visitAttribute(Attribute arg0) {
		cache.createEvent("visitAttribute", "", arg0);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.FieldVisitor#visitEnd()
	 */
	@Override
	public void visitEnd() {
		cache.createEvent("visitEnd", "", (Object[])null);
	}

}
