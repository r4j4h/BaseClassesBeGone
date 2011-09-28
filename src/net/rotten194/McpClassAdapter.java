package net.rotten194;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodHandle;
import org.objectweb.asm.MethodVisitor;

import static net.rotten194.McpCsvLoader.getClassName;
import static net.rotten194.McpCsvLoader.getMethodName;
import static net.rotten194.McpCsvLoader.getMethodNotchSig;
import static net.rotten194.McpCsvLoader.getFieldName;


/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */
public class McpClassAdapter extends ClassAdapter {
	String name;
	/**
	 * @param arg0
	 */
	public McpClassAdapter(ClassVisitor arg0, String name) {
		super(arg0);
		this.name = name;
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitField(int, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	/*@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		// TODO Auto-generated method stub
		return super.visitField(access, getFieldName(name, this.name), desc, signature, value);
	}*/
	
	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		//return super.visitMethod(access, getMethodName(name, desc, this.name), desc, signature, exceptions);
		// TODO Auto-generated method stub
		System.out.println("Reobfing method " + name + desc + " to " + this.name + "." + getMethodName(name, desc, this.name));
		MethodVisitor mv = super.visitMethod(access, getMethodName(name, desc, this.name), desc, signature, exceptions);
		return new MethodAdapter(mv) {
			/* (non-Javadoc)
			 * @see org.objectweb.asm.MethodAdapter#visitFieldInsn(int, java.lang.String, java.lang.String, java.lang.String)
			 */
			@Override
			public void visitFieldInsn(int opcode, String owner, String name, String desc) {
				// TODO Auto-generated method stub
				String className = McpCsvLoader.getClassName(owner);
				System.out.println("Replacing field ref: " + owner + "." + name + " with " + className + "." + McpCsvLoader.getFieldName(name, className));
				super.visitFieldInsn(opcode, className, McpCsvLoader.getFieldName(name, className), desc);
			}
			
			/* (non-Javadoc)
			 * @see org.objectweb.asm.MethodAdapter#visitInvokeDynamicInsn(java.lang.String, java.lang.String, org.objectweb.asm.MethodHandle, java.lang.Object[])
			 */
			@Override
			public void visitInvokeDynamicInsn(String name, String desc, MethodHandle bsm, Object... bsmArgs) {
				// TODO Auto-generated method stub
				String className = McpCsvLoader.getClassName(bsm.getOwner());
				MethodHandle newBSM = new MethodHandle(bsm.getTag(), className,  McpCsvLoader.getMethodName(bsm.getName(), bsm.getDesc(), bsm.getOwner()), bsm.getDesc());
				System.out.println("Replacing method call: " + bsm.getOwner() + "." + name + " with " + className + "." +  McpCsvLoader.getMethodName(name, desc, bsm.getOwner()) + "(" + newBSM);	
				super.visitInvokeDynamicInsn(className, desc, newBSM, bsmArgs);
			}
			
			/* (non-Javadoc)
			 * @see org.objectweb.asm.MethodAdapter#visitMethodInsn(int, java.lang.String, java.lang.String, java.lang.String)
			 */
			@Override
			public void visitMethodInsn(int opcode, String owner, String name, String desc)  {
				// TODO Auto-generated method stub
				String className =  McpCsvLoader.getClassName(owner);
				if (name.equals("func_35356_c")) System.out.println(name + ", " + desc + ", " + className);
				System.out.println("Replacing method call: " + owner + "." + name + " with " + className + "." +  McpCsvLoader.getMethodName(name, desc, owner));
				super.visitMethodInsn(opcode, className, McpCsvLoader.getMethodName(name, desc, owner), desc);
			}
		};
				
	}
	
	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visit(int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		// TODO Auto-generated method stub
		super.visit(version, access, name, signature, superName, interfaces);
	}
}
