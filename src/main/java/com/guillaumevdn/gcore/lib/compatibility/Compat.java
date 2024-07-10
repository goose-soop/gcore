package com.guillaumevdn.gcore.lib.compatibility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.gui.ItemFlag;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionEnum;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureBiConsumer;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureBiFunction;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureFunction;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureSexaConsumer;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureTriConsumer;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author GuillaumeVDN
 */
public final class Compat {

	// ----- create chat component
	private static final ReflectionProcedureFunction<String, ReflectionObject> CREATE_CHAT_COMPONENT = new ReflectionProcedureFunction<String, ReflectionObject>()
			.setIf(Version.IS_1_7, string -> {
				return Reflection.invokeNmsMethod("ChatSerializer", "a", null, "{\"text\": \"" + string.replace("\"", "\\\"") + "\"}");
			}).orIf(Version.CURRENT == Version.MC_1_20_R2, string -> {
				return Reflection.invokeNmsMethod((Version.REMAPPED ? "network.chat." : "") + "IChatBaseComponent$ChatSerializer", "b", null,
						"{\"text\": \"" + string.replace("\"", "\\\"") + "\"}");
			}).orElse(string -> {
				return Reflection.invokeNmsMethod((Version.REMAPPED ? "network.chat." : "") + "IChatBaseComponent$ChatSerializer", "a", null,
						"{\"text\": \"" + string.replace("\"", "\\\"") + "\"}");
			});

	public static ReflectionObject createChatComponent(String string) {
		return CREATE_CHAT_COMPONENT.process(string);
	}

	public static Object createChatComponentIfNonEmptyOrNull(String string) {
		return string == null || string.isEmpty() ? null : createChatComponent(string).get();
	}

	// ----- send title
	private static final ReflectionProcedureSexaConsumer<Player, String, String, Integer, Integer, Integer> SEND_TITLE = new ReflectionProcedureSexaConsumer<Player, String, String, Integer, Integer, Integer>()
			.setIf(Version.ATLEAST_1_17, (player, title, subtitle, fadeIn, duration, fadeOut) -> {
				player.sendTitle(title, subtitle, fadeIn, duration, fadeOut);
			}).orIf(Version.ATLEAST_1_8, (player, title, subtitle, fadeIn, duration, fadeOut) -> {
				ReflectionEnum enumTitleAction = Reflection.getNmsEnum("PacketPlayOutTitle$EnumTitleAction");
				Reflection.sendNmsPacket(player, "PacketPlayOutTitle", enumTitleAction.valueOf("TIMES").get(), null, fadeIn, duration, fadeOut);
				if (subtitle != null)
					Reflection.sendNmsPacket(player, "PacketPlayOutTitle", enumTitleAction.valueOf("SUBTITLE").get(), createChatComponent(subtitle).get());
				if (title != null)
					Reflection.sendNmsPacket(player, "PacketPlayOutTitle", enumTitleAction.valueOf("TITLE").get(), createChatComponent(title).get());
			});

	public static void sendTitle(Player player, String title, String subtitle, long fadeIn, long duration, long fadeOut) {
		int fadeInTicks = (int) (fadeIn / 50L);
		int durationTicks = (int) (duration / 50L);
		int fadeOutTicks = (int) (fadeOut / 50L);
		if (durationTicks > 0) {
			sendTitle(player, title, subtitle, fadeInTicks, durationTicks, fadeOutTicks);
		}
	}

