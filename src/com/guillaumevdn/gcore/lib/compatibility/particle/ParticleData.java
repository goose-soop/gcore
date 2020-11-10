package com.guillaumevdn.gcore.lib.compatibility.particle;

import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.particle.ParticleData.ParticleExtra;
import com.guillaumevdn.gcore.lib.compatibility.variants.SimpleVariantData;
import com.guillaumevdn.gcore.lib.logic.ComparisonType;

/**
 * @author GuillaumeVDN
 */
public final class ParticleData extends SimpleVariantData<ParticleExtra> {

	private boolean colorable;
	private boolean musicNote;

	public ParticleData(Version version, ComparisonType versionComparison, String dataName, List<ParticleExtra> extra) {
		super(version, versionComparison, dataName, extra);
		this.colorable = extra != null && extra.contains(ParticleExtra.COLORABLE);
		this.musicNote = extra != null && extra.contains(ParticleExtra.MUSIC_NOTE);
	}

	// get
	public boolean isColorable() {
		return colorable;
	}

	public boolean isMusicNote() {
		return musicNote;
	}

	// object
	@Override
	public ParticleData clone() {
		return new ParticleData(getVersion(), getVersionComparison(), getDataName(), getExtra().isEmpty() ? null : CollectionUtils.asList(getExtra()));
	}

	// extra
	public static enum ParticleExtra {
		COLORABLE, MUSIC_NOTE;
	}

}
