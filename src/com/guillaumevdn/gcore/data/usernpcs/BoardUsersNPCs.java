package com.guillaumevdn.gcore.data.usernpcs;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.data.Query;
import com.guillaumevdn.gcore.lib.data.board.keyed.KeyReference;
import com.guillaumevdn.gcore.lib.data.board.keyed.UniKeyedBoardRemote;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.legacy_npc.ElementNPC;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCManager;
import com.guillaumevdn.gcore.lib.legacy_npc.NpcProtocols;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.player.PlayerUtils;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class BoardUsersNPCs extends UniKeyedBoardRemote<UUID, UserNPCs> {

	private static BoardUsersNPCs instance = null;
	public static BoardUsersNPCs inst() { return instance; }

	public BoardUsersNPCs() {
		super(GCore.inst(), "gcore_users_npcs_v8", UserNPCs.class, 20 * 60);
		instance = this;
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- data
	// ----------------------------------------------------------------------------------------------------

	@Override
	protected void onInitialized() {
		pullOnline();
	}

	public void pullOnline() {
		if (Version.ATLEAST_1_9 && PluginUtils.isPluginEnabled("ProtocolLib") && NpcProtocols.inst() != null) {
			Set<KeyReference<UUID>> keys = new HashSet<>();
			for (Player player : PlayerUtils.getOnline()) {
				keys.add(new KeyReference<>(player.getUniqueId()));
			}
			pullElements(BukkitThread.ASYNC, keys, null);
		}
	}

	public void createAndSpawnDefault(Player player, UserNPCs user, ElementNPC npcConfig, Replacer replacer) {
		// get npc id
		Integer npcId = NumberUtils.integerOrNull(npcConfig.getId());
		if (npcId == null) return;
		// add data if hasn't
		UserNPC userNpc = user.getNPC(npcId);
		if (userNpc == null) {
			user.updateNpc(npcId, new UserNPC(npcId));
		}
		// add npc if shown (shown check is made in method so just call it)
		NPCManager.inst().spawnNpc(player, npcId);
	}

	@Override
	protected void pulledElement(BukkitThread thread, KeyReference<UUID> reference, UserNPCs usr) {
		// no value ; create it
		if (usr == null) {
			putValue(reference.getKey(), usr = new UserNPCs(reference.getKey()), null, true);
		}
		UserNPCs user = usr; // pepega

		// not connected
		Player player = Bukkit.getPlayer(reference.getKey());
		if (player == null) {
			return;
		}

		// create default data for each npc
		NPCManager.ifPresent(manager -> {
			Replacer replacer = Replacer.of(player);
			for (ElementNPC npcConfig : manager.getNPCsConfig().values()) {
				createAndSpawnDefault(player, user, npcConfig, replacer);
			}
		});
	}

	@Override
	protected void beforeDisposeCacheElement(BukkitThread thread, KeyReference<UUID> reference, UserNPCs user) {
		if (user != null) {
			Player player = Bukkit.getPlayer(user.getUniqueId());
			if (player != null) {
				// remove npcs
				NPCManager.ifPresent(manager -> {
					manager.removeNpcs(player);
				});
			}
		}
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- json
	// ----------------------------------------------------------------------------------------------------

	// ----- file
	@Override
	public File getRoot() {
		return GCore.inst().getDataFile("data_v8/users_npcs/");
	}

	@Override
	public File getFile(UUID key) {
		return GCore.inst().getDataFile("data_v8/users_npcs/" + key + ".json");
	}

	@Override
	public UUID getKey(File file) {
		return ObjectUtils.uuidOrNull(FileUtils.getSimpleName(file));
	}

	@Override
	protected void remotePullAllJson() throws Throwable {
		throw new UnsupportedOperationException();
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- mysql
	// ----------------------------------------------------------------------------------------------------

	// ----- init
	private final String TABLE_NAME = getId();

	@Override
	protected void remoteInitMySQL() throws Throwable {
		GCore.inst().getMySQLConnector().performUpdateQuery(getPlugin(), getLogger(),
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
						+ "user_uuid CHAR(36) NOT NULL,"
						+ "data LONGTEXT NOT NULL,"
						+ "PRIMARY KEY(user_uuid)"
						+ ") ENGINE=InnoDB DEFAULT CHARSET = 'utf8';"
				);
	}

	@Override
	protected void remotePullAllMySQL() throws Throwable {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void remotePullElementsMySQL(Set<KeyReference<UUID>> references) throws Throwable {
		GCore.inst().getMySQLConnector().performGetQuery(getPlugin(), getLogger(),
				Query.buildSelectKeysIn(TABLE_NAME, "user_uuid", references),
				set -> {
					while (set.next()) {
						UUID uuid = UUID.fromString(set.getString("user_uuid")); // row can't contain an invalid UUID, since the query above was built from a valid UUID object
						String rawData = set.getString("data");
						try {
							UserNPCs user = GCore.inst().getGson().fromJson(rawData, UserNPCs.class);
							if (user == null) {
								getLogger().warning("Found invalid user NPC data for '" + uuid + "' in database, skipped it");
								continue;
							}
							cache.put(uuid, user);
						} catch (Throwable exception) {
							exception.printStackTrace();
						}
					}
				});
	}

	@Override
	protected void remotePushElementsMySQL(Set<KeyReference<UUID>> refs) throws Throwable {
		if (refs.isEmpty()) return;
		for (Collection<? extends KeyReference<UUID>> references : CollectionUtils.splitCollection(refs, 999)) {  // multiple VALUES are limited to 1000 elements ; https://stackoverflow.com/questions/452859/inserting-multiple-rows-in-a-single-sql-query#comment22032805_452934
			Query query = Query.buildInsertOrUpdatePair(TABLE_NAME, "user_uuid", "data", references, k -> GCore.inst().getGson().toJson(getCachedValue(k.getKey())));
			GCore.inst().getMySQLConnector().performUpdateQuery(getPlugin(), getLogger(), query);
		}
	}

	@Override
	protected void remoteDeleteElementsMySQL(Set<KeyReference<UUID>> references) throws Throwable {
		if (references.isEmpty()) return;  // let's avoid deleting the whole table just because there's no WHERE clause
		Query query = Query.buildDeleteKeysIn(TABLE_NAME, "user_uuid", references);
		GCore.inst().getMySQLConnector().performUpdateQuery(getPlugin(), getLogger(), query);
	}

}
