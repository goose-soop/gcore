package com.guillaumevdn.gcore.lib.compatibility.material;

import java.util.List;
import java.util.Objects;

import org.bukkit.Material;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.material.MatData.MatExtra;
import com.guillaumevdn.gcore.lib.compatibility.variants.SimpleExistingVariantData;
import com.guillaumevdn.gcore.lib.logic.ComparisonType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public final class MatData extends SimpleExistingVariantData<Material, MatExtra> {

	private int legacyData;
	private boolean door, traversable, damageable;

	public MatData(Version version, ComparisonType versionComparison, String dataName, Material dataInstance, int legacyData, List<MatExtra> extra) {
		super(version, versionComparison, dataName, dataInstance, extra);
		this.legacyData = legacyData;
		this.door = extra != null && extra.contains(MatExtra.DOOR);
		this.traversable = extra != null && extra.contains(MatExtra.TRAVERSABLE);
		this.damageable = extra != null && extra.contains(MatExtra.DAMAGEABLE);
	}

	// get
	public int getLegacyData() {
		return legacyData;
	}

	public boolean isDoor() {
		return door;
	}

	public boolean isTraversable() {
		return traversable;
	}

	public boolean isDamageable() {
		return damageable;
	}

	// object
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		MatData other = ObjectUtils.castOrNull(obj, MatData.class);
		return other != null
				&& Objects.deepEquals(legacyData, other.legacyData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), legacyData);
	}

	@Override
	public MatData clone() {
		return new MatData(getVersion(), getVersionComparison(), getDataName(), getDataInstance(), legacyData, getExtra().isEmpty() ? null : CollectionUtils.asList(getExtra()));
	}

	@Override
	public String toString() {
		return "("
				+ getVersionComparison().getSymbol() + getVersion() + ","
				+ getDataName() + (legacyData == 0 ? "" : ":" + legacyData)
				+ (getExtra().isEmpty() ? "" : "," + StringUtils.toTextString("-", getExtra()))
				+ ")";
	}

	// extra
	public static enum MatExtra {
		DOOR,
		TRAVERSABLE,
		DAMAGEABLE
		;
	}

}
