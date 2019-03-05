package be.pyrrh4.pyrcore.lib.parseable.editor;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.PyrPlugin;
import be.pyrrh4.pyrcore.lib.gui.ClickeableItem;
import be.pyrrh4.pyrcore.lib.gui.GUI;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.ContainerParseable;
import be.pyrrh4.pyrcore.lib.parseable.ListParseable;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.PrimitiveParseable;
import be.pyrrh4.pyrcore.lib.parseable.placeholder.PlaceholderParser;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.util.input.ChatInput;

public abstract class EditorGUI extends GUI {

	// icons
	public static Mat ICON_BLOCK = Mat.GRASS_BLOCK;
	public static Mat ICON_MOB = Mat.SPAWNER;
	public static Mat ICON_ITEM = Mat.IRON_PICKAXE;
	public static Mat ICON_NUMBER = Mat.HOPPER;
	public static Mat ICON_NUMBER_CHANCE = Mat.GOLD_NUGGET;
	public static Mat ICON_NUMBER_LEVEL = Mat.EXPERIENCE_BOTTLE;
	public static Mat ICON_BOOLEAN = Mat.BAKED_POTATO;
	public static Mat ICON_STRING = Mat.PAPER;
	public static Mat ICON_STRING_LIST = Mat.WRITABLE_BOOK;
	public static Mat ICON_ENCHANTMENT = Mat.ENCHANTED_BOOK;
	public static Mat ICON_NBT = Mat.ANVIL;
	public static Mat ICON_SOUND = Mat.NOTE_BLOCK;
	public static Mat ICON_ACTION = Mat.COMMAND_BLOCK_MINECART;
	public static Mat ICON_SKILL = Mat.DIAMOND_AXE;
	public static Mat ICON_CONDITION = Mat.GOLD_INGOT;
	public static Mat ICON_GROUP = Mat.BOOKSHELF;
	public static Mat ICON_LEVELS = Mat.ENCHANTING_TABLE;
	public static Mat ICON_MODIFIER = Mat.REDSTONE_TORCH;
	public static Mat ICON_TECHNICAL = Mat.REPEATER;
	public static Mat ICON_ATTRIBUTE = Mat.REDSTONE;
	public static Mat ICON_ENUM = Mat.ENDER_CHEST;
	public static Mat ICON_POTION = Mat.SPLASH_POTION;
	public static Mat ICON_GOTO = Mat.POWERED_RAIL;
	public static Mat ICON_LOCATION = Mat.RAIL;
	public static Mat ICON_WORLD = Mat.END_PORTAL_FRAME;

	// max lines
	public static int MAX_DESCRIPTION_LINES = 22;

	// base
	private EditorGUI parent;

	public EditorGUI(PyrPlugin plugin, EditorGUI parent, String name, int size, int maxRegularSlot) {
		super(plugin, name, size, maxRegularSlot, true);
		this.parent = parent;
	}

	// get
	public EditorGUI getParent() {
		return parent;
	}

	// overriden
	@Override
	public PyrPlugin getPlugin() {
		return (PyrPlugin) super.getPlugin();
	}

	@Override
	public void setRegularItem(ClickeableItem item) {
		checkOverwrite(item);
		super.setRegularItem(item);
	}

	@Override
	public void setPersistentItem(ClickeableItem item) {
		checkOverwrite(item);
		super.setPersistentItem(item);
	}

	public void checkOverwrite(ClickeableItem item) {
		if (item.getItemData().getSlot() < 0) return;
		ClickeableItem present = getItemInSlot(0, item.getItemData().getSlot());
		if (present != null) {
			getPlugin().warning("Overriding item " + present.getItemData().getId() + " by " + item.getItemData().getId() + " at slot " + item.getItemData().getSlot() + " of GUI " + getName());
		}
	}

	@Override
	public void open(Player player) {
		open(player, 0);
	}

	@Override
	public void open(Player player, int pageIndex) {
		if (isRegistered()) {
			unregister();
		}
		register();
		fill();
		super.open(player, pageIndex);
	}

	protected abstract void fill();

