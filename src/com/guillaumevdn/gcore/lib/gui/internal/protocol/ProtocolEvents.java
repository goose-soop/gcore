package com.guillaumevdn.gcore.lib.gui.internal.protocol;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUI.Option;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;

/**
 * @author GuillaumeVDN
 */
public class ProtocolEvents implements PacketListener {

	private ProtocolHandler handler;

	public ProtocolEvents(ProtocolHandler handler) {
		this.handler = handler;
	}

	// get
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

	// receive
	private transient long lastClick = 0L;

	@Override
	public void onPacketReceiving(PacketEvent event) {
		// https://wiki.vg/index.php?title=Protocol#Click_Window
		if (event.getPacketType().equals(PacketType.Play.Client.WINDOW_CLICK)) {
			if (event.getPacket().getIntegers().getValues().get(0) == ProtocolHandler.WINDOW_ID) {
				// not a valid page here
				Window page = handler.getPage(event.getPlayer());
				if (page == null) {
					return;
				}
				event.setCancelled(true);
				// read packet
				short actionId = event.getPacket().getShorts().getValues().get(0);
				int slot = event.getPacket().getIntegers().getValues().get(1);
				int button = event.getPacket().getIntegers().getValues().get(2);
				int mode;
				if (!Version.ATLEAST_1_9) {
					mode = event.getPacket().getIntegers().getValues().get(3);
				} else {
					try {
						Class clickTypeEnum = Reflection.getNmsClass("InventoryClickType");
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
					// send transaction packet to cancel click ; https://wiki.vg/Protocol#Window_Confirmation_.28clientbound.29
					Reflection.sendNmsPacket(event.getPlayer(), "PacketPlayOutTransaction", page.getId(), actionId, false);
					// if this is a shift click, resend the whole window because we can't know where the new item will be located
					if (mode == 1) {
						ProtocolPackets.SET_WINDOW_ITEMS.process(CollectionUtils.asList(event.getPlayer()), page);
					}
					// else, only reset some slots
					else {
						// reset cursor
						ProtocolPackets.SET_SLOT.process(CollectionUtils.asList(event.getPlayer()), -1, -1, null); // -1 and -1 for cursor ; see https://wiki.vg/Protocol#Set_Slot
						// reset slot in GUI
						if (slot < page.getGUI().getType().getSize()) {
							ProtocolPackets.SET_SLOT.process(CollectionUtils.asList(event.getPlayer()), page.getId(), slot, page.getItems().get(slot));
						}
						// reset slot in player inventory
						else {
							int playerInventorySlot = slot - page.getGUI().getType().getSize();
							if (playerInventorySlot >= 27) {
								ProtocolPackets.SET_SLOT.process(CollectionUtils.asList(event.getPlayer()), page.getId(), slot, event.getPlayer().getInventory().getContents()[playerInventorySlot - 27]);
							} else {
								ProtocolPackets.SET_SLOT.process(CollectionUtils.asList(event.getPlayer()), page.getId(), slot, event.getPlayer().getInventory().getContents()[playerInventorySlot + 9]);
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
								ProtocolPackets.SET_SLOT.process(CollectionUtils.asList(event.getPlayer()), page.getId(), buttonSlot, event.getPlayer().getInventory().getContents()[button]);
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
				if (mode == 0) {
					click(event.getPlayer(), button == 0 ? ClickType.LEFT : ClickType.RIGHT, page.getIndex(), slot);
				} else if (mode == 1) {
					click(event.getPlayer(), button == 0 ? ClickType.SHIFT_LEFT : ClickType.SHIFT_RIGHT, page.getIndex(), slot);
				} else if (mode == 2) {
					click(event.getPlayer(), button == 40 ? ClickType.KEY_OFFHAND : ClickType.valueOf("NUMBER_KEY_" + (button + 1)), page.getIndex(), slot);
				} else if (mode == 3) {
					click(event.getPlayer(), ClickType.MIDDLE, page.getIndex(), slot);
				} else if (mode == 4) {
					click(event.getPlayer(), button == 0 ? ClickType.DROP : ClickType.CONTROL_DROP, page.getIndex(), slot);
				} else if (mode == 6) {
					click(event.getPlayer(), ClickType.DOUBLE_CLICK, page.getIndex(), slot);
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
				handler.getGUI().onClose(event.getPlayer());
			} catch (Throwable exception) {
				throw new Error("couldn't perform close effects in GUI " + handler.getGUI().getId() + " for page " + page.getIndex(), exception);
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

	private void click(Player player, ClickType click, int pageIndex, int slot) {
		BukkitThread.SYNC.operate(() -> {
			// player inventory
			if (slot >= handler.getGUI().getType().getSize()) {
				int s = slot >= handler.getGUI().getType().getSize() + 27 ? slot - handler.getGUI().getType().getSize() - 27 : slot - handler.getGUI().getType().getSize() + 9;
				handler.getGUI().onPlayerInventoryClick(player, s, handler.getPageItem(pageIndex, slot), click, pageIndex);
			}
			// another inventory
			else {
				handler.onClick(player, click, slot, pageIndex);
			}
		}, exception -> {
			throw new Error("couldn't perform click effects in GUI " + handler.getGUI().getId() + " at slot " + slot + " of page " + pageIndex, exception);
		});
	}

	// send
	@Override
	public void onPacketSending(PacketEvent event) {
	}

}
