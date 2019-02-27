package be.pyrrh4.pyrcore.lib.versioncompat;

import java.io.IOException;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.util.Utils;

public abstract class Compat {

	// ------------------------------------------------------------
	// Instance
	// ------------------------------------------------------------

	public static final Compat INSTANCE = Utils.createCompat();

	public void init() {
	}

	// ------------------------------------------------------------
	// Abstract
	// ------------------------------------------------------------

	// optional
	public void sendTitle(Player player, String title, String subtitle, int fadeIn, int duration, int fadeOut) {}
	public void sendActionBar(Player player, String message) {}
	public void changeTab(Player player, String head, String foot) {}

	public ItemMeta addItemFlags(ItemMeta meta) {
		return meta;
	}

	public Mat getArmorStandHelmetType(Entity armorStand) {
		return null;
	}

	// vary
	public abstract Score getScore(Objective objective, String name);
	public abstract ItemStack buildPotion(PotionType type, int level, boolean extended, boolean splash);
	public abstract PotionData getPotionData(ItemStack item);

	public abstract String serializeNbt(Object nbt) throws IOException;
	public abstract Object unserializeNbt(String serialized) throws IOException;
	public abstract Object getNbt(ItemStack item);
	public abstract ItemStack setNbt(ItemStack item, Object nbt);

	public void setScoreboardTeamNameTags(Team team, String prefix, String suffix) {}
	public abstract Enchantment getEnchantment(String raw);

	// ------------------------------------------------------------
	// PotionData
	// ------------------------------------------------------------

	public static class PotionData
	{
		private PotionType type;
		private int level;
		private boolean extended, splash;

		public PotionData(PotionType type, int level, boolean extended, boolean splash) {
			this.type = type;
			this.level = level;
			this.extended = extended;
			this.splash = splash;
		}

		public PotionType getType() {
			return type;
		}

		public int getLevel() {
			return level;
		}

		public boolean isExtended() {
			return extended;
		}

		public boolean isSplash() {
			return splash;
		}
	}

}
