package be.pyrrh4.pyrcore.lib.loadable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.configuration.YMLConfiguration;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorCallback;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorItem;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingBoolean;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingDouble;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingEnum;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingEnumList;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingFloat;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingInteger;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingLocation;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingMat;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingMatList;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingPerm;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingPotionEffectType;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingString;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingStringList;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingText;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingUUID;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingWorld;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.util.Utils;

public abstract class Loadable<T> implements Comparable<Loadable<?>> {

	// fields
	private Loadable<?> parent;
	private String id;
	private boolean mandatory;
	private Mat icon;
	private List<String> description;

	private Map<String, AbstractSetting<?>> settings = new HashMap<String, AbstractSetting<?>>();
	private Map<String, Loadable<?>> loadableSettings = new HashMap<String, Loadable<?>>();

	protected String configRoot = null;
	protected boolean configContains = false;
	private LoadResult<T> loadResult = new LoadResult<T>();

	public Loadable(Loadable<?> parent, String id, boolean mandatory, Mat icon, List<String> description) {
		this.parent = parent;
		this.id = id;
		this.mandatory = mandatory;
		this.icon = icon;
		this.description = description;
		loadResult.setResult((T) this);
	}

	// get
	public Loadable<?> loadParent() {
		return parent;
	}
	
	public void loadSetParent(Loadable<?> parent) {
		this.parent = parent;
	}

	public String getId() {
		return id;
	}

	public boolean loadMandatory() {
		return mandatory;
	}

	public Mat loadIcon() {
		return icon;
	}

	public List<String> loadDescription() {
		return description;
	}

	protected String loadConfigRoot() {
		return configRoot;
	}

	public LoadResult<T> loadResult() {
		return loadResult;
	}

	// settings getters
	public Map<String, AbstractSetting<?>> getSettings() {
		return settings;
	}

	public AbstractSetting<?> getSetting(String id) {
		return settings.get(id);
	}

	public <S extends AbstractSetting<?>> S getSetting(String id, Class<S> typeClass) {
		return (S) settings.get(id);
	}

	protected SettingBoolean getSettingBoolean(String id) {
		return (SettingBoolean) getSetting(id);
	}

	protected SettingDouble getSettingDouble(String id) {
		return (SettingDouble) getSetting(id);
	}

	protected <E extends Enum<E>> SettingEnum<E> getSettingEnum(String id, Class<E> enumClass) {
		return (SettingEnum<E>) getSetting(id);
	}

	protected <E extends Enum<E>> SettingEnumList<E> getSettingEnumList(String id, Class<E> enumClass) {
		return (SettingEnumList<E>) getSetting(id);
	}

	protected SettingFloat getSettingFloat(String id) {
		return (SettingFloat) getSetting(id);
	}

	protected SettingInteger getSettingInteger(String id) {
		return (SettingInteger) getSetting(id);
	}

	protected SettingLocation getSettingLocation(String id) {
		return (SettingLocation) getSetting(id);
	}

	protected SettingMat getSettingMat(String id) {
		return (SettingMat) getSetting(id);
	}

	protected SettingMatList getSettingMatList(String id) {
		return (SettingMatList) getSetting(id);
	}

	protected SettingPerm getSettingPerm(String id) {
		return (SettingPerm) getSetting(id);
	}

	protected SettingPotionEffectType getSettingPotionEffectType(String id) {
		return (SettingPotionEffectType) getSetting(id);
	}

	protected SettingString getSettingString(String id) {
		return (SettingString) getSetting(id);
	}

	protected SettingStringList getSettingStringList(String id) {
		return (SettingStringList) getSetting(id);
	}

	protected SettingWorld getSettingWorld(String id) {
		return (SettingWorld) getSetting(id);
	}

	protected SettingText getSettingText(String id) {
		return (SettingText) getSetting(id);
	}

	protected SettingUUID getSettingUUID(String id) {
		return (SettingUUID) getSetting(id);
	}

	// loadable getters
	public Map<String, Loadable<?>> getLoadSettings() {
		return loadableSettings;
	}

