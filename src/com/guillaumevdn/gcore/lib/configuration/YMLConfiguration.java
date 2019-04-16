package com.guillaumevdn.gcore.lib.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.Logger;
import com.guillaumevdn.gcore.lib.Logger.Level;
import com.guillaumevdn.gcore.lib.gui.ItemData;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.messenger.Tab;
import com.guillaumevdn.gcore.lib.messenger.Title;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.lib.versioncompat.Compat;

/**
 * Represents a YML configuration file.
 */

@SuppressWarnings("unused")
public class YMLConfiguration {

	// ------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------

	private transient GPlugin plugin;
	transient YamlConfiguration yaml;
	File file;
	String headerComment = "";

	public GPlugin getPlugin() {
		return plugin;
	}

	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	/**
	 * @param plugin the plugin associated with this configuration
	 * @param file the file
	 * @param defaultResource the default path that will be extracted from the plugin if the file doesn't exists yet (can be null)
	 * @param createIfNoDefault true if there is no file, no default resource, and the file must still be created anyway
	 * @param load true if you wish to load the file contents (if exists) in the constructor
	 */

	public YMLConfiguration(GPlugin plugin, File file, String defaultResource, boolean createIfNoDefault, boolean load) {
		this.plugin = plugin;
		(this.file = file).getParentFile().mkdirs();
		Utils.setDefaultResource(plugin, file, defaultResource, createIfNoDefault);
		if (load && file.exists()) {
			reload(false);
		} else {
			this.yaml = new YamlConfiguration();
		}
	}

	// ------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------

	/**
	 * @return the configuration's file
	 */

	public File getFile() {
		return file;
	}

	@Deprecated
	public void reload(boolean save) {
		if (save) {
			save();
		} else {
			reload();
		}
	}

	/**
	 * Reload the configuration from file
	 */

	public void reload() {
		if (file.exists()) {
			try {
				load();
			} catch (FileNotFoundException exception) {
				exception.printStackTrace();
				Logger.log(Level.SEVERE, GCore.inst(), "Could not load the file " + file.getName());
			} catch (IOException exception) {
				exception.printStackTrace();
				Logger.log(Level.SEVERE, GCore.inst(), "Could not read lines of file " + file.getName());
			}
		}
	}

	/**
	 * Check if the configuration contains a path
	 * @param path the path
	 * @return true if the configuration contains the path
	 */

	public boolean contains(String path) {
		return yaml.contains(path.replace(" ", "").replace("\t", ""));
	}

	/**
	 * Check if the configuration contains an integer in a path
	 * @param path the path
	 * @return true if the configurations contains an integer at this path
	 */

	public boolean containsInt(String path) {
		return contains(path) ? Utils.isInteger(getString(path, "-")) : false;
	}

	/**
	 * Get the keys of a section. Doesn't include default file (if there is any) keys.
	 * @param sectionPath the path of the section
	 * @param sort must sort the keys in alphanumerical order
	 * @return the keys associated to the section (can be empty)
	 */

	public LinkedHashSet<String> getKeysForSection(String sectionPath, boolean sort) {
		sectionPath = sectionPath.replace(" ", "").replace("\t", "");
		LinkedHashSet<String> keys = new LinkedHashSet<String>();
		if (yaml.isConfigurationSection(sectionPath)) {
			if (sort) {
				List<String> list = Utils.asList(yaml.getConfigurationSection(sectionPath).getKeys(false));
				Collections.sort(list);
				keys.addAll(list);
			} else {
				keys.addAll(yaml.getConfigurationSection(sectionPath).getKeys(false));
			}
		}
		return keys;
	}

	/**
	 * Check if a path is a configuration section.
	 * @param sectionPath the path of the section
	 * @return true if it's a configuration section
	 */

	public boolean isConfigurationSection(String sectionPath) {
		return yaml.isConfigurationSection(sectionPath);
	}

	/**
	 * Get a configuration section
	 * @param sectionPath the path of the section
	 * @return the section or null if it's not a configuration section
	 */

	public ConfigurationSection getConfigurationSection(String sectionPath) {
		return isConfigurationSection(sectionPath) ? yaml.getConfigurationSection(sectionPath) : null;
	}

	/**
	 * Copy things (deep, with sections as well)
	 * @param path the first path
	 * @param target the second path
	 * @param remove must remove the first path content
	 */

	public void copy(String path, String target, boolean remove) {
		if (contains(path)) {
			// proceed section
			if (isConfigurationSection(path)) {
				for (String key : getKeysForSection(path, false)) {
					copy(path + "." + key, target + "." + key, false);
				}
				if (remove) set(path, null);
			}
			// proceed value
			else {
				set(target, getObject(path, null));
				if (remove) set(path, null);
			}
		}
	}

