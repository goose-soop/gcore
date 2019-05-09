package com.guillaumevdn.gcore.lib.parseable.container;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.gui.ItemData;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ContainerParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.PrimitiveParseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.list.LPEnchantment;
import com.guillaumevdn.gcore.lib.parseable.list.LPPotionEffect;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPBoolean;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPDouble;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPInteger;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPMat;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPString;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPStringList;
import com.guillaumevdn.gcore.lib.versioncompat.Compat;

public class CPItem extends ContainerParseable {

	// base
	private PPMat type = addComponent(new PPMat("type", this, Mat.AIR.toString(), isMandatory(), 0, EditorGUI.ICON_ITEM, GLocale.GUI_GENERIC_EDITOR_ITEM_TYPELORE.getLines()));
	private PPInteger durability = addComponent(new PPInteger("durability", this, "0", 0, Integer.MAX_VALUE, false, 1, EditorGUI.ICON_NUMBER, GLocale.GUI_GENERIC_EDITOR_ITEM_DURABILITYLORE.getLines()));
	private PPBoolean unbreakable = addComponent(new PPBoolean("unbreakable", this, "false", false, 2, EditorGUI.ICON_BOOLEAN, GLocale.GUI_GENERIC_EDITOR_ITEM_UNBREAKABLELORE.getLines()));
	private PPInteger amount = addComponent(new PPInteger("amount", this, "1", 1, Integer.MAX_VALUE, false, 3, EditorGUI.ICON_NUMBER, GLocale.GUI_GENERIC_EDITOR_ITEM_AMOUNTLORE.getLines()));
	private PPInteger maxAmount = addComponent(new PPInteger("max_amount", this, "0", 0, Integer.MAX_VALUE, false, 4, EditorGUI.ICON_NUMBER, GLocale.GUI_GENERIC_EDITOR_ITEM_MAXAMOUNTLORE.getLines()));
	private PPString name = addComponent(new PPString("name", this, null, false, 5, EditorGUI.ICON_STRING, GLocale.GUI_GENERIC_EDITOR_ITEM_LORE.getLines()));
	private PPStringList lore = addComponent(new PPStringList("lore", this, null, false, 6, EditorGUI.ICON_STRING_LIST, GLocale.GUI_GENERIC_EDITOR_ITEM_LORELORE.getLines()));
	private LPEnchantment enchants = addComponent(new LPEnchantment("enchants", this, false, 7, EditorGUI.ICON_ENCHANTMENT, GLocale.GUI_GENERIC_EDITOR_ITEM_ENCHANTSLORE.getLines()));
	private LPPotionEffect effects = addComponent(new LPPotionEffect("effects", this, false, 8, EditorGUI.ICON_POTION, GLocale.GUI_GENERIC_EDITOR_ITEM_EFFECTSLORE.getLines()));
	private PPString nbt = addComponent(new PPString("nbt", this, null, false, 9, EditorGUI.ICON_NBT, GLocale.GUI_GENERIC_EDITOR_ITEM_NBTLORE.getLines()));
	private PPBoolean enabled = addComponent(new PPBoolean("enabled", this, "true", false, 10, EditorGUI.ICON_BOOLEAN, GLocale.GUI_GENERIC_EDITOR_ITEM_ENABLEDLORE.getLines()));
	private PPInteger slot = addComponent(new PPInteger("slot", this, "-1", -1, 54, false, 11, EditorGUI.ICON_NUMBER, GLocale.GUI_GENERIC_EDITOR_ITEM_SLOTLORE.getLines()));
	private PPDouble chance = addComponent(new PPDouble("chance", this, "0", -1d, 100d, false, 12, EditorGUI.ICON_NUMBER, GLocale.GUI_GENERIC_EDITOR_ITEM_CHANCELORE.getLines()));
	private PPBoolean mustHaveInHand = addComponent(new PPBoolean("must_have_in_hand", this, "false", false, 13, EditorGUI.ICON_BOOLEAN, GLocale.GUI_GENERIC_EDITOR_ITEM_MUSTHAVEINHANDLORE.getLines()));
	private PPBoolean removeAfterAction = addComponent(new PPBoolean("remove_after_action", this, "false", false, 14, EditorGUI.ICON_BOOLEAN, GLocale.GUI_GENERIC_EDITOR_ITEM_REMOVEAFTERACTIONLORE.getLines()));

