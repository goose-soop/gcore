package be.pyrrh4.pyrcore.libs.com.google.gson.adapter;

import java.io.IOException;

import org.bukkit.Location;

import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.libs.com.google.gson.TypeAdapter;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonReader;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonToken;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterLocation extends TypeAdapter<Location> {

	@Override
	public Location read(JsonReader reader) throws IOException {
		// null
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		// unserialize
		return Utils.unserializeWXYZLocation(reader.nextString());
	}

	@Override
	public void write(JsonWriter writer, Location obj) throws IOException {
		// null
		if (obj == null) {
			writer.nullValue();
			return;
		}
		// serialized
		writer.value(Utils.serializeWXYZLocation(obj));
	}
}