	/**
	 * Get the values of a section. Doesn't include default file (if there is any) values.
	 * @param sectionPath the path of the section
	 * @return the values associated to the section
	 */

	public Map<String, Object> getValuesForSection(String sectionPath)
	{
		sectionPath = sectionPath.replace(" ", "").replace("\t", "");
		Map<String, Object> values = new HashMap<String, Object>();

		for (String key : getKeysForSection(sectionPath, false)) {
			String path = sectionPath + "." + key;
			values.put(path, getObject(path, null));
		}

		return values;
	}

	/**
	 * Get a value from the configuration
	 * @param path the path
	 * @param def the default value
	 * @return the associated value
	 */

	public Object getObject(String path, Object def) {
		// has value
		if (contains(path)) {
			return yaml.get(path);
		}
		// doesn't contains
		return def;
	}

	/**
	 * Get a boolean from the configuration
	 * @param path the path
	 * @param def the default value
	 * @return the associated value
	 */

	public boolean getBoolean(String path, boolean def) {
		// has value
		if (contains(path)) {
			return yaml.getBoolean(path);
		}
		// doesn't contains
		return def;
	}

	/**
	 * Get an integer from the configuration
	 * @param path the path
	 * @param def the default value
	 * @return the associated value
	 */

	public int getInt(String path, int def) {
		// has value
		if (contains(path)) {
			return yaml.getInt(path);
		}
		// doesn't contains
		return def;
	}

	/**
	 * Get an long from the configuration
	 * @param path the path
	 * @param def the default value
	 * @return the associated value
	 */

	public long getLong(String path, long def) {
		// has value
		if (contains(path)) {
			return yaml.getLong(path);
		}
		// doesn't contains
		return def;
	}

	/**
	 * Get a byte from the configuration
	 * @param path the path
	 * @param def the default value
	 * @return the associated value
	 */

	public byte getByte(String path, byte def) {
		// has value
		if (contains(path)) {
			return (byte) yaml.getInt(path);
		}
		// doesn't contains
		return def;
	}

	/**
	 * Get a short from the configuration
	 * @param path the path
	 * @param def the default value
	 * @return the associated value
	 */

	public short getShort(String path, short def) {
		// has value
		if (contains(path)) {
			return (short) yaml.getInt(path);
		}
		// doesn't contains
		return def;
	}

	/**
	 * Get a double from the configuration
	 * @param path the path
	 * @param def the default value
	 * @return the associated value
	 */

	public double getDouble(String path, double def) {
		// has value
		if (contains(path)) {
			return yaml.getDouble(path);
		}
		// doesn't contains
		return def;
	}

	/**
	 * Get a float from the configuration
	 * @param path the path
	 * @param def the default value
	 * @return the associated value
	 */

	public float getFloat(String path, float def) {
		// has value
		if (contains(path)) {
			return (float) yaml.getDouble(path);
		}
		// doesn't contains
		return def;
	}

	/**
	 * Get a string from the configuration
	 * @param path the path
	 * @param def the default value
	 * @return the associated value
	 */

	public String getString(String path, String def) {
		// has value
		if (contains(path)) {
			return yaml.getString(path);
		}
		// doesn't contains
		return def;
	}

	/**
	 * Get a formatted string from the configuration
	 * @param path the path
	 * @param def the default value (unformatted)
	 * @return the associated value
	 */

	public String getStringFormatted(String path, String def) {
		return Utils.format(getString(path, def));
	}

	/**
	 * Get a list from the configuration
	 * @param path the path
	 * @param def the default value
	 * @return the associated value
	 */

	public List<String> getList(String path, List<String> def) {
		return getList(path, def, false);
	}

	/**
	 * Get a list from the configuration
	 * @param path the path
	 * @param def the default value
	 * @param compact true if the list can be compact in config : 'value1,value2,value3'
	 * @return the associated value
	 */

	public List<String> getList(String path, List<String> def, boolean compact) {
		// has value
		if (contains(path)) {
			List<String> list = new ArrayList<String>();
			Object get = yaml.get(path);
			// list
			if (get instanceof Collection<?>) {
				for (Object obj : (Collection<?>) get) {
					list.add(String.valueOf(obj));
				}
			} else {// string
				String string = String.valueOf(get);
				if (compact) {// compact
					list.addAll(Utils.fromNiceString(string, false));
				} else {// regular
					list.add(string);
				}
			}
			return list;
		}
		// doesn't contains
		return def;
	}

