package com.guillaumevdn.gcore.lib.compatibility.sound;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.compatibility.variants.Variant;
import com.guillaumevdn.gcore.lib.object.Optional;

/**
 * @author GuillaumeVDN
 */
public class Sound extends Variant<SoundData> {

	public Sound(String id, SoundData data) {
		super(id, data);
	}

	// ----- object
	@Override
	public Sound clone() {
		return new Sound(getId(), getData().clone());
	}

	// ----- play
	public void play(Object target) {
		play(target, null);
	}

	public void play(Object target, Location forceLocation) {
		play(target, 1F, 1F, forceLocation);
	}

	public void play(Object target, float volume, float pitch) {
		play(target, volume, pitch, null);
	}

	public void play(Object target, float volume, float pitch, Location forceLocation) {
		if (target instanceof Collection<?>) {
			for (Object sub : ((Collection<?>) target)) {
				play(sub, volume, pitch, forceLocation);
			}
		} else if (target instanceof OfflinePlayer) {
			Player player = ((OfflinePlayer) target).getPlayer();
			if (player != null) player.playSound(forceLocation != null ? forceLocation : player.getLocation(), getData().getDataInstance(), volume, pitch);
		} else if (target instanceof UUID) {
			Player player = Bukkit.getPlayer((UUID) target);
			if (player != null) player.playSound(forceLocation != null ? forceLocation : player.getLocation(), getData().getDataInstance(), volume, pitch);
		}
	}

	// ----- static
	public static Collection<Sound> values() {
		return ConfigGCore.sounds.values();
	}

	public static Optional<Sound> fromId(String string) {
		return ConfigGCore.sounds.fromId(string);
	}

	public static Optional<Sound> firstFromIdOrDataName(String string) {
		return ConfigGCore.sounds.fromIdOrDataName(string);
	}

}
