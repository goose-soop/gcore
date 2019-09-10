package com.guillaumevdn.gcore.lib.parseable.primitive;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.gui.GUI;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ParseResult;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.PrimitiveParseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorItem;
import com.guillaumevdn.gcore.lib.parseable.editor.ModifCallback;
import com.guillaumevdn.gcore.lib.util.Utils;

public class PPEnum<T extends Enum<T>> extends PrimitiveParseable<T> {

	// base
	private Class<T> enumClass;

	public PPEnum(String id, Parseable parent, String defaultValue, Class<T> enumClass, String typeName, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, defaultValue == null ? null : Utils.asList(defaultValue), typeName, mandatory, editorSlot, editorIcon, editorDescription);
		this.enumClass = enumClass;
	}

	// get
	public Class<T> getEnumClass() {
		return enumClass;
	}

	// parse
	@Override
	public ParseResult<T> parseValue(List<String> value, Player parsing) throws Throwable {
		if (value.isEmpty()) {
			return new ParseResult<T>(null);
		}
		T result = Utils.valueOfOrNull(enumClass, value.get(0));
		return result != null ? new ParseResult<T>(result) : null;
	}

	// editor
	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// current, raw and delete
		EditorGUI.fillItemCurrent(gui, player, 0, this, onModif);
		EditorGUI.fillItemRaw(gui, player, this, 3, getValue() == null || getValue().isEmpty() ? null : getValue().get(0), onModif);
		EditorGUI.fillItemDelete(gui, player, 6, this, onModif);
		// select
		gui.setRegularItem(new EditorItem("control_item_select", 2, Mat.ENDER_CHEST, GLocale.GUI_GENERIC_EDITORENUMSELECT.getLine(), GLocale.GUI_GENERIC_EDITORENUMSELECTLORE.getLines()) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				// selection gui
				EditorGUI sub = new EditorGUI(getLastData().getPlugin(), gui, Utils.getNewInventoryName(gui.getName(), "Select"), 54, GUI.SLOTS_0_TO_44) {
					@Override
					protected void fill() {
						// add values
						boolean mobs = enumClass.equals(EntityType.class);
						for (final T val : enumClass.getEnumConstants()) {
							if (mobs && !mobTypes.contains(val)) continue;// invalid mob
							final String valName = val.name();
							setRegularItem(new EditorItem("value_" + valName, -1, Mat.ENDER_CHEST, "§6" + valName, null) {
								@Override
								protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
									// replace value
									if (getValue() != null) {
										getValue().set(0, valName);
									} else {
										setValue(Utils.asList(valName));
									}
									onModif.callback(null, gui, player);
									// re-fill and open
									gui.open(player);
								}
							});
						}
						// back item
						setPersistentItem(new EditorItem("control_item_back", 52, Mat.ARROW, GLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
							@Override
							protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
								gui.open(player);
							}
						});
					}
				};
				// open it
				sub.open(player);
			}
		});
	}

	@Override
	public int getEditorSize() {
		return 9;
	}

	@Override
	public List<Integer> getEditorRegularSlots() {
		return GUI.SLOTS_0_TO_7;
	}

	@Override
	public int getEditorBackSlot() {
		return 8;
	}

	// clone
	protected PPEnum() {
		super();
	}

	@Override
	public PPEnum<T> clone() {
		// clone
		PPEnum<T> clone = (PPEnum<T>) super.clone();
		// clone properties
		clone.enumClass = enumClass;
		// success
		return clone;
	}

	// mob types
	private static final List<EntityType> mobTypes = new ArrayList<EntityType>();

	static {
		List<String> ok = Utils.asList("ARMOR_STAND", "BAT", "BLAZE", "BOAT", "CAVE_SPIDER", "CHICKEN", "COD", "COW", "CREEPER", "DOLPHIN", "DONKEY", "ELDER_GUARDIAN", "ENDER_CRYSTAL", "ENDER_DRAGON", "ENDERMAN", "ENDERMITE", "EVOKER", "EXPERIENCE_ORB", "GHAST", "GIANT", "GUARDIAN", "HORSE", "HUSK", "ILLUSIONER", "IRON_GOLEM", "LLAMA", "MAGMA_CUBE", "MINECART", "MINECART_CHEST", "MINECART_COMMAND", "MINECART_FURNACE", "MINECART_HOPPER", "MINECART_MOB_SPAWNER", "MINECART_TNT", "MULE", "MUSHROOM_COW", "OCELOT", "PARROT", "PHANTOM", "PIG", "PIG_ZOMBIE", "PLAYER", "POLAR_BEAR", "PRIMED_TNT", "PUFFERFISH", "RABBIT", "SALMON", "SHEEP", "SHULKER", "SILVERFISH", "SKELETON", "SKELETON_HORSE", "SLIME", "SNOWMAN", "SPIDER", "SQUID", "STRAY", "THROWN_EXP_BOTTLE", "TROPICAL_FISH", "TURTLE", "VEX", "VILLAGER", "VINDICATOR", "WITCH", "WITHER", "WITHER_SKELETON", "WOLF", "ZOMBIE", "ZOMBIE_HORSE", "ZOMBIE_VILLAGER");
		for (EntityType type : EntityType.values()) {
			if (ok.contains(type.toString())) {
				mobTypes.add(type);
			}
		}
	}

}
