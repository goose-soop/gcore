package com.guillaumevdn.gcore.lib.validator.type;

import com.guillaumevdn.gcore.lib.validator.Validator;

/**
 * @author GuillaumeVDN
 */
public abstract class ValueValidator<T> extends Validator<T> {

	public ValueValidator(String mustBeDescription) {
		super(mustBeDescription);
	}

}
