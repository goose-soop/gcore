package be.guillaumevdn.gcore.lib.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import be.guillaumevdn.gcore.GLocale;
import be.guillaumevdn.gcore.lib.Perm;
import be.guillaumevdn.gcore.lib.GPlugin;
import be.guillaumevdn.gcore.lib.messenger.Messenger;
import be.guillaumevdn.gcore.lib.util.Utils;

public class CommandArgument {

	// base
	private GPlugin plugin;
	private CommandArgument parent;
	private List<String> aliases;
	private String description;
	private Perm permission;
	private boolean playerOnly;
	private List<CommandArgument> children = new ArrayList<CommandArgument>();
	private List<Param> expectedParams = new ArrayList<Param>();

	public CommandArgument(GPlugin plugin, List<String> aliases, String description, Perm permission, boolean playerOnly, Param... expectedParams) {
		this(plugin, null, aliases, description, permission, playerOnly, expectedParams);
	}

	public CommandArgument(GPlugin plugin, CommandArgument parent, List<String> aliases, String description, Perm permission, boolean playerOnly, Param... expectedParams) {
		this.plugin = plugin;
		this.parent = parent;
		this.aliases = aliases;
		this.description = description;
		this.permission = permission;
		this.playerOnly = playerOnly;
		// decode expected params
		if (expectedParams != null) {
			this.expectedParams.addAll(Utils.asList(expectedParams));
		}
	}

	// get
	public GPlugin getPlugin() {
		return plugin;
	}

	public CommandArgument getParent() {
		return parent;
	}

	public List<String> getAliases() {
		return aliases;
	}

	public String getFullName() {
		// get full name
		String fullName = null;
		CommandArgument p = this;
		while (p != null) {
			fullName = (p.getParent() == null ? "/" : "") + p.getAliases().get(0) + (fullName == null ? "" : " " + fullName);
			p = p.getParent();
		}
		// return
		return fullName;
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

	public List<CommandArgument> getChildren() {
		return children;
	}

	public List<Param> getExpectedParams() {
		return expectedParams;
	}

	public CommandArgument getChild(String name) {
		for (CommandArgument child : children) {
			if (Utils.containsIgnoreCase(child.getAliases(), name)) {
				return child;
			}
		}
		return null;
	}

	// methods
	public void addChild(CommandArgument child) {
		addChild(children.size(), child);
	}

	public void addChild(int index, CommandArgument child) {
		children.add(index, child);
		child.parent = this;
	}

	public boolean canUse(CommandSender sender) {
		return (isPlayerOnly() ? Utils.instanceOf(sender, CommandSender.class) : true) && (getPermission() == null ? true : getPermission().has(sender));
	}

	public List<String> getHelp(CommandSender sender) {
		if (description != null && canUse(sender)) {
			List<String> params = new ArrayList<String>();
			for (Param param : expectedParams) {
				if (param.canUse(sender)) {
					params.add((param.isMandatory() ? "§c§o" : "§e§o") + "-" + param + (param.getDescription() == null ? "" : ":[" + param.getDescription() + "]"));
				}
			}
			//if (!params.isEmpty()) params = " §o" + params.substring(0, params.length() - 1) + "";
			return Utils.asList("§c§l" + getFullName() + (params.isEmpty() ? "" : " " + Utils.asNiceString(params, true).replace(",", "")), "§7 " + description);
		}
		return null;
	}

	public void showHelp(CommandSender sender) {
		showHelp(sender, 1);
	}

	public void showHelp(CommandSender sender, int pageNumber) {
		showHelp(sender, 7, pageNumber, getFullName());
	}

	public void showHelp(CommandSender sender, int pageLimit, int pageNumber, String helpPrefix) {
		// get children help
		List<String> available = Utils.emptyList();
		for (CommandArgument child : children) {
			List<String> help = child.getHelp(sender);
			if (help != null) {
				available.addAll(help);
			}
		}
		// empty available or this argument has params, so add it
		List<String> thisHelp = getHelp(sender);
		if (thisHelp != null && (thisHelp.contains("-") || available.isEmpty())) {
			available.addAll(0, thisHelp);
		}
		// get page
		List<String> page = Utils.emptyList();
		int firstIndex = pageNumber == 1 ? 0 : (pageNumber - 1) * (pageLimit * 2);
		int lastIndex = pageNumber == 1 ? pageLimit * 2 - 1 : firstIndex + (pageLimit * 2);
		for (int i = firstIndex; i <= lastIndex && i < available.size(); i++) {
			page.add(available.get(i));
		}
		// show
		int maxPage = (int) Math.ceil(available.size() / 2 / pageLimit);
		if (maxPage == 0 && !available.isEmpty()) maxPage = 1;
		if (page.isEmpty()) {
			Messenger.send(sender, "§7This help menu has only " + maxPage + Utils.getPluralFor(" page", maxPage) + ".");
		} else {
			page.add(0, "§7 ---- §6§l" + helpPrefix + " §6help (" + pageNumber + "/" + maxPage + ") §7----");
			Messenger.send(sender, page);
		}
	}

	/**
	 * @return true if the call ended
	 */
	private static final Param paramHelp = new Param(Utils.asList("help", "?"), null, null, false, false);

	public boolean call(CommandCall call, int argumentIndexForThis) {
		// name doesn't match, so return false
		if (!Utils.containsIgnoreCase(aliases, call.getArguments().get(argumentIndexForThis))) {
			return false;
		}
		// has more arguments, so keep going
		if (argumentIndexForThis + 1 < call.getArguments().size()) {
			// no children
			if (children.isEmpty()) {
				GLocale.MSG_GENERIC_COMMAND_NOCHILDREN.send(call.getSender(), "{plugin}", plugin.getName(), "{current_path}", getFullName());
				return true;
			}
			// this argument has children
			for (CommandArgument child : children) {
				if (child.call(call, argumentIndexForThis + 1)) {
					return true;
				}
			}
			// no children ended the call
			GLocale.MSG_GENERIC_COMMAND_NOCHILDPERFORMED.send(call.getSender(), "{plugin}", plugin.getName(), "{current_path}", getFullName());
			return true;
		}
		// sender isn't a player, so return false
		if (playerOnly ? !call.senderIsPlayer() : false) {
			GLocale.MSG_GENERIC_NOTPLAYER.send(call.getSender(), "{plugin}", plugin.getName());
			return true;
		}
		// sender doesn't have permission, so return false
		if (permission == null ? false : !permission.has(call.getSender())) {
			GLocale.MSG_GENERIC_NOPERMISSION.send(call.getSender(), "{plugin}", plugin.getName());
			return true;
		}
		// has help param, so display help for this
		if (paramHelp.has(call)) {
			if (paramHelp.hasValue(call)) {
				int pageNumber = paramHelp.getInt(call);
				if (pageNumber != Integer.MIN_VALUE) {
					showHelp(call.getSender(), pageNumber);
				}
			} else {
				showHelp(call.getSender());
			}
		}
		// no help param and no more arguments, so perform this
		else {
			perform(call);
		}
		return true;
	}

	// TODO : tab complete for params value (for every Param object we specify a "tab complete" method)
	/**
	 * @return a list of completions, or null if no result
	 */
	public List<String> tabComplete(CommandCall call, String partialArgument, int argumentIndexForThis) {
		// name doesn't match, so return false
		if (!Utils.containsIgnoreCase(aliases, call.getArguments().get(argumentIndexForThis))) {
			return null;
		}
		// has more arguments, so keep going
		if (argumentIndexForThis + 1 < call.getArguments().size()) {
			// no children
			if (children.isEmpty()) {
				return null;
			}
			// this argument has children
			for (CommandArgument child : children) {
				List<String> result = child.tabComplete(call, partialArgument, argumentIndexForThis + 1);
				if (result != null) {
					return result;
				}
			}
			// no result found for children
			return null;
		}
		// prepare result
		List<String> result = new ArrayList<String>();
		// children (if no params yet)
		if (call.getParameters().isEmpty()) {
			for (CommandArgument child : children) {
				String alias = child.getAliases().get(0);
				if (child.getDescription() != null && child.canUse(call.getSender()) && (partialArgument.isEmpty() || alias.toLowerCase().startsWith(partialArgument.toLowerCase()))) {
					result.add(alias);
				}
			}
		}
		// params
		for (Param param : expectedParams) {
			if (!param.has(call) && param.isCompatibleWith(call) && param.canUse(call.getSender())) {
				String alias = param.getAliases().get(0);
				if (partialArgument.isEmpty() || alias.toLowerCase().startsWith(partialArgument.toLowerCase())) {
					result.add("-" + alias + (param.getDescription() == null ? "" : ":"));
				}
			}
		}
		result.add("-help");
		result.add("-help:page");
		return result;
	}

	protected void perform(CommandCall call) {
		// show help for children by default
		showHelp(call.getSender());
	}

}
