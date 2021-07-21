package com.guillaumevdn.gcore.lib.data.board.keyed;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class KeyReference<K> {

	private final K key;

	public KeyReference(K key) {
		this.key = key;
	}

	// ----- get
	public final K getKey() {
		return key;
	}

	// ----- object
	@Override
	public boolean equals(Object obj) {
		return ObjectUtils.equals(obj, getClass(), other -> key.equals(other.getKey()));
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public String toString() {
		return key.toString();
	}

	// ----- static
	public static <K> KeyReference<K> of(K key) {
		return new KeyReference<>(key);
	}

}
