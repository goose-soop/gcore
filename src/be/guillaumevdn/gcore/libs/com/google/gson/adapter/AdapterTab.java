package be.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.IOException;

import be.guillaumevdn.gcore.lib.messenger.Tab;
import be.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import be.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import be.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import be.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterTab extends TypeAdapter<Tab> {

	@Override
	public Tab read(JsonReader reader) throws IOException {
		// null
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		// read
		Tab tab = new Tab();
		reader.beginObject();
		tab.setHeader(reader.nextString());
		tab.setFooter(reader.nextString());
		reader.endObject();
		// return
		return tab;
	}

	@Override
	public void write(JsonWriter writer, Tab obj) throws IOException {
		// null
		if (obj == null) {
			writer.nullValue();
			return;
		}
		// write
		writer.beginObject();
		writer.value(obj.getHeader());
		writer.value(obj.getFooter());
		writer.endObject();
	}
}
