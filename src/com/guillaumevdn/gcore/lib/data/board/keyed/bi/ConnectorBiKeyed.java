package com.guillaumevdn.gcore.lib.data.board.keyed.bi;

import java.util.Set;

import com.guillaumevdn.gcore.lib.data.board.keyed.ConnectorKeyed;
import com.guillaumevdn.gcore.lib.tuple.Pair;

public abstract class ConnectorBiKeyed<K, K2, V> extends ConnectorKeyed<Pair<K, K2>, V> {

	public ConnectorBiKeyed(BiKeyedBoard<K, K2, V> board) {
		super(board);
	}

	public abstract void remotePullElementsByPrimary(Set<K> references) throws Throwable;

}
