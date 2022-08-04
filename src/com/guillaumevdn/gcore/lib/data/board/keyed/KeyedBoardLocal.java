package com.guillaumevdn.gcore.lib.data.board.keyed;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.data.board.BoardType;

/**
 * @author GuillaumeVDN
 */
public abstract class KeyedBoardLocal<K, V> extends KeyedBoard<K, V> {

	public KeyedBoardLocal(GPlugin plugin, String id, Class<V> valueClass, int saveDelayTicks) {
		super(plugin, id, BoardType.LOCAL, valueClass, saveDelayTicks);
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- get
	// ----------------------------------------------------------------------------------------------------

	public final void forPresentValue(K key, Consumer<V> ifValue) {
		forValue(key, ifValue, null);
	}

	public final void forAbsentValue(K key, Runnable ifAbsent) {
		forValue(key, null, ifAbsent);
	}

	public final void forValue(K key, Consumer<V> ifValue, Runnable ifAbsent) {
		V value = getCachedValue(key);
		if (value == null) {
			if (ifAbsent != null) ifAbsent.run();
		} else {
			if (ifValue != null) ifValue.accept(value);
		}
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- set
	// ----------------------------------------------------------------------------------------------------

	public final V putValue(K key, V value) {
		V old = putInCache(key, value);
		addCachedToSave(key);
		onValuePut(key, value);
		return old;
	}

	protected void onValuePut(K key, V value) {
	}

	public final V computeValue(K key, BiFunction<K, V, V> mappingFunction) {
		V value = mappingFunction.apply(key, getCachedValue(key));
		putValue(key, value);
		return value;
	}

	public final V computeValueIfAbsent(K key, Function<K, V> mappingFunction) {
		V value = getCachedValue(key);
		return value != null ? value : computeValue(key, (k, old) -> mappingFunction.apply(k));
	}

	public final V computeValueIfPresent(K key, Function<K, V> mappingFunction) {
		V value = getCachedValue(key);
		return value == null ? null : computeValue(key, (k, old) -> mappingFunction.apply(k));
	}

}
