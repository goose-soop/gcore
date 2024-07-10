package com.guillaumevdn.gcore.lib.legacy_npc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.data.usernpcs.BoardUsersNPCs;
import com.guillaumevdn.gcore.data.usernpcs.UserNPC;
import com.guillaumevdn.gcore.data.usernpcs.UserNPCs;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.navigator.Navigator;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.navigator.NavigatorResult;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

public class NPCManager implements Listener {

	private static NPCManager instance = null;

	public static NPCManager inst() {
		return instance;
	}

	public static void ifPresent(Consumer<NPCManager> consumer) {
		if (instance != null) {
			consumer.accept(instance);
		}
	}

	// ----- base
	private Map<Integer, ElementNPC> elementsNpcs = new HashMap<>();
	private Map<Player, Map<Integer, NPC>> npcs = new HashMap<>();
	private List<Navigator> navigators = new ArrayList<>();
	private List<BukkitTask> tasks = new ArrayList<>();
	private NPCPacketListener packetListener = null;

	public NPCManager() {
		instance = this;
	}

	// ----- get
	public Map<Integer, ElementNPC> getNPCsConfig() {
		return elementsNpcs;
	}

	public ElementNPC getNpcConfig(Integer id) {
		return elementsNpcs.get(id);
	}

	public NPC getNpc(Player player, int id) {
		Map<Integer, NPC> playerNpcs = npcs.get(player);
		return playerNpcs != null ? playerNpcs.get(id) : null;
	}

	public Map<Player, Map<Integer, NPC>> getNpcs() {
		return Collections.unmodifiableMap(npcs);
	}

	public Collection<NPC> getNpcs(Player player) {
		return Collections.unmodifiableCollection(npcs.get(player).values());
	}

	public List<Navigator> getNavigators() {
		return Collections.unmodifiableList(navigators);
	}

