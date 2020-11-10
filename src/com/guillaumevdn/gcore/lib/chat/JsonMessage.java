package com.guillaumevdn.gcore.lib.chat;

import java.util.Collection;
import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Compat;

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
		private final String[] strings;
		private String hover = "", click = "";

		private JsonStringBuilder(JsonMessage jsonMessage, String text) {
			message = jsonMessage;
			String[] colors = text.split(String.valueOf(ChatColor.COLOR_CHAR));
			for (int i = 0; i < colors.length; i++) {
				if (i == 0 && !text.startsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
					colors[i] = "{\"text\":\"" + colors[i] + "\"}";
				} else if (colors[i].length() < 1) {
					colors[i] = "{\"text\":\"\"}";
				} else {
					ChatColor color = ChatColor.getByChar(colors[i].substring(0, 1));
					colors[i] = "{\"text\":\"" + colors[i].substring(1) + "\",\"color\":\"" + color.name().toLowerCase(Locale.US) + "\"}";
				}
				if (i + 1 != colors.length) colors[i] = colors[i] + ",";
			}
			strings = colors;
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
			for (String string : strings) {
				builder.append(string);
			}
			builder.append("]" + hover + click + "}");
			message.msg = builder.toString();
			return message;
		}

	}

}