package com.guillaumevdn.gcore.lib.command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.argument.Arg;
import com.guillaumevdn.gcore.lib.command.argument.Argument;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentFixed;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class Command implements CommandExecutor, TabCompleter {

	private static final List<String> HELP_ALIASES = CollectionUtils.asUnmodifiableList("?", "help"/* , "halp", "aled", "JEANNE" */);

	private GPlugin plugin;
	private String name;
	private String helpName;
	private List<Subcommand> subcommands = new ArrayList<>();
	private Subcommand base;

	public Command(GPlugin plugin, String name, String helpName, Subcommand base) {
		this.name = name;
		this.helpName = helpName;
		this.plugin = plugin;
		this.base = base;
		if (base != null) {
			for (Argument argument : base.getArguments()) {
				if (argument instanceof ArgumentFixed) {
					throw new IllegalArgumentException("base command can't have fixed arguments");
				}
			}
		}
	}

	// ----- get
	public GPlugin getPlugin() {
		return plugin;
	}

	public String getName() {
		return name;
	}

	public String getHelpName() {
		return helpName;
	}

	public List<Subcommand> getSubcommands() {
		return subcommands;
	}

	// ----- set
	public <S extends Subcommand> S setSubcommand(S subcommand) {
		subcommands.add(subcommand);
		return subcommand;
	}

	// ----- do
	public void showHelp(CommandSender sender) {
		showHelp(sender, 1);
	}

	public void showHelp(CommandSender sender, int pageNumber) {
		showHelp(sender, 17, pageNumber);
	}

	public void showHelp(CommandSender sender, int maxPageLines, int pageNumber) {
		// get children help
		List<String> available = new ArrayList<>();
		if (base != null) {
			List<String> tmp = base.buildHelp(this, true, sender);
			if (tmp != null) {
				available.addAll(tmp);
			}
		}
		subcommands.forEach(sub -> {
			List<String> tmp = sub.buildHelp(this, false, sender);
			if (tmp != null) {
				available.addAll(tmp);
			}
		});
		// wow, such empty
		if (available.isEmpty()) {
			return;
		}
		// separate pages
		List<List<String>> pages = new ArrayList<>();
		List<String> currentPage = new ArrayList<>();
		String prefix = TextGeneric.messageCommandHelpElementPrefix.parseLine();
		for (String line : available) {
			// it's a header, must we split ?
			if (line.startsWith(prefix) && currentPage.size() >= maxPageLines) {
				pages.add(currentPage);
				currentPage = new ArrayList<>();
			}
			// add to current page
			currentPage.add(line);
		}
		pages.add(currentPage);
		// show
		if (pageNumber < 1)
			pageNumber = 1;
		if (pageNumber > pages.size()) {
			TextGeneric.messageCommandHelpPageOutsideBounds.replace("{pages}", () -> pages.size())
					.replace("{plural}", () -> StringUtils.pluralize(pages.size())).send(sender);
		} else {
			final int pageNumberF = pageNumber; // come on you pepega
			List<String> page = pages.get(pageNumber - 1);
			String header = TextGeneric.messageCommandHelpHeader.replace("{prefix}", () -> "/" + helpName).replace("{page}", () -> pageNumberF)
					.replace("{pages}", () -> pages.size()).parseLine();
			String headerSeparator = "§7§m" + StringUtils.repeatString(" ", Math.abs(70 - header.length()) / 2);
			page.add(0, headerSeparator + "§r " + header + " " + headerSeparator);
			page.forEach(line -> sender.sendMessage(line));
		}
	}

	// ----- command
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] original) {
		if (plugin.isReloading()) {
			return true;
		}
		// decode arguments
		List<String> arguments = new ArrayList<>();
		List<String> parameters = new ArrayList<>();
		for (int i = 0; i < original.length; ++i) {
			String arg = original[i];
			if (!arg.trim().isEmpty()) {
				if (arg.startsWith("-") && NumberUtils.integerOrNull(arg) == null /* there will be no number param, this allows negative numbers */) {
					parameters.add(arg.substring(1).toLowerCase());
				} else {
					arguments.add(arg);
				}
			}
		}
		// help
		int helpPage = -1;
		if (arguments.size() >= 1 && HELP_ALIASES.contains(arguments.get(arguments.size() - 1).toLowerCase())) {
			arguments.remove(arguments.size() - 1);
			helpPage = 1;
		} else if (arguments.size() >= 2 && HELP_ALIASES.contains(arguments.get(arguments.size() - 2).toLowerCase())) {
			Integer page = NumberUtils.integerOrNull(arguments.remove(arguments.size() - 1));
			arguments.remove(arguments.size() - 1);
			helpPage = page == null || page <= 1 ? 1 : page;
		}
		// find matching subcommand
		Subcommand subcommand = base;
		if (!arguments.isEmpty()) {
			for (Subcommand sub : subcommands) {
				if (sub.getAliases().contains(arguments.get(0).toLowerCase())) {
					subcommand = sub;
					arguments.remove(0);
					break;
				}
			}
		}
		if (subcommand != null && (helpPage == -1 || !subcommand.equals(base))) {
			// can't use subcommand
			if (!subcommand.validateUse(sender)) {
				return true;
			}
			// help
			if (helpPage != -1) {
				subcommand.buildHelp(this, subcommand == base, sender).forEach(line -> sender.sendMessage(line));
				return true;
			}
			// parse arguments
			CommandCall call = new CommandCall(this, subcommand, sender, original, arguments, parameters);
			for (int i = 0; i < subcommand.getArguments().size(); ++i) {
				Argument argument = subcommand.getArguments().get(i);
				Object value = argument.consume(call);

				// found ; validate use
				if (value != null && !argument.validateUse(sender)) {
					return true;
				}
				call.setArgumentValue(i, value);
			}
			// incompatible arguments
			for (List<Arg> incompatible : subcommand.getIncompatible()) {
				Arg has1 = null;
				for (Arg inc : incompatible) {
					if (inc.has(call)) {
						if (has1 == null) {
							has1 = inc;
						} else {
							final Arg has1F = has1;
							TextGeneric.messageCommandIncompatibleArguments.replace("{argument1}", () -> has1F.getName())
									.replace("{argument2}", () -> inc.getName()).send(sender);
							return true;
						}
					}
				}
			}
			// dependent arguments
			for (Pair<Arg, List<Arg>> dependent : subcommand.getDependent()) {
				if (dependent.getA().has(call)) {
					for (Arg dep : dependent.getB()) {
						if (!dep.has(call)) {
							TextGeneric.messageCommandDependentArguments.replace("{argument1}", () -> dependent.getA().getName())
									.replace("{argument2}", () -> dep.getName()).send(sender);
							return true;
						}
					}
				}
			}
			// missing required arguments
			for (Argument argument : subcommand.getArguments()) {
				if (argument.getUsage() != null && argument.getNeed().equals(NeedType.REQUIRED) && argument.get(call) == null) {
					subcommand.logMissingArgument(argument, call);
					return true;
				}
			}
			// extra arguments
			if (!call.getArguments().isEmpty()) {
				TextGeneric.messageCommandUnnecessaryArguments.replace("{unnecessary}", () -> StringUtils.toTextString(" ", call.getArguments())).send(sender);
				return true;
			}
			// perform subcommand
			subcommand.perform(call);
			return true;
		}
		// unknown subcommand
		else {
			// help
			if (arguments.isEmpty()) {
				showHelp(sender, helpPage);
			} else {
				TextGeneric.messageCommandUnknown.replace("{subcommand}", () -> arguments.get(0)).send(sender);
			}
			return true;
		}
	}

	private final List<String> EMPTY_TAB_COMPLETE = CollectionUtils.asUnmodifiableList();

	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] original) {
		if (plugin.isReloading()) {
			return null;
		}
		// decode arguments
		List<String> arguments = new ArrayList<>();
		List<String> parameters = new ArrayList<>();
		for (int i = 0; i < original.length; ++i) {
			String arg = original[i];
			if (!arg.trim().isEmpty()) {
				if (arg.startsWith("-")) {
					parameters.add(arg.substring(1).toLowerCase());
				} else {
					arguments.add(arg);
				}
			}
		}
		// help
		if (arguments.stream().anyMatch(argument -> HELP_ALIASES.contains(argument.toLowerCase()))) {
			return EMPTY_TAB_COMPLETE;
		}
		// find matching subcommand
		Subcommand subcommand = null;
		if (!arguments.isEmpty()) {
			for (Subcommand sub : subcommands) {
				if (sub.getAliases().contains(arguments.get(0).toLowerCase())) {
					subcommand = sub;
					arguments.remove(0);
					break;
				}
			}
		}
		if (subcommand != null) {
			return suggestSubcommand(subcommand, arguments, parameters, sender, label, original);
		}
		// unknown subcommand
		else {
			// if stopped typing, we don't want to suggest anything
			if (original.length > 1) {
				return EMPTY_TAB_COMPLETE;
			}

			final List<String> suggest = new ArrayList<>(0);

			// suggest all subcommands
			if (arguments.isEmpty()) {
				suggest.addAll(subcommands.stream().filter(sub -> sub.canUse(sender)).map(sub -> sub.getAliases().get(0)).collect(Collectors.toList()));
				if (base != null) {
					suggest.addAll(suggestSubcommand(base, arguments, parameters, sender, label, original));
				}
			}
			// suggest subcommand that we're currently typing
			else if (arguments.size() == 1) {
				final String arg = arguments.get(0).toLowerCase();
				suggest.addAll(subcommands.stream().filter(sub -> sub.getAliases().stream().anyMatch(alias -> alias.startsWith(arg)))
						.filter(sub -> sub.canUse(sender)).map(sub -> sub.getAliases().get(0)).collect(Collectors.toList()));
			}

			// add base subcommand
			if (base != null) {
				suggest.addAll(suggestSubcommand(base, arguments, parameters, sender, label, original));
			}

			return suggest;
		}
	}

	private List<String> suggestSubcommand(Subcommand subcommand, List<String> arguments, List<String> parameters, CommandSender sender, String label,
			String[] original) {
		// can't use subcommand
		if (!subcommand.canUse(sender)) {
			return EMPTY_TAB_COMPLETE;
		}

		// parse arguments
		CommandCall call = new CommandCall(this, subcommand, sender, original, arguments, parameters, true);
		Argument lastParsedArgument = null;
		for (int i = 0; i < subcommand.getArguments().size(); ++i) {
			Argument argument = subcommand.getArguments().get(i);
			Object value = argument.consume(call);
			// found
			if (!argument.canUse(sender)) {
				return EMPTY_TAB_COMPLETE;
			}
			call.setArgumentValue(i, value);
			if (value != null) {
				lastParsedArgument = argument;
			}
		}
		final Argument lastParsedArgumentF = lastParsedArgument; // dummy dum dum
		// incompatible arguments
		for (List<Arg> incompatible : subcommand.getIncompatible()) {
			Arg has1 = null;
			for (Arg inc : incompatible) {
				if (inc.has(call)) {
					if (has1 == null) {
						has1 = inc;
					} else {
						return EMPTY_TAB_COMPLETE;
					}
				}
			}
		}
		// don't suggest new arguments if previous argument couldn't be parsed
		String previous = original.length < 3 /* 2 would check the subcommand alias */ ? "" : original[original.length - 2];
		if (!previous.isEmpty() && previous.charAt(0) != '-' && arguments.contains(previous) /* still contains means it can't be parsed */) {
			return EMPTY_TAB_COMPLETE;
		}
		// -
		String currentLower = original[original.length - 1].toLowerCase();
		// suggesting parameter
		if (!currentLower.isEmpty() && currentLower.charAt(0) == '-') {
			String p = currentLower.substring(1);
			List<String> suggest = subcommand.getParameters().stream()
					.filter(param -> p.isEmpty() || param.getAliases().stream().anyMatch(alias -> alias.startsWith(p))).filter(param -> !param.has(call))
					.flatMap(param -> param.getTabComplete().stream()).collect(Collectors.toList());
			if (suggest.size() <= 1) {
				return EMPTY_TAB_COMPLETE; // most likely found param or incorrect param, either way don't suggest new arguments because there's no space
			}
			return suggest; // only suggest params if start with hyphen
		}
		// not a parameter
		else {
			// don't suggest new arguments if there's no space behind an argument we just successfully parsed
			/*
			 * if (!current.isEmpty() && !arguments.contains(current)) { return EMPTY_TAB_COMPLETE; }
			 */
			List<String> suggest = new ArrayList<>();
			// if there's nothing yet, suggest parameters as well
			if (currentLower.isEmpty()) {
				suggest.addAll(subcommand.getParameters().stream().filter(param -> !param.has(call)).flatMap(param -> param.getTabComplete().stream())
						.collect(Collectors.toList()));
			}
			// and suggest arguments that start with the current typed thing
			final Subcommand subcommandF = subcommand;
			suggest.addAll(subcommand.getArguments().stream().filter(arg -> arg.canUse(sender))
					.filter(arg -> arg.get(call) == null || (arg == lastParsedArgumentF && !currentLower.isEmpty())) // show tab completion for the entire
																														// current argument, only if we're still
																														// typing it
					.filter(arg -> subcommandF.getIncompatible().stream()
							.allMatch(incompatible -> !incompatible.contains(arg) || incompatible.stream().allMatch(inc -> !inc.has(call)))) // don't suggest
																																				// arguments
																																				// that are
																																				// incompatible
																																				// with already
																																				// typed
																																				// arguments
					.map(arg -> arg.tabComplete(call)).filter(tabComplete -> tabComplete != null).flatMap(tabComplete -> tabComplete.stream())
					.filter(elem -> currentLower.isEmpty() || elem.toLowerCase().startsWith(currentLower)).collect(Collectors.toList()));
			// done
			return suggest;
		}
	}

}
