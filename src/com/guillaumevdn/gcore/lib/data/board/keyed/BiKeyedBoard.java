package com.guillaumevdn.gcore.lib.data.board.keyed;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.data.BoardType;
import com.guillaumevdn.gcore.lib.data.DataBackEnd;
import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;

/**
 * @author GuillaumeVDN
 */
public abstract class BiKeyedBoard<K, K2, V> extends KeyedBoard<K, Map<K2, V>, BiKeyReference<K, K2>> {

	public BiKeyedBoard(GPlugin plugin, String id, BoardType type, int saveDelayTicks) {
		super(plugin, id, type, (Class<Map<K2, V>>) new HashMap<K2, V>().getClass(), saveDelayTicks);
	}

	// ----------------------------------------------------------------------------------------------------
	// data
	// ----------------------------------------------------------------------------------------------------

	@Override
	protected abstract Map<K2, V> valueFromJson(FileReader reader);

	public final void pullKeys(BukkitThread thread, Set<KeyReference<K>> references, ThrowableRunnable callback) {
		operate(thread, "pull board keys", () -> {
			toSave.removeIf(ref -> references.contains(ref.getKey()));
			onPullKeys(references);
			if (callback != null) {
				callback.run();
			}
		}, () -> {
			if (DataBackEnd.MYSQL.equals(getBackEnd())) {
				remotePullKeysMySQL(references);
			} else if (DataBackEnd.JSON.equals(getBackEnd())) {
				remotePullKeysJson(references);
			}
		});
	}

	protected void onPullKeys(Set<KeyReference<K>> references) {
	}

	@Override
	public void removeElementsFromCache(Set<BiKeyReference<K, K2>> references) {
		references.forEach(ref -> deleteCacheElement(ref.getKey(), ref.getKey2()));
	}

	protected final V deleteCacheElement(K key, K2 key2) {
		Map<K2, V> map = cache.get(key);
		if (map != null) {
			V old = map.remove(key2);
			if (map.isEmpty()) {
				cache.remove(key);
			}
			return old;
		}
		return null;
	}


	// mysql
	// ----------------------------------------------------------------------------------------------------

	protected abstract void remotePullKeysMySQL(Set<KeyReference<K>> references) throws Throwable;

}
