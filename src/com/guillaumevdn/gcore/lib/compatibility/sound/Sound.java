package com.guillaumevdn.gcore.lib.compatibility.sound;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.compatibility.variants.Variant;
import com.guillaumevdn.gcore.lib.location.Point;
import com.guillaumevdn.gcore.lib.object.Optional;
import com.guillaumevdn.gcore.lib.player.PlayerUtils;

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
		play(target, 1f, 1f, forceLocation);
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
			final Player player = PlayerUtils.getOnline((OfflinePlayer) target);
			if (player != null) player.playSound(forceLocation != null ? forceLocation : player.getLocation(), getData().getDataInstance(), volume, pitch);
		} else if (target instanceof UUID) {
			final Player player = Bukkit.getPlayer((UUID) target);
			if (player != null) player.playSound(forceLocation != null ? forceLocation : player.getLocation(), getData().getDataInstance(), volume, pitch);
		} else if (target instanceof Location) {
			final Location loc = (Location) target;
			play(loc.getWorld().getPlayers(), volume, pitch, loc);
		} else if (target instanceof Point) {
			final Point point = (Point) target;
			play(point.getWorld().getPlayers(), volume, pitch, point.toLocation());
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
