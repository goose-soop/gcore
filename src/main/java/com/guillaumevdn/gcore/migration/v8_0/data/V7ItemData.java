package com.guillaumevdn.gcore.migration.v8_0.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTCompound;
import com.guillaumevdn.gcore.lib.gui.ItemFlag;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterItemStack;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterNBTCompound;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;
import com.guillaumevdn.gcore.migration.v8_0.config.MigrationV8Config;

public class V7ItemData {

	// ----- fields
	public Mat type;
	public int durability = 0;
	public int amount = 1;
	public List<PotionEffect> effects;
	public Map<Enchantment, Integer> enchants;
	public String name;
	public List<String> lore;
	public String customNbt;
	public boolean unbreakable = false;
	public boolean hideFlags = false;

	// ----- methods
	public ItemStack toItemStack() throws Throwable {
		DataIO data = new DataIO();
		data.write("version", 1);
		data.write("type", type.getId());
		if (durability != 0) {
			data.write("durability", durability);
		}
		if (amount != 1) {
			data.write("amount", amount);
		}
		if (unbreakable) {
			data.write("unbreakable", true);
		}
		if (enchants != null) {
			data.writeObject("enchantments", w -> enchants.forEach((enchantment, level) -> w.write(enchantment.getName(), level)));
		}
		if (hideFlags) {
			data.writeSerializedList("flags", CollectionUtils.asList(ItemFlag.values()));
		}
		if (name != null) {
			data.write("name", name);
		}
		if (lore != null) {
			data.writeSerializedList("lore", lore);
		}
		if (effects != null) {
			data.writeSerializedList("customEffects", effects);
		}
		if (customNbt != null) {
			try {
				DataIO nbtWriter = new DataIO();
				AdapterNBTCompound.INSTANCE.write(new NBTCompound(null, "root", 0,
						(customNbt.toLowerCase().startsWith("custom:") ? MigrationV8Config.PARSE_MOJANGSON : MigrationV8Config.UNSERIALIZE_NBT)
								.process(customNbt)),
						nbtWriter);
			} catch (Throwable exception) {
				GCore.inst().getMainLogger().error("Couldn't migrate item nbt '" + customNbt + "', skipping", exception);
			}
		}
		return AdapterItemStack.INSTANCE.read(data);

	}

	// ----- serialization
	public static class GsonAdapter extends TypeAdapter<V7ItemData> {

