package com.guillaumevdn.gcore.lib.compatibility.particle;

import java.util.Collection;
import com.guillaumevdn.gcore.lib.object.Optional;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.compatibility.variants.Variant;

/**
 * @author GuillaumeVDN
 */
public class Particle extends Variant<ParticleData> {

	public Particle(String id, ParticleData data) {
		super(id, data);
	}

	// object
	@Override
	public Particle clone() {
		return new Particle(getId(), getData().clone());
	}

	// methods
	public void send(Player player, Location location) {
		send(player, location, null, 1, 0f);
	}

	public void send(Player player, Location location, int count) {
		send(player, location, null, count, 0f);
	}

	public void send(Player player, Location location, int count, float speed) {
		send(player, location, null, count, speed);
	}

	public void send(Player player, Location location, Color color, int count, float speed) {
		send(player, location, color, count, speed);
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
		ParticleCompat.sendParticle(players, location, this, color, noteColor, count, speed);
	}

	// static
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
