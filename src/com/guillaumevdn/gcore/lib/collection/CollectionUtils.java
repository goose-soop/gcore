package com.guillaumevdn.gcore.lib.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.guillaumevdn.gcore.lib.object.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.function.MapSupplier;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.function.ThrowableTriConsumer;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.wrapper.WrapperBoolean;

/**
 * @author GuillaumeVDN
 */
public final class CollectionUtils {

	// create collection
	public static <T> List<T> asList(Consumer<List<T>> filler) {
		List<T> list = new ArrayList<>();
		filler.accept(list);
		return list;
	}

	@SafeVarargs
	public static <T> List<T> asList(T... elements) {
		return Stream.of(elements).collect(Collectors.toList());
	}

	public static <T> List<T> asList(Iterable<T> iterable) {
		List<T> result = new ArrayList<>();
		for (T t : iterable) {
			result.add(t);
		}
		return result;
	}

	public static List<Integer> asList(int[] elements) {
		List<Integer> list = new ArrayList<>();
		for (int elem : elements) {
			list.add(elem);
		}
		return list;
	}

	public static <T> List<T> createList(Class<T> type, Collection<?> content) {
		List<T> list = new ArrayList<>();
		for (Object elem : content) {
			list.add((T) elem);
		}
		return list;
	}

	public static List<String> asLowercaseList(String... elements) {
		LowerCaseArrayList list = new LowerCaseArrayList();
		Stream.of(elements).forEach(element -> list.add(element));
		return list;
	}

	public static LowerCaseArrayList asLowercaseList(Collection<String> elements) {
		LowerCaseArrayList list = new LowerCaseArrayList();
		elements.forEach(element -> list.add(element));
		return list;
	}

	public static <T> List<T> asList(Collection<T> collection) {
		return collection.stream().collect(Collectors.toList());
	}

	public static <T> List<T> asListMultiple(Class<T> typeClass, Object... objects) {
		List<T> list = new ArrayList<>();
		addMultiple(objects, typeClass, list);
		return list;
	}

	private static <T> void addMultiple(Object object, Class<T> typeClass, Collection<T> collection) {
		if (object instanceof Collection<?>) {
			for (Object obj : (Collection<?>) object) {
				addMultiple(obj, typeClass, collection);
			}
		} else if (object instanceof Object[]) {
			for (Object obj : (Object[]) object) {
				addMultiple(obj, typeClass, collection);
			}
		} else {
			if (object == null) {
				collection.add(null);
			} else {
				T casted = ObjectUtils.castOrNull(object, typeClass);
				if (casted == null) throw new ClassCastException("couldn't cast to " + typeClass + " object " + object);
				collection.add(casted);
			}
		}
	}

	public static <T> List<T> asUnmodifiableList(Consumer<List<T>> filler) {
		return Collections.unmodifiableList(asList(filler));
	}

	@SafeVarargs
	public static <T> List<T> asUnmodifiableList(T... elements) {
		return Collections.unmodifiableList(asList(elements));
	}

	public static List<String> asUnmodifiableLowercaseList(String... elements) {
		return Collections.unmodifiableList(asLowercaseList(elements));
	}

	public static List<String> asUnmodifiableLowercaseList(Collection<String> elements) {
		return Collections.unmodifiableList(asLowercaseList(elements));
	}

	public static <T> List<T> asUnmodifiableList(Collection<T> collection) {
		return Collections.unmodifiableList(asList(collection));
	}

	public static <T> List<T> revertList(List<T> list) {
		Collections.reverse(list);
		return list;
	}

	public static <T> List<T> asRevertSet(Set<T> set) {
		return revertList(asList(set));
	}

	public static <T extends Comparable<T>> List<T> asSortedList(Collection<T> collection, Comparator<T> comparator) {
		List<T> list = new ArrayList<>();
		if (collection != null) {
			list.addAll(collection);
			if (comparator == null) {
				Collections.sort(list);
			} else {
				list.sort(comparator);
			}
		}
		return list;
	}

	public static <T> Set<T> asSet(Consumer<Set<T>> filler) {
		Set<T> list = new HashSet<>();
		filler.accept(list);
		return list;
	}

	@SafeVarargs
	public static <T> Set<T> asSet(T... objects) {
		return Stream.of(objects).collect(Collectors.toSet());
	}

	public static <T> Set<T> asSet(Collection<T> collection) {
		return collection.stream().collect(Collectors.toSet());
	}

	public static <T> Set<T> asSortedSet(Collection<? extends T> collection, Comparator<T> comparator) {
		TreeSet<T> set = new TreeSet<T>(comparator);
		if (collection != null) {
			set.addAll(collection);
		}
		return set;
	}

	public static <T> Set<T> asSetMultiple(Class<T> typeClass, Object... objects) {
		Set<T> list = new HashSet<>();
		addMultiple(objects, typeClass, list);
		return list;
	}

