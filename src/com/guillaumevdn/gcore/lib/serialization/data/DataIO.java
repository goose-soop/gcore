package com.guillaumevdn.gcore.lib.serialization.data;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.concurrency.RWArrayList;
import com.guillaumevdn.gcore.lib.concurrency.RWHashSet;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode.SectionNodeType;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.function.ThrowableFunction;
import com.guillaumevdn.gcore.lib.function.ThrowableTriFunction;
import com.guillaumevdn.gcore.lib.function.TriFunction;
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

	private LinkedHashMap<String, Object> objects = new LinkedHashMap<>();  // not a lowercase one because we're encoding stuff in camelcase

	// ----- get
	public Set<String> getKeys() {
		return objects.keySet();
	}

	public boolean isEmpty() {
		return objects.isEmpty();
	}

	// ----- write
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
				data.write("version", AdapterItemStack.INSTANCE.getVersion());
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
			if (list instanceof RWArrayList) {
				RWArrayList<T> rw = (RWArrayList<T>) list;
				Serializer<T> serializer = Serializer.find(rw.get(0));
				objects.put(key, rw.streamResult(s -> s.map(elem -> serializer.serialize(elem)).collect(Collectors.toList())));
			} else if (list instanceof RWHashSet) {
				RWHashSet<T> rw = (RWHashSet<T>) list;
				Serializer<T> serializer = Serializer.find(rw.findFirst().orNull());
				objects.put(key, rw.streamResult(s -> s.map(elem -> serializer.serialize(elem)).collect(Collectors.toList())));
			} else {
				Serializer<T> serializer = Serializer.find(list.iterator().next());
				objects.put(key, list.stream().map(elem -> serializer.serialize(elem)).collect(Collectors.toList()));
			}
		}
	}

	public SectionNode toYML(SectionNode parent, String id, boolean snakeCaseToCamelCase) {
		SectionNode node = new SectionNode(parent, id, SectionNodeType.REGULAR, null);
		objects.forEach((key, object) -> objectToYML(key, object, node, snakeCaseToCamelCase));
		return node;
	}

	private static final Set<String> IGNORE_SNAKECASE = CollectionUtils.asSet("nbt", "enchantments");

	private void objectToYML(String id, Object object, SectionNode node, boolean camelCaseToSnakeCase) {
		if (object instanceof DataIO) {
			sectionToYML(id, (DataIO) object, node, !IGNORE_SNAKECASE.contains(id.toLowerCase()) && camelCaseToSnakeCase);
		} else if (object instanceof List) {
			List list = (List) object;
			if (!list.isEmpty()) {
				if (list.stream().anyMatch(elem -> elem instanceof DataIO)) {
					SectionNode sub = new SectionNode(node, id, SectionNodeType.REGULAR, null);
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
		SectionNode sub = new SectionNode(node, maybeToSnakeCase(id, camelCaseToSnakeCase), SectionNodeType.REGULAR, null);
		node.setConfigNode(sub);
		data.objects.forEach((key, obj) -> objectToYML(key, obj, sub, camelCaseToSnakeCase));
	}

	private static String maybeToSnakeCase(String key, boolean camelCaseToSnakeCase) {
		return camelCaseToSnakeCase ? StringUtils.camelCaseToSnakeCase(key) : key;
	}

	// ----- read
	public DataType getType(String key) {
		Object object = read(key);
		if (ObjectUtils.instanceOf(object, Collection.class)) {
			return DataType.LIST;
		} else if (ObjectUtils.instanceOf(object, DataIO.class)) {
			return DataType.OBJECT;
		}
		return DataType.VALUE;
	}

	@Nullable
	public Object read(String key) {
		return objects.get(key);
	}

	@Nullable
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

	@Nullable
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

	@Nullable
	public UUID readUUID(String key) {
		return readSerialized(key, UUID.class);
	}

	@Nullable
	public Long readLong(String key) {
		return readSerialized(key, Long.class);
	}

	@Nullable
	public Integer readInteger(String key) {
		return readSerialized(key, Integer.class);
	}

	@Nullable
	public Double readDouble(String key) {
		return readSerialized(key, Double.class);
	}

	@Nullable
	public Boolean readBoolean(String key) {
		return readSerialized(key, Boolean.class);
	}

	@Nullable
	public String readString(String key) {
		Object obj = read(key);
		return obj != null ? obj.toString() : null;
	}

	@Nullable
	public <E extends Enum<E>> E readEnum(String key, Class<E> enumClass) {
		String raw = readString(key);
		return raw != null ? ObjectUtils.safeValueOf(raw, enumClass) : null;
	}

	@Nullable
	public Enchantment readEnchantment(String key) {
		String raw = readString(key);
		return raw != null ? ObjectUtils.enchantmentOrNull(raw) : null;
	}

	@Nullable
	public <A, B> Pair<A, B> readPair(String key, Function<String, A> a, Function<String, B> b) {
		String string = readString(key);
		if (string == null) {
			return null;
		}
		String[] split = string.split(",");
		return Pair.of(a.apply(split[0]), b.apply(split[1]));
	}

	@Nullable
	public ItemStack readItem(String key) throws Throwable {
		return readObjectOrThrow(key, data -> AdapterItemStack.INSTANCE.read(data));
	}

	@Nullable
	public DataIO readObject(String key) {
		DataIO data = readSerialized(key, DataIO.class);
		return data != null ? data : null;
	}

	@Nullable
	public <T> T readObject(String key, Function<DataIO, T> deserializer) {
		DataIO data = readSerialized(key, DataIO.class);
		return data != null ? deserializer.apply(data) : null;
	}

	public <T> T readObjectOrElse(String key, Function<DataIO, T> deserializer, T def) {
		DataIO data = readSerialized(key, DataIO.class);
		return data != null ? deserializer.apply(data) : def;
	}

	@Nullable
	public <T> T readObjectOrThrow(String key, ThrowableFunction<DataIO, T> deserializer) throws Throwable {
		DataIO data = readSerialized(key, DataIO.class);
		return data != null ? deserializer.apply(data) : null;
	}

	public <T> T readObjectOrElseOrThrow(String key, ThrowableFunction<DataIO, T> deserializer, T def) throws Throwable {
		DataIO data = readSerialized(key, DataIO.class);
		return data != null ? deserializer.apply(data) : def;
	}

	@Nonnull
	public<K, V, M extends Map<K, V>> M readObjectMap(String key, Class<K> keyClass, M map, TriFunction<String, K, DataIO, V> valueDeserializer) {
		readNullableObjectMap(key, keyClass, map, valueDeserializer);
		return map;
	}

	@Nonnull
	public <K, V, M extends Map<K, V>> M readObjectMapOrThrow(String key, Class<K> keyClass, M map, ThrowableTriFunction<String, K, DataIO, V> valueDeserializer) throws Throwable {
		readNullableObjectMapOrThrow(key, keyClass, map, valueDeserializer);
		return map;
	}

	@Nullable
	public<K, V, M extends Map<K, V>> M readNullableObjectMap(String key, Class<K> keyClass, M map, TriFunction<String, K, DataIO, V> valueDeserializer) {
		try {
			return readNullableObjectMapOrThrow(key, keyClass, map, (subKeyRaw, subKey, subReader) -> valueDeserializer.apply(subKeyRaw, subKey, subReader));
		} catch (Throwable exception) {
			throw new RuntimeException(exception);
		}
	}

	@Nullable
	public <K, V, M extends Map<K, V>> M readNullableObjectMapOrThrow(String key, Class<K> keyClass, M map, ThrowableTriFunction<String, K, DataIO, V> valueDeserializer) throws Throwable {
		readObjectOrThrow(key, mapReader -> {
			Serializer<K> keySerializer = Serializer.find(keyClass);
			for (String elementKey : mapReader.getKeys()) {
				K elementK = keySerializer.deserialize(elementKey);
				if (elementK != null) {
					DataIO rawValue = mapReader.readObject(elementKey);
					V elementValue = valueDeserializer.apply(elementKey, elementK, rawValue);
					if (elementValue != null) {
						map.put(elementK, elementValue);
					}
				}
			}
			return map;
		});
		return map;
	}

	@Nonnull
	public <K, V, M extends Map<K, V>> M readSimpleMap(String key, Class<K> keyClass, M map, TriFunction<String, K, DataIO, V> valueDeserializer) {
		readNullableSimpleMap(key, keyClass, map, valueDeserializer);
		return map;
	}

	@Nonnull
	public <K, V, M extends Map<K, V>> M readSimpleMapOrThrow(String key, Class<K> keyClass, M map, ThrowableTriFunction<String, K, DataIO, V> valueDeserializer) throws Throwable {
		readNullableSimpleMapOrThrow(key, keyClass, map, valueDeserializer);
		return map;
	}

	@Nullable
	public<K, V, M extends Map<K, V>> M readNullableSimpleMap(String key, Class<K> keyClass, M map, TriFunction<String, K, DataIO, V> valueDeserializer) {
		try {
			return readNullableSimpleMapOrThrow(key, keyClass, map, (subKeyRaw, subKey, mapReader) -> valueDeserializer.apply(subKeyRaw, subKey, mapReader));
		} catch (Throwable exception) {
			throw new RuntimeException(exception);
		}
	}

	@Nullable
	public <K, V, M extends Map<K, V>> M readNullableSimpleMapOrThrow(String key, Class<K> keyClass, M map, ThrowableTriFunction<String, K, DataIO, V> valueDeserializer) throws Throwable {
		readObjectOrThrow(key, mapReader -> {
			Serializer<K> keySerializer = Serializer.find(keyClass);
			for (String elementKey : mapReader.getKeys()) {
				K elementK = keySerializer.deserialize(elementKey);
				if (elementK != null) {
					V elementValue = valueDeserializer.apply(elementKey, elementK, mapReader);
					if (elementValue != null) {
						map.put(elementK, elementValue);
					}
				}
			}
			return map;
		});
		return map;
	}

	@Nullable
	public List readDirectList(String key) {
		Object rawValue = objects.get(key);
		List<Object> list = null;
		if (rawValue != null) {
			if (rawValue instanceof String) {
				list = CollectionUtils.asList(rawValue);
			} else {
				list = (List<Object>) rawValue;
			}
		}
		return list;
	}

	@Nullable
	public <T> List<T> readSerializedList(String key, Class<T> clazz) {
		return readSerializedList(key, Serializer.find(clazz)::deserialize);
	}

	@Nullable
	public <T> List<T> readSerializedList(String key, Function<String, T> deserialized) {
		Object rawValue = objects.get(key);
		List<String> list = null;
		if (rawValue != null) {
			if (rawValue instanceof String) {
				list = CollectionUtils.asList((String) rawValue);
			} else {
				list = (List<String>) rawValue;
			}
		}
		if (list != null) {
			return list.stream().map(raw -> deserialized.apply(raw)).filter(elem -> elem != null).collect(Collectors.toList());
		}
		return null;
	}

	@Nonnull
	public <T, L extends Collection<T>> L readSerializedList(String key, Class<T> clazz, L resultList) {
		return readSerializedList(key, resultList, Serializer.find(clazz)::deserialize);
	}

	@Nonnull
	public <T, L extends Collection<T>> L readSerializedList(String key, L resultList, Function<String, T> deserialized) {
		Object rawValue = objects.get(key);
		List<String> list = null;
		if (rawValue != null) {
			if (rawValue instanceof String) {
				list = CollectionUtils.asList((String) rawValue);
			} else {
				list = (List<String>) rawValue;
			}
		}
		if (list != null) {
			list.stream().map(raw -> deserialized.apply(raw)).filter(elem -> elem != null).forEach(elem -> resultList.add(elem));
		}
		return resultList;
	}

}
