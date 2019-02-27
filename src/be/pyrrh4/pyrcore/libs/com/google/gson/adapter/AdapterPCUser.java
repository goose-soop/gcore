package be.pyrrh4.pyrcore.libs.com.google.gson.adapter;

import java.io.IOException;
import java.util.UUID;

import be.pyrrh4.pyrcore.data.PCUser;
import be.pyrrh4.pyrcore.libs.com.google.gson.TypeAdapter;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonReader;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonToken;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterPCUser extends TypeAdapter<PCUser> {

	@Override
	public PCUser read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		String[] raw = reader.nextString().split("_");
		return new PCUser(UUID.fromString(raw[0]), raw[1]);
	}

	@Override
	public void write(JsonWriter writer, PCUser obj) throws IOException {
		if (obj == null) {
			writer.nullValue();
			return;
		}
		writer.value(obj.getUniqueId().toString() + "_" + obj.getProfile());
	}
}
