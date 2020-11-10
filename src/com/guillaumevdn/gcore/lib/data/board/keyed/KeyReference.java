package com.guillaumevdn.gcore.lib.data.board.keyed;

import java.util.Objects;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class KeyReference<K> {

	private K key;

	public KeyReference(K key) {
		this.key = key;
	}

	// get
	public K getKey() {
		return key;
	}

	// object
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		KeyReference<K> other = ObjectUtils.castOrNull(obj, getClass());
		return other != null && key.equals(other.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

	@Override
	public String toString() {
		return key.toString();
	}

	// static
	public static <K> KeyReference<K> of(K key) {
		return new KeyReference<>(key);
	}

}
