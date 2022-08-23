package com.guillaumevdn.gcore.lib.data.board.singleton;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.guillaumevdn.gcore.lib.data.board.BoardConnector;
import com.guillaumevdn.gcore.lib.file.FileUtils;

/**
 * @author GuillaumeVDN
 */
public abstract class ConnectorSingletonJson<W> implements BoardConnector {

	private SingletonBoard board;
	private Class<W> jsonDataWrapperClass;

	public ConnectorSingletonJson(SingletonBoard board, Class<W> jsonDataWrapperClass) {
		this.board = board;
		this.jsonDataWrapperClass = jsonDataWrapperClass;
	}

	// ----- file
	public abstract File getFile();

	// ----- init
	@Override
	public void remoteInit() throws Throwable {
	}

	// ----- push
	@Override
	public final void remotePushCached() throws Throwable {
		File file = getFile();
		FileUtils.reset(file);
		try (FileWriter writer = new FileWriter(file)) {
			W wrapper = jsonDataWrapperClass.newInstance();
			wrapJsonData(wrapper);
			board.getPluginGson().toJson(wrapper, jsonDataWrapperClass, writer);
		}
	}

	protected abstract void wrapJsonData(W wrapper);

	@Override
	public final void forcePushAllCached() throws Throwable {
		remotePushCached();
	}

	// ----- pull
	@Override
	public final void remotePullAll() throws Throwable {
		File file = getFile();
		if (file.exists()) {
			try (FileReader reader = new FileReader(file)) {
				W wrapper = board.getPluginGson().fromJson(reader, jsonDataWrapperClass);
				if (wrapper != null) {
					unwrapJsonData(wrapper);
				}
			}
		}
	}

	protected abstract void unwrapJsonData(W wrapper);

}
