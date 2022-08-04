package com.guillaumevdn.gcore.lib.data.board.keyed;

import java.util.Set;

import com.guillaumevdn.gcore.lib.data.board.BoardConnector;

/**
 * @author GuillaumeVDN
 */
public abstract class ConnectorKeyed<K, V> implements BoardConnector {

	protected final KeyedBoard<K, V> board;

	public ConnectorKeyed(KeyedBoard<K, V> board) {
		this.board = board;
	}

	// ----- pull
	public abstract void remotePullElements(Set<K> references) throws Throwable;

	// ----- push
	@Override
	public final void remotePushCached() throws Throwable {
		remotePushElements(board.copyCacheKeys());
	}

	public abstract void remotePushElements(Set<K> references) throws Throwable;

	@Override
	public void forcePushAllCached() throws Throwable {
		remotePushElements(board.copyCacheKeys());
	}

	// ----- delete
	public abstract void remoteDeleteElements(Set<K> references) throws Throwable;

}
