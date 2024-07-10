package com.guillaumevdn.gcore.data.usernpcs;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.data.board.keyed.ConnectorKeyed;
import com.guillaumevdn.gcore.lib.data.board.keyed.ConnectorKeyedJson;
import com.guillaumevdn.gcore.lib.data.board.keyed.ConnectorKeyedSQL;
import com.guillaumevdn.gcore.lib.data.board.keyed.KeyedBoardRemote;
import com.guillaumevdn.gcore.lib.data.sql.MySQLHandler;
import com.guillaumevdn.gcore.lib.data.sql.SQLHandler;
import com.guillaumevdn.gcore.lib.data.sql.SQLiteHandler;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.legacy_npc.ElementNPC;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCManager;
import com.guillaumevdn.gcore.lib.legacy_npc.NpcProtocols;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.player.PlayerUtils;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;

/**
 * @author GuillaumeVDN
 */
public class BoardUsersNPCs extends KeyedBoardRemote<UUID, UserNPCs> {

	private static BoardUsersNPCs instance = null;

	public static BoardUsersNPCs inst() {
		return instance;
	}

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
			Set<UUID> keys = new HashSet<>();
			for (Player player : PlayerUtils.getOnline()) {
				keys.add(player.getUniqueId());
			}
			pullElements(BukkitThread.ASYNC, keys, null);
		}
	}

	public void createAndSpawnDefault(Player player, UserNPCs user, ElementNPC npcConfig) {
		// get npc id
		Integer npcId = NumberUtils.integerOrNull(npcConfig.getId());
		if (npcId == null)
			return;
		// add data if hasn't
		UserNPC userNpc = user.getNPC(npcId);
		if (userNpc == null) {
			user.updateNpc(npcId, new UserNPC(npcId));
		}
		// add npc if shown (shown check is made in method so just call it)
		NPCManager.inst().spawnNpc(player, npcId);
	}

	@Override
	protected void pulledElement(BukkitThread thread, UUID key, UserNPCs usr) {
		// no value ; create it
		if (usr == null) {
			putValue(key, usr = new UserNPCs(key), null, true);
		}
		UserNPCs user = usr; // pepega

		// not connected
		Player player = Bukkit.getPlayer(key);
		if (player == null) {
			return;
		}

		// create default data for each npc
		NPCManager.ifPresent(manager -> {
			for (ElementNPC npcConfig : manager.getNPCsConfig().values()) {
				createAndSpawnDefault(player, user, npcConfig);
			}
		});
	}

	@Override
	protected void beforeDisposeCacheElement(BukkitThread thread, UUID key, UserNPCs user) {
		if (user != null) {
			Player player = Bukkit.getPlayer(key);
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

	@Override
	protected ConnectorKeyed<UUID, UserNPCs> createConnectorJson() {
		return new ConnectorKeyedJson<UUID, UserNPCs>(this) {

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

		};
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- sqlite/mysql
	// ----------------------------------------------------------------------------------------------------

	private ConnectorKeyedSQL<UUID, UserNPCs> createConnectorSQL(SQLHandler handler) {
		return new ConnectorKeyedSQL<UUID, UserNPCs>(this, handler) {

			@Override
			public String keyName() {
				return "user_uuid";
			}

			@Override
			protected UUID decodeKey(String raw) {
				return UUID.fromString(raw); // row can't contain an invalid UUID, since the query was built from a valid UUID object
			}

			@Override
			protected UserNPCs decodeValue(String jsonData) {
				return GCore.inst().getGson().fromJson(jsonData, UserNPCs.class);
			}

			@Override
			protected String encodeValue(UserNPCs value) {
				return GCore.inst().getGson().toJson(value);
			}

		};
	}

	@Override
	protected ConnectorKeyed<UUID, UserNPCs> createConnectorMySQL() {
		return createConnectorSQL(new MySQLHandler(ConfigGCore.createMySQLConnector(getPlugin())));
	}

	@Override
	protected ConnectorKeyed<UUID, UserNPCs> createConnectorSQLite() {
		return createConnectorSQL(new SQLiteHandler(getPlugin(), GCore.inst().getDataFile("data_v8/users_npcs.sqlite.db")));
	}

}
