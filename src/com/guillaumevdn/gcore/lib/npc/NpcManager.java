package com.guillaumevdn.gcore.lib.npc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import com.guillaumevdn.gcore.data.ModifiedNpcData;
import com.guillaumevdn.gcore.lib.gui.ItemData;
import com.guillaumevdn.gcore.lib.npc.navigation.Navigator;
import com.guillaumevdn.gcore.lib.util.Utils;

public class NpcManager implements Listener {

	// base
	private BukkitTask updateTask = null;
	private NpcPacketListener packetListener = null;
	private Map<Player, Map<Integer, Npc>> npcs = new HashMap<Player, Map<Integer, Npc>>();
	private List<Navigator> navigators = new ArrayList<Navigator>();
	private List<UUID> loadSkinLater = new ArrayList<UUID>();

	// get
	public Npc getNpc(Player player, int id) {
		Map<Integer, Npc> playerNpcs = npcs.get(player);
		return playerNpcs != null ? playerNpcs.get(id) : null;
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

	public List<UUID> getLoadSkinLater() {
		return loadSkinLater;
	}

	public void loadSkinLater(UUID skin) {
		if (GCore.inst().getData().initializedNpcSkins()) {
			if (GCore.inst().getData().getMojangStatus()) {
				new SkinData(skin).refresh();// will be put it in the npc skins board
			}
		} else if (!loadSkinLater.contains(skin)) {
			loadSkinLater.add(skin);
		}
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
				if (playerNpcs.remove(npc.getId()) != null) {
					npc.despawn();
				}
			}
			if (playerNpcs.isEmpty()) {
				npcs.remove(player);
			}
		}
	}

	public boolean spawnNpc(Player player, int id, NpcData npcData, Location forcedLocation) {
		// unknown npc
		if (npcData == null) {
			return false;
		}
		// already spawned
		if (getNpc(player, id) != null) {
			return false;
		}
		// not shown
		GUser user = GUser.get(player);
		ModifiedNpcData userNpcData = user.getNpc(id);
		if ((userNpcData != null && !userNpcData.isShown()) || !npcData.getShown(player)) {
			return false;
		}
		// build npc data
		String name = userNpcData != null && userNpcData.getName() != null ? userNpcData.getName() : npcData.getName(player);
		UUID skin = userNpcData != null && userNpcData.getSkin() != null ? userNpcData.getSkin() : npcData.getSkin(player);
		Location location = forcedLocation != null ? forcedLocation : (userNpcData != null && userNpcData.getLocation() != null ? userNpcData.getLocation() : npcData.getLocation(player));
		Double targetDistance = userNpcData != null && userNpcData.getTargetDistance() != null ? userNpcData.getTargetDistance() : npcData.getTargetDistance(player);
		Set<NpcStatus> status = userNpcData != null && userNpcData.getStatus() != null ? userNpcData.getStatus() : null;
		if (status == null) {
			List<NpcStatus> parsed = npcData.getStatus(player);
			if (parsed != null) status = new HashSet<NpcStatus>(parsed);
		}
		ItemData[] items = new ItemData[6];
		for (int i = 0; i < 6; ++i) {
			if (userNpcData != null && userNpcData.getItems() != null && userNpcData.getItems().length >= i) {// has user data
				items[i] = userNpcData.getItems()[i];
			} else {// default stuff
				if (i == 0) items[i] = npcData.getHeldItem(player);
				else if (i == 1) items[i] = npcData.getHeldItemOff(player);
				else if (i == 2) items[i] = npcData.getBoots(player);
				else if (i == 3) items[i] = npcData.getLeggings(player);
				else if (i == 4) items[i] = npcData.getChestplate(player);
				else if (i == 5) items[i] = npcData.getHelmet(player);
			}
		}
		// create npc and spawn it
		addNpc(player, new Npc(player, id, name, skin, location, targetDistance, status, items));
		// spawn
		return true;
	}

	// methods : enable/disable
	public void enable() {
		// start update task
		long delay = GCore.inst().getConfiguration().getLong("npc_update_delay", 5L);
		updateTask = new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : npcs.keySet()) {
					for (Npc npc : npcs.get(player).values()) {
						npc.update();
					}
				}
			}
		}.runTaskTimer(GCore.inst(), 100L, delay);
		// listeners
		Bukkit.getPluginManager().registerEvents(this, GCore.inst());
		ProtocolLibrary.getProtocolManager().addPacketListener(packetListener = new NpcPacketListener());
	}

	public void disable() {
		// stop update task
		if (updateTask != null) {
			updateTask.cancel();
			updateTask = null;
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

	// events
	@EventHandler(priority = EventPriority.HIGHEST)
	public void event(PlayerQuitEvent event) {
		// despawn and remove npcs
		removeNpcs(event.getPlayer());
	}

}
