package com.guillaumevdn.gcore.lib.data.board.singleton;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.data.BoardType;

/**
 * @author GuillaumeVDN
 */
public abstract class SingletonBoardLocalJson<W> extends SingletonBoard<W> {

	public SingletonBoardLocalJson(GPlugin plugin, String id, int saveDelayTicks, Class<W> jsonDataWrapperClass, boolean prettyJson) {
		super(plugin, id, BoardType.LOCAL, saveDelayTicks, jsonDataWrapperClass, prettyJson);
	}

	@Override
	protected final void remotePushAllMySQL() throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	protected final void remoteInitMySQL() throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	protected final void remotePullAllMySQL() throws Throwable {
		throw new UnsupportedOperationException();
	}

}
