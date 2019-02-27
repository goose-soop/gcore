package be.pyrrh4.pyrcore.convert.v6;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import be.pyrrh4.pyrcore.libs.com.google.gson.TypeAdapter;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonReader;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonToken;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterPre6Location extends TypeAdapter<Location> {

	@Override
	public Location read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		return unserializeLocation(reader.nextString());
	}

	@Override
	public void write(JsonWriter writer, Location object) throws IOException {
		throw new UnsupportedOperationException();
	}

	private Location unserializeLocation(String raw) {
		if (raw == null || raw.isEmpty()) {
			return null;
		}
		int wIndex = raw.lastIndexOf("_");
		String w = raw.substring(0, wIndex);
		raw = raw.substring(wIndex + 1);
		World world = Bukkit.getWorld(w);
		if (world == null) {
			return null;
		}
		double x = Double.parseDouble(raw.split("s")[0].replace("d", ".").replace("n", "-"));
		double y = Double.parseDouble(raw.split("s")[1].replace("d", ".").replace("n", "-"));
		double z = Double.parseDouble(raw.split("s")[2].replace("d", ".").replace("n", "-"));
		float yaw = Float.parseFloat(raw.split("s")[3].replace("d", ".").replace("n", "-"));
		float pitch = Float.parseFloat(raw.split("s")[4].replace("d", ".").replace("n", "-"));
		return new Location(world, x, y, z, yaw, pitch);
	}

}
