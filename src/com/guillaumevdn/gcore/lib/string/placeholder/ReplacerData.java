package com.guillaumevdn.gcore.lib.string.placeholder;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author GuillaumeVDN
 */
public final class ReplacerData {

	private Supplier<Player> player = null;
	private Supplier<Location> location = null;
	private StringReplacer custom = StringReplacer.empty();

	// get
	@Nullable
	public Player getPlayer() {
		return player == null ? null : player.get();
	}

	@Nullable
	public Location getLocation() {
		return location == null ? null : location.get();
	}

	@Nullable
	public Location getLocationOrPlayer() {
		return location != null ? location.get() : (player != null ? player.get().getLocation() : null);
	}

	@Nonnull
	public StringReplacer getCustom() {
		return custom;
	}

	// set
	public ReplacerData with(Player player) {
		return withPlayer(() -> player);
	}

	public ReplacerData withPlayer(Supplier<Player> player) {
		this.player = player;
		return this;
	}

	public ReplacerData with(Location location) {
		return withLocation(() -> location);
	}

	public ReplacerData withLocation(Supplier<Location> location) {
		this.location = location;
		return this;
	}

	public ReplacerData with(String placeholder, Supplier<Object> replacer) {
		custom.with(placeholder, replacer);
		return this;
	}

	public ReplacerData with(String customMatcherDesc, CustomMatcher customMatcher) {
		custom.with(customMatcherDesc, customMatcher);
		return this;
	}

	public ReplacerData replaceCustom(String customMatcherDesc, CustomMatcher customMatcher) {
		custom.replaceCustom(customMatcherDesc, customMatcher);
		return this;
	}

	public ReplacerData with(StringReplacer custom) {
		this.custom.with(custom);
		return this;
	}

	public ReplacerData formatNumbers(boolean formatNumbers) {
		this.custom.formatNumbers(formatNumbers);
		return this;
	}

	// str
	@Override
	public String toString() {
		return "player " + getPlayer() + ", location " + getLocation() + ", custom " + custom.toString();
	}

	// clone
	@Override
	public ReplacerData clone() {
		ReplacerData clone = new ReplacerData();
		clone.player = player;
		clone.location = location;
		clone.custom = custom.clone();
		return clone;
	}

}
