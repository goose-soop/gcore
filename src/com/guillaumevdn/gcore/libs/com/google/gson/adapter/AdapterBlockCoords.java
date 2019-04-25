package com.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.IOException;

import com.guillaumevdn.gcore.lib.util.BlockCoords;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterBlockCoords extends TypeAdapter<BlockCoords> {

	@Override
	public BlockCoords read(JsonReader reader) throws IOException {
		// null
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		// unserialize
		return Utils.unserializeBlockCoords(reader.nextString());
	}

	@Override
	public void write(JsonWriter writer, BlockCoords obj) throws IOException {
		// null
		if (obj == null) {
			writer.nullValue();
			return;
		}
		// serialized
		writer.value(Utils.serializeBlockCoords(obj));
	}
}
