package com.guillaumevdn.gcore.lib.compatibility.particle;

import java.util.Collection;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.variants.Variant;
import com.guillaumevdn.gcore.lib.object.Optional;

/**
 * @author GuillaumeVDN
 */
public class Particle extends Variant<ParticleData> {

	public Particle(String id, ParticleData data) {
		super(id, data);
	}

	// ----- object
	@Override
	public Particle clone() {
		return new Particle(getId(), getData().clone());
	}

	// ----- methods
	public void send(Player player, Location location) {
		send(player, location, null, null, 1, 0f);
	}

	public void send(Player player, Location location, int count) {
		send(player, location, null, null, count, 0f);
	}

	public void send(Player player, Location location, int count, float speed) {
		send(player, location, null, null, count, speed);
	}
	
	public void send(Player player, Location location, float offx, float offy, float offz) {
		send(CollectionUtils.asList(player), location, null, 1f, null, 1, 0f, offx, offy, offz);
	}

	public void send(Player player, Location location, Color color, Integer noteColor, int count, float speed) {
		send(CollectionUtils.asList(player), location, color, noteColor, count, speed);
	}

	public void send(Player player, Location location, Color color, Float redstoneColorScale, Integer noteColor, int count, float speed) {
		send(CollectionUtils.asList(player), location, color, redstoneColorScale, noteColor, count, speed);
	}

	public void send(Collection<Player> players, Location location) {
		send(players, location, null, null, 1, 0f);
	}

	public void send(Collection<Player> players, Location location, int count) {
		send(players, location, null, null, count, 0f);
	}

	public void send(Collection<Player> players, Location location, int count, float speed) {
		send(players, location, null, null, count, speed);
	}

	public void send(Collection<Player> players, Location location, Color color, Integer noteColor, int count, float speed) {
		send(players, location, color, 1f, noteColor, count, speed);
	}

	public void send(Collection<Player> players, Location location, Color color, Float redstoneColorScale, Integer noteColor, int count, float speed) {
		send(players, location, color, redstoneColorScale, noteColor, count, speed, 0f, 0f, 0f);
	}

	public void send(Collection<Player> players, Location location, Color color, Float redstoneColorScale, Integer noteColor, int count, float speed, float offx, float offy, float offz) {
		ParticleCompat.sendParticle(players, location, this, color, redstoneColorScale, noteColor, count, speed, offx, offy, offz);
	}

	// ----- static
	public static Collection<Particle> values() {
		return ConfigGCore.particles.values();
	}

	public static Optional<Particle> fromId(String string) {
		return ConfigGCore.particles.fromId(string);
	}

	public static Optional<Particle> firstFromIdOrDataName(String string) {
		return ConfigGCore.particles.fromIdOrDataName(string);
	}

}
