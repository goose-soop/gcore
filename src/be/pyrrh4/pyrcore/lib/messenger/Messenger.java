package be.pyrrh4.pyrcore.lib.messenger;

import java.util.List;

import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.versioncompat.Compat;

public class Messenger {

	// ------------------------------------------------------------
	// Message type
	// ------------------------------------------------------------

	public enum Level {

		// values
		NORMAL_TITLE("§2", "§2§l", "§2"),
		NORMAL_INFO("§2", "§a", "§7"),
		NORMAL_SUCCESS("§5", "§d", "§7"),
		SEVERE_INFO("§6", "§e", "§7"),
		SEVERE_ERROR("§4", "§c", "§7");

		// base
		private String colorTitle, colorVariable, colorText;

		private Level(String colorTitle, String colorVariable, String colorText) {
			this.colorTitle = colorTitle;
			this.colorVariable = colorVariable;
			this.colorText = colorText;
		}

		// get
		public String getColorTitle() {
			return colorTitle;
		}

		public String getColorVariable() {
			return colorVariable;
		}

		public String getColorText() {
			return colorText;
		}

		// methods
		public List<String> format(String prefix, List<String> text) {
			return Utils.addBeforeAll(new Replacer("<", colorVariable, ">", colorText).apply(text), colorTitle + getPrefix(prefix) + colorText);
		}

		public String format(String prefix, String text) {
			return colorTitle + getPrefix(prefix) + colorText + new Replacer("<", colorVariable, ">", colorText).apply(text);
		}

	}

	// ------------------------------------------------------------
	// Static fields
	// ------------------------------------------------------------

	public static final String SEPARATOR = "§2-----------------------------------------------------";

	// ------------------------------------------------------------
	// Send message
	// ------------------------------------------------------------

	// Single

	public static void send(Object target, Level level, String prefix, String message, Object... replacers)
	{
		String text = level.format(prefix, message);

		if (replacers != null && replacers.length > 0) {
			text = new Replacer(replacers).apply(text);
		}
		
		new MessageTarget(target).send(text);
	}

	// Multiple

	public static void send(Object target, Level level, String prefix, List<String> message, Object... replacers)
	{
		List<String> text = level.format(prefix, message);

		if (replacers != null && replacers.length > 0) {
			text = new Replacer(replacers).apply(text);
		}

		new MessageTarget(target).send(text);
	}

	// Simple

	public static void send(Object target, String message, Object... replacers)
	{
		String text = message;

		if (replacers != null && replacers.length > 0) {
			text = new Replacer(replacers).apply(text);
		}

		new MessageTarget(target).send(text);
	}

	public static void send(Object target, List<String> message, Object... replacers) {
		List<String> text = message;
		if (replacers != null && replacers.length > 0) {
			text = new Replacer(replacers).apply(text);
		}
		new MessageTarget(target).send(text);
	}

	// ------------------------------------------------------------
	// Send tab, title and action bar
	// ------------------------------------------------------------

	public static void tab(Player player, String header, String footer, Object... replacers)
	{
		if (replacers != null && replacers.length > 0)
		{
			Replacer replacer = new Replacer(replacers);
			header = replacer.apply(header);
			footer = replacer.apply(footer);
		}

		Compat.INSTANCE.changeTab(player, header, footer);
	}

	public static void title(Player player, String title, String subtitle, int fadeIn, int duration, int fadeOut, Object... replacers)
	{
		if (replacers != null && replacers.length > 0)
		{
			Replacer replacer = new Replacer(replacers);
			title = replacer.apply(title);
			subtitle = replacer.apply(subtitle);
		}

		Compat.INSTANCE.sendTitle(player, title, subtitle, fadeIn, duration, fadeOut);
	}

	public static void actionBar(Player player, String bar, Object... replacers)
	{
		if (replacers != null && replacers.length > 0)
		{
			Replacer replacer = new Replacer(replacers);
			bar = replacer.apply(bar);
		}

		Compat.INSTANCE.sendActionBar(player, bar);
	}

	// ------------------------------------------------------------
	// Utils
	// ------------------------------------------------------------

	private static String getPrefix(String prefix) {
		return prefix == null || prefix.isEmpty() ? "" : prefix + " >> ";
	}

}
