package com.guillaumevdn.gcore.lib.messenger;

import java.util.Collection;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.util.Utils;

public class Title {

	// bases
	private String title, subtitle;
	private int fadeIn, duration, fadeOut;

	public Title() {
		this("", "", 0, 0, 0);
	}

	public Title(YMLConfiguration config, String path) {
		this(config.getStringFormatted(path + ".title", ""), config.getStringFormatted(path + ".subtitle", ""), config.getInt(path + ".fade_in", 5),
				config.getInt(path + ".duration", 50), config.getInt(path + ".fade_out", 5));
	}

	public Title(String title, String subtitle, int fadeIn, int duration, int fadeOut) {
		this.title = title;
		this.subtitle = subtitle;
		this.fadeIn = fadeIn;
		this.duration = duration;
		this.fadeOut = fadeOut;
	}

	// get
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public int getFadeIn() {
		return fadeIn;
	}

	public void setFadeIn(int fadeIn) {
		this.fadeIn = fadeIn;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getFadeOut() {
		return fadeOut;
	}

	public void setFadeOut(int fadeOut) {
		this.fadeOut = fadeOut;
	}

	// methods
	public void send(Player player, Object... replacers) {
		Messenger.title(player, Utils.fillPlaceholderAPI(player, title), Utils.fillPlaceholderAPI(player, subtitle), fadeIn, duration, fadeOut, replacers);
	}

	public void send(Collection<Player> players, Object... replacers) {
		for (Player player : players) {
			Messenger.title(player, Utils.fillPlaceholderAPI(player, title), Utils.fillPlaceholderAPI(player, subtitle), fadeIn, duration, fadeOut, replacers);
		}
	}

	public void broadcast(Object... replacers) {
		send(Utils.getOnlinePlayers(), replacers);
	}

}
