package com.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.IOException;
import java.math.BigDecimal;

import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterDouble extends TypeAdapter<Double> {

	@Override
	public Double read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		return new BigDecimal(reader.nextString()).doubleValue();
	}

	@Override
	public void write(JsonWriter writer, Double obj) throws IOException {
		if (obj == null) {
			writer.nullValue();
			return;
		}
		writer.value(BigDecimal.valueOf(obj).toPlainString());
	}

}
