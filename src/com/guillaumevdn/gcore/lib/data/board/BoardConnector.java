package com.guillaumevdn.gcore.lib.data.board;

/**
 * @author GuillaumeVDN
 */
public interface BoardConnector {

	void remoteInit() throws Throwable;
	void remotePullAll() throws Throwable;
	void remotePushCached() throws Throwable;

	void forcePushAllCached() throws Throwable;

	default void shutdown() {
	}

}
