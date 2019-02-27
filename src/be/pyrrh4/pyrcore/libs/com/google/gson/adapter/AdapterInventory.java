package be.pyrrh4.pyrcore.libs.com.google.gson.adapter;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.libs.com.google.gson.TypeAdapter;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonReader;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonToken;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterInventory extends TypeAdapter<Inventory> {

	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

	@Override
	public Inventory read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		// begin
		reader.beginObject();
		// size
		reader.nextName();
		int size = reader.nextInt();
		// title
		reader.nextName();
		String title = reader.nextString();
		// init inventory
		Inventory inventory = Bukkit.createInventory(null, size, title);
		// content
		while (!reader.peek().equals(JsonToken.END_OBJECT)) {
			int slot = Integer.parseInt(reader.nextName().substring("item-".length()));
			ItemStack item = Utils.unserializeItem(reader.nextString());
			inventory.setItem(slot, item);
		}
		// end
		reader.endObject();
		return inventory;
	}

	@Override
	public void write(JsonWriter writer, Inventory obj) throws IOException {
		if (obj == null) {
			writer.nullValue();
			return;
		}
		writer.beginObject();
		// inv
		writer.name("size").value(obj.getSize());
		writer.name("title").value(obj.getTitle());
		// items
		for (int i = 0; i < obj.getContents().length; i++) {
			ItemStack item = obj.getContents()[i];
			if (item != null && !item.getType().equals(Material.AIR)) {
				writer.name("item-" + i);
				writer.value(Utils.serializeItem(item));
			}
		}
		writer.endObject();
	}

}
