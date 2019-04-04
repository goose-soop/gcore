package be.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.File;
import java.io.IOException;

import be.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import be.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import be.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import be.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterFile extends TypeAdapter<File>
{
	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

	@Override
	public File read(JsonReader reader) throws IOException
	{
		if (reader.peek() == JsonToken.NULL)
		{
			reader.nextNull();
			return null;
		}

		return new File(reader.nextString());
	}

	@Override
	public void write(JsonWriter writer, File obj) throws IOException
	{
		if (obj == null)
		{
			writer.nullValue();
			return;
		}

		writer.value(obj.getPath());
	}
}
