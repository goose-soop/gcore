package com.guillaumevdn.gcore.lib.tuple;

/**
 * @author GuillaumeVDN
 */
public final class ImmutableBooleanPair extends BooleanPair {

	private ImmutableBooleanPair() {
		super();
	}

	private ImmutableBooleanPair(Boolean a, Boolean b) {
		super(a, b);
	}

	@Override
	public void set(Boolean a, Boolean b) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setA(Boolean a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setB(Boolean b) {
		throw new UnsupportedOperationException();
	}

	// ----- static
	public static ImmutableBooleanPair of(Boolean a, Boolean b) {
		return new ImmutableBooleanPair(a, b);
	}

	public static ImmutableBooleanPair empty() {
		return new ImmutableBooleanPair();
	}

}
