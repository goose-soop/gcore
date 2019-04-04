package com.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.IOException;

import org.bukkit.Material;

import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterMaterial extends TypeAdapter<Material> {

	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

	@Override
	public Material read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		return Material.getMaterial(reader.nextString());
	}

	@Override
	public void write(JsonWriter writer, Material obj) throws IOException {
		if (obj == null) {
			writer.nullValue();
			return;
		}
		writer.value(obj.toString());
	}

}
