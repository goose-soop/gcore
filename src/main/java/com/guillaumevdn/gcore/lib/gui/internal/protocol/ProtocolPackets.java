package com.guillaumevdn.gcore.lib.gui.internal.protocol;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Compat;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.concurrency.RWHashSet;
import com.guillaumevdn.gcore.lib.legacy_npc.NpcProtocols;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureBiConsumer;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureConsumer;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureQuintConsumer;

/**
 * @author GuillaumeVDN
 */
public class ProtocolPackets {

    // ----- open/close window
    static final ReflectionProcedureBiConsumer<RWHashSet<Player>, Window> OPEN_WINDOW = new ReflectionProcedureBiConsumer<RWHashSet<Player>, Window>()
            .setIf(Version.IS_1_7, (players, window) -> {
                Reflection.sendNmsPacket(players, "PacketPlayOutOpenWindow", window.getId(), window.getGUI().getType().getContainerId(),
                        Compat.createChatComponent(window.getGUI().getName()).get(), window.getGUI().getType().getPre114PacketSlots(), true);
            }).orIf(Version.ATLEAST_1_17, (players, window) -> {
                Reflection.sendNmsPacket(players, "network.protocol.game.PacketPlayOutOpenWindow", window.getId(),
                        Reflection.getNmsFakeEnum("world.inventory.Containers").valueOf(window.getGUI().getType().getContainerId117()).get(),
                        Compat.createChatComponent(window.getGUI().getName()).get());
            }).orIf(Version.ATLEAST_1_14, (players, window) -> {
                Reflection.sendNmsPacket(players, "PacketPlayOutOpenWindow", window.getId(),
                        Reflection.getNmsFakeEnum("Containers").valueOf(window.getGUI().getType().getContainerId()).get(),
                        Compat.createChatComponent(window.getGUI().getName()).get());
            }).orElse((players, window) -> {
                Reflection.sendNmsPacket(players, "PacketPlayOutOpenWindow", window.getId(), window.getGUI().getType().getContainerIdPre114(),
                        Compat.createChatComponent(window.getGUI().getName()).get(), window.getGUI().getType().getPre114PacketSlots());
            });

    static final ReflectionProcedureBiConsumer<RWHashSet<Player>, Window> CLOSE_WINDOW = new ReflectionProcedureBiConsumer<RWHashSet<Player>, Window>()
            .setIf(Version.ATLEAST_1_17, (players, window) -> {
                Reflection.sendNmsPacket(players, "network.protocol.game.PacketPlayOutCloseWindow", window.getId());
            }).orElse((players, window) -> {
                Reflection.sendNmsPacket(players, "PacketPlayOutCloseWindow", window.getId());
            });

    // ----- set window items
    static final ReflectionProcedureBiConsumer<RWHashSet<Player>, Window> SET_WINDOW_ITEMS = new ReflectionProcedureBiConsumer<RWHashSet<Player>, Window>()
            .set((players, window) -> {

                Object emptyStack = !Version.ATLEAST_1_11 ? null : emptyStack();
                Class itemStackClass = Reflection.getNmsClass((Version.REMAPPED ? "world.item." : "") + "ItemStack");

                players.forEachThrowable(player -> {
                    ItemStack[] contents = player.getInventory().getContents();
                    List list;
                    if (Version.ATLEAST_1_17) {
                        list = Reflection.invokeNmsMethod("core.NonNullList", Version.MOJANG_MAPPINGS ? "create" : "a", null).get();
                    } else {
                        list = new ArrayList<>();
                    }

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
                    ReflectionObject packet;

                    if (Version.ATLEAST_1_17_1) {
                        packet = Reflection.newNmsInstance("network.protocol.game.PacketPlayOutWindowItems", window.getId(), window.incrementStateId(), list,
                                emptyStack() /* carriedItem, the cursor I assume */);
                    } else if (Version.ATLEAST_1_17) {
                        packet = Reflection.newNmsInstance("network.protocol.game.PacketPlayOutWindowItems", window.getId(), list);
                    } else {
                        packet = Reflection.newNmsInstance("PacketPlayOutWindowItems");
                        packet.setField("a", window.getId());
                        packet.setField("b",
                                !Version.ATLEAST_1_11 ? Reflection.createArray(itemStackClass, list) : CollectionUtils.createList(itemStackClass, list));
                    }

                    Reflection.sendNmsPacket(player, packet.get());
                });
            });

    private static void addToList(ItemStack item, List list, Object emptyStack) throws Throwable {
        list.add(item != null ? Reflection.invokeCraftbukkitMethod("inventory.CraftItemStack", "asNMSCopy", null, item).get() : emptyStack);
    }

    private static final Object emptyStack() throws Throwable {
        return Reflection.invokeCraftbukkitMethod("inventory.CraftItemStack", "asNMSCopy", null, new ItemStack(Material.AIR)).get();
    }

    // ----- set window item
    static final ReflectionProcedureQuintConsumer<RWHashSet<Player>, Integer, Integer, Integer, ItemStack> SET_SLOT = new ReflectionProcedureQuintConsumer<RWHashSet<Player>, Integer, Integer, Integer, ItemStack>()
            .set((players, windowId, stateId, slot, item) -> {
                Object stack = item != null ? Reflection.invokeCraftbukkitMethod("inventory.CraftItemStack", "asNMSCopy", null, item).get() : emptyStack();
                if (Version.ATLEAST_1_17_1) {
                    Reflection.sendNmsPacket(players, "network.protocol.game.PacketPlayOutSetSlot", windowId, stateId, slot, stack);
                } else {
                    Reflection.sendNmsPacket(players, (Version.REMAPPED ? "network.protocol.game." : "") + "PacketPlayOutSetSlot", windowId, slot, stack);
                }
            });

    // ----- refresh equipment
    static final ReflectionProcedureConsumer<Player> REFRESH_EQUIPMENT = new ReflectionProcedureConsumer<Player>().set(player -> {
        NpcProtocols.inst().sendInventory(player, player.getEntityId(), player.getInventory().getItemInMainHand(), player.getInventory().getItemInOffHand(),
                player.getInventory().getBoots(), player.getInventory().getLeggings(), player.getInventory().getChestplate(),
                player.getInventory().getHelmet());
    });

}
