package com.guillaumevdn.gcore.lib.versioncompat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.Multimap;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.util.Utils;

import net.minecraft.server.v1_13_R2.ChatMessageType;

public class Compat1_13_1 extends Compat {

	@Override
	public void sendTitle(Player player, String title, String subtitle, int fadeIn, int duration, int fadeOut) {
		if (subtitle != null || title != null) {
			net.minecraft.server.v1_13_R2.PlayerConnection connection = ((org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer) player).getHandle().playerConnection;
			net.minecraft.server.v1_13_R2.PacketPlayOutTitle packetPlayOutTimes = new net.minecraft.server.v1_13_R2.PacketPlayOutTitle(
					net.minecraft.server.v1_13_R2.PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, duration, fadeOut);
			connection.sendPacket(packetPlayOutTimes);

			if (subtitle != null) {
				net.minecraft.server.v1_13_R2.IChatBaseComponent titleSub = net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
				net.minecraft.server.v1_13_R2.PacketPlayOutTitle packetPlayOutSubTitle = new net.minecraft.server.v1_13_R2.PacketPlayOutTitle(
						net.minecraft.server.v1_13_R2.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleSub);
				connection.sendPacket(packetPlayOutSubTitle);
			}

			if (title != null) {
				net.minecraft.server.v1_13_R2.IChatBaseComponent titleMain = net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
				net.minecraft.server.v1_13_R2.PacketPlayOutTitle packetPlayOutTitle = new net.minecraft.server.v1_13_R2.PacketPlayOutTitle(
						net.minecraft.server.v1_13_R2.PacketPlayOutTitle.EnumTitleAction.TITLE, titleMain);
				connection.sendPacket(packetPlayOutTitle);
			}
		}
	}

