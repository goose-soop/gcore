package com.guillaumevdn.gcore.lib.migration;

/**
 * @author GuillaumeVDN
 */
public class SilentFail extends Error {

	private static final long serialVersionUID = 8928197740987764306L;

	public SilentFail() {
		super();
	}

	public SilentFail(String message) {
		super(message);
	}

	public SilentFail(Throwable cause) {
		super(cause);
	}

	public SilentFail(String message, Throwable cause) {
		super(message, cause);
	}

}
