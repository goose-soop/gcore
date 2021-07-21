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
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.guillaumevdn.gcore.lib.concurrency.RWHashSet;
import com.guillaumevdn.gcore.lib.function.MapSupplier;
import com.guillaumevdn.gcore.lib.function.QuadriConsumer;
import com.guillaumevdn.gcore.lib.function.ThrowableTriConsumer;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.object.Optional;
import com.guillaumevdn.gcore.lib.wrapper.WrapperBoolean;

/**
 * @author GuillaumeVDN
 */
public final class CollectionUtils {

	// ----- create collection
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

	public static LowerCaseHashSet asLowercaseSet(String... elements) {
		LowerCaseHashSet list = new LowerCaseHashSet();
		for (String element : elements) list.add(element);
		return list;
	}

	public static LowerCaseHashSet asLowercaseSet(Collection<String> elements) {
		LowerCaseHashSet list = new LowerCaseHashSet();
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

	@SafeVarargs
	public static <T> RWHashSet<T> asRWSet(T... objects) {
		RWHashSet<T> list = new RWHashSet<>();
		if (objects != null) {
			for (T obj : objects) {
				list.add(obj);
			}
		}
		return list;
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

	// ----- map
	private static <K, V, M extends Map<K, V>> M doAsMap(M map, Object... objects) {
		for (int i = 0; i < objects.length; ++i) {
			if (i + 1 >= objects.length) break;
			map.put((K) objects[i], (V) objects[++i]);
		}
		return map;
	}

	public static <K, V> Map<K, V> asMap(Object... objects) {
		return doAsMap(new HashMap<K, V>(), objects);
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
		return doAsMap(new LowerCaseHashMap<V>(), objects);
	}

	public static <K, V> LinkedHashMap<K, V> asLinkedMap(Object... objects) {
		return doAsMap(new LinkedHashMap<K, V>(), objects);
	}

	public static <K, V> WeakHashMap<K, V> asWeakMap(Object... objects) {
		return doAsMap(new WeakHashMap<K, V>(), objects);
	}

	public static <K, V> Map<K, V> asUnmodifiableMap(Object... objects) {
		return Collections.unmodifiableMap(asMap(objects));
	}

	public static <V> Map<String, V> asUnmodifiableLowerCaseMap(Object... objects) {
		return Collections.unmodifiableMap(asLowerCaseMap(objects));
	}

	@Nullable
	public static <K, K2, V> Map<K2, V> remapKeys(@Nullable Map<K, V> map, Function<K, K2> mapper) {
		if (map == null) return null;
		return asMap(r -> map.forEach((k, v) -> r.put(mapper.apply(k), v)));
	}

	// ----- random
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

	// ----- iterate
	public static <K, V> void iterateMap(Map<K, V> map, QuadriConsumer<K, V, WrapperBoolean, WrapperBoolean> consumer) {
		iterate(map.entrySet().iterator(), (entry, remover, breaker) -> consumer.accept(entry.getKey(), entry.getValue(), remover, breaker));
	}

	public static <T> void iterate(Collection<T> collection, TriConsumer<T, WrapperBoolean, WrapperBoolean> consumer) {
		iterate(collection.iterator(), consumer);
	}

	public static <T> void iterate(Iterator<T> iterator, TriConsumer<T, WrapperBoolean, WrapperBoolean> consumer) {
		WrapperBoolean remover = WrapperBoolean.of(false);
		WrapperBoolean breaker = WrapperBoolean.of(false);
		while (iterator.hasNext()) {
			consumer.accept(iterator.next(), remover, breaker);
			if (remover.get()) {
				iterator.remove();
				remover.set(false);
			}
			if (breaker.get()) {
				return;
			}
		}
	}

	public static <T> void iterateCatching(Collection<T> collection, ThrowableTriConsumer<T, WrapperBoolean, WrapperBoolean> consumer) {
		WrapperBoolean remover = WrapperBoolean.of(false);
		WrapperBoolean breaker = WrapperBoolean.of(false);
		for (Iterator<T> iterator = collection.iterator(); iterator.hasNext(); ) {
			try {
				consumer.accept(iterator.next(), remover, breaker);
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
			if (remover.get()) {
				iterator.remove();
				remover.set(false);
			}
			if (breaker.get()) {
				return;
			}
		}
	}

	public static <T> void iterateNonNull(Collection<T> collection, TriConsumer<T, WrapperBoolean, WrapperBoolean> consumer) {
		if (collection != null) {
			WrapperBoolean remover = WrapperBoolean.of(false);
			WrapperBoolean breaker = WrapperBoolean.of(false);
			for (Iterator<T> iterator = collection.iterator(); iterator.hasNext(); ) {
				T next = iterator.next();
				if (next != null) {
					consumer.accept(next, remover, breaker);
					if (remover.get()) {
						iterator.remove();
						remover.set(false);
					}
					if (breaker.get()) {
						return;
					}
				}
			}
		}
	}

	public static <T> void iterateNonNullValues(Map<?, T> collection, TriConsumer<T, WrapperBoolean, WrapperBoolean> consumer) {
		if (collection != null) {
			WrapperBoolean remover = WrapperBoolean.of(false);
			WrapperBoolean breaker = WrapperBoolean.of(false);
			for (Iterator<T> iterator = collection.values().iterator(); iterator.hasNext(); ) {
				T next = iterator.next();
				if (next != null) {
					consumer.accept(next, remover, breaker);
					if (remover.get()) {
						iterator.remove();
						remover.set(false);
					}
					if (breaker.get()) {
						return;
					}
				}
			}
		}
	}

	// ----- equals/contains
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
		if (c1 == null || c2 == null) {
			return c1 == c2;
		}
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

	// ----- misc
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

	/* @return a singleton list containing the same collection if less than split size, or multiple new collections otherwise */
	public static <T> List<Collection<T>> splitCollection(Collection<T> collection, int splitSize) {
		if (splitSize < 1) {
			throw new Error("split size must be at least 1");
		}
		List<Collection<T>> result = new ArrayList<>();
		if (collection.size() <= splitSize) {  // only one
			result.add(collection);
		} else {  // must split
			List<T> current = new ArrayList<>();
			result.add(current);
			Iterator<? extends T> iterator = collection.iterator();
			while (iterator.hasNext()) {
				current.add(iterator.next());
				if (iterator.hasNext() && current.size() >= splitSize) {
					current = new ArrayList<>();
					result.add(current);
				}
			}
		}
		return result;
	}

}
