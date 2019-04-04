package com.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.IOException;

import com.guillaumevdn.gcore.lib.versioncompat.sound.Sound;
import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterSound extends TypeAdapter<Sound>
{
	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

	@Override
	public Sound read(JsonReader reader) throws IOException
	{
		if (reader.peek() == JsonToken.NULL)
		{
			reader.nextNull();
			return null;
		}

		return Sound.valueOf(reader.nextString());
	}

	@Override
	public void write(JsonWriter writer, Sound obj) throws IOException
	{
		if (obj == null)
		{
			writer.nullValue();
			return;
		}

		writer.value(obj.toString());
	}
}
