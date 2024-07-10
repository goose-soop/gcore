package com.guillaumevdn.gcore.lib.string.placeholder;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.concurrency.RWWeakHashMap;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public interface Replacer {

	@Nonnull
	ReplacerData getReplacerData();

	// ----- parsing
	default String parse(String string) {
		if (StringUtils.hasPlaceholders(string)) {
			ReplacerData data = getReplacerData();

			// since we don't know in which order placeholders will go (containers that contain placeholders parsed by custom, or
			// the opposite), we parse both twice (as long as there are placeholders)

			if (data.getCustom() != null && StringUtils.hasPlaceholders(string)) {
				string = data.getCustom().apply(string);
			}
			if (StringUtils.hasPlaceholders(string)) {
				string = PlaceholderReplacer.parseAll(string, data.getPlayer(), true /* always silence math errors at first try */);
			}
			if (data.getCustom() != null && StringUtils.hasPlaceholders(string)) {
				string = data.getCustom().apply(string);
			}
			if (StringUtils.hasPlaceholders(string)) {
				string = PlaceholderReplacer.parseAll(string, data.getPlayer(), getReplacerData().mustSilenceMathErrors());
			}

			if (string.contains("@removeline"))
				string = "";
		}
		return string;
	}

	default List<String> parse(List<String> raw) {
		return parse(raw, true);
	}

	default List<String> parse(List<String> raw, boolean clone) {
		// this method don't use the parse(String) method because otherwise multi-line placeholders are translated to one line

		if (raw == null) {
			return null;
		}
		ReplacerData data = getReplacerData();

		// since we don't know in which order placeholders will go (containers that contain placeholders parsed by custom, or
		// the opposite), we parse both twice (as long as there are placeholders)

		List<String> parsed;
		if (data.getCustom() != null) {
			parsed = data.getCustom().apply(raw, clone);
		} else {
			parsed = clone ? CollectionUtils.asList(raw) : raw;
		}
		if (StringUtils.hasPlaceholders(parsed)) {
			for (int i = 0; i < parsed.size(); ++i) {
				String r = parsed.get(i);
				if (StringUtils.hasPlaceholders(r)) {
					parsed.set(i, PlaceholderReplacer.parseAll(r, data.getPlayer(), true /* silence math errors anyways first */));
				}
			}
		}
		if (data.getCustom() != null && StringUtils.hasPlaceholders(parsed)) {
			parsed = data.getCustom().apply(parsed, false);
		}
		if (StringUtils.hasPlaceholders(parsed)) {
			for (int i = 0; i < parsed.size(); ++i) {
				String r = parsed.get(i);
				if (StringUtils.hasPlaceholders(r)) {
					parsed.set(i, PlaceholderReplacer.parseAll(r, data.getPlayer(), getReplacerData().mustSilenceMathErrors()));
				}
			}
		}

		for (int i = 0; i < parsed.size(); ++i) {
			if (parsed.get(i).contains("@removeline")) {
				parsed.remove(i);
				--i;
			}
		}

		return parsed;
	}

	default ItemStack parse(ItemStack original) {
		ItemStack copy = original == null ? null : original.clone();
		if (copy == null || !copy.hasItemMeta()) {
			return copy;
		}
		ItemMeta meta = copy.getItemMeta();
		if (!meta.hasDisplayName() && (!meta.hasLore() || meta.getLore().isEmpty())) {
			return copy;
		}
		if (meta.hasDisplayName()) {
			meta.setDisplayName(parse(meta.getDisplayName()));
		}
		if (meta.hasLore()) {
			meta.setLore(parse(meta.getLore()));
		}
		copy.setItemMeta(meta);
		return copy;
	}

	// ----- alterer
	default Replacer cloneReplacer() {
		ReplacerData data = getReplacerData().clone();
		return new Replacer() {

			@Override
			public ReplacerData getReplacerData() {
				return data;
			}

		};
	}

	default Replacer with(Player player) {
		getReplacerData().with(player);
		return this;
	}

	default Replacer withPlayer(Supplier<Player> player) {
		getReplacerData().withPlayer(player);
		return this;
	}

	default Replacer with(Location location) {
		getReplacerData().with(location);
		return this;
	}

	default Replacer withLocation(Supplier<Location> location) {
		getReplacerData().withLocation(location);
		return this;
	}

	default Replacer with(String placeholder, Supplier<Object> replacer) {
		getReplacerData().with(placeholder, replacer);
		return this;
	}

	default Replacer with(String customMatcherDesc, CustomMatcher customMatcher) {
		getReplacerData().with(customMatcherDesc, customMatcher);
		return this;
	}

	default Replacer replaceCustom(String customMatcherDesc, CustomMatcher customMatcher) {
		getReplacerData().replaceCustom(customMatcherDesc, customMatcher);
		return this;
	}

	default Replacer with(StringReplacer custom) {
		getReplacerData().with(custom);
		return this;
	}

	default Replacer with(Replacer other) {
		if (other.getReplacerData().getPlayer() != null) {
			getReplacerData().with(other.getReplacerData().getPlayer());
		}
		if (other.getReplacerData().getLocation() != null) {
			getReplacerData().with(other.getReplacerData().getLocation());
		}
		getReplacerData().with(other.getReplacerData().getCustom());
		return this;
	}

	default Replacer formatNumbers(boolean formatNumbers) {
		getReplacerData().formatNumbers(formatNumbers);
		return this;
	}

	default Replacer silenceMathErrors(boolean silenceMathErrors) {
		getReplacerData().silenceMathErrors(silenceMathErrors);
		return this;
	}

	// ----- to string
	default String describeReplacer() {
		ReplacerData data = getReplacerData();
		Location loc = data.getLocation();
		return (data.getPlayer() != null ? "player " + data.getPlayer().getName() : "generic") + (loc != null ? "/location" : "")
				+ (data.getCustom() != null ? "/custom" : "");
	}

	// ----- creation
	static final Replacer GENERIC = empty();
	static final RWWeakHashMap<Player, Replacer> JUST_PLAYER_REPLACER_CACHE = new RWWeakHashMap<>(10, 1f); // initializing a Replacer just for the player can be
																											// consuming if done often ; this optimizes a few
																											// things, especially in recurring tasks
	// FIXME : ^ remove this cache if still issues

	static Replacer justPlayer(Player player) {
		return JUST_PLAYER_REPLACER_CACHE.computeIfAbsent(player, __ -> of(player));
	}

	static Replacer empty() {
		return new SimpleReplacer(new ReplacerData());
	}

	static Replacer of(Player player) {
		return player == null ? empty() : new SimpleReplacer(new ReplacerData().with(player)); // do not valuesCache players, since this method might be called
																								// to create a replacer with other things
	}

	static Replacer ofPlayer(Supplier<Player> player) {
		return new SimpleReplacer(new ReplacerData().withPlayer(player));
	}

	static Replacer of(Location location) {
		return new SimpleReplacer(new ReplacerData().with(location));
	}

	static Replacer ofLocation(Supplier<Location> location) {
		return new SimpleReplacer(new ReplacerData().withLocation(location));
	}

	static Replacer of(String placeholder, Supplier<Object> replacer) {
		return new SimpleReplacer(new ReplacerData().with(placeholder, replacer));
	}

	static Replacer of(String customMatcherDesc, CustomMatcher customMatcher) {
		return new SimpleReplacer(new ReplacerData().with(customMatcherDesc, customMatcher));
	}

	static Replacer of(StringReplacer custom) {
		return new SimpleReplacer(new ReplacerData().with(custom));
	}

}
