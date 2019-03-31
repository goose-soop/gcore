package be.pyrrh4.pyrcore.data;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.data.DataElement;
import be.pyrrh4.pyrcore.lib.data.mysql.Query;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class PCUser extends DataElement {

	// static get
	public static PCUser get(Object param) {
		return PyrCore.inst().getData().getUsers().getElement(param);
	}

	// base
	private UserInfo user;
	private Map<Integer, ModifiedNpcData> npcs = new HashMap<Integer, ModifiedNpcData>();

	PCUser(UserInfo user) {
		this.user = user;
	}

	// get
	public UserInfo getInfo() {
		return user;
	}

	/**
	 * Get all the modified values for NPCs for this user
	 * @return a map containing the npcs by id
	 */
	public Map<Integer, ModifiedNpcData> getNpcs() {
		return Collections.unmodifiableMap(npcs);
	}

	/**
	 * Get an user-modified NPC by its id
	 * @param id the npc id
	 * @return the npc if has at least one modified value for this user
	 */
	public ModifiedNpcData getNpc(Integer id) {
		return npcs.get(id);
	}

	/**
	 * Update the user-modified NPC
	 * @param id the npc id
	 * @param npc the npc data (null or no modified value = remove)
	 */
	public void updateNpc(Integer id, ModifiedNpcData npc) {
		// remove
		if (npc == null || npc.isEmpty()) {
			if (npcs.containsKey(id)) {
				npcs.remove(id);
				pushAsync();
			}
		}
		// update
		else {
			npcs.put(id, npc);
			pushAsync();
		}
	}

	// data
	@Override
	protected final UserBoard getBoard() {
		return PyrCore.inst().getData().getUsers();
	}

	@Override
	protected final String getDataId() {
		return user.toString();
	}

	private static final class JsonData {
		private final Map<Integer, ModifiedNpcData> npcs;
		private JsonData(PCUser user) {
			this.npcs = user.npcs;
		}
	}

	private void readJsonData(JsonData data) {
		// reset cache
		this.npcs.clear();
		// replace
		if (data.npcs != null) this.npcs.putAll(data.npcs);
	}

	@Override
	protected final void jsonPull() {
		File file = getBoard().getJsonFile(this);
		JsonData data = Utils.loadFromGson(JsonData.class, file, true, PyrCore.GSON);
		if (data != null) {
			readJsonData(data);
		}
	}

	@Override
	protected final void jsonPush() {
		File file = getBoard().getJsonFile(this);
		Utils.saveToGson(new JsonData(this), file, PyrCore.GSON);
	}

	@Override
	protected final void jsonDelete() {
		File file = getBoard().getJsonFile(this);
		if (file.exists()) {
			file.delete();
		}
	}

	// MySQL
	@Override
	protected final void mysqlPull(ResultSet set) throws SQLException {
		// data
		JsonData data = PyrCore.UNPRETTY_GSON.fromJson(set.getString("data"), JsonData.class);
		if (data != null) {
			readJsonData(data);
		}
	}

	@Override
	protected final Query getMySQLPullQuery() {
		return new Query("SELECT * FROM `" + getBoard().getMySQLTable() + "` WHERE `id`=?;", getDataId());
	}

	@Override
	protected final Query getMySQLPushQuery() {
		return new Query("REPLACE INTO `" + getBoard().getMySQLTable() + "`(`id`,`data`) VALUES(?,?);", getDataId(), PyrCore.UNPRETTY_GSON.toJson(new JsonData(this)));
	}

	@Override
	protected final Query getMySQLDeleteQuery() {
		return new Query("DELETE FROM `" + getBoard().getMySQLTable() + "` WHERE `id`=?;", getDataId());
	}

}
