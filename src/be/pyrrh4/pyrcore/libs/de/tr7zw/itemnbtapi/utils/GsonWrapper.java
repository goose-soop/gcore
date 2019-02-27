package be.pyrrh4.pyrcore.libs.de.tr7zw.itemnbtapi.utils;

import be.pyrrh4.pyrcore.PyrCore;

public class GsonWrapper {

	public static String getString(Object obj) {
		return PyrCore.UNPRETTY_GSON.toJson(obj);
	}

	public static <T> T deserializeJson(String json, Class<T> type) {
		try {
			if (json == null) {
				return null;
			}

			T obj = PyrCore.UNPRETTY_GSON.fromJson(json, type);
			return type.cast(obj);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
