package com.guillaumevdn.gcore;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.lib.UpdateCheck;
import com.guillaumevdn.gcore.lib.event.PlayerBlockDropEvent;
import com.guillaumevdn.gcore.lib.event.PlayerCowMilkEvent;
import com.guillaumevdn.gcore.lib.event.PlayerCraftedItemsEvent;
import com.guillaumevdn.gcore.lib.event.PlayerFireBlockEvent;
import com.guillaumevdn.gcore.lib.event.PlayerKillEvent;
import com.guillaumevdn.gcore.lib.event.PlayerSpawnedMobEvent;
import com.guillaumevdn.gcore.lib.event.PlayerTradedVillagerEvent;
import com.guillaumevdn.gcore.lib.gui.InventoryState;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.util.BucketType;
import com.guillaumevdn.gcore.lib.util.Pair;
import com.guillaumevdn.gcore.lib.util.SpawnEggUtils;
import com.guillaumevdn.gcore.lib.util.Utils;

public class Listeners implements Listener {

	private Map<Player, Block> interactedBlocks = new HashMap<Player, Block>();
	private Map<EntityType, Player> lastInteractedEggs = new HashMap<EntityType, Player>();
	private long lastBreakEvent = 0L;
	private BlockBreakEvent lastBreakBlock = null;
	private Mat lastBreakBlockType = null;
	private Map<Player, Cow> interactedCows = new HashMap<Player, Cow>();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void event(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Cow) {
			interactedCows.put(event.getPlayer(), (Cow) event.getRightClicked());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void event(PlayerBucketFillEvent event) {
		BucketType blockType = BucketType.get(event.getBlockClicked());
		if (blockType == null) {// milk bucket
			// get entity
			Cow cow = interactedCows.remove(event.getPlayer());
			if (cow != null) {
				PlayerCowMilkEvent ev = new PlayerCowMilkEvent(event.getPlayer(), cow);
				Bukkit.getPluginManager().callEvent(ev);
				event.setCancelled(ev.isCancelled());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PlayerInteractEvent event) {
		// location input
		Player player = event.getPlayer();
		if (event.getAction().toString().contains("CLICK_BLOCK") && GCore.inst().getLocationInputs().containsKey(player)) {
			GCore.inst().getLocationInputs().remove(player).onChoose(player, event.getClickedBlock().getLocation());
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void eventMonitor(PlayerInteractEvent event) {
		// clicked block
		if (event.getClickedBlock() != null) {
			Player player = event.getPlayer();
			interactedBlocks.put(player, event.getClickedBlock());
			// ... with creature egg
			if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getItem() != null) {
				try {
					lastInteractedEggs.put(SpawnEggUtils.getSpawnedType(event.getItem()), player);
				} catch (Throwable ignored) {
				} // unsupported by server
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void event(CreatureSpawnEvent event) {
		if (event.getSpawnReason().toString().contains("EGG")) {
			Player player = lastInteractedEggs.remove(event.getEntity().getType());
			if (player != null) {
				Bukkit.getPluginManager().callEvent(new PlayerSpawnedMobEvent(player, event.getEntity()));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(BlockIgniteEvent event) {
		Player player = event.getPlayer();
		if (interactedBlocks.containsKey(player)) {
			PlayerFireBlockEvent newEvent = new PlayerFireBlockEvent(player, interactedBlocks.remove(player),
					event.getBlock(), event.getCause());
			Bukkit.getPluginManager().callEvent(newEvent);
			if (newEvent.isCancelled()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (player.getKiller() != null) {
			Bukkit.getPluginManager().callEvent(new PlayerKillEvent(player.getKiller(), player));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(BlockBreakEvent event) {
		lastBreakEvent = System.currentTimeMillis();
		lastBreakBlockType = Mat.from(event.getBlock());
		lastBreakBlock = event;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(ItemSpawnEvent event) {
		if (System.currentTimeMillis() - lastBreakEvent < 50L) {
			PlayerBlockDropEvent newEvent = new PlayerBlockDropEvent(lastBreakBlock.getPlayer(), event.getEntity(),
					lastBreakBlock, lastBreakBlockType);
			Bukkit.getPluginManager().callEvent(newEvent);
			event.setCancelled(newEvent.isCancelled());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PlayerDropItemEvent event) {
		// item input
		if (GCore.inst().getItemInputs().containsKey(event.getPlayer())) {
			Player player = event.getPlayer();
			GCore.inst().getItemInputs().remove(event.getPlayer()).onChoose(player, event.getItemDrop().getItemStack());
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PlayerToggleSneakEvent event) {
		// location input
		if (GCore.inst().getLocationInputs().containsKey(event.getPlayer())) {
			Player player = event.getPlayer();
			GCore.inst().getLocationInputs().remove(event.getPlayer()).onChoose(player, player.getLocation());
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(final AsyncPlayerChatEvent event) {
		// chat input
		if (GCore.inst().getChatInputs().containsKey(event.getPlayer())) {
			event.setCancelled(true);
			event.getRecipients().clear();
			// resync to continue
			new BukkitRunnable() {
				@Override
				public void run() {
					GCore.inst().getChatInputs().remove(event.getPlayer()).onChat(event.getPlayer(), Utils.format(event.getMessage()));
				}
			}.runTask(GCore.inst());
			return;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void event(PlayerJoinEvent event) {
		if (GCore.inst().updateCheck() && GPerm.GCORE_ADMIN.has(event.getPlayer())) {
			UpdateCheck.notify(Utils.asList(event.getPlayer()));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void event(final CraftItemEvent event) {
		// build initial state
		final Player player = (Player) event.getWhoClicked();
		final InventoryState initialState = new InventoryState(player, event.getView().getTopInventory(), 1, 2, 3, 4, 5, 6, 7, 8, 9);
		// delay for craft
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!event.isCancelled()) {
					// get new state and given/received
					InventoryState newState = new InventoryState(player, event.getView().getTopInventory(), 1, 2, 3, 4, 5, 6, 7, 8, 9);
					Pair<Map<Integer, ItemStack>, Map<Integer, ItemStack>> changes = initialState.getChanges(newState);
					Map<Integer, ItemStack> removed = changes.getA();
					Map<Integer, ItemStack> added = changes.getB();
					// call craft event
					if (!(removed.isEmpty() && added.isEmpty())) {
						Bukkit.getPluginManager().callEvent(new PlayerCraftedItemsEvent(event, initialState, newState, removed, added));
					}
				}
			}
		}.runTaskLater(GCore.inst(), 1L);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void event(final InventoryClickEvent event) {
		// ensure we're trading
		if (event instanceof CraftItemEvent) return;
		if (event.getView().getTopInventory() == null) return;
		if (!event.getView().getTopInventory().getType().equals(InventoryType.MERCHANT)) return;
		if (event.getRawSlot() != 2) return;
		// build initial state
		final Player player = (Player) event.getWhoClicked();
		final InventoryState initialState = new InventoryState(player, event.getView().getTopInventory(), 0, 1);
		// delay for craft
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!event.isCancelled()) {
					// get new state and given/received
					InventoryState newState = new InventoryState(player, event.getView().getTopInventory(), 0, 1);
					Pair<Map<Integer, ItemStack>, Map<Integer, ItemStack>> changes = initialState.getChanges(newState);
					Map<Integer, ItemStack> removed = changes.getA();
					Map<Integer, ItemStack> added = changes.getB();
					// call trade event
					if (!(removed.isEmpty() && added.isEmpty())) {
						Bukkit.getPluginManager().callEvent(new PlayerTradedVillagerEvent(event, initialState, newState, removed, added));
					}
				}
			}
		}.runTaskLater(GCore.inst(), 1L);
	}

}
