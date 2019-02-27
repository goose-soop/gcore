package be.pyrrh4.pyrcore.lib.loadable.setting.complex;

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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.gui.ItemData;
import be.pyrrh4.pyrcore.lib.loadable.Loadable;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorCallback;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorItem;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingBoolean;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingDouble;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingInteger;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingMat;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingString;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingStringList;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.placeholder.PlaceholderParser;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.util.input.ItemInput;
import be.pyrrh4.pyrcore.lib.versioncompat.Compat;

public class ItemSetting extends Loadable<ItemSetting> {

	// base
	public ItemSetting(Loadable<?> parent, String id, boolean mandatory, Mat icon, List<String> description) {
		super(parent, id, mandatory, icon, description);
		registerSetting(new SettingInteger("slot", "-1", false, PCLocale.GUI_GENERIC_EDITOR_ITEM_SLOTLORE.getLines()));
		registerSetting(new SettingDouble("chance", "-1", false, PCLocale.GUI_GENERIC_EDITOR_ITEM_CHANCELORE.getLines()));
		registerSetting(new SettingInteger("max_amount", "0", false, PCLocale.GUI_GENERIC_EDITOR_ITEM_MAXAMOUNTLORE.getLines()));
		registerSetting(new SettingBoolean("enabled", "true", false, PCLocale.GUI_GENERIC_EDITOR_ITEM_ENABLEDLORE.getLines()));
		registerSetting(new SettingMat("type", "AIR", true, PCLocale.GUI_GENERIC_EDITOR_ITEM_TYPELORE.getLines()));
		registerSetting(new SettingInteger("durability", "0", false, PCLocale.GUI_GENERIC_EDITOR_ITEM_DURABILITYLORE.getLines()));
		registerSetting(new SettingBoolean("unbreakable", "false", false, PCLocale.GUI_GENERIC_EDITOR_ITEM_UNBREAKABLELORE.getLines()));
		registerSetting(new SettingInteger("amount", "1", false, PCLocale.GUI_GENERIC_EDITOR_ITEM_AMOUNTLORE.getLines()));
		registerSetting(new SettingString("name", null, false, PCLocale.GUI_GENERIC_EDITOR_ITEM_NAMELORE.getLines()));
		registerSetting(new SettingStringList("lore", null, false, PCLocale.GUI_GENERIC_EDITOR_ITEM_LORELORE.getLines()));
		registerSetting(new SettingStringList("enchants", Utils.emptyList(), false, PCLocale.GUI_GENERIC_EDITOR_ITEM_ENCHANTSLORE.getLines()));
		registerSetting(new SettingString("nbt", null, false, PCLocale.GUI_GENERIC_EDITOR_ITEM_NBTLORE.getLines()));
		registerSetting(new SettingBoolean("must_have_in_hand", "false", false, PCLocale.GUI_GENERIC_EDITOR_ITEM_MUSTHAVEINHANDLORE.getLines()));
		registerSetting(new SettingBoolean("remove_after_action", "false", false, PCLocale.GUI_GENERIC_EDITOR_ITEM_REMOVEAFTERACTIONLORE.getLines()));
	}

	// get
	private Map<UUID, ItemData> cache = new HashMap<UUID, ItemData>();

	public ItemData getItem(Player player) {
		if ((player != null && !cache.containsKey(player.getUniqueId())) || !cache.containsKey(null)) {
			// create
			ItemData item = new ItemData(loadConfigRoot());
			item.setSlot(getSettingInteger("slot").getParsed(player));
			item.setChance(getSettingDouble("chance").getParsed(player));
			item.setMaxAmount(getSettingInteger("max_amount").getParsed(player));
			item.setEnabled(getSettingBoolean("enabled").getParsed(player));

			// type
			item.setType(Mat.from(getSettingMat("type").getParsed(player).getModernName(), getSettingInteger("durability").getParsed(player)));

			// amount
			item.setAmount(getSettingInteger("amount").getParsed(player));

			// meta
			item.setName(getSettingString("name").getParsed(player));
			item.setLore(getSettingStringList("lore").getParsed(player));

			// enchants
			List<String> enchants = getSettingStringList("enchants").getParsed(player);
			if (enchants != null) {
				for (String enchant : enchants) {
					try {
						String[] raw = enchant.split(",");
						Enchantment ench = Compat.INSTANCE.getEnchantment(raw[0]);
						int level = Integer.parseInt(raw[1]);
						item.setEnchant(ench, level);
					} catch (Throwable ignored) {}
				}
			}

			// nbt
			String nbt = getSettingString("nbt").getParsed(player);
			if (nbt != null) {
				try {
					item.setCustomNbt(Compat.INSTANCE.unserializeNbt(nbt));
				} catch (Throwable exception) {
					exception.printStackTrace();
					PyrCore.inst().error("Couldn't load NBT from " + nbt + " for item " + loadConfigRoot());
				}
			}

			// unbreakable
			if (getSettingBoolean("unbreakable").getParsed(player)) {
				item.setUnbreakable(true);
			}

			// return
			cache.put(player != null ? player.getUniqueId() : null, item);
		}
		return cache.get(player != null ? player.getUniqueId() : null);
	}

	// methods
	public boolean isEmpty(Player parsingPlayer) {
		ItemData item = getItem(parsingPlayer);
		return item == null || item.getType() == null || item.getType().isAir();
	}

	public boolean isValid(Player player, Player parsingPlayer) {
		// has item
		ItemData item = getItem(parsingPlayer);
		if (item != null && item.getType() != null && !item.getType().isAir()) {
			return getSettingBoolean("must_have_in_hand").getParsed(parsingPlayer) ? isValid(player.getItemInHand(), true, parsingPlayer) : item.contains(player.getInventory());
		}
		// no conditions so it's valid
		return true;
	}

