package com.guillaumevdn.gcore.lib.data.board.keyed;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.concurrency.RWHashSet;
import com.guillaumevdn.gcore.lib.data.Board;
import com.guillaumevdn.gcore.lib.data.BoardType;
import com.guillaumevdn.gcore.lib.data.DataBackEnd;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.function.QuadriConsumer;
import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.wrapper.WrapperBoolean;

/**
 * @author GuillaumeVDN
 */
public abstract class KeyedBoard<K, V, R extends KeyReference<K>> extends Board {

	private final Class<V> valueClass;
	protected final RWHashMap<K, V> cache = new RWHashMap<>(10, 1f);

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

	public final V getCachedValue(R key) {
		return cache.get(key.getKey());
	}

	public final List<V> copyCacheValues() {
		return cache.copyValues();
	}

	public final <RES> RES streamResultValues(Function<Stream<V>, RES> operator) {
		return cache.streamResultValues(operator);
	}

	public final void iterateAndModifyCache(QuadriConsumer<K, V, WrapperBoolean /* remover */, WrapperBoolean /* breaker */> consumer) {
		cache.iterateAndModify(consumer);
	}

	public final void iterateCache(BiConsumer<K, V> consumer) {
		cache.forEach(consumer);
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- save
	// ----------------------------------------------------------------------------------------------------

	private transient RWHashSet<R> toSave = new RWHashSet<>(5);

	@Override
	public boolean mustSaveSomething() {
		return !toSave.isEmpty();
	}

	public final void addCachedToSave(R element) {
		toSave.add(element);
	}

	protected final void removeCachedToSaveIf(Predicate<R> filter) {
		toSave.removeIf(filter);
	}

	@Override
	public final void saveNeeded(BukkitThread thread, ThrowableRunnable callback) {
		pushElements(thread, toSave.copy(), callback);
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- data
	// ----------------------------------------------------------------------------------------------------

	public final void pullElements(BukkitThread thread, Set<R> references, ThrowableRunnable callback) {
		if (references.isEmpty()) {
			return;
		}
		operate(thread, "pull board elements " + StringUtils.toTextString(", ", references), () -> {
			toSave.removeAll(references);
			references.forEach(ref -> pulledElement(thread, ref, getCachedValue(ref)));
			if (callback != null) {
				callback.run();
			}
		}, () -> {
			toSave.removeAll(references);
			if (DataBackEnd.MYSQL.equals(getBackEnd())) {
				remotePullElementsMySQL(references);
			} else if (DataBackEnd.JSON.equals(getBackEnd())) {
				remotePullElementsJson(references);
			}
		});
	}

	protected void pulledElement(BukkitThread thread, R reference, V value) {
	}

	public final void pushElements(BukkitThread thread, Set<R> references, ThrowableRunnable callback) {
		if (references.isEmpty()) {
			return;
		}
		beforePushElements(thread, references);
		operate(thread, "push board elements " + references, callback, () -> {
			toSave.removeAll(references);
			if (DataBackEnd.MYSQL.equals(getBackEnd())) {
				remotePushElementsMySQL(references);
			} else if (DataBackEnd.JSON.equals(getBackEnd())) {
				remotePushElementsJson(references);
			}
		});
	}

	protected void beforePushElements(BukkitThread thread, Set<R> references) {
	}

	public final void deleteElements(BukkitThread thread, Set<R> references, ThrowableRunnable callback) {
		if (references.isEmpty()) {
			return;
		}
		references.forEach(ref -> beforeDeleteElement(thread, ref, getCachedValue(ref)));
		operate(thread, "delete board elements " + references, callback, () -> {
			toSave.removeAll(references);
			references.forEach(ref -> cache.remove(ref.getKey()));
			if (DataBackEnd.MYSQL.equals(getBackEnd())) {
				remoteDeleteElementsMySQL(references);
			} else if (DataBackEnd.JSON.equals(getBackEnd())) {
				remoteDeleteElementsJson(references);
			}
		});
	}

	public abstract void removeElementsFromCache(Set<R> references);

	protected void beforeDeleteElement(BukkitThread thread, R reference, V value) {
	}

	public final void disposeCacheElements(BukkitThread thread, R reference, ThrowableRunnable callback) {
		disposeCacheElements(thread, CollectionUtils.asSet(reference), callback);
	}

	public final void disposeCacheElements(BukkitThread thread, Set<R> references, ThrowableRunnable callback) {
		if (references.isEmpty()) {
			return;
		}
		references.forEach(ref -> beforeDisposeCacheElement(thread, ref, getCachedValue(ref)));  // this might set some more elements to save

		// disposing means "saving if needed and then remove from valuesCache" ; if elements don't need to be saved, remove directly from valuesCache
		Set<R> mustPush = references.stream().filter(ref -> toSave.contains(ref)).collect(Collectors.toSet());
		Set<R> musntPush = references.stream().filter(ref -> !toSave.contains(ref)).collect(Collectors.toSet());
		removeElementsFromCache(musntPush);
		musntPush.forEach(ref -> disposedCacheElement(ref));;

		// push needed elements, then remove from valuesCache
		pushElements(thread, mustPush, () -> {
			removeElementsFromCache(mustPush);
			mustPush.forEach(ref -> disposedCacheElement(ref));
			if (callback != null) {
				callback.run();
			}
		});
	}

	protected void beforeDisposeCacheElement(BukkitThread thread, R reference, V value) {
	}

	protected void disposedCacheElement(R reference) {
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- json
	// ----------------------------------------------------------------------------------------------------

	// ----- wrapper and file
	public abstract File getRoot();
	public abstract File getFile(K key);
	public abstract K getKey(File file);

	// ----- init
	@Override
	protected final void remoteInitJson() throws Throwable {
		getRoot().mkdirs();
	}

	// ----- pull
	@Override
	protected void remotePullAllJson() throws Throwable {
		File root = getRoot();
		if (root.exists()) {
			Set<KeyReference<K>> keys = new HashSet<>();
			for (File file : root.listFiles()) {
				if (file.isFile()) {
					K key = getKey(file);
					if (key != null) {
						keys.add(new KeyReference<>(key));
					}
				}
			}
			remotePullKeysJson(keys);
		}
	}

	protected final void remotePullElementsJson(Set<R> references) throws Throwable {
		remotePullKeysJson(references.stream().map(ref -> new KeyReference<K>(ref.getKey())).collect(Collectors.toSet()));
	}

	protected final void remotePullKeysJson(Set<KeyReference<K>> keys) throws Throwable {
		for (KeyReference<K> ref : keys) {
			K key = ref.getKey();
			File file = getFile(key);
			if (file.exists()) {
				try (FileReader reader = new FileReader(file)) {
					V value = valueFromJson(reader);
					if (value != null) {
						cache.put(key, value);
					}
				}
			}
		}
	}

	protected V valueFromJson(FileReader reader) {  // this can be overriden because for complex values such as maps gson seems to be drunk and puts keys/values as raw string in the map
		return getPlugin().getPrettyGson().fromJson(reader, valueClass);
	}

	// ----- push
	protected final void remotePushElementsJson(Set<R> references) throws Throwable {
		remotePushKeysJson(references.stream().map(ref -> new KeyReference<K>(ref.getKey())).collect(Collectors.toSet()));
	}

	protected final void remotePushKeysJson(Set<KeyReference<K>> keys) throws Throwable {
		for (KeyReference<K> ref : keys) {
			K key = ref.getKey();
			V value = cache.get(key);
			File file = getFile(key);
			if (value == null) {
				FileUtils.delete(file);
			} else {
				FileUtils.reset(file);
				try (FileWriter writer = new FileWriter(file)) {
					valueToJson(value, writer);
				}
			}
		}
	}

	protected void valueToJson(V value, FileWriter writer) {  // this can be overriden because for complex values such as maps gson seems to be drunk and puts keys/values as raw string in the map
		getPlugin().getPrettyGson().toJson(value, valueClass, writer);
	}

	// ----- delete
	protected final void remoteDeleteElementsJson(Set<R> references) throws Throwable {
		// at this point, values have been removed from valuesCache
		// this method will delete the file if no value, or update with remaining values
		remotePushElementsJson(references);
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- mysql
	// ----------------------------------------------------------------------------------------------------

	protected abstract void remotePullElementsMySQL(Set<R> references) throws Throwable;
	protected abstract void remotePushElementsMySQL(Set<R> references) throws Throwable;
	protected abstract void remoteDeleteElementsMySQL(Set<R> references) throws Throwable;

}