	public CPItem(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "item", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPInteger getSlot() {
		return slot;
	}

	public Integer getSlot(Player parser) {
		return slot.getParsedValue(parser);
	}

	public PPDouble getChance() {
		return chance;
	}

	public Double getChance(Player parser) {
		return chance.getParsedValue(parser);
	}

	public PPInteger getMaxAmount() {
		return maxAmount;
	}

	public Integer getMaxAmount(Player parser) {
		return maxAmount.getParsedValue(parser);
	}

	public PPBoolean getEnabled() {
		return enabled;
	}

	public Boolean getEnabled(Player parser) {
		return enabled.getParsedValue(parser);
	}

	public PPMat getType() {
		return type;
	}

	public Mat getType(Player parser) {
		return type.getParsedValue(parser);
	}

	public PPInteger getDurability() {
		return durability;
	}

	public Integer getDurability(Player parser) {
		return durability.getParsedValue(parser);
	}

	public PPBoolean getUnbreakable() {
		return unbreakable;
	}

	public Boolean getUnbreakable(Player parser) {
		return unbreakable.getParsedValue(parser);
	}

	public PPInteger getAmount() {
		return amount;
	}

	public Integer getAmount(Player parser) {
		return amount.getParsedValue(parser);
	}

	public PPString getName() {
		return name;
	}

	public String getName(Player parser) {
		return name.getParsedValue(parser);
	}

	public PPStringList getLore() {
		return lore;
	}

	public List<String> getLore(Player parser) {
		return lore.getParsedValue(parser);
	}

	public LPEnchantment getEnchants() {
		return enchants;
	}

	public LPPotionEffect getEffects() {
		return effects;
	}

	public PPString getNbt() {
		return nbt;
	}

	public String getNbt(Player parser) {
		return nbt.getParsedValue(parser);
	}

	public PPBoolean getMustHaveInHand() {
		return mustHaveInHand;
	}

	public Boolean getMustHaveInHand(Player parser) {
		return mustHaveInHand.getParsedValue(parser);
	}

	public PPBoolean getRemoveAfterAction() {
		return removeAfterAction;
	}

	public Boolean getRemoveAfterAction(Player parser) {
		return removeAfterAction.getParsedValue(parser);
	}

	// methods
	private Map<UUID, ItemData> cache = new HashMap<UUID, ItemData>();

	public ItemData getParsedValue(Player parser) {
		if ((parser != null && !cache.containsKey(parser.getUniqueId())) || !cache.containsKey(null)) {
			// create
			ItemData item = new ItemData(getId());
			item.setSlot(getSlot(parser));
			item.setChance(getChance(parser));
			item.setMaxAmount(getMaxAmount(parser));
			item.setEnabled(getEnabled(parser));

			// type
			item.setType(Mat.from(getType(parser).getModernName(), getDurability(parser)));

			// amount
			item.setAmount(getAmount(parser));

			// parse meta
			String name = getName(parser);
			List<String> lore = getLore(parser);
			boolean metaPlaceholders = PrimitiveParseable.isParseable(name);
			if (!metaPlaceholders && lore != null) {
				for (String line : lore) {
					if (PrimitiveParseable.isParseable(line)) {
						metaPlaceholders = true;
						break;
					}
				}
			}

			// set meta
			item.setName(name);
			item.setLore(lore);

			// enchants
			for (CPEnchantment enchant : enchants.getElements().values()) {
				Enchantment type = enchant.getType(parser);
				Integer level = enchant.getLevel(parser);
				if (type != null && level != null) {
					item.setEnchant(type, level);
				}
			}

			// effects
			for (CPPotionEffect effect : effects.getElements().values()) {
				PotionEffect parsed = effect.getParsedValue(parser);
				if (parsed != null) {
					item.getEffects().add(parsed);
				}
			}

			// nbt
			String nbt = getNbt(parser);
			if (nbt != null) {
				try {
					item.setCustomNbt(Compat.INSTANCE.unserializeNbt(nbt));
				} catch (Throwable exception) {
					exception.printStackTrace();
					GCore.inst().error("Couldn't load NBT from " + nbt + " for item " + getId());
				}
			}

			// unbreakable
			if (getUnbreakable(parser)) {
				item.setUnbreakable(true);
			}

			// cache if name/lore don't contain placeholders
			if (!metaPlaceholders) {
				cache.put(parser != null ? parser.getUniqueId() : null, item);
			}

			// return
			return item;
		}
		// eventually return content of cache
		return cache.get(parser != null ? parser.getUniqueId() : null);
	}

	// methods
	public boolean isEmpty(Player parser) {
		ItemData item = getParsedValue(parser);
		return item == null || item.getType() == null || item.getType().isAir();
	}

	public boolean contains(Player player, Player parser) {
		// has item
		ItemData item = getParsedValue(parser);
		if (item != null && item.getType() != null && !item.getType().isAir()) {
			return getMustHaveInHand(parser) ? isValid(player.getItemInHand(), true, parser) : item.contains(player.getInventory());
		}
		// no conditions so it's valid
		return true;
	}

	public boolean isValid(Material toCheckType, Player parser) {
		// has item
		ItemData item = getParsedValue(parser);
		if (item != null && item.getType() != null && !item.getType().isAir()) {
			return item.getType().isMat(toCheckType);
		}
		// no conditions so it's valid
		return true;
	}

	public boolean isValid(ItemStack toCheck, boolean checkAmount, Player parser) {
		// has item
		ItemData item = getParsedValue(parser);
		if (item != null && item.getType() != null && !item.getType().isAir()) {
			return checkAmount ? item.isAtLeast(toCheck) : item.isSimilar(toCheck);
		}
		// no conditions so it's valid
		return true;
	}

	public int getValidAmount(Collection<ItemStack> items, Player parser) {
		// has item
		ItemData item = getParsedValue(parser);
		if (item != null && item.getType() != null && !item.getType().isAir()) {
			int amount = 0;
			for (ItemStack it : items) {
				if (item.isSimilar(it)) {
					amount += it.getAmount();
				}
			}
			return amount;
		}
		// no item, so max value
		return Integer.MAX_VALUE;
	}

	public void remove(Player player, Player parser, boolean force) {
		ItemData item = getParsedValue(parser);
		if (item != null && item.getType() != null && !item.getType().isAir() && (force || getRemoveAfterAction(parser))) {
			if (getMustHaveInHand(parser)) {
				ItemStack inHand = player.getItemInHand();
				inHand.setAmount(inHand.getAmount() - item.getAmount());
				player.setItemInHand(inHand.getAmount() > 0 ? inHand : null);
			} else {
				item.remove(player.getInventory());
			}
			player.updateInventory();
		}
	}

	public Item drop(Location location, Player parser) {
		ItemData item = getParsedValue(parser);
		if (item != null && item.getType() != null && !item.getType().isAir()) {
			return location.getWorld().dropItem(location, item.getItemStack());
		}
		return null;
	}

	public void give(Player player, Player parser) {
		ItemData item = getParsedValue(parser);
		if (item != null && item.getType() != null && !item.getType().isAir()) {
			if (player.getInventory().firstEmpty() == -1) {
				player.getWorld().dropItem(player.getEyeLocation(), item.getItemStack());
			} else {
				player.getInventory().addItem(item.getItemStack());
				player.updateInventory();
			}
		}
	}

	// clone
	protected CPItem() {
		super();
	}

	@Override
	public CPItem clone() {
		return (CPItem) super.clone();
	}

}
