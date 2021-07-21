package com.guillaumevdn.gcore.lib.compatibility.sound;

import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.sound.SoundData.SoundExtra;
import com.guillaumevdn.gcore.lib.compatibility.variants.SimpleExistingVariantData;
import com.guillaumevdn.gcore.lib.logic.ComparisonType;

/**
 * @author GuillaumeVDN
 */
public class SoundData extends SimpleExistingVariantData<org.bukkit.Sound, SoundExtra> {
	
	public SoundData(Version version, ComparisonType versionComparison, String dataName, org.bukkit.Sound dataInstance, List<SoundExtra> extra) {
		super(version, versionComparison, dataName, dataInstance, extra);
	}

	// ----- object
	@Override
	public SoundData clone() {
		return new SoundData(getVersion(), getVersionComparison(), getDataName(), getDataInstance(), getExtra().isEmpty() ? null : CollectionUtils.asList(getExtra()));
	}

	// ----- extra
	public static enum SoundExtra {
	}

}
