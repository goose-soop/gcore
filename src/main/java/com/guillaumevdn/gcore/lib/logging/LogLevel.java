package com.guillaumevdn.gcore.lib.logging;

import org.bukkit.ChatColor;

/**
 * @author GuillaumeVDN
 */
public enum LogLevel {

	INFO("info", "§a"), WARNING("WARNING", "§e"), ERROR("ERROR", "§c"), DEBUG("DEBUG", "§d"),;

	private String filePrefix;
	private String consoleColor;

	LogLevel(String filePrefix, String consoleColor) {
		this.filePrefix = filePrefix;
		this.consoleColor = consoleColor;
	}

	// ----- get
	public String getFilePrefix() {
		return filePrefix;
	}

	public String getConsoleColor() {
		return consoleColor;
	}

	// ----- set
	public void setConsoleColor(ChatColor color) {
		if (color != null) {
			this.consoleColor = "§" + color.getChar();
		}
	}

}
