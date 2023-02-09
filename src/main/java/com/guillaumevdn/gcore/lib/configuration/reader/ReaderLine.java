package com.guillaumevdn.gcore.lib.configuration.reader;

public final class ReaderLine {

	private final int number;
	private final String line;

	public ReaderLine(int number, String line) {
		this.number = number;
		this.line = line;
	}

	public int getNumber() {
		return number;
	}

	public String getLine() {
		return line;
	}

	// ----- obj
	@Override
	public String toString() {
		return number + " '" + line + "'";
	}

}