	public static <K, V> Map<K, V> asMap(Object... objects) {
		Map<K, V> map = new HashMap<>();
		for (int i = 0; i < objects.length; ++i) {
			if (i + 1 >= objects.length) break;
			map.put((K) objects[i], (V) objects[++i]);
		}
		return map;
	}

	public static <K, V> Map<K, V> asMap(Map<K, V> map) {
		return new HashMap<>(map);
	}

	public static <K, V> Map<K, V> asMap(MapSupplier<K, V> supplier) {
		return supplier.get();
	}

	public static <K, V> Map<K, V> asMapUniqueValue(Collection<K> keys, V value) {
		Map<K, V> map = new HashMap<>();
		keys.forEach(key -> map.put(key, value));
		return map;
	}

	public static <V> LowerCaseHashMap<V> asLowerCaseMap(Object... objects) {
		LowerCaseHashMap<V> map = new LowerCaseHashMap<>();
		for (int i = 0; i < objects.length; ++i) {
			if (i + 1 >= objects.length) break;
			map.put((String) objects[i], (V) objects[++i]);
		}
		return map;
	}

	public static <K, V> LinkedHashMap<K, V> asLinkedMap(Object... objects) {
		LinkedHashMap<K, V> map = new LinkedHashMap<>();
		for (int i = 0; i < objects.length; ++i) {
			if (i + 1 >= objects.length) break;
			map.put((K) objects[i], (V) objects[++i]);
		}
		return map;
	}

	public static <K, V> Map<K, V> asUnmodifiableMap(Object... objects) {
		return Collections.unmodifiableMap(asMap(objects));
	}

	public static <V> Map<String, V> asUnmodifiableLowerCaseMap(Object... objects) {
		return Collections.unmodifiableMap(asLowerCaseMap(objects));
	}

	// random
	public static <T> T random(List<? extends T> list) {
		return randomOptional(list).orNull();
	}

	public static <T> T random(Collection<? extends T> set) {
		if (!set.isEmpty()) {
			int index = NumberUtils.random(0, set.size() - 1);
			int i = -1;
			for (T elem : set) {
				if (++i == index) {
					return elem;
				}
			}
		}
		return null;
	}

	public static <T> Optional<T> randomOptional(List<? extends T> list) {
		return list.isEmpty() ? Optional.empty() : Optional.of(list.get(NumberUtils.random(0, list.size() - 1)));
	}

	// iterate
	public static <T> void iterate(Collection<T> collection, TriConsumer<Iterator<T>, T, WrapperBoolean> consumer) {
		WrapperBoolean breaker = WrapperBoolean.of(false);
		for (Iterator<T> iterator = collection.iterator(); iterator.hasNext(); ) {
			consumer.accept(iterator, iterator.next(), breaker);
			if (breaker.get()) {
				return;
			}
		}
	}

