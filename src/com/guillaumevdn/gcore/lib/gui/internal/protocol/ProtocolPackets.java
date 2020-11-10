package com.guillaumevdn.gcore.lib.gui.internal.protocol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Compat;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.legacy_npc.NpcProtocols;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureBiConsumer;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureConsumer;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureQuadriConsumer;

/**
 * @author GuillaumeVDN
 */
public class ProtocolPackets {

	// open/close window
	static final ReflectionProcedureBiConsumer<Collection<Player>, Window> OPEN_WINDOW = new ReflectionProcedureBiConsumer<Collection<Player>, Window>()
			.setIf(Version.IS_1_7, (players, window) -> {
				Reflection.sendNmsPacket(players, "PacketPlayOutOpenWindow", window.getId(), window.getGUI().getType().getContainerId(), Compat.createChatComponent(window.getGUI().getName()).get(), window.getGUI().getType().getPre114PacketSlots(), true);
			})
			.setIf(Version.ATLEAST_1_14, (players, window) -> {
				Reflection.sendNmsPacket(players, "PacketPlayOutOpenWindow", window.getId(), Reflection.getNmsFakeEnum("Containers").valueOf(window.getGUI().getType().getContainerId()).get(), Compat.createChatComponent(window.getGUI().getName()).get());
			})
			.orElse((players, window) -> {
				Reflection.sendNmsPacket(players, "PacketPlayOutOpenWindow", window.getId(), window.getGUI().getType().getPre114ContainerId(), Compat.createChatComponent(window.getGUI().getName()).get(), window.getGUI().getType().getPre114PacketSlots());
			})
			;

	static final ReflectionProcedureBiConsumer<Collection<Player>, Window> CLOSE_WINDOW = new ReflectionProcedureBiConsumer<Collection<Player>, Window>()
			.set((players, window) -> {
				Reflection.sendNmsPacket(players, "PacketPlayOutCloseWindow", window.getId());
			});

	// set window items
	static final ReflectionProcedureBiConsumer<Collection<Player>, Window> SET_WINDOW_ITEMS = new ReflectionProcedureBiConsumer<Collection<Player>, Window>()
			.set((players, window) -> {

				Object emptyStack = !Version.ATLEAST_1_11 ? null : emptyStack();

				for (Player player : players) {
					ItemStack[] contents = player.getInventory().getContents();
					List list = new ArrayList<>();

					// fill content ; the list goes from 0 (top left) to x (bottom right), so add it in the correct order
					for (int i = 0; i < window.getGUI().getType().getSize(); ++i) { // GUI contents
						addToList(window.getItems().get(i), list, emptyStack);
					}
					for (int i = 9; i < 36; ++i) { // player inventory contents
						addToList(contents[i], list, emptyStack);
					}
					for (int i = 0; i < 9; ++i) { // player hotbar contents
						addToList(contents[i], list, emptyStack);
					}

					// build and send packet
					Reflection.sendNmsPacket(player, Reflection.newNmsInstance("PacketPlayOutWindowItems")
							.setField("a", window.getId())
							.setField("b", !Version.ATLEAST_1_11 ? list.toArray() : CollectionUtils.createList(Reflection.getNmsClass("ItemStack"), list))
							.get()
							);
				}
			});

	private static void addToList(ItemStack item, List list, Object emptyStack) throws Throwable {
		list.add(item != null ? Reflection.invokeCraftbukkitMethod("inventory.CraftItemStack", "asNMSCopy", null, item).get() : emptyStack);
	}

	private static final Object emptyStack() throws Throwable {
		return Reflection.invokeCraftbukkitMethod("inventory.CraftItemStack", "asNMSCopy", null, new ItemStack(Material.AIR)).get();
	}

	// set window item
	static final ReflectionProcedureQuadriConsumer<Collection<Player>, Integer, Integer, ItemStack> SET_SLOT = new ReflectionProcedureQuadriConsumer<Collection<Player>, Integer, Integer, ItemStack>()
			.set((players, window, slot, item) -> {
				// build and send packet
				Reflection.sendNmsPacket(players, "PacketPlayOutSetSlot", (int) window, slot, item != null ? Reflection.invokeCraftbukkitMethod("inventory.CraftItemStack", "asNMSCopy", null, item).get() : emptyStack());
			});

	// refresh equipment
	static final ReflectionProcedureConsumer<Player> REFRESH_EQUIPMENT = new ReflectionProcedureConsumer<Player>()
			.set(player -> {
				NpcProtocols.inst().sendInventory(player, player.getEntityId(),
						player.getInventory().getItemInMainHand(),
						player.getInventory().getItemInOffHand(),
						player.getInventory().getBoots(),
						player.getInventory().getLeggings(),
						player.getInventory().getChestplate(),
						player.getInventory().getHelmet()
						);
			});

}
