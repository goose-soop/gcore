package com.guillaumevdn.gcore.lib.configuration.file;

/**
 * @author GuillaumeVDN
 */
public class YMLError extends Error {

	private static final long serialVersionUID = 609002656213244555L;

	public YMLError() {
	}

	public YMLError(String message) {
		super(message);
	}

	public YMLError(Throwable cause) {
		super(cause);
	}

	public YMLError(String message, Throwable cause) {
		super(message, cause);
	}

}
