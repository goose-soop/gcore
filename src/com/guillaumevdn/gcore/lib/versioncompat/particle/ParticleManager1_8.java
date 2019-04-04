package com.guillaumevdn.gcore.lib.versioncompat.particle;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.util.Utils;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class ParticleManager1_8 implements ParticleManager {

	public void send(ParticleManager.Type type, Location loc, float speed, int count, Collection<Player> players)
	{
		float x = (float) loc.getX();
		float y = (float) loc.getY();
		float z = (float) loc.getZ();
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.valueOf(type.getName()), true, x, y, z, 0.0F, 0.0F, 0.0F, speed, count, null);

		for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
			((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public void send(ParticleManager.Type type, Location loc, float speed, int count, Player player)
	{
		send(type, loc, speed, count, Utils.asList(player));
	}

	public void send(ParticleManager.Type type, Location loc, float speed, int count, World world)
	{
		send(type, loc, speed, count, new ArrayList<Player>(world.getPlayers()));
	}

	public void sendColor(ParticleManager.Type type, Location loc, float speed, int count, Color color, Collection<Player> players)
	{
		float x = (float) loc.getX();
		float y = (float) loc.getY();
		float z = (float) loc.getZ();
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.valueOf(type.getName()), true, x, y, z, color.getRed(), color.getGreen(), color.getBlue(), speed, count, null);

		for (Player pl : players) {
			((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public void sendColor(ParticleManager.Type type, Location loc, float speed, int count, Color color, Player player) {
		sendColor(type, loc, speed, count, color, Utils.asList(player));
	}

	public void sendColor(ParticleManager.Type type, Location loc, float speed, int count, Color color, World world) {
		sendColor(type, loc, speed, count, color, Utils.asList(world.getPlayers()));
	}

}
