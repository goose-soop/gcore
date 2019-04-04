package com.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.IOException;

import com.guillaumevdn.gcore.lib.messenger.Text;
import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterText extends TypeAdapter<Text> {

	// methods
	@Override
	public Text read(JsonReader reader) throws IOException {
		// null
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		// read
		Text text = new Text();
		reader.beginArray();
		while (reader.hasNext()) {
			text.getLines().add(reader.nextString());
		}
		reader.endArray();
		// return
		return text;
	}

	@Override
	public void write(JsonWriter writer, Text obj) throws IOException {
		// null
		if (obj == null) {
			writer.nullValue();
			return;
		}
		// write
		writer.beginArray();
		for (String line : obj.getLines()) {
			writer.value(line);
		}
		writer.endArray();
	}

}