	/**
	 * Get a formatted list from the configuration
	 * @param path the path
	 * @param def the default value (unformatted)
	 * @return the associated value
	 */

	public List<String> getListFormatted(String path, List<String> def) {
		return getListFormatted(path, def, false);
	}

	/**
	 * Get a formatted list from the configuration
	 * @param path the path
	 * @param def the default value (unformatted)
	 * @param compact true if the list can be compact in config : 'value1,value2,value3'
	 * @return the associated value
	 */

	public List<String> getListFormatted(String path, List<String> def, boolean compact) {
		return Utils.format(getList(path, def, compact));
	}

	/**
	 * Get a title from the configuration
	 * @param path the path
	 * @param def the default value
	 * @return the associated value
	 */
	public Title getTitle(String path, Title def) {
		return contains(path) ? new Title(this, path) : def;
	}

	/**
	 * Get a tab from the configuration
	 * @param path the path
	 * @param def the default value
	 * @return the associated value
	 */
	public Tab getTab(String path, Tab def) {
		return contains(path) ? new Tab(this, path) : def;
	}

	/**
	 * Get an enum element from the configuration
	 * @param path the path
	 * @param enumClass the enum class
	 * @param def the default enum value (raw string name)
	 * @return the associated value
	 */

	public <T extends Enum<T>> T getEnumValue(String path, Class<T> enumClass, String def) {
		T result = Utils.valueOfOrNull(enumClass, getString(path, def));
		return result == null ? Utils.valueOfOrNull(enumClass, def) : result;
	}

	/**
	 * Get an enum element from the configuration
	 * @param path the path
	 * @param enumClass the enum class
	 * @param def the default enum value
	 * @return the associated value
	 */

	public <T extends Enum<T>> T getEnumValue(String path, Class<T> enumClass, T def) {
		T result = Utils.valueOfOrNull(enumClass, getString(path, def == null ? null : def.name()));
		return result == null ? def : result;
	}

	/**
	 * Get a location from the configuration, with the WXYZ format
	 * @param path the path
	 * @param def the default value
	 * @return the associated value
	 */

	public Location getLocationWXYZ(String path, Location def) {
		return contains(path) ? Utils.unserializeWXYZLocation(getString(path, null)) : def;
	}

	/**
	 * Get a material from the configuration
	 * @param path the path
	 * @param def the default value
	 * @param durability the mat durability
	 * @return the associated value
	 */

	public Mat getMat(String path, int durability, Mat def) {
		return contains(path) ? Mat.from(getString(path, null), durability) : def;
	}

	/**
	 * Get an item from the configuration
	 * @param path the path
	 * @return the associated value
	 */

	public ItemData getItem(String path) {
		String id = null;
		if (isConfigurationSection(path)) id = getString(path + ".id", null);
		if (id == null) {
			int idIndex = path.lastIndexOf(".") + 1;
			id = idIndex < 0 ? path : path.substring(idIndex);
		}
		return getItem(path, id, null);
	}

	/**
	 * Get an item from the configuration
	 * @param path the path
	 * @param def the default value
	 * @return the associated value
	 */

	public ItemData getItem(String path, ItemData def) {
		String id = null;
		if (isConfigurationSection(path)) id = getString(path + ".id", null);
		if (id == null) {
			int idIndex = path.lastIndexOf(".") + 1;
			id = idIndex < 0 ? path : path.substring(idIndex + 1);
		}
		return getItem(path, id, def);
	}

	/**
	 * Get an item from the configuration
	 * @param path the path
	 * @param id the item id
	 * @param def the default value
	 * @return the associated value
	 */

