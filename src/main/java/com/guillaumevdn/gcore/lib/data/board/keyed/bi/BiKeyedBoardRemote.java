package com.guillaumevdn.gcore.lib.data.board.keyed.bi;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.data.board.BoardType;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public abstract class BiKeyedBoardRemote<K, K2, V> extends BiKeyedBoard<K, K2, V> {

	public BiKeyedBoardRemote(GPlugin plugin, String id, Class<V> valueClass, int saveDelayTicks) {
		super(plugin, id, BoardType.REMOTE, valueClass, saveDelayTicks);
	}

	public void fetchValuesByPrimary(K key, Consumer<Map<K2, V>> ifFound, boolean forceFetch, boolean mustCache) {
		// cached
		if (forceFetch) {
			iterateAndModifyCache((k, value, iter) -> {
				if (k.equals(key)) {
					iter.remove();
				}
			});
		} else {
			if (ifFound != null) {
				Map<K2, V> cachedValue = streamResult(str -> str
						.filter(e -> e.getKey().getA().equals(key))
						.collect(Collectors.toMap(e -> e.getKey().getB(), e -> e.getValue()))
						);
				if (!cachedValue.isEmpty()) {
					ifFound.accept(cachedValue);
					return;
				}
			}
		}

		// not cached or force fetch
		pullKeysByPrimary(BukkitThread.ASYNC, CollectionUtils.asSet(key), () -> {
			// build final consumer
			Consumer<Map<K2, V>> consumer = values -> {
				try {
					if (ifFound != null) {
						ifFound.accept(values);
					}
				} catch (Throwable exception) {
					throw exception;
				} finally {
					if (!mustCache) {
						disposeCacheElements(
								BukkitThread.ASYNC,
								values.entrySet().stream().map(e -> Pair.of(key, e.getKey())).collect(Collectors.toSet()),
								null
								);  // will be saved if needed
					}
				}
			};

			// absent
			Map<K2, V> result = streamResult(str -> str
					.filter(e -> e.getKey().getA().equals(key))
					.collect(Collectors.toMap(e -> e.getKey().getB(), e -> e.getValue()))
					);

			// process
			if (result != null) {
				consumer.accept(result);
			}
		});

	}

	public final void fetchValue(K key, K2 key2, Consumer<V> ifFound, Supplier<V> def, boolean forceFetch, boolean mustCache) {
		// cached
		Pair<K, K2> ref = Pair.of(key, key2);
		V cachedValue = forceFetch ? removeFromCache(ref) : getCachedValue(ref);
		if (cachedValue != null && !forceFetch) {
			if (ifFound != null) {
				ifFound.accept(cachedValue);
			}
		}
		// not cached, fetch
		else {
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
							disposeCacheElement(BukkitThread.ASYNC, ref, null); // will be saved if needed
						}
					}
				};
				// absent
				V result = getCachedValue(key, key2);
				if (result == null && def != null) {
					result = def.get();
					if (mustCache) {
						putInCache(ref, result);
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
	// 		set
	// ----------------------------------------------------------------------------------------------------

	public final void putValue(K key, K2 key2, V value, Runnable onPush, boolean mustCache) {
		Pair<K, K2> ref = Pair.of(key, key2);

		// put and push element
		putInCache(ref, value);
		pushElements(BukkitThread.ASYNC, CollectionUtils.asSet(ref), () -> {
			if (onPush != null) {
				onPush.run();
			}
			if (!mustCache) {
				disposeCacheElement(BukkitThread.ASYNC, ref, null);
			}
		});

		// done
		onValuePut(key, key2, value);
	}

	protected void onValuePut(K key, K2 key2, V value) {
	}

}