	@Override
	public void sendActionBar(Player player, String message) {
		net.minecraft.server.v1_13_R2.IChatBaseComponent actionbar = net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
		net.minecraft.server.v1_13_R2.PacketPlayOutChat actionbarPacket = new net.minecraft.server.v1_13_R2.PacketPlayOutChat(actionbar, ChatMessageType.GAME_INFO);
		((org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(actionbarPacket);

	}

	@Override
	public void changeTab(Player player, String head, String foot) {
		net.minecraft.server.v1_13_R2.IChatBaseComponent header = net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + head + "\"}");
		net.minecraft.server.v1_13_R2.IChatBaseComponent footer = net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + foot + "\"}");
		net.minecraft.server.v1_13_R2.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_13_R2.PacketPlayOutPlayerListHeaderFooter();

		try {
			Field headerField = packet.getClass().getDeclaredField("header");
			headerField.setAccessible(true);
			headerField.set(packet, header);
			headerField.setAccessible(!headerField.isAccessible());

			Field footerField = packet.getClass().getDeclaredField("footer");
			footerField.setAccessible(true);
			footerField.set(packet, footer);
			footerField.setAccessible(!footerField.isAccessible());

			((org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		} catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
			exception.printStackTrace();
		}
	}

	private final List<ItemFlag> itemFlags = Utils.asList(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

	@Override
	public ItemMeta addItemFlags(ItemMeta meta) {
		for (ItemFlag flag : itemFlags) {
			meta.addItemFlags(flag);	
		}
		return meta;
	}

	@Override
	public Mat getArmorStandHelmetType(Entity armorStand) {
		return armorStand instanceof ArmorStand ? Mat.fromItem(((ArmorStand) armorStand).getHelmet()) : null;
	}

	@Override
	public Score getScore(Objective objective, String name) {
		return objective.getScore(name);
	}

	// https://bukkit.org/threads/serializing-a-nbt-tag.404362/
	// https://stackoverflow.com/questions/1536054/how-to-convert-byte-array-to-string-and-vice-versa
	// https://www.spigotmc.org/threads/help-help-needed-with-adding-nbt-tags-to-items.230498/
	// https://www.spigotmc.org/threads/converting-itemstacks-to-strings-and-back.334600/ (mojangson for custom NBT string)

	@Override
	public Object parseMojangson(String serialized) throws Throwable {
		return net.minecraft.server.v1_13_R2.MojangsonParser.parse(serialized);
	}

	@Override
	public String serializeNbt(Object nbt) throws IOException {
		if (Utils.instanceOf(nbt, net.minecraft.server.v1_13_R2.NBTTagCompound.class)) {
			// convert to byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			net.minecraft.server.v1_13_R2.NBTCompressedStreamTools.a((net.minecraft.server.v1_13_R2.NBTTagCompound) nbt, baos);
			// return base64 string
			return Base64.encodeBase64String(baos.toByteArray());
		}
		return null;
	}

	@Override
	public Object unserializeNbt(String serialized) throws IOException {
		if (serialized != null) {
			// get the byte array
			byte[] ba = Base64.decodeBase64(serialized);
			// convert to nbt
			ByteArrayInputStream bais = new ByteArrayInputStream(ba);
			return net.minecraft.server.v1_13_R2.NBTCompressedStreamTools.a(bais);
		}
		return null;
	}

	@Override
	public Object getNbt(ItemStack item) {
		if (item != null) {
			net.minecraft.server.v1_13_R2.ItemStack itemNms = org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asNMSCopy(item);
			if (itemNms.hasTag() && !itemNms.getTag().isEmpty()) {
				return itemNms.getTag();
			}
		}
		return null;
	}

	@Override
	public ItemStack setNbt(ItemStack item, Object nbt) {
		if (item != null) {
			net.minecraft.server.v1_13_R2.ItemStack itemNms = org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asNMSCopy(item);
			itemNms.setTag((net.minecraft.server.v1_13_R2.NBTTagCompound) (nbt));
			// return item
			return org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asBukkitCopy(itemNms);
		}
		return null;
	}

	@Override
	public void setScoreboardTeamNameTags(Team team, String prefix, String suffix) {
		team.setPrefix(prefix);
		team.setSuffix(suffix);
		team.setNameTagVisibility(org.bukkit.scoreboard.NameTagVisibility.ALWAYS);
	}

	@Override
	public Enchantment getEnchantment(String raw) {
		if (Utils.isInteger(raw)) {
			GCore.inst().error("Trying to get enchantment from id '" + raw + "', but ids are not supported since 1.13");
			return null;
		} else {
			return Enchantment.getByName(raw.toUpperCase());
		}
	}

	@Override
	public double getAttackDamage(ItemStack item) {
		double attackDamage = 1.0;
		UUID uuid = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack.asNMSCopy(item);
		net.minecraft.server.v1_13_R2.Item nmsItem = nmsStack.getItem();
		if (nmsItem instanceof net.minecraft.server.v1_13_R2.ItemSword || nmsItem instanceof net.minecraft.server.v1_13_R2.ItemTool || nmsItem instanceof net.minecraft.server.v1_13_R2.ItemHoe) {
			Multimap<String,  net.minecraft.server.v1_13_R2.AttributeModifier> map = nmsItem.a(net.minecraft.server.v1_13_R2.EnumItemSlot.MAINHAND);
			Collection<net.minecraft.server.v1_13_R2.AttributeModifier> attributes = map.get(net.minecraft.server.v1_13_R2.GenericAttributes.ATTACK_DAMAGE.getName());
			if(!attributes.isEmpty()) {
				for (net.minecraft.server.v1_13_R2.AttributeModifier am : attributes) {
					if (am.a().toString().equalsIgnoreCase(uuid.toString()) && am.c() == 0) {
						attackDamage += am.d();
					}
				}
				double y = 1;
				for (net.minecraft.server.v1_13_R2.AttributeModifier am : attributes) {
					if(am.a().toString().equalsIgnoreCase(uuid.toString()) && am.c() == 1) {
						y += am.d();
					}
				}
				attackDamage *= y;
				for(net.minecraft.server.v1_13_R2.AttributeModifier am : attributes) {
					if(am.a().toString().equalsIgnoreCase(uuid.toString()) && am.c() == 2) {
						attackDamage *= (1 + am.d());
					}
				}
			}
		}
		return attackDamage;
	}

}