	public ItemData getItem(String path, String id, ItemData def) {
		// doesn't contains
		if (!contains(path)) {
			return def;
		}
		// configuration section
		if (isConfigurationSection(path)) {
			// create
			ItemData item = new ItemData(id);
			item.setSlot(getInt(path + ".slot", -1));
			item.setChance(getDouble(path + ".chance", -1D));
			item.setMaxAmount(getInt(path + ".max_amount", 0));
			item.setEnabled(getBoolean(path + ".enabled", true));

			// potion
			if (contains(path + ".potion")) {
				List<String> raw = Utils.split(",", getString(path + ".potion", null), false);
				PotionType type = Utils.valueOfOrNull(PotionType.class, raw.get(0));
				int level = Utils.isInteger(raw.get(1)) ? Integer.parseInt(raw.get(1)) : 1;
				boolean extended = Boolean.parseBoolean(raw.get(2));
				boolean splash = Boolean.parseBoolean(raw.get(3));
				Potion potion = new Potion(type, level);
				if (extended) potion.extend();
				if (splash) potion.splash();
				ItemStack it = potion.toItemStack(1);
				item.setType(Mat.from(it.getType(), it.getDurability()));
			}
			/*// enchanted book
			else if (contains(path + ".enchanted_book")) {
				String enchantedBook = getString(path + ".enchanted_book");
				item = new ItemStack(Material.ENCHANTED_BOOK);
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
				Enchantment enchant;
				if (enchantedBook.equalsIgnoreCase("RANDOM")) {
					enchant = Utils.random(Utils.asList(Enchantment.values()));
				} else {
					enchant = Utils.enchantmentOrNull(enchantedBook);
				}
				meta.addStoredEnchant(enchant, Utils.random(1, enchant.getMaxLevel()), true);
				item.setItemMeta(meta);
			}*/
			// basic item
			else {
				item.setType(Mat.from(getString(path + ".type", null), getInt(path + ".durability", 0)));
			}

			// amount
			item.setAmount(getInt(path + ".amount", 1));

			// meta
			item.setName(getStringFormatted(path + ".name", null));
			item.setLore(getListFormatted(path + ".lore", null));

			// enchants
			List<String> enchants = getList(path + ".enchants", Utils.emptyList());
			for (String enchant : enchants) {
				try {
					String[] raw = enchant.split(",");
					Enchantment ench = Compat.INSTANCE.getEnchantment(raw[0]);
					int level = Integer.parseInt(raw[1]);
					item.setEnchant(ench, level);
				} catch (Throwable ignored) {}
			}

			// effects
			List<String> effects = getList(path + ".effects", Utils.emptyList());
			for (String effect : effects) {
				try {
					String[] raw = effect.split(",");
					PotionEffectType type = Utils.potionEffectTypeOrNull(raw[0]);
					int amplifier = Integer.parseInt(raw[1]);
					int duration = Integer.parseInt(raw[2]);
					item.addPotionEffect(new PotionEffect(type, duration, amplifier));
				} catch (Throwable ignored) {}
			}

			// nbt
			String nbt = getString(path + ".nbt", null);
			if (nbt != null) {
				try {
					item.setCustomNbt(Compat.INSTANCE.unserializeNbt(nbt));
				} catch (Throwable exception) {
					exception.printStackTrace();
					GCore.inst().error("Couldn't load NBT for item " + id + " (" + getFile().getName() + ", " + path + ")");
				}
			}

			// unbreakable
			if (getBoolean(path + ".unbreakable", false)) {
				item.setUnbreakable(true);
			}

			// return
			return item;
		}
		// not a configuration section
		else {
			String raw = getString(path, "");
			List<String> split = Utils.split(" ", raw, true);
			// [type] [amount]
			if (split.size() == 2) {
				try {
					Mat type = Mat.from(split.get(0), 0);
					int amount = Integer.parseInt(split.get(2));
					return new ItemData(id, -1, type, (short) 0, 1, null, null);
				} catch (Throwable exception) {
					exception.printStackTrace();
					GCore.inst().error("Could not load item from '" + raw + "'");
					return null;
				}
			}
			// unknown model
			return null;
		}
	}

	/**
	 * Get a list item from the configuration
	 * @param path the path
	 * @return the associated value
	 */

	public List<ItemData> getItems(String path) {
		List<ItemData> items = new ArrayList<ItemData>();
		for (String key : getKeysForSection(path, false)) {
			items.add(getItem(path + "." + key));
		}
		return items;
	}

	// ----------------------------------------------------------------------
	// Reading
	// ----------------------------------------------------------------------

	/**
	 * Inner reload
	 * @throws IOException 
	 */

	private void load() throws FileNotFoundException, IOException {
		new YMLReader(this).read();
	}

	// ----------------------------------------------------------------------
	// Writer
	// ----------------------------------------------------------------------

	/**
	 * Set the file's comment header
	 */

	public void setHeader(String... header)
	{
		this.headerComment = "";

		for (String str : header) {
			headerComment += "# " + str + "\n";
		}

		headerComment += "\n";
	}

	/**
	 * Assign a value to a path
	 * @param path the path
	 * @param value the value
	 */

	public void set(String path, Object value) {
		path = path.replace(" ", "").replace("\t", "");
		// clear whole file
		if (value == null && path.isEmpty()) {
			for (String key : getKeysForSection("", false)) {
				yaml.set(key, null);
			}
		}
		// set
		else if (Utils.instanceOf(value, Set.class)) {
			yaml.set(path, Utils.asList((Set<?>) value));
		}
		// value
		else {
			yaml.set(path, value);
		}
	}

