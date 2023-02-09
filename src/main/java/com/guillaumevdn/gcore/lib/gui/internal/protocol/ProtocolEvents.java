package com.guillaumevdn.gcore.lib.gui.internal.protocol;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.command.GcoreItemReadClick;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.concurrency.RWHashSet;
import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;

/**
 * @author GuillaumeVDN
 */
public class ProtocolEvents implements PacketListener, Listener {

	private ProtocolHandler handler;

	public ProtocolEvents(ProtocolHandler handler) {
		this.handler = handler;
	}

	// ----- -----

	@Override
	public Plugin getPlugin() {
		return handler.getGUI().getPlugin();
	}

	@Override
	public ListeningWhitelist getReceivingWhitelist() {
		return ListeningWhitelist.newBuilder().types(PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.CLOSE_WINDOW/*, PacketType.Play.Client.TRANSACTION*/).priority(ListenerPriority.LOWEST).build();
	}

	@Override
	public ListeningWhitelist getSendingWhitelist() {
		return ListeningWhitelist.newBuilder()/*.types(PacketType.Play.Client.TRANSACTION)*/.build();
	}

	/* spigot uses those non-intuitive enum names for modes in 1.9+, because of... reasons :
			PICKUP -> normal click (0)
			QUICK_MOVE -> shift click (1)
			SWAP -> number key (2)
			CLONE -> middle mouse click, creative (3)
			THROW -> drop key (4)
			QUICK_CRAFT -> drag (5) (ignored)
			PICKUP_ALL -> double click (6)
	 */
	private static final Map<String, Integer> MODES_19 = CollectionUtils.asUnmodifiableMap(
			"PICKUP", 0,
			"QUICK_MOVE", 1,
			"SWAP", 2,
			"CLONE", 3,
			"THROW", 4,
			"QUICK_CRAFT", 5,
			"PICKUP_ALL", 6
			);

	// ----- receive

	private transient long lastClick = 0L;

