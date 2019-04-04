package com.guillaumevdn.gcore.lib.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.Perm;
import com.guillaumevdn.gcore.lib.util.Utils;

public class Param {

	// base
	private List<String> aliases;
	private String description;
	private Perm permission;
	private boolean playerOnly, mandatory;
	private List<Param> incompatibleWith = new ArrayList<Param>();

	public Param(List<String> aliases, String description, Perm permission, boolean playerOnly, boolean mandatory) {
		this.aliases = aliases;
		this.description = description;
		this.permission = permission;
		this.playerOnly = playerOnly;
		this.mandatory = mandatory;
	}

	// get
	public List<String> getAliases() {
		return aliases;
	}

	public String getDescription() {
		return description;
	}

	public Perm getPermission() {
		return permission;
	}

	public boolean isPlayerOnly() {
		return playerOnly;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setIncompatibleWith(Param... params) {
		if (params != null) {
			for (Param param : params) {
				if (!incompatibleWith.contains(param)) {
					incompatibleWith.add(param);
				}
			}
		}
	}

	// methods
	public boolean canUse(CommandSender sender) {
		return (isPlayerOnly() ? Utils.instanceOf(sender, CommandSender.class) : true) && (getPermission() == null ? true : getPermission().has(sender));
	}

	@Override
	public String toString() {
		return getAliases().get(0);
	}

	public boolean has(CommandCall call) {
		for (String alias : getAliases()) {
			if (call.getParameters().containsKey(alias)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasValue(CommandCall call) {
		for (String alias : getAliases()) {
			if (call.getParameters().containsKey(alias)) {
				return call.getParameters().get(alias) != null;
			}
		}
		return false;
	}

	public boolean isCompatibleWith(CommandCall call) {
		for (String callParam : call.getParameters().keySet()) {
			for (Param incompatible : incompatibleWith) {
				if (Utils.containsIgnoreCase(incompatible.getAliases(), callParam)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean ensureAuthorization(CommandCall call) {
		// sender isn't a player, so return false
		if (playerOnly ? !call.senderIsPlayer() : false) {
			GLocale.MSG_GENERIC_NOTPLAYER.send(call.getSender(), "{plugin}", call.getPlugin().getName());
			return false;
		}
		// sender doesn't have permission, so return false
		if (permission == null ? false : !permission.has(call.getSender())) {
			GLocale.MSG_GENERIC_NOPERMISSION.send(call.getSender(), "{plugin}", call.getPlugin().getName());
			return false;
		}
		// good
		return true;
	}

	public String getString(CommandCall call) {
		// check for match
		for (String alias : getAliases()) {
			// match
			if (call.getParameters().containsKey(alias)) {
				// ensure authorization
				if (!ensureAuthorization(call)) {
					return null;
				}
				// no value, so send error message
				String value = call.getParameters().get(alias);
				if (value == null) {
					break;
				}
				// return parameter value
				return value;
			}
		}
		// no match, missing parameter
		GLocale.MSG_GENERIC_COMMAND_MISSINGPARAM.send(call.getSender(), "{plugin}", call.getPlugin().getName(), "{parameter}", "-" + toString() + (getDescription() == null ? "" : ":[" + getDescription() + "]"));
		return null;
	}

	public String getStringAlphanumeric(CommandCall call) {
		String value = getString(call);
		if (value != null) {
			// valid alphanumeric string
			if (Utils.isAlphanumeric(value)) {
				return value;
			}
			// not alphanumeric
			else {
				GLocale.MSG_GENERIC_COMMAND_INVALIDALPHANUMERICPARAM.send(call.getSender(), "{plugin}", call.getPlugin().getName(), "{parameter}", "-" + toString(), "{value}", value);
			}
		}
		return null;
	}

	public UUID getUUID(CommandCall call) {
		String value = getString(call);
		if (value != null) {
			// valid UUID
			try {
				return UUID.fromString(value);
			}
			// not a number
			catch (NumberFormatException ignored) {
				GLocale.MSG_GENERIC_COMMAND_INVALIDUUIDPARAM.send(call.getSender(), "{plugin}", call.getPlugin().getName(), "{parameter}", "-" + toString(), "{value}", value);
			}
		}
		return null;
	}

	public int getInt(CommandCall call) {
		String value = getString(call);
		if (value != null) {
			// valid number
			try {
				return Integer.parseInt(value);
			}
			// not a number
			catch (NumberFormatException ignored) {
				GLocale.MSG_GENERIC_COMMAND_INVALIDINTPARAM.send(call.getSender(), "{plugin}", call.getPlugin().getName(), "{parameter}", "-" + toString(), "{value}", value);
			}
		}
		return Integer.MIN_VALUE;
	}

	public double getDouble(CommandCall call) {
		String value = getString(call);
		if (value != null) {
			// valid number
			try {
				return Double.parseDouble(value);
			}
			// not a number
			catch (NumberFormatException ignored) {
				GLocale.MSG_GENERIC_COMMAND_INVALIDDOUBLEPARAM.send(call.getSender(), "{plugin}", call.getPlugin().getName(), "{parameter}", "-" + toString(), "{value}", value);
			}
		}
		return Integer.MIN_VALUE;
	}

	public OfflinePlayer getOfflinePlayer(CommandCall call, boolean senderIfNone) {
		// return sender if none
		if (!has(call) && senderIfNone) {
			return call.senderIsPlayer() ? call.getSenderAsPlayer() : null;
		}
		// self
		String value = getString(call);
		if (value == null) return null;
		if (call.senderIsPlayer() && value.equalsIgnoreCase(call.getSender().getName())) {
			return call.getSenderAsPlayer();
		}
		// UUID
		UUID uuid = null;
		try {
			uuid = UUID.fromString(value);
		} catch (IllegalArgumentException ignored) {}
		// valid player
		OfflinePlayer player = uuid == null ? Utils.getOfflinePlayer(value) : Utils.getOfflinePlayer(uuid);
		if (player != null) {
			return player;
		}
		// not a player
		else {
			GLocale.MSG_GENERIC_COMMAND_INVALIDOFFLINEPLAYERPARAM.send(call.getSender(), "{plugin}", call.getPlugin().getName(), "{parameter}", "-" + toString(), "{value}", value);
			return null;
		}
	}

	public <T extends Enum<T>> T getEnum(CommandCall call, Class<T> enumClass) {
		String value = getString(call);
		if (value != null) {
			// valid enum
			T t = Utils.valueOfOrNull(enumClass, value);
			if (t != null) {
				return t;
			}
			// unknown enum
			GLocale.MSG_GENERIC_COMMAND_INVALIDENUMPARAM.send(call.getSender(), "{plugin}", call.getPlugin().getName(), "{parameter}", "-" + toString(), "{value}", value, "{enum}", enumClass.getSimpleName());
		}
		return null;

	}

	public Player getPlayer(CommandCall call, boolean senderIfNone) {
		// return sender if none
		if (!has(call) && senderIfNone) {
			return call.senderIsPlayer() ? call.getSenderAsPlayer() : null;
		}
		// self
		String value = getString(call);
		if (value == null) return null;
		if (call.senderIsPlayer() && value.equalsIgnoreCase(call.getSender().getName())) {
			return call.getSenderAsPlayer();
		}
		// UUID
		UUID uuid = null;
		try {
			uuid = UUID.fromString(value);
		} catch (IllegalArgumentException ignored) {}
		// valid player
		Player player = uuid == null ? Utils.getPlayer(value) : Utils.getPlayer(uuid);
		if (player != null) {
			return player;
		}
		// not a player
		else {
			GLocale.MSG_GENERIC_COMMAND_INVALIDPLAYERPARAM.send(call.getSender(), "{plugin}", call.getPlugin().getName(), "{parameter}", "-" + toString(), "{value}", value);
			return null;
		}
	}

	public <T> T get(CommandCall call, ParamParser<T> parser) {
		String value = getString(call);
		if (value != null) {
			return parser.parse(call.getSender(), this, value);
		}
		return null;
	}

}
