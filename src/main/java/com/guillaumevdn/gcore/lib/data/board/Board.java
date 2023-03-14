package com.guillaumevdn.gcore.lib.data.board;

import org.bukkit.scheduler.BukkitTask;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.data.DataBackEnd;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;
import com.guillaumevdn.gcore.lib.logging.Logger;
import com.guillaumevdn.gcore.libs.com.google.gson.Gson;

/**
 * @author GuillaumeVDN
 */
public abstract class Board<C extends BoardConnector> {

	private final GPlugin<?, ?> plugin;
	private final String id;
	private final BoardType boardType;
	private final int saveDelayTicks;
	private final Logger logger;
	private C connector = null;

	private BukkitTask savingTask = null;
	private boolean initialized = false;
	private boolean localPulledAll = false;

	public Board(GPlugin plugin, String id, BoardType boardType, int saveDelayTicks) {
		this.plugin = plugin;
		this.id = id;
		this.boardType = boardType;
		this.saveDelayTicks = saveDelayTicks;
		String loggerId = "data-" + id;
		plugin.registerLogger(logger = new Logger(plugin, plugin.getName() + "-" + loggerId,
				plugin.getConfiguration().logDataConsole(this),
				plugin.getConfiguration().logDataFile(this),
				plugin.getConfiguration().logDataSQL(this)
				));
	}

	// ----- connector
	protected final void operateOnConnector(ThrowableConsumer<C> operator) throws Throwable {
		if (connector != null) {
			operator.accept(connector);
		} else if (!DataBackEnd.NONE.equals(getBackEnd())) {
			getLogger().error("Tried to operate on connector but no connector found even though back-end is " + getBackEnd(), new Error());
		}
	}

	public final void initializeConnector(DataBackEnd backEnd) {
		connector = createConnector(backEnd);
	}

	protected final C createConnector(DataBackEnd backEnd) {
		if (backEnd.equals(DataBackEnd.JSON)) {
			return createConnectorJson();
		} else if (backEnd.equals(DataBackEnd.SQLITE)) {
			return createConnectorSQLite();
		} else if (backEnd.equals(DataBackEnd.MYSQL)) {
			return createConnectorMySQL();
		}
		return null;
	}

	protected abstract C createConnectorJson();
	protected abstract C createConnectorMySQL();
	protected abstract C createConnectorSQLite();

	// ----- get
	public final GPlugin getPlugin() {
		return plugin;
	}

	public Gson getPluginGson() {
		return plugin.getPrettyGson();
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
		return result != null ? (lastKnownBackEnd = result) : lastKnownBackEnd;  // useful on reload
	}

	public final boolean isInitialized() {
		return initialized;
	}

	public final boolean didPullAllLocal() {
		return localPulledAll;
	}

	// ----------------------------------------------------------------------------------------------------
	// 		 saving
	// ----------------------------------------------------------------------------------------------------

	public final void startSaving() {
		if (getSaveDelayTicks() <= 0) return;
		getPlugin().registerTask("board_saving_" + getId(), true, getSaveDelayTicks(), () -> {
			saveNeeded(BukkitThread.ASYNC);
		});
	}

	public final void stopSaving() {
		if (getSaveDelayTicks() <= 0) return;
		getPlugin().stopTask("board_saving_" + getId());
	}

	public void saveNeeded(BukkitThread thread) {
		saveNeeded(thread, null);
	}
	public void saveNeeded(BukkitThread thread, ThrowableRunnable callback) {
		saveNeeded(thread);  // by default, use saveNeeded to avoid breaking API change
	}

	public abstract boolean mustSaveSomething();

	// ----------------------------------------------------------------------------------------------------
	// 		 data
	// ----------------------------------------------------------------------------------------------------

	public final void initialize(BukkitThread thread, ThrowableRunnable callback) {
		if (initialized)
			throw new IllegalStateException("board " + getId() + " is already initialized");
		if (!DataBackEnd.NONE.equals(getBackEnd()) && connector == null)
			throw new IllegalStateException("board " + getId() + " must have a connector before being initialized with back-end " + getBackEnd());
		operate(thread, "initialize board", () -> {
			initialized = true;
			onInitialized();
			if (boardType.equals(BoardType.LOCAL)) {
				pullAll(thread, () -> {
					localPulledAll = true;
					if (callback != null) callback.run();
				});
			} else {
				if (callback != null) callback.run();
			}
		}, () -> {
			operateOnConnector(BoardConnector::remoteInit);
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
			operateOnConnector(BoardConnector::remotePullAll);
		});
	}

	protected void onPulledAll() {
	}

	public final void shutdown() {
		if (connector != null) {
			connector.shutdown();
			connector = null;
		}
		onShutdown();
	}

	protected void onShutdown() {
	}

	// ----------------------------------------------------------------------------------------------------
	// 		 remote
	// ----------------------------------------------------------------------------------------------------

	protected final void operate(BukkitThread thread, final String operationName, final ThrowableRunnable callback, final ThrowableRunnable runner) {
		// no gcore
		if (GCore.inst() == null) {
			return;
		}

		// perform
		final Error origin = new Error("An error occured while performing operation");
		final long start = System.currentTimeMillis();
		plugin.operate(thread, () -> {
			try {
				runner.run();
				logger.info("Success : " + operationName + " (took " + (System.currentTimeMillis() - start) + " ms)");
			} catch (Throwable error) {
				origin.initCause(error);
				logger.error("Failure : " + operationName + " (after " + (System.currentTimeMillis() - start) + " ms)", origin);
			}
			if (callback != null) {
				callback.run();
			}
		}, error -> {
			origin.initCause(error);
			logger.error("Failure : " + operationName + " (after " + (System.currentTimeMillis() - start) + " ms)", origin);
		});
	}

}
