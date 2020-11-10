package com.guillaumevdn.gcore.lib.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class CommandCall {

	private Command command;
	private Subcommand subcommand;
	private CommandSender sender;
	private Player senderPlayer;
	private String[] original;
	private List<String> arguments;
	private Object[] argumentValues;
	private List<String> parameters;
	private boolean forTabComplete;

	public CommandCall(Command command, Subcommand subcommand, CommandSender sender, String[] original, List<String> arguments, List<String> parameters) {
		this(command, subcommand, sender, original, arguments, parameters, false);
	}

	public CommandCall(Command command, Subcommand subcommand, CommandSender sender, String[] original, List<String> arguments, List<String> parameters, boolean forTabComplete) {
		this.subcommand = subcommand;
		this.sender = sender;
		this.senderPlayer = ObjectUtils.castOrNull(sender, Player.class);
		this.original = original;
		this.arguments = arguments;
		this.argumentValues = new Object[subcommand.getArguments().size()];
		this.parameters = parameters;
		this.forTabComplete = forTabComplete;
	}

	// get
	public Command getCommand() {
		return command;
	}

	public Subcommand getSubcommand() {
		return subcommand;
	}

	public CommandSender getSender() {
		return sender;
	}

	public Player getSenderPlayer() {
		return senderPlayer;
	}

	public String[] getOriginal() {
		return original;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public <T> T getArgumentValue(int index) {
		return index < 0 || index >= argumentValues.length ? null : (T) argumentValues[index];
	}

	public List<String> getParameters() {
		return parameters;
	}

	public boolean isForTabComplete() {
		return forTabComplete;
	}

	// set
	public void setArgumentValue(int index, Object value) {
		if (index < argumentValues.length) {
			argumentValues[index] = value;
		}
	}

}