	// static methods
	public static void fillItemCurrent(final EditorGUI gui, Player player, final PrimitiveParseable<?> component, final int currentSlot, final ModifCallback onModif) {
		fillItemCurrent(gui, player, component.getId(), component.describe(0), component.getEditorDescription(), component.getTypeName(), component.isMandatory(), component.getEditorIcon(), currentSlot, onModif);
	}

	public static void fillItemCurrent(final EditorGUI gui, Player player, String currentId, List<String> currentValue, List<String> editorDescription, String typeName, boolean mandatory, Mat editorIcon, final int currentSlot, final ModifCallback onModif) {
		// build lore
		List<String> currentLore = Utils.emptyList();
		int i = 0;
		for (String line : Utils.separateSentences(PCLocale.GUI_GENERIC_EDITORCURRENTLORE.getLines("{description}", editorDescription, "{type}", typeName, "{mandatory}", mandatory ? "&cyes" : "&ano", "{current}", currentValue), 50, " ")) {
			if (++i == EditorGUI.MAX_DESCRIPTION_LINES) {
				currentLore.add(" §8...");
				break;
			}
			currentLore.add(line);
		}
		// add item
		gui.setRegularItem(new EditorItem("control_item_current", currentSlot, editorIcon, "§6" + currentId, currentLore) {
			@Override
			protected void onClick(Player player, ClickType clickType, int pageIndex) {
			}
		});
	}

	public static void fillItemDelete(final EditorGUI gui, Player player, final Parseable component, final int deleteSlot, final ModifCallback onModif) {
		fillItemDelete(gui, player, deleteSlot, onModif, new ModifCallback() {
			@Override
			public void callback(EditorGUI from, Player player) {
				delete(component);
			}
		});
	}

	public static void fillItemDelete(final EditorGUI gui, Player player, final int deleteSlot, final ModifCallback onModif, final ModifCallback onDelete) {
		gui.setPersistentItem(new EditorItem("control_item_delete", deleteSlot, Mat.TNT_MINECART, PCLocale.GUI_GENERIC_EDITORITEMDELETESELF.getLine(), PCLocale.GUI_GENERIC_EDITORITEMDELETESELFLORE.getLines()) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				// delete value
				onDelete.callback(gui, player);
				onModif.callback(gui, player);
				// re-fill and open
				gui.open(player);
			}
		});
	}

	private static void delete(Parseable component) {
		if (component instanceof PrimitiveParseable<?>) {
			((PrimitiveParseable<?>) component).setValue(null);
		} else if (component instanceof ContainerParseable) {
			for (Parseable comp : ((ContainerParseable) component).getComponents().values()) {
				delete(comp);
			}
		} else if (component instanceof ListParseable<?>) {
			for (Parseable element : ((ListParseable<?>) component).getElements().values()) {
				delete(element);
			}
		}
	}

	public static void fillItemRaw(final EditorGUI gui, Player player, final PrimitiveParseable<?> component, final int rawSlot, final ModifCallback onModif) {
		fillItemRaw(gui, player, rawSlot, onModif, new RawChangeCallback() {
			@Override
			public void callback(EditorGUI from, Player player, String value) {
				if (component.getValue() == null) {
					component.setValue(Utils.asList(value));
				} else {
					component.getValue().set(0, value);
				}
			}
		});
	}

	public static void fillItemRaw(final EditorGUI gui, Player player, final int rawSlot, final ModifCallback onModif, final RawChangeCallback onChange) {
		gui.setRegularItem(new EditorItem("control_item_raw", rawSlot, Mat.COMMAND_BLOCK, PCLocale.GUI_GENERIC_EDITORRAW.getLine(), PCLocale.GUI_GENERIC_EDITORRAWLORE.getLines("{placeholders}", PlaceholderParser.describeAll())) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				// chat
				player.closeInventory();
				PCLocale.MSG_GENERIC_CHATINPUT.send(player);
				PyrCore.inst().getChatInputs().put(player, new ChatInput() {
					@Override
					public void onChat(Player player, String value) {
						// replace value
						if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
							onChange.callback(gui, player, value);
							onModif.callback(gui, player);
						}
						// re-fill and open
						gui.open(player);
					}
				});
			}
		});
	}

	public static interface RawChangeCallback {
		public void callback(EditorGUI from, Player player, String value);
	}

}
