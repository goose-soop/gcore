package com.guillaumevdn.gcore.lib.exception;

/**
 * @author GuillaumeVDN
 */
public class ConfigError extends Error {

	private static final long serialVersionUID = 6678619956748987331L;

	public ConfigError() {
		super();
	}

	public ConfigError(String message) {
		super(message);
	}

	public ConfigError(Throwable cause) {
		super(cause);
	}

	public ConfigError(String message, Throwable cause) {
		super(message, cause);
	}

}
