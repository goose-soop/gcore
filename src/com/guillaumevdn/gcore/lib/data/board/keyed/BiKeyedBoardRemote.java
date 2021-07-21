package com.guillaumevdn.gcore.lib.data.board.keyed;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.data.BoardType;

/**
 * @author GuillaumeVDN
 */
public abstract class BiKeyedBoardRemote<K, K2, V> extends BiKeyedBoard<K, K2, V> {

	public BiKeyedBoardRemote(GPlugin plugin, String id, int saveDelayTicks) {
		super(plugin, id, BoardType.REMOTE, saveDelayTicks);
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- get
	// ----------------------------------------------------------------------------------------------------

	public final V getCachedValue(K key, K2 key2) {
		RWHashMap<K2, V> values = cache.get(key);
		return values == null ? null : values.get(key2);
	}

	public void fetchValues(K key, Consumer<RWHashMap<K2, V>> ifFound, boolean createDef, boolean forceFetch, boolean mustCache) {
		// cached
		RWHashMap<K2, V> cachedValue = forceFetch ? cache.remove(key) : cache.get(key);
		if (cachedValue != null && !forceFetch) {
			if (ifFound != null) {
				ifFound.accept(cachedValue);
			}
		}
		// not cached, fetch
		else {
			KeyReference<K> ref = new KeyReference<>(key);
			pullKeys(BukkitThread.ASYNC, CollectionUtils.asSet(ref), () -> {
				// build final consumer
				Consumer<RWHashMap<K2, V>> consumer = values -> {
					try {
						if (ifFound != null) {
							ifFound.accept(values);
						}
					} catch (Throwable exception) {
						throw exception;
					} finally {
						if (!mustCache) {
							disposeCacheElements(BukkitThread.ASYNC, values.streamResultKeys(s -> s.map(k2 -> BiKeyReference.of(key, k2)).collect(Collectors.toSet())), null); // will be saved if needed
						}
					}
				};
				// absent
				RWHashMap<K2, V> result = cache.get(key);
				if (result == null && createDef) {
					result = new RWHashMap<>();
					if (mustCache) {
						cache.put(key, result);
					}
				}
				// process
				if (result != null) {
					consumer.accept(result);
				}
			});
		}
	}

	public final void fetchValue(K key, K2 key2, Consumer<V> ifFound, Supplier<V> def, boolean forceFetch, boolean mustCache) {
		// cached
		V cachedValue = forceFetch ? deleteCacheElement(key, key2) : getCachedValue(key, key2);
		if (cachedValue != null && !forceFetch) {
			if (ifFound != null) {
				ifFound.accept(cachedValue);
			}
		}
		// not cached, fetch
		else {
			BiKeyReference<K, K2> ref = new BiKeyReference<>(key, key2);
			pullElements(BukkitThread.ASYNC, CollectionUtils.asSet(ref), () -> {
				// build final consumer
				Consumer<V> consumer = values -> {
					try {
						if (ifFound != null) {
							ifFound.accept(values);
						}
					} catch (Throwable exception) {
						throw exception;
					} finally {
						if (!mustCache) {
							disposeCacheElements(BukkitThread.ASYNC, ref, null); // will be saved if needed
						}
					}
				};
				// absent
				V result = getCachedValue(key, key2);
				if (result == null && def != null) {
					result = def.get();
					if (mustCache) {
						cache.computeIfAbsent(key, __ -> new RWHashMap<>()).put(key2, result);
					}
				}
				// process
				if (result != null) {
					consumer.accept(result);
				}
			});
		}
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- set
	// ----------------------------------------------------------------------------------------------------

	public final void putValue(K key, K2 key2, V value, Runnable onPush, boolean mustCache) {
		BiKeyReference<K, K2> ref = new BiKeyReference<>(key, key2);
		// valuesCache new value
		cache.computeIfAbsent(key, k -> new RWHashMap<>()).put(key2, value);
		// push element
		pushElements(BukkitThread.ASYNC, CollectionUtils.asSet(ref), () -> {
			if (onPush != null) {
				onPush.run();
			}
			if (!mustCache) {
				disposeCacheElements(BukkitThread.ASYNC, ref, null);
			}
		});
		// done
		onValuePut(key, key2, value);
	}

	protected void onValuePut(K key, K2 key2, V value) {
	}

}
