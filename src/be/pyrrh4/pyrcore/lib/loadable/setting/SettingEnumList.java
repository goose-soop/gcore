package be.pyrrh4.pyrcore.lib.loadable.setting;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.loadable.AbstractListSetting;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorCallback;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorItem;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.placeholder.PlaceholderParser;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.util.input.ChatInput;

public class SettingEnumList<T extends Enum<T>> extends AbstractListSetting<List<T>> {

	// base
	private Class<T> typeClass;

	public SettingEnumList(String id, List<String> def, boolean mandatory, Class<T> typeClass, List<String> description) {
		this(id, def, mandatory, false, typeClass, description);
	}

	public SettingEnumList(String id, List<String> def, boolean mandatory, boolean compact, Class<T> typeClass, List<String> description) {
		super(id, def, mandatory, compact, "list of " + Utils.separateOnCaps(typeClass.getSimpleName()), description);
		this.typeClass = typeClass;
	}

	public Class<T> getTypeClass() {
		return typeClass;
	}

	// get
	@Override
	public List<T> parse(List<String> rawList) {
		List<T> result = new ArrayList<T>();
		for (String raw : rawList) {
			T t = Utils.valueOfOrNull(typeClass, raw);
			if (t == null) throw new IllegalArgumentException("'raw' isn't a valid " + Utils.separateOnCaps(typeClass.getSimpleName()));
			result.add(t);
		}
		return result;
	}

	@Override
	public List<T> getParsed(Player player) {
		List<T> result = super.getParsed(player);
		return result != null ? result : new ArrayList<T>();
	}

	@Override
	public void initializeEditorItem(final EditorGUI parent, final int slot, final EditorCallback onModif) {
		// sub
		parent.setRegularItem(new EditorItem(getId(), slot, Mat.ENDER_CHEST, getId(), fillEditorItemLore()) {
			@Override
			public void onClick(Player player, ClickType clickType, int pageIndex) {
				// init
				EditorGUI sub = new EditorGUI(parent, getId()) {
					private final EditorGUI subThis = this;
					@Override
					protected void fill() {
						// set line icons
						int slot = -1;
						if (getValue() != null) {
							for (String line : getValue()) {
								final int index = ++slot;
								setRegularItem(new EditorItem("line_" + index, index, Mat.ENDER_CHEST, "§6" + (index + 1), PCLocale.GUI_GENERIC_EDITORTEXTLINELORE.getLines("{value_mandatory}", isMandatory() ? "§cyes" : "§ano", "{value_type}", "text line", "{value_current}", line)) {
									@Override
									protected void onClick(Player player, ClickType clickType, int pageIndex) {
										// edit
										if (clickType.isLeftClick()) {
											// sub
											EditorGUI subEdit = new EditorGUI(parent, getId(), 9, 8) {
												private final EditorGUI subEditThis = this;
												@Override
												protected void fill() {
													// select
													subEditThis.setRegularItem(new EditorItem("select", 2, Mat.ENDER_CHEST, PCLocale.GUI_GENERIC_EDITORENUMSELECT.getLine(), fillEditorItemLore(PCLocale.GUI_GENERIC_EDITORENUMSELECTLORE.getLines())) {
														@Override
														protected void onClick(Player player, ClickType clickType, int pageIndex) {
															// init
															EditorGUI subEnum = new EditorGUI(subThis, "Select") {
																private EditorGUI subEnumThis = this;
																@Override
																protected void fill() {
																	// values
																	int valueSlot = -1;
																	for (final T value : Utils.asObjectSortedList(Utils.asList(typeClass.getEnumConstants()))) {
																		subEnumThis.setRegularItem(new EditorItem("value_" + value.name(), ++valueSlot, Mat.ENDER_CHEST, value.name(), null) {
																			@Override
																			protected void onClick(Player player, ClickType clickType, int pageIndex) {
																				getValue().set(index, value.name());
																				onModif.callback();
																				subEditThis.open(player);
																			}
																		});
																	}
																	// back
																	subEnumThis.setPersistentItem(new EditorItem("back", 52, Mat.ARROW, PCLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
																		@Override
																		protected void onClick(Player player, ClickType clickType, int pageIndex) {
																			subEditThis.open(player);
																		}
																	});
																}
															};
															// open sub
															subEnum.open(player);
														}
													});
													// raw
													subEditThis.setRegularItem(new EditorItem("raw", 3, Mat.COMMAND_BLOCK, PCLocale.GUI_GENERIC_EDITORRAW.getLine(), fillEditorItemLore(PCLocale.GUI_GENERIC_EDITORRAWLORE.getLines("{placeholders}", PlaceholderParser.describeAll()))) {
														@Override
														protected void onClick(Player player, ClickType clickType, int pageIndex) {
															// chat
															player.closeInventory();
															PCLocale.MSG_GENERIC_CHATINPUT.send(player);
															PyrCore.inst().getChatInputs().put(player, new ChatInput() {
																@Override
																public void onChat(Player player, String value) {
																	if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
																		getValue().set(index, value);
																		onModif.callback();
																	}
																	subEditThis.open(player);
																}
															});
														}
													});
													// delete
													subEditThis.setPersistentItem(new EditorItem("delete", 6, Mat.TNT_MINECART, PCLocale.GUI_GENERIC_EDITORITEMDELETESELF.getLine(), PCLocale.GUI_GENERIC_EDITORITEMDELETESELFLORE.getLines()) {
														@Override
														protected void onClick(Player player, ClickType clickType, int pageIndex) {
															setValue(null);
															onModif.callback();
															open(player);
														}
													});
													// back
													subEditThis.setPersistentItem(new EditorItem("back", 8, Mat.ARROW, PCLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
														@Override
														protected void onClick(Player player, ClickType clickType, int pageIndex) {
															parent.open(player);
														}
													});
												}
											};
											// open sub
											subEdit.open(player);
										}
										// delete
										else if (clickType.isRightClick()) {
											getValue().remove(index);
											onModif.callback();
											open(player);
										}
									}
								});
							}
						}
						// new
						setPersistentItem(new EditorItem("add", 49, Mat.BLAZE_ROD, PCLocale.GUI_GENERIC_EDITORITEMADD.getLine(), null) {
							@Override
							protected void onClick(Player player, ClickType clickType, int pageIndex) {
								if (getValue() == null) {
									setValue(Utils.asList(typeClass.getEnumConstants()[0].name()));
								} else {
									getValue().add(typeClass.getEnumConstants()[0].name());
								}
								onModif.callback();
								open(player);
							}
						});
						// delete
						setPersistentItem(new EditorItem("delete", 46, Mat.TNT_MINECART, PCLocale.GUI_GENERIC_EDITORITEMDELETESELF.getLine(), PCLocale.GUI_GENERIC_EDITORITEMDELETESELFLORE.getLines()) {
							@Override
							protected void onClick(Player player, ClickType clickType, int pageIndex) {
								if (getValue() != null) {
									getValue().clear();
								}
								onModif.callback();
								open(player);
							}
						});
						// back
						setPersistentItem(new EditorItem("back", 52, Mat.ARROW, PCLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
							@Override
							protected void onClick(Player player, ClickType clickType, int pageIndex) {
								parent.open(player);
							}
						});
					}
				};
				// open sub
				sub.open(player);
			}
		});
	}

}