	/**
	 * Assign a location to a path
	 * @param path the path
	 * @param location the location
	 */

	public void setLocation(String path, Location location) {
		set(path, Utils.serializeWXYZLocation(location));// null checks are performed in the Utils method
	}

	/**
	 * Assign an item to a path
	 * @param path the path
	 * @param item the item
	 */

	public void setItem(String path, ItemData item) {
		setItem(path, item, false);
	}

	@Deprecated
	public void setItem(String path, ItemData item, boolean saveId) {
		// clear
		set(path, null);
		// null item
		if (item == null) return;
		// data
		if (saveId && item.getId() != null && !item.getId().isEmpty()) set(path + ".id", item.getId());
		if (item.getSlot() >= 0) set(path + ".slot", item.getSlot());
		if (item.getChance() >= 0) set(path + ".chance", item.getChance());
		// item (potion damage is saved in durability)
		String type = item.getType().toString();
		if (type.contains(":") || type.contains("(")) {
			GCore.inst().warning("Exported mat '" + type + "'. Name '" + item.getType().getModernName() + ", legacy " + item.getType().getLegacyName() + "', toString() '" + item.getType().toString() + "', material '" + (item.getType().exists() ? "null" : String.valueOf(item.getType().getCurrentMaterial())) + "'. Please notify the developer with /gcore support");
		}
		set(path + ".type", type);
		if (item.getType().getDurability() != (short) 0) set(path + ".durability", (int) item.getType().getDurability());
		if (item.isUnbreakable()) set(path + ".unbreakable", true);
		// amount
		set(path + ".amount", item.getAmount());
		// meta
		if (item.getName() != null) set(path + ".name", retranslateColorCodes(item.getName()));
		if (item.getLore() != null && !item.getLore().isEmpty()) set(path + ".lore", retranslateColorCodes(item.getLore()));
		// enchants
		if (!item.getEnchants().isEmpty()) {
			List<String> enchants = new ArrayList<String>();
			for (Enchantment enchant : item.getEnchants().keySet()) {
				enchants.add(enchant.getName() + "," + item.getEnchants().get(enchant));
			}
			set(path + ".enchants", enchants);
		}
		// nbt
		try {
			if (item.getCustomNbt() != null) {
				set(path + ".nbt", Compat.INSTANCE.serializeNbt(item.getCustomNbt()));
			}
		} catch (Throwable exception) {
			exception.printStackTrace();
		}
	}

	private List<String> retranslateColorCodes(List<String> list) {// TODO : do a proper Utils method and color codes list
		for (int i = 0; i < list.size(); i++) {
			list.set(i, retranslateColorCodes(list.get(i)));
		}
		return list;
	}

	private String retranslateColorCodes(String string) {// TODO : do a proper Utils method and color codes list
		if (string.contains("§")) {
			for (char c : Arrays.asList('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f')) {
				if (string.contains("§" + c)) string = string.replace("§" + c, "&" + c);
			}
		}
		return string;
	}

	// Save

	/**
	 * Save the file (override) with the keys/values of this object
	 */
	public void save()
	{
		try {
			innerSave();
		} catch (IOException exception) {
			exception.printStackTrace();
			Logger.log(Level.SEVERE, GCore.inst(), "Could not save the file while reloading " + file.getName());
		}
	}

	private void innerSave() throws IOException
	{
		// Resetting file
		if (file.exists()) {
			file.delete();
			file.createNewFile();
		} else {
			file.getParentFile().mkdirs();
		}

		// Save file
		yaml.save(file);

		// Header
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
		String ln = null;
		while ((ln = reader.readLine()) != null) {
			lines.add(ln);
		}
		reader.close();

		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);

		if (headerComment != null) {
			writer.write(headerComment);
		}

		for (String line : lines) {
			writer.write(line + "\n");
		}

		writer.close();
	}

	// ----------------------------------------------------------------------
	// Other utils
	// ----------------------------------------------------------------------

	public String getClassName(Class<?> className)
	{
		String all = className.getName();
		String[] split = all.split("\\.");

		if (split.length == 0) {
			return all;
		}

		return split[split.length - 1];
	}

	private boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (Throwable exception) {
			return false;
		}
	}

	private void replace(List<String> list, String str, String repl)
	{
		for (int i = 0; i < list.size(); i++) {
			list.set(i, list.get(i).replace(str, repl));
		}
	}

	private static int countChars(char ch, String str)
	{
		int result = 0;

		for (char c : str.toCharArray()) {
			if (c == ch) {
				result++;
			}
		}

		return result;
	}
}
