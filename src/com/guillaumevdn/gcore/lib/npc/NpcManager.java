package com.guillaumevdn.gcore.lib.npc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.ProtocolLibrary;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.GUserNpcData;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.event.GUserPulledEvent;
import com.guillaumevdn.gcore.lib.gui.ItemData;
import com.guillaumevdn.gcore.lib.npc.navigation.Navigator;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.util.Utils;

public class NpcManager implements Listener {

	// base : npcs
	private Map<Integer, NpcData> npcsData = new HashMap<Integer, NpcData>();
	private Map<Player, Map<Integer, Npc>> npcs = new HashMap<Player, Map<Integer, Npc>>();
	private List<Navigator> navigators = new ArrayList<Navigator>();

	// base : registration
	private List<BukkitTask> tasks = new ArrayList<BukkitTask>();
	private NpcPacketListener packetListener = null;

	// get
	public Map<Integer, NpcData> getNpcsData() {
		return npcsData;
	}

	public NpcData getNpcData(Integer id) {
		return npcsData.get(id);
	}

	public Npc getNpc(Player player, int id) {
		Map<Integer, Npc> playerNpcs = npcs.get(player);
		return playerNpcs != null ? playerNpcs.get(id) : null;
	}

	public Map<Player, Map<Integer, Npc>> getNpcs() {
		return Collections.unmodifiableMap(npcs);
	}

	public Collection<Npc> getNpcs(Player player) {
		return Collections.unmodifiableCollection(npcs.get(player).values());
	}

	public List<Navigator> getNavigators() {
		return Collections.unmodifiableList(navigators);
	}

	public List<Navigator> getNavigators(Npc npc) {
		List<Navigator> result = new ArrayList<Navigator>();
		for (Navigator navigator : navigators) {
			if (navigator.getAffected().contains(npc)) {
				result.add(navigator);
			}
		}
		return result;
	}

	public void addNavigator(Navigator navigator) {
		navigators.add(navigator);
	}

	public void removeNavigator(Navigator navigator) {
		navigators.remove(navigator);
	}

	// methods
	public void addNpc(Player player, Npc npc) {
		Map<Integer, Npc> playerNpcs = npcs.get(player);
		if (playerNpcs == null) npcs.put(player, playerNpcs = new HashMap<Integer, Npc>());
		playerNpcs.put(npc.getId(), npc);
		npc.spawn();
	}

	public void removeNpc(Player player, Npc npc) {
		removeNpcs(player, Utils.asList(npc));
	}

	public void removeNpcs(Player player) {
		Map<Integer, Npc> playerNpcs = npcs.remove(player);
		if (playerNpcs != null) {
			removeNpcs(player, Utils.asList(playerNpcs.values()));
		}
	}

	public void removeNpcs(Player player, Collection<Npc> toDespawn) {
		Map<Integer, Npc> playerNpcs = npcs.get(player);
		if (playerNpcs != null) {
			for (Npc npc : toDespawn) {
				playerNpcs.remove(npc.getId());
				npc.despawn();
			}
			if (playerNpcs.isEmpty()) {
				npcs.remove(player);
			}
		}
	}

	public boolean spawnNpc(Player player, int id, Location forcedLocation) {
		// already spawned
		if (getNpc(player, id) != null) {
			return false;
		}
		// not shown
		GUser user = GUser.get(player);
		GUserNpcData userNpc = user.getUserNpcData(id);
		if (!userNpc.isShown()) {
			return false;
		}
		// build npc data
		ItemData[] items = new ItemData[6];
		for (int i = 0; i < 6; ++i) {
			if (i == 0) items[i] = userNpc.getHeldItem();
			else if (i == 1) items[i] = userNpc.getHeldItemOff();
			else if (i == 2) items[i] = userNpc.getBoots();
			else if (i == 3) items[i] = userNpc.getLeggings();
			else if (i == 4) items[i] = userNpc.getChestplate();
			else if (i == 5) items[i] = userNpc.getHelmet();
		}
		// invalid name or location
		if (userNpc.getName() == null) {
			GCore.inst().error("Couldn't spawn npc " + id + " for player " + player.getName() + " : invalid name");
			return false;
		} else if (userNpc.getLocation() == null) {
			GCore.inst().error("Couldn't spawn npc " + id + " for player " + player.getName() + " : invalid location");
			return false;
		}
		// create npc and spawn it
		addNpc(player, new Npc(player, id, userNpc.getName(), userNpc.getSkinData(), userNpc.getSkinSignature(), userNpc.getLocation(), userNpc.getTargetDistance(), userNpc.getStatus(), items));
		// spawn
		return true;
	}

