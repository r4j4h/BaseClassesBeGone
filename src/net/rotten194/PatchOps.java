package net.rotten194;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */

public enum PatchOps {
	REPLACE_METHOD,
	ADD_METHOD,
	REMOVE_METHOD,
	REPLACE_FIELD,
	ADD_FIELD,
	REMOVE_FIELD,
	INSERT_BYTECODE,
	EDIT_CONSTANT;
}
