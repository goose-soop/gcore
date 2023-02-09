package com.guillaumevdn.gcore.lib.data.board.keyed;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.data.board.BoardType;

/**
 * @author GuillaumeVDN
 */
public abstract class KeyedBoardRemote<K, V> extends KeyedBoard<K, V> {

	public KeyedBoardRemote(GPlugin plugin, String id, Class<V> valueClass, int saveDelayTicks) {
		super(plugin, id, BoardType.REMOTE, valueClass, saveDelayTicks);
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- get
	// ----------------------------------------------------------------------------------------------------

	public void fetchValue(K key, Consumer<V> ifFound, Supplier<V> def, boolean forceFetch, boolean mustCache) {
		fetchValue(BukkitThread.ASYNC, key, ifFound, def, forceFetch, mustCache);
	}

	public void fetchValue(BukkitThread pullThread, K key, Consumer<V> ifFound, Supplier<V> def, boolean forceFetch, boolean mustCache) {
		// force fetch
		if (forceFetch) {
			removeFromCache(key);
		}
		// cached
		V cachedValue = getCachedValue(key);
		if (cachedValue != null) {
			if (ifFound != null) {
				ifFound.accept(cachedValue);
			}
		}
		// not cached, fetch
		else {
			pullElements(pullThread, CollectionUtils.asSet(key), () -> {
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
							disposeCacheElement(BukkitThread.ASYNC, key, null);  // will be saved if needed
						}
					}
				};
				// absent
				V result = getCachedValue(key);
				if (result == null && def != null) {
					result = def.get();
					if (mustCache) {
						putInCache(key, result);
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

	public final void putValue(K key, V value, Runnable pushCallback, boolean mustCache) {
		// valuesCache new value
		if (mustCache) {
			putInCache(key, value);
		}
		// push element
		pushElements(BukkitThread.ASYNC, CollectionUtils.asSet(key), () -> {
			try {
				if (pushCallback != null) {
					pushCallback.run();
				}
			} catch (Throwable exception) {
				throw exception;
			} finally {
				if (!mustCache) {
					disposeCacheElement(BukkitThread.ASYNC, key, null); // will be saved if needed
				}
			}
		});
		// done
		onValueSet(key, value);
	}

	protected void onValueSet(K key, V value) {
	}

}
