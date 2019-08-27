package com.guillaumevdn.gcore.lib.parseable.container;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.gui.ItemData;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.parseable.ContainerParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.PrimitiveParseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorItem;
import com.guillaumevdn.gcore.lib.parseable.editor.ModifCallback;
import com.guillaumevdn.gcore.lib.parseable.list.LPEnchantment;
import com.guillaumevdn.gcore.lib.parseable.list.LPPotionEffect;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPBoolean;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPDouble;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPInteger;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPMat;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPString;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPStringList;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.lib.util.input.ChatInput;
import com.guillaumevdn.gcore.lib.util.input.ItemInput;
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
	private PPBoolean checkDurability = addComponent(new PPBoolean("check_durability", this, "true", false, 15, EditorGUI.ICON_BOOLEAN, GLocale.GUI_GENERIC_EDITOR_ITEM_CHECKDURABILITYLORE.getLines()));
	private PPBoolean exactMatch = addComponent(new PPBoolean("exact_match", this, "true", false, 16, EditorGUI.ICON_BOOLEAN, GLocale.GUI_GENERIC_EDITOR_ITEM_EXACTMATCHLORE.getLines()));
	private PPBoolean hideFlags = addComponent(new PPBoolean("hide_flags", this, "false", false, 17, EditorGUI.ICON_BOOLEAN, GLocale.GUI_GENERIC_EDITOR_ITEM_HIDEFLAGSLORE.getLines()));

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

	public PPBoolean getCheckDurability() {
		return checkDurability;
	}

	public Boolean getCheckDurability(Player parser) {
		return checkDurability.getParsedValue(parser);
	}

	public PPBoolean getExactMatch() {
		return exactMatch;
	}

	public Boolean getExactMatch(Player parser) {
		return exactMatch.getParsedValue(parser);
	}

	public PPBoolean getHideFlags() {
		return hideFlags;
	}

	public Boolean getHideFlags(Player parser) {
		return hideFlags.getParsedValue(parser);
	}

	// methods
	private static Set<CPItem> withCache = new HashSet<CPItem>();
	private Map<UUID, ItemData> cache = new HashMap<UUID, ItemData>();

	public static void clearAllCache() {
		for (CPItem item : withCache) {
			item.cache.clear();
		}
		withCache.clear();
	}

	public ItemData getParsedValue(Player parser) {
		if ((parser != null && !cache.containsKey(parser.getUniqueId())) || !cache.containsKey(null)) {
			// create
			ItemData item = new ItemData(getId());
			item.setSlot(getSlot(parser));
			item.setChance(getChance(parser));
			item.setMaxAmount(getMaxAmount(parser));
			item.setEnabled(getEnabled(parser));

			// type
			Mat mat = getType(parser);
			Integer durability = getDurability(parser);
			if (mat != null && durability != null && mat.getDurability() != durability) mat = mat.cloneWithDurability(durability);
			item.setType(mat);

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

			// flags
			if (getHideFlags(parser)) {
				item.setHideFlags(true);
			}

			// cache if name/lore don't contain placeholders and if can cache
			if (!metaPlaceholders && GCore.inst().cacheParsedItems()) {
				cache.put(parser != null ? parser.getUniqueId() : null, item);
				withCache.add(this);
			}

			// return
			return item;
		}
		// eventually return content of cache
		return cache.get(parser != null ? parser.getUniqueId() : null);
	}

	public void replace(ItemData item, Player nbtErrorMessagePlayer) {
		type.setValue(Utils.asList(item.getType().toString()));
		durability.setValue(Utils.asList("" + item.getType().getDurability()));
		unbreakable.setValue(Utils.asList("" + item.isUnbreakable()));
		amount.setValue(Utils.asList("" + item.getAmount()));
		maxAmount.setValue(Utils.asList("" + item.getAmount()));
		name.setValue(item.getName() != null ? Utils.asList(item.getName()) : null);
		lore.setValue(item.getLore() != null ? Utils.asList(item.getLore()) : null);
		enchants.clearElements();
		int id = 0;
		for (Enchantment enchant : item.getEnchants().keySet()) {
			CPEnchantment ench = enchants.createElement("" + ++id);
			ench.getType().setValue(Utils.asList(enchant.getName()));
			ench.getLevel().setValue(Utils.asList("" + item.getEnchants().get(enchant)));
		}
		effects.clearElements();
		id = 0;
		for (PotionEffect effect : item.getEffects()) {
			CPPotionEffect eff = effects.createElement("" + ++id);
			eff.getType().setValue(Utils.asList(effect.getType().getName()));
			eff.getAmplifier().setValue(Utils.asList("" + effect.getAmplifier()));
			eff.getDuration().setValue(Utils.asList("" + effect.getDuration()));
		}
		try {
			String nbtString = Compat.INSTANCE.serializeNbt(item.getCustomNbt());
			nbt.setValue(nbtString == null ? null : Utils.asList(nbtString));
		} catch (IOException exception) {
			exception.printStackTrace();
			if (nbtErrorMessagePlayer != null) {
				Messenger.send(nbtErrorMessagePlayer, Messenger.Level.SEVERE_ERROR, GCore.inst().getName(), "Couldn't save item NTB (see console)");
			}
		}
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
			if (getMustHaveInHand(parser)) {
				return isValid(player.getItemInHand(), true, parser);
			} else {
				return item.contains(player.getInventory(), item.getAmount(), getCheckDurability(parser), getExactMatch(parser), 0d);
			}
		}
		// no conditions so it's valid
		return true;
	}

	public int count(Player player, Player parser) {
		// has item
		ItemData item = getParsedValue(parser);
		if (item != null && item.getType() != null && !item.getType().isAir()) {
			if (getMustHaveInHand(parser)) {// in hand
				if (!item.isSimilar(player.getItemInHand())) {
					return 0;
				} else {
					return player.getItemInHand().getAmount();
				}
			} else {// in inventory
				return item.count(player.getInventory(),getCheckDurability(parser), getExactMatch(parser), 0d);
			}
		}
		// well there's no item configured and we won't count fookin empty slots
		return 0;
	}

	public boolean isValid(Material toCheckType, Player parser) {
		// has item
		ItemData item = getParsedValue(parser);
		if (item != null && item.getType() != null && !item.getType().isAir()) {
			return item.getType().equals(Mat.fromMaterial(toCheckType, 0, 0), getCheckDurability(parser));
		}
		// no conditions so it's valid
		return true;
	}

	public boolean isValid(ItemStack toCheck, boolean checkAmount, Player parser) {
		// has item
		ItemData item = getParsedValue(parser);
		if (item != null && item.getType() != null && !item.getType().isAir()) {
			return checkAmount ? item.isAtLeast(toCheck, getCheckDurability(parser), getExactMatch(parser)) : item.isSimilar(toCheck, getCheckDurability(parser), getExactMatch(parser), false);
		}
		// no conditions so it's valid
		return true;
	}

	public int getValidAmount(Collection<ItemStack> items, Player parser) {
		// has item
		ItemData item = getParsedValue(parser);
		boolean checkDurability = getCheckDurability(parser);
		boolean exactMatch = getExactMatch(parser);
		if (item != null && item.getType() != null && !item.getType().isAir()) {
			int amount = 0;
			for (ItemStack toCheck : items) {
				if (item.isSimilar(toCheck, checkDurability, exactMatch, false)) {
					amount += toCheck.getAmount();
				}
			}
			return amount;
		}
		// no item, so max value
		return Integer.MAX_VALUE;
	}

	public int remove(Player player, Player parser, boolean force) {
		return remove(player, parser, null, force);
	}

	public int remove(Player player, Player parser, Integer forcedAmount, boolean force) {
		// try to remove
		int removed = 0;
		ItemData item = getParsedValue(parser);
		if (item != null && item.getType() != null && !item.getType().isAir() && (force || getRemoveAfterAction(parser))) {
			int amount = forcedAmount != null ? forcedAmount : item.getAmount();
			if (getMustHaveInHand(parser)) {
				ItemStack inHand = player.getItemInHand();
				if (inHand != null && item.isSimilar(inHand, getCheckDurability(parser), getExactMatch(parser), false)) {
					removed = inHand.getAmount();
					inHand.setAmount(inHand.getAmount() - amount);
					player.setItemInHand(inHand.getAmount() > 0 ? inHand : null);
				} else {
					removed = item.remove(player.getInventory(), amount, getCheckDurability(parser), getExactMatch(parser), 0d);
				}
			} else {
				removed = item.remove(player.getInventory(), amount, getCheckDurability(parser), getExactMatch(parser), 0d);
			}
			player.updateInventory();
		}
		// return
		return removed;
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

	// editor
	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// fill editor
		super.fillEditor(gui, player, onModif);
		// add import item
		gui.setPersistentItem(new EditorItem("control_item_import", getEditorBackSlot() - 4, Mat.ENDER_CHEST, GLocale.GUI_GENERIC_EDITORITEMIMPORT.getLine(), GLocale.GUI_GENERIC_EDITORITEMIMPORTLORE.getLines()) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				// location
				player.closeInventory();
				GLocale.MSG_GENERIC_ITEMINPUT.send(player);
				GCore.inst().getItemInputs().put(player, new ItemInput() {
					@Override
					public void onChoose(Player player, ItemStack value) {
						// replace value
						if (value != null && !Mat.fromItem(value).isAir()) {
							ItemData item = new ItemData(value);
							type.setValue(Utils.asList(item.getType().toString()));
							durability.setValue(Utils.asList("" + item.getType().getDurability()));
							unbreakable.setValue(Utils.asList("" + item.isUnbreakable()));
							amount.setValue(Utils.asList("" + item.getAmount()));
							maxAmount.setValue(Utils.asList("" + item.getAmount()));
							name.setValue(item.getName() != null ? Utils.asList(item.getName()) : null);
							lore.setValue(item.getLore() != null ? Utils.asList(item.getLore()) : null);
							enchants.clearElements();
							int id = 0;
							for (Enchantment enchant : item.getEnchants().keySet()) {
								CPEnchantment ench = enchants.createElement("" + ++id);
								ench.getType().setValue(Utils.asList(enchant.getName()));
								ench.getLevel().setValue(Utils.asList("" + item.getEnchants().get(enchant)));
							}
							effects.clearElements();
							id = 0;
							for (PotionEffect effect : item.getEffects()) {
								CPPotionEffect eff = effects.createElement("" + ++id);
								eff.getType().setValue(Utils.asList(effect.getType().getName()));
								eff.getAmplifier().setValue(Utils.asList("" + effect.getAmplifier()));
								eff.getDuration().setValue(Utils.asList("" + effect.getDuration()));
							}
							try {
								String nbtString = Compat.INSTANCE.serializeNbt(item.getCustomNbt());
								nbt.setValue(nbtString == null ? null : Utils.asList(nbtString));
							} catch (IOException exception) {
								exception.printStackTrace();
								Messenger.send(player, Messenger.Level.SEVERE_ERROR, GCore.inst().getName(), "Couldn't save item NTB (see console)");
							}
							// callback
							onModif.callback(gui, player);
						}
						// re-fill and open
						gui.open(player);
					}
				});
			}
		});
		// add import HeadDatabase item
		if (GCore.inst().getHeadDatabaseIntegration() != null) {
			gui.setPersistentItem(new EditorItem("control_item_import_headdatabase", getEditorBackSlot() - 3, Mat.ENDER_CHEST, GLocale.GUI_GENERIC_EDITORITEMIMPORTHEADDATABASE.getLine(), GLocale.GUI_GENERIC_EDITORITEMIMPORTHEADDATABASELORE.getLines()) {
				@Override
				protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
					// location
					player.closeInventory();
					GLocale.MSG_GENERIC_ITEMHEADDATABASEINPUT.send(player);
					GCore.inst().getChatInputs().put(player, new ChatInput() {
						@Override
						public void onChat(Player player, String value) {
							// replace value
							if (!value.replace(" ", "").equalsIgnoreCase("cancel") && GCore.inst().getHeadDatabaseIntegration() != null) {
								ItemStack headStack = GCore.inst().getHeadDatabaseIntegration().getItem(value);
								if (headStack != null) {
									replace(new ItemData(headStack), player);
									// callback
									onModif.callback(gui, player);
								}
							}
							// re-fill and open
							gui.open(player);
						}
					});
				}
			});
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
