package net.rotten194;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodHandle;
import org.objectweb.asm.MethodVisitor;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */

public class MethodStealer extends ClassAdapter implements MethodVisitor{
	String methodTarget;
	String desc;
	String newClass;
	String oldClass;
	MethodVisitor mv;
	
	public MethodStealer(String methodTarget, String desc, String oldClass, String newClass, MethodVisitor mv){
		super(new ClassWriter(0));
		this.methodTarget = methodTarget;
		this.desc = desc;
		this.mv = mv;
		this.newClass = newClass;
		this.oldClass= oldClass;
	}
	
	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		// TODO Auto-generated method stub
		if (name.equals(methodTarget) && desc.equals(desc)){
			return this;
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
		mv.visitAnnotation(arg0, arg1);
		return null;
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		mv.visitAnnotationDefault();
		return null;
	}

	@Override
	public void visitAttribute(Attribute arg0) {
		mv.visitAttribute(arg0);
	}

	@Override
	public void visitCode() {
		mv.visitCode();
	}

	@Override
	public void visitEnd() {
		mv.visitEnd();
	}

	@Override
	public void visitFieldInsn(int arg0, String arg1, String arg2, String arg3) {
		//mv.visitFieldInsn(Opcodes.GETFIELD, "net/tests/ASMMethodTest$Class1", "foo", "I");
		mv.visitFieldInsn(arg0, arg1.replace(oldClass, newClass), arg2, arg3);
	}

	@Override
	public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3, Object[] arg4) {
		mv.visitFrame(arg0, arg1, arg2, arg3, arg4);	
	}

	@Override
	public void visitIincInsn(int arg0, int arg1) {
		mv.visitIincInsn(arg0, arg1);
		
	}

	@Override
	public void visitInsn(int arg0) {
		mv.visitInsn(arg0);
	}

	@Override
	public void visitIntInsn(int arg0, int arg1) {
		mv.visitIntInsn(arg0, arg1);
	}

	@Override
	public void visitInvokeDynamicInsn(String arg0, String arg1, MethodHandle arg2, Object... arg3) {
		mv.visitInvokeDynamicInsn(arg0.replace(oldClass, newClass), arg1.replace(oldClass, newClass), arg2, arg3);
	}

	@Override
	public void visitJumpInsn(int arg0, Label arg1) {
		mv.visitJumpInsn(arg0, arg1);
	}

	@Override
	public void visitLabel(Label arg0) {
		mv.visitLabel(arg0);
	}

	@Override
	public void visitLdcInsn(Object arg0) {
		mv.visitLdcInsn(arg0);
	}

	@Override
	public void visitLineNumber(int arg0, Label arg1) {
		mv.visitLineNumber(arg0, arg1);
	}

	@Override
	public void visitLocalVariable(String arg0, String arg1, String arg2, Label arg3, Label arg4, int arg5) {
		//mv.visitLocalVariable("this", "Lnet/tests/ASMMethodTest$Class1;", null, l0, l2, 0);
		mv.visitLocalVariable(arg0, arg1.replace(oldClass, newClass), arg2, arg3, arg4, arg5);
	}

	@Override
	public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {
		mv.visitLookupSwitchInsn(arg0, arg1, arg2);
	}

	@Override
	public void visitMaxs(int arg0, int arg1) {
		mv.visitMaxs(arg0, arg1);
	}

	@Override
	public void visitMethodInsn(int arg0, String arg1, String arg2, String arg3) {
		mv.visitMethodInsn(arg0, arg1.replace(oldClass, newClass), arg2.replace(oldClass, newClass), arg3.replace(oldClass, newClass));
	}

	@Override
	public void visitMultiANewArrayInsn(String arg0, int arg1) {
		mv.visitMultiANewArrayInsn(arg0.replace(oldClass, newClass), arg1);
	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1, boolean arg2) {
		mv.visitParameterAnnotation(arg0, arg1.replace(oldClass, newClass), arg2);
		return null;
	}

	@Override
	public void visitTableSwitchInsn(int arg0, int arg1, Label arg2, Label... arg3) {
		mv.visitTableSwitchInsn(arg0, arg1, arg2, arg3);
	}

	@Override
	public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2, String arg3) {
		mv.visitTryCatchBlock(arg0, arg1, arg2, arg3);
	}

	@Override
	public void visitTypeInsn(int arg0, String arg1) {
		mv.visitTypeInsn(arg0, arg1.replace(oldClass, newClass));
	}

	@Override
	public void visitVarInsn(int arg0, int arg1) {
		mv.visitVarInsn(arg0, arg1);
	}	
}
