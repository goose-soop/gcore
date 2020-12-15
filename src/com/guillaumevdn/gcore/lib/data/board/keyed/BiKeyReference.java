package com.guillaumevdn.gcore.lib.data.board.keyed;

import java.util.Objects;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class BiKeyReference<K, K2> extends KeyReference<K> {

	private final K2 key2;

	public BiKeyReference(K key, K2 key2) {
		super(key);
		this.key2 = key2;
	}

	// get
	public final K2 getKey2() {
		return key2;
	}

	// object
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		BiKeyReference<K, K2> other = ObjectUtils.castOrNull(obj, getClass());
		return other != null && getKey().equals(other.getKey()) && key2.equals(other.key2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getKey(), key2);
	}

	@Override
	public String toString() {
		return "(" + super.toString() + "/" + key2.toString() + ")";
	}

	// static
	public static <K, K2> BiKeyReference<K, K2> of(K key, K2 key2) {
		return new BiKeyReference<>(key, key2);
	}

}
