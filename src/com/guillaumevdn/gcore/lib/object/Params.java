package com.guillaumevdn.gcore.lib.object;

/**
 * @author GuillaumeVDN
 */
public final class Params {

	// params
	private Object[] params = null;

	public Params() {
	}

	// get
	public Object[] getParams() {
		return params == null ? new Object[0] : params;
	}

	public <T> T getMandatory(Class<T> paramClass) throws IllegalStateException {
		return getMandatory(paramClass, 0);
	}

	public <T> T getMandatory(Class<T> paramClass, int skipCount) throws IllegalStateException {
		T found = getOrDefault(paramClass, null, skipCount);
		if (found != null) return found;
		throw new IllegalStateException("there's no param of type " + paramClass);
	}

	public <T> T getOrDefault(Class<T> paramClass, T def) {
		return getOrDefault(paramClass, def, 0);
	}

	public <T> T getOrDefault(Class<T> paramClass, T def, int mustSkip) {
		int skipped = 0;
		for (Object param : getParams()) {
			T cast = param == null ? null : ObjectUtils.castOrNull(param, paramClass);
			if (cast != null && ++skipped > mustSkip) {
				return cast;
			}
		}
		return def;
	}

	// static
	public static Params of(Object... params) {
		Params result = new Params();
		result.params = params;
		return result;
	}

}
