package com.guillaumevdn.gcore.lib.data.board.keyed;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.data.Board;
import com.guillaumevdn.gcore.lib.data.BoardType;
import com.guillaumevdn.gcore.lib.data.DataBackEnd;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public abstract class KeyedBoard<K, V, R extends KeyReference<K>> extends Board {

	private final Class<V> valueClass;
	protected final Map<K, V> cache = new HashMap<>();

	public KeyedBoard(GPlugin plugin, String id, BoardType type, Class<V> valueClass, int saveDelayTicks) {
		super(plugin, id, type, saveDelayTicks);
		this.valueClass = valueClass;
	}

	// ----------------------------------------------------------------------------------------------------
	// get
	// ----------------------------------------------------------------------------------------------------

	public final Map<K, V> getCache() {
		return Collections.unmodifiableMap(cache);
	}

	public final Set<K> getCacheKeys() {
		return Collections.unmodifiableSet(cache.keySet());
	}

	public final Collection<V> getCacheValues() {
		return Collections.unmodifiableCollection(cache.values());
	}

	protected V getCachedValue(K key) {
		return cache.get(key);
	}

	protected V getCachedValue(R key) {
		return cache.get(key.getKey());
	}

	protected Collection<V> getCachedValues(Set<R> refs) {
		List<V> values = new ArrayList<>();
		refs.forEach(ref -> {
			V value = getCachedValue(ref);
			if (value != null) {
				values.add(value);
			}
		});
		return Collections.unmodifiableCollection(values);
	}

	// ----------------------------------------------------------------------------------------------------
	// save
	// ----------------------------------------------------------------------------------------------------

	protected transient Set<R> toSave = new HashSet<>();

	@Override
	public boolean mustSaveSomething() {
		return !toSave.isEmpty();
	}

	public final void addCachedToSave(R element) {
		toSave.add(element);
	}

	@Override
	public final void saveNeeded(BukkitThread thread, ThrowableRunnable callback) {
		pushElements(thread, CollectionUtils.asSet(toSave), callback);
	}

	// ----------------------------------------------------------------------------------------------------
	// data
	// ----------------------------------------------------------------------------------------------------

	public final void pullElements(BukkitThread thread, Set<R> references, ThrowableRunnable callback) {
		if (references.isEmpty()) {
			return;
		}
		operate(thread, "pull board elements " + StringUtils.toTextString(", ", references), () -> {
			toSave.removeAll(references);
			onPulledElements(thread, references);
			if (callback != null) {
				callback.run();
			}
		}, () -> {
			if (DataBackEnd.MYSQL.equals(getBackEnd())) {
				remotePullElementsMySQL(references);
			} else if (DataBackEnd.JSON.equals(getBackEnd())) {
				remotePullElementsJson(references);
			}
		});
	}

	protected void onPulledElements(BukkitThread thread, Set<R> references) {
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
		beforeDeleteElements(thread, references);
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
	protected void beforeDeleteElements(BukkitThread thread, Set<R> references) {
	}

	public final void disposeCacheElements(BukkitThread thread, R reference, ThrowableRunnable callback) {
		disposeCacheElements(thread, CollectionUtils.asSet(reference), callback);
	}

	public final void disposeCacheElements(BukkitThread thread, Set<R> references, ThrowableRunnable callback) {
		if (references.isEmpty()) {
			return;
		}
		beforeDisposeCacheElements(thread, references);  // this might set some more elements to save
		// disposing means "saving if needed and then remove from cache" ; if elements don't need to be saved, remove directly from cache
		Set<R> mustPush = references.stream().filter(ref -> toSave.contains(ref)).collect(Collectors.toSet());
		Set<R> musntPush = references.stream().filter(ref -> !toSave.contains(ref)).collect(Collectors.toSet());
		removeElementsFromCache(musntPush);
		// push needed elements, then remove from cache
		pushElements(thread, mustPush, () -> {
			removeElementsFromCache(mustPush);
			if (callback != null) {
				callback.run();
			}
		});
	}

	protected void beforeDisposeCacheElements(BukkitThread thread, Set<R> references) {
	}

	// ----------------------------------------------------------------------------------------------------
	// json
	// ----------------------------------------------------------------------------------------------------

	// wrapper and file
	public abstract File getRoot();
	public abstract File getFile(K key);
	public abstract K getKey(File file);

	// init
	@Override
	protected final void remoteInitJson() throws Throwable {
		getRoot().mkdirs();
	}

	// pull
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

	protected V valueFromJson(FileReader reader) {  // this can be overriden because for complex values such as maps gson seems to be completely drunk and literally puts keys/values as raw string in the map...
		return getPlugin().getPrettyGson().fromJson(reader, valueClass);
	}

	// push
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
					getPlugin().getPrettyGson().toJson(value, valueClass, writer);
				}
			}
		}
	}

	// delete
	protected final void remoteDeleteElementsJson(Set<R> references) throws Throwable {
		// at this point, values have been removed from cache
		// this method will delete the file if no value, or update with remaining values
		remotePushElementsJson(references);
	}

	// ----------------------------------------------------------------------------------------------------
	// mysql
	// ----------------------------------------------------------------------------------------------------

	protected abstract void remotePullElementsMySQL(Set<R> references) throws Throwable;
	protected abstract void remotePushElementsMySQL(Set<R> references) throws Throwable;
	protected abstract void remoteDeleteElementsMySQL(Set<R> references) throws Throwable;

}
