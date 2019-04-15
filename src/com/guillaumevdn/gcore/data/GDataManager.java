package com.guillaumevdn.gcore.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.data.DataManager;
import com.guillaumevdn.gcore.lib.npc.MojangsterAPI;
import com.guillaumevdn.gcore.lib.npc.SkinData;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.lib.versioncompat.npc.NpcProtocols;

public class GDataManager extends DataManager implements Listener {

	// base
	private DataProfileBoard dataProfiles = null;
	private StatisticsBoard statistics = null;
	private NpcSkinBoard npcSkins = null;
	private boolean mojangStatus = false, initializedNpcSkins = false;
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

	public NpcSkinBoard getNpcSkins() {
		return npcSkins;
	}

	public UserBoard getUsers() {
		return userBoard;
	}

	public boolean getMojangStatus() {
		return mojangStatus;
	}

	public boolean initializedNpcSkins() {
		return initializedNpcSkins;
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
		// skins
		try {
			if (NpcProtocols.INSTANCE != null && Utils.getPlugin("ProtocolLib") != null) {
				this.npcSkins = new NpcSkinBoard();
				npcSkins.pullAsync(new Callback() {
					@Override
					public void callback() {
						// loaded
						initializedNpcSkins = true;
						// refresh skins
						HashMap<String, Boolean> mojangStatus = MojangsterAPI.getMojangStatus();
						if (GDataManager.this.mojangStatus = (getStatus(mojangStatus, "textures.minecraft.net") && getStatus(mojangStatus, "api.mojang.com") && getStatus(mojangStatus, "sessionserver.mojang.com") && getStatus(mojangStatus, "authserver.mojang.com"))) {
							// refresh loaded skins
							for (SkinData skin : npcSkins.getAll().values()) {
								skin.refresh();
							}
							// refresh and add new ones
							for (UUID skin : GCore.inst().getNpcManager().getLoadSkinLater()) {
								new SkinData(skin).refresh();// will be put it in the npc skins board
							}
							GCore.inst().getNpcManager().getLoadSkinLater().clear();
						} else {
							GCore.inst().error("Couldn't refresh npc skins (Mojang servers seems to be down)");
						}
					}
				});
			}
		} catch (Throwable exception) {
			exception.printStackTrace();
			GCore.inst().error("An error occured while initializing npc skins board (see above)");
		}
		// users
		Bukkit.getPluginManager().registerEvents(userBoard = new UserBoard(), getPlugin());
		userBoard.pullOnline();
	}

	private boolean getStatus(Map<String, Boolean> status, String id) {
		return status.containsKey(id) && status.get(id);
	}

	@Override
	protected void innerSynchronize() {
		dataProfiles.pullAsync();
		statistics.pullAsync();
		if (npcSkins != null) npcSkins.pullAsync(null);
		userBoard.pullOnline();
	}

	@Override
	protected void innerReset() {
		dataProfiles.clearAll();
		statistics.clearAll();
		if (npcSkins != null) npcSkins.clearAll();
		userBoard.deleteAsync();
	}

	@Override
	protected void innerDisable() {
		dataProfiles = null;
		statistics = null;
		npcSkins = null;
		HandlerList.unregisterAll(userBoard);
		userBoard = null;
	}

}
