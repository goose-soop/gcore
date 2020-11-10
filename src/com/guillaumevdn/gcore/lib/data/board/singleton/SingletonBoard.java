package com.guillaumevdn.gcore.lib.data.board.singleton;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.data.Board;
import com.guillaumevdn.gcore.lib.data.BoardType;
import com.guillaumevdn.gcore.lib.data.DataBackEnd;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;

/**
 * @author GuillaumeVDN
 */
public abstract class SingletonBoard<W> extends Board {

	private Class<W> jsonDataWrapperClass;
	private boolean prettyJson;

	public SingletonBoard(GPlugin plugin, String id, BoardType type, int saveDelayTicks, Class<W> jsonDataWrapperClass, boolean prettyJson) {
		super(plugin, id, type, saveDelayTicks);
		this.jsonDataWrapperClass = jsonDataWrapperClass;
		this.prettyJson = prettyJson;
	}

	// ----------------------------------------------------------------------------------------------------
	// save
	// ----------------------------------------------------------------------------------------------------

	private transient boolean toSave = false;

	public final void setToSave() {
		this.toSave = true;
	}

	@Override
	public boolean mustSaveSomething() {
		return toSave;
	}

	@Override
	public final void saveNeeded(BukkitThread thread, ThrowableRunnable callback) {
		pushAll(thread, callback);
	}

	// ----------------------------------------------------------------------------------------------------
	// data
	// ----------------------------------------------------------------------------------------------------

	public final void pushAll(BukkitThread thread, ThrowableRunnable callback) {
		if (!toSave) {
			return;
		}
		operate(thread, "push all board", () -> {
			toSave = false;
			if (callback != null) {
				callback.run();
			}
		}, () -> {
			if (DataBackEnd.MYSQL.equals(getBackEnd())) {
				remotePushAllMySQL();
			} else if (DataBackEnd.JSON.equals(getBackEnd())) {
				remotePushAllJson();
			}
		});
	}

	// ----------------------------------------------------------------------------------------------------
	// json
	// ----------------------------------------------------------------------------------------------------

	// file
	public abstract File getFile();

	// init
	@Override
	protected void remoteInitJson() throws Throwable {
	}

	// push
	protected void remotePushAllJson() throws Throwable {
		File file = getFile();
		FileUtils.reset(file);
		try (FileWriter writer = new FileWriter(file)) {
			W wrapper = jsonDataWrapperClass.newInstance();
			wrapJsonData(wrapper);
			(prettyJson ? getPlugin().getPrettyGson() : getPlugin().getGson()).toJson(wrapper, jsonDataWrapperClass, writer);
		}
	}

	protected abstract void wrapJsonData(W wrapper);

	// pull
	@Override
	protected void remotePullAllJson() throws Throwable {
		File file = getFile();
		if (file.exists()) {
			try (FileReader reader = new FileReader(file)) {
				W wrapper = (prettyJson ? getPlugin().getPrettyGson() : getPlugin().getGson()).fromJson(reader, jsonDataWrapperClass);
				if (wrapper != null) {
					unwrapJsonData(wrapper);
				}
			}
		}
	}

	protected abstract void unwrapJsonData(W wrapper);

	// ----------------------------------------------------------------------------------------------------
	// mysql
	// ----------------------------------------------------------------------------------------------------

	protected abstract void remotePushAllMySQL() throws Throwable;

}
