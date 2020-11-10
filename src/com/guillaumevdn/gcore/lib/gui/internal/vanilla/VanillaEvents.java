package com.guillaumevdn.gcore.lib.gui.internal.vanilla;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUI.Option;

/**
 * @author GuillaumeVDN
 */
public class VanillaEvents implements Listener {

	private VanillaHandler handler;
	private transient long lastClick = 0L;

	public VanillaEvents(VanillaHandler handler) {
		this.handler = handler;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(final InventoryClickEvent event) {
		// not a page of this GUI
		final int pageIndex = handler.pageIndexOf(event.getInventory());
		if (pageIndex == -1) {
			return;
		}
		// cancel event
		event.setCancelled(true);
		// not on inventory
		if (event.getClickedInventory() == null) {
			return;
		}
		// not a player
		final Player player = event.getWhoClicked() != null && event.getWhoClicked() instanceof Player ? (Player) event.getWhoClicked() : null;
		if (player == null) {
			return;
		}
		// too recent
		if (System.currentTimeMillis() - lastClick <= 20L) {
			return;
		}
		lastClick = System.currentTimeMillis();
		// process later
		new BukkitRunnable() {
			@Override
			public void run() {
				int slot = event.getRawSlot();
				try {
					// player inventory
					if (player.getInventory().equals(event.getClickedInventory())) {
						handler.getGUI().onPlayerInventoryClick(player, event.getRawSlot(), Mat.isVoid(event.getCurrentItem()) ? null : event.getCurrentItem(), ClickType.valueOf(event.getClick().name()), pageIndex);
					}
					// another inventory
					else {
						handler.onClick(player, ClickType.valueOf(event.getClick().toString()), slot, pageIndex);
					}
				} catch (Throwable exception) {
					throw new Error("couldn't perform click effects in GUI " + handler.getGUI().getId() + " at slot " + slot + " of page " + pageIndex, exception);
				}
			}
		}.runTaskLater(handler.getGUI().getPlugin(), 1L);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(InventoryCloseEvent event) {
		// not a page of this GUI
		final int pageIndex = handler.pageIndexOf(event.getInventory());
		if (pageIndex == -1) {
			return;
		}
		// not a player
		final Player player = event.getPlayer() != null && event.getPlayer() instanceof Player ? (Player) event.getPlayer() : null;
		if (player == null) {
			return;
		}
		// call on close
		try {
			handler.getGUI().onClose(player);
		} catch (Throwable exception) {
			throw new Error("couldn't perform close effects in GUI " + handler.getGUI().getId() + " for page " + pageIndex, exception);
		}
		// unregister on close
		if (!handler.getGUI().getOptions().contains(Option.DONT_UNREGISTER_ON_CLOSE)) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (handler.getViewers().isEmpty()) {
						handler.getGUI().deactivate(true);
					}
				}
			}.runTaskLater(handler.getGUI().getPlugin(), 5L);
		}
	}

}
