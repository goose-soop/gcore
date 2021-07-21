package com.guillaumevdn.gcore.lib.concurrency;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.function.QuadriConsumer;
import com.guillaumevdn.gcore.lib.function.ThrowableQuadriConsumer;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.wrapper.WrapperBoolean;

/**
 * Since this can remove things when reading, we need standard synchronization
 * Subsets methods will throw an UnsupportedOperationException when called and forEach() or iterate() should be used instead.
 * @author GuillaumeVDN
 */
public class RWWeakHashMap<K, V> extends WeakHashMap<K, V> {

	// -------------------- utils --------------------

	protected K keyModifier(Object key) {  // key modifiers for subclasses
		return (K) key;
	}

	public void lock(Runnable operation) {
		try {
			synchronized (this) {
				operation.run();
			}
		} catch (Throwable error) {
			throw new RuntimeException("ERROR WHILE LOCKED, thread " + Thread.currentThread().toString(), error);
		}
	}

	public <R> R lock(Supplier<R> operation) {
		try {
			synchronized (this) {
				return operation.get();
			}
		} catch (Throwable error) {
			throw new RuntimeException("ERROR WHILE LOCKED, thread " + Thread.currentThread().toString(), error);
		}
	}

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
		lock(() -> {
			Iterator<Entry<K, V>> it = super.entrySet().iterator();
			while (it.hasNext()) {
				Entry<K, V> next = it.next();
				action.accept(next.getKey(), next.getValue());
			}
		});
	}

	/**
	 * @param consumer next key, next value, remover (set to true to remove current element), breaker (set to true to stop iterating)
	 */
	public final void iterate(QuadriConsumer<K, V, WrapperBoolean /* remover */, WrapperBoolean /* breaker */> consumer) {
		try {
			iterateOrThrow((key, value, remover, breaker) -> consumer.accept(key, value, remover, breaker));
		} catch (Throwable exception) {
			throw new RuntimeException(exception);
		}
	}

	public final void iterateOrThrow(ThrowableQuadriConsumer<K, V, WrapperBoolean /* remover */, WrapperBoolean /* breaker */> consumer) throws Throwable {
		lock(() -> {
			Iterator<Entry<K, V>> it = super.entrySet().iterator();
			WrapperBoolean remover = WrapperBoolean.of(false);
			WrapperBoolean breaker = WrapperBoolean.of(false);
			while (it.hasNext()) {
				Entry<K, V> next = it.next();
				try {
					consumer.accept(next.getKey(), next.getValue(), remover, breaker);
				} catch (Throwable exception) {
					exception.printStackTrace();
				}
				if (remover.get()) {
					it.remove();
					remover.set(false);
				}
				if (breaker.get()) {
					break;
				}
			}
		});
	}

	public final WeakHashMap<K, V> copy() {
		return lock(() -> {
			WeakHashMap<K, V> copy = new WeakHashMap<>();
			Iterator<Entry<K, V>> it = super.entrySet().iterator();
			while (it.hasNext()) {
				Entry<K, V> next = it.next();
				copy.put(next.getKey(), next.getValue());
			}
			return copy;
		});
	}

	public final Set<K> copyKeys() {
		return lock(() -> {
			return CollectionUtils.asSet(super.keySet());
		});
	}

	public final List<V> copyValues() {
		return lock(() -> {
			return CollectionUtils.asList(super.values());
		});
	}

	public final <R> R streamResultKeys(Function<Stream<K>, R> operator) {
		return lock(() -> {
			return operator.apply(super.keySet().stream());
		});
	}

	public final <R> R streamResultValues(Function<Stream<V>, R> operator) {
		return lock(() -> {
			return operator.apply(super.values().stream());
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

		return lock(() -> {
			// other is also a RW map ; lock once and use unsafe methods to avoid locking/unlocking every 3 nanoseconds
			RWWeakHashMap<?, ?> otherRW = (RWWeakHashMap<?, ?>) ObjectUtils.castOrNull(other, RWWeakHashMap.class);
			if (otherRW != null) {
				return otherRW.lock(() -> {
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
		return lock(() -> {
			int h = 0;
			Iterator<Entry<K,V>> i = super.entrySet().iterator();
			while (i.hasNext())
				h += i.next().hashCode();
			return h;
		});
	}

	@Override
	public final String toString() {
		return "{" + toTextString(", ", "=") + "}";
	}

	public String toTextString(String separator, String keySeparator) {
		return lock(() -> {
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
	public final RWWeakHashMap<K, V> clone() {
		return lock(() -> {
			RWWeakHashMap<K, V> clone = new RWWeakHashMap<>();
			clone.putAll(this);
			return clone;
		});
	}

	// -------------------- read methods --------------------

	@Override
	public final boolean isEmpty() {
		return lock(() -> {
			return super.isEmpty();
		});
	}

	@Override
	public final int size() {
		return lock(() -> {
			return super.size();
		});
	}

	@Override
	public final boolean containsKey(Object key) {
		return lock(() -> {
			return super.containsKey(keyModifier(key));
		});
	}

	@Override
	public final boolean containsValue(Object value) {
		return lock(() -> {
			return super.containsValue(value);
		});
	}

	@Override
	public final V get(Object key) {
		return lock(() -> {
			return super.get(keyModifier(key));
		});
	}

	@Override
	public final V getOrDefault(Object key, V defaultValue) {
		return lock(() -> {
			return super.getOrDefault(keyModifier(key), defaultValue);
		});
	}

	// -------------------- write methods --------------------

	@Override
	public final V put(K key, V value) {
		return lock(() -> {
			return super.put(keyModifier(key), value);
		});
	}

	@Override
	public final void putAll(Map<? extends K, ? extends V> map) {
		lock(() -> {
			map.forEach((key, value) -> super.put(keyModifier(key), value));
		});
	}

	@Override
	public final V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return lock(() -> {
			return super.compute(keyModifier(key), remappingFunction);
		});
	}

	@Override
	public final void clear() {
		lock(() -> {
			super.clear();
		});
	}

	/* -------------------- (potential) write methods with loss of performance --------------------
		due to (a) pre-check that would be made during iteration in the original method
		       (b) using a write lock without being sure that modifications will be made
		... I'm too lazy to completely rewrite a HashSet implementation with "low-level" direct operations on the table :Kappa:
	 */

	@Override
	public final V remove(Object key) {
		return lock(() -> {
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
		}
		return super.putIfAbsent(keyModifier(key), value);
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
		lock(() -> {
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
		return lock(() -> {
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
			return lock(() -> {
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
