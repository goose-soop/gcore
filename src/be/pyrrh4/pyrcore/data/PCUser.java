package be.pyrrh4.pyrcore.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.util.Utils;

/**
 * Represents a PyrCore user with a data profile - this is not cached and should not be cached except if you want to store a specific profile.
 */
public class PCUser {

	// base
	private UUID uuid;
	private String profile;

	public PCUser(OfflinePlayer player) {
		this(player.getUniqueId());
	}

	public PCUser(UUID uuid) {
		this(uuid, PyrCore.inst().getData().getDataProfiles().get(uuid));
	}

	public PCUser(Player player, String profile) {
		this(player.getUniqueId(), profile);
	}

	public PCUser(UUID uuid, String profile) {
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

	public OfflinePlayer getOfflinePlayer() {
		return Utils.getOfflinePlayer(uuid);
	}

	public Player getPlayer() {
		return Utils.getPlayer(uuid);
	}

	public boolean isCurrentProfile() {
		return PyrCore.inst().getData().getDataProfiles().get(uuid).equals(profile);
	}

	// methods
	public void sendMessage(String message) {
		Player player = getPlayer();
		if (player != null) {
			player.sendMessage(message);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(Utils.instanceOf(obj, PCUser.class))) {
			return false;
		}
		PCUser other = (PCUser) obj;
		return uuid.equals(other.uuid) && profile.equals(other.profile);
	}

	@Override
	public String toString() {
		return uuid.toString() + "_" + profile;
	}

	public String toStringName() {
		return getOfflinePlayer().getName() + "_" + profile;
	}

	// methods
	public static List<Player> getOnlinePlayers(Collection<PCUser> users) {
		List<Player> result = new ArrayList<Player>();
		for (PCUser user : users) {
			if (user.isCurrentProfile()) {
				Player player = user.getPlayer();
				if (player != null) {
					result.add(player);
				}
			}
		}
		return result;
	}

	public static List<String> getPlayerNames(Collection<PCUser> users) {
		List<String> result = new ArrayList<String>();
		for (PCUser user : users) {
			if (user.isCurrentProfile()) {
				OfflinePlayer player = user.getOfflinePlayer();
				if (player != null) {
					result.add(player.getName());
				}
			}
		}
		return result;
	}

	public static List<PCUser> getUsers(Collection<Player> players) {
		List<PCUser> result = new ArrayList<PCUser>();
		for (Player player : players) {
			result.add(new PCUser(player));
		}
		return result;
	}

	public static PCUser fromString(String pcUserRaw) {
		String[] split = pcUserRaw.split("_");
		return new PCUser(UUID.fromString(split[0]), split[1]);
	}

}
