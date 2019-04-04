package be.guillaumevdn.gcore.libs.com.google.gson.adapter;

import be.guillaumevdn.gcore.libs.com.google.gson.Gson;
import be.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import be.guillaumevdn.gcore.libs.com.google.gson.TypeAdapterFactory;
import be.guillaumevdn.gcore.libs.com.google.gson.reflect.TypeToken;

public class AdapterClassFactory implements TypeAdapterFactory
{
	
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken)
	{
		if (!Class.class.isAssignableFrom(typeToken.getRawType())) {
			return null;
		}

		return (TypeAdapter<T>) new AdapterClass();
	}
}