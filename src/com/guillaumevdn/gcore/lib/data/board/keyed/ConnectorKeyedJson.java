package com.guillaumevdn.gcore.lib.data.board.keyed;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.guillaumevdn.gcore.lib.file.FileUtils;

/**
 * @author GuillaumeVDN
 */
public abstract class ConnectorKeyedJson<K, V> extends ConnectorKeyed<K, V> {

	public ConnectorKeyedJson(KeyedBoard<K, V> board) {
		super(board);
	}

	// ----- wrapper and file
	public abstract File getRoot();
	public abstract File getFile(K key);
	public abstract K getKey(File file);

	// ----- init
	@Override
	public final void remoteInit() throws Throwable {
		getRoot().mkdirs();
	}

	// ----- pull
	@Override
	public final void remotePullAll() throws Throwable {
		File root = getRoot();
		if (root.exists()) {
			Set<K> keys = new HashSet<>();
			for (File file : root.listFiles()) {
				if (file.isFile()) {
					K key = getKey(file);
					if (key != null) {
						keys.add(key);
					}
				}
			}
			remotePullElements(keys);
		}
	}

	@Override
	public void remotePullElements(Set<K> keys) throws Throwable {
		for (K key : keys) {
			File file = getFile(key);
			if (file.exists()) {
				try (FileReader reader = new FileReader(file)) {
					V value = valueFromJson(reader);
					if (value != null) {
						board.putInCache(key, value);
					}
				}
			}
		}
	}

	protected V valueFromJson(FileReader reader) {  // this can be overriden because for complex values such as maps gson seems to be drunk and puts keys/values as raw string in the map
		return board.getPluginGson().fromJson(reader, board.getValueClass());
	}

	// ----- push
	@Override
	public void remotePushElements(Set<K> keys) throws Throwable {
		for (K key : keys) {
			V value = board.getCachedValue(key);
			File file = getFile(key);
			if (value == null) {
				FileUtils.delete(file);
			} else {
				FileUtils.reset(file);
				try (FileWriter writer = new FileWriter(file)) {
					valueToJson(value, writer);
				}
			}
			onPushedElement(key, value);
		}
	}

	protected void valueToJson(V value, FileWriter writer) {  // this can be overriden because for complex values such as maps gson seems to be drunk and puts keys/values as raw string in the map
		board.getPluginGson().toJson(value, board.getValueClass(), writer);
	}

	protected void onPushedElement(K key, @Nullable V value) {
	}

	// ----- delete
	@Override
	public void remoteDeleteElements(Set<K> references) throws Throwable {
		// at this point, values have been removed from valuesCache
		// this method will delete the file if no value, or update with remaining values
		remotePushElements(references);
	}

}