	protected <L extends Loadable<? super L>> L getLoadSetting(String id) {
		return (L) loadableSettings.get(id);
	}

	protected <L extends Loadable<? super L>> L getLoadSetting(String id, Class<L> clazz) {
		return (L) loadableSettings.get(id);
	}

	// methods
	public void registerSetting(AbstractSetting<?> setting) {
		if (settings.containsKey(setting.getId())) {
			throw new IllegalArgumentException("attempting to register setting " + setting.getId() + " but it already exists");
		}
		settings.put(setting.getId(), setting);
	}

	public void registerSetting(Loadable<?> setting) {
		if (loadableSettings.containsKey(setting.getId())) {
			throw new IllegalArgumentException("attempting to register loadable setting " + setting.getId() + " but it already exists");
		}
		loadableSettings.put(setting.getId(), setting);
	}

	public boolean loadConfigContains() {
		return configContains;
	}

	public void loadSettings(YMLConfiguration config, String configRoot) {
		// if config doesn't contains this loadable
		this.configContains = config.contains(this.configRoot = configRoot);
		if (!configContains) {
			// log error if we're mandatory
			if (loadMandatory()) {
				loadResult().setError("missing setting of type '" + Utils.separateOnCaps(getClass().getSimpleName()) + "' at '" + configRoot + "'");
				loadResult().logError();
			}
			// don't keep going
			return;
		}
		// for every setting
		for (AbstractSetting<?> setting : Utils.asObjectSortedList(settings.values())) {
			// load
			LoadResult<AbstractSetting<?>> result = new LoadResult<AbstractSetting<?>>(loadResult().getConfigErrorPrefix());
			setting.load(config, (configRoot != null && !configRoot.isEmpty() ? configRoot + "." : "") + setting.getId(), result);
			// log error if it's not about a missing thing and we're mandatory
			if (result.getError() != null && (result.getError().contains("missing") ? loadMandatory() : true)) {
				result.logError();
				loadResult().setError(result.getError());
			}
		}
		// for every loadableSettings
		for (Loadable<?> setting : Utils.asObjectSortedList(loadableSettings.values())) {
			// load
			setting.loadSettings(config, (configRoot != null && !configRoot.isEmpty() ? configRoot + "." : "") + setting.getId());
			// keep track of error
			if (setting.loadResult().getError() != null && !setting.loadResult().getError().contains("missing")) {
				loadResult().setError(setting.loadResult().getError());
			}
		}
	}

	public void saveSettings(YMLConfiguration config, String configRoot) {
		if (configRoot == null) configRoot = "";
		this.configRoot = configRoot;
		config.set(configRoot, null);
		for (AbstractSetting<?> setting : Utils.asObjectSortedList(settings.values())) {
			setting.save(config, (!configRoot.isEmpty() ? configRoot + "." : "") + setting.getId());
		}
		for (Loadable<?> setting : Utils.asObjectSortedList(loadableSettings.values())) {
			setting.saveSettings(config, (!configRoot.isEmpty() ? configRoot + "." : "") + setting.getId());
		}
	}

	public void loadReset() {
		for (AbstractSetting<?> setting : Utils.asObjectSortedList(settings.values())) {
			if (setting instanceof AbstractUniqueSetting<?>) {
				AbstractUniqueSetting<?> unique = (AbstractUniqueSetting<?>) setting;
				unique.setValue(unique.getDef());
			} else if (setting instanceof AbstractListSetting<?>) {
				AbstractListSetting<?> unique = (AbstractListSetting<?>) setting;
				unique.setValue(unique.getDef());
			}
		}
		for (Loadable<?> setting : Utils.asObjectSortedList(loadableSettings.values())) {
			setting.loadReset();
		}
	}

	public void loadEditorReplace(Loadable<?> setting, Player player, EditorGUI gui, EditorGUI parent, String name, EditorCallback onModif) {
		throw new UnsupportedOperationException("can't replace loadable setting of type " + setting.getClass().getSimpleName() + " in " + getClass().getSimpleName());
	}

	public int loadEditorFillReplacers(EditorGUI gui, EditorGUI parent, String name, EditorCallback onModif) {
		return 0;
	}

