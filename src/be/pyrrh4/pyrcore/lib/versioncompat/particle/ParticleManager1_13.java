package be.pyrrh4.pyrcore.lib.versioncompat.particle;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.util.ServerVersion;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class ParticleManager1_13 implements ParticleManager {

	public void send(ParticleManager.Type type, Location loc, float speed, int count, Collection<Player> players) {
		Particle particleType = Utils.valueOfOrNull(Particle.class, type.getName());
		if (particleType == null) {
			PyrCore.inst().warning("Trying to display particle of type " + type.toString() + " but it's most likely not supported on this server version (" + ServerVersion.CURRENT.getName() + ")");
			return;
		}

		if (particleType.getDataType().getName().contains("DustOptions")) {
			Object data = getDustData(null);
			for (Player pl : players) {
				try {
					pl.spawnParticle(particleType, loc, count, 0F, 0F, 0F, speed, data);
				} catch (Throwable exception) {
					exception.printStackTrace();
					PyrCore.inst().error("Couldn't display particle " + particleType.toString());
				}
			}
		} else {
			for (Player pl : players) {
				pl.spawnParticle(particleType, loc, count, 0F, 0F, 0F, speed, null);
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
			PyrCore.inst().warning("Trying to display particle of type " + type.toString() + " but it's most likely not supported on this server version (" + ServerVersion.CURRENT.getName() + ")");
			return;
		}
		for (Player pl : players) {
			pl.spawnParticle(particleType, loc, count, 0F, 0F, 0F, speed, getDustData(color));
		}
	}

	public void sendColor(ParticleManager.Type type, Location loc, float speed, int count, Color color, Player player) {
		sendColor(type, loc, speed, count, color, Utils.asList(player));
	}

	public void sendColor(ParticleManager.Type type, Location loc, float speed, int count, Color color, World world) {
		sendColor(type, loc, speed, count, color, Utils.asList(world.getPlayers()));
	}

}
