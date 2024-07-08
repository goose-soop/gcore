package com.guillaumevdn.gcore.lib.item;

import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTItem;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.gui.ItemFlag;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterItemStack;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
final class ItemReferenceElementNoType implements ItemReference {

	private ElementItem element;
	private Replacer replacer;

	public ItemReferenceElementNoType(ElementItem element, Replacer replacer) {
		this.element = element;
		this.replacer = replacer;
	}

	@Override
	public boolean isVoid() {
		return false;
	}

	@Override
	public Mat getType() {
		return element.getType().directParseOrNull(replacer);
	}

	@Override
	public int getDurability() {
		return element.getDurability().directParseOrElse(replacer, 0);
	}

	@Override
	public int getCustomModelData() {
		return element.getCustomModelData().directParseOrElse(replacer, 0);
	}

	@Override
	public boolean isUnbreakable() {
		return element.getUnbreakable().directParseOrElse(replacer, false);
	}

	@Override
	public boolean hasEnchants() {
		return !element.getEnchantments().isEmpty();
	}

	@Override
	public Map<Enchantment, Integer> getEnchants() {
		return element.getEnchantments().parse(replacer).orElse(ItemUtils.EMPTY_ENCHANTS);
	}

	@Override
	public List<ItemFlag> getFlags() {
		return element.getFlags().parse(replacer).orElse(ItemUtils.EMPTY_FLAGS);
	}

	@Override
	public String getDisplayName() {
		return element.getName().directParseOrNull(replacer);
	}

	@Override
	public List<String> getLore() {
		return element.getLore().directParseOrElse(replacer, ItemUtils.EMPTY_LORE);
	}

	private NBTItem nbt = null;

	@Override
	public NBTItem getNBTItem() throws Throwable {
		if (nbt == null) {
			DataIO data = new DataIO();  // dummy item with just NBT
			data.write("type", "STONE");
			if (element.getNbt().getValue() != null) {
				data.writeObject("nbt", element.getNbt().getValue().toIO(false, replacer));
			}
			ItemStack dummy = AdapterItemStack.INSTANCE.readCurrent(data);
			nbt = new NBTItem(dummy);
		}
		return nbt;
	}

	@Override
	public boolean hasMeta(Class<? extends ItemMeta> metaClass) {
		return false;  // no type, we can't know the meta and don't have the elements
	}

	// ----- BookMeta

	@Override
	public boolean hasAuthor() {
		return false;
	}

	@Override
	public String getAuthor() {
		return null;
	}

	@Override
	public boolean hasTitle() {
		return false;
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public boolean hasGeneration() {
		return false;
	}

	@Override
	public Object getGeneration() {
		return null;
	}

	@Override
	public boolean hasPages() {
		return false;
	}

	@Override
	public List<String> getPages() {
		return null;
	}

	// ----- EnchantmentStorageMeta

	@Override
	public boolean hasStoredEnchants() {
		return false;
	}

	@Override
	public Map<Enchantment, Integer> getStoredEnchants() {
		return ItemUtils.EMPTY_ENCHANTS;
	}

	// ----- FireworkEffectMeta

	@Override
	public boolean hasEffect() {
		return false;
	}

	@Override
	public FireworkEffect getEffect() {
		return null;
	}

	// ----- FireworkMeta

	@Override
	public boolean hasEffects() {
		return false;
	}

	@Override
	public List<FireworkEffect> getEffects() {
		return null;
	}

	// ----- LeatherArmorMeta

	@Override
	public Color getArmorColor() {
		return null;
	}

	// ----- PotionMeta

	@Override
	public Object getBasePotionDataOrType() {
		return null;
	}

	@Override
	public Color getPotionColor() {
		return null;
	}

	@Override
	public boolean hasPotionCustomEffects() {
		return false;
	}

	@Override
	public List<PotionEffect> getPotionCustomEffects() {
		return null;
	}

	// ----- SkullMeta

	@Override
	public boolean hasOwner() {
		return false;
	}

	@Override
	public String getOwner() {
		return null;
	}

	// ----- org.bukkit.inventory.meta.BannerMeta

	@Override
	public DyeColor getBaseColor() {
		return null;
	}

	@Override
	public Object getPatterns() {
		return null;
	}

	// ----- SpawnEggMeta

	@Override
	public EntityType getSpawnedType() {
		return null;
	}

	// ----- org.bukkit.inventory.meta.KnowledgeBookMeta

	@Override
	public boolean hasRecipes() {
		return false;
	}

	@Override
	public Object getRecipes() {
		return null;
	}

	// ----- org.bukkit.inventory.meta.TropicalFishBucketMeta

	@Override
	public boolean hasVariant() {
		return false;
	}

	@Override
	public DyeColor getBodyColor() {
		return null;
	}

	@Override
	public DyeColor getPatternColor() {
		return null;
	}

	@Override
	public Object getPattern() {
		return null;
	}

	// ----- org.bukkit.inventory.meta.CrossbowMeta

	@Override
	public boolean hasChargedProjectiles() {
		return false;
	}

	@Override
	public List<ItemStack> getChargedProjectiles() {
		return null;
	}

	// ----- org.bukkit.inventory.meta.SuspiciousStewMeta

	@Override
	public boolean hasSuspiciousCustomEffects() {
		return false;
	}

	@Override
	public List<PotionEffect> getSuspiciousCustomEffects() {
		return null;
	}

}
