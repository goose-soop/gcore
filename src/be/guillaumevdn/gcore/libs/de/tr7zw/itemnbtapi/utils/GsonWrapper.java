package be.guillaumevdn.gcore.libs.de.tr7zw.itemnbtapi.utils;

import be.guillaumevdn.gcore.GCore;

public class GsonWrapper {

	public static String getString(Object obj) {
		return GCore.UNPRETTY_GSON.toJson(obj);
	}

	public static <T> T deserializeJson(String json, Class<T> type) {
		try {
			if (json == null) {
				return null;
			}

			T obj = GCore.UNPRETTY_GSON.fromJson(json, type);
			return type.cast(obj);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
