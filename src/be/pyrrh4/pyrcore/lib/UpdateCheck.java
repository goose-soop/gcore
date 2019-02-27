package be.pyrrh4.pyrcore.lib;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.Logger.Level;
import be.pyrrh4.pyrcore.lib.messenger.MessageSpecial;
import be.pyrrh4.pyrcore.lib.messenger.Messenger;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class UpdateCheck {

	public static void notify(final List<Player> players) {
		new BukkitRunnable() {
			@Override
			public void run() {
				// check plugins
				final List<PyrPlugin> unknownServer = new ArrayList<PyrPlugin>();
				final List<PyrPlugin> unregistered = new ArrayList<PyrPlugin>();
				final Map<PyrPlugin, String> outdated = new HashMap<PyrPlugin, String>();
				for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
					if (pl.isEnabled() && pl instanceof PyrPlugin) {
						PyrPlugin plugin = (PyrPlugin) pl;
						if (plugin.spigotResourceId > 0) {
							String officialVersion = getVersionRelatedCharacters(getLatestOfficial(plugin));
							// errors
							if (officialVersion.equals("?resource=id") || officialVersion.equals("Invalid resource")) {
								unregistered.add(plugin);
							} else if (officialVersion.isEmpty() || officialVersion.equals("unknown_server")) {
								unknownServer.add(plugin);
							} else {
								String currentVersion = getVersionRelatedCharacters(plugin.getDescription().getVersion());
								// is an update
								if (!currentVersion.equals(officialVersion)) {
									outdated.put(plugin, officialVersion);
								}
							}
						}
					}
				}

				// sync
				new BukkitRunnable() {
					@Override
					public void run() {
						// errors
						if (!unknownServer.isEmpty()) {
							Logger.log(Level.SEVERE, PyrCore.inst(), "Could not contact the update server for plugin" + Utils.getPlural(unregistered.size()) + " " + Utils.asNiceString(unknownServer, true) + ".");
						}
						if (!unregistered.isEmpty()) {
							Logger.log(Level.SEVERE, PyrCore.inst(), "Auto update isn't registered for " + Utils.getPluralFor("plugin", unregistered.size()) + " " + Utils.asNiceString(unregistered, true) + ". Please notify the author (pyrrh4).");
						}

						// notify outdated
						if (!outdated.isEmpty()) {
							// players
							if (!players.isEmpty()) {
								Messenger.send(players, Messenger.Level.SEVERE_INFO, PyrCore.inst().getName(), (outdated.size() > 1 ? "These plugins are" : "This plugin is") + " outdated :");
								for (PyrPlugin plugin : outdated.keySet()) {
									MessageSpecial special = new MessageSpecial();
									special.newJComp(Messenger.Level.SEVERE_INFO.format("", "<" + plugin.getName() + " (new is " + outdated.get(plugin) + ", current is " + getVersionRelatedCharacters(plugin.getDescription().getVersion()) + ")>")).build(special);
									special.newJComp(" §b§l[UPDATE]").addURL("https://www.spigotmc.org/resources/" + plugin.spigotResourceId + "/").build(special);
									special.send(players);
								}
							}
							// console
							Messenger.send(Bukkit.getConsoleSender(), Messenger.Level.SEVERE_INFO, PyrCore.inst().getName(), (outdated.size() > 1 ? "These plugins are" : "This plugin is") + " outdated :");
							Messenger.send(Bukkit.getConsoleSender(), Messenger.Level.SEVERE_INFO, PyrCore.inst().getName(), (outdated.size() > 1 ? "These plugins are" : "This plugin is") + " outdated : " + Utils.asNiceString(outdated.keySet(), true) + ".");
							Messenger.send(Bukkit.getConsoleSender(), Messenger.Level.SEVERE_INFO, PyrCore.inst().getName(), "Get the update" + Utils.getPlural(outdated.size()) + " here : <http://www.pyrrh4.be/plugins/list/>");
						}
					}
				}.runTask(PyrCore.inst());
			}
		}.runTaskAsynchronously(PyrCore.inst());
	}

	// ------------------------------------------------------------
	// Utils
	// ------------------------------------------------------------

	private static final List<Character> allowedCharacters = Utils.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.');

	private static String getVersionRelatedCharacters(String raw) {
		if (raw == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (char c : raw.toCharArray()) {
			if (allowedCharacters.contains(c)) {
				builder.append(c);
			}
		}
		return builder.toString();
	}

	private static String getLatestOfficial(PyrPlugin plugin) {
		try {
			URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + plugin.spigotResourceId);
			HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
			BufferedReader rd;

			conn.setDoOutput(true);

			if (conn.getResponseCode() == 200) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			String serverResponse = rd.readLine();

			if (serverResponse == null) {
				return "unknown_server";
			}

			return serverResponse;
		} catch (Throwable exception) {
			exception.printStackTrace();
			return "unknown_server";
		}
	}

}
