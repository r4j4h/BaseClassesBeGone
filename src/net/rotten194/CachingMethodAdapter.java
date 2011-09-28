package net.rotten194;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodHandle;
import org.objectweb.asm.MethodVisitor;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */
public class CachingMethodAdapter implements MethodVisitor {
	CallCache cache;
	int access;
	String name;
	String desc;
	String signature;
	String[] exceptions;
	String className;
	public boolean patch = true;

	public CachingMethodAdapter(CallCache cache, int access, String name, String desc, String signature, String[] exceptions, String className) {
		super();
		this.cache = cache;
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.exceptions = exceptions;
		this.className = className;
	}
	
	public void unCache(ClassVisitor cw, String patchName, String sourceName){
		MethodVisitor mv = cw.visitMethod(access, name, desc, signature, exceptions);
		List<Object[]> events = cache.getEvents();
		for (Object[] event : events){
			for (int i = 2; i < event.length; i++){
				if (event[i] instanceof String){
					event[i] = ((String)event[i]).replace(patchName, sourceName);
				}
			}
			String md = (String)event[0];
			if (md.equals("visitAnnotation")){
				//(String desc, boolean visible) 
				mv.visitAnnotation((String)event[2], (Boolean)event[3]);
			}
			else if (md.equals("visitAnnotationDefault")){
				//()
				mv.visitAnnotationDefault();
			}
			else if (md.equals("visitAttribute")){
				//(Attribute attr) 
				mv.visitAttribute((Attribute)event[2]);
			}
			else if (md.equals("visitCode")){
				//() 
				mv.visitCode();
			}
			else if (md.equals("visitEnd")){
				//() 
				mv.visitEnd();
			}
			else if (md.equals("visitFieldInsn")){
				//(int opcode, String owner, String name, String desc) 
				mv.visitFieldInsn((Integer)event[2], (String)event[3], (String)event[4], (String)event[5]);
			}
			else if (md.equals("visitFrame")){
				//(int type, int nLocal, Object[] local, int nStack, Object[] stack) 
				mv.visitFrame((Integer)event[2], (Integer)event[3], (Object[])event[4], (Integer)event[5], (Object[])event[6]);
			}
			else if (md.equals("visitIincInsn")){
				//(int var, int increment) 
				mv.visitIincInsn((Integer)event[2], (Integer)event[3]);
			}
			else if (md.equals("visitInsn")){
				//(int opcode) 
				mv.visitInsn((Integer)event[2]);
			}
			else if (md.equals("visitIntInsn")){
				//(int opcode, int operand) 
				mv.visitIntInsn((Integer)event[2], (Integer)event[3]);
			}
			else if (md.equals("visitInvokeDynamicInsn")){
				//(String name, String desc, MethodHandle bsm, Object... bsmArgs) 
				mv.visitInvokeDynamicInsn((String)event[2], (String)event[3], (MethodHandle)event[4], (Object[])event[5]);
			}
			else if (md.equals("visitJumpInsn")){
				//(int opcode, Label label) 
				mv.visitJumpInsn((Integer)event[2], (Label)event[3]);
			}
			else if (md.equals("visitLabel")){
				//(Label label) 
				mv.visitLabel((Label)event[2]);
			}
			else if (md.equals("visitLdcInsn")){
				//(Object cst) 
				mv.visitLdcInsn(event[2]);
			}
			else if (md.equals("visitLineNumber")){
				//(int line, Label start) 
				mv.visitLineNumber((Integer)event[2], (Label)event[3]);
			}
			else if (md.equals("visitLocalVariable")){
				//(String name, String desc, String signature, Label start, Label end, int index) 
				mv.visitLocalVariable((String)event[2], (String)event[3], (String)event[4], (Label)event[5], (Label)event[6], (Integer)event[7]);
			}
			else if (md.equals("visitLookupSwitchInsn")){
				//(Label dflt, int[] keys, Label[] labels) 
				mv.visitLookupSwitchInsn((Label)event[2], (int[])event[3], (Label[])event[4]);
			}
			else if (md.equals("visitMaxs")){
				//(int maxStack, int maxLocals) 
				mv.visitMaxs((Integer)event[2], (Integer)event[3]);
			}
			else if (md.equals("visitMethodInsn")){
				//(int opcode, String owner, String name, String desc) 
				System.out.println(event[2].toString());
				mv.visitMethodInsn((Integer)event[2], (String)event[3], (String)event[4], (String)event[5]);
			}
			else if (md.equals("visitMultiANewArrayInsn")){
				//(String desc, int dims) 
				mv.visitMultiANewArrayInsn((String)event[2], (Integer)event[3]);
			}
			else if (md.equals("visitParameterAnnotation")){
				//(int parameter, String desc, boolean visible) 
				mv.visitParameterAnnotation((Integer)event[2], (String)event[3], (Boolean)event[4]);
			}
			else if (md.equals("visitTableSwitchInsn")){
				//(int min, int max, Label dflt, Label... labels) 
				mv.visitTableSwitchInsn((Integer)event[2], (Integer)event[3], (Label)event[4], (Label[])event[5]);
			}
			else if (md.equals("visitTryCatchBlock")){
				//(Label start, Label end, Label handler, String type) 
				mv.visitTryCatchBlock((Label)event[2], (Label)event[3], (Label)event[4], (String)event[5]);
			}
			else if (md.equals("visitTypeInsn")){
				//(int opcode, String type) 
				mv.visitTypeInsn((Integer)event[2], (String)event[3]);
			}
			else if (md.equals("visitVarInsn")){
				//(int opcode, int var) 
				mv.visitVarInsn((Integer)event[2], (Integer)event[3]);
			}
			else{
				System.err.println("No such method: " + md + " d: " + event);
			}
		}
	}
	
