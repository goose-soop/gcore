package com.guillaumevdn.gcore.lib.serialization.gson;

import java.io.IOException;

import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

/**
 * @author GuillaumeVDN
 */
public class SerializableGsonAdapter<T> extends TypeAdapter<T> {
	
	private final Serializer<T> serializer;

	public SerializableGsonAdapter(Serializer<T> serializer) {
		this.serializer = serializer;
	}

	// ----- get
	public Serializer<T> getSerializer() {
		return serializer;
	}

	// ----- json
	@Override
	public T read(JsonReader reader) throws IOException {
		if (reader.peek().equals(JsonToken.NULL)) {
			reader.nextNull();
			return null;
		}
		return serializer.deserialize(reader.nextString());
	}

	@Override
	public void write(JsonWriter writer, T value) throws IOException {
		if (value == null) {
			writer.nullValue();
		} else {
			writer.value(serializer.serialize(value));
		}
	}

}
