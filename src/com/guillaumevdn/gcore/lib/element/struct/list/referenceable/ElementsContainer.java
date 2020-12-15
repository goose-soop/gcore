package com.guillaumevdn.gcore.lib.element.struct.list.referenceable;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.SortedLowerCaseHashMap;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.element.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.element.struct.SuperElement;
import com.guillaumevdn.gcore.lib.exception.ConfigError;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.object.Optional;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.tuple.IntegerPair;

/**
 * @author GuillaumeVDN
 */
public abstract class ElementsContainer<V extends SuperElement> {

	private static final DateTimeFormatter DELETE_FILE_LOCALDATETIME_FORMAT = DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'-'HH'-'mm'-'ss");

	private final GPlugin plugin;
	private final String typeName;
	private final Class<V> typeClass;
	private final File baseFolder;
	private final SortedLowerCaseHashMap<V> elements = SortedLowerCaseHashMap.keySorted();

	public ElementsContainer(GPlugin plugin, String typeName, Class<V> typeClass, File baseFolder) {
		this.plugin = plugin;
		this.typeName = typeName;
		this.typeClass = typeClass;
		this.baseFolder = baseFolder;
	}

	// get
	public final GPlugin getPlugin() {
		return plugin;
	}

	public final Class<V> getTypeClass() {
		return typeClass;
	}

	public final String getTypeName() {
		return typeName;
	}

	public final File getBaseFolder() {
		return baseFolder;
	}

	public final Collection<V> values() {
		return elements.values(); // already immutable
	}

	public final boolean isEmpty() {
		return elements.isEmpty();
	}

	public final Map<String, Mat> getIcons() {
		return CollectionUtils.asMap(map -> values().forEach(value -> map.put(value.getId(), value.editorIconType())));
	}

	public final Optional<V> getElement(String key) {
		return Optional.of(elements.get(key));
	}

	// set
	public final void load() throws Throwable {
		elements.clear();
		doLoad(baseFolder);
		if (!elements.isEmpty()) {
			plugin.getMainLogger().info("Successfully loaded " + StringUtils.pluralizeAmountDesc(typeName, elements.size()) + " : " + StringUtils.toTextString(", ", elements.keySet()));
		}
	}

	private void doLoad(File file) throws Throwable {
		if (!file.exists()) { // might happen if the folder wasn't created yet
			return;
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				doLoad(f);
			}
		} else {
			// not a .yml file, skip it
			if (!file.getName().toLowerCase().endsWith(".yml")) {
				return;
			}
			// can't load for another reason
			String id = FileUtils.getSimpleName(file).toLowerCase();
			ensureCanLoad(file, id);
			// load element
			V elem = createElement(file, id);
			elem.read();
			setElement(elem);
			// notify loading errors
			if (!elem.getLoadErrors().isEmpty()) {
				getPlugin().getMainLogger().error("Errors were found when loading " + getTypeName() + " " + id + " :", true);
				elem.getLoadErrors().forEach(error -> getPlugin().getMainLogger().error("- " + error, true));
			}
		}
	}

	protected void ensureCanLoad(File file, String id) throws ConfigError {
		// invalid id
		if (!StringUtils.isAlphanumeric(id.replace('_', 'a'))) {
			throw new ConfigError("Id '" + id + "' is not allowed for a " + typeName);
		}
	}

	public final void setElement(V element) {
		elements.put(element.getId(), element);
	}

	protected abstract V createElement(File file, String id);

	public final void delete(V element) {
		delete(element, true);
	}

	public final void delete(V element, boolean moveToDeleted) {
		elements.remove(element.getId());
		File ownFile = element.getOwnFile();
		if (ownFile != null) {
			if (ownFile.exists()) {
				if (moveToDeleted) {
					File newFile = new File(baseFolder + "/deleted/" + DELETE_FILE_LOCALDATETIME_FORMAT.format(ConfigGCore.timeNow()) + "_" + element.getId() + ".ymlr");
					newFile.getParentFile().mkdirs();
					if (!ownFile.renameTo(newFile)) {
						FileUtils.delete(ownFile);
					}
				} else {
					FileUtils.delete(ownFile);
				}
			}
		} else {
			YMLConfiguration config = element.getConfiguration();
			if (config.contains(element.getConfigurationPath())) {
				config.write(element.getConfigurationPath(), null);
				config.save();
			}
		}
	}

	// editor
	public EditorGUI editorGUI(GPlugin plugin, String title, ClickCall fromCall) {
		EditorGUI editor = new EditorGUI(plugin, title, fromCall) {
			@Override
			protected boolean doFill() {
				// values
				int page = 0;
				int slot = -1;
				for (V element : elements.values().stream().sorted().collect(Collectors.toList())) {
					// build icon
					ItemStack icon = element.editorIcon();
					ItemMeta meta = icon.getItemMeta();
					List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
					lore.add("§r");
					lore.addAll(TextEditorGeneric.controlDelete.parseLines());
					meta.setLore(lore);
					icon.setItemMeta(meta);
					// build location
					if (++slot > getType().getRegularItemSlotsEnd()) {
						++page;
						slot = 0;
					}
					// set item
					setRegularItem(new GUIItem("element_" + element.getId(), CollectionUtils.asList(IntegerPair.of(page, slot)), icon, call -> {
						// control drop : delete
						if (call.getType().equals(ClickType.CONTROL_DROP)) {
							delete(element);
							refill();
						}
						// other
						else {
							element.onEditorClick(call);
						}
					}));
				}
				// create item
				setPersistentItem(new GUIItem("new_element", 50, ItemUtils.createItem(CommonMats.BLAZE_ROD, TextEditorGeneric.controlAddElementName.parseLine(), null), call -> {
					// left-click : create
					if (call.getType().equals(ClickType.LEFT)) {
						WorkerGCore.inst().awaitChat(call.getClicker(), TextEditorGeneric.messageElementCreateEnterId, raw -> {
							// invalid id, or already exists
							final String id = raw.toLowerCase().trim();
							final File file = new File(baseFolder + "/" + id + ".yml");
							if (!StringUtils.isAlphanumeric(id.replace("_", ""))) {
								TextEditorGeneric.messageElementCreateInvalidId.replace("{value}", () -> id).send(call.getClicker());
								call.getGUI().openFor(call.getClicker(), call.getPageIndex());
							} else if (getElement(id).isPresent() || file.exists()) {
								TextEditorGeneric.messageElementCreateAlreadyExists.replace("{value}", () -> id).send(call.getClicker());
								call.getGUI().openFor(call.getClicker(), call.getPageIndex());
							}
							// create element
							else {
								V value = createElement(file, id);
								try {
									value.write();
									value.getConfiguration().save();
									elements.put(id, value);
								} catch (Throwable exception) {
									exception.printStackTrace();
								}
								// reopen GUI (that refreshes it since it's an editor GUI)
								call.getGUI().openFor(call.getClicker(), call.getPageIndex());
							}
						}, () -> call.getGUI().openFor(call.getClicker(), call.getPageIndex()));
					}
				}));
				// done
				return super.doFill();
			}
		};
		return editor;
	}

}
