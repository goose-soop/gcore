package com.guillaumevdn.gcore.lib.data;

import org.bukkit.scheduler.BukkitTask;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;
import com.guillaumevdn.gcore.lib.logging.Logger;

/**
 * @author GuillaumeVDN
 */
public abstract class Board {

	private final GPlugin<?, ?> plugin;
	private final String id;
	private final BoardType boardType;
	private final int saveDelayTicks;
	private final Logger logger;
	private BukkitTask savingTask = null;
	private boolean initialized = false;

	public Board(GPlugin plugin, String id, BoardType boardType, int saveDelayTicks) {
		this.plugin = plugin;
		this.id = id;
		this.boardType = boardType;
		this.saveDelayTicks = saveDelayTicks;
		String loggerId = "data-" + id;
		plugin.registerLogger(logger = new Logger(plugin, plugin.getName() + "-" + loggerId, plugin.getConfiguration().logDataConsole(this), plugin.getConfiguration().logDataFile(this)));
	}

	// ----- get
	public final GPlugin getPlugin() {
		return plugin;
	}

	public final String getId() {
		return id;
	}

	public final BoardType getType() {
		return boardType;
	}

	public final int getSaveDelayTicks() {
		return saveDelayTicks;
	}

	public final Logger getLogger() {
		return logger;
	}

	public final BukkitTask getSavingTask() {
		return savingTask;
	}

	private DataBackEnd lastKnownBackEnd = null;

	public final DataBackEnd getBackEnd() {
		DataBackEnd result = plugin.getConfiguration() == null ? null : plugin.getConfiguration().dataBackEnd(this);
		return result != null ? (lastKnownBackEnd = result) : lastKnownBackEnd; // on reload
	}

	public final boolean isInitialized() {
		return initialized;
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- saving
	// ----------------------------------------------------------------------------------------------------

	public final void startSaving() {
		if (getSaveDelayTicks() <= 0) return;
		getPlugin().registerTask("board_saving_" + getId(), true, getSaveDelayTicks(), () -> {
			saveNeeded(BukkitThread.ASYNC, null);
		});
	}

	public final void stopSaving() {
		if (getSaveDelayTicks() <= 0) return;
		getPlugin().stopTask("board_saving_" + getId());
	}

	public abstract void saveNeeded(BukkitThread thread, ThrowableRunnable callback);

	public abstract boolean mustSaveSomething();

	// ----------------------------------------------------------------------------------------------------
	// ----- data
	// ----------------------------------------------------------------------------------------------------

	public final void initialize(BukkitThread thread, ThrowableRunnable callback) {
		if (initialized) {
			throw new IllegalStateException("board " + getId() + " is already initialized");
		}
		operate(thread, "initialize board", () -> {
			initialized = true;
			onInitialized();
			if (boardType.equals(BoardType.LOCAL)) {
				pullAll(thread, callback);
			} else {
				if (callback != null) {
					callback.run();
				}
			}
		}, () -> {
			if (DataBackEnd.MYSQL.equals(getBackEnd())) {
				remoteInitMySQL();
			} else if (DataBackEnd.JSON.equals(getBackEnd())) {
				remoteInitJson();
			}
		});
	}

	protected void onInitialized() {
	}

	public final void pullAll(BukkitThread thread, ThrowableRunnable callback) {
		operate(thread, "pull all board", () -> {
			onPulledAll();
			if (callback != null) {
				callback.run();
			}
		}, () -> {
			if (DataBackEnd.MYSQL.equals(getBackEnd())) {
				remotePullAllMySQL();
			} else if (DataBackEnd.JSON.equals(getBackEnd())) {
				remotePullAllJson();
			}
		});
	}

	protected void onPulledAll() {
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- remote
	// ----------------------------------------------------------------------------------------------------

	protected abstract void remoteInitJson() throws Throwable;
	protected abstract void remotePullAllJson() throws Throwable;

	protected abstract void remoteInitMySQL() throws Throwable;
	protected abstract void remotePullAllMySQL() throws Throwable;

	protected final void operate(BukkitThread thread, final String operationName, final ThrowableRunnable callback, final ThrowableRunnable runner) {
		// no gcore
		if (GCore.inst() == null) {
			return;
		}
		// no data saving
		if (getBackEnd().equals(DataBackEnd.MYSQL) && (GCore.inst().getMySQLConnector() == null || !GCore.inst().getMySQLConnector().canConnect())) {
			logger.error("Couldn't operate board " + getId() + ", no MySQL connector found");
			return;
		}
		// perform
		final long start = System.currentTimeMillis();
		plugin.operate(thread, () -> {
			try {
				runner.run();
			} catch (Throwable ignored) {
				ignored.printStackTrace();
			}
			logger.info("Success : " + operationName + " (took " + (System.currentTimeMillis() - start) + " ms)");
			if (callback != null) {
				callback.run();
			}
		}, error -> {
			logger.error("Failure : " + operationName + " (after " + (System.currentTimeMillis() - start) + " ms)", error);
		});
	}

}