	public boolean spawnNpc(Player player, int id, String name, String skinData, String skinSignature, Location location, double targetDistance, Set<NpcStatus> status, ItemData[] items) {
		// already spawned
		if (getNpc(player, id) != null) {
			return false;
		}
		// invalid name or location
		if (name == null) {
			GCore.inst().error("Couldn't spawn npc " + id + " for player " + player.getName() + " : invalid name");
			return false;
		} else if (location == null) {
			GCore.inst().error("Couldn't spawn npc " + id + " for player " + player.getName() + " : invalid location");
			return false;
		}
		// create npc and spawn it
		addNpc(player, new Npc(player, id, name, skinData, skinSignature, location, targetDistance, status, items));
		// spawn
		return true;
	}

	/**
	 * Initialize default NPCs data for an user
	 * @param user the user
	 * @param player the connected user
	 * @return the amount of new npc data created or completed with new values
	 */
	public int createDefaultNpcData(GUser user, Player player) {
		int changed = 0;
		for (NpcData npcData : npcsData.values()) {
			// get npc id
			Integer npcId = Utils.integerOrNull(npcData.getId());
			if (npcId == null) continue;
			// add data if hasn't
			GUserNpcData userNpc = user.getUserNpcData(npcId);
			if (userNpc == null) {
				user.updateNpc(npcId, new GUserNpcData(npcId, npcData, player));
				++changed;
			}
			// ensure data is complete if has
			else {
				if (userNpc.replaceValues(npcData, player, false) > 0) {
					++changed;
				}
			}
			// add npc if shown (shown check is made in method so just call it)
			spawnNpc(player, npcId, null);
		}
		return changed;
	}

	// enable/disable/reload
	public void enable() {
		// start update task
		long delay = GCore.inst().getConfiguration().getLong("npc_update_delay", 5L);
		tasks.add(new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : npcs.keySet()) {
					for (Npc npc : npcs.get(player).values()) {
						npc.update();
					}
				}
			}
		}.runTaskTimer(GCore.inst(), 100L, delay));
		// listeners
		Bukkit.getPluginManager().registerEvents(this, GCore.inst());
		ProtocolLibrary.getProtocolManager().addPacketListener(packetListener = new NpcPacketListener());
	}

	public void disable(boolean async) {
		// stop tasks
		for (BukkitTask task : tasks) {
			task.cancel();
		}
		// cancel navigations
		for (Navigator navigator : navigators) {
			navigator.cancel();
		}
		navigators.clear();
		// despawn npcs
		for (Player player : npcs.keySet()) {
			for (Npc npc : npcs.get(player).values()) {
				npc.despawn();
			}
		}
		npcs.clear();
		// listeners
		HandlerList.unregisterAll(this);
		if (packetListener != null) {
			ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
			packetListener = null;
		}
	}

	public void reload() {
		// despawn existing NPCs
		for (Player player : npcs.keySet()) {
			for (Npc npc : npcs.get(player).values()) {
				npc.despawn();
			}
		}
		// load npcs
		npcsData.clear();
		YMLConfiguration config = new YMLConfiguration(GCore.inst(), new File(GCore.inst().getDataFolder() + "/npcs.yml"), "npcs.yml", true, true);
		for (String rawId : config.getKeysForSection("npcs", false)) {
			Integer id = Utils.integerOrNull(rawId);
			if (id == null || id < 1) {
				GCore.inst().warning("Id " + id + " for npc data is invalid, it must be a number (at least 1)");
				continue;
			}
			ConfigData data = new ConfigData(GCore.inst(), "npc " + id, config, "npcs." + id);
			final NpcData npc = new NpcData(id.toString(), null, false, -1, EditorGUI.ICON_NPC, null);
			// register npc
			npc.load(data);
			npcsData.put(id, npc);
			// log
			if (!npc.hasErrors()) {
				GCore.inst().success("Loaded npc data " + id);
			} else {
				GCore.inst().warning("Loaded npc data " + id + " but some parts failed to load");
			}
		}
		// respawn existing NPCs
		for (Player player : npcs.keySet()) {
			for (Npc npc : npcs.get(player).values()) {
				npc.spawn();
			}
		}
	}

	// events
	@EventHandler(priority = EventPriority.LOWEST)
	public void event(GUserPulledEvent event) {
		// get user and ensure he's online
		GUser user = event.getUser();
		Player player = user.getInfo().toPlayer();
		if (player == null) return;
		// ensure user has data for every npcs, and add it eventually
		if (createDefaultNpcData(user, player) > 0) {
			user.pushAsync();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void event(PlayerQuitEvent event) {
		// despawn and remove npcs
		removeNpcs(event.getPlayer());
	}

}
