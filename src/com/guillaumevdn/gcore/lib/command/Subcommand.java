package com.guillaumevdn.gcore.lib.command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.argument.Arg;
import com.guillaumevdn.gcore.lib.command.argument.Argument;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentDouble;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentEnum;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentFixed;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentInteger;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentInteger.TabCompleteMode;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentOfflinePlayer;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentPlayer;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentString;
import com.guillaumevdn.gcore.lib.command.argument.Parameter;
import com.guillaumevdn.gcore.lib.command.argument.PlayerArgument;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public abstract class Subcommand extends UsageRestriction {

	private final List<String> aliases;
	private final Text description;
	private final List<Argument<?>> arguments = new ArrayList<>(); // the ? is required for streams to work properly with tab completion :confusedwat:
	private final List<Parameter> parameters = new ArrayList<>();
	private final List<List<Arg>> incompatible = new ArrayList<>();
	private final List<Pair<Arg, List<Arg>>> dependent = new ArrayList<>();

	public Subcommand(boolean playerOnly, Permission permission, Text description, List<String> aliases) {
		super(playerOnly, permission);
		this.aliases = CollectionUtils.asUnmodifiableLowercaseList(aliases);
		this.description = description;
	}

	// get
	public final Text getDescription() {
		return description;
	}

	public final List<String> getAliases() {
		return aliases;
	}

	public final List<Argument<?>> getArguments() { // the ? is required for streams to work properly with tab completion :confusedwat:
		return arguments;
	}

	public final List<Parameter> getParameters() {
		return parameters;
	}

	public final List<List<Arg>> getIncompatible() {
		return incompatible;
	}

	public List<Pair<Arg, List<Arg>>> getDependent() {
		return dependent;
	}

	// set
	public final <A extends Argument> A addArgument(A argument) {
		arguments.add(argument);
		return argument;
	}

	public final ArgumentFixed addArgumentFixed(NeedType need, boolean playerOnly, Permission permission, String... aliases) {
		return addArgument(new ArgumentFixed(need, playerOnly, permission, aliases));
	}

	public final ArgumentString addArgumentString(NeedType need, boolean playerOnly, Permission permission, Text usage) {
		return addArgument(new ArgumentString(need, playerOnly, permission, usage));
	}

	public final ArgumentPlayer addArgumentPlayer(NeedType need, boolean playerOnly, Permission permission, Text usage, boolean senderIfNone) {
		return addArgument(new ArgumentPlayer(need, playerOnly, permission, usage, senderIfNone));
	}

	public final ArgumentOfflinePlayer addArgumentOfflinePlayer(NeedType need, boolean playerOnly, Permission permission, Text usage, boolean senderIfNone) {
		return addArgument(new ArgumentOfflinePlayer(need, playerOnly, permission, usage, senderIfNone));
	}

	public final <E extends Enum<E>> ArgumentEnum<E> addArgumentEnum(NeedType need, boolean playerOnly, Permission permission, Text usage, Class<E> enumClass) {
		return addArgument(new ArgumentEnum<E>(need, playerOnly, permission, usage, enumClass));
	}

	public final ArgumentInteger addArgumentInteger(NeedType need, boolean playerOnly, Permission permission, Text usage, TabCompleteMode mode) {
		return addArgument(new ArgumentInteger(need, playerOnly, permission, usage, mode));
	}
	
	public final ArgumentDouble addArgumentDouble(NeedType need, boolean playerOnly, Permission permission, Text usage, TabCompleteMode mode) {
		return addArgument(new ArgumentDouble(need, playerOnly, permission, usage, mode));
	}

	public final Parameter addParameter(boolean playerOnly, Permission permission, String... aliases) {
		Parameter parameter = new Parameter(playerOnly, permission, aliases);
		parameters.add(parameter);
		return parameter;
	}

	public final void setIncompatible(Arg... objects) {
		incompatible.add(CollectionUtils.asList(objects));
	}

	public final void setDependant(Arg dependent, Arg... dependencies) {
		this.dependent.add(Pair.of(dependent, CollectionUtils.asList(dependencies)));
	}

	// do
	public abstract void perform(CommandCall call);

	// help/text
	public void logMissingArgument(Argument argument, CommandCall call) {
		List<String> found = getArguments().stream()
				.filter(arg -> arg.getUsage() != null && arg.get(call) != null)
				.map(arg -> arg.getUsage().parseLine())
				.collect(Collectors.toList());
		(found.isEmpty() ? TextGeneric.messageCommandMissingArgument : TextGeneric.messageCommandMissingArgumentFound).replace("{argument}", () -> argument.getUsage().parseLines()).replace("{found}", () -> StringUtils.toTextString(", ", found)).send(call.getSender());
	}

	public String buildUsage(Command parent, boolean isBase, CommandSender sender) {
		String colorArgumentOptional = TextGeneric.messageCommandHelpColorArgumentOptional.parseLine();
		String colorArgumentRequired = TextGeneric.messageCommandHelpColorArgumentRequired.parseLine();
		List<String> list = CollectionUtils.asList("/" + parent.getHelpName());
		if (!isBase) list.add(aliases.get(0));
		for (Argument argument : arguments) {
			if (argument.getUsage() != null && argument.canUse(sender) && (!(argument instanceof PlayerArgument) || !((PlayerArgument) argument).canUseBecauseOfSenderIfNone(sender))) {
				if (argument.getNeed().equals(NeedType.REQUIRED)) {
					list.add(colorArgumentRequired + "<" + argument.getUsage().parseLine() + ">");
				} else {
					list.add(colorArgumentOptional + "[" + argument.getUsage().parseLine() + "]");
				}
			}
		}
		for (Parameter parameter : parameters) {
			if (parameter.canUse(sender)) {
				list.add(colorArgumentOptional + "-" + parameter.getAliases().get(0));
			}
		}
		return StringUtils.toTextString(" ", list);
	}

	public List<String> buildHelp(Command parent, boolean isBase, CommandSender sender) {
		if (description != null && canUse(sender)) {
			String line = TextGeneric.messageCommandHelpElementPrefix.parseLine()
					+ TextGeneric.messageCommandHelpElement
					.replace("{usage}", () -> buildUsage(parent, isBase, sender))
					.replace("{description}", () -> description.parseLines())
					.parseLine();
			return StringUtils.splitLongText(line, 68, str -> " " + str);
		}
		return null;
	}

}
