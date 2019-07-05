package com.guillaumevdn.gcore.lib.npc.behavior.action;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.gui.GUI;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.ContainerParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.ParseableContainment;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorItem;
import com.guillaumevdn.gcore.lib.parseable.editor.ModifCallback;
import com.guillaumevdn.gcore.lib.util.Utils;

/**
 * Represents the configuration and data of a behavior action
 */
public abstract class BAction extends ContainerParseable {

	// base
	private BActionType type;

	public BAction(String id, BActionType type, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "npc behavior action", mandatory, editorSlot, editorIcon, editorDescription);
		this.type = type;
	}

	// get
	public BActionType getType() {
		return type;
	}

	/**
	 * Run this action
	 * @param player the player
	 * @param npc the npc
	 */
	public abstract void run(Player player, Npc npc);

	// save
	@Override
	public void save(ConfigData data) {
		super.save(data);
		if (data != null) {
			data.getConfig().set(data.getPath() + ".type", type.getId());
		}
	}

	// editor
	@Override
	public List<String> describe(int depth) {
		List<String> desc = super.describe(depth);
		String spaces = Utils.copyString(" ", depth + 1);
		desc.add(1, spaces + " §6> type : §e" + type.getId());
		return desc;
	}

	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// add type selector
		gui.setRegularItem(new EditorItem("object_type", 0, Mat.ENDER_CHEST, "§6type", GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORACTIONTYPELORE.getLines()) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				// create sub GUI
				String name = Utils.getNewInventoryName(gui.getName(), "type");
				EditorGUI sub = new EditorGUI(getLastData().getPlugin(), gui, name, 9, GUI.SLOTS_0_TO_7) {
					private EditorGUI subThis = this;
					@Override
					protected void fill() {
						// current
						EditorGUI.fillItemCurrent(subThis, player, "type", Utils.asList(type.getId()), null, "behavior event type", isMandatory(), getEditorIcon(), 0, onModif);
						// select
						setRegularItem(new EditorItem("control_item_select", 2, Mat.ENDER_CHEST, GLocale.GUI_GENERIC_EDITORENUMSELECT.getLine(), GLocale.GUI_GENERIC_EDITORENUMSELECTLORE.getLines()) {
							@Override
							protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
								// selection gui
								EditorGUI subSelection = new EditorGUI(getLastData().getPlugin(), gui, Utils.getNewInventoryName(gui.getName(), "Select"), 54, GUI.SLOTS_0_TO_44) {
									@Override
									protected void fill() {
										// add values
										for (final BActionType val : Utils.asSortedList(BActionType.values(), Utils.objectSorter)) {
											final String valName = val.getId();
											setRegularItem(new EditorItem("value_" + valName, -1, Mat.ENDER_CHEST, "§6" + valName, null) {
												@Override
												protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
													BAction curr = BAction.this;
													final BAction result = val.createNew(curr.getId(), curr.getParent(), curr.getLastData().clone(), false, curr.isMandatory(), curr.getEditorSlot(), curr.getEditorIcon(), curr.getEditorDescription());
													// error loading
													if (result == null) {
														player.sendMessage("§cAn unknown error occured, check the console for more details.");
														gui.open(player);
													}
													// success
													else {
														// replace result
														try {
															((ParseableContainment<BAction>) BAction.this.getParent()).replaceContaining(result);
														} catch (Throwable ignored) {}
														onModif.callback(subThis, player);
														// create result GUI
														String name = Utils.getNewInventoryName(gui.getName(), result.getId());
														EditorGUI comp = new EditorGUI(result.getLastData().getPlugin(), gui.getParent(), name, result.getEditorSize(), result.getEditorRegularSlots()) {
															private EditorGUI compThis = this;
															@Override
															protected void fill() {
																result.fillEditor(compThis, player, onModif);
																// back item (do it there after filling the other GUI because otherwise it doesn't appear somehow)
																compThis.setPersistentItem(new EditorItem("control_item_back", result.getEditorBackSlot(), Mat.ARROW, GLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
																	@Override
																	protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
																		gui.getParent().open(player);
																	}
																});
															}
														};
														// open it
														comp.open(player);
													}
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
								subSelection.open(player);
							}
						});
						// back item
						setPersistentItem(new EditorItem("control_item_back", 8, Mat.ARROW, GLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
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
		// super
		super.fillEditor(gui, player, onModif);
	}

	// static methods
	/**
	 * Load an element from a configuration
	 * @return the loaded behavior action, or null if couldn't instantiate it for some reason
	 */
	public static BAction load(String id, Parseable parent, ConfigData data, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		// compact
		BActionType type = null;
		// missing type setting
		data.setContains(data.getConfig().contains(data.getPath() + ".type"));
		if (!data.contains()) {
			data.log("missing primitive setting 'type' (must be a behavior action type)");
			return null;
		}
		// invalid type
		type = BActionType.valueOf(data.getConfig().getString(data.getPath() + ".type", ""));
		if (type == null) {
			data.log("invalid primitive setting 'type' (must be a behavior action type)");
			return null;
		}
		// create
		return type.createNew(id, parent, data, true, mandatory, editorSlot, editorIcon, editorDescription);
	}

}
