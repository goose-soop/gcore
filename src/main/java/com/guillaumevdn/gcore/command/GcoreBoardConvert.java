package com.guillaumevdn.gcore.command;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentDynamicFakeEnum;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentFakeEnum;
import com.guillaumevdn.gcore.lib.data.DataBackEnd;
import com.guillaumevdn.gcore.lib.data.board.Board;
import com.guillaumevdn.gcore.lib.data.board.BoardConnector;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class GcoreBoardConvert extends Subcommand {

	private final ArgumentDynamicFakeEnum<String> argumentBoardId = addArgumentDynamicFakeEnum(NeedType.REQUIRED, false, null, Text.of("board id"), String.class, () -> {
		return PluginUtils.getGPlugins().stream().flatMap(plugin -> plugin.getData().copyKeys().stream()).collect(Collectors.toList());
	});
	private final ArgumentFakeEnum<DataBackEnd> argumentBackEnd = addArgumentFakeEnum(NeedType.REQUIRED, false, null, Text.of("target data"), DataBackEnd.class, CollectionUtils.asList(DataBackEnd.JSON, DataBackEnd.SQLITE, DataBackEnd.MYSQL));
	private final Set<CommandSender> mustConfirm = new HashSet<>();

	public GcoreBoardConvert() {
		super(false, PermissionGCore.inst().gcoreAdmin, Text.of("copy data from a board to another back-end type"), CollectionUtils.asList("convertboard", "convert"));
	}

	@Override
	public final void perform(CommandCall call) {
		String boardPartId = argumentBoardId.get(call);
		DataBackEnd toBackEnd = argumentBackEnd.get(call);

		Board board = PluginUtils.getGPlugins().stream()
				.flatMap(plugin -> plugin.getData().copyValues().stream())
				.filter(b -> b.getId().contains(boardPartId))
				.findAny().orElse(null);

		if (board == null) {
			call.getSender().sendMessage("§7No board found containing id §c" + boardPartId + "§7.");
			return;
		}

		if (board.getBackEnd().equals(toBackEnd)) {
			call.getSender().sendMessage("§7Target board §e" + board.getId() + "§7 is already configured to §e" + toBackEnd + "§7.");
			return;
		}

		BoardConnector fakeConnector;
		try {
			fakeConnector = ReflectionObject.of(board).invokeMethod("createConnector", toBackEnd).get();
		} catch (Throwable exception) {
			call.getSender().sendMessage("§7Target board §c" + board.getId() + "§7 does not support §c" + toBackEnd + "§7.");
			return;
		}

		/*if (toBackEnd.equals(DataBackEnd.MYSQL) && !GCore.inst().getMySQLHandler().canConnect()) {
			call.getSender().sendMessage("§7Could not connect to MySQL. Make sure the identifiers are correctly configured in §cGCore/config.yml§7.");
			return;
		}*/

		GPlugin<?, ?> plugin = board.getPlugin();
		if (mustConfirm.add(call.getSender())) {
			call.getSender().sendMessage("§eThis will suspend " + plugin.getName() + " while data is converting. Continue ? (re-enter command)");
			GCore.inst().operateAsyncLater(() -> mustConfirm.remove(call.getSender()), 100L);
			return;
		}
		mustConfirm.remove(call.getSender());

		call.getSender().sendMessage("§dCopying board data " + board.getId() + " from " + board.getBackEnd() + " to " + toBackEnd + "...");
		plugin.operateAsync(() -> {
			// lock plugin
			ReflectionObject.of(plugin).setField("reloading", true);

			// save board data if needed
			call.getSender().sendMessage("§d> Saving data...");
			board.saveNeeded(BukkitThread.current());

			// re-pull entire board
			call.getSender().sendMessage("§d> Loading all data from " + board.getBackEnd() + "...");
			BoardConnector connector = ReflectionObject.of(board).getField("connector").get();
			connector.remotePullAll();

			// save to target back-end
			call.getSender().sendMessage("§d> Saving all data to " + toBackEnd + "...");
			fakeConnector.remoteInit();
			fakeConnector.forcePushAllCached();
			fakeConnector.shutdown();

			// unlock plugin
			ReflectionObject.of(plugin).setField("reloading", false);
			call.getSender().sendMessage("§d> Success !");
		});
	}

}
