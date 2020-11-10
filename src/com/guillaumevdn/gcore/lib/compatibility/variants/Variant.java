package com.guillaumevdn.gcore.lib.compatibility.variants;

import java.util.Objects;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public abstract class Variant<D extends VariantData> implements Comparable<Variant>, Cloneable {

	private final String id;
	private final D data;
	private final boolean nocheck;

	public Variant(String id, D data) {
		this.id = id.toUpperCase();
		this.data = data;
		this.nocheck = id.equalsIgnoreCase("NOCHECK");
	}

	// get
	public final String getId() {
		return id;
	}

	public final D getData() {
		return data;
	}

	// object
	@Override
	public final String toString() {
		return id;
	}

	@Override
	public final boolean equals(Object obj) {
		Variant other = ObjectUtils.castOrNull(obj, getClass());
		return other != null && (nocheck || other.nocheck || other.id.equalsIgnoreCase(id));
	}

	@Override
	public final int hashCode() {
		return nocheck ? 0 : Objects.hash(id);
	}

	@Override
	public final int compareTo(Variant obj) {
		Variant other = ObjectUtils.castOrNull(obj, getClass());
		return other == null ? -1 : id.compareToIgnoreCase(other.id);
	}

	@Override
	public abstract Variant clone();

}