	public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int duration, int fadeOut) {
		SEND_TITLE.process(player, title, subtitle, fadeIn, duration, fadeOut);
	}

	// ----- send actionbar
	private static final ReflectionProcedureBiConsumer<Player, String> SEND_ACTIONBAR = new ReflectionProcedureBiConsumer<Player, String>()
			.setIf(Version.ATLEAST_1_17, (player, actionbar) -> {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbar));
			}).orIf(Version.ATLEAST_1_11, (player, actionbar) -> {
				ReflectionEnum enumTitleAction = Reflection.getNmsEnum("PacketPlayOutTitle$EnumTitleAction");
				Reflection.sendNmsPacket(player, "PacketPlayOutTitle", enumTitleAction.valueOf("ACTIONBAR").justGet(),
						createChatComponent(actionbar).justGet());
			}).orIf(Version.ATLEAST_1_8, (player, actionbar) -> {
				Reflection.sendNmsPacket(player, "PacketPlayOutChat", createChatComponent(actionbar).get(), (byte) 2);
			});

	public static void sendActionbar(Player player, String actionbar) {
		SEND_ACTIONBAR.process(player, actionbar);
	}

	public static void sendActionbar(Collection<Player> players, String actionbar) {
		players.forEach(pl -> sendActionbar(pl, actionbar));
	}

	// ----- send json message
	private static final ReflectionProcedureBiConsumer<Collection<Player>, String> SEND_JSON_CHAT = new ReflectionProcedureBiConsumer<Collection<Player>, String>()
			.setIf(Version.IS_1_7, (players, json) -> {
				Object component = Reflection.invokeNmsMethod("ChatSerializer", "a", null, json).get();
				Reflection.sendNmsPacket(players, "PacketPlayOutChat", component);
			}).orIf(Version.ATLEAST_1_19, (players, json) -> {
				// TODO : find a cooler packet solution with new encryption system
				GCore.inst().operateSync(() -> {
					for (Player pl : players) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + pl.getName() + " " + json);
					}
				});
			}).orIf(Version.ATLEAST_1_17, (players, json) -> {
				ReflectionEnum messageTypeEnum = Reflection.getNmsEnum("network.chat.ChatMessageType");
				Object component = Reflection.invokeNmsMethod("network.chat.IChatBaseComponent$ChatSerializer", "a", null, json).get();
				for (Player pl : players) {
					Reflection.sendNmsPacket(pl, "network.protocol.game.PacketPlayOutChat", component, messageTypeEnum.valueOf("CHAT").get(), pl.getUniqueId());
				}
			}).orIf(Version.ATLEAST_1_16, (players, json) -> {
				ReflectionEnum messageTypeEnum = Reflection.getNmsEnum("ChatMessageType");
				Object component = Reflection.invokeNmsMethod("IChatBaseComponent$ChatSerializer", "a", null, json).get();
				for (Player pl : players) {
					Reflection.sendNmsPacket(pl, "PacketPlayOutChat", component, messageTypeEnum.valueOf("CHAT").get(), pl.getUniqueId());
				}
			}).orElse((players, json) -> {
				Object component = Reflection.invokeNmsMethod("IChatBaseComponent$ChatSerializer", "a", null, json).get();
				Reflection.sendNmsPacket(players, "PacketPlayOutChat", component);
			});

	public static void sendJsonChat(Collection<Player> players, String json) {
		SEND_JSON_CHAT.process(players, json);
	}

	// ----- change tab
	private static final ReflectionProcedureTriConsumer<Player, String, String> CHANGE_TAB = new ReflectionProcedureTriConsumer<Player, String, String>()
			.setIf(Version.ATLEAST_1_17, (player, header, footer) -> {
				Reflection.sendNmsPacket(player, Reflection.newNmsInstance("network.protocol.game.PacketPlayOutPlayerListHeaderFooter")
						.setField("a", createChatComponent(header).get()).setField("b", createChatComponent(footer).get()).get());
			}).orElse((player, header, footer) -> {
				Reflection.sendNmsPacket(player,
						Reflection.newNmsInstance("PacketPlayOutPlayerListHeaderFooter")
								.setField(Version.ATLEAST_1_13 ? "header" : "a", createChatComponent(header).get())
								.setField(Version.ATLEAST_1_13 ? "footer" : "b", createChatComponent(footer).get()).get());
			});

	public static void changeTab(Player player, String header, String footer) {
		CHANGE_TAB.process(player, header, footer);
	}

	// ----- set durability
	private static final ReflectionProcedureBiFunction<ItemStack, Integer, ItemStack> SET_DURABILITY = new ReflectionProcedureBiFunction<ItemStack, Integer, ItemStack>()
			.setIf(Version.ATLEAST_1_14, (item, durability) -> {
				ItemMeta meta = item.getItemMeta();
				org.bukkit.inventory.meta.Damageable damageable = ObjectUtils.castOrNull(meta, org.bukkit.inventory.meta.Damageable.class);
				if (damageable == null) {
					return item;
				}
				damageable.setDamage(durability);
				item.setItemMeta(meta);
				return item;
			}).orElse((item, durability) -> {
				ReflectionObject.of(item).invokeMethod("setDurability", durability.shortValue());
				return item;
			});

	public static ItemStack setDurability(ItemStack item, int durability) {
		return SET_DURABILITY.process(item, durability);
	}

	// ----- set data
	private static final ReflectionProcedureBiFunction<ItemStack, Integer, ItemStack> SET_LEGACY_DATA = new ReflectionProcedureBiFunction<ItemStack, Integer, ItemStack>()
			.setIf(Version.ATLEAST_1_13, (item, durability) -> item).orElse((item, data) -> {
				ReflectionObject reflectionItem = ReflectionObject.of(item);
				reflectionItem.invokeMethod("setData", reflectionItem.invokeMethod("getData").invokeVoidMethod("setData", data.byteValue()));
				return item;
			});

	public static ItemStack setLegacyData(ItemStack item, int durability) {
		return SET_LEGACY_DATA.process(item, durability);
	}

	// ----- get durability
	private static final ReflectionProcedureFunction<ItemStack, Integer> GET_DURABILITY = new ReflectionProcedureFunction<ItemStack, Integer>()
			.setIf(Version.ATLEAST_1_13, (item) -> {
				ItemMeta meta = item.getItemMeta();
				org.bukkit.inventory.meta.Damageable damageable = ObjectUtils.castOrNull(meta, org.bukkit.inventory.meta.Damageable.class);
				return damageable == null ? 0 : damageable.getDamage();
			}).orElse((item) -> (int) ReflectionObject.of(item).invokeMethod("getDurability").get(short.class));

	public static int getDurability(ItemStack item) {
		return GET_DURABILITY.process(item);
	}

	// ----- get legacy data
	private static final ReflectionProcedureFunction<ItemStack, Integer> GET_LEGACY_DATA = new ReflectionProcedureFunction<ItemStack, Integer>()
			.setIf(Version.ATLEAST_1_13, (item) -> 0)
			.orElse((item) -> (int) ReflectionObject.of(item).invokeMethod("getData").invokeMethod("getData").get(byte.class));

	public static int getLegacyData(ItemStack item) {
		return GET_LEGACY_DATA.process(item);
	}

	private static final ReflectionProcedureFunction<Block, Integer> GET_LEGACY_DATA_BLOCK = new ReflectionProcedureFunction<Block, Integer>()
			.setIf(Version.ATLEAST_1_13, block -> 0).orElse(block -> (int) ReflectionObject.of(block).invokeMethod("getData").get(byte.class));

	public static int getLegacyData(Block block) {
		return GET_LEGACY_DATA_BLOCK.process(block);
	}

	private static final ReflectionProcedureFunction<BlockState, Integer> GET_LEGACY_DATA_BLOCK_STATE = new ReflectionProcedureFunction<BlockState, Integer>()
			.setIf(Version.ATLEAST_1_13, block -> 0).orElse(block -> (int) ReflectionObject.of(block).invokeMethod("getRawData").get(byte.class));

	public static int getLegacyData(BlockState block) {
		return GET_LEGACY_DATA_BLOCK_STATE.process(block);
	}

	// ----- add item flags
	private static final ReflectionProcedureBiConsumer<ItemMeta, Collection<ItemFlag>> ADD_ITEM_FLAGS = new ReflectionProcedureBiConsumer<ItemMeta, Collection<ItemFlag>>()
			.setIf(Version.ATLEAST_1_8, (meta, flags) -> {
				List<org.bukkit.inventory.ItemFlag> list = flags.stream().map(flag -> ObjectUtils.safeValueOf(flag.name(), org.bukkit.inventory.ItemFlag.class))
						.filter(obj -> obj != null).collect(Collectors.toList());
				meta.addItemFlags(list.toArray(new org.bukkit.inventory.ItemFlag[list.size()]));
			});

	public static void addItemFlags(ItemMeta meta, ItemFlag... flags) {
		addItemFlags(meta, CollectionUtils.asList(flags));
	}

	public static void addItemFlags(ItemMeta meta, Collection<ItemFlag> flags) {
		ADD_ITEM_FLAGS.process(meta, flags);
	}

	// ----- get item flags
	private static final ReflectionProcedureFunction<ItemMeta, List<ItemFlag>> GET_ITEM_FLAGS = new ReflectionProcedureFunction<ItemMeta, List<ItemFlag>>()
			.setIf(Version.ATLEAST_1_7_10, meta -> {
				List<ItemFlag> result = new ArrayList<>();
				for (Object object : ((Collection) ReflectionObject.of(meta).invokeMethod("getItemFlags").get())) {
					ItemFlag flag = ObjectUtils.safeValueOf(String.valueOf(object), ItemFlag.class);
					if (flag != null) {
						result.add(flag);
					}
				}
				return result;
			});

	public static List<ItemFlag> getItemFlags(ItemMeta meta) {
		return GET_ITEM_FLAGS.process(meta, new ArrayList<>());
	}

	// ----- set unbreakable
	private static final ReflectionProcedureBiConsumer<ItemMeta, Boolean> SET_UNBREAKABLE = new ReflectionProcedureBiConsumer<ItemMeta, Boolean>()
			.setIf(Version.ATLEAST_1_7_10, (meta, unbreakable) -> {
				ReflectionObject.of(meta).invokeMethod("spigot").invokeMethod("setUnbreakable", unbreakable.booleanValue());
			}).setIf(Version.ATLEAST_1_11, (meta, unbreakable) -> {
				ReflectionObject.of(meta).invokeMethod("setUnbreakable", unbreakable);
			});

	public static void setUnbreakable(ItemMeta meta, boolean unbreakable) {
		SET_UNBREAKABLE.process(meta, unbreakable);
	}

	// ----- get unbreakable
	private static final ReflectionProcedureFunction<ItemMeta, Boolean> IS_UNBREAKABLE = new ReflectionProcedureFunction<ItemMeta, Boolean>()
			.setIf(Version.ATLEAST_1_7_10, (meta) -> {
				return ReflectionObject.of(meta).invokeMethod("spigot").invokeMethod("isUnbreakable").get();
			}).setIf(Version.ATLEAST_1_11, (meta) -> {
				return ReflectionObject.of(meta).invokeMethod("isUnbreakable").get();
			});

	public static boolean isUnbreakable(ItemMeta meta) {
		return IS_UNBREAKABLE.process(meta, false);
	}

}