	public boolean matches(String name, String desc, String signature){
		return this.name.equals(name) && this.desc.equals(desc); 
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitAnnotation(java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
		System.out.println("Annotation: " + arg0);
		if (arg0.equals("Lnet/minecraft/rotten194/Annotations$noPatch;")){
			this.patch = false;
			System.out.println("Skipping method " + name + desc);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitAnnotationDefault()
	 */
	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitAttribute(org.objectweb.asm.Attribute)
	 */
	@Override
	public void visitAttribute(Attribute arg0) {
		cache.createEvent("visitAttribute", "", arg0);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitCode()
	 */
	@Override
	public void visitCode() {
		cache.createEvent("visitCode", "", (Object[])null);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitEnd()
	 */
	@Override
	public void visitEnd() {
		cache.createEvent("visitEnd", "", (Object[])null);
		System.out.println("Finished caching method: " + access + " " + className + " " + desc + " " + name + " " + signature + " " + patch);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitFieldInsn(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void visitFieldInsn(int arg0, String arg1, String arg2, String arg3) {
		cache.createEvent("visitFieldInsn", CallCache.getId(arg1, arg2, arg3), arg0, arg1, arg2, arg3);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitFrame(int, int, java.lang.Object[], int, java.lang.Object[])
	 */
	@Override
	public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3, Object[] arg4) {
		cache.createEvent("visitFrame", "", arg0, arg1, arg2, arg3, arg4);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitIincInsn(int, int)
	 */
	@Override
	public void visitIincInsn(int arg0, int arg1) {
		cache.createEvent("visitIincInsn", "", arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitInsn(int)
	 */
	@Override
	public void visitInsn(int arg0) {
		cache.createEvent("visitInsn", "", arg0);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitIntInsn(int, int)
	 */
	@Override
	public void visitIntInsn(int arg0, int arg1) {
		cache.createEvent("visitIntInsn", "", arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitInvokeDynamicInsn(java.lang.String, java.lang.String, org.objectweb.asm.MethodHandle, java.lang.Object[])
	 */
	@Override
	public void visitInvokeDynamicInsn(String arg0, String arg1, MethodHandle arg2, Object... arg3) {
		cache.createEvent("visitInvokeDynamicInsn", CallCache.getId(arg0, arg1), arg3);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitJumpInsn(int, org.objectweb.asm.Label)
	 */
	@Override
	public void visitJumpInsn(int arg0, Label arg1) {
		cache.createEvent("visitJumpInsn", "", arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitLabel(org.objectweb.asm.Label)
	 */
	@Override
	public void visitLabel(Label arg0) {
		cache.createEvent("visitLabel", "", arg0);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitLdcInsn(java.lang.Object)
	 */
	@Override
	public void visitLdcInsn(Object arg0) {
		cache.createEvent("visitLdcInsn", "", arg0);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitLineNumber(int, org.objectweb.asm.Label)
	 */
	@Override
	public void visitLineNumber(int arg0, Label arg1) {
		cache.createEvent("visitLineNumber", "", arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitLocalVariable(java.lang.String, java.lang.String, java.lang.String, org.objectweb.asm.Label, org.objectweb.asm.Label, int)
	 */
	@Override
	public void visitLocalVariable(String arg0, String arg1, String arg2, Label arg3, Label arg4, int arg5) {
		cache.createEvent("visitLocalVariable", "", arg0, arg1, arg2, arg3, arg4, arg5);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitLookupSwitchInsn(org.objectweb.asm.Label, int[], org.objectweb.asm.Label[])
	 */
	@Override
	public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {
		cache.createEvent("visitLookupSwitchInsn", "", arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitMaxs(int, int)
	 */
	@Override
	public void visitMaxs(int arg0, int arg1) {
		cache.createEvent("visitMaxs", "", arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitMethodInsn(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void visitMethodInsn(int arg0, String arg1, String arg2, String arg3) {
		cache.createEvent("visitMethodInsn", "", arg0, arg1, arg2, arg3);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitMultiANewArrayInsn(java.lang.String, int)
	 */
	@Override
	public void visitMultiANewArrayInsn(String arg0, int arg1) {
		cache.createEvent("visitMultiANewArrayInsn", arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitParameterAnnotation(int, java.lang.String, boolean)
	 */
	@Override
	public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1, boolean arg2) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitTableSwitchInsn(int, int, org.objectweb.asm.Label, org.objectweb.asm.Label[])
	 */
	@Override
	public void visitTableSwitchInsn(int arg0, int arg1, Label arg2, Label... arg3) {
		cache.createEvent("visitTableSwitchInsn", "", arg0, arg1, arg2, arg3);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitTryCatchBlock(org.objectweb.asm.Label, org.objectweb.asm.Label, org.objectweb.asm.Label, java.lang.String)
	 */
	@Override
	public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2, String arg3) {
		cache.createEvent("visitTryCatchBlock", "", arg0, arg1, arg2, arg3);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitTypeInsn(int, java.lang.String)
	 */
	@Override
	public void visitTypeInsn(int arg0, String arg1) {
		cache.createEvent("visitTypeInsn", "", arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.MethodVisitor#visitVarInsn(int, int)
	 */
	@Override
	public void visitVarInsn(int arg0, int arg1) {
		cache.createEvent("visitVarInsn", "", arg0, arg1);
	}

}
