package com.guillaumevdn.gcore.data;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.data.DataManager;

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
		dataProfiles.initAsync(new Callback() { @Override public void callback() {
			dataProfiles.pullAsync();
		}});
		// statistics
		statistics = new StatisticsBoard();
		statistics.initAsync(new Callback() { @Override public void callback() {
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