	public List<Navigator> getNavigators(NPC npc) {
		List<Navigator> result = new ArrayList<Navigator>();
		for (Navigator navigator : navigators) {
			if (navigator.getNpcs().contains(npc)) {
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

	// ----- methods
	public void addNpc(Player player, NPC npc) {
		Map<Integer, NPC> playerNpcs = npcs.get(player);
		if (playerNpcs == null)
			npcs.put(player, playerNpcs = new HashMap<>(elementsNpcs.size()));
		playerNpcs.put(npc.getId(), npc);
		npc.spawn();
	}

	public void removeNpc(Player player, NPC npc) {
		removeNpcs(player, CollectionUtils.asList(npc));
	}

	public void removeNpcs(Player player) {
		Map<Integer, NPC> playerNpcs = npcs.remove(player);
		if (playerNpcs != null) {
			removeNpcs(player, CollectionUtils.asList(playerNpcs.values()));
		}
	}

	public void removeNpcs(Player player, Collection<NPC> toDespawn) {
		Map<Integer, NPC> playerNpcs = npcs.get(player);
		if (playerNpcs != null) {
			for (NPC npc : toDespawn) {
				playerNpcs.remove(npc.getId());
				npc.despawn();
			}
			if (playerNpcs.isEmpty()) {
				npcs.remove(player);
			}
		}
	}

	public boolean spawnNpc(Player player, int id) {
		// already spawned : attempt to spawn it again, we never know :think: timmy said it bugged sometimes
		NPC npc = getNpc(player, id);
		if (npc != null) {
			return npc.spawn();
		}
		// not shown
		UserNPCs user = UserNPCs.get(player);
		UserNPC userNpc = user.getNPC(id);
		Replacer replacer = Replacer.justPlayer(player);
		if (!userNpc.getShown(replacer)) {
			return false;
		}
		// build npc data
		ItemStack[] items = new ItemStack[6];
		for (int i = 0; i < 6; ++i) {
			if (i == 0)
				items[i] = userNpc.getHeldItem(replacer);
			else if (i == 1)
				items[i] = userNpc.getHeldItemOff(replacer);
			else if (i == 2)
				items[i] = userNpc.getBoots(replacer);
			else if (i == 3)
				items[i] = userNpc.getLeggings(replacer);
			else if (i == 4)
				items[i] = userNpc.getChestplate(replacer);
			else if (i == 5)
				items[i] = userNpc.getHelmet(replacer);
		}
		// invalid name or location
		if (userNpc.getName(replacer) == null) {
			GCore.inst().getMainLogger().error("Couldn't spawn npc " + id + " for player " + player.getName() + " : invalid name");
			return false;
		} else if (userNpc.getLocation(replacer) == null) {
			GCore.inst().getMainLogger().error("Couldn't spawn npc " + id + " for player " + player.getName() + " : invalid location");
			return false;
		}
		// create npc and spawn it
		addNpc(player, new NPC(player, id, userNpc.getName(replacer), userNpc.getSkinData(replacer), userNpc.getSkinSignature(replacer),
				userNpc.getLocation(replacer), userNpc.getTargetDistance(replacer), userNpc.getStatus(replacer), items));
		// spawn
		return true;
	}

	public boolean spawnNpc(Player player, int id, String name, String skinData, String skinSignature, Location location, double targetDistance,
			List<NPCStatus> status, ItemStack[] items) {
		// already spawned
		if (getNpc(player, id) != null) {
			return false;
		}
		// invalid name or location
		if (name == null) {
			GCore.inst().getMainLogger().error("Couldn't spawn npc " + id + " for player " + player.getName() + " : invalid name");
			return false;
		} else if (location == null) {
			GCore.inst().getMainLogger().error("Couldn't spawn npc " + id + " for player " + player.getName() + " : invalid location");
			return false;
		}
		// create npc and spawn it
		addNpc(player, new NPC(player, id, name, skinData, skinSignature, location, targetDistance, status, items));
		// spawn
		return true;
	}

	// ----- enable/disable/reload
	public void enable() throws Throwable {
		reload();
		// start update task
		tasks.add(new BukkitRunnable() {

			@Override
			public void run() {
				try {
					for (Player player : npcs.keySet()) {
						for (NPC npc : npcs.get(player).values()) {
							npc.update();
						}
					}
				} catch (ConcurrentModificationException ignored) {
				} // BG #505, only on reconnect so don't care
			}

		}.runTaskTimer(GCore.inst(), 100L, 2L));
		// listeners
		Bukkit.getPluginManager().registerEvents(this, GCore.inst());
		com.comphenix.protocol.ProtocolLibrary.getProtocolManager().addPacketListener(packetListener = new NPCPacketListener());
	}

	public void disable() {
		// stop tasks
		for (BukkitTask task : tasks) {
			task.cancel();
		}
		// cancel navigations
		for (Navigator navigator : navigators) {
			navigator.end(NavigatorResult.CANCEL);
		}
		navigators.clear();
		// despawn npcs
		for (Player player : npcs.keySet()) {
			for (NPC npc : npcs.get(player).values()) {
				npc.despawn();
			}
		}
		npcs.clear();
		// listeners
		HandlerList.unregisterAll(this);
		if (packetListener != null) {
			com.comphenix.protocol.ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
			packetListener = null;
		}
	}

	public void reload() throws Throwable {
		// despawn existing NPCs
		npcs.forEach((player, npcs) -> npcs.forEach((__, npc) -> npc.despawn()));
		// load npcs
		elementsNpcs.clear();
		YMLConfiguration config = GCore.inst().loadConfigurationFile("npcs.yml");
		for (int id : config.readNumberKeysForSection("npcs", null)) {
			ElementNPC npc = new ElementNPC("" + id);
			npc.read();
			elementsNpcs.put(id, npc);
		}
		// respawn existing NPCs
		npcs.forEach((player, npcs) -> npcs.forEach((__, npc) -> npc.spawn()));
	}

	// ----- events
	@EventHandler(priority = EventPriority.HIGHEST)
	public void event(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		// despawn and remove npcs
		removeNpcs(player);
		// dispose board element
		BoardUsersNPCs.inst().disposeCacheElements(BukkitThread.ASYNC, CollectionUtils.asSet(player.getUniqueId()), null);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void event(EntityDamageEvent event) {
		Player player = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
		if (player != null && event.getFinalDamage() >= player.getHealth()) {
			Map<Integer, NPC> playerNpcs = npcs.get(event.getEntity());
			if (playerNpcs != null) {
				for (NPC npc : playerNpcs.values()) {
					npc.despawn();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void event(PlayerDeathEvent event) {
		Map<Integer, NPC> playerNpcs = npcs.get(event.getEntity());
		if (playerNpcs != null) {
			for (NPC npc : playerNpcs.values()) {
				npc.despawn();
			}
		}
	}

}
