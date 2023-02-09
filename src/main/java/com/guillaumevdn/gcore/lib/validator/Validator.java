package com.guillaumevdn.gcore.lib.validator;

/**
 * @author GuillaumeVDN
 */
public abstract class Validator<T> {
	
	private String mustBeDescription;

	public Validator(String mustBeDescription) {
		this.mustBeDescription = mustBeDescription;
	}

	// ----- get
	public String getMustBeDescription() {
		return mustBeDescription;
	}

	// ----- abstract methods
	public abstract boolean isValid(T value);

}
