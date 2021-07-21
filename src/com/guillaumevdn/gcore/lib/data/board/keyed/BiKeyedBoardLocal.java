package com.guillaumevdn.gcore.lib.data.board.keyed;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.data.BoardType;
import com.guillaumevdn.gcore.lib.function.TriFunction;

/**
 * @author GuillaumeVDN
 */
public abstract class BiKeyedBoardLocal<K, K2, V> extends BiKeyedBoard<K, K2, V> {
	
	public BiKeyedBoardLocal(GPlugin plugin, String id, int saveDelayTicks) {
		super(plugin, id, BoardType.LOCAL, saveDelayTicks);
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- get
	// ----------------------------------------------------------------------------------------------------

	public final V getValue(K key, K2 key2) {
		RWHashMap<K2, V> values = cache.get(key);
		return values == null ? null : values.get(key2);
	}

	public final V getValueOrCreate(K key, K2 key2, BiFunction<K, K2, V> mappingFunction) {
		RWHashMap<K2, V> values = cache.computeIfAbsent(key, k -> new RWHashMap<>());
		V value = values.get(key2);
		if (value == null) {
			putValue(key, key2, value = mappingFunction.apply(key, key2));
		}
		return value;
	}

	public final void forPresentValue(K key, K2 key2, Consumer<V> ifPresent) {
		forValue(key, key2, ifPresent, null);
	}

	public final void forAbsentValue(K key, K2 key2, Runnable ifAbsent) {
		forValue(key, key2, null, ifAbsent);
	}

	public final void forValue(K key, K2 key2, Consumer<V> ifPresent, Runnable ifAbsent) {
		V value = getValue(key, key2);
		if (value == null) {
			if (ifAbsent != null) {
				ifAbsent.run();
			}
		} else if (ifPresent != null) {
			ifPresent.accept(value);
		}
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- set
	// ----------------------------------------------------------------------------------------------------

	public final V putValue(K key, K2 key2, V value) {
		if (value == null) throw new IllegalArgumentException("value can't be null");
		V old = cache.computeIfAbsent(key, s -> new RWHashMap<>()).put(key2, value);
		addCachedToSave(new BiKeyReference<>(key, key2));
		onValuePut(key, key2, value);
		return old;
	}

	public final V compute(K key, K2 key2, TriFunction<K, K2, V, V> mappingFunction) {
		return putValue(key, key2, mappingFunction.apply(key, key2, getValue(key, key2)));
	}

	public final V computeIfAbsent(K key, K2 key2, BiFunction<K, K2, V> mappingFunction) {
		V value = getValue(key, key2);
		if (value != null) {
			return value;
		}
		value = mappingFunction.apply(key, key2);
		putValue(key, key2, value);
		return value;
	}

	protected void onValuePut(K key, K2 key2, V value) {
	}

}
