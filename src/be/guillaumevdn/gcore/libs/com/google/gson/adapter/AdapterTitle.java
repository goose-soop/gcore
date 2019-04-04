package be.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.IOException;

import be.guillaumevdn.gcore.lib.messenger.Title;
import be.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import be.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import be.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import be.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterTitle extends TypeAdapter<Title>
{
	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

	@Override
	public Title read(JsonReader reader) throws IOException
	{
		if (reader.peek() == JsonToken.NULL)
		{
			reader.nextNull();
			return null;
		}

		Title title = new Title();
		reader.beginObject();
		title.setFadeIn(reader.nextInt());
		title.setDuration(reader.nextInt());
		title.setFadeOut(reader.nextInt());
		title.setTitle(reader.nextString());
		title.setSubtitle(reader.nextString());
		reader.endObject();
		return title;
	}

	@Override
	public void write(JsonWriter writer, Title obj) throws IOException
	{
		if (obj == null)
		{
			writer.nullValue();
			return;
		}

		writer.beginObject();
		writer.value(obj.getFadeIn());
		writer.value(obj.getDuration());
		writer.value(obj.getFadeOut());
		writer.value(obj.getTitle());
		writer.value(obj.getSubtitle());
		writer.endObject();
	}
}
