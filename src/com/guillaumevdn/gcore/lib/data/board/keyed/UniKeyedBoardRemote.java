package com.guillaumevdn.gcore.lib.data.board.keyed;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.data.BoardType;

/**
 * @author GuillaumeVDN
 */
public abstract class UniKeyedBoardRemote<K, V> extends UniKeyedBoard<K, V> {

	public UniKeyedBoardRemote(GPlugin plugin, String id, Class<V> valueClass, int saveDelayTicks) {
		super(plugin, id, BoardType.REMOTE, valueClass, saveDelayTicks);
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- get
	// ----------------------------------------------------------------------------------------------------

	public void fetchValue(K key, Consumer<V> ifFound, Supplier<V> def, boolean forceFetch, boolean mustCache) {
		// force fetch
		if (forceFetch) {
			cache.remove(key);
		}
		// cached
		V cachedValue = cache.get(key);
		if (cachedValue != null) {
			if (ifFound != null) {
				ifFound.accept(cachedValue);
			}
		}
		// not cached, fetch
		else {
			KeyReference<K> ref = new KeyReference<>(key);
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
				V result = cache.get(key);
				if (result == null && def != null) {
					result = def.get();
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

	// ----------------------------------------------------------------------------------------------------
	// ----- set
	// ----------------------------------------------------------------------------------------------------

	public final void putValue(K key, V value, Runnable onPush, boolean mustCache) {
		// valuesCache new value
		if (mustCache) {
			cache.put(key, value);
		}
		// push element
		KeyReference<K> ref = new KeyReference<>(key);
		pushElements(BukkitThread.ASYNC, CollectionUtils.asSet(ref), () -> {
			try {
				if (onPush != null) {
					onPush.run();
				}
			} catch (Throwable exception) {
				throw exception;
			} finally {
				if (!mustCache) {
					disposeCacheElements(BukkitThread.ASYNC, ref, null); // will be saved if needed
				}
			}
		});
		// done
		onValueSet(key, value);
	}

	protected void onValueSet(K key, V value) {
	}

}
