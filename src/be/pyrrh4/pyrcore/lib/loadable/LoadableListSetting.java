package be.pyrrh4.pyrcore.lib.loadable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.configuration.YMLConfiguration;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorCallback;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorItem;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.util.input.ChatInput;

public abstract class LoadableListSetting<T extends Loadable<T>> extends Loadable<LoadableListSetting<T>> {

	// base
	private Map<String, T> list = new HashMap<String, T>();
	private boolean upperCaseKeys;

	public LoadableListSetting(Loadable<?> parent, String id, boolean mandatory, Mat icon, List<String> description) {
		this(parent, id, mandatory, false, icon, description);
	}

	public LoadableListSetting(Loadable<?> parent, String id, boolean mandatory, boolean upperCaseKeys, Mat icon, List<String> description) {
		super(parent, id, mandatory, icon, description);
		this.upperCaseKeys = upperCaseKeys;
	}

	// abstract methods
	protected abstract T instantiate(Loadable<?> parent, String id, boolean mandatory, Mat icon, List<String> description);

	public void addSample() {
		list.put("1", instantiate(this, "1", loadMandatory(), loadIcon(), Utils.asList("Sample")));
	}

	// get
	public Map<String, T> list() {
		return list;
	}

	// overriden methods
	@Override
	public void loadSettings(YMLConfiguration config, String configRoot) {
		this.configContains = config.contains(this.configRoot = configRoot);
		for (String id : config.getKeysForSection(configRoot, false)) {
			String error = loadElement(config, configRoot + "." + id, id);
			loadResult().setError(error);
		}
	}

	private String loadElement(YMLConfiguration config, String configRoot, String id) {
		T elem = instantiate(this, id, loadMandatory(), loadIcon(), null);
		elem.loadSettings(config, configRoot);
		list.put(id, elem);
		return elem.loadResult().getError();
	}

	@Override
	public void saveSettings(YMLConfiguration config, String configRoot) {
		for (T elem : Utils.asList(list.values())) {
			elem.saveSettings(config, configRoot + "." + elem.getId());
		}
	}

	@Override
	public void loadReset() {
		list.clear();
	}

	@Override
	public EditorGUI loadEditorInitialize(final EditorGUI parent, String name, final EditorCallback onModif) {
		// gui
		EditorGUI gui = new EditorGUI(parent, name) {
			private final EditorGUI guiThis = this;
			private boolean awaitingDelete = false;
			@Override
			protected void fill() {
				// add elements
				int slot = -1;
				for (final T element : Utils.asObjectSortedList(list.values())) {
					setRegularItem(new EditorItem("element_" + element.getId(), ++slot, Mat.DIAMOND, "§6" + element.getId(), element.loadEditorDescribe(null, true)) {
						@Override
						protected void onClick(Player player, ClickType clickType, int pageIndex) {
							// delete branch
							if (awaitingDelete) {
								awaitingDelete = false;
								// delete and callback
								list.remove(element.getId());
								onModif.callback();
								// open
								guiThis.open(player);
								return;
							}
							// initialize element GUI and open it
							element.loadEditorInitialize(guiThis, "§6" + element.getId(), onModif).open(player);
						}
					});
				}
				// new
				setPersistentItem(new EditorItem("add", 49, Mat.BLAZE_ROD, PCLocale.GUI_GENERIC_EDITORITEMADD.getLine(), null) {
					@Override
					protected void onClick(Player player, ClickType clickType, int pageIndex) {
						player.closeInventory();
						PCLocale.MSG_GENERIC_CHATINPUTID.send(player);
						PyrCore.inst().getChatInputs().put(player, new ChatInput() {
							@Override
							public void onChat(Player player, String value) {
								if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
									value = value.replace(" ", "_");
									value = upperCaseKeys ? value.toUpperCase() : value.toLowerCase();
									if (!Utils.isAlphanumeric(value.replace("_", ""))) {
										PCLocale.MSG_GENERIC_INVALIDALPHANUMERIC.send(player, "{plugin}", PyrCore.inst().getName(), "{error}", value);
									} else if (list.containsKey(value)) {
										PCLocale.MSG_GENERIC_DUPLICATEELEMENT.send(player, "{id}", value);
									} else {
										list.put(value, instantiate(LoadableListSetting.this, value, LoadableListSetting.this.loadMandatory(), LoadableListSetting.this.loadIcon(), null));
										onModif.callback();
									}
								}
								guiThis.open(player);
							}
						});
					}
				});
				// delete
				setPersistentItem(new EditorItem("delete", 46, Mat.TNT_MINECART, PCLocale.GUI_GENERIC_EDITORITEMDELETE.getLine(), PCLocale.GUI_GENERIC_EDITORITEMDELETELORE.getLines()) {
					@Override
					protected void onClick(Player player, ClickType clickType, int pageIndex) {
						awaitingDelete = true;
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
		// return
		return gui;
	}

	@Override
	public List<String> loadDescribe(String spaces, int depth) {
		// too deep
		if (depth >= 3) {
			return Utils.asList(spaces + " §7...");
		}
		// super
		List<String> description = super.loadDescribe(spaces, depth);
		// describe list elements
		for (T element : Utils.asObjectSortedList(list.values())) {
			if (description.size() >= EditorGUI.MAX_DESC_LENGTH) {
				description.add(spaces + " §7...");
				return description;
			}
			description.add(spaces + "§7> §6" + element.getId() + " §7:");
			description.add(element.loadDescribe(spaces + " ", depth + 1).get(0));// add only the type
			description.add(spaces + "  §7...");
		}
		// return
		return description;
	}

}
