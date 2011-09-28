package net.minecraft.rotten194;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */

public class Annotations {
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ClassNameTarget{
		String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface MCPClassNameTarget{
		String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface noPatch{}
}
