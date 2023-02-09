package com.guillaumevdn.gcore.lib.element.struct.basic;

/**
 * @author GuillaumeVDN
 */
public enum SizeTolerance {

	// ----- values
	DISALLOW_EMPTY_OR_LIST(false, false),
	ALLOW_EMPTY(true, false),
	ALLOW_LIST(false, true),
	ALLOW_EMPTY_AND_LIST(true, true);
	
	private boolean allowEmpty, allowList;

	SizeTolerance(boolean allowEmpty, boolean allowList) {
		this.allowEmpty = allowEmpty;
		this.allowList = allowList;
	}

	// ----- get
	public boolean allowEmpty() {
		return allowEmpty;
	}

	public boolean allowList() {
		return allowList;
	}

}
