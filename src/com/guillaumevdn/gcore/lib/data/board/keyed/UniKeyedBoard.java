package com.guillaumevdn.gcore.lib.data.board.keyed;

import java.util.Set;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.data.BoardType;

/**
 * @author GuillaumeVDN
 */
public abstract class UniKeyedBoard<K, V> extends KeyedBoard<K, V, KeyReference<K>> {

	public UniKeyedBoard(GPlugin plugin, String id, BoardType type, Class<V> valueClass, int saveDelayTicks) {
		super(plugin, id, type, valueClass, saveDelayTicks);
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- data
	// ----------------------------------------------------------------------------------------------------

	@Override
	public void removeElementsFromCache(Set<KeyReference<K>> references) {
		references.forEach(ref -> cache.remove(ref.getKey()));
	}

}
