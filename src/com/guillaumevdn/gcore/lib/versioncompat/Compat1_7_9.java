package com.guillaumevdn.gcore.lib.versioncompat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.Multimap;
import com.guillaumevdn.gcore.lib.util.Utils;

public class Compat1_7_9 extends Compat {

	@Override
	public Score getScore(Objective objective, String name) {
		return objective.getScore(Bukkit.getOfflinePlayer(name));
	}

	// https://bukkit.org/threads/serializing-a-nbt-tag.404362/
	// https://stackoverflow.com/questions/1536054/how-to-convert-byte-array-to-string-and-vice-versa
	// https://www.spigotmc.org/threads/help-help-needed-with-adding-nbt-tags-to-items.230498/
	// https://www.spigotmc.org/threads/converting-itemstacks-to-strings-and-back.334600/ (mojangson for custom NBT string)

	@Override
	public Object parseMojangson(String serialized) throws Throwable {
		return net.minecraft.server.v1_7_R3.MojangsonParser.parse(serialized);
	}

	@Override
	public String serializeNbt(Object nbt) throws IOException {
		if (Utils.instanceOf(nbt, net.minecraft.server.v1_7_R3.NBTTagCompound.class)) {
			// convert to byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			net.minecraft.server.v1_7_R3.NBTCompressedStreamTools.a((net.minecraft.server.v1_7_R3.NBTTagCompound) nbt, baos);
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
			return net.minecraft.server.v1_7_R3.NBTCompressedStreamTools.a(bais);
		}
		return null;
	}

	@Override
	public Object getNbt(ItemStack item) {
		if (item != null) {
			net.minecraft.server.v1_7_R3.ItemStack itemNms = org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack.asNMSCopy(item);
			if (itemNms.hasTag() && !itemNms.getTag().isEmpty()) {
				return itemNms.getTag();
			}
		}
		return null;
	}

	@Override
	public ItemStack setNbt(ItemStack item, Object nbt) {
		if (item != null) {
			net.minecraft.server.v1_7_R3.ItemStack itemNms = org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack.asNMSCopy(item);
			itemNms.setTag((net.minecraft.server.v1_7_R3.NBTTagCompound) (nbt == null ? null : nbt));
			// return item
			return org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack.asBukkitCopy(itemNms);
		}
		return null;
	}

	@Override
	public void setScoreboardTeamNameTags(Team team, String prefix, String suffix) {
		team.setPrefix(prefix);
		team.setSuffix(suffix);
		team.setDisplayName(prefix + " " + suffix);
	}

	@Override
	public Enchantment getEnchantment(String raw) {
		return Utils.isInteger(raw) ? Enchantment.getById(Integer.parseInt(raw)) : Enchantment.getByName(raw.toUpperCase());
	}

	@Override
	public double getAttackDamage(ItemStack item) {
		double attackDamage = 1.0;
		UUID uuid = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
		net.minecraft.server.v1_7_R3.ItemStack nmsStack = org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack.asNMSCopy(item);
		net.minecraft.server.v1_7_R3.Item nmsItem = nmsStack.getItem();
		if (nmsItem instanceof net.minecraft.server.v1_7_R3.ItemSword || nmsItem instanceof net.minecraft.server.v1_7_R3.ItemTool || nmsItem instanceof net.minecraft.server.v1_7_R3.ItemHoe) {
			Multimap<String, net.minecraft.server.v1_7_R3.AttributeModifier> map = (Multimap<String, net.minecraft.server.v1_7_R3.AttributeModifier>) nmsItem.k();
			Collection<net.minecraft.server.v1_7_R3.AttributeModifier> attributes = map.get(net.minecraft.server.v1_7_R3.GenericAttributes.e.a());
			if(!attributes.isEmpty()) {
				for (net.minecraft.server.v1_7_R3.AttributeModifier am : attributes) {
					if (am.a().toString().equalsIgnoreCase(uuid.toString()) && am.c() == 0) {
						attackDamage += am.d();
					}
				}
				double y = 1;
				for (net.minecraft.server.v1_7_R3.AttributeModifier am : attributes) {
					if(am.a().toString().equalsIgnoreCase(uuid.toString()) && am.c() == 1) {
						y += am.d();
					}
				}
				attackDamage *= y;
				for(net.minecraft.server.v1_7_R3.AttributeModifier am : attributes) {
					if(am.a().toString().equalsIgnoreCase(uuid.toString()) && am.c() == 2) {
						attackDamage *= (1 + am.d());
					}
				}
			}
		}
		return attackDamage;
	}

}
