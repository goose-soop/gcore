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

public class ParticleManager1_13 implements ParticleManager {

	public void send(ParticleManager.Type type, Location loc, float speed, int count, Collection<Player> players) {
		Particle particleType = Utils.valueOfOrNull(Particle.class, type.getName());
		if (particleType == null) {
			GCore.inst().warning("Trying to display particle of type " + type.toString() + " but it's most likely not supported on this server version (" + ServerVersion.CURRENT.getName() + ")");
			return;
		}
		if (particleType.equals(Particle.NOTE)) {
			for (Player pl : players) {
				pl.spawnParticle(particleType, loc, count, speed, ((double) Utils.random(1, 24)) / 25d, 1);
			}
		} else {
			for (Player pl : players) {
				pl.spawnParticle(particleType, loc, count, speed, 0F, 0F, 0F);
			}
		}
	}

	private Object getDustData(Color color) {
		try {
			return Class.forName("org.bukkit.Particle$DustOptions").getConstructor(Color.class, float.class).newInstance(color == null ? Utils.getRandomBukkitColor() : color, 1F);
		} catch (Throwable ignored) {
			ignored.printStackTrace();
			return null;
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
		if (particleType.equals(Particle.REDSTONE)) {
			Object dust = getDustData(color);
			for (Player pl : players) {
				pl.spawnParticle(particleType, loc, count, speed, 0F, 0F, 0F, dust);
			}
		} else if (particleType.equals(Particle.SPELL_MOB) || particleType.equals(Particle.SPELL_MOB_AMBIENT)) {
			for (Player pl : players) {
				pl.spawnParticle(particleType, loc, count, speed, ((double) color.getRed()) / 255d, ((double) color.getGreen()) / 255d, ((double) color.getBlue()) / 255d);
			}
		} else {
			send(type, loc, speed, count, players);
		}
	}

	public void sendColor(ParticleManager.Type type, Location loc, float speed, int count, Color color, Player player) {
		sendColor(type, loc, speed, count, color, Utils.asList(player));
	}

	public void sendColor(ParticleManager.Type type, Location loc, float speed, int count, Color color, World world) {
		sendColor(type, loc, speed, count, color, Utils.asList(world.getPlayers()));
	}

}
