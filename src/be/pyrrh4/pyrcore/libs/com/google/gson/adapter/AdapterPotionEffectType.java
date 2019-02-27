package be.pyrrh4.pyrcore.libs.com.google.gson.adapter;

import java.io.IOException;

import org.bukkit.potion.PotionEffectType;

import be.pyrrh4.pyrcore.libs.com.google.gson.TypeAdapter;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonReader;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonToken;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterPotionEffectType extends TypeAdapter<PotionEffectType>
{
	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

	@Override
	public PotionEffectType read(JsonReader reader) throws IOException
	{
		if (reader.peek() == JsonToken.NULL)
		{
			reader.nextNull();
			return null;
		}

		return PotionEffectType.getByName(reader.nextString());
	}

	@Override
	public void write(JsonWriter writer, PotionEffectType obj) throws IOException
	{
		if (obj == null)
		{
			writer.nullValue();
			return;
		}

		writer.value(obj.getName());
	}
}
