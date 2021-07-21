package com.guillaumevdn.gcore.lib.serialization.adapter;

import java.util.Collection;

import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;

/**
 * @author GuillaumeVDN
 */
public class YMLAdapter<T> {

	private DataAdapter<T> adapter;

	public YMLAdapter(DataAdapter<T> serializer) {
		this.adapter = serializer;
	}

	// ----- write
	public void write(YMLConfiguration writer, String path, T value) throws Throwable {
		writer.write(path, null); // reset path
		if (value == null) {
			return;
		}
		DataIO data = new DataIO();
		try {
			adapter.write(value, data);
		} catch (Throwable exception) {
			throw new Throwable(exception);
		}
		writeObject(writer, path, data);
	}

	private void writeObject(YMLConfiguration writer, String path, DataIO data) throws Throwable {
		for (String key : data.getKeys()) {
			writeValue(writer, path + "." + key, data.read(key));
		}
	}

	private void writeValue(YMLConfiguration writer, String path, Object value) throws Throwable {
		if (value == null) {
			writer.write(path, null);
		} else if (value instanceof DataIO) {
			writeObject(writer, path, (DataIO) value);
		} else if (value instanceof Collection) {
			writer.write(path, (Collection) value);
		} else {
			writer.write(path, value); // writer will find a matching serializer
		}
	}

	// ----- read
	public T read(YMLConfiguration reader, String path, boolean snakeCaseToCamelCase) throws Throwable {
		if (!reader.contains(path)) {
			return null;
		}
		// not an object
		if (!reader.isConfigurationSection(path)) {
			throw new IllegalStateException("expected configuration section but found value when reading " + adapter.getType());
		}
		// read object and serializer version
		DataIO data = reader.readObject(path, snakeCaseToCamelCase);
		// decode object
		try {
			return adapter.read(data);
		} catch (Throwable exception) {
			throw new Throwable(exception);
		}
	}

}
