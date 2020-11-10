package com.guillaumevdn.gcore.lib.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.gui.InventoryState;
import com.guillaumevdn.gcore.lib.gui.PlayerInventoryState;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public final class CustomEventsListeners implements Listener {

	private final Object __VALUE = new Object();

	// block fire
	private WeakHashMap<Player, Block> ignitedBlocks = new WeakHashMap<>();
	private WeakHashMap<Player, Object> ignitedBlocksNotSide = new WeakHashMap<>();
	private final List<String> CAN_IGNITE_FROM_SIDES = CollectionUtils.asList("WOOD", "LOG", "STAIRS", "SLAB");  // FIXME : temporary solution ; make it per-material mater with --IGNITABLE and --IGNITABLE_FROM_SIDES

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void eventIgnite(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !Mat.isVoid(event.getItem()) && event.getItem().getType().toString().equals("FLINT_AND_STEEL")) {
			if (event.getBlockFace().equals(BlockFace.UP)) {
				ignitedBlocks.put(event.getPlayer(), event.getClickedBlock());
			} else {
				String type = event.getClickedBlock().getType().toString();
				if (CAN_IGNITE_FROM_SIDES.stream().anyMatch(str -> type.contains(str))) {
					if (Mat.fromBlock(event.getClickedBlock().getRelative(event.getBlockFace()).getRelative(BlockFace.DOWN)).get().isAir()) {
						ignitedBlocks.put(event.getPlayer(), event.getClickedBlock());
					} else {
						ignitedBlocksNotSide.put(event.getPlayer(), __VALUE);  // tried to ignite block from side but fire will go on the block below
					}
				} else {
					ignitedBlocksNotSide.put(event.getPlayer(), __VALUE);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void eventIgnite(BlockIgniteEvent event) {
		Block onFire = ignitedBlocks.remove(event.getIgnitingEntity());
		if (onFire != null || ignitedBlocksNotSide.remove(event.getIgnitingEntity()) != null) {
			PlayerBlockIgniteEvent ev = new PlayerBlockIgniteEvent(event, onFire != null ? onFire : event.getBlock().getRelative(BlockFace.DOWN));
			Bukkit.getPluginManager().callEvent(ev);
			event.setCancelled(ev.isCancelled());
		}
	}

	// item fish
	private WeakHashMap<Player, Object> bitten = new WeakHashMap<>();
	// we use this to make sure the player was in BITE state (because he could just grab a random item on the ground, not in water, see #111)
	// however in 1.8 there's no BITE state so don't check it

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void event(PlayerFishEvent event) {
		Player player = event.getPlayer();
		// caught fish
		if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
			// was biting
			if (!Version.ATLEAST_1_8 || bitten.containsKey(player)) {
				Item item = ObjectUtils.castOrNull(event.getCaught(), Item.class);
				// it's an item
				if (item != null) {
					PlayerItemFishEvent ev = new PlayerItemFishEvent(event, (Item) event.getCaught());
					Bukkit.getPluginManager().callEvent(ev);
					event.setCancelled(ev.isCancelled());
				}
			}
		}
		// update bite state regardless of the result
		if (Version.ATLEAST_1_8) {
			if (event.getState().equals(PlayerFishEvent.State.BITE)) {
				bitten.put(player, __VALUE);
			} else {
				bitten.remove(player);
			}
		}
	}

	// entity damage
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void event(EntityDamageByEntityEvent event) {
		Player damagerPlayer = ObjectUtils.castOrNull(event.getDamager(), Player.class);
		Player damagedPlayer = ObjectUtils.castOrNull(event.getEntity(), Player.class);
		if (damagerPlayer != null) {
			Bukkit.getPluginManager().callEvent(new PlayerDamageEntityEvent(event, damagerPlayer, event.getEntity()));  // cancellable is backed up by original event
		}
		if (damagedPlayer != null) {
			Bukkit.getPluginManager().callEvent(new EntityDamagePlayerEvent(event, event.getDamager(), damagedPlayer));  // cancellable is backed up by original event
		}
	}

	// potion throw
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void event(PotionSplashEvent og) {
		Player player = ObjectUtils.castOrNull(og.getPotion().getShooter(), Player.class);
		if (player != null) {
			PlayerItemThrowEvent event = new PlayerItemThrowEvent(player, og.getPotion().getItem(), og.getPotion().getLocation());
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				og.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void event(ExpBottleEvent og) {
		Player player = ObjectUtils.castOrNull(og.getEntity().getShooter(), Player.class);
		if (player != null) {
			PlayerItemThrowEvent event = new PlayerItemThrowEvent(player, og.getEntity().getItem(), og.getEntity().getLocation());
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				og.setExperience(0);
				og.setShowEffect(false);
			}

		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void event(PlayerEggThrowEvent og) {
		Player player = ObjectUtils.castOrNull(og.getEgg().getShooter(), Player.class);
		if (player != null) {
			PlayerItemThrowEvent event = new PlayerItemThrowEvent(player, og.getEgg().getItem(), og.getEgg().getLocation());
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				og.setHatching(false);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void event(ProjectileHitEvent og) {
		Snowball snowball = ObjectUtils.castOrNull(og.getEntity(), Snowball.class);
		Player player = snowball == null ? null : ObjectUtils.castOrNull(snowball.getShooter(), Player.class);
		if (player != null) {
			Bukkit.getPluginManager().callEvent(new PlayerItemThrowEvent(player, Version.ATLEAST_1_13 ? snowball.getItem() : Mat.firstFromIdOrDataName("SNOWBALL").get().newStack(), snowball.getLocation()));
		}
	}

	// creature spawn
	private Map<EntityType, Player> lastInteractedEggs = new HashMap<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void event(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null && event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getItem() != null) {
			EntityType spawnedType = null;
			Mat mat = Mat.fromItem(event.getItem()).orNull();
			if (mat != null && mat.getData().getDataName().endsWith("_SPAWN_EGG")) {
				spawnedType = ObjectUtils.safeValueOf(mat.getData().getDataName().substring(0, "_SPAWN_EGG".length() - 3), EntityType.class);
			}
			if (spawnedType == null) {
				try {
					spawnedType = ((org.bukkit.inventory.meta.SpawnEggMeta) event.getItem().getItemMeta()).getSpawnedType();
				} catch (Throwable ignored) {}
			}
			if (spawnedType != null) {
				lastInteractedEggs.put(spawnedType, event.getPlayer());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void event(CreatureSpawnEvent event) {
		if (event.getSpawnReason().toString().contains("EGG")) {
			Player player = lastInteractedEggs.remove(event.getEntity().getType());
			if (player != null) {
				Bukkit.getPluginManager().callEvent(new PlayerSpawnedEntityEvent(player, event.getEntity()));
			}
		}
	}

	// container manipulation
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void eventContainerManipulation(InventoryClickEvent clickEvent) {
		// build initial state
		if (clickEvent.getClickedInventory() == null || !clickEvent.getClickedInventory().equals(clickEvent.getView().getTopInventory())) {
			return;
		}
		InventoryState initialState = new InventoryState(clickEvent.getView().getTopInventory(), 0, clickEvent.getView().getTopInventory().getContents().length - 1);
		// delay
		BukkitThread.SYNC.operateLater(() -> {
			if (!clickEvent.isCancelled()) {
				// get new state and given/received
				InventoryState newState = new InventoryState(clickEvent.getView().getTopInventory(), 0, clickEvent.getView().getTopInventory().getContents().length - 1);
				Pair<Map<Integer, ItemStack>, Map<Integer, ItemStack>> changes = initialState.findChanges(newState);
				Map<Integer, ItemStack> removed = changes.getA();
				Map<Integer, ItemStack> added = changes.getB();
				// call craft event
				if (!(removed.isEmpty() && added.isEmpty())) {
					Bukkit.getPluginManager().callEvent(new PlayerManipulatedContainerEvent(clickEvent, initialState, newState, removed, added));
				}
			}
		}, null, 1);
	}

	// items crafting
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void event(CraftItemEvent craftEvent) {
		// build initial state
		Player player = (Player) craftEvent.getWhoClicked();
		PlayerInventoryState initialState = new PlayerInventoryState(player, craftEvent.getView().getTopInventory(), 1, 2, 3, 4, 5, 6, 7, 8, 9);
		// delay
		BukkitThread.SYNC.operateLater(() -> {
			if (!craftEvent.isCancelled()) {
				// get new state and given/received
				PlayerInventoryState newState = new PlayerInventoryState(player, craftEvent.getView().getTopInventory(), 1, 2, 3, 4, 5, 6, 7, 8, 9);
				Pair<Map<Integer, ItemStack>, Map<Integer, ItemStack>> changes = initialState.findChanges(newState);
				Map<Integer, ItemStack> removed = changes.getA();
				Map<Integer, ItemStack> added = changes.getB();
				// call craft event
				if (!(removed.isEmpty() && added.isEmpty())) {
					Bukkit.getPluginManager().callEvent(new PlayerCraftedItemsEvent(craftEvent, initialState, newState, removed, added));
				}
			}
		}, null, 1);
	}

	// trading with villagers
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void eventVillagers(InventoryClickEvent event) {
		// ensure we're trading
		if (event instanceof CraftItemEvent) return;
		if (event.getView().getTopInventory() == null) return;
		if (!event.getView().getTopInventory().getType().equals(InventoryType.MERCHANT)) return;
		if (event.getRawSlot() != 2) return;
		Villager villager = ObjectUtils.castOrNull(event.getView().getTopInventory().getHolder(), Villager.class);
		if (villager == null) return;
		Player player = ObjectUtils.castOrNull(event.getWhoClicked(), Player.class);
		if (player == null) return;
		// build initial state
		PlayerInventoryState initialState = new PlayerInventoryState(player, villager.getInventory(), 0, 1);
		// delay
		BukkitThread.SYNC.operateLater(() -> {
			if (!event.isCancelled()) {
				// get new state and given/received
				PlayerInventoryState newState = new PlayerInventoryState(player, villager.getInventory(), 0, 1);
				Pair<Map<Integer, ItemStack>, Map<Integer, ItemStack>> changes = initialState.findChanges(newState);
				Map<Integer, ItemStack> removed = changes.getA();
				Map<Integer, ItemStack> added = changes.getB();
				// call trade event
				if (!(removed.isEmpty() && added.isEmpty())) {
					Bukkit.getPluginManager().callEvent(new PlayerTradedVillagerEvent(villager, event, initialState, newState, removed, added));
				}
			}
		}, null, 1);
	}

	// potion brewing
	private Map<Block, BrewingStandData> brewingStandInventories = new HashMap<>();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void eventPotionBrewing(InventoryClickEvent event) {
		if (event.getView().getTopInventory() != null && event.getView().getTopInventory().getType().equals(InventoryType.BREWING)) {
			// build initial state
			BrewerInventory inv = (BrewerInventory) event.getView().getTopInventory();
			Player player = (Player) event.getWhoClicked();
			BukkitThread.SYNC.operateLater(() -> {
				if (!event.isCancelled()) {
					InventoryState newState = new InventoryState(inv, 0, 4);
					brewingStandInventories.put(inv.getHolder().getBlock(), new BrewingStandData(player.getUniqueId(), newState));
				}
			}, null, 1);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void event(BrewEvent event) {
		BrewingStand stand = event.getBlock().getState() != null && event.getBlock().getState() instanceof BrewingStand ? (BrewingStand) event.getBlock().getState() : null;
		BrewingStandData data = stand == null ? null : brewingStandInventories.remove(stand.getBlock());
		if (data != null) {
			// delay
			BukkitThread.SYNC.operateLater(() -> {
				if (!event.isCancelled()) {
					// get new state and given/received
					InventoryState newState = new InventoryState(event.getContents(), 0, 4);
					Pair<Map<Integer, ItemStack>, Map<Integer, ItemStack>> changes = data.currentState.findChanges(newState);
					Map<Integer, ItemStack> removed = changes.getA();
					Map<Integer, ItemStack> added = changes.getB();
					// call brew event
					if (!(removed.isEmpty() && added.isEmpty())) {
						Bukkit.getPluginManager().callEvent(new PlayerBrewPotionsEvent(event, data.player, stand, data.currentState, newState, removed, added));
					}
					// update state
					brewingStandInventories.put(stand.getBlock(), new BrewingStandData(data.player, newState));
				}
			}, null, 1);
		}
	}

	private static class BrewingStandData {
		private UUID player;
		private InventoryState currentState;
		private BrewingStandData(UUID player, InventoryState currentState) {
			this.player = player;
			this.currentState = currentState;
		}
	}

	// furnace burning
	private Map<Block, FurnaceData> furnaceInventories = new HashMap<>();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void eventFurnaceBurning(InventoryClickEvent event) {
		if (event.getView().getTopInventory() != null && event.getView().getTopInventory().getType().toString().contains("FURNACE")) {
			// build initial state
			FurnaceInventory inv = (FurnaceInventory) event.getView().getTopInventory();
			Player player = (Player) event.getWhoClicked();
			BukkitThread.SYNC.operateLater(() -> {
				if (!event.isCancelled()) {
					InventoryState newState = new InventoryState(inv, 0, 2);
					furnaceInventories.put(inv.getHolder().getBlock(), new FurnaceData(player.getUniqueId(), newState));
				}
			}, null, 1);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void event(FurnaceSmeltEvent event) {
		Furnace furnace = event.getBlock().getState() != null && event.getBlock().getState() instanceof Furnace ? (Furnace) event.getBlock().getState() : null;
		FurnaceData data = furnace == null ? null : furnaceInventories.remove(furnace.getBlock());
		if (data != null) {
			// delay
			BukkitThread.SYNC.operateLater(() -> {
				if (!event.isCancelled()) {
					// get new state and given/received
					InventoryState newState = new InventoryState(furnace.getInventory(), 0, 2);
					Pair<Map<Integer, ItemStack>, Map<Integer, ItemStack>> changes = data.currentState.findChanges(newState);
					Map<Integer, ItemStack> removed = changes.getA();
					Map<Integer, ItemStack> added = changes.getB();
					// call smelt event
					if (!(removed.isEmpty() && added.isEmpty())) {
						Bukkit.getPluginManager().callEvent(new PlayerSmeltItemsEvent(event, data.player, furnace, data.currentState, newState, removed, added));
					}
					// update state
					furnaceInventories.put(furnace.getBlock(), new FurnaceData(data.player, newState));
				}
			}, null, 1);
		}
	}

	private static class FurnaceData {
		private UUID player;
		private InventoryState currentState;
		private FurnaceData(UUID player, InventoryState currentState) {
			this.player = player;
			this.currentState = currentState;
		}
	}

	// items repairing
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void event(InventoryClickEvent event) {
		// ensure we're repairing
		Player player = ObjectUtils.castOrNull(event.getWhoClicked(), Player.class);
		if (player == null) return;
		if (event instanceof CraftItemEvent) return;
		if (event.getView().getTopInventory() == null) return;
		if (!event.getView().getTopInventory().getType().equals(InventoryType.ANVIL)) return;
		if (event.getRawSlot() != 2) return;
		if (event.getView().getTopInventory().getContents().length < 3) return;  // https://pastebin.com/wnX7MCsi :confusedwat:
		ItemStack cost1 = event.getView().getTopInventory().getContents()[0];
		ItemStack cost2 = event.getView().getTopInventory().getContents()[1];
		ItemStack repairing = event.getView().getTopInventory().getContents()[2];
		if (!Mat.fromItem(cost1).equals(Mat.fromItem(cost2)) || !Mat.fromItem(cost2).equals(Mat.fromItem(repairing))) return;
		// call event
		BukkitThread.SYNC.operateLater(() -> {
			if (!event.isCancelled()) {
				Bukkit.getPluginManager().callEvent(new PlayerRepairItemEvent(event, player, cost1, cost2, repairing));
			}
		}, null, 1);
	}


}

