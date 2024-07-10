package com.guillaumevdn.gcore.lib.serialization.adapter.type;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.adapter.DataAdapter;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;

/**
 * @author GuillaumeVDN
 */
public final class AdapterPotionEffect extends DataAdapter<PotionEffect> {

	public static final AdapterPotionEffect INSTANCE = new AdapterPotionEffect();

	public AdapterPotionEffect() {
		super(PotionEffect.class, 1);
	}

	@Override
	public void write(PotionEffect effect, DataIO writer) throws Throwable {
		writer.write("type", effect.getType());
		writer.write("duration", effect.getDuration());
		writer.write("amplifier", effect.getAmplifier());
		if (effect.isAmbient())
			writer.write("ambient", effect.isAmbient());
		if (Version.ATLEAST_1_8 && effect.hasParticles()) {
			writer.write("particles", effect.hasParticles());
		}
		if (Version.ATLEAST_1_13 && effect.hasIcon()) {
			writer.write("icon", effect.hasIcon());
		}
	}

	@Override
	public PotionEffect read(int version, DataIO reader) throws Throwable {
		if (version == 1) {
			PotionEffectType type = ObjectUtils.potionEffectTypeOrNull(reader.readString("type"));
			Integer duration = reader.readInteger("duration");
			Integer amplifier = reader.readInteger("amplifier");
			Boolean ambient = reader.readBoolean("ambient");
			Boolean particles = reader.readBoolean("particles");
			Boolean icon = reader.readBoolean("icon");
			if (Version.ATLEAST_1_13) {
				return new PotionEffect(type, duration, amplifier, ambient != null && ambient, particles != null && particles, icon != null && icon);
			} else if (Version.ATLEAST_1_8) {
				return new PotionEffect(type, duration, amplifier, ambient != null && ambient, particles != null && particles);
			} else {
				return new PotionEffect(type, duration, amplifier, ambient != null && ambient);
			}
		}
		throw new IllegalArgumentException("unknown adapter version " + version);
	}

}
