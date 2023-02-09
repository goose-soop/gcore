package com.guillaumevdn.gcore.lib.string;

import java.util.List;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.player.PlayerUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.CustomMatcher;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public interface Text {

	// ----- get
	List<String> getCurrentLines();

	default int size() {
		return getCurrentLines().size();
	}

	default boolean isEmpty() {
		return getCurrentLines().isEmpty();
	}

	default String getCurrentLine(int index) {
		return getCurrentLine(index, null);
	}

	default String getCurrentLine(int index, String def) {
		List<String> lines = getCurrentLines();
		return index >= 0 && index < lines.size() ? lines.get(index) : def;
	}

	TextType getType();

	// ----- set
	void setLines(List<String> newLines);

	// ----- parse
	default String parseLine() {
		return parseLine(null);
	}

	String parseLine(Replacer replacer);

	default List<String> parseLines() {
		return parseLines(null);
	}

	List<String> parseLines(Replacer replacer);

	// ----- send
	default void send(Object target) {
		parseLines().forEach(line -> PlayerUtils.sendMessage(target, line));
	}

	default void send(Object target, CommandSender cc, boolean onlyCC) {
		parseLines().forEach(line -> PlayerUtils.sendMessage(target, line, cc, onlyCC));
	}

	default void broadcast() {
		send(CollectionUtils.asListMultiple(CommandSender.class, PlayerUtils.getOnline(), Bukkit.getConsoleSender()));
	}

	// ----- replace and parse/send directly
	default ReplacingText replace(Player player) {
		return new ReplacingText(this, Replacer.of(player));
	}

	default ReplacingText replace(Replacer replacer) {
		return new ReplacingText(this, replacer.cloneReplacer());
	}

	default ReplacingText replace(String placeholder, Supplier<Object> replacer) {
		return new ReplacingText(this).replace(placeholder, replacer);
	}

	default ReplacingText replace(String customMatcherDesc, CustomMatcher customMatcher) {
		return new ReplacingText(this).replace(customMatcherDesc, customMatcher);
	}

	class ReplacingText {

		private Text text;
		private Replacer replacer;

		private ReplacingText(Text text) {
			this(text, Replacer.empty());
		}

		private ReplacingText(Text text, Replacer replacer) {
			this.text = text;
			this.replacer = replacer;
		}

		public ReplacingText replace(Player player) {
			this.replacer.with(player);
			return this;
		}

		public ReplacingText replace(String placeholder, Supplier<Object> replacer) {
			this.replacer.with(placeholder, replacer);
			return this;
		}

		public ReplacingText replace(String customMatcherDesc, CustomMatcher customMatcher) {
			this.replacer.with(customMatcherDesc, customMatcher);
			return this;
		}

		public ReplacingText replace(Replacer other) {
			this.replacer.with(other);
			return this;
		}

		public String parseLine() {
			return text.parseLine(replacer);
		}

		public List<String> parseLines() {
			return text.parseLines(replacer);
		}

		public void send(Object target) {
			parseLines().forEach(line -> PlayerUtils.sendMessage(target, line));
		}

		public void send(Object target, CommandSender cc, boolean onlyCC) {
			parseLines().forEach(line -> PlayerUtils.sendMessage(target, line, cc, onlyCC));
		}

		public void broadcast() {
			send(CollectionUtils.asListMultiple(CommandSender.class, PlayerUtils.getOnline(), Bukkit.getConsoleSender()));
		}

	}

	// ----- type
	public static enum TextType {
		NORMAL,
		RANDOM_LINE
	}

	// ----- fast creation
	public static Text empty() {
		return new TextElement();
	}

	public static Text of(String... lines) {
		return of(CollectionUtils.asList(lines));
	}

	public static Text of(List<String> lines) {
		StringUtils.format(lines);
		return new TextElement(lines);
	}

}
