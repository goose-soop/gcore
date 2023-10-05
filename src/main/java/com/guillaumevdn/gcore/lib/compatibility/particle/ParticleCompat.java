package com.guillaumevdn.gcore.lib.compatibility.particle;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionEnum;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureUndeciConsumer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

/**
 * @author GuillaumeVDN
 */
public final class ParticleCompat
{

    // ----- send particle
    private static final int[] emptyData = new int[0];
    private static final ReflectionProcedureUndeciConsumer<Collection<Player>, Location, Particle, Color, Float, Integer, Integer, Float, Float, Float, Float> SEND_PARTICLE = new ReflectionProcedureUndeciConsumer<Collection<Player>, Location, Particle, Color, Float, Integer, Integer, Float, Float, Float, Float>()
        .setIf(Version.ATLEAST_1_13, (players, location, particle, color, redstoneColorScale, noteColor, count, speed, offx, offy, offz) -> {
            ReflectionEnum particleEnum = Reflection.getEnum("org.bukkit.Particle");
            ReflectionObject particleBukkit = particleEnum.safeValueOf(particle.getData().getDataName());
            if (particleBukkit == null) {
                GCore.inst().getMainLogger().warning("Couldn't display unknown particle " + particle.getData().getDataName() + " (registered as " + particle.getId() + ")");
            } else {
                // note
                if (particle.getData().isMusicNote()) {
                    float note = NumberUtils.isInRange(noteColor, 0, 24) ? (float) noteColor : NumberUtils.random(0f, 24f);
                    players.forEach(player -> player.spawnParticle(particleBukkit.get(), location.getX(), location.getY(), location.getZ(), count, offx, offy, offz, Optional.of(note)));
                }
                // redstone
                else if (particle.getData().getDataName().equals("REDSTONE")) {
                    org.bukkit.Particle.DustOptions data = new org.bukkit.Particle.DustOptions(color != null ? color : Color.fromRGB(NumberUtils.random(0, 255), NumberUtils.random(0, 255), NumberUtils.random(0, 255)), redstoneColorScale);
                    players.forEach(player -> {
                        player.spawnParticle(particleBukkit.get(), location.getX(), location.getY(), location.getZ(), 0, offx, offy, offz, 0f, data);
                    });
                }
                // not colorable
                else {
                    boolean useColor = color != null && particle.getData().isColorable();
                    players.forEach(player -> {
                        player.spawnParticle(particleBukkit.get(), location.getX(), location.getY(), location.getZ(),
                            useColor ? 0 : count,
                            (!useColor ? offx : color.getRed()), (!useColor ? offy : color.getGreen()), (!useColor ? offz : color.getBlue()),
                            useColor ? 1f : speed);
                    });

                }
            }
        })
        .orIf(Version.ATLEAST_1_8, (players, location, particle, color, redstoneColorScale, noteColor, count, speed, offx, offy, offz) -> {
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
                        offx, offy, offz,
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
                        (!useColor ? offx : color.getRed()), (!useColor ? offy : color.getGreen()), (!useColor ? offz : color.getBlue()),
                        useColor ? 1f : speed, useColor ? 0 : count, emptyData
                    );
                }
            }
        })
        .orElse((players, location, particle, color, redstoneColorScale, noteColor, count, speed, offx, offy, offz) -> {
            // note
            if (particle.getData().isMusicNote()) {
                float note = NumberUtils.isInRange(noteColor, 0, 24) ? (float) noteColor : NumberUtils.random(0f, 24f);
                Reflection.sendNmsPacket(players, "PacketPlayOutWorldParticles",
                    particle.getData().getDataName(),
                    (float) location.getX(), (float) location.getY(), (float) location.getZ(),
                    offx, offy, offz,
                    note, count
                );
            }
            // not a note
            else {
                boolean useColor = color != null && particle.getData().isColorable();
                Reflection.sendNmsPacket(players, "PacketPlayOutWorldParticles",
                    particle.getData().getDataName(),
                    (float) location.getX(), (float) location.getY(), (float) location.getZ(),
                    (!useColor ? offx : color.getRed()), (!useColor ? offy : color.getGreen()), (!useColor ? offz : color.getBlue()),
                    speed, count
                );
            }
        });

    public static void sendParticle(Collection<Player> players, Location location, Particle particle, Color color, Integer noteColor, int count, float speed, float offx, float offy, float offz) {
        sendParticle(players, location, particle, color, 1f, noteColor, count, speed, offx, offy, offz);
    }

    public static void sendParticle(Collection<Player> players, Location location, Particle particle, Color color, Float redstoneColorScale, Integer noteColor, int count, float speed, float offx, float offy, float offz) {
        SEND_PARTICLE.process(players, location, particle, color, redstoneColorScale, noteColor, count, speed, offx, offy, offz);
    }

}
