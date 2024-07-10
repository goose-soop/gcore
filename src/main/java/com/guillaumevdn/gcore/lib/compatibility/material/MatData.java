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

	private int legacyData; // if there's only one
	private List<Integer> legacyDatas; // if multiple are allowed (for instance, multiple facings of the same block, such as wood)
	private boolean door, traversable, damageable;

	public MatData(Version version, ComparisonType versionComparison, String dataName, Material dataInstance, int legacyData, List<Integer> legacyDatas,
			List<MatExtra> extra) {
		super(version, versionComparison, dataName, dataInstance, extra);
		if (legacyData >= 0 && legacyDatas != null)
			throw new IllegalArgumentException("got both legacyData and legacyDatas");
		this.legacyData = legacyData;
		this.legacyDatas = legacyDatas;
		this.door = extra != null && extra.contains(MatExtra.DOOR);
		this.traversable = extra != null && extra.contains(MatExtra.TRAVERSABLE);
		this.damageable = extra != null && extra.contains(MatExtra.DAMAGEABLE);
	}

	// ----- get
	public boolean hasLegacyData() {
		return legacyData >= 0 || legacyDatas != null;
	}

	public int getLegacyDataOrZero() {
		return legacyData >= 0 ? legacyData : (legacyDatas != null ? legacyDatas.get(0) : 0);
	}

	public int _getLegacyData() {
		return legacyData;
	}

	public List<Integer> _getLegacyDatas() {
		return legacyDatas;
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

	// ----- data
	public boolean acceptsLegacyData(int data) {
		return legacyDatas != null ? legacyDatas.contains(data) : legacyData < 0 || legacyData == data;
	}

	// ----- object
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		MatData other = ObjectUtils.castOrNull(obj, MatData.class);
		return other != null && CollectionUtils.contentEquals(legacyDatas, other.legacyDatas);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), legacyDatas);
	}

	@Override
	public MatData clone() {
		return new MatData(getVersion(), getVersionComparison(), getDataName(), getDataInstance(), legacyData,
				legacyDatas != null ? CollectionUtils.asList(legacyDatas) : null, getExtra().isEmpty() ? null : CollectionUtils.asList(getExtra()));
	}

	@Override
	public String toString() {
		return "(" + getVersionComparison().getSymbol() + getVersion() + "," + getDataName()
				+ (legacyDatas == null && legacyData == 0 ? "" : ":" + (legacyDatas == null ? legacyData : legacyDatas))
				+ (getExtra().isEmpty() ? "" : "," + StringUtils.toTextString("-", getExtra())) + ")";
	}

	// ----- extra
	public static enum MatExtra {
		DOOR, TRAVERSABLE, DAMAGEABLE,
	}

}
