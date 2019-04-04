package com.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.IOException;

import org.bukkit.enchantments.Enchantment;

import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterEnchantment extends TypeAdapter<Enchantment>
{
	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

	@Override
	public Enchantment read(JsonReader reader) throws IOException
	{
		if (reader.peek() == JsonToken.NULL)
		{
			reader.nextNull();
			return null;
		}

		return Enchantment.getByName(reader.nextString());
	}

	@Override
	public void write(JsonWriter writer, Enchantment obj) throws IOException
	{
		if (obj == null)
		{
			writer.nullValue();
			return;
		}

		writer.value(obj.getName());
	}
}
