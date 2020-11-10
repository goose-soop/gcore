package com.guillaumevdn.gcore.lib.serialization.gson;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.libs.com.google.gson.Gson;
import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapterFactory;
import com.guillaumevdn.gcore.libs.com.google.gson.reflect.TypeToken;

public class GsonAdapterClassFactory implements TypeAdapterFactory {

	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
		if (!ObjectUtils.instanceOf(typeToken, Class.class)) {
			return null;
		}
		return (TypeAdapter<T>) Serializer.of("class", null, value -> value.getName(), string -> ObjectUtils.safeClass(string)).getGsonAdapter();
	}

}
