package com.guillaumevdn.gcore.lib.validator.type;

import com.guillaumevdn.gcore.lib.validator.Validator;

/**
 * @author GuillaumeVDN
 */
public abstract class IntegerValidator extends Validator<Integer> {
	
	public IntegerValidator(String mustBeDescription) {
		super(mustBeDescription);
	}

}
