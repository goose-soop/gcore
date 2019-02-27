package be.pyrrh4.pyrcore.lib.loadable;

import be.pyrrh4.pyrcore.PyrCore;

public class LoadResult<T> {

	// base
	private T result;
	private String error;
	private String configErrorPrefix;

	public LoadResult() {
		this("");
	}

	public LoadResult(String configErrorPrefix) {
		this(configErrorPrefix, null, null, false);
	}

	public LoadResult(String configErrorPrefix, T result, String error, boolean instantLog) {
		this.configErrorPrefix = configErrorPrefix;
		this.result = result;
		this.error = error;
		if (instantLog) {
			logError();
		}
	}

	// get
	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public String getError() {
		return error;
	}

	public String getConfigErrorPrefix() {
		return configErrorPrefix;
	}

	public void setConfigErrorPrefix(String configErrorPrefix) {
		this.configErrorPrefix = configErrorPrefix;
	}

	/**
	 * @param error the error message
	 * @return itself
	 */
	public LoadResult<T> setError(String error) {
		return setError(error, false);
	}

	/**
	 * @param error the error message
	 * @param overwriteExisting true if the current error message (if any) must be overwritten
	 * @return itself
	 */
	public LoadResult<T> setError(String error, boolean overwriteExisting) {
		return setError(error, overwriteExisting, false);
	}

	/**
	 * @param error the error message
	 * @param overwriteExisting true if the current error message (if any) must be overwritten
	 * @param instantLog true if the log must be instantly logged
	 * @return itself
	 */
	public LoadResult<T> setError(String error, boolean overwriteExisting, boolean instantLog) {
		if (error != null && (this.error == null || overwriteExisting)) {
			this.error = error;
			if (instantLog) {
				logError();
			}
		}
		return this;
	}

	/**
	 * Log the current error message if there's any
	 * @return true if there was an error
	 */
	public boolean logError() {
		if (error == null) {
			return false;
		}
		PyrCore.inst().error("Loading error" + (configErrorPrefix != null && !configErrorPrefix.isEmpty() ? " (in " + configErrorPrefix + ") : " : " : ") + error);
		return true;
	}

}
