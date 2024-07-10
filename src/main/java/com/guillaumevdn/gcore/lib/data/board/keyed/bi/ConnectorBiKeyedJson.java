package com.guillaumevdn.gcore.lib.data.board.keyed.bi;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.tuple.Pair;

public abstract class ConnectorBiKeyedJson<K, K2, V> extends ConnectorBiKeyed<K, K2, V> {

	public ConnectorBiKeyedJson(BiKeyedBoard<K, K2, V> board) {
		super(board);
	}

	// ----- wrapper and file
	public abstract File getRoot();

	public abstract File getFile(K key);

	public abstract K getKey(File file);

	@Override
	public void remoteInit() throws Throwable {
		getRoot().mkdirs();
	}

	@Override
	public void remotePullAll() throws Throwable {
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
			remotePullElementsByPrimary(keys);
		}
	}

	@Override
	public void remotePullElementsByPrimary(Set<K> keys) throws Throwable {
		if (keys.isEmpty())
			return;
		for (K key : keys) {
			File file = getFile(key);
			if (file.exists()) {
				try (FileReader reader = new FileReader(file)) {
					Map<K2, V> values = secondaryAndValuesFromJson(reader);
					if (values != null) {
						((BiKeyedBoard) board).removeElementsFromCacheByPrimary(keys);
						values.forEach((key2, v) -> board.putInCache(Pair.of(key, key2), v));
					}
				}
			}
		}
	}

	@Override
	public void remotePullElements(Set<Pair<K, K2>> keys) throws Throwable {
		if (keys.isEmpty())
			return;

		Map<K, Set<K2>> map = new HashMap<>();
		keys.forEach(ref -> map.computeIfAbsent(ref.getA(), __ -> new HashSet<>()).add(ref.getB()));

		for (Entry<K, Set<K2>> ref : map.entrySet()) {
			File file = getFile(ref.getKey());
			if (file.exists()) {
				try (FileReader reader = new FileReader(file)) {
					Map<K2, V> values = secondaryAndValuesFromJson(reader);
					if (values != null) {
						ref.getValue().forEach(key2 -> {
							V v = values.get(key2);
							if (v != null) {
								board.putInCache(Pair.of(ref.getKey(), key2), v);
							}
						});
					}
				}
			}
		}
	}

	protected abstract Map<K2, V> secondaryAndValuesFromJson(FileReader reader);

	@Override
	public void remotePushElements(Set<Pair<K, K2>> refs) throws Throwable {
		if (refs.isEmpty())
			return;

		Set<K> keys = refs.stream().map(e -> e.getA()).distinct().collect(Collectors.toSet()); // ignore K2, we push the whole file anyways
		Map<K2, V> values = board
				.streamResult(str -> str.filter(e -> keys.contains(e.getKey().getA())).collect(Collectors.toMap(e -> e.getKey().getB(), e -> e.getValue())));

		for (K key : keys) {
			File file = getFile(key);
			if (values.isEmpty()) {
				FileUtils.delete(file);
			} else {
				FileUtils.reset(file);
				try (FileWriter writer = new FileWriter(file)) {
					secondaryAndValuesToJson(values, writer);
				}
			}
		}
	}

	protected abstract void secondaryAndValuesToJson(Map<K2, V> values, FileWriter writer);

	@Override
	public void remoteDeleteElements(Set<Pair<K, K2>> references) throws Throwable {
		if (references.isEmpty())
			return;

		// at this point, values have been removed from valuesCache
		// this method will delete the file if no value, or update with remaining values
		remotePushElements(references);
	}

}
