package com.guillaumevdn.gcore.lib.player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;

import com.guillaumevdn.gcore.GCore;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;

/**
 * @author GuillaumeVDN
 */
public final class MojangUtils {

	@Nullable
	public static UUID fetchUUID(String name) throws Throwable {
		NameAnswer answer = jsonRequest("https://api.mojang.com/users/profiles/minecraft/" + name + "?at=" + (System.currentTimeMillis() / 1000L - 100L) + "&unsigned=false", NameAnswer.class);
		if (answer != null && answer.trimmedUUID != null) {  // content and uuid found
			StringBuilder builder = new StringBuilder(36);
			char[] ch = answer.trimmedUUID.toCharArray();
			for (int i = 0; i < ch.length; ++i) {
				if (i == 8 || i == 13 || i == 18 || i == 23) {
					builder.append('-');
				}
				builder.append(ch[i]);
			}
			return UUID.fromString(builder.toString());
		}
		return null;  // no content or no uuid
	}

	@Nullable
	public static GameProfile fetchProfile(UUID uuid) throws Throwable {
		String suuid = UUIDTypeAdapter.fromUUID(uuid);
		ProfileAnswer answer = jsonRequest("https://sessionserver.mojang.com/session/minecraft/profile/" + suuid + "?unsigned=false", ProfileAnswer.class);
		if (answer == null) return null;
		if (answer.name == null) throw new Error("No name found");
		if (answer.properties == null || answer.properties.isEmpty()) throw new Error("No properties found (" + suuid + ")");
		for (ProfileProperty property : answer.properties) {
			if (property.name.equalsIgnoreCase("textures")) {
				// find textures
				if (property.value == null) throw new Error("No value found in textures property (" + suuid + ")");
				if (property.signature == null) throw new Error("No signature found in textures property (" + suuid + ")");
				// we good
				GameProfile profile = new GameProfile(uuid, answer.name);
				profile.getProperties().put("textures", new Property("textures", property.value, property.signature));
				return profile;
			}
		}
		throw new Error("No textures property found (" + suuid + ")");
	}

	@Nullable
	private static <T> T jsonRequest(String url, Class<T> answerType) throws Throwable {
		HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
		if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
			// read json
			String json = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			for (String line; (line = reader.readLine()) != null; ) {
				json += line;
			}
			// read answer
			try {
				return GCore.inst().getGson().fromJson(json, answerType);
			} catch (Throwable exception) {
				System.out.println("Raw json : " + json);
				throw new Error("couldn't read answer, for " + url, exception);
			}
		} else if (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
			return null;
		} else {
			throw new Error("Reponse code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ", for " + url);
		}
	}

	private static class NameAnswer {
		private String trimmedUUID;
	}

	private static class ProfileAnswer {
		private String name;
		private List<ProfileProperty> properties;
	}

	private static class ProfileProperty {
		private String name, value, signature;
	}

}
