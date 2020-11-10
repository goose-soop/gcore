package com.guillaumevdn.gcore.lib.compatibility.variants;

import java.util.List;
import java.util.Objects;

import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.logic.ComparisonType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public abstract class SimpleExistingVariantData<T, E extends Enum<E>> extends SimpleVariantData<E> {
	
	private final T dataInstance;

	public SimpleExistingVariantData(Version version, ComparisonType versionComparison, String dataName, T dataInstance, List<E> extra) {
		super(version, versionComparison, dataName, extra);
		this.dataInstance = dataInstance;
	}

	// get
	public final T getDataInstance() {
		return dataInstance;
	}

	// object
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		SimpleExistingVariantData other = ObjectUtils.castOrNull(obj, getClass());
		return other != null && Objects.deepEquals(dataInstance, other.dataInstance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), dataInstance);
	}

}
