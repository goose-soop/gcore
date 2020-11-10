package com.guillaumevdn.gcore.lib.compatibility.particle;

import java.util.List;

import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.particle.ParticleData.ParticleExtra;
import com.guillaumevdn.gcore.lib.compatibility.variants.Variants;
import com.guillaumevdn.gcore.lib.logic.ComparisonType;

/**
 * @author GuillaumeVDN
 */
public final class Particles extends Variants<Particle, ParticleExtra, ParticleData> {

	public Particles(boolean regenerate) {
		super("particle", Particle.class, ParticleData.class, ParticleExtra.class, regenerate, false);
	}

	// load
	@Override
	public ParticleData loadElementConfigAndCreateData(Version version, ComparisonType comparison, List<ParticleExtra> extra, String rawData) throws Throwable {
		return new ParticleData(version, comparison, rawData, extra);
	}

	@Override
	protected Particle createElement(String id, ParticleData data) throws Throwable {
		return new Particle(id, data);
	}

}
