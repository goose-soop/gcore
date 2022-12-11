package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementCommandList extends ElementStringList {

	public ElementCommandList(Element parent, String id, Need need, Text editorDescription) {
		super(parent, id, need, editorDescription);
	}

	public void execute(OfflinePlayer player) {
		execute(player, null);
	}

	public void execute(OfflinePlayer player, Replacer replacer) {
		final Replacer rep = replacer == null ? Replacer.empty() : replacer.cloneReplacer();
		final List<String> commands = parse(rep.with("{player}", () -> player.getName())).orEmptyList();
		commands.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
	}

	public void execute(List<OfflinePlayer> players, Replacer replacer) {
		players.forEach(pl -> execute(pl, replacer));
	}

	public void execute(Replacer replacer) {
		final Replacer rep = replacer == null ? Replacer.empty() : replacer.cloneReplacer();
		final List<String> commands = parse(rep).orEmptyList();
		commands.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
	}

}
