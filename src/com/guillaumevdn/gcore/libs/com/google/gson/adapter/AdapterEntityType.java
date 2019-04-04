package com.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.IOException;

import org.bukkit.entity.EntityType;

import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterEntityType extends TypeAdapter<EntityType>
{
	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

	@Override
	public EntityType read(JsonReader reader) throws IOException
	{
		if (reader.peek() == JsonToken.NULL)
		{
			reader.nextNull();
			return null;
		}

		return EntityType.valueOf(reader.nextString());
	}

	@Override
	public void write(JsonWriter writer, EntityType obj) throws IOException
	{
		if (obj == null)
		{
			writer.nullValue();
			return;
		}

		writer.value(obj.toString());
	}
}
