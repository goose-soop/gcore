package com.guillaumevdn.gcore.lib.validator.type;

import java.util.Collection;

import com.guillaumevdn.gcore.lib.validator.Validator;

/**
 * @author GuillaumeVDN
 */
public abstract class CollectionIntegerValidator extends Validator<Collection<Integer>> {

	public CollectionIntegerValidator(String mustBeDescription) {
		super(mustBeDescription);
	}

}
