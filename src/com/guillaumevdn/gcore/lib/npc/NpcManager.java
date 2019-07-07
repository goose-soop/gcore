package com.guillaumevdn.gcore.lib.npc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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
import com.guillaumevdn.gcore.lib.event.NpcAttackEvent;
import com.guillaumevdn.gcore.lib.gui.ItemData;
import com.guillaumevdn.gcore.lib.npc.behavior.Behavior;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BActionType;
import com.guillaumevdn.gcore.lib.npc.behavior.condition.BConditionType;
import com.guillaumevdn.gcore.lib.npc.behavior.event.BEvent.AttemptContext;
import com.guillaumevdn.gcore.lib.npc.behavior.event.BEventType;
import com.guillaumevdn.gcore.lib.npc.navigation.Navigator;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.util.Utils;

public class NpcManager implements Listener {

	// base : behavior
	private Map<String, BEventType> behaviorEventTypes = new HashMap<String, BEventType>();
	private Map<String, BActionType> behaviorActionTypes = new HashMap<String, BActionType>();
	private Map<String, BConditionType> behaviorConditionTypes = new HashMap<String, BConditionType>();
	private Map<String, Behavior> behaviors = new HashMap<String, Behavior>();
	private Set<GUser> behaviorMustPushUsers = new HashSet<GUser>();

	// base : npcs
	private Map<Integer, NpcData> npcsData = new HashMap<Integer, NpcData>();
	private Map<Player, Map<Integer, Npc>> npcs = new HashMap<Player, Map<Integer, Npc>>();
	private List<Navigator> navigators = new ArrayList<Navigator>();

	// base : registration
	private List<BukkitTask> tasks = new ArrayList<BukkitTask>();
	private NpcPacketListener packetListener = null;

	// get
	public Map<String, BEventType> getBehaviorEventTypes() {
		return behaviorEventTypes;
	}

	public Map<String, BActionType> getBehaviorActionTypes() {
		return behaviorActionTypes;
	}

	public Map<String, BConditionType> getBehaviorConditionTypes() {
		return behaviorConditionTypes;
	}

	public Map<String, Behavior> getBehaviors() {
		return behaviors;
	}

	public Behavior getBehavior(String id) {
		return id == null ? null : behaviors.get(id.toLowerCase());
	}

	public Set<GUser> getBehaviorMustPushUsers() {
		return behaviorMustPushUsers;
	}

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
				if (playerNpcs.remove(npc.getId()) != null) {
					npc.despawn();
				}
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
		} else if (!userNpc.getLocation().getWorld().equals(player.getWorld())) {
			GCore.inst().error("Couldn't spawn npc " + id + " for player " + player.getName() + " : not the same world");
			return false;
		}
		// create npc and spawn it
		addNpc(player, new Npc(player, id, userNpc.getName(), userNpc.getSkinData(), userNpc.getSkinSignature(), userNpc.getLocation(), userNpc.getTargetDistance(), userNpc.getStatus(), items));
		// spawn
		return true;
	}

	public void runBehaviorsAttempt(AttemptContext context, Player player, Event event) {
		// check npcs
		for (Npc npc : GCore.inst().getNpcManager().getNpcs(player)) {
			if (!npc.isSpawned()) continue;
			runBehaviorsAttempt(context, player, npc, event);
		}
	}

	public void runBehaviorsAttempt(AttemptContext context, Player player, Npc npc, Event event) {
		// no data
		NpcData npcData = npc.getData();
		if (npcData == null) return;
		// no behaviors
		List<String> behaviors = npcData.getBehaviors(player);
		if (behaviors == null) return;
		// check behaviors
		for (String behaviorId : behaviors) {
			Behavior behavior = getBehavior(behaviorId);
			if (behavior == null) {
				GCore.inst().error("Attempting to run unknown behavior " + behaviorId + " for npc " + npc.getId() + " (player " + player.getName() + "), skipping it");
			} else {
				behavior.runAttempt(context, player, npc, event);
			}
		}
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
		// start behavior tasks
		/* FIXME uncomment
		tasks.add(new BehaviorProgressTask().runTaskTimer(GCore.inst(), 100L, 1L));
		tasks.add(new BehaviorSaveUserTask().runTaskTimer(GCore.inst(), 100L, 20L * 5L));
		tasks.add(new BehaviorTimerEventTask().runTaskTimer(GCore.inst(), 100L, 20L));*/
		// listeners
		Bukkit.getPluginManager().registerEvents(this, GCore.inst());
		ProtocolLibrary.getProtocolManager().addPacketListener(packetListener = new NpcPacketListener());
	}

	public void disable(boolean async) {
		// push users
		GCore.inst().getData().getUsers().push(async, Utils.asList(behaviorMustPushUsers));
		behaviorMustPushUsers.clear();
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
		/* FIXME uncomment
		// load behaviors
		behaviors.clear();
		loadBehaviors(new File(GCore.inst().getDataFolder() + "/npcs/behaviors/"));*/
		// respawn existing NPCs
		for (Player player : npcs.keySet()) {
			for (Npc npc : npcs.get(player).values()) {
				npc.spawn();
			}
		}
	}

	public void loadBehaviors(File file) {
		if (file == null) return;
		// directory
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				loadBehaviors(f);
			}
		}
		// quest file
		else {
			if (file.exists() && file.isFile() && file.getName().toLowerCase().endsWith(".yml")) {
				try {
					// get id and config
					String id = Utils.getFileNameWithoutExtension(file).toLowerCase();
					YMLConfiguration config = new YMLConfiguration(GCore.inst(), file, null, false, true);
					// load
					Behavior behavior = new Behavior(id, file, null, false, -1, EditorGUI.ICON_TECHNICAL, null);
					ConfigData data = new ConfigData(GCore.inst(), "npc behavior " + id, config, "");
					behavior.load(data);
					behaviors.put(id, behavior);
					// log
					if (!behavior.hasErrors()) {
						GCore.inst().success("Loaded npc behavior " + id);
					} else {
						GCore.inst().warning("Loaded npc behavior " + id + " but some parts failed to load");
					}
				} catch (Throwable exception) {
					GCore.inst().error("Could not load npc behavior from file " + file.getPath() + " :");
					exception.printStackTrace();
				}
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
		boolean push = false;
		for (NpcData npcData : npcsData.values()) {
			// get npc id
			Integer npcId = Utils.integerOrNull(npcData.getId());
			if (npcId == null) continue;
			// add data if hasn't
			GUserNpcData userNpc = user.getUserNpcData(npcId);
			if (userNpc == null) {
				user.updateNpc(npcId, new GUserNpcData(npcId, npcData, player));
				push = true;
			}
			// ensure data is complete if has
			else {
				if (userNpc.replaceNullValues(npcData, player) > 0) {
					push = true;
				}
			}
			// add npc if shown (check made in submethod)
			spawnNpc(player, npcId, null);
		}
		// push data
		if (push) {
			user.pushAsync();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void event(PlayerQuitEvent event) {
		// despawn and remove npcs
		removeNpcs(event.getPlayer());
		// push user data if needed
		GUser user = GUser.get(event.getPlayer());
		if (user != null && behaviorMustPushUsers.remove(user)) {
			user.pushAsync();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void event(NpcAttackEvent event) {
		GCore.inst().getNpcManager().runBehaviorsAttempt(AttemptContext.EVENT, event.getPlayer(), event.getNpc(), event);
	}

}
