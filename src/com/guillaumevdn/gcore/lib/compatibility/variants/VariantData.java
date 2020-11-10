package com.guillaumevdn.gcore.lib.compatibility.variants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.logic.ComparisonType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public abstract class VariantData<E extends Enum<E>> implements Cloneable {

	private final Version version;
	private final ComparisonType versionComparison;
	private final String dataName;
	private final List<E> extra;

	public VariantData(Version version, ComparisonType versionComparison, String dataName, List<E> extra) {
		this.version = version;
		this.versionComparison = versionComparison;
		this.dataName = dataName;
		this.extra = Collections.unmodifiableList(extra == null ? new ArrayList<>() : extra);
	}

	// get
	public final Version getVersion() {
		return version;
	}

	public final ComparisonType getVersionComparison() {
		return versionComparison;
	}

	public final String getDataName() {
		return dataName;
	}

	public final List<E> getExtra() {
		return extra;
	}

	// object
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		VariantData<E> other = ObjectUtils.castOrNull(obj, getClass());
		return other != null
				&& Objects.deepEquals(dataName, other.dataName)
				&& CollectionUtils.contentEquals(extra, other.extra, false);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dataName, Arrays.hashCode(extra.toArray()));
	}

	@Override
	public abstract VariantData<E> clone();

	@Override
	public abstract String toString();

}
