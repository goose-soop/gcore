package com.guillaumevdn.gcore.command;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.bukkit.OfflinePlayer;

import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentOfflinePlayer;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentString;
import com.guillaumevdn.gcore.lib.data.board.keyed.UniKeyedBoardRemote;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class GcoreBoardElementPrint extends Subcommand {

	private final ArgumentOfflinePlayer argumentTarget = addArgumentOfflinePlayer(NeedType.REQUIRED, false, null, TextGeneric.commandParameterUsageTarget, true);
	private final ArgumentString argumentBoardId = addArgumentString(NeedType.REQUIRED, false, null, Text.of("board id"));

	public GcoreBoardElementPrint() {
		super(false, PermissionGCore.inst().gcoreAdmin, Text.of("print player data from a board"), CollectionUtils.asList("printboardelement", "pbe"));
	}

	@Override
	public final void perform(CommandCall call) {
		OfflinePlayer target = argumentTarget.get(call);
		String boardPartId = argumentBoardId.get(call);

		UniKeyedBoardRemote board = (UniKeyedBoardRemote) PluginUtils.getGPlugins().stream()
				.flatMap(plugin -> plugin.getData().copyValues().stream())
				.filter(b -> b instanceof UniKeyedBoardRemote && ((UniKeyedBoardRemote) b).getId().contains(boardPartId))
				.findAny().orElse(null);

		if (board == null) {
			call.getSender().sendMessage("§dNo board found containing id '" + boardPartId + "'.");
			return;
		}

		call.getSender().sendMessage("§dFrom board " + board.getId() + " :");
		board.fetchValue(target.getUniqueId(), user -> {
			try (StringWriter writer = new StringWriter()) {
				board.getPlugin().getPrettyGson().toJson(user, board.getValueClass(), new PrintWriter(writer));
				for (String line : writer.toString().split("\n")) {
					call.getSender().sendMessage(line);
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}, null, false, false);
	}

}
