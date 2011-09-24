package net.rotten194;

import java.util.ArrayList;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */

public class PostStubClassAdapter extends ClassAdapter {
	
	ArrayList<Object[]> todo = new ArrayList<Object[]>();
	
	public PostStubClassAdapter(ClassVisitor cv) {
		super(cv);
	}
	
	public void addMethod(String method, String desc, int id, byte[] code, Patch p, String signature, String[] exceptions){
		todo.add(new Object[]{PatchOps.ADD_METHOD, method, desc, id, code, p, signature, exceptions});
	}
	
	public void replaceMethod(String method, String desc, int id, byte[] code, Patch p, String signature, String[] exceptions){
		todo.add(new Object[]{PatchOps.REPLACE_METHOD, method, desc, id, code, p, signature, exceptions});
	}
	
	public void removeMethod(String method, String desc, int id, byte[] code, Patch p){
		todo.add(new Object[]{PatchOps.REMOVE_METHOD, method, desc, id, p});
	}
	
	public void removeField(String field, int id, Patch p){
		todo.add(new Object[]{PatchOps.REMOVE_FIELD, field, id, p});
	}
	
	public void addField(String field, Type type, Object value, int id, Patch p){
		todo.add(new Object[]{PatchOps.ADD_FIELD, field, type, value, id, p});
	}
	
	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassAdapter#visitEnd()
	 */
	@Override
	public void visitEnd() {
		for (Object[] o : todo){
			PatchOps p = (PatchOps)o[0];
			if (p == PatchOps.ADD_FIELD){
				cv.visitField(Opcodes.ACC_PUBLIC, (String)o[1], ((Type)o[2]).getDescriptor(), (String)o[3], o[4]);
			}
			else if (p == PatchOps.ADD_METHOD || p == PatchOps.REPLACE_METHOD){
				byte[] b = (byte[])o[4];
				ClassReader cr = new ClassReader(b);
				Patch ptch = (Patch)o[5];
				MethodStealer ms = new MethodStealer((String)o[1], (String)o[2], ptch.getClass().getName(), ptch.metadata.getTarget(),
										cv.visitMethod(Opcodes.ACC_PUBLIC, (String)o[1], (String)o[2], (String)o[6], (String[])o[7]));
				cr.accept(ms, 0);
			}
		}
		super.visitEnd();
	}

}