		@Override
		public V7ItemData read(JsonReader reader) throws IOException {
			// null
			if (reader.peek().equals(JsonToken.NULL)) {
				reader.nextNull();
				return null;
			}
			// object
			if (reader.peek().equals(JsonToken.BEGIN_OBJECT)) {
				reader.beginObject();
				V7ItemData item = new V7ItemData();
				while (reader.peek().equals(JsonToken.NAME)) {
					String name = reader.nextName();
					if (name.equalsIgnoreCase("hideFlags")) {
						item.hideFlags = reader.nextBoolean();
					} else if (name.equalsIgnoreCase("type")) {
						String[] split = reader.nextString().split(",", -1);
						Mat type = Mat.firstFromIdOrDataName(split[0]).orElse(null);
						if (type != null && type.canHaveItem()) {
							Integer matDura = NumberUtils.integerOrNull(split[1]);
							if (matDura != null && matDura != 0) {
								item.durability = matDura;
							}
							item.type = type;
						}
					} else if (name.equalsIgnoreCase("unbreakable")) {
						item.unbreakable = reader.nextBoolean();
					} else if (name.equalsIgnoreCase("amount")) {
						item.amount = reader.nextInt();
					} else if (name.equalsIgnoreCase("effects")) {
						reader.beginArray();
						while (reader.peek().equals(JsonToken.STRING)) {
							String[] split = reader.nextString().split(",", -1);
							PotionEffectType type = ObjectUtils.potionEffectTypeOrNull(split[0]);
							Integer amplifier = NumberUtils.integerOrNull(split[1]);
							Integer duration = NumberUtils.integerOrNull(split[2]);
							if (type != null && amplifier != null && duration != null) {
								item.effects.add(new PotionEffect(type, amplifier, duration));
							}
						}
						reader.endArray();
					} else if (name.equalsIgnoreCase("enchants")) {
						reader.beginArray();
						while (reader.peek().equals(JsonToken.STRING)) {
							String[] split = reader.nextString().split(",", -1);
							Enchantment type = ObjectUtils.enchantmentOrNull(split[0]);
							Integer level = NumberUtils.integerOrNull(split[1]);
							if (type != null && level != null) {
								item.enchants.put(type, level);
							}
						}
						reader.endArray();
					} else if (name.equalsIgnoreCase("name")) {
						item.name = reader.nextString();
					} else if (name.equalsIgnoreCase("lore")) {
						reader.beginArray();
						List<String> lore = new ArrayList<>();
						while (reader.peek().equals(JsonToken.STRING)) {
							lore.add(reader.nextString());
						}
						if (!lore.isEmpty()) {
							item.lore = lore;
						}
						reader.endArray();
					} else if (name.equalsIgnoreCase("customNbt")) {
						item.customNbt = reader.nextString();
					} else if (name.equalsIgnoreCase("id")) {
						reader.nextString();
					} else if (name.equalsIgnoreCase("enabled")) {
						reader.nextBoolean();
					} else if (name.equalsIgnoreCase("slot")) {
						reader.nextInt();
					} else if (name.equalsIgnoreCase("chance")) {
						reader.nextInt();
					} else if (name.equalsIgnoreCase("maxAmount")) {
						reader.nextInt();
					}
				}
				reader.endObject();
				return item.type == null ? null : item;
			}
			// string
			if (reader.peek().equals(JsonToken.STRING)) {
				OldDataWrapper wrapper = GCore.inst().getGson().fromJson(reader.nextString(), OldDataWrapper.class);
				return wrapper != null ? wrapper.toItemData() : null;
			}
			// huh
			return null;
		}

		@Override
		public void write(JsonWriter writer, V7ItemData item) throws IOException {
			throw new UnsupportedOperationException();
		}

	}

	private static class OldDataWrapper {

		private Mat type = null;
		private Integer amount = null;
		private List<PotionEffect> effects = null;
		private Map<Enchantment, Integer> enchants = null;
		private String name = null;
		private List<String> lore = null;
		private String customNbt = null;
		private Boolean unbreakable = null;
		private Boolean hideFlags = null;

		private OldDataWrapper(V7ItemData item) {
			if (item.type != null)
				this.type = item.type;
			if (item.amount != 1)
				this.amount = item.amount;
			if (item.effects != null && !item.effects.isEmpty())
				this.effects = item.effects;
			if (item.enchants != null && !item.enchants.isEmpty())
				this.enchants = item.enchants;
			if (item.name != null)
				this.name = item.name;
			if (item.lore != null && !item.lore.isEmpty())
				this.lore = item.lore;
			if (item.customNbt != null)
				this.customNbt = item.customNbt;
			if (item.unbreakable)
				this.unbreakable = true;
			if (item.hideFlags)
				this.hideFlags = true;
		}

		private V7ItemData toItemData() {
			V7ItemData item = new V7ItemData();
			if (this.type != null)
				item.type = this.type;
			if (this.amount != null && this.amount != 1)
				item.amount = this.amount;
			if (this.effects != null && !this.effects.isEmpty())
				item.effects = this.effects;
			if (this.enchants != null && !this.enchants.isEmpty())
				item.enchants = this.enchants;
			if (this.name != null)
				item.name = this.name;
			if (this.lore != null && !this.lore.isEmpty())
				item.lore = this.lore;
			if (this.customNbt != null)
				item.customNbt = this.customNbt;
			if (this.unbreakable != null && this.unbreakable)
				item.unbreakable = true;
			if (this.hideFlags != null && this.hideFlags)
				item.hideFlags = true;
			return item;
		}

	}

}
