package com.guillaumevdn.gcore.lib.element.type.container;

public enum ElementItemMode {

	MATCH(false, false),
	BUILDABLE(true, true),
	BUILDABLE_NO_AMOUNT(true, false);

	private final boolean requireType;
	private final boolean allowAmount;

	ElementItemMode(boolean requireType, boolean allowAmount) {
		this.requireType = requireType;
		this.allowAmount = allowAmount;
	}

	public boolean requireType() {
		return requireType;
	}
	public boolean allowAmount() {
		return allowAmount;
	}

}
