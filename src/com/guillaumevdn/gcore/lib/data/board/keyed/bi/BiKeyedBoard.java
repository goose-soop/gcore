package com.guillaumevdn.gcore.lib.data.board.keyed.bi;

import java.util.Set;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.data.board.BoardType;
import com.guillaumevdn.gcore.lib.data.board.keyed.KeyedBoard;
import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public abstract class BiKeyedBoard<K, K2, V> extends KeyedBoard<Pair<K, K2>, V> {

	public BiKeyedBoard(GPlugin plugin, String id, BoardType type, Class<V> valueClass, int saveDelayTicks) {
		super(plugin, id, type, valueClass, saveDelayTicks);
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- get
	// ----------------------------------------------------------------------------------------------------

	public final V getCachedValue(K key, K2 key2) {
		return getCachedValue(Pair.of(key, key2));
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- data
	// ----------------------------------------------------------------------------------------------------

	public final void pullKeys(BukkitThread thread, Set<Pair<K, K2>> references, ThrowableRunnable callback) {
		operate(thread, "pull board keys", () -> {
			onPullKeys(references);
			if (callback != null) {
				callback.run();
			}
		}, () -> {
			removeCachedToSaveIf(key -> references.contains(key));
			operateOnConnector(c -> c.remotePullElements(references));
		});
	}

	public final void pullKeysByPrimary(BukkitThread thread, Set<K> references, ThrowableRunnable callback) {
		operate(thread, "pull board keys by primary", () -> {
			onPullKeysByPrimary(references);
			if (callback != null) {
				callback.run();
			}
		}, () -> {
			removeCachedToSaveIf(key -> references.contains(key.getA()));
			operateOnConnector(c -> ((ConnectorBiKeyed<K, K2, V>) c).remotePullElementsByPrimary(references));
		});
	}

	protected void onPullKeys(Set<Pair<K, K2>> references) {
	}

	protected void onPullKeysByPrimary(Set<K> references) {
	}

	@Override
	public void removeElementsFromCache(Set<Pair<K, K2>> keys) {
		keys.forEach(key -> removeFromCache(key));
	}

	public void removeElementsFromCacheByPrimary(Set<K> keys) {
		iterateAndModifyCache((ref, value, iter) -> {
			if (keys.contains(ref.getA())) {
				iter.remove();
			}
		});
	}

}
