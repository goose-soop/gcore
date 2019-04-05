package com.guillaumevdn.gcore.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.util.Utils;

/**
 * Represents a GCore user with a data profile - this is not cached and should not be cached except if you want to store a specific profile.
 */
public class UserInfo {

	// base
	private UUID uuid;
	private String profile;

	public UserInfo(OfflinePlayer player) {
		this(player.getUniqueId());
	}

	public UserInfo(UUID uuid) {
		this(uuid, GCore.inst().getData().getDataProfiles().get(uuid));
	}

	public UserInfo(Player player, String profile) {
		this(player.getUniqueId(), profile);
	}

	public UserInfo(UUID uuid, String profile) {
		this.uuid = uuid;
		this.profile = profile;
	}

	// get
	public UUID getUniqueId() {
		return uuid;
	}

	public String getProfile() {
		return profile;
	}

	public OfflinePlayer toOfflinePlayer() {
		return Utils.getOfflinePlayer(uuid);
	}

	public Player toPlayer() {
		return Utils.getPlayer(uuid);
	}

	public boolean isCurrentProfile() {
		return GCore.inst().getData().getDataProfiles().get(uuid).equals(profile);
	}

	// methods
	public void sendMessage(String message) {
		Player player = toPlayer();
		if (player != null) {
			player.sendMessage(message);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(Utils.instanceOf(obj, UserInfo.class))) {
			return false;
		}
		UserInfo other = (UserInfo) obj;
		return uuid.equals(other.uuid) && profile.equals(other.profile);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((profile == null) ? 0 : profile.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return uuid.toString() + "_" + profile;
	}

	public String toStringName() {
		return toOfflinePlayer().getName() + "_" + profile;
	}

	// methods
	public static List<Player> getOnlinePlayers(Collection<UserInfo> users) {
		List<Player> result = new ArrayList<Player>();
		for (UserInfo user : users) {
			if (user.isCurrentProfile()) {
				Player player = user.toPlayer();
				if (player != null) {
					result.add(player);
				}
			}
		}
		return result;
	}

	public static List<String> getPlayerNames(Collection<UserInfo> users) {
		List<String> result = new ArrayList<String>();
		for (UserInfo user : users) {
			if (user.isCurrentProfile()) {
				OfflinePlayer player = user.toOfflinePlayer();
				if (player != null) {
					result.add(player.getName());
				}
			}
		}
		return result;
	}

	public static List<UserInfo> getUsers(Collection<Player> players) {
		List<UserInfo> result = new ArrayList<UserInfo>();
		for (Player player : players) {
			result.add(new UserInfo(player));
		}
		return result;
	}

	public static UserInfo fromString(String pcUserRaw) {
		String[] split = pcUserRaw.split("_");
		return new UserInfo(UUID.fromString(split[0]), split[1]);
	}

}
