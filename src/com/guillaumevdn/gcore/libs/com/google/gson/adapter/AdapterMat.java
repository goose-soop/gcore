package com.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.IOException;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterMat extends TypeAdapter<Mat> {

	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

	@Override
	public Mat read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		String[] raw = reader.nextString().split(",");
		return Mat.valueOf(raw[0], Integer.parseInt(raw[1]));
	}

	@Override
	public void write(JsonWriter writer, Mat obj) throws IOException {
		if (obj == null) {
			writer.nullValue();
			return;
		}
		writer.value(obj.getModernName() + "," + obj.getDurability());
	}

}