	public EditorGUI loadEditorInitialize(final EditorGUI parent, final String name, final EditorCallback onModif) {
		// gui
		EditorGUI gui = new EditorGUI(parent, name) {
			private EditorGUI guiThis = this;
			@Override
			protected void fill() {
				// add setting replacers
				int slot = loadEditorFillReplacers(guiThis, parent, name, onModif) - 1;
				// add settings
				for (AbstractSetting<?> setting : Utils.asObjectSortedList(settings.values())) {
					setting.initializeEditorItem(this, ++slot, onModif);
				}
				// add loadableSettings
				for (final Loadable<?> setting : Utils.asObjectSortedList(loadableSettings.values())) {
					setRegularItem(new EditorItem(setting.getId(), ++slot, setting.loadIcon(), "§6" + setting.getId(), setting.loadEditorDescribe(setting.loadDescription(), setting.loadMandatory())) {
						@Override
						protected void onClick(Player player, ClickType clickType, int pageIndex) {
							// initialize and open sub on click
							setting.loadEditorInitialize(guiThis, setting.getId(), onModif).open(player);
						}
					});
				}
				// reset
				setPersistentItem(new EditorItem("reset", 46, Mat.TNT_MINECART, PCLocale.GUI_GENERIC_EDITORITEMDELETESELF.getLine(), PCLocale.GUI_GENERIC_EDITORITEMDELETESELFLORE.getLines()) {
					@Override
					protected void onClick(Player player, ClickType clickType, int pageIndex) {
						loadReset();
						onModif.callback();
						guiThis.open(player);
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

	public List<String> loadEditorDescribe(List<String> description, boolean mandatory) {
		return EditorGUI.fillItemLore(description, Utils.separateOnCaps(getClass().getSimpleName()), loadDescribe(), mandatory);
	}

	public List<String> loadDescribe() {
		return loadDescribe("", 1);
	}

	public List<String> loadDescribe(String spaces, int depth) {
		// too deep
		if (depth >= 3) {
			return Utils.asList(spaces + " §7...");
		}
		// describe settings
		List<String> description = Utils.emptyList();
		for (AbstractSetting<?> setting : settings.values()) {
			if (description.size() >= EditorGUI.MAX_DESC_LENGTH) {
				description.add(spaces + " §7...");
				return description;
			}
			if (Utils.instanceOf(setting, AbstractUniqueSetting.class)) {
				description.add(spaces + "§7> §6" + setting.getId() + " §7: §e" + (setting.getValue() != null ? setting.getValue() : setting.getDef()));
			} else {
				AbstractListSetting<?> listSetting = (AbstractListSetting<?>) setting;
				List<String> value = listSetting.getValue() != null && !listSetting.getValue().isEmpty() ? listSetting.getValue() : listSetting.getDef();
				if (value == null || value.isEmpty()) {
					value = Utils.asList("§7- §e/" + (listSetting.getDef() == null || listSetting.getDef().isEmpty() ? " §7§l(default)" : ""));
				} else {
					value = Utils.addBeforeAll(value, "§7- §e");
					if (value.equals(listSetting.getDef())) {
						value.add("§7§l(default)");
					}
				}
				description.add(spaces + "§7> §6" + setting.getId() + " §7: §e/");
				description.addAll(Utils.addBeforeAll(value, spaces + " "));
			}
		}
		// loadableSettings
		for (Loadable<?> setting : loadableSettings.values()) {
			if (description.size() >= EditorGUI.MAX_DESC_LENGTH) {
				description.add(spaces + " §7...");
				return description;
			}
			description.add(spaces + "§7> §6" + setting.getId() + " §7:");
			description.addAll(setting.loadDescribe(spaces + " ", depth + 1));
		}
		// return
		return description;
	}

	// overriden
	@Override
	public String toString() {
		return getId();
	}

	@Override
	public int compareTo(Loadable<?> other) {
		if (loadMandatory() && !other.loadMandatory()) return -1;
		if (other.loadMandatory() && !loadMandatory()) return 1;
		return String.CASE_INSENSITIVE_ORDER.compare(getId(), other.getId());
	}

}
