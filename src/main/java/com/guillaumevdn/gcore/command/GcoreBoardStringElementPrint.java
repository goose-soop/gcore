package com.guillaumevdn.gcore.command;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentString;
import com.guillaumevdn.gcore.lib.data.board.keyed.KeyedBoard;
import com.guillaumevdn.gcore.lib.data.board.keyed.KeyedBoardRemote;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class GcoreBoardStringElementPrint extends Subcommand {

	private final ArgumentString argumentBoardId = addArgumentString(NeedType.REQUIRED, false, null, Text.of("board id"));
	private final ArgumentString argumentId = addArgumentString(NeedType.REQUIRED, false, null, Text.of("element id"));

	public GcoreBoardStringElementPrint() {
		super(false, PermissionGCore.inst().gcoreAdmin, Text.of("print data from a board"), CollectionUtils.asList("printstringboardelement", "psbe"));
	}

	@Override
	public final void perform(CommandCall call) {
		final String boardPartId = argumentBoardId.get(call);
		final String id = argumentId.get(call);

		final KeyedBoard board = (KeyedBoard) PluginUtils.getGPlugins().stream().flatMap(plugin -> plugin.getData().copyValues().stream())
				.filter(b -> b instanceof KeyedBoard && ((KeyedBoard) b).getId().contains(boardPartId)).findAny().orElse(null);

		if (board == null) {
			call.getSender().sendMessage("§dNo board found containing id '" + boardPartId + "'.");
			return;
		}

		call.getSender().sendMessage("§dFrom board " + board.getId() + " :");

		final Consumer<Object> consumer = value -> {
			if (value == null)
				return;
			try (StringWriter writer = new StringWriter()) {
				board.getPlugin().getPrettyGson().toJson(value, board.getValueClass(), new PrintWriter(writer));
				for (String line : writer.toString().split("\n")) {
					call.getSender().sendMessage(line);
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		};

		if (board instanceof KeyedBoardRemote) {
			((KeyedBoardRemote) board).fetchValue(id, consumer, null, false, false);
		} else {
			consumer.accept(board.getCachedValue(id));
		}
	}

}
