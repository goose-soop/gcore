package be.guillaumevdn.gcore.data;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import be.guillaumevdn.gcore.GCore;
import be.guillaumevdn.gcore.lib.data.DataManager;
import be.guillaumevdn.gcore.lib.data.DataSingletonDisk;
import be.guillaumevdn.gcore.lib.npc.SkinData;
import be.guillaumevdn.gcore.lib.util.Utils;

public class NpcSkinBoard extends DataSingletonDisk {

	// base
	private Map<UUID, SkinData> skins = new HashMap<UUID, SkinData>();

	public NpcSkinBoard() {
	}

	public Map<UUID, SkinData> getAll() {
		return Collections.unmodifiableMap(skins);
	}

	public void clearAll() {
		skins.clear();
		deleteAsync();
	}

	public SkinData get(UUID player) {
		return skins.containsKey(player) ? skins.get(player) : null;
	}

	public void set(UUID player, SkinData profile) {

		pushAsync();
	}

	// data
	@Override
	public final DataManager getDataManager() {
		return GCore.inst().getData();
	}

	private final static class JsonData {
		private final Map<UUID, SkinData> skins;
		protected JsonData(Map<UUID, SkinData> skins) {
			this.skins = skins;
		}
	}

	@Override
	protected final File getJsonFile() {
		return new File(GCore.inst().getDataRootFolder() + "/npc_skins.json");
	}

	@Override
	protected final void jsonPull() {
		File file = getJsonFile();
		if (file.exists()) {
			JsonData data = Utils.loadFromGson(JsonData.class, file, true);
			this.skins.putAll(data.skins);
		}
	}

	@Override
	protected final void jsonPush() {
		Utils.saveToGson(new JsonData(skins), getJsonFile());
	}

}