	public static <T> void iterateCatching(Collection<T> collection, ThrowableTriConsumer<Iterator<T>, T, WrapperBoolean> consumer) {
		WrapperBoolean breaker = WrapperBoolean.of(false);
		for (Iterator<T> iterator = collection.iterator(); iterator.hasNext(); ) {
			try {
				consumer.accept(iterator, iterator.next(), breaker);
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
			if (breaker.get()) {
				return;
			}
		}
	}

	public static <T> void iterateNonNull(Collection<T> collection, TriConsumer<Iterator<T>, T, WrapperBoolean> consumer) {
		if (collection != null) {
			WrapperBoolean breaker = WrapperBoolean.of(false);
			for (Iterator<T> iterator = collection.iterator(); iterator.hasNext(); ) {
				T next = iterator.next();
				if (next != null) {
					consumer.accept(iterator, next, breaker);
					if (breaker.get()) {
						return;
					}
				}
			}
		}
	}

	public static <T> void iterateNonNullValues(Map<?, T> collection, TriConsumer<Iterator<T>, T, WrapperBoolean> consumer) {
		if (collection != null) {
			WrapperBoolean breaker = WrapperBoolean.of(false);
			for (Iterator<T> iterator = collection.values().iterator(); iterator.hasNext(); ) {
				T next = iterator.next();
				if (next != null) {
					consumer.accept(iterator, next, breaker);
					if (breaker.get()) {
						return;
					}
				}
			}
		}
	}

	// equals/contains
	public static <K, V> boolean contentEquals(Map<? extends K, ? extends V> m1, Map<? extends K, ? extends V> m2) {
		if (m1.size() != m2.size()) {
			return false;
		}
		Set<? extends K> k1 = m1.keySet();
		Set<? extends K> k2 = m2.keySet();
		if (!contentEquals(k1, k2, false)) {
			return false;
		}
		for (K k : k1) {
			if (!Objects.deepEquals(m1.get(k), m2.get(k))) {
				return false;
			}
		}
		return true;
	}

	public static <T> boolean contentEquals(Collection<? extends T> c1, Collection<? extends T> c2) {
		return contentEquals(c1, c2, true);
	}

	public static <T> boolean contentEquals(Collection<? extends T> c1, Collection<? extends T> c2, boolean sameOrder) {
		if (c1.size() != c2.size()) {
			return false;
		}
		if (sameOrder) {
			Iterator<? extends T> i1 = c1.iterator();
			Iterator<? extends T> i2 = c2.iterator();
			while (i1.hasNext()) {
				if (!i2.hasNext()) {
					return false;
				}
				if (!Objects.deepEquals(i1.next(), i2.next())) {
					return false;
				}
			}
		} else {
			List<? extends T> l1 = asList(c1);
			main: for (T t2 : c2) {
				for (T t1 : l1) {
					if (Objects.deepEquals(t1, t2)) {
						continue main;
					}
				}
				return false;
			}
		}
		return true;
	}

	public static boolean containsIgnoreCase(Collection<String> coll, String string) {
		if (string == null) return coll.contains(null);
		for (String str : coll) {
			if (str != null && str.equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}

	public static <T> boolean containsOne(Collection<T> coll, Collection<T> mustHaveOneFromThis) {
		for (T t : mustHaveOneFromThis) {
			if (coll.contains(t)) {
				return true;
			}
		}
		return false;
	}

	public static <T> boolean hasOneMatching(Collection<T> coll, Function<T, Boolean> matcher) {
		for (T t : coll) {
			if (matcher.apply(t)) {
				return true;
			}
		}
		return false;
	}

	public static <T> List<T> findNew(Collection<T> previous, Collection<T> next) {
		List<T> diff = new ArrayList<>();
		for (T elem : next) {
			if (!previous.contains(elem)) {
				diff.add(elem);
			}
		}
		return diff;
	}

	// clear
	public static <T> void clearForEach(Collection<? extends T> collection, Consumer<T> consumer) {
		collection.forEach(consumer);
		collection.clear();
	}

	public static <T> void clearForEachThrowable(Collection<? extends T> collection, ThrowableConsumer<T> consumer) throws Throwable {
		for (T t : collection) {
			consumer.accept(t);
		}
		collection.clear();
	}

	public static <T> void clearForEachThrowableIgnore(Collection<? extends T> collection, ThrowableConsumer<T> consumer) {
		for (T t : collection) {
			try {
				consumer.accept(t);
			} catch (Throwable ignored) {}
		}
		collection.clear();
	}

	public static void clearForEachOnline(Collection<UUID> collection, Consumer<Player> consumer) {
		collection.forEach(uuid -> {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				consumer.accept(player);
			}
		});
		collection.clear();
	}

	public static <K, V> void clearForEach(Map<K, V> map, BiConsumer<K, V> consumer) {
		map.forEach((key, value) -> consumer.accept(key, value));
		map.clear();
	}

	public static <V> void clearForEachOnline(Map<UUID, V> map, BiConsumer<Player, V> consumer) {
		map.forEach((uuid, value) -> {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				consumer.accept(player, value);
			}
		});
		map.clear();
	}

	// misc
	public static <T> void addIfNotNull(Collection<T> collection, T element) {
		if (element != null) {
			collection.add(element);
		}
	}

	public static <T> void addAllIfNonNull(Collection<T> main, Collection<T> sub) {
		addAllIfNonNull(main, sub, null);
	}

	public static <T> void addAllIfNonNull(Collection<T> main, Collection<T> sub, UnaryOperator<T> elementOperator) {
		if (sub != null) {
			if (elementOperator != null) {
				sub.forEach(element -> {
					if (element != null) {
						main.add(elementOperator.apply(element));
					}
				});
			} else {
				sub.forEach(element -> addIfNotNull(main, element));
			}
		}
	}

	public static <T> List<List<T>> split(Collection<? extends T> collection, int splitSize) {
		if (splitSize < 1) {
			throw new Error("split size must be at least 1");
		}
		List<T> current = new ArrayList<>();
		List<List<T>> split = new ArrayList<>();
		split.add(current);
		Iterator<? extends T> iterator = collection.iterator();
		while (iterator.hasNext()) {
			current.add(iterator.next());
			if (iterator.hasNext() && current.size() >= splitSize) {
				split.add(current);
				current = new ArrayList<>();
			}
		}
		return split;
	}

	public static <T> List<Set<T>> split(Set<? extends T> collection, int splitSize) {
		if (splitSize < 1) {
			throw new Error("split size must be at least 1");
		}
		Set<T> current = new HashSet<>();
		List<Set<T>> split = new ArrayList<>();
		split.add(current);
		Iterator<? extends T> iterator = collection.iterator();
		while (iterator.hasNext()) {
			current.add(iterator.next());
			if (iterator.hasNext() && current.size() >= splitSize) {
				split.add(current);
				current = new HashSet<>();
			}
		}
		return split;
	}

}
