package com.guillaumevdn.gcore.lib.plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public final class PluginUtils {

	// ----- plugins
	public static boolean isPluginEnabled(String plugin) {
		for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
			if (pl.getName().equalsIgnoreCase(plugin)) {
				return pl.isEnabled();
			}
		}
		return false;
	}

	public static Plugin getPlugin(String plugin) {
		for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
			if (pl.getName().equalsIgnoreCase(plugin)) {
				return pl;
			}
		}
		return null;
	}

	// ----- gplugins
	public static List<GPlugin> getGPlugins() {
		List<GPlugin> result = new ArrayList<>();
		for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			GPlugin gplugin = ObjectUtils.castOrNull(plugin, GPlugin.class);
			if (gplugin != null) {
				result.add(gplugin);
			}
		}
		return result;
	}

	public static GPlugin getGPlugin(String plugin) {
		for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
			if (pl.getName().equalsIgnoreCase(plugin)) {
				return ObjectUtils.castOrNull(pl, GPlugin.class);
			}
		}
		return null;
	}

	public static boolean isGPluginActivated(String plugin) {
		GPlugin pl = getGPlugin(plugin);
		return pl != null && pl.isEnabled() && pl.isActivated();
	}

	public static void ifActiveGPlugin(String plugin, Runnable runnable) {
		if (isGPluginActivated(plugin)) {
			runnable.run();
		}
	}

	// ----- update
	public static String getOfficialVersion(GPlugin plugin) {
		try {
			// connect
			URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + plugin.getSpigotResourceId());
			HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			// read
			String response = null;
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream()))) {
				response = reader.readLine();
			}
			return response != null ? response : "unknown_server";
		} catch (Throwable exception) {
			exception.printStackTrace();
			return "unknown_server";
		}
	}

}
