package net.rotten194;

import java.util.ArrayList;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */

public class ForkClassAdapter extends ClassAdapter {
	String sourceName;
	String patchName;
	CachingClassAdapter override;
	/**
	 * @param cv
	 */
	public ForkClassAdapter(ClassVisitor cv, String sourceName, String patchName, CachingClassAdapter override) {
		super(cv);
		this.sourceName = sourceName;
		this.patchName = patchName;
		this.override = override;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitField(int, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public FieldVisitor visitField(int arg0, String arg1, String arg2, String arg3, Object arg4) {
		ArrayList<CachingFieldAdapter> fields = override.getFields();
		for (CachingFieldAdapter field : fields){
			if (field.matches(arg1)){
				return null;
			}
		}
		return super.visitField(arg0, arg1, arg2, arg3, arg4);
	}
	
	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public MethodVisitor visitMethod(int arg0, String arg1, String arg2, String arg3, String[] arg4) {
		ArrayList<CachingMethodAdapter> methods = override.getMethods();
		for (CachingMethodAdapter method : methods){
			if (method.matches(arg1, arg2, arg3)){
				return null;
			}
		}
		return super.visitMethod(arg0, arg1, arg2, arg3, arg4);
	}
	
	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitEnd()
	 */
	@Override
	public void visitEnd() {
		ArrayList<CachingMethodAdapter> methods = override.getMethods();
		ArrayList<CachingFieldAdapter> fields = override.getFields();
		for (CachingMethodAdapter method : methods){
			method.unCache(cv, patchName, sourceName);
		}
		for (CachingFieldAdapter field : fields){
			field.unCache(cv);
		}
		super.visitEnd();
	}
	
	
}
