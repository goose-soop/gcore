package com.guillaumevdn.gcore.lib.element.struct.parsing;

import com.guillaumevdn.gcore.lib.object.Optional;

/**
 * @author GuillaumeVDN
 */
public class ParsedCache<T> {

	private Optional<T> value = null;

	public boolean isPresent() {
		return value != null;
	}

	public Optional<T> get() {
		return value;
	}

	public void set(Optional<T> value) {
		beforeSet(value);
		this.value = value;
	}

	public void clear() {
		set(null);
	}

	protected void beforeSet(Optional<T> value) {
	}

}
