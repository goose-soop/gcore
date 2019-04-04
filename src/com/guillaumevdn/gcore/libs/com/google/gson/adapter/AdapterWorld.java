package com.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterWorld extends TypeAdapter<World>
{
	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

	@Override
	public World read(JsonReader reader) throws IOException
	{
		if (reader.peek() == JsonToken.NULL)
		{
			reader.nextNull();
			return null;
		}

		return Bukkit.getWorld(reader.nextString());
	}

	@Override
	public void write(JsonWriter writer, World obj) throws IOException
	{
		if (obj == null)
		{
			writer.nullValue();
			return;
		}

		writer.value(obj.getName());
	}
}
