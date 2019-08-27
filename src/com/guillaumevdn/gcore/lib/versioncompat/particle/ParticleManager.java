package com.guillaumevdn.gcore.lib.versioncompat.particle;

import java.util.Collection;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.util.ServerVersion;
import com.guillaumevdn.gcore.lib.util.Utils;

public interface ParticleManager {

	// ------------------------------------------------------------
	// Particle types
	// ------------------------------------------------------------

	public enum Type {

		EXPLOSION_NORMAL("explode", "EXPLOSION_NORMAL"),
		EXPLOSION_LARGE("largeexplode", "EXPLOSION_LARGE"),
		EXPLOSION_HUGE("hugeexplosion", "EXPLOSION_HUGE"),
		FIREWORKS_SPARK("fireworksSpark", "FIREWORKS_SPARK"),
		WATER_BUBBLE("bubble", "WATER_BUBBLE"),
		WATER_SPLASH("splash", "WATER_SPLASH"),
		WATER_WAKE("wake", "WATER_WAKE"),
		SUSPENDED("suspended", "SUSPENDED"),
		SUSPENDED_DEPTH("depthsuspend", "SUSPENDED_DEPTH"),
		CRIT("crit", "CRIT"),
		CRIT_MAGIC("magicCrit", "CRIT_MAGIC"),
		SMOKE_NORMAL("smoke", "SMOKE_NORMAL"),
		SMOKE_LARGE("largesmoke", "SMOKE_LARGE"),
		SPELL("spell", "SPELL"),
		SPELL_INSTANT("instantSpell", "SPELL_INSTANT"),
		SPELL_MOB("mobSpell", "SPELL_MOB"),
		SPELL_MOB_AMBIENT("mobSpellAmbient", "SPELL_MOB_AMBIENT"),
		SPELL_WITCH("witchMagic", "SPELL_WITCH"),
		DRIP_WATER("dripWater", "DRIP_WATER"),
		DRIP_LAVA("dripLava", "DRIP_LAVA"),
		VILLAGER_ANGRY("angryVillager", "VILLAGER_ANGRY"),
		VILLAGER_HAPPY("happyVillager", "VILLAGER_HAPPY"),
		TOWN_AURA("townaura", "TOWN_AURA"),
		NOTE("note", "NOTE"),
		PORTAL("portal", "PORTAL"),
		ENCHANTMENT_TABLE("enchantmenttable", "ENCHANTMENT_TABLE"),
		FLAME("flame", "FLAME"),
		LAVA("lava", "LAVA"),
		CLOUD("cloud", "CLOUD"),
		REDSTONE("reddust", "REDSTONE"),
		SNOWBALL("snowballpoof", "SNOWBALL"),
		SNOW_SHOVEL("snowshovel", "SNOW_SHOVEL"),
		SLIME("slime", "SLIME"),
		HEART("heart", "HEART"),
		BARRIER("barrier", "BARRIER"),
		WATER_DROP("droplet", "WATER_DROP"),
		MOB_APPEARANCE("", "MOB_APPEARANCE"), 
		DRAGON_BREATH("", "DRAGON_BREATH"), 
		END_ROD("", "END_ROD"), 
		DAMAGE_INDICATOR("", "DAMAGE_INDICATOR"), 
		SWEEP_ATTACK("", "SWEEP_ATTACK"), 
		TOTEM("", "TOTEM"), 
		SPIT("", "SPIT"), 
		SQUID_INK("", "SPIT"), 
		BUBBLE_POP("", "BUBBLE_POP"), 
		CURRENT_DOWN("", "CURRENT_DOWN"), 
		BUBBLE_COLUMN_UP("", "BUBBLE_COLUMN_UP"), 
		NAUTILUS("", "NAUTILUS"), 
		DOLPHIN("", "DOLPHIN")
		;

		private final String name_1_7, name_1_8;

		private Type(String n17, String n18) {
			this.name_1_7 = n17;
			this.name_1_8 = n18;
		}

		public String getName() {
			if (ServerVersion.CURRENT.isOrLess(ServerVersion.MC_1_7_10)) {
				return name_1_7;
			}
			return name_1_8;
		}

		public boolean hasColor() {
			return equals(SPELL_MOB) || equals(SPELL_MOB_AMBIENT) || equals(REDSTONE);
		}

		public static Type from(String name) {
			if (name == null) return null;
			for (Type type : values()) {
				if (type.toString().equalsIgnoreCase(name)) {
					return type;
				}
			}
			return null;
		}

	}

	// ------------------------------------------------------------
	// Instance
	// ------------------------------------------------------------

	public ParticleManager INSTANCE = Utils.createParticleManagerCompat();

	// ------------------------------------------------------------
	// Abstract
	// ------------------------------------------------------------

	public void send(ParticleManager.Type type, Location loc, float speed, int count, Collection<Player> players);
	public void send(ParticleManager.Type type, Location loc, float speed, int count, Player player);
	public void send(ParticleManager.Type type, Location loc, float speed, int count, World world);
	public void sendColor(ParticleManager.Type type, Location loc, float speed, int count, Color color, Collection<Player> players);
	public void sendColor(ParticleManager.Type type, Location loc, float speed, int count, Color color, Player player);
	public void sendColor(ParticleManager.Type type, Location loc, float speed, int count, Color color, World world);

}
