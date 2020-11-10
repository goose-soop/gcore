package com.guillaumevdn.gcore.lib.serialization.gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.TextElement;
import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

/**
 * @author GuillaumeVDN
 */
public class GsonAdapterText extends TypeAdapter<Text> {

	@Override
	public Text read(JsonReader reader) throws IOException {
		// null
		if (reader.peek().equals(JsonToken.NULL)) {
			reader.nextNull();
			return null;
		}
		// lines
		List<String> lines = new ArrayList<>();
		if (reader.peek().equals(JsonToken.BEGIN_ARRAY)) {
			reader.beginArray();
			while (reader.hasNext()) {
				lines.add(reader.nextString());
			}
			reader.endArray();
		} else {
			lines.add(reader.nextString());
		}
		// return
		if (lines.size() == 1 && lines.get(0).equals("@empty")) {
			lines.clear();
		}
		return new TextElement(lines);
	}

	@Override
	public void write(JsonWriter writer, Text value) throws IOException {
		if (value == null) {
			writer.nullValue();
		} else if (value.size() <= 1) {
			writer.value(value.getCurrentLine(0, "@empty"));
		} else {
			writer.beginArray();
			for (String line : value.getCurrentLines()) {
				writer.value(line);
			}
			writer.endArray();
		}
	}

}
