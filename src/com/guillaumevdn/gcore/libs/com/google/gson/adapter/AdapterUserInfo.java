package com.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.IOException;
import java.util.UUID;

import com.guillaumevdn.gcore.data.UserInfo;
import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterUserInfo extends TypeAdapter<UserInfo> {

	@Override
	public UserInfo read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		String[] raw = reader.nextString().split("_");
		return new UserInfo(UUID.fromString(raw[0]), raw[1]);
	}

	@Override
	public void write(JsonWriter writer, UserInfo obj) throws IOException {
		if (obj == null) {
			writer.nullValue();
			return;
		}
		writer.value(obj.getUniqueId().toString() + "_" + obj.getProfile());
	}
}
