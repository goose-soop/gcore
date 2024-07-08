package com.guillaumevdn.gcore.lib.item;

import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.potion.PotionEffect;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.compatibility.Compat;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTItem;
import com.guillaumevdn.gcore.lib.gui.ItemFlag;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;

/**
 * @author GuillaumeVDN
 */
final class ItemReferenceVanilla implements ItemReference {

	private ItemStack item;
	private ItemMeta meta;

	public ItemReferenceVanilla(ItemStack item) {
		this.item = item;
		this.meta = item.hasItemMeta() ? item.getItemMeta() : null;
	}

	@Override
	public boolean isVoid() {
		return Mat.isVoid(item);
	}

	@Override
	public Mat getType() {
		return Mat.fromItem(item).orElse(null);
	}

	@Override
	public int getDurability() {
		return Compat.getDurability(item);
	}

	@Override
	public int getCustomModelData() {
		return meta != null && meta.hasCustomModelData() ? meta.getCustomModelData() : 0;
	}

	@Override
	public boolean isUnbreakable() {
		return meta != null && Compat.isUnbreakable(meta);
	}

	@Override
	public boolean hasEnchants() {
		return meta != null && meta.hasEnchants();
	}

	@Override
	public Map<Enchantment, Integer> getEnchants() {
		return meta != null ? meta.getEnchants() : ItemUtils.EMPTY_ENCHANTS;
	}

	@Override
	public List<ItemFlag> getFlags() {
		return meta != null ? Compat.getItemFlags(meta) : ItemUtils.EMPTY_FLAGS;
	}

	@Override
	public String getDisplayName() {
		return meta == null ? null : (!meta.hasDisplayName() ? null : meta.getDisplayName());
	}

	@Override
	public List<String> getLore() {
		return meta == null ? ItemUtils.EMPTY_LORE : (!meta.hasLore() ? ItemUtils.EMPTY_LORE : meta.getLore());
	}

	@Override
	public NBTItem getNBTItem() throws Throwable {
		return new NBTItem(item);
	}

	@Override
	public boolean hasMeta(Class<? extends ItemMeta> metaClass) {
		return ObjectUtils.castOrNull(meta, metaClass) != null;
	}

	// ----- BookMeta

	@Override
	public boolean hasAuthor() {
		return ((BookMeta) this.meta).hasAuthor();
	}

	@Override
	public String getAuthor() {
		return ((BookMeta) this.meta).getAuthor();
	}

	@Override
	public boolean hasTitle() {
		return ((BookMeta) this.meta).hasTitle();
	}

	@Override
	public String getTitle() {
		return ((BookMeta) this.meta).getTitle();
	}

	@Override
	public boolean hasGeneration() {
		return ((BookMeta) this.meta).hasGeneration();
	}

	@Override
	public Object getGeneration() {
		return ((BookMeta) this.meta).getGeneration();
	}

	@Override
	public boolean hasPages() {
		return ((BookMeta) this.meta).hasPages();
	}

	@Override
	public List<String> getPages() {
		return ((BookMeta) this.meta).getPages();
	}

	// ----- EnchantmentStorageMeta

	@Override
	public boolean hasStoredEnchants() {
		return ((EnchantmentStorageMeta) this.meta).hasStoredEnchants();
	}

	@Override
	public Map<Enchantment, Integer> getStoredEnchants() {
		return ((EnchantmentStorageMeta) this.meta).getStoredEnchants();
	}

	// ----- FireworkEffectMeta

	@Override
	public boolean hasEffect() {
		return ((FireworkEffectMeta) this.meta).hasEffect();
	}

	@Override
	public FireworkEffect getEffect() {
		return ((FireworkEffectMeta) this.meta).getEffect();
	}

	// ----- FireworkMeta

	@Override
	public boolean hasEffects() {
		return ((FireworkMeta) this.meta).hasEffects();
	}

	@Override
	public List<FireworkEffect> getEffects() {
		return ((FireworkMeta) this.meta).getEffects();
	}

	// ----- LeatherArmorMeta

