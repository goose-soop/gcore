/**
 * Parts of this code was from ZQuest, it was refactored by GuillaumeVDN
 */

package com.guillaumevdn.gcore.lib.legacy_npc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.collect.LinkedHashMultimap;

public class MojangsterAPI {

	// ----- static base
	public static final String NAME_TO_SKIN = "http://minotar.net/helm/<player>/16.png";
	public static final String UUID_TO_PROPERTY = "http://api.mcplayerindex.com/raw/<uuid>/signed";
	public static final String UUID_TO_NAME = " https://api.mojang.com/user/profiles/<uuid>/names";
	public static final String NAME_TO_UUID = "https://api.mojang.com/users/profiles/minecraft/<player>";
	public static final String MOJANG_STATUS = "http://status.mojang.com/check";

	// ----- static methods
	public static HashMap<String, Boolean> getMojangStatus() {
		final HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
		try {
			final InputStream inputStream = makeConnection(new URL("http://status.mojang.com/check")).getInputStream();
			try {
				final JSONArray jsonArray = (JSONArray)new JSONParser().parse(readAll(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))));
				for (int i = 0; i < jsonArray.size(); ++i) {
					final JSONObject jsonObject = (JSONObject)jsonArray.get(i);
					final String s = (String) jsonObject.keySet().iterator().next();
					boolean b = false;
					final String s2 = (String)jsonObject.get(s);
					switch (s2) {
					case "green": {
						b = true;
						break;
					}
					case "yellow": {
						b = false;
						break;
					}
					case "red": {
						b = false;
						break;
					}
					}
					hashMap.put(s, b);
				}
			}
			finally {
				inputStream.close();
			}
		} catch (ParseException | IOException exception) {
			exception.printStackTrace();
		}
		return hashMap;
	}

	public static Object[] getSkinPropandNameTest(UUID uuid) {
		Object[] array = new Object[2];
		LinkedHashMultimap create = LinkedHashMultimap.create();
		try {
			InputStream inputStream = (makeConnection(new URL("http://api.mcplayerindex.com/raw/<uuid>/signed".replaceAll("<uuid>", uuid.toString().replaceAll("-", ""))))).getInputStream();
			try {
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(readAll(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))));
				array[0] = jsonObject.get("name");
				JSONArray jsonArray = (JSONArray)jsonObject.get("properties");
				for (int i = 0; i < jsonArray.size(); ++i) {
					JSONObject jsonObject2 = (JSONObject)jsonArray.get(i);
					if (jsonObject2.containsKey("name") && jsonObject2.get("name").equals("textures")) {
						create.put("textures", new WrappedSignedProperty((String)jsonObject2.get("name"), (String)jsonObject2.get("value"), (String)jsonObject2.get("signature")));
						array[1] = create;
						break;
					}
				}
			}
			finally {
				inputStream.close();
			}
		}
		catch (ParseException | IOException ex) {
			return null;
		}
		return array;
	}

	public static Object[] getSkinPropandName(UUID uuid) {
		Object[] array = new Object[2];
		LinkedHashMultimap create = LinkedHashMultimap.create();
		try {
			array[0] = getName(uuid);
		} catch (Throwable premiumPlayerEx) {
			premiumPlayerEx.printStackTrace();
		}
		Object handle = WrappedGameProfile.fromOfflinePlayer(Bukkit.getOfflinePlayer(uuid)).getHandle();
		Object sessionService = getSessionService();
		try {
			getFillMethod(sessionService).invoke(sessionService, handle, true);
		} catch (IllegalAccessException exception) {
			exception.printStackTrace();
			return null;
		} catch (Throwable ignored) {
			return null;
		}
		Object[] arr = WrappedGameProfile.fromHandle(handle).getProperties().get("textures").toArray();
		if (arr.length == 0) return null;
		create.put("textures", arr[0]);
		array[1] = create;
		return array;
	}

	public static String getName(UUID uuid) {
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection)makeConnection(new URL(" https://api.mojang.com/user/profiles/<uuid>/names".replaceAll("<uuid>", uuid.toString().replaceAll("-", ""))));
			if (httpURLConnection.getResponseCode() == 204 || httpURLConnection.getResponseCode() == 404) {
				throw new Error(uuid + " is not an existing player UUID");
			}
			InputStream inputStream = httpURLConnection.getInputStream();
			try {
				return (String)((JSONObject)((JSONArray)new JSONParser().parse(readAll(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))))).get(0)).get("name");
			}
			finally {
				inputStream.close();
			}
		} catch (ParseException | IOException exception) {
			exception.printStackTrace();
			return null;
		}
	}

	// ----- static utils
	private static String readAll(Reader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		int read;
		while ((read = reader.read()) != -1) {
			sb.append((char)read);
		}
		return sb.toString();
	}

	public static UUID getUUIDfromString(String s) {
		return (s == null) ? null : UUID.fromString(s.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
	}

	private static URLConnection makeConnection(URL url) throws IOException {
		URLConnection openConnection = url.openConnection();
		openConnection.setConnectTimeout(10000);
		openConnection.setReadTimeout(10000);
		openConnection.setUseCaches(false);
		openConnection.setDefaultUseCaches(false);
		openConnection.addRequestProperty("User-Agent", "Mozilla/5.0");
		openConnection.addRequestProperty("Cache-Control", "no-valuesCache, no-store, must-revalidate");
		openConnection.addRequestProperty("Pragma", "no-valuesCache");
		return openConnection;
	}

	private static Object getSessionService() {
		Server server = Bukkit.getServer();
		try {
			Object invoke = server.getClass().getDeclaredMethod("getServer", new Class[0]).invoke(server);
			for (Method method : invoke.getClass().getMethods()) {
				if (method.getReturnType().getSimpleName().equalsIgnoreCase("MinecraftSessionService")) {
					return method.invoke(invoke);
				}
			}
		}
		catch (Exception ex) {
			throw new IllegalStateException("An error occurred while trying to get the session service", ex);
		}
		throw new IllegalStateException("No session service found :o");
	}

	private static Method getFillMethod(Object o) {
		for (Method method : o.getClass().getDeclaredMethods()) {
			if (method.getName().equals("fillProfileProperties")) {
				return method;
			}
		}
		throw new IllegalStateException("No fillProfileProperties method found in the session service :o");
	}

}
