package be.pyrrh4.pyrcore.data;

import java.util.HashMap;

import org.bukkit.event.Listener;

import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.data.DataManager;
import be.pyrrh4.pyrcore.lib.npc.MojangsterAPI;
import be.pyrrh4.pyrcore.lib.npc.SkinData;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.versioncompat.npc.NpcProtocols;

public class PCDataManager extends DataManager implements Listener {

	// base
	private DataProfiles dataProfiles = null;
	private Statistics statistics = null;
	private NpcSkins npcSkins = null;

	public PCDataManager(BackEnd backend) {
		super(PyrCore.inst(), backend);
	}

	// get
	public DataProfiles getDataProfiles() {
		return dataProfiles;
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public NpcSkins getNpcSkins() {
		return npcSkins;
	}

	// methods
	@Override
	protected void innerEnable() {
		// data profiles
		this.dataProfiles = new DataProfiles();
		dataProfiles.initAsync(new Callback() { @Override public void callback() {
			dataProfiles.pullAsync();
		}});
		// statistics
		this.statistics = new Statistics();
		statistics.initAsync(new Callback() { @Override public void callback() {
			statistics.pullAsync();
		}});
		// skins
		try {
			if (NpcProtocols.INSTANCE != null && Utils.getPlugin("ProtocolLib") != null) {
				this.npcSkins = new NpcSkins();
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
	}

	@Override
	protected void innerSynchronize() {
		dataProfiles.pullAsync();
		statistics.pullAsync();
		if (npcSkins != null) npcSkins.pullAsync(null);
	}

	@Override
	protected void innerReset() {
		dataProfiles.clearAll();
		statistics.clearAll();
		if (npcSkins != null) npcSkins.clearAll();
	}

	@Override
	protected void innerDisable() {
		this.dataProfiles = null;
		this.statistics = null;
		this.npcSkins = null;
	}

}
