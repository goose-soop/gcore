package com.guillaumevdn.gcore.lib.concurrency;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.IteratorControls;
import com.guillaumevdn.gcore.lib.function.ThrowableBiConsumer;
import com.guillaumevdn.gcore.lib.function.ThrowableTriConsumer;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * Uses a ReentrantReadWriteLock (allows multiple readers and prioritizes the only writer if any)
 * Subsets methods will throw an UnsupportedOperationException when called and forEach() or iterate() should be used instead.
 * @author GuillaumeVDN
 */
public class RWLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = -7860239421985019956L;

	// -------------------- constructor --------------------

	public RWLinkedHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	// -------------------- key modifier for subclasses --------------------

	protected K keyModifier(Object key) {
		return (K) key;
	}

	// -------------------- lock --------------------

	private final RWLock lock = new RWLock();

	// -------------------- sets/iteration --------------------

	@Override
	public final Set<K> keySet() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Set<Entry<K, V>> entrySet() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Collection<V> values() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void forEach(BiConsumer<? super K, ? super V> action) {
		lock.read(() -> {
			Iterator<Entry<K, V>> it = super.entrySet().iterator();
			while (it.hasNext()) {
				Entry<K, V> next = it.next();
				action.accept(next.getKey(), next.getValue());
			}
		});
	}

	public final void forEachThrowable(ThrowableBiConsumer<? super K, ? super V> action) throws Throwable {
		lock.readThrowable(() -> {
			Iterator<Entry<K, V>> it = super.entrySet().iterator();
			while (it.hasNext()) {
				Entry<K, V> next = it.next();
				action.accept(next.getKey(), next.getValue());
			}
		});
	}

	public final void iterateAndModify(TriConsumer<K, V, IteratorControls> consumer) {
		try {
			iterateAndModifyOrThrow((key, value, iter) -> consumer.accept(key, value, iter));
		} catch (Throwable exception) {
			throw new RuntimeException(exception);
		}
	}

	public final void iterateAndModifyOrThrow(ThrowableTriConsumer<K, V, IteratorControls> consumer) throws Throwable {
		// always lock on write here
		// if the method is called, it means we want to modify the map
		// if this is called simultaneously and elements are removed, it would throw errors with the usual "read > write > read" process since the iterator would no longer be valid (ConcurrentModificationException)

		lock.writeThrowable(() -> {
			Iterator<Entry<K, V>> it = super.entrySet().iterator();
			IteratorControls iter = new IteratorControls();
			
			while (it.hasNext()) {
				Entry<K, V> next = it.next();
				consumer.accept(next.getKey(), next.getValue(), iter);
				if (iter.mustRemove()) {
					it.remove();
					iter.reset();
				}
				if (iter.mustStop()) {
					break;
				}
			}
		});
	}

	public final HashMap<K, V> copy() {
		return lock.read(() -> {
			HashMap<K, V> copy = new HashMap<>(super.size());
			Iterator<Entry<K, V>> it = super.entrySet().iterator();
			while (it.hasNext()) {
				Entry<K, V> next = it.next();
				copy.put(next.getKey(), next.getValue());
			}
			return copy;
		});
	}

	public final Set<K> copyKeys() {
		return lock.read(() -> {
			return CollectionUtils.asSet(super.keySet());
		});
	}

	public final List<V> copyValues() {
		return lock.read(() -> {
			return CollectionUtils.asList(super.values());
		});
	}

	public final <R> R streamResult(Function<Stream<Entry<K, V>>, R> operator) {
		return lock.read(() -> {
			return operator.apply(super.entrySet().stream());
		});
	}

	public final <R> R streamResultKeys(Function<Stream<K>, R> operator) {
		return lock.read(() -> {
			return operator.apply(super.keySet().stream());
		});
	}

	public final <R> R streamResultValues(Function<Stream<V>, R> operator) {
		return lock.read(() -> {
			return operator.apply(super.values().stream());
		});
	}

	public final void streamValues(Consumer<Stream<V>> operator) {
		lock.read(() -> {
			operator.accept(super.values().stream());
		});
	}

	public final V randomValue() {
		return lock.read(() -> {
			return CollectionUtils.random(super.values());
		});
	}

	// -------------------- object --------------------

	private int unsafeSize() { return super.size(); }
	private V getUnsafe(Object key) { return super.get(keyModifier(key)); }
	private boolean containsKeyUnsafe(Object key) { return super.containsKey(keyModifier(key)); }

	@Override
	public final boolean equals(Object o) {  // adapted code from HashMap
		if (o == this)
			return true;
		if (!(o instanceof Map))
			return false;
		Map<?,?> other = (Map<?,?>) o;

		return lock.read(() -> {
			// other is also a RW map ; lock once and use unsafe methods to avoid locking/unlocking every 3 nanoseconds
			RWLinkedHashMap<?, ?> otherRW = (RWLinkedHashMap<?, ?>) ObjectUtils.castOrNull(other, RWLinkedHashMap.class);
			if (otherRW != null) {
				return otherRW.lock.read(() -> {
					if (otherRW.unsafeSize() != super.size())
						return false;

					try {
						Iterator<Entry<K,V>> i = super.entrySet().iterator();
						while (i.hasNext()) {
							Entry<K,V> e = i.next();
							K key = e.getKey();
							V value = e.getValue();
							if (value == null) {
								if (!(otherRW.getUnsafe(key) == null && otherRW.containsKeyUnsafe(key)))
									return false;
							} else {
								if (!value.equals(otherRW.getUnsafe(key)))
									return false;
							}
						}
					} catch (ClassCastException unused) {
						return false;
					} catch (NullPointerException unused) {
						return false;
					}

					return true;
				});
			}
			// other is not a RW map ; use regular methods
			else {
				if (other.size() != super.size())
					return false;

				try {
					Iterator<Entry<K,V>> i = super.entrySet().iterator();
					while (i.hasNext()) {
						Entry<K,V> e = i.next();
						K key = e.getKey();
						V value = e.getValue();
						if (value == null) {
							if (!(other.get(key) == null && other.containsKey(key)))
								return false;
						} else {
							if (!value.equals(other.get(key)))
								return false;
						}
					}
				} catch (ClassCastException unused) {
					return false;
				} catch (NullPointerException unused) {
					return false;
				}

				return true;
			}
		});
	}

	@Override
	public final int hashCode() {  // adapted code from HashMap
		return lock.read(() -> {
			int h = 0;
			Iterator<Entry<K,V>> i = super.entrySet().iterator();
			while (i.hasNext())
				h += i.next().hashCode();
			return h;
		});
	}

	@Override
	public final String toString() {  // adapted code from HashMap
		return lock.read(() -> {
			Iterator<Entry<K,V>> i = super.entrySet().iterator();
			if (!i.hasNext())
				return "{}";

			StringBuilder sb = new StringBuilder();
			sb.append('{');
			for (;;) {
				Entry<K,V> e = i.next();
				K key = e.getKey();
				V value = e.getValue();
				sb.append(key == this ? "(this Map)" : key);
				sb.append('=');
				sb.append(value == this ? "(this Map)" : value);
				if (!i.hasNext())
					return sb.append('}').toString();
				sb.append(',').append(' ');
			}
		});
	}

	public String toTextString(String separator, String keySeparator) {
		return lock.read(() -> {
			StringBuilder builder = new StringBuilder();
			Iterator<Entry<K, V>> iterator = super.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry next = iterator.next();
				StringUtils.toTextStringObject(next.getKey(), builder);
				builder.append(keySeparator);
				StringUtils.toTextStringObject(next.getValue(), builder);
				if (iterator.hasNext()) {
					builder.append(separator);
				}
			}
			return builder.toString();
		});
	}

	@Override
	public final RWLinkedHashMap<K, V> clone() {
		return lock.read(() -> {
			RWLinkedHashMap<K, V> clone = new RWLinkedHashMap<>(super.size(), 1f);
			clone.putAll(this);
			return clone;
		});
	}

	// -------------------- read methods --------------------

	@Override
	public final boolean isEmpty() {
		return lock.read(() -> {
			return super.isEmpty();
		});
	}

	@Override
	public final int size() {
		return lock.read(() -> {
			return super.size();
		});
	}

	@Override
	public final boolean containsKey(Object key) {
		return lock.read(() -> {
			return super.containsKey(keyModifier(key));
		});
	}

	@Override
	public final boolean containsValue(Object value) {
		return lock.read(() -> {
			return super.containsValue(value);
		});
	}

	@Override
	public final V get(Object key) {
		return lock.read(() -> {
			return super.get(keyModifier(key));
		});
	}

	@Override
	public final V getOrDefault(Object key, V defaultValue) {
		return lock.read(() -> {
			return super.getOrDefault(keyModifier(key), defaultValue);
		});
	}

	// -------------------- write methods --------------------

	@Override
	public final V put(K key, V value) {
		return lock.write(() -> {
			return super.put(keyModifier(key), value);
		});
	}

	@Override
	public final void putAll(Map<? extends K, ? extends V> map) {
		lock.write(() -> {
			map.forEach((key, value) -> super.put(keyModifier(key), value));
		});
	}

	@Override
	public final V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return lock.write(() -> {
			return super.compute(keyModifier(key), remappingFunction);
		});
	}

	@Override
	public final void clear() {
		lock.write(() -> {
			super.clear();
		});
	}

	/* -------------------- (potential) write methods with loss of performance --------------------
		due to (a) pre-check that would be made during iteration in the original method
		       (b) using a write lock without being sure that modifications will be made
		... I'm too lazy to completely rewrite a HashMap implementation with "low-level" direct operations on the table :Kappa:
	 */

	@Override
	public final V remove(Object key) {
		return lock.write(() -> {
			return super.remove(keyModifier(key));
		});
	}

	@Override
	public final boolean remove(Object key, Object mustBeValue) {
		return Objects.deepEquals(remove(keyModifier(key)), mustBeValue);
	}

	@Override
	public final V putIfAbsent(K key, V value) {
		V existing = get(key);
		if (existing == null) {
			put(key, value);
		} else {
			existing = value;
		}
		return existing;
	}

	/**
	 * Replaces the entry for the specified key only if it is currently not mapped to some value.
	 * @return true if the value was modified
	 */
	public final boolean replaceIfNot(K key, V value) {
		V existing = get(key);
		if (!Objects.deepEquals(existing, value)) {
			put(key, value);
			return true;
		}
		return false;
	}

	@Override
	public final V replace(K key, V value) {
		V existing = get(key);
		if (existing != null) {
			put(key, value);
		}
		return existing;
	}

	@Override
	public final boolean replace(K key, V oldValue, V newValue) {
		V existing = get(key);
		if (Objects.deepEquals(existing, oldValue)) {
			put(key, newValue);
			return true;
		}
		return false;
	}

	@Override
	public final void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		lock.write(() -> {
			super.replaceAll(function);
		});
	}

	@Override
	public final V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		V existing = get(key);
		if (existing == null) {
			put(key, value);
			return value;
		}
		return lock.write(() -> {
			V newValue = remappingFunction.apply(existing, value);
			if (newValue == null) {
				super.remove(key);
			} else {
				put(key, newValue);
			}
			return newValue;
		});
	}

	@Override
	public final V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		V existing = get(key);
		if (existing != null) {
			return existing;
		}
		V newValue = mappingFunction.apply(key);
		put(key, newValue);
		return newValue;
	}

	@Override
	public final V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		V existing = get(key);
		if (existing != null) {
			return lock.write(() -> {
				V newValue = remappingFunction.apply(key, existing);
				if (newValue == null) {
					super.remove(key);
				} else {
					put(key, newValue);
				}
				return newValue;
			});
		}
		return existing;
	}

}
