package com.guillaumevdn.gcore.libs.com.google.gson.adapter;

import com.guillaumevdn.gcore.libs.com.google.gson.Gson;
import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapterFactory;
import com.guillaumevdn.gcore.libs.com.google.gson.reflect.TypeToken;

public class AdapterClassFactory implements TypeAdapterFactory {

	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
		if (!Class.class.isAssignableFrom(typeToken.getRawType())) {
			return null;
		}
		return (TypeAdapter<T>) new AdapterClass();
	}

}
