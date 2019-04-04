package be.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.IOException;

import be.guillaumevdn.gcore.lib.material.Mat;
import be.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import be.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import be.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import be.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

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
		return Mat.from(raw[0], Integer.parseInt(raw[1]));
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
