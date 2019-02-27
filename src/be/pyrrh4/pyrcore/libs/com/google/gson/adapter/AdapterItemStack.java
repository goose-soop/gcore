package be.pyrrh4.pyrcore.libs.com.google.gson.adapter;

import java.io.IOException;

import org.bukkit.inventory.ItemStack;

import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.libs.com.google.gson.TypeAdapter;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonReader;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonToken;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonWriter;

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
