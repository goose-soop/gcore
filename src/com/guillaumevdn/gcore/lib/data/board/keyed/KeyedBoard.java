package com.guillaumevdn.gcore.lib.data.board.keyed;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.IteratorControls;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.concurrency.RWHashSet;
import com.guillaumevdn.gcore.lib.data.board.Board;
import com.guillaumevdn.gcore.lib.data.board.BoardType;
import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public abstract class KeyedBoard<K, V> extends Board<ConnectorKeyed<K, V>> {

	private final RWHashMap<K, V> cache = new RWHashMap<>(10, 1f);
	private final Class<V> valueClass;

	public KeyedBoard(GPlugin plugin, String id, BoardType type, Class<V> valueClass, int saveDelayTicks) {
		super(plugin, id, type, saveDelayTicks);
		this.valueClass = valueClass;
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- get
	// ----------------------------------------------------------------------------------------------------

	public final Class<V> getValueClass() {
		return valueClass;
	}

	public final V getCachedValue(K key) {
		return cache.get(key);
	}

	public final Map<K, V> copyCache() {
		return cache.copy();
	}

	public final Set<K> copyCacheKeys() {
		return cache.copyKeys();
	}

	public final List<V> copyCacheValues() {
		return cache.copyValues();
	}

	public final <RES> RES streamResult(Function<Stream<Map.Entry<K, V>>, RES> operator) {
		return cache.streamResult(operator);
	}

	public final <RES> RES streamResultValues(Function<Stream<V>, RES> operator) {
		return cache.streamResultValues(operator);
	}

	public final void iterateAndModifyCache(TriConsumer<K, V, IteratorControls> consumer) {
		cache.iterateAndModify(consumer);
	}

	public final void iterateCache(BiConsumer<K, V> consumer) {
		cache.forEach(consumer);
	}

	public final V computeCacheIfAbsent(K key, Function<K, V> ifAbsent) {
		return cache.computeIfAbsent(key, ifAbsent);
	}

	public final V putInCache(K key, V value) {
		return cache.put(key, value);
	}

	public final V removeFromCache(K key) {
		return cache.remove(key);
	}

	public final void clearCache() {
		cache.clear();
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- save
	// ----------------------------------------------------------------------------------------------------

	private transient RWHashSet<K> toSave = new RWHashSet<>(5);

	@Override
	public boolean mustSaveSomething() {
		return !toSave.isEmpty();
	}

	public final void addCachedToSave(K element) {
		toSave.add(element);
	}

	protected final void removeCachedToSaveIf(Predicate<K> filter) {
		toSave.removeIf(filter);
	}

	@Override
	public final void saveNeeded(BukkitThread thread) {
		toSave.consume().forEach(r -> {
			pushElements(thread, CollectionUtils.asSet(r), null);
		});
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- data
	// ----------------------------------------------------------------------------------------------------

	public final void pullElements(BukkitThread thread, Set<K> keys, ThrowableRunnable callback) {
		if (keys.isEmpty()) {
			return;
		}
		operate(thread, "pull board elements " + StringUtils.toTextString(", ", keys), () -> {
			toSave.removeAll(keys);
			keys.forEach(key -> pulledElement(thread, key, getCachedValue(key)));
			if (callback != null) {
				callback.run();
			}
		}, () -> {
			toSave.removeAll(keys);
			operateOnConnector(c -> c.remotePullElements(keys));
		});
	}

	protected void pulledElement(BukkitThread thread, K key, V value) {
	}

	public final void pushElements(BukkitThread thread, Set<K> keys, ThrowableRunnable callback) {
		if (keys.isEmpty()) {
			return;
		}
		beforePushElements(thread, keys);
		operate(thread, "push board elements " + keys, callback, () -> {
			toSave.removeAll(keys);
			operateOnConnector(c -> c.remotePushElements(keys));
		});
	}

	protected void beforePushElements(BukkitThread thread, Set<K> keys) {
	}

	public final void deleteElements(BukkitThread thread, Set<K> keys, ThrowableRunnable callback) {
		if (keys.isEmpty()) {
			return;
		}
		keys.forEach(key -> beforeDeleteElement(thread, key, getCachedValue(key)));
		operate(thread, "delete board elements " + keys, callback, () -> {
			toSave.removeAll(keys);
			keys.forEach(key -> cache.remove(key));
			operateOnConnector(c -> c.remoteDeleteElements(keys));
		});
	}

	public void removeElementsFromCache(Set<K> keys) {
		keys.forEach(key -> removeFromCache(key));
	}

	protected void beforeDeleteElement(BukkitThread thread, K key, V value) {
	}

	public final void disposeCacheElement(BukkitThread thread, K key, ThrowableRunnable callback) {
		disposeCacheElements(thread, CollectionUtils.asSet(key), callback);
	}

	public final void disposeCacheElements(BukkitThread thread, Set<K> keys, ThrowableRunnable callback) {
		if (keys.isEmpty()) {
			return;
		}
		keys.forEach(key -> beforeDisposeCacheElement(thread, key, getCachedValue(key)));  // this might set some more elements to save

		// disposing means "saving if needed and then remove from valuesCache" ; if elements don't need to be saved, remove directly from valuesCache
		Set<K> mustPush = keys.stream().filter(key -> toSave.contains(key)).collect(Collectors.toSet());
		Set<K> musntPush = keys.stream().filter(key -> !toSave.contains(key)).collect(Collectors.toSet());
		removeElementsFromCache(musntPush);
		musntPush.forEach(key -> disposedCacheElement(key));;

		// push needed elements, then remove from valuesCache
		pushElements(thread, mustPush, () -> {
			removeElementsFromCache(mustPush);
			mustPush.forEach(key -> disposedCacheElement(key));
			if (callback != null) {
				callback.run();
			}
		});

		// if there's nothing to push, call callback instantly
		if (mustPush.isEmpty() && callback != null) {
			try {
				callback.run();
			} catch (Throwable exeption) {
				exeption.printStackTrace();
			}
		}
	}

	protected void beforeDisposeCacheElement(BukkitThread thread, K key, V value) {
	}

	protected void disposedCacheElement(K key) {
	}

}
