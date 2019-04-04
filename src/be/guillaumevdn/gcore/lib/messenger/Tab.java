package be.guillaumevdn.gcore.lib.messenger;

import java.util.Collection;

import org.bukkit.entity.Player;

import be.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import be.guillaumevdn.gcore.lib.util.Utils;

public class Tab {

	// base
	private String header, footer;

	public Tab() {
		this("", "");
	}

	public Tab(YMLConfiguration config, String path) {
		this(config.getString(path + ".header", "Header " + path), config.getString(path + ".footer", "Footer " + path));
	}

	public Tab(String header, String footer) {
		this.header = header;
		this.footer = footer;
	}

	// get
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	// methods
	public void send(Player player, Object... replacers) {
		Messenger.tab(player, Utils.fillPlaceholderAPI(player, header), Utils.fillPlaceholderAPI(player, footer), replacers);
	}

	public void send(Collection<Player> players, Object... replacers) {
		for (Player player : players) {
			Messenger.tab(player, Utils.fillPlaceholderAPI(player, header), Utils.fillPlaceholderAPI(player, footer), replacers);
		}
	}

	public void broadcast(Object... replacers) {
		send(Utils.getOnlinePlayers(), replacers);
	}

}
