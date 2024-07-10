package com.guillaumevdn.gcore.lib.serialization.adapter;

import com.guillaumevdn.gcore.lib.serialization.data.DataIO;

/**
 * @author GuillaumeVDN
 */
public abstract class DataAdapter<T> {

	private final Class<T> type;
	private final int version;
	private final GsonAdapter<T> adapterGson = new GsonAdapter<>(this);
	private final YMLAdapter<T> adapterYML = new YMLAdapter<>(this);

	public DataAdapter(Class<T> type, int version) {
		this.type = type;
		this.version = version;
	}

	// ----- get
	public final Class<T> getType() {
		return type;
	}

	public final int getVersion() {
		return version;
	}

	public final GsonAdapter<T> getGsonAdapter() {
		return adapterGson;
	}

	public final YMLAdapter<T> getYMLAdapter() {
		return adapterYML;
	}

	// ----- set
	public abstract void write(T object, DataIO writer) throws Throwable;

	public abstract T read(int version, DataIO reader) throws Throwable;

	public final T read(DataIO reader) throws Throwable {
		Integer version = reader.readInteger("version");
		if (version == null) {
			throw new IllegalStateException("didn't find version when reading " + getType());
		}
		return read(version, reader);
	}

	public final T readCurrent(DataIO reader) throws Throwable {
		return read(getVersion(), reader);
	}

}
