package be.pyrrh4.pyrcore.lib;

import org.bukkit.Bukkit;

import be.pyrrh4.pyrcore.PyrCore;

public class Logger {

	// ------------------------------------------------------------
	// Logger level
	// ------------------------------------------------------------

	public enum Level {
		INFO("§f", "§f§l", "INFO: "),
		SUCCESS("§d", "§d§l", "SUCCESS: "),
		WARNING("§e", "§6", "WARNING: "),
		DEBUG("§5", "§d", "DEBUG: "),
		SEVERE("§c", "§4", "ERROR: ");

		private String color, varColor, prefix;

		private Level(String color, String varColor, String prefix) {
			this.color = color;
			this.varColor = varColor;
			this.prefix = prefix;
		}

		public String getColor() {
			return color;
		}

		public String getVarColor() {
			return varColor;
		}

		public String getPrefix() {
			return prefix;
		}
	}

	// ------------------------------------------------------------
	// Log
	// ------------------------------------------------------------

	public static void log(Level level, PyrPlugin plugin, String message) {
		log(level, plugin, message, true);
	}

	public static void log(Level level, PyrPlugin plugin, String message, boolean color) {
		log(level, plugin.getName(), message, color);
	}

	public static void log(Level level, String prefix, String message) {
		log(level, prefix, message, true);
	}

	public static void log(Level level, String prefix, String message, boolean color) {
		if (level.equals(Level.DEBUG) && !(PyrCore.inst().getConfiguration() == null ? true : PyrCore.inst().getConfiguration().getBoolean("show_debug", true))) {
			return;
		}
		Bukkit.getConsoleSender().sendMessage((color ? level.getColor() : "") + "[" + prefix + "] " + level.getPrefix() + message.replace("<", level.getVarColor()).replace(">", level.getColor()));
	}

}
