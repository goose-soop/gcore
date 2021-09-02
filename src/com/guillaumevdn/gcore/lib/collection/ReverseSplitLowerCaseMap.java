package com.guillaumevdn.gcore.lib.collection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * This (very specific) map implementation split the keys (reversed) with the said separator to separate values into submaps
 * For instance, if the character is <code>_</code>, here's how this object could be structured :
 * <pre>
 * {@code
 * BLOCK:
 *   COAL: value for final key "COAL_BLOCK"
 *   CORAL:
 *     BLUE: value for final key "BLUE_CORAL_BLOCK"
 * SWORD:
 *   IRON: value for final key "IRON_SWORD"
 *   DIAMOND: value for final key "DIAMOND_SWORD"
 * }
 * </pre>
 * Additionally, an extra map is created for values that don't contain the separator character.
 * @author GuillaumeVDN
 */
public class ReverseSplitLowerCaseMap<V> {

	private final char separator;
	private final Map<Integer, Object> base = new HashMap<>(1, 1f);  // we directly use the part hashcode as a key
	private final Map<Integer, V> standalone = new HashMap<>(1, 1f);  // for keys with no separator

	public ReverseSplitLowerCaseMap(char separator) {
		this.separator = separator;
	}

	// ----- internal
	private Pair<Map<Integer, V>, Integer> findParent(String elementKey) {
		elementKey = elementKey.toLowerCase();
		int count = StringUtils.countChar(elementKey, separator);

		// standalone
		if (count == 0) {
			return Pair.of(standalone, elementKey.hashCode());
		}

		// find closest parent
		int end = elementKey.length();
		Map<Integer, Object> parent = base;
		while (--count >= 0) {
			int begin = elementKey.lastIndexOf(separator, end - 1);
			int keyPart = elementKey.substring(begin + 1, end).hashCode();

			// parent don't exist yet, create it
			Object existingForKey = parent.get(keyPart);
			if (existingForKey == null) {
				parent.put(keyPart, existingForKey = new HashMap<>(1));
				parent = (Map<Integer, Object>) existingForKey;
			}
			// parent already exists
			else if (existingForKey instanceof Map) {
				parent = (Map<Integer, Object>) existingForKey;
			}
			// key exists but it's not a parent ; move it
			else {
				Map<Integer, Object> sub = new HashMap<>(1);
				sub.put(0, existingForKey);
				parent.put(keyPart, sub);
				parent = sub;
			}

			// next
			end = begin;
		}

		// maybe it's element 0
		int remaining = elementKey.substring(0, end).hashCode();
		Object value = parent.get(remaining);
		if (value != null && value instanceof Map) {
			parent = (Map<Integer, Object>) value;
			remaining = 0;
		}
		return Pair.of((Map<Integer, V>) parent, remaining);
	}

	public void print(File dataFile) {
		try (FileWriter writer = new FileWriter(dataFile)) {
			writer.append("----- STANDALONE\n\n");
			printRec(standalone, writer, 1);
			writer.append("\n\n----- MAP\n\n");
			printRec(base, writer, 1);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private void printRec(Map<Integer, ?> map, FileWriter writer, int depth) throws IOException {
		String spaces = StringUtils.repeatString("\t", depth);
		for (int key : map.keySet()) {
			Object obj = map.get(key);
			if (obj instanceof Map) {
				writer.append(spaces + key + ":\n");
				printRec((Map<Integer, ?>) obj, writer, depth + 1);
			} else {
				writer.append(spaces + key + ": " + obj + "\n");
			}
		}
	}

	// ----- methods
	public boolean isEmpty() {
		return size() == 0; // calculate size because there might be empty submaps
	}

	public int size() {
		return standalone.size() + recSize(base);
	}

	private int recSize(Map<Integer, Object> map) {
		int size = 0;
		for (Object value : map.values()) {
			if (value instanceof Map) {
				size += recSize((Map<Integer, Object>) value);
			} else {
				++size;
			}
		}
		return size;
	}

	public boolean containsKey(String key) {
		return findParent(key).process((map, part) -> map.containsKey(part));
	}

	// ----- get
	public V get(String key) {
		return findParent(key).process((map, part) -> map.get(part));
	}

	public V getOrDefault(String key, V def) {
		return findParent(key).process((map, part) -> map.getOrDefault(part, def));
	}

	public Collection<V> values() {
		List<V> values = new ArrayList<>();
		values.addAll(standalone.values());
		addValuesRec(base, values);
		return values;
	}

	private void addValuesRec(Map<Integer, Object> map, List<V> values) {
		for (Object value : map.values()) {
			if (value instanceof Map) {
				addValuesRec((Map<Integer, Object>) value, values);
			} else {
				values.add((V) value);
			}
		}
	}

	// ----- set
	public void put(String key, V value) {
		findParent(key).consume((map, part) -> map.put(part, value));
	}

	public void putAll(Map<String, ? extends V> putAll) {
		putAll.forEach((key, value) -> put(key, value));
	}

	public V compute(String key, Function<V, V> mappingFunction) {
		return findParent(key).process((map, part) -> {
			return map.compute(part, (__, value) -> mappingFunction.apply(value));
		});
	}

	public V computeIfAbsent(String key, Supplier<V> ifAbsent) {
		return findParent(key).process((map, part) -> {
			return map.computeIfAbsent(part, __ -> ifAbsent.get());
		});
	}

	public V remove(String key) {
		return findParent(key).process((map, part) -> map.remove(part));
	}

	public void clear() {
		base.clear();
		standalone.clear();
	}

	// ----- object
	private static final int prime = 31;

	@Override
	public int hashCode() {
		int hash = 1;
		hash = prime * hash + standalone.hashCode();
		hash = prime * hash + recHashCode(base);
		return hash;
	}

	private int recHashCode(Map<Integer, Object> map) {
		int hash = 1;
		for (Object value : map.values()) {
			if (value instanceof Map) {
				hash = prime * hash + recHashCode((Map<Integer, Object>) value);
			} else {
				hash = prime * hash + value.hashCode();
			}
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == null ? false : obj.hashCode() == hashCode();
	}

}
