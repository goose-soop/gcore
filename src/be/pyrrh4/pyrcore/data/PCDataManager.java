package be.pyrrh4.pyrcore.data;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.data.DataManager;
import be.pyrrh4.pyrcore.lib.npc.MojangsterAPI;
import be.pyrrh4.pyrcore.lib.npc.SkinData;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.versioncompat.npc.NpcProtocols;

public class PCDataManager extends DataManager implements Listener {

	// base
	private DataProfileBoard dataProfiles = null;
	private StatisticsBoard statistics = null;
	private NpcSkinBoard npcSkins = null;
	private UserBoard userBoard = null;

	public PCDataManager(BackEnd backend) {
		super(PyrCore.inst(), backend);
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
						// refresh skins
						HashMap<String, Boolean> mojangStatus = MojangsterAPI.getMojangStatus();
						if (mojangStatus.get("textures.minecraft.net") && mojangStatus.get("api.mojang.com") && mojangStatus.get("sessionserver.mojang.com") && mojangStatus.get("auth.mojang.com")) {
							for (SkinData skin : npcSkins.getAll().values()) {
								skin.refresh();
							}
						} else {
							PyrCore.inst().error("Couldn't refresh skins (Mojang servers seems to be down)");
						}
					}
				});
			}
		} catch (Throwable ignored) {}
		// users
		Bukkit.getPluginManager().registerEvents(userBoard = new UserBoard(), getPlugin());
		userBoard.pullOnline();
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
