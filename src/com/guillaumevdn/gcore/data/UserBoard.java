package com.guillaumevdn.gcore.data;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.data.DataBoard;
import com.guillaumevdn.gcore.lib.data.DataManager.Callback;
import com.guillaumevdn.gcore.lib.data.mysql.Query;
import com.guillaumevdn.gcore.lib.event.UserDataProfileChangedEvent;
import com.guillaumevdn.gcore.lib.util.Utils;

public class UserBoard extends DataBoard<PCUser> implements Listener {

	// fields
	private Map<UserInfo, PCUser> cache = new HashMap<UserInfo, PCUser>();

	// get
	public Map<UserInfo, PCUser> getCache() {
		return Collections.unmodifiableMap(cache);
	}

	@Override
	public PCUser getElement(Object param) {
		if (param instanceof OfflinePlayer) {
			return cache.get(new UserInfo((OfflinePlayer) param));
		} else if (param instanceof UUID) {
			return cache.get(new UserInfo((UUID) param));
		} else if (param instanceof UserInfo) {
			return cache.get((UserInfo) param);
		}
		throw new IllegalArgumentException("param type " + param.getClass() + " isn't allowed");
	}

	// methods
	public void pullOnline() {
		// get users
		final List<PCUser> toPull = new ArrayList<PCUser>();
		for (Player pl : Utils.getOnlinePlayers()) {
			UserInfo info = new UserInfo(pl);
			PCUser user = cache.get(info);
			if (user == null) cache.put(info, user = new PCUser(info));// add to cache
			toPull.add(user);// add to pull list
		}
		// pull users
		pullAsync(toPull, null);
	}

	/**
	 * Loads an user, overwriting the current cached data if any is present.
	 * @param info the user info
	 */
	public void loadUser(UserInfo info, Callback callback) {
		// get user or add to cache
		PCUser user;
		if (cache.containsKey(info)) {
			user = cache.get(info);
		} else {
			cache.put(info, user = new PCUser(info));
		}
		// pull user
		user.pullAsync(callback);
	}

	/**
	 * Use this with caution, you shouldn't unload an online player. This is called automatically when an user switches profile.
	 * @param info the user info
	 */
	public void unloadUser(UserInfo info) {
		// loaded
		PCUser user = cache.remove(info);
		if (user != null) {
			// despawn npcs if online
			Player player = info.toPlayer();
			if (player != null) {
				GCore.inst().getNpcManager().removeNpcs(player);
			}
		}
	}

	// events
	@EventHandler(priority = EventPriority.LOWEST)
	public void event(PlayerJoinEvent event) {
		loadUser(new UserInfo(event.getPlayer()), null);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void event(UserDataProfileChangedEvent event) {
		// unload
		UserInfo user = event.getUser();
		unloadUser(user);
		// load if online
		if (user.toPlayer() != null) {
			loadUser(user, null);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void event(PlayerQuitEvent event) {
		unloadUser(new UserInfo(event.getPlayer()));
	}

	// data
	@Override
	public PCDataManager getDataManager() {
		return GCore.inst().getData();
	}

	@Override
	protected final File getJsonFile(PCUser element) {
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
