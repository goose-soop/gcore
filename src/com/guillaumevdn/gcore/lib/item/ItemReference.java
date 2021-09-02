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
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public interface ItemReference {

	boolean isVoid();
	Mat getType();
	int getDurability();
	int getCustomModelData();
	boolean isUnbreakable();
	boolean hasEnchants();
	Map<Enchantment, Integer> getEnchants();
	List<ItemFlag> getFlags();

	String getDisplayName();
	List<String> getLore();

	NBTItem getNBTItem() throws Throwable;

	boolean hasMeta(Class<? extends ItemMeta> metaClazz);

	// ----- BookMeta

	boolean hasAuthor();
	String getAuthor();

	boolean hasTitle();
	String getTitle();

	boolean hasGeneration();
	<T> T getGeneration();

	boolean hasPages();
	List<String> getPages();

	// ----- EnchantmentStorageMeta

	boolean hasStoredEnchants();
	Map<Enchantment, Integer> getStoredEnchants();

	// ----- FireworkEffectMeta

	boolean hasEffect();
	FireworkEffect getEffect();

	// ----- FireworkMeta

	boolean hasEffects();
	List<FireworkEffect> getEffects();

	// ----- LeatherArmorMeta

	Color getArmorColor();

	// ----- PotionMeta

	<T> T getBasePotionData();
	Color getPotionColor();
	boolean hasPotionCustomEffects();
	List<PotionEffect> getPotionCustomEffects();

	// ----- SkullMeta

	boolean hasOwner();
	String getOwner();

	// ----- org.bukkit.inventory.meta.BannerMeta

	DyeColor getBaseColor();
	<T> T getPatterns();

	// ----- SpawnEggMeta

	EntityType getSpawnedType();

	// ----- org.bukkit.inventory.meta.KnowledgeBookMeta

	boolean hasRecipes();
	<T> T getRecipes();

	// ----- org.bukkit.inventory.meta.TropicalFishBucketMeta

	boolean hasVariant();
	DyeColor getBodyColor();
	DyeColor getPatternColor();
	<T> T getPattern();

	// ----- org.bukkit.inventory.meta.CrossbowMeta

	boolean hasChargedProjectiles();
	List<ItemStack> getChargedProjectiles();

	// ----- org.bukkit.inventory.meta.SuspiciousStewMeta

	boolean hasSuspiciousCustomEffects();
	List<PotionEffect> getSuspiciousCustomEffects();

	// ----- static

	static ItemReference of(ItemStack item) {
		return new ItemReferenceVanilla(item);
	}

	static ItemReference of(ElementItem element, Replacer replacer) {
		if (!element.getType().readContains()) {
			return new ItemReferenceElementNoType(element, replacer);
		}
		return of(element.parse(replacer).orNull());  // if type was written in config, MUST be valid configuration
	}

}
