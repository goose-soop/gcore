package com.guillaumevdn.gcore.lib.chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Compat;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author JustisR and GuillaumeVDN
 */
public class JsonMessage {

	private String msg = "[{\"text\":\"\",\"extra\":[{\"text\": \"\"}";

	public JsonStringBuilder append(String text) {
		return new JsonStringBuilder(this, esc(text));
	}

	private static String esc(String s) {
		return JSONObject.escape(s.replace('&', '§'));
	}

	public void send(Player... players) {
		send(CollectionUtils.asList(players));
	}

	public void send(Collection<Player> players) {
		Compat.sendJsonChat(players, msg + "]}]");
	}

	/**
	 * @author JustisR
	 */
	public static class JsonStringBuilder {

		private final JsonMessage message;
		private final String string = ",{\"text\":\"\",\"extra\":[";
		private List<String> parts = new ArrayList<>();
		private String hover = "", click = "";

		private JsonStringBuilder(JsonMessage jsonMessage, String text) {
			message = jsonMessage;
			// unformatted text
			if (!text.contains("§")) {
				parts.add("{\"text\":\"" + text + "\"}");
			}
			// formatted
			else {
				String[] split = text.split(String.valueOf(ChatColor.COLOR_CHAR));
				for (int i = 0; i < split.length; ++i) {
					// empty text
					if (split[i].isEmpty()) {
						parts.add("{\"text\":\"\"}");
					}
					// with color
					else {
						String ch = split[i].substring(0, 1);
						// hex color code
						if (ch.equalsIgnoreCase("x")) {
							// get code
							String code = "";
							int j = i + 1;
							for (; j < split.length && j < i + 6; ++j) {
								code += split[j];
							}
							String last = j < split.length ? split[j] : null;
							if (last != null && !last.isEmpty()) {  // valid hex code
								parts.add("{\"text\":\"" + last.substring(1) + "\",\"color\":\"#" + (code + last.charAt(0)).toUpperCase() + "\"}");
								i = j;
							} else {  // invalid code
								parts.add("{\"text\":\"" + split[i] + "\"}");
							}
						}
						// regular code
						else {
							ChatColor color = ChatColor.getByChar(ch);
							if (color != null) {
								parts.add("{\"text\":\"" + split[i].substring(1) + "\",\"color\":\"" + color.name().toLowerCase(Locale.US) + "\"}");
							} else {  // unknown color code
								parts.add("{\"text\":\"" + split[i] + "\"}");
							}
						}
					}
				}
			}
		}

		public JsonStringBuilder setHover(String... lore) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < lore.length; i++) {
				builder.append(lore[i] + (i + 1 < lore.length ? "\n" : ""));
			}
			hover = ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + esc(builder.toString()) + "\"}";
			return this;
		}

		public JsonStringBuilder setURL(String link) {
			click = ",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + esc(link).replace("§", "&") + "\"}";
			return this;
		}

		public JsonStringBuilder setSuggest(String cmd) {
			click = ",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + esc(cmd).replace("§", "&") + "\"}";
			return this;
		}

		public JsonStringBuilder setCommand(String cmd) {
			click = ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + esc(cmd).replace("§", "&") + "\"}";
			return this;
		}

		public JsonMessage build() {
			StringBuilder builder = new StringBuilder(message.msg + string);
			builder.append(StringUtils.toTextString(",", parts));
			builder.append("]" + hover + click + "}");
			message.msg = builder.toString();
			return message;
		}

	}

}