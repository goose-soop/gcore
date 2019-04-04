package be.guillaumevdn.gcore.lib.messenger;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.guillaumevdn.gcore.data.UserInfo;
import be.guillaumevdn.gcore.lib.Logger;
import be.guillaumevdn.gcore.lib.Logger.Level;
import be.guillaumevdn.gcore.lib.util.Utils;

public class MessageTarget
{
	// ------------------------------------------------------------
	// Static fields
	// ------------------------------------------------------------

	public static final MessageTarget CONSOLE = new MessageTarget(Bukkit.getConsoleSender());

	// ------------------------------------------------------------
	// Fields and constructor
	// ------------------------------------------------------------

	private Object target;

	public MessageTarget(Object target) {
		this.target = target;
	}

	// ------------------------------------------------------------
	// Send methods
	// ------------------------------------------------------------

	public void send(String message) {
		// Message target
		if (target instanceof MessageTarget) {
			((MessageTarget) target).send(message);
		}
		// ArrayList of targets
		else if (target instanceof Collection<?>) {
			for (Object obj : (Collection<?>) target) {
				new MessageTarget(obj).send(message);
			}
		}
		// Player
		else if (target instanceof Player) {
			((Player) target).sendMessage(message);
		}
		// Command sender
		else if (target instanceof CommandSender) {
			((CommandSender) target).sendMessage(message);
		}
		// UUID
		else if (target instanceof UUID) {
			Player player = Utils.getPlayer((UUID) target);
			if (player != null) {
				player.sendMessage(message);
			}
		}
		// UserInfo
		else if (target instanceof UserInfo) {
			Player player = ((UserInfo) target).toPlayer();
			if (player != null) {
				player.sendMessage(message);
			}
		}
		// Unknown
		else {
			Logger.log(Level.WARNING, "GCore", "Could not send message (unknown target type : " + target.getClass() + ")");
			Logger.log(Level.WARNING, "GCore", ">> " + message);
		}
	}

	public void send(String... message) {
		send(Utils.asList(message));
	}

	public void send(List<String> message) {
		// Message target
		if (target instanceof MessageTarget) {
			((MessageTarget) target).send(message);
		}
		// ArrayList of targets
		else if (target instanceof Collection<?>) {
			for (Object obj : (Collection<?>) target) {
				new MessageTarget(obj).send(message);
			}
		}
		// Player
		else if (target instanceof Player) {
			Player player = (Player) target;
			for (String msg : message) {
				player.sendMessage(msg);
			}
		}
		// Command sender
		else if (target instanceof CommandSender) {
			CommandSender target = (CommandSender) this.target;
			if (target instanceof Player) message = Utils.fillPlaceholderAPI((Player) target, message);
			for (String msg : message) target.sendMessage(msg);
		}
		// UUID
		else if (target instanceof UUID) {
			Player player = Utils.getPlayer((UUID) target);
			if (player != null) {
				for (String msg : message) {
					player.sendMessage(msg);
				}
			}
		}
		// UserInfo
		else if (target instanceof UserInfo) {
			Player player = ((UserInfo) target).toPlayer();
			if (player != null) {
				for (String msg : message) {
					player.sendMessage(msg);
				}
			}
		}
		// Unknown
		else {
			Logger.log(Level.WARNING, "GCore", "Could not send message (unknown target type : " + target.getClass() + ")");
			for (String str : message) Logger.log(Level.WARNING, "GCore", ">> " + str);
		}
	}
}
