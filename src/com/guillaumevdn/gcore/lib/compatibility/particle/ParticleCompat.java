package com.guillaumevdn.gcore.lib.compatibility.particle;

import java.util.Collection;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionEnum;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureOctoConsumer;

/**
 * @author GuillaumeVDN
 */
public final class ParticleCompat {

	// send particle
	private static final int[] emptyData = new int[0];
	private static final ReflectionProcedureOctoConsumer<Collection<Player>, Location, Particle, Color, Float, Integer, Integer, Float> SEND_PARTICLE = new ReflectionProcedureOctoConsumer<Collection<Player>, Location, Particle, Color, Float, Integer, Integer, Float>()
			.setIf(Version.ATLEAST_1_13, (players, location, particle, color, redstoneColorScale, noteColor, count, speed) -> {
				ReflectionEnum particleEnum = Reflection.getEnum("org.bukkit.Particle");
				ReflectionObject particleBukkit = particleEnum.safeValueOf(particle.getData().getDataName());
				if (particleBukkit == null) {
					GCore.inst().getMainLogger().warning("Couldn't display unknown particle " + particle.getData().getDataName() + " (registered as " + particle.getId() + ")");
				} else {
					// note
					if (particle.getData().isMusicNote()) {
						float note = NumberUtils.isInRange(noteColor, 0, 24) ? (float) noteColor : NumberUtils.random(0f, 24f);
						players.forEach(player -> {
							player.spawnParticle(particleBukkit.get(), location.getX(), location.getY(), location.getZ(), count, 0f, 0f, 0f, note);
						});
					}
					// redstone
					else if (particle.getData().getDataName().equals("REDSTONE")) {
						org.bukkit.Particle.DustOptions data = new org.bukkit.Particle.DustOptions(color, redstoneColorScale);
						players.forEach(player -> {
							player.spawnParticle(particleBukkit.get(), location.getX(), location.getY(), location.getZ(), 0, (0f), (0f), (0f), 0f, data);
						});
					}
					// not a note
					else {
						boolean useColor = color != null && particle.getData().isColorable();
						players.forEach(player -> {
							player.spawnParticle(particleBukkit.get(), location.getX(), location.getY(), location.getZ(),
									useColor ? 0 : count,
											(!useColor ? 0f : color.getRed()), (!useColor ? 0f : color.getGreen()), (!useColor ? 0f : color.getBlue()),
											useColor ? 1f : speed);
						});

					}
				}
			})
			.orIf(Version.ATLEAST_1_8, (players, location, particle, color, redstoneColorScale, noteColor, count, speed) -> {
				ReflectionEnum particleEnum = Reflection.getNmsEnum("EnumParticle");
				ReflectionObject particleNms = particleEnum.safeValueOf(particle.getData().getDataName());
				if (particleNms == null) {
					GCore.inst().getMainLogger().warning("Couldn't display unknown particle " + particle.getData().getDataName() + " (registered as " + particle.getId() + ")");
				} else {
					// note
					if (particle.getData().isMusicNote()) {
						float note = NumberUtils.isInRange(noteColor, 0, 24) ? (float) noteColor : NumberUtils.random(0f, 24f);
						Reflection.sendNmsPacket(players, "PacketPlayOutWorldParticles",
								particleNms.get(),
								true,
								(float) location.getX(), (float) location.getY(), (float) location.getZ(),
								0f, 0f, 0f,
								note, count, emptyData
								);
					}
					// not a note
					else {
						boolean useColor = color != null && particle.getData().isColorable();
						Reflection.sendNmsPacket(players, "PacketPlayOutWorldParticles",
								particleNms.get(),
								true,
								(float) location.getX(), (float) location.getY(), (float) location.getZ(),
								(!useColor ? 0f : color.getRed()), (!useColor ? 0f : color.getGreen()), (!useColor ? 0f : color.getBlue()),
								useColor ? 1f : speed, useColor ? 0 : count, emptyData
								);
					}
				}
			})
			.orElse((players, location, particle, color, redstoneColorScale, noteColor, count, speed) -> {
				// note
				if (particle.getData().isMusicNote()) {
					float note = NumberUtils.isInRange(noteColor, 0, 24) ? (float) noteColor : NumberUtils.random(0f, 24f);
					Reflection.sendNmsPacket(players, "PacketPlayOutWorldParticles",
							particle.getData().getDataName(),
							(float) location.getX(), (float) location.getY(), (float) location.getZ(),
							0f, 0f, 0f,
							note, count
							);
				}
				// not a note
				else {
					boolean useColor = color != null && particle.getData().isColorable();
					Reflection.sendNmsPacket(players, "PacketPlayOutWorldParticles",
							particle.getData().getDataName(),
							(float) location.getX(), (float) location.getY(), (float) location.getZ(),
							(!useColor ? 0f : color.getRed()), (!useColor ? 0f : color.getGreen()), (!useColor ? 0f : color.getBlue()),
							speed, count
							);
				}
			});

	public static void sendParticle(Collection<Player> players, Location location, Particle particle, Color color, Integer noteColor, int count, float speed) {
		sendParticle(players, location, particle, color, 1f, noteColor, count, speed);
	}

	public static void sendParticle(Collection<Player> players, Location location, Particle particle, Color color, Float redstoneColorScale, Integer noteColor, int count, float speed) {
		SEND_PARTICLE.process(players, location, particle, color, redstoneColorScale, noteColor, count, speed);
	}

}
