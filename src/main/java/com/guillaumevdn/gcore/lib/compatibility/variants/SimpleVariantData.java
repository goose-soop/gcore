package com.guillaumevdn.gcore.lib.compatibility.variants;

import java.util.List;

import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.logic.ComparisonType;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public abstract class SimpleVariantData<E extends Enum<E>> extends VariantData<E> {
	
	public SimpleVariantData(Version version, ComparisonType versionComparison, String dataName, List<E> extra) {
		super(version, versionComparison, dataName, extra);
	}

	// ----- get
	@Override
	public String toString() {
		return "("
				+ getVersionComparison().getSymbol() + getVersion() + ","
				+ getDataName()
				+ (getExtra().isEmpty() ? "" : "," + StringUtils.toTextString("-", getExtra()))
				+ ")";
	}

}
