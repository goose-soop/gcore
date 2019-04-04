package com.guillaumevdn.gcore.lib.versioncompat.particle;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.util.ServerVersion;
import com.guillaumevdn.gcore.lib.util.Utils;

public class ParticleManager1_9 implements ParticleManager {

	public void send(ParticleManager.Type type, Location loc, float speed, int count, Collection<Player> players) {
		Particle particleType = Utils.valueOfOrNull(Particle.class, type.getName());
		if (particleType == null) {
			GCore.inst().warning("Trying to display particle of type " + type.toString() + " but it's most likely not supported on this server version (" + ServerVersion.CURRENT.getName() + ")");
			return;
		}

		for (Player pl : players) {
			pl.spawnParticle(particleType, loc, count, 0F, 0F, 0F, speed, null);
		}
	}

	public void send(ParticleManager.Type type, Location loc, float speed, int count, Player player) {
		send(type, loc, speed, count, Utils.asList(player));
	}

	public void send(ParticleManager.Type type, Location loc, float speed, int count, World world) {
		send(type, loc, speed, count, new ArrayList<Player>(world.getPlayers()));
	}

	public void sendColor(ParticleManager.Type type, Location loc, float speed, int count, Color color, Collection<Player> players) {
		Particle particleType = Utils.valueOfOrNull(Particle.class, type.getName());
		if (particleType == null) {
			GCore.inst().warning("Trying to display particle of type " + type.toString() + " but it's most likely not supported on this server version (" + ServerVersion.CURRENT.getName() + ")");
			return;
		}

		for (Player pl : players) {
			pl.spawnParticle(particleType, loc, count, 0F, 0F, 0F, speed);
		}
	}

	public void sendColor(ParticleManager.Type type, Location loc, float speed, int count, Color color, Player player) {
		sendColor(type, loc, speed, count, color, Utils.asList(player));
	}

	public void sendColor(ParticleManager.Type type, Location loc, float speed, int count, Color color, World world) {
		sendColor(type, loc, speed, count, color, Utils.asList(world.getPlayers()));
	}

}
