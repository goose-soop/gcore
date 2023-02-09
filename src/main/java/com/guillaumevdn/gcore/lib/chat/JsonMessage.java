package com.guillaumevdn.gcore.lib.chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	public static class JsonStringBuilder {

		private static final Map<ChatColor, String> FORMAT_CODES = CollectionUtils.asMap(
				ChatColor.MAGIC, "obfuscated",
				ChatColor.BOLD, "bold",
				ChatColor.STRIKETHROUGH, "strikethrough",
				ChatColor.UNDERLINE, "underlined",
				ChatColor.ITALIC, "italic"
				);

		private final JsonMessage message;
		private final String string = ",{\"text\":\"\",\"extra\":[";
		private List<String> parts = new ArrayList<>();
		private String hover = "", click = "";

		private JsonStringBuilder(JsonMessage jsonMessage, String rawText) {
			message = jsonMessage;

			// unformatted text
			if (!rawText.contains("§")) {
				parts.add("{\"text\":\"" + rawText + "\"}");
			}
			// formatted
			else {
				String[] split = rawText.split(String.valueOf(ChatColor.COLOR_CHAR));

				String textColor = "white";
				Set<String> textFormat = new HashSet<>();

				for (int i = 0; i < split.length; ++i) {
					// empty text
					if (split[i].isEmpty()) {
						parts.add("{\"text\":\"\"}");
					}

					// with color
					else {
						String text = split[i];
						String ch = split[i].substring(0, 1);

						// hex code
						if (ch.equalsIgnoreCase("x")) {
							String code = "";
							int j = i + 1;
							for (; j < split.length && j < i + 6; ++j) {
								code += split[j];
							}
							String last = j < split.length ? split[j] : null;
							if (last != null && !last.isEmpty()) {  // valid hex code
								textColor = "#" + (code + last.charAt(0)).toUpperCase();
								textFormat.clear();

								text = last.substring(1);
								i = j;
							} else {  // invalid hex code
								textColor = "white";
								textFormat.clear();
							}
						}
						// regular code
						else {
							text = split[i].substring(1);

							ChatColor color = ChatColor.getByChar(ch);
							if (color != null) {
								String format = FORMAT_CODES.get(color);
								if (format != null) {
									textFormat.add(format);
								} else {
									textFormat.clear();
									textColor = color.name().toLowerCase();
								}
							} else {  // unknown color code
								textFormat.clear();
								textColor = "white";
							}
						}

						// add text with color and all format codes
						String json = "{\"text\":\"" + text + "\",\"color\":\"" + textColor + "\"";
						for (String format : textFormat) {
							json += ",\"" + format + "\":true";
						}
						json += "}";

						parts.add(json);
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