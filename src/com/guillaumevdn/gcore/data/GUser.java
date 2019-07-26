package com.guillaumevdn.gcore.data;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.data.DataElement;
import com.guillaumevdn.gcore.lib.data.mysql.Query;
import com.guillaumevdn.gcore.lib.util.Utils;

public class GUser extends DataElement {

	// static get
	public static GUser get(Object param) {
		return GCore.inst().getData().getUsers().getElement(param);
	}

	// base
	private UserInfo user;
	private Map<Integer, GUserNpcData> npcs = new HashMap<Integer, GUserNpcData>();

	GUser(UserInfo user) {
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
	public Map<Integer, GUserNpcData> getUserNpcData() {
		return Collections.unmodifiableMap(npcs);
	}

	/**
	 * Get an user NPC data by its id
	 * @param id the npc id
	 * @return the npc data for this user
	 */
	public GUserNpcData getUserNpcData(Integer id) {
		return npcs.get(id);
	}

	/**
	 * Update the user-modified NPC
	 * @param id the npc id
	 * @param npc the npc data
	 */
	public void updateNpc(Integer id, GUserNpcData npc) {
		updateNpc(id, npc, true);
	}

	/**
	 * Update the user-modified NPC
	 * @param id the npc id
	 * @param npc the npc data
	 * @param push true if the data must be pushed
	 */
	public void updateNpc(Integer id, GUserNpcData npc, boolean push) {
		if (npc == null) throw new IllegalArgumentException("npc can't be null");
		// update
		npcs.put(id, npc);
		if (push) pushAsync();
	}

	/**
	 * Remove the user-modified NPC
	 * @param id the npc id
	 * @param push true if the data must be pushed
	 */
	public void removeNpc(Integer id, boolean push) {
		if (npcs.remove(id) != null) {
			if (push) pushAsync();
		}
	}

	// data
	@Override
	protected final UserBoard getBoard() {
		return GCore.inst().getData().getUsers();
	}

	@Override
	protected final String getDataId() {
		return user.toString();
	}

	private static final class JsonData {
		private final Map<Integer, GUserNpcData> npcs;
		private JsonData(GUser user) {
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
		JsonData data = Utils.loadFromGson(JsonData.class, file, true, GCore.GSON);
		if (data != null) {
			readJsonData(data);
		}
	}

	@Override
	protected final void jsonPush() {
		File file = getBoard().getJsonFile(this);
		Utils.saveToGson(new JsonData(this), file, GCore.GSON);
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
		JsonData data = GCore.UNPRETTY_GSON.fromJson(set.getString("data"), JsonData.class);
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
		return new Query("REPLACE INTO `" + getBoard().getMySQLTable() + "`(`id`,`data`) VALUES(?,?);", getDataId(), GCore.UNPRETTY_GSON.toJson(new JsonData(this)));
	}

	@Override
	protected final Query getMySQLDeleteQuery() {
		return new Query("DELETE FROM `" + getBoard().getMySQLTable() + "` WHERE `id`=?;", getDataId());
	}

}
