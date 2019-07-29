package com.guillaumevdn.gcore.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.data.DataManager;
import com.guillaumevdn.gcore.lib.util.Utils;

public class GDataManager extends DataManager implements Listener {

	// base
	private DataProfileBoard dataProfiles = null;
	private StatisticsBoard statistics = null;
	private UserBoard userBoard = null;

	public GDataManager(BackEnd backend) {
		super(GCore.inst(), backend);
	}

	// get
	public DataProfileBoard getDataProfiles() {
		return dataProfiles;
	}

	public StatisticsBoard getStatistics() {
		return statistics;
	}

	public UserBoard getUsers() {
		return userBoard;
	}

	// methods
	@Override
	protected void innerEnable() {
		// data profiles
		dataProfiles = new DataProfileBoard();
		dataProfiles.initAsync(new Callback() {
			@Override
			public void callback() {
				dataProfiles.pullAsync(new Callback() {
					@Override
					public void callback() {
						// ensure everyone has a profile
						int created = 0;
						boolean json = getBackEnd().equals(BackEnd.JSON);
						for (Player player : Utils.getOnlinePlayers()) {
							if (dataProfiles.createDefaultProfile(player.getUniqueId(), !json)) {
								++created;
							}
						}
						if (created != 0) {
							// log
							if (GCore.inst().getConfiguration() == null ? false : GCore.inst().getConfiguration().getBoolean("show_data_debug", true)) {
								GCore.inst().debug("Created " + created + " new default player data profile" + Utils.getPlural(created));
							}
							// push data if json
							if (json) {
								dataProfiles.pushAsync();
							}
						}
					}
				});
			}});
		// statistics
		statistics = new StatisticsBoard();
		statistics.initAsync(new Callback() {
			@Override
			public void callback() {
				statistics.pullAsync();
			}});
		// users
		Bukkit.getPluginManager().registerEvents(userBoard = new UserBoard(), getPlugin());
		userBoard.initAsync(new Callback() {
			@Override
			public void callback() {
				userBoard.pullOnline();
			}
		});
	}

	@Override
	protected void innerSynchronize() {
		dataProfiles.pullAsync();
		statistics.pullAsync();
		userBoard.pullOnline();
	}

	@Override
	protected void innerReset() {
		dataProfiles.clearAll();
		statistics.clearAll();
		userBoard.deleteAsync();
	}

	@Override
	protected void innerDisable() {
		dataProfiles = null;
		statistics = null;
		HandlerList.unregisterAll(userBoard);
		userBoard = null;
	}

}
