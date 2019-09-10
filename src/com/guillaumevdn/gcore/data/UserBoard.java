package com.guillaumevdn.gcore.data;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.data.DataBoard;
import com.guillaumevdn.gcore.lib.data.DataManager.BackEnd;
import com.guillaumevdn.gcore.lib.data.DataManager.Callback;
import com.guillaumevdn.gcore.lib.data.mysql.Query;
import com.guillaumevdn.gcore.lib.event.GUserPulledEvent;
import com.guillaumevdn.gcore.lib.event.GUsersPulledOnlineEvent;
import com.guillaumevdn.gcore.lib.event.UserDataProfileChangedEvent;
import com.guillaumevdn.gcore.lib.util.Utils;

public class UserBoard extends DataBoard<GUser> implements Listener {

	// fields
	private Map<UserInfo, GUser> cache = new HashMap<UserInfo, GUser>();

	// get
	public Map<UserInfo, GUser> getCache() {
		return Collections.unmodifiableMap(cache);
	}

	@Override
	public GUser getElement(Object param) {
		if (param instanceof OfflinePlayer) {
			return cache.get(new UserInfo((OfflinePlayer) param));
		} else if (param instanceof UUID) {
			return cache.get(new UserInfo((UUID) param));
		} else if (param instanceof UserInfo) {
			return cache.get(param);
		}
		throw new IllegalArgumentException("param type " + param.getClass() + " isn't allowed");
	}

	// methods
	public void pullOnline() {
		// get users
		final List<GUser> toPull = new ArrayList<GUser>();
		for (Player pl : Utils.getOnlinePlayers()) {
			UserInfo info = new UserInfo(pl);
			GUser user = cache.get(info);
			if (user == null) cache.put(info, user = new GUser(info));// add to cache
			toPull.add(user);// add to pull list
		}
		// pull users
		pullAsync(toPull, new Callback() {
			@Override
			public void callback() {
				Bukkit.getPluginManager().callEvent(new GUsersPulledOnlineEvent(!Bukkit.isPrimaryThread()));
			}
		});
	}

	/**
	 * Loads an user, overwriting the current cached data if any is present.
	 * @param info the user info
	 */
	public void loadUser(UserInfo info, final GUserPulledEvent.Context context, final Callback callback) {
		// get user or add to cache
		final GUser user;
		if (cache.containsKey(info)) {
			user = cache.get(info);
		} else {
			cache.put(info, user = new GUser(info));
		}
		// pull user
		user.pullAsync(new Callback() {
			@Override
			public void callback() {
				if (callback != null) callback.callback();
				Bukkit.getPluginManager().callEvent(new GUserPulledEvent(!Bukkit.isPrimaryThread(), user, context));
			}
		});
	}

	/**
	 * Use this with caution, you shouldn't unload an online player. This is called automatically when an user switches profile.
	 * @param info the user info
	 */
	public void unloadUser(UserInfo info) {
		// loaded
		GUser user = cache.remove(info);
		if (user != null) {
			// despawn npcs if online
			Player player = info.toPlayer();
			if (player != null) {
				if (GCore.inst().getNpcManager() != null) {
					GCore.inst().getNpcManager().removeNpcs(player);
				}
			}
		}
	}

	// events
	@EventHandler(priority = EventPriority.LOWEST)
	public void event(final PlayerJoinEvent event) {
		// ensure he has a profile
		final Player player = event.getPlayer();
		boolean created = false;
		boolean json = getDataManager().getBackEnd().equals(BackEnd.JSON);
		if (getDataManager().getDataProfiles().createDefaultProfile(player.getUniqueId(), !json)) {
			created = true;
		}
		if (created) {
			// log
			if (GCore.inst().getConfiguration() != null && GCore.inst().getConfiguration().getBoolean("show_data_debug", true)) {
				GCore.inst().debug("Created " + created + " new default player data profile");
			}
			// push data if json
			if (json) {
				getDataManager().getDataProfiles().pushAsync();
			}
		}
		// load user
		loadUser(new UserInfo(player), GUserPulledEvent.Context.JOIN, null);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void event(final UserDataProfileChangedEvent event) {
		// unload
		UserInfo user = event.getUser();
		unloadUser(user);
		// load if online
		if (user.toPlayer() != null) {
			loadUser(user, GUserPulledEvent.Context.PROFILE_CHANGE, null);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void event(PlayerQuitEvent event) {
		unloadUser(new UserInfo(event.getPlayer()));
	}

	// data
	@Override
	public GDataManager getDataManager() {
		return GCore.inst().getData();
	}

	@Override
	protected final File getJsonFile(GUser element) {
		return new File(GCore.inst().getUserDataRootFolder() + "/" + element.getDataId() + "/gcore_user.json");
	}

	@Override
	protected final void jsonPull() {
		throw new UnsupportedOperationException();// can't pull the whole user board
	}

	@Override
	protected final void jsonDelete() {
		File root = GCore.inst().getUserDataRootFolder();
		if (root.exists() && root.isDirectory()) {
			for (File userRoot : root.listFiles()) {
				if (userRoot.exists() && userRoot.isDirectory()) {
					File user = new File(userRoot.getPath() + "/questcreator_user.json");
					if (user.exists()) {
						user.delete();
					}
				}
			}
		}
	}

	// MySQL
	@Override
	protected final String getMySQLTable() {
		return "gcore_users";
	}

	@Override
	protected final Query getMySQLInitQuery() {
		return new Query(
				// create table if not exists
				"CREATE TABLE IF NOT EXISTS `" + getMySQLTable() + "`("
				+ "id VARCHAR(100) NOT NULL,"
				+ "data LONGTEXT NOT NULL,"
				+ "PRIMARY KEY(id)"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=?;", "utf8");
	}

	@Override
	protected final void mysqlPull() throws SQLException {
		throw new UnsupportedOperationException();// can't pull the whole user board
	}

	@Override
	protected final void mysqlDelete() {
		getDataManager().performMySQLUpdateQuery(new Query("DELETE FROM `" + getMySQLTable() + "`;"));
	}

}
