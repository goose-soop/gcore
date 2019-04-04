package be.guillaumevdn.gcore.libs.com.google.gson.adapter;

import java.io.IOException;

import org.bukkit.inventory.ItemStack;

import be.guillaumevdn.gcore.lib.util.Utils;
import be.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import be.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import be.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import be.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterItemStack extends TypeAdapter<ItemStack> {

	@Override
	public ItemStack read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		return Utils.unserializeItem(reader.nextString());
	}

	@Override
	public void write(JsonWriter writer, ItemStack obj) throws IOException {
		if (obj == null) {
			writer.nullValue();
			return;
		}
		writer.value(Utils.serializeItem(obj));
	}

}
