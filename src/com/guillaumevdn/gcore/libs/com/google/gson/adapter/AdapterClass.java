package com.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.IOException;

import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterClass extends TypeAdapter<Class<?>>
{
	@Override
	public void write(JsonWriter jsonWriter, Class<?> clazz) throws IOException
	{
		if (clazz == null){
			jsonWriter.nullValue();
			return;
		}

		jsonWriter.value(clazz.getName());
	}

	@Override
	public Class<?> read(JsonReader jsonReader) throws IOException
	{
		if (jsonReader.peek() == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		}

		Class<?> clazz = null;
		try {
			clazz = Class.forName(jsonReader.nextString());
		} catch (ClassNotFoundException exception) {
			throw new IOException(exception);
		}

		return clazz;
	}
}