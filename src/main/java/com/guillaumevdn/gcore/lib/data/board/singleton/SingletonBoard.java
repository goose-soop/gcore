package com.guillaumevdn.gcore.lib.data.board.singleton;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.data.board.Board;
import com.guillaumevdn.gcore.lib.data.board.BoardConnector;
import com.guillaumevdn.gcore.lib.data.board.BoardType;
import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;

/**
 * @author GuillaumeVDN
 */
public abstract class SingletonBoard extends Board<BoardConnector> {

	public SingletonBoard(GPlugin plugin, String id, BoardType type, int saveDelayTicks) {
		super(plugin, id, type, saveDelayTicks);
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- save
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
	public final void saveNeeded(BukkitThread thread) {
		pushAll(thread, null);
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- data
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
			operateOnConnector(c -> c.remotePushCached());
		});
	}

}