	@Override
	public Color getArmorColor() {
		return ((LeatherArmorMeta) this.meta).getColor();
	}

	// ----- PotionMeta

	@Override
	public Object getBasePotionDataOrType() {
		if (Version.ATLEAST_1_20_5) {
			return ((PotionMeta) this.meta).getBasePotionType();
		}
		try {
			return ReflectionObject.of((PotionMeta) this.meta).invokeMethod("getBasePotionData").get();
		} catch (Throwable exception) {
			GCore.inst().getMainLogger().error("Could not get base potion data", exception);
			return null;
		}
	}

	@Override
	public Color getPotionColor() {
		return ((PotionMeta) this.meta).getColor();
	}

	@Override
	public boolean hasPotionCustomEffects() {
		return ((PotionMeta) this.meta).hasCustomEffects();
	}

	@Override
	public List<PotionEffect> getPotionCustomEffects() {
		return ((PotionMeta) this.meta).getCustomEffects();
	}

	// ----- SkullMeta

	@Override
	public boolean hasOwner() {
		return ((SkullMeta) this.meta).hasOwner();
	}

	@Override
	public String getOwner() {
		return ((SkullMeta) this.meta).getOwner();
	}

	// ----- org.bukkit.inventory.meta.BannerMeta

	@Override
	public DyeColor getBaseColor() {
		if (Version.ATLEAST_1_20_5) {
			return null;
		}
		try {
			return ReflectionObject.of((org.bukkit.inventory.meta.BannerMeta) this.meta).invokeMethod("getBaseColor").get();
		} catch (Throwable exception) {
			GCore.inst().getMainLogger().error("Could not get base color", exception);
			return null;
		}
	}

	@Override
	public Object getPatterns() {
		return ((org.bukkit.inventory.meta.BannerMeta) this.meta).getPatterns();
	}

	// ----- SpawnEggMeta

	@Override
	public EntityType getSpawnedType() {
		return ((SpawnEggMeta) this.meta).getSpawnedType();
	}

	// ----- org.bukkit.inventory.meta.KnowledgeBookMeta

	@Override
	public boolean hasRecipes() {
		return ((org.bukkit.inventory.meta.KnowledgeBookMeta) this.meta).hasRecipes();
	}

	@Override
	public Object getRecipes() {
		return ((org.bukkit.inventory.meta.KnowledgeBookMeta) this.meta).getRecipes();
	}

	// ----- org.bukkit.inventory.meta.TropicalFishBucketMeta

	@Override
	public boolean hasVariant() {
		return ((org.bukkit.inventory.meta.TropicalFishBucketMeta) this.meta).hasVariant();
	}

	@Override
	public DyeColor getBodyColor() {
		return ((org.bukkit.inventory.meta.TropicalFishBucketMeta) this.meta).getBodyColor();
	}

	@Override
	public DyeColor getPatternColor() {
		return ((org.bukkit.inventory.meta.TropicalFishBucketMeta) this.meta).getPatternColor();
	}

	@Override
	public Object getPattern() {
		return ((org.bukkit.inventory.meta.TropicalFishBucketMeta) this.meta).getPattern();
	}

	// ----- org.bukkit.inventory.meta.CrossbowMeta

	@Override
	public boolean hasChargedProjectiles() {
		return ((org.bukkit.inventory.meta.CrossbowMeta) this.meta).hasChargedProjectiles();
	}

	@Override
	public List<ItemStack> getChargedProjectiles() {
		return ((org.bukkit.inventory.meta.CrossbowMeta) this.meta).getChargedProjectiles();
	}

	// ----- org.bukkit.inventory.meta.SuspiciousStewMeta

	@Override
	public boolean hasSuspiciousCustomEffects() {
		return ((org.bukkit.inventory.meta.SuspiciousStewMeta) this.meta).hasCustomEffects();
	}

	@Override
	public List<PotionEffect> getSuspiciousCustomEffects() {
		return ((org.bukkit.inventory.meta.SuspiciousStewMeta) this.meta).getCustomEffects();
	}

}
