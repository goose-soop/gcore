package be.pyrrh4.pyrcore.libs.com.google.gson.adapter;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import be.pyrrh4.pyrcore.libs.com.google.gson.TypeAdapter;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonReader;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonToken;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterPlugin extends TypeAdapter<Plugin> {

	@Override
	public Plugin read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		return Bukkit.getPluginManager().getPlugin(reader.nextString());
	}

	@Override
	public void write(JsonWriter writer, Plugin obj) throws IOException {
		if (obj == null) {
			writer.nullValue();
		} else {
			writer.value(obj.getName());
		}
	}

}
