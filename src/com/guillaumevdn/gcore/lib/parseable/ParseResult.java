package com.guillaumevdn.gcore.lib.parseable;

public class ParseResult<T> {

	// base
	private T parsed;

	public ParseResult(T parsed) {
		this.parsed = parsed;
	}

	// get
	public T getParsed() {
		return parsed;
	}

}
