package com.guillaumevdn.gcore.lib.serialization.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

/**
 * @author GuillaumeVDN
 */
public class GsonAdapter<T> extends TypeAdapter<T> {

	private DataAdapter<T> adapter;

	public GsonAdapter(DataAdapter<T> serializer) {
		this.adapter = serializer;
	}

	// ----- write
	@Override
	public void write(JsonWriter writer, T value) throws IOException {
		if (value == null) {
			writer.nullValue();
			return;
		}
		DataIO data = new DataIO();
		data.write("version", adapter.getVersion());
		try {
			adapter.write(value, data);
		} catch (Throwable exception) {
			throw new IOException("error with adapter " + adapter.getType().getSimpleName(), exception);
		}
		writeObject(writer, data);
	}

	private void writeObject(JsonWriter writer, DataIO data) throws IOException {
		writer.beginObject();
		for (String key : data.getKeys()) {
			writer.name(key);
			writeValue(writer, data.read(key));
		}
		writer.endObject();
	}

	private void writeArray(JsonWriter writer, Collection<?> data) throws IOException {
		writer.beginArray();
		for (Object value : data) {
			writeValue(writer, value);
		}
		writer.endArray();
	}

	private void writeValue(JsonWriter writer, Object value) throws IOException {
		if (value == null) {
			writer.nullValue();
		}
		else if (value instanceof DataIO) {
			writeObject(writer, (DataIO) value);
		}
		else if (value instanceof Collection<?>) {
			writeArray(writer, (Collection<?>) value);
		}
		else if (value instanceof Long) {
			writer.value((Long) value);
		}
		else if (NumberUtils.longOrNull(value) != null) {
			writer.value(Long.parseLong((String) value));
		}
		else if (value instanceof Integer) {
			writer.value((Integer) value);
		}
		else if (NumberUtils.integerOrNull(value) != null) {
			writer.value(Integer.parseInt((String) value));
		}
		else if (value instanceof Double) {
			writer.value((Double) value);
		}
		else if (NumberUtils.doubleOrNull(value) != null) {
			writer.value(Double.parseDouble((String) value));
		}
		else if (value instanceof Boolean) {
			writer.value((Boolean) value);
		}
		else if (value instanceof String && (((String) value).equalsIgnoreCase("true") || ((String) value).equalsIgnoreCase("false"))) {
			writer.value(Boolean.parseBoolean((String) value));
		}
		else if (value instanceof String) {
			writer.value((String) value);
		}
		else {
			throw new IllegalArgumentException("can't write value of type " + value.getClass());
		}
	}

	// ----- read
	@Override
	public T read(JsonReader reader) throws IOException {
		if (reader.peek().equals(JsonToken.NULL)) {
			reader.nextNull();
			return null;
		}
		// not an object
		if (!reader.peek().equals(JsonToken.BEGIN_OBJECT)) {
			throw new IllegalStateException("expected object but found " + reader.peek() + " when reading " + adapter.getType());
		}
		// read object and serializer version
		DataIO data = readObject(reader);
		// decode object
		try {
			return adapter.read(data);
		} catch (Throwable exception) {
			throw new IOException(exception);
		}
	}

	private DataIO readObject(JsonReader reader) throws IOException {
		DataIO data = new DataIO();
		reader.beginObject();
		while (reader.hasNext()) {
			data.write(reader.nextName(), readValue(reader));
		}
		reader.endObject();
		return data;
	}

	private List<Object> readArray(JsonReader reader) throws IOException {
		List<Object> list = new ArrayList<>();
		reader.beginArray();
		while (reader.hasNext()) {
			list.add(readValue(reader));
		}
		reader.endArray();
		return list;
	}

	private Object readValue(JsonReader reader) throws IOException {
		switch (reader.peek()) {
		case BEGIN_OBJECT: return readObject(reader);
		case BEGIN_ARRAY: return readArray(reader);
		case STRING: return reader.nextString();
		case NULL: return null;
		case NUMBER:
			String raw = reader.nextString();
			Long l = NumberUtils.longOrNull(raw);
			if (l != null) return l;
			Integer i = NumberUtils.integerOrNull(raw);
			if (i != null) return i;
			Double d = NumberUtils.doubleOrNull(raw);
			if (d != null) return d;
		case BOOLEAN: return reader.nextBoolean();
		default: throw new IllegalStateException("peeked " + reader.peek());
		}
	}

}
