package net.rotten194;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.EmptyVisitor;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */

public class PatchEncloser {
	private byte[] patch;
	private String target;
	private String mcpTarget;
	
	public static PatchEncloser generateEncloser(byte[] c){
		return new PatchEncloser(c);
	}
	
	private PatchEncloser(byte[] b){
		patch = b;
		ClassReader cr = new ClassReader(b);
		System.out.println(cr.getSuperName());
		cr.accept(new ClassAdapter(new ClassWriter(0)){
			@Override
			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				System.out.println(desc);
				if (desc.endsWith("ClassNameTarget;")){
					return new EmptyVisitor(){
						@Override
						public void visit(String name, Object value) {
							System.out.println(name + " " + value);
							target = value.toString();
						}
					};
				}
				else if (desc.endsWith("MCPClassNameTarget;")){
					return new EmptyVisitor(){
						@Override
						public void visit(String name, Object value) {
							System.out.println(name + " " + value);
							mcpTarget = value.toString();
						}
					};
				}
				return null;
			}
		}, 0);
	}

	public byte[] getPatch() {
		return patch;
	}

	public String getTarget() {
		return target;
	}

	public String getMcpTarget() {
		return mcpTarget;
	}

}
