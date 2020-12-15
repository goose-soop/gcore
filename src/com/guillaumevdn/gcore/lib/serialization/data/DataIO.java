package com.guillaumevdn.gcore.lib.serialization.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.LowerCaseArrayList;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashSet;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode;
import com.guillaumevdn.gcore.lib.function.ThrowableBiFunction;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.function.ThrowableFunction;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterItemStack;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public final class DataIO {

	private LinkedHashMap<String, Object> objects = new LinkedHashMap<>(); // not a lowercase one because we're encoding stuff in camelcase

	// get
	public Set<String> getKeys() {
		return objects.keySet();
	}

	public boolean isEmpty() {
		return objects.isEmpty();
	}

	// write
	public void write(String key, Object value) {
		if (value == null) {
			return;
		}
		if (value instanceof DataIO) {
			writeObject(key, (DataIO) value);
		} else if (value instanceof List) {
			writeDirectList(key, (List) value);
		} else if (value instanceof Pair) {
			Pair pair = (Pair) value;
			doWrite(key, pair.getA() + "," + pair.getB());
		} else if (value instanceof int[]) {
			doWrite(key, "__intarray__" + Serializer.PRIMITIVE_INT_ARRAY.serialize((int[]) value));
		} else if (value instanceof byte[]) {
			doWrite(key, "__bytearray__" + Serializer.PRIMITIVE_BYTE_ARRAY.serialize((byte[]) value));
		} else {
			doWrite(key, Serializer.find(value).serialize(value));
		}
	}

	public void write(String key, ItemStack value) throws Throwable {
		if (value != null) {
			writeObjectOrThrow(key, data -> {
				AdapterItemStack.INSTANCE.write(value, data);
			});
		}
	}

	private void doWrite(String key, Object value) {
		if (value != null) {
			objects.put(key != null ? key : StringUtils.generateRandomAlphanumericString(10), value);
		}
	}

	public void writeObject(String key, Consumer<DataIO> writer) {
		DataIO data = new DataIO();
		writer.accept(data);
		writeObject(key, data);
	}

	public void writeObject(String key, DataIO data) {
		if (!data.objects.isEmpty()) {
			objects.put(key, data);
		}
	}

	public void writeObjectOrThrow(String key, ThrowableConsumer<DataIO> writer) throws Throwable {
		DataIO data = new DataIO();
		writer.accept(data);
		if (!data.objects.isEmpty()) {
			objects.put(key, data);
		}
	}

	public void writeDirectList(String key, List list) {
		if (list != null && !list.isEmpty()) {
			objects.put(key, list);
		}
	}

	public <T> void writeSerializedList(String key, Collection<T> list) {
		if (list != null && !list.isEmpty()) {
			Serializer<T> serializer = Serializer.find(list.iterator().next());
			objects.put(key, list.stream().map(elem -> serializer.serialize(elem)).collect(Collectors.toList()));
		}
	}

	public SectionNode toYML(SectionNode parent, String id, boolean snakeCaseToCamelCase) {
		SectionNode node = new SectionNode(parent, id, null);
		objects.forEach((key, object) -> objectToYML(key, object, node, snakeCaseToCamelCase));
		return node;
	}

	private void objectToYML(String id, Object object, SectionNode node, boolean camelCaseToSnakeCase) {
		if (object instanceof DataIO) {
			sectionToYML(id, (DataIO) object, node, !id.equalsIgnoreCase("nbt") && camelCaseToSnakeCase);
		} else if (object instanceof List) {
			List list = (List) object;
			if (!list.isEmpty()) {
				if (list.stream().anyMatch(elem -> elem instanceof DataIO)) {
					SectionNode sub = new SectionNode(node, id, null);
					node.setConfigNode(sub);
					int i = 0;
					for (Object obj : list) {
						DataIO data = (DataIO) obj;
						sectionToYML("__list__" + ++i, data, sub, camelCaseToSnakeCase);
					}
				} else {
					if (list.size() == 1) {
						node.setSingleValue(id, Serializer.find(list.get(0)).serialize(list.get(0)), null);
					} else {
						node.setListValue(id, Serializer.find(list.get(0)).serialize(list), false, false, null);
					}
				}
			}
		} else {
			node.setSingleValue(maybeToSnakeCase(id, camelCaseToSnakeCase), String.valueOf(object), null);
		}
	}

	private void sectionToYML(String id, DataIO data, SectionNode node, boolean camelCaseToSnakeCase) {
		SectionNode sub = new SectionNode(node, maybeToSnakeCase(id, camelCaseToSnakeCase), null);
		node.setConfigNode(sub);
		data.objects.forEach((key, obj) -> objectToYML(key, obj, sub, camelCaseToSnakeCase));
	}

	private static String maybeToSnakeCase(String key, boolean camelCaseToSnakeCase) {
		return camelCaseToSnakeCase ? StringUtils.camelCaseToSnakeCase(key) : key;
	}

	// read
	public DataType getType(String key) {
		Object object = read(key);
		if (ObjectUtils.instanceOf(object, Collection.class)) {
			return DataType.LIST;
		} else if (ObjectUtils.instanceOf(object, DataIO.class)) {
			return DataType.OBJECT;
		}
		return DataType.VALUE;
	}

	public Object read(String key) {
		return objects.get(key);
	}

	public Object readUnknown(String key) {
		Object obj = read(key);
		if (obj == null) {
			return null;
		}
		if (obj instanceof String) {
			Byte nbb = NumberUtils.byteOrNull(obj);
			if (nbb != null) return nbb;
			Integer nbi = NumberUtils.integerOrNull(obj);
			if (nbi != null) return nbi;
			Double nbl = NumberUtils.doubleOrNull(obj);
			if (nbl != null) return nbl;
			String str = (String) obj;
			if (str.startsWith("__intarray__")) {
				return Serializer.PRIMITIVE_INT_ARRAY.deserialize(str.substring("__intarray__".length()));
			}
			if (str.startsWith("__bytearray__")) {
				return Serializer.PRIMITIVE_BYTE_ARRAY.deserialize(str.substring("__bytearray__".length()));
			}
		}
		return obj;
	}

	public <T> T readSerialized(String key, Class<T> typeClass) {
		Object value = read(key);
		if (value != null) {
			T t = ObjectUtils.castOrNull(value, typeClass);
			if (t == null) {
				try {
					t = Serializer.find(typeClass).deserialize(value.toString());
				} catch (Throwable exception) {
					throw new IllegalStateException("expected " + typeClass + " but found " + value.getClass() + " for " + key + ", and couldn't deserialize it", exception);
				}
				if (t == null) {
					throw new NullPointerException("deserializing '" + value.toString() + "' created a null value");
				}
			}
			return t;
		}
		return null;
	}

	public UUID readUUID(String key) {
		return readSerialized(key, UUID.class);
	}

	public Long readLong(String key) {
		return readSerialized(key, Long.class);
	}

	public Integer readInteger(String key) {
		return readSerialized(key, Integer.class);
	}

	public Double readDouble(String key) {
		return readSerialized(key, Double.class);
	}

	public Boolean readBoolean(String key) {
		return readSerialized(key, Boolean.class);
	}

	public String readString(String key) {
		Object obj = read(key);
		return obj != null ? obj.toString() : null;
	}

	public <E extends Enum<E>> E readEnum(String key, Class<E> enumClass) {
		String raw = readString(key);
		return raw != null ? ObjectUtils.safeValueOf(raw, enumClass) : null;
	}

	public Enchantment readEnchantment(String key) {
		String raw = readString(key);
		return raw != null ? ObjectUtils.enchantmentOrNull(raw) : null;
	}

	public <A, B> Pair<A, B> readPair(String key, Function<String, A> a, Function<String, B> b) {
		String string = readString(key);
		if (string == null) {
			return null;
		}
		String[] split = string.split(",");
		return Pair.of(a.apply(split[0]), b.apply(split[1]));
	}

	public ItemStack readItem(String key) throws Throwable {
		return readObjectOrThrow(key, data -> AdapterItemStack.INSTANCE.read(data));
	}

	public DataIO readObject(String key) {
		DataIO data = readSerialized(key, DataIO.class);
		return data != null ? data : null;
	}

	public <T> T readObject(String key, Function<DataIO, T> deserializer) {
		DataIO data = readSerialized(key, DataIO.class);
		return data != null ? deserializer.apply(data) : null;
	}

	public <T> T readObjectOrThrow(String key, ThrowableFunction<DataIO, T> deserializer) throws Throwable {
		DataIO data = readSerialized(key, DataIO.class);
		return data != null ? deserializer.apply(data) : null;
	}

	public <K, V> Map<K, V> readSameMap(String key, Class<K> keyClass, BiFunction<String, DataIO, V> valueDeserializer) {
		return readObject(key, data -> {
			Map<K, V> map = new HashMap<>();
			Serializer<K> keySerializer = Serializer.find(keyClass);
			data.getKeys().forEach(rawK -> {
				K k = keySerializer.deserialize(rawK);
				V value = valueDeserializer.apply(rawK, data);
				if (k != null && value != null) {
					map.put(k, value);
				}
			});
			return map;
		});
	}

	public <K, V> Map<K, V> readSubMap(String key, Class<K> keyClass, BiFunction<String, DataIO, V> valueDeserializer) {
		return readObject(key, data -> {
			Map<K, V> map = new HashMap<>();
			Serializer<K> keySerializer = Serializer.find(keyClass);
			data.getKeys().forEach(rawK -> {
				DataIO kd = data.readObject(rawK);
				if (kd != null) {
					K k = keySerializer.deserialize(rawK);
					V value = valueDeserializer.apply(rawK, kd);
					if (k != null && value != null) {
						map.put(k, value);
					}
				}
			});
			return map;
		});
	}

	public <K, V> Map<K, V> readSameMapOrThrow(String key, Class<K> keyClass, ThrowableBiFunction<String, DataIO, V> valueDeserializer) throws Throwable {
		return readObjectOrThrow(key, data -> {
			Map<K, V> map = new HashMap<>();
			Serializer<K> keySerializer = Serializer.find(keyClass);
			for (String rawK : data.getKeys()) {
				K k = keySerializer.deserialize(rawK);
				V value = valueDeserializer.apply(rawK, data);
				if (k != null && value != null) {
					map.put(k, value);
				}
			}
			return map;
		});
	}

	public <K, V> Map<K, V> readSubMapOrThrow(String key, Class<K> keyClass, ThrowableBiFunction<String, DataIO, V> valueDeserializer) throws Throwable {
		return readObjectOrThrow(key, data -> {
			Map<K, V> map = new HashMap<>();
			Serializer<K> keySerializer = Serializer.find(keyClass);
			for (String rawK : data.getKeys()) {
				DataIO kd = data.readObject(rawK);
				if (kd != null) {
					K k = keySerializer.deserialize(rawK);
					V value = valueDeserializer.apply(rawK, kd);
					if (k != null && value != null) {
						map.put(k, value);
					}
				}
			}
			return map;
		});
	}

	public <V> LowerCaseHashMap<V> readSameLowercaseMap(String key, BiFunction<String, DataIO, V> valueDeserializer) {
		return readObject(key, data -> {
			LowerCaseHashMap<V> map = new LowerCaseHashMap<>();
			data.getKeys().forEach(k -> {
				V value = valueDeserializer.apply(k, data);
				if (value != null) {
					map.put(k, value);
				}
			});
			return map;
		});
	}

	public <V> LowerCaseHashMap<V> readSubLowercaseMap(String key, BiFunction<String, DataIO, V> valueDeserializer) {
		return readObject(key, data -> {
			LowerCaseHashMap<V> map = new LowerCaseHashMap<>();
			data.getKeys().forEach(k -> {
				DataIO kd = data.readObject(k);
				V value = kd == null ? null : valueDeserializer.apply(k, kd);
				if (value != null) {
					map.put(k, value);
				}
			});
			return map;
		});
	}

	public <V> LowerCaseHashMap<V> readSubLowercaseMapOrThrow(String key, ThrowableBiFunction<String, DataIO, V> valueDeserializer) throws Throwable {
		return readObjectOrThrow(key, data -> {
			LowerCaseHashMap<V> map = new LowerCaseHashMap<>();
			for (String k : data.getKeys()) {
				DataIO kd = data.readObject(k);
				V value = kd == null ? null : valueDeserializer.apply(k, kd);
				if (value != null) {
					map.put(k, value);
				}
			}
			return map;
		});
	}

	public List readDirectList(String key) {
		List<Object> list = (List<Object>) objects.get(key);
		return list != null ? list : null;
	}

	public <T> List<T> readSerializedList(String key, Class<T> clazz) {
		return readSerializedList(key, Serializer.find(clazz)::deserialize);
	}

	public <T> List<T> readSerializedList(String key, Function<String, T> deserialized) {
		List<String> list = (List<String>) objects.get(key);
		if (list != null) {
			return list.stream().map(raw -> deserialized.apply(raw)).filter(elem -> elem != null).collect(Collectors.toList());
		}
		return null;
	}

	public <T> Set<T> readSerializedSet(String key, Class<T> clazz) {
		return readSerializedSet(key, Serializer.find(clazz)::deserialize);
	}

	public <T> Set<T> readSerializedSet(String key, Function<String, T> deserialized) {
		List<String> list = (List<String>) objects.get(key);
		if (list != null) {
			return list.stream().map(raw -> deserialized.apply(raw)).filter(elem -> elem != null).collect(Collectors.toSet());
		}
		return null;
	}

	public LowerCaseArrayList readLowerCaseList(String key) {
		List<String> list = (List<String>) objects.get(key);
		if (list != null) {
			return CollectionUtils.asLowercaseList(list);
		}
		return null;
	}

	public LowerCaseHashSet readLowerCaseSet(String key) {
		List<String> list = (List<String>) objects.get(key);
		if (list != null) {
			return CollectionUtils.asLowercaseSet(list);
		}
		return null;
	}

}