	public boolean isValid(Material toCheckType, Player player) {
		// has item
		ItemData item = getItem(player);
		if (item != null && item.getType() != null && !item.getType().isAir()) {
			return item.getType().isMat(toCheckType);
		}
		// no conditions so it's valid
		return true;
	}

	public boolean isValid(ItemStack toCheck, boolean checkAmount, Player player) {
		// has item
		ItemData item = getItem(player);
		if (item != null && item.getType() != null && !item.getType().isAir()) {
			return checkAmount ? item.isAtLeast(toCheck) : item.isSimilar(toCheck);
		}
		// no conditions so it's valid
		return true;
	}

	public int getValidAmount(Collection<ItemStack> items, Player player) {
		// has item
		ItemData item = getItem(player);
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

	public void remove(Player player, Player parsingPlayer, boolean force) {
		ItemData item = getItem(parsingPlayer);
		if (item != null && item.getType() != null && !item.getType().isAir() && (force || getSettingBoolean("remove_after_action").getParsed(parsingPlayer))) {
			if (getSettingBoolean("must_have_in_hand").getParsed(parsingPlayer)) {
				ItemStack inHand = player.getItemInHand();
				inHand.setAmount(inHand.getAmount() - item.getAmount());
				player.setItemInHand(inHand.getAmount() > 0 ? inHand : null);
			} else {
				item.remove(player.getInventory());
			}
			player.updateInventory();
		}
	}

	public Item drop(Location location, Player player) {
		ItemData item = getItem(player);
		if (item != null && item.getType() != null && !item.getType().isAir()) {
			return location.getWorld().dropItem(location, item.getItemStack());
		}
		return null;
	}

	public void give(Player player, Player parsingPlayer) {
		ItemData item = getItem(parsingPlayer);
		if (item != null && item.getType() != null && !item.getType().isAir()) {
			if (player.getInventory().firstEmpty() == -1) {
				player.getWorld().dropItem(player.getEyeLocation(), item.getItemStack());
			} else {
				player.getInventory().addItem(item.getItemStack());
				player.updateInventory();
			}
		}
	}

	// editor GUI
	@Override
	public EditorGUI loadEditorInitialize(final EditorGUI parent, final String name, final EditorCallback onModif) {
		// init
		EditorGUI gui = new EditorGUI(parent, getId(), 9, 8) {
			private EditorGUI guiThis = this;
			@Override
			protected void fill() {
				// import
				setRegularItem(new EditorItem("import", 2, EditorGUI.ICON_ITEM, PCLocale.GUI_GENERIC_EDITORITEMIMPORT.getLine(), fillItemLore(PCLocale.GUI_GENERIC_EDITORITEMIMPORTLORE.getLines(), "item", loadDescribe(), loadMandatory())) {
					@Override
					protected void onClick(Player player, ClickType clickType, int pageIndex) {
						// item
						player.closeInventory();
						PCLocale.MSG_GENERIC_ITEMINPUT.send(player);
						PyrCore.inst().getItemInputs().put(player, new ItemInput() {
							@Override
							public void onChoose(Player player, ItemStack value) {
								if (value != null) {
									ItemData data = new ItemData(null, value);
									getSettingMat("type").setValue(data.getType().getModernName());
									getSettingInteger("durability").setValue(String.valueOf(data.getType().getDurability()));
									getSettingBoolean("unbreakable").setValue(String.valueOf(data.isUnbreakable()));
									getSettingInteger("amount").setValue(String.valueOf(data.getAmount()));
									getSettingString("name").setValue(data.getName());
									getSettingStringList("lore").setValue(data.getLore());
									List<String> enchants = Utils.emptyList();
									for (Enchantment enchant : data.getEnchants().keySet()) {
										enchants.add(enchant.getName() + "," + data.getEnchants().get(enchant));
									}
									getSettingStringList("enchants").setValue(enchants);
									String nbt = null;
									try {
										if (data.getCustomNbt() != null) {
											nbt = Compat.INSTANCE.serializeNbt(data.getCustomNbt());
										}
										getSettingString("nbt").setValue(nbt);
									} catch (Throwable ignored) {
										PyrCore.inst().messageError(player, "Couldn't decode custom NBT for this item.");
									}
									onModif.callback();
								}
								open(player);
							}
						});
					}
				});
				// raw
				setRegularItem(new EditorItem("raw", 3, Mat.COMMAND_BLOCK, PCLocale.GUI_GENERIC_EDITORRAW.getLine(), fillItemLore(PCLocale.GUI_GENERIC_EDITORRAWLORE.getLines("{placeholders}", PlaceholderParser.describeAll()), "item", loadDescribe(), loadMandatory())) {
					@Override
					protected void onClick(Player player, ClickType clickType, int pageIndex) {
						// open super gui
						ItemSetting.super.loadEditorInitialize(guiThis, "Raw", onModif).open(player);
					}
				});
				// delete
				setPersistentItem(new EditorItem("delete", 6, Mat.TNT_MINECART, PCLocale.GUI_GENERIC_EDITORITEMDELETESELF.getLine(), PCLocale.GUI_GENERIC_EDITORITEMDELETESELFLORE.getLines()) {
					@Override
					protected void onClick(Player player, ClickType clickType, int pageIndex) {
						loadReset();
						onModif.callback();
						open(player);
					}
				});
				// back
				setPersistentItem(new EditorItem("back", 8, Mat.ARROW, PCLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
					@Override
					protected void onClick(Player player, ClickType clickType, int pageIndex) {
						parent.open(player);
					}
				});
			}
		};
		// return
		return gui;
	}

}
