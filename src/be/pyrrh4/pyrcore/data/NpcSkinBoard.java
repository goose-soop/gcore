package be.pyrrh4.pyrcore.data;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.data.DataManager;
import be.pyrrh4.pyrcore.lib.data.DataSingletonDisk;
import be.pyrrh4.pyrcore.lib.npc.SkinData;
import be.pyrrh4.pyrcore.lib.util.Utils;

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
		return PyrCore.inst().getData();
	}

	private final static class JsonData {
		private final Map<UUID, SkinData> skins;
		protected JsonData(Map<UUID, SkinData> skins) {
			this.skins = skins;
		}
	}

	@Override
	protected final File getJsonFile() {
		return new File(PyrCore.inst().getDataRootFolder() + "/npc_skins.json");
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