	@Override
	public void onPacketReceiving(PacketEvent event) {
		// https://wiki.vg/index.php?title=Protocol#Click_Window
		if (event.getPacketType().equals(PacketType.Play.Client.WINDOW_CLICK)) {

			final int windowId = event.getPacket().getIntegers().getValues().get(0);
			if (windowId == ProtocolHandler.WINDOW_ID) {
				// not a valid page here
				Window page = handler.getPage(event.getPlayer());
				if (page == null) {
					return;
				}
				event.setCancelled(true);

				// read packet
				final int actionId;
				final int slot;
				final int button;
				int mode;

				if (Version.ATLEAST_1_17_1) {
					actionId = event.getPacket().getIntegers().getValues().get(1);
					slot = event.getPacket().getIntegers().getValues().get(2);
					button = event.getPacket().getIntegers().getValues().get(3);
				} else {
					actionId = event.getPacket().getShorts().getValues().get(0);
					slot = event.getPacket().getIntegers().getValues().get(1);
					button = event.getPacket().getIntegers().getValues().get(2);
				}

				if (!Version.ATLEAST_1_9) {
					mode = event.getPacket().getIntegers().getValues().get(3);
				} else {
					try {
						Class clickTypeEnum = Reflection.getNmsClass((Version.REMAPPED ? "world.inventory." : "") + "InventoryClickType");
						String modeEnum = ReflectionObject.of(event.getPacket().getEnumModifier(clickTypeEnum, clickTypeEnum).getValues().get(0)).invokeMethod("name").get();
						Integer foundMode = MODES_19.get(modeEnum);
						if (foundMode != null) {
							mode = foundMode;
						} else {
							GCore.inst().getMainLogger().error("Couldn't determine click mode from " + modeEnum);
							mode = -1;
						}
					} catch (Throwable exception) {
						GCore.inst().getMainLogger().error("Couldn't determine click mode", exception);
						mode = -1;
					}
				}
				// cancel action
				try {
					RWHashSet<Player> set = CollectionUtils.asRWSet(event.getPlayer());

					// send transaction packet to cancel click ; https://wiki.vg/Protocol#Window_Confirmation_.28clientbound.29
					// removed in 1.17, we'll see what it does
					if (Version.ATLEAST_1_17) {
					} else {
						Reflection.sendNmsPacket(event.getPlayer(), "PacketPlayOutTransaction", page.getId(), (short) actionId, false);
					}

					// if this is a shift click, resend the whole window because we can't know where the new item will be located
					if (mode == 1) {
						ProtocolPackets.SET_WINDOW_ITEMS.process(set, page);
					}
					// else, only reset some slots
					else {
						// reset cursor
						ProtocolPackets.SET_SLOT.process(set, -1, page.incrementStateId(), -1, null); // -1 and -1 for cursor ; see https://wiki.vg/Protocol#Set_Slot

						// reset slot in GUI
						if (slot < page.getGUI().getType().getSize()) {
							ProtocolPackets.SET_SLOT.process(set, page.getId(), page.incrementStateId(), slot, page.getItems().get(slot));
						}
						// reset slot in player inventory
						else {
							int playerInventorySlot = slot - page.getGUI().getType().getSize();
							if (playerInventorySlot >= 27) {
								ProtocolPackets.SET_SLOT.process(set, page.getId(), page.incrementStateId(), slot, event.getPlayer().getInventory().getContents()[playerInventorySlot - 27]);
							} else {
								ProtocolPackets.SET_SLOT.process(set, page.getId(), page.incrementStateId(), slot, event.getPlayer().getInventory().getContents()[playerInventorySlot + 9]);
							}
						}

						// reset offhand slot if it's a offhand key click
						if (mode == 2 && button == 40) {
							ProtocolPackets.REFRESH_EQUIPMENT.process(event.getPlayer());
						}
						// reset slot in hotbar if it's a number key click
						else if (mode == 2) {
							int buttonSlot = page.getGUI().getType().getSize() + 27 + button;
							if (buttonSlot != slot) {
								ProtocolPackets.SET_SLOT.process(set, page.getId(), page.incrementStateId(), buttonSlot, event.getPlayer().getInventory().getContents()[button]);
							}
						}
					}
				} catch (Throwable exception) {
					GCore.inst().getMainLogger().error("Couldn't cancel action", exception);
					event.getPlayer().updateInventory();
				}

				// ignore special slot
				if (slot == -999) {
					return;
				}

				// too recent
				if (System.currentTimeMillis() - lastClick <= 20L) {
					return;
				}
				lastClick = System.currentTimeMillis();

				// process click
				final int m = mode;  // turbo pepega
				Runnable processor = () -> {
					if (m == 0) {
						click(event.getPlayer(), button == 0 ? ClickType.LEFT : ClickType.RIGHT, page.getIndex(), slot);
					} else if (m == 1) {
						click(event.getPlayer(), button == 0 ? ClickType.SHIFT_LEFT : ClickType.SHIFT_RIGHT, page.getIndex(), slot);
					} else if (m == 2) {
						click(event.getPlayer(), button == 40 ? ClickType.KEY_OFFHAND : ClickType.valueOf("NUMBER_KEY_" + (button + 1)), page.getIndex(), slot);
					} else if (m == 3) {
						click(event.getPlayer(), ClickType.MIDDLE, page.getIndex(), slot);
					} else if (m == 4) {
						click(event.getPlayer(), button == 0 ? ClickType.DROP : ClickType.CONTROL_DROP, page.getIndex(), slot);
					} else if (m == 6) {
						click(event.getPlayer(), ClickType.DOUBLE_CLICK, page.getIndex(), slot);
					}
				};
				if (ConfigGCore.delayProtocolGUIClicksTicks > 0) {  // maybe delay it, #1206
					BukkitThread.current().operateLater(handler.getGUI().getPlugin(), ThrowableRunnable.fromSafe(processor), null, ConfigGCore.delayProtocolGUIClicksTicks);
				} else {
					processor.run();
				}

			}
		}
		// https://wiki.vg/Protocol#Window_Confirmation_.28serverbound.29
		else if (event.getPacketType().equals(PacketType.Play.Client.TRANSACTION)) {
			if (event.getPacket().getIntegers().getValues().get(0) == ProtocolHandler.WINDOW_ID) {
				// not a valid page here
				Window page = handler.getPage(event.getPlayer());
				if (page == null) {
					return;
				}
				// ignore transaction confirm packets
				event.setCancelled(true);
			}
		}
		// https://wiki.vg/index.php?title=Protocol#Close_Window
		else if (event.getPacketType().equals(PacketType.Play.Client.CLOSE_WINDOW)) {
			// not a valid page here
			Window page = handler.getPage(event.getPlayer());
			if (page == null) {
				return;
			}
			event.setCancelled(true);
			// remove from viewers
			page.getViewers().remove(event.getPlayer());
			// call on close
			try {
				handler.onClose(event.getPlayer());
			} catch (Throwable exception) {
				throw new Error("couldn't perform close effects in GUI " + handler.getGUI().getId() + " for page " + page.getIndex(), exception);
			}
		}
	}

	private void click(Player player, ClickType click, int pageIndex, int slot) {
		BukkitThread.FORCE_SYNC.operate(handler.getGUI().getPlugin(), () -> {
			// player inventory
			if (slot >= handler.getGUI().getType().getSize()) {
				int s = slot >= handler.getGUI().getType().getSize() + 27 ? slot - handler.getGUI().getType().getSize() - 27 : slot - handler.getGUI().getType().getSize() + 9;
				handler.getGUI().onPlayerInventoryClick(new ClickCall(player, click, handler.getGUI(), pageIndex, s), player.getInventory().getItem(s));

				if (GcoreItemReadClick.TOGGLED.contains(player)) {
					GcoreItemReadClick.logItem(player, player.getInventory().getItem(s));
				}
			}
			// another inventory
			else {
				handler.onClick(player, click, slot, pageIndex);

				if (GcoreItemReadClick.TOGGLED.contains(player)) {
					GcoreItemReadClick.logItem(player, handler.getPageItem(pageIndex, slot));
				}
			}
		}, error -> {
			handler.getGUI().getPlugin().getMainLogger().error("couldn't perform click effects in GUI " + handler.getGUI().getId() + " at slot " + slot + " of page " + pageIndex, error);
		});
	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		handler.removeViewer(event.getPlayer());
	}

	// ----- send

	@Override
	public void onPacketSending(PacketEvent event) {
	}

}
