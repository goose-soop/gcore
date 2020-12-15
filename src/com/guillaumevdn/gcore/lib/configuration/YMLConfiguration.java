package com.guillaumevdn.gcore.lib.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.file.YMLFile;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode;
import com.guillaumevdn.gcore.lib.cost.Cost;
import com.guillaumevdn.gcore.lib.economy.Currency;
import com.guillaumevdn.gcore.lib.exception.ConfigError;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterItemStack;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.tuple.ItemChancePair;
import com.guillaumevdn.gcore.lib.validator.type.CollectionIntegerValidator;
import com.guillaumevdn.gcore.lib.validator.type.DoubleValidator;
import com.guillaumevdn.gcore.lib.validator.type.IntegerValidator;
import com.guillaumevdn.gcore.lib.validator.type.ValueValidator;

/**
 * @author GuillaumeVDN
 */
public class YMLConfiguration {

	private GPlugin plugin;
	private File file;
	private YMLFile yml = new YMLFile(this);
	private String logFilePath = null;

	public YMLConfiguration(GPlugin plugin, File file) {
		this.plugin = plugin;
		file.getParentFile().mkdirs();
		this.logFilePath = file.getPath().replace("plugins" + File.separator, "").replace(plugin.getDataFolder().getName() + File.separator, "").replace(File.separator, "/");
		this.file = file;
		load();
	}

	// get
	public GPlugin getPlugin() {
		return plugin;
	}

	public File getFile() {
		return file;
	}

	public String getLogFilePath() {
		return logFilePath;
	}

	public YMLFile getBackingYML() {
		return yml;
	}

	// load/save
	public void load() {
		try {
			yml.read();
		} catch (Throwable exception) {
			throw new Error("couldn't load config from file " + logFilePath, exception);
		}
	}

	public void save() {
		save(file);
	}

	public void save(File file) {
		try {
			yml.write(file);
		} catch (Throwable exception) {
			throw new Error("couldn't save config to file " + logFilePath, exception);
		}
	}

	public void print() {
		yml.print();
	}

	// config
	public boolean contains(String path) {
		validatePath(path);
		return yml.contains(path);
	}

	public boolean isConfigurationSection(String path) {
		return contains(path) && yml.isConfigurationSection(path);
	}

	// get section keys
	public List<String> readMandatoryKeysForSection(String path) {
		if (!isConfigurationSection(path)) {
			throwMissing(path, "configuration section");
		}
		return readKeysForSection(path);
	}

	private static final List<String> EMPTY_KEYS = CollectionUtils.asUnmodifiableList();

	public List<String> readKeysForSection(String path) {
		if (isConfigurationSection(path)) {
			return yml.getSectionNode(path).getConfigKeys();
		}
		return EMPTY_KEYS;
	}

	public List<Integer> readMandatoryNumberKeysForSection(String path, Integer continuityStart) {
		return readMandatoryNumberKeysForSection(path, continuityStart, null);
	}

	public List<Integer> readMandatoryNumberKeysForSection(String path, Integer continuityStart, IntegerValidator validator) {
		if (!isConfigurationSection(path)) {
			throwMissing(path, "configuration section");
		}
		return readNumberKeysForSection(path, continuityStart, validator);
	}

	public List<Integer> readNumberKeysForSection(String path, Integer continuityStart) {
		return readNumberKeysForSection(path, continuityStart, null);
	}

	public List<Integer> readNumberKeysForSection(String path, Integer continuityStart, IntegerValidator validator) {
		// get keys
		List<Integer> keys = new ArrayList<>();
		if (isConfigurationSection(path)) {
			for (String key : yml.getSectionNode(path).getConfigKeys()) {
				Integer keyNumber = NumberUtils.integerOrNull(key);
				if (keyNumber == null) {
					throwError("key " + key + " must be a number at path " + path + "." + key);
				}
				keys.add(keyNumber);
			}
		}
		// validate
		if (validator != null) {
			for (Integer key : keys) {
				if (!validator.isValid(key)) {
					throwError("Invalid key (" + validator.getMustBeDescription() + ") at path " + path + "." + key);
				}
			}
		}
		// continuity
		if (continuityStart != null) {
			// discontinuity found
			Iterator<Integer> iterator = keys.iterator();
			int expected = continuityStart - 1;
			while (iterator.hasNext()) {
				Integer found = iterator.next();
				if (found != ++expected) {
					throwError("missing key " + expected + " before " + found + " for section at path" + path);
				}
			}
		}
		// return
		return keys;
	}

	// get object
	public Object read(String path, Object def) {
		return contains(path) ? doRead(path) : def;
	}

	public Object readMandatory(String path) {
		if (!contains(path)) {
			throwMissing(path, "object");
		}
		return doRead(path);
	}

	private Object doRead(String path) {
		validatePath(path);
		Object value = yml.getConfigValue(path);
		return value;
	}

	// get object
	public DataIO readObject(String path, boolean snakeCaseToCamelCase) {
		return contains(path) /* this also validates the path */ ? getBackingYML().getSectionNode(path).toIO(snakeCaseToCamelCase) : null;
	}

	public DataIO readMandatoryObject(String path, boolean snakeCaseToCamelCase) {
		if (!contains(path)) { // this also validates the path
			throwMissing(path, "object IO");
		}
		return getBackingYML().getSectionNode(path).toIO(snakeCaseToCamelCase);
	}

	// get string
	public String readStringNonEmptyStringOrNull(String path) {
		String value = readString(path, null);
		return value != null && !value.trim().isEmpty() ? value : null;
	}

	public String readString(String path, String def) {
		return contains(path) ? doReadString(path) : def;
	}

	public String readMandatoryString(String path) {
		if (!contains(path)) {
			throwMissing(path, "text");
		}
		return doReadString(path);
	}

	protected String doReadString(String path) {
		validatePath(path);
		return StringUtils.format(yml.getConfigValueString(path));
	}

	// get boolean
	public boolean readBoolean(String path, boolean def) {
		return contains(path) ? doReadBoolean(path) : def;
	}

	public boolean readMandatoryBoolean(String path) {
		if (!contains(path)) {
			throwMissing(path, "boolean");
		}
		return doReadBoolean(path);
	}

	private boolean doReadBoolean(String path) {
		validatePath(path);
		return Boolean.parseBoolean(readString(path, null));
	}

	// get integer
	public int readInteger(String path, int def) {
		return readInteger(path, def, null);
	}

	public int readInteger(String path, int def, IntegerValidator validator) {
		return contains(path) ? doReadInteger(path, validator) : def;
	}

	public int readMandatoryInteger(String path) {
		return readMandatoryInteger(path, null);
	}

	public int readMandatoryInteger(String path, IntegerValidator validator) {
		if (!contains(path)) {
			throwMissing(path, "number");
		}
		return doReadInteger(path, validator);
	}

	private int doReadInteger(String path, IntegerValidator validator) {
		validatePath(path);
		Integer value = NumberUtils.integerOrNull(readString(path, null));
		if (value == null) {
			throwError("Invalid number at path " + path);
		}
		if (validator != null && !validator.isValid(value)) {
			throwError("Invalid number (" + validator.getMustBeDescription() + ") at path " + path);
		}
		return value;
	}

	// get long
	public long readLong(String path, long def) {
		return contains(path) ? doReadLong(path) : def;
	}

	public long readMandatoryLong(String path) {
		if (!contains(path)) {
			throwMissing(path, "number");
		}
		return doReadLong(path);
	}

	private long doReadLong(String path) {
		validatePath(path);
		Long value = NumberUtils.longOrNull(readString(path, null));
		if (value == null) {
			throwError("Invalid number at path " + path);
		}
		return value;
	}

	// get double
	public double readDouble(String path, double def) {
		return readDouble(path, def, null);
	}

	public double readDouble(String path, double def, DoubleValidator validator) {
		return contains(path) ? doReadDouble(path, validator) : def;
	}

	public double readMandatoryDouble(String path) {
		return readMandatoryDouble(path, null);
	}

	public double readMandatoryDouble(String path, DoubleValidator validator) {
		if (!contains(path)) {
			throwMissing(path, "decimal number");
		}
		return doReadDouble(path, validator);
	}

	private double doReadDouble(String path, DoubleValidator validator) {
		validatePath(path);
		Double value = NumberUtils.doubleOrNull(readString(path, null));
		if (value == null) {
			throwError("Invalid number at path " + path);
		}
		if (validator != null && !validator.isValid(value)) {
			throwError("Invalid decimal number (" + validator.getMustBeDescription() + ") at path " + path);
		}
		return value;
	}

	// value : get string list
	public List<String> readStringList(String path, List<String> def) {
		return contains(path) ? doReadStringList(path) : StringUtils.formatCopy(def);
	}

	public List<String> readMandatoryStringList(String path) {
		if (!contains(path)) {
			throwMissing(path, "text list");
		}
		return doReadStringList(path);
	}

	protected List<String> doReadStringList(String path) {
		validatePath(path);
		// get list
		Object object = yml.getConfigValue(path);
		List<String> value = new ArrayList<String>();
		if (object != null) {
			if (object instanceof List) {
				for (Object obj : (List) object) {
					value.add(String.valueOf(obj));
				}
			} else {
				value.add(String.valueOf(object));
			}
		}
		// format
		StringUtils.format(value);
		return value;
	}

	// value : get int list
	public List<Integer> readIntegerList(String path, List<Integer> def) {
		return readIntegerList(path, def, null);
	}

	public List<Integer> readIntegerList(String path, List<Integer> def, CollectionIntegerValidator validator) {
		return contains(path) ? doReadIntegerList(path, validator) : def;
	}

	public List<Integer> readMandatoryIntegerList(String path) {
		return readMandatoryIntegerList(path, null);
	}

	public List<Integer> readMandatoryIntegerList(String path, CollectionIntegerValidator validator) {
		if (!contains(path)) {
			throwMissing(path, "number list");
		}
		return doReadIntegerList(path, validator);
	}

	private List<Integer> doReadIntegerList(String path, CollectionIntegerValidator validator) {
		validatePath(path);
		// get list
		Object object = yml.getConfigValue(path);
		List<Integer> value = new ArrayList<Integer>();
		if (object != null) {
			if (object instanceof List) {
				for (Object obj : (List) object) {
					try {
						value.add(Integer.parseInt(String.valueOf(obj)));
					} catch (Throwable ignored) {
						throwError("element " + String.valueOf(obj) + " is invalid for number list at path " + path);
					}
				}
			} else {
				try {
					value.add(Integer.parseInt(String.valueOf(object)));
				} catch (Throwable ignored) {
					throwError("element " + String.valueOf(object) + " is invalid for number list at path " + path);
				}
			}
		}
		// validate
		if (validator != null && !validator.isValid(value)) {
			throwError("Invalid number list (" + validator.getMustBeDescription() + ") at path " + path);
		}
		return value;
	}

	// value : get value list
	public <T> List<T> readSerializedList(String path, List<T> def, Class<T> typeClass) {
		return contains(path) ? doReadSerializedList(path, typeClass) : def;
	}

	public <T> List<T> readMandatorySerializedList(String path, Class<T> typeClass) {
		if (!contains(path)) {
			throwMissing(path, StringUtils.getReadableName(typeClass).toLowerCase() + " list");
		}
		return doReadSerializedList(path, typeClass);
	}

	private <T> List<T> doReadSerializedList(String path, Class<T> typeClass) {
		validatePath(path);
		Serializer<T> serializer = Serializer.find(typeClass);
		// get list
		Object object = yml.getConfigValue(path);
		List<T> value = null;
		if (object != null) {
			value = new ArrayList<>();
			if (object instanceof List) {
				for (Object obj : (List) object) {
					try {
						T valueObj = serializer.deserialize(String.valueOf(obj));
						if (valueObj != null) {
							value.add(valueObj);
							continue;
						}
					} catch (Throwable ignored) {}
					throwError("element " + obj + " is invalid for " + serializer.getTypeName() + " at path " + path);
				}
			} else label:{
				try {
					T valueObject = serializer.deserialize(String.valueOf(object));
					if (valueObject != null) {
						value.add(valueObject);
						break label;
					}
				} catch (Throwable ignored) {}
				throwError("element " + object + " is invalid for " + serializer.getTypeName() + " at path " + path);
			}
		}
		return value;
	}

	// value : get enum list
	public <T extends Enum<T>> List<T> readEnumList(String path, List<T> def, Class<T> enumClass) {
		return contains(path) ? doReadEnumList(path, enumClass) : def;
	}

	public <T extends Enum<T>> List<T> readMandatoryEnumList(String path, Class<T> enumClass) {
		if (!contains(path)) {
			throwMissing(path, StringUtils.getReadableName(enumClass) + " list");
		}
		return doReadEnumList(path, enumClass);
	}

	private <T extends Enum<T>> List<T> doReadEnumList(String path, Class<T> enumClass) {
		validatePath(path);
		// get list
		Object object = yml.getConfigValue(path);
		List<T> value = new ArrayList<>();
		Serializer<T> serializer = Serializer.ofEnum(enumClass);
		if (object != null) {
			if (object instanceof List) {
				for (Object obj : (List) object) {
					try {
						T valueObj = serializer.deserialize(String.valueOf(obj));
						if (valueObj != null) {
							value.add(valueObj);
							continue;
						}
					} catch (Throwable ignored) {}
					throwError("element " + obj + " is invalid for " + serializer.getTypeName() + " at path " + path);
				}
			} else label:{
				try {
					T valueObject = serializer.deserialize(String.valueOf(object));
					if (valueObject != null) {
						value.add(valueObject);
						break label;
					}
				} catch (Throwable ignored) {}
				throwError("element " + object + " is invalid for " + serializer.getTypeName() + " at path " + path);
			}
		}
		return value;
	}

	// value : get item list
	public List<ItemStack> readItemList(String path, List<ItemStack> def) {
		return contains(path) ? doReadItemList(path) : def;
	}

	public List<ItemStack> readMandatoryItemStackList(String path) {
		if (!contains(path)) {
			throwMissing(path, "item list");
		}
		return doReadItemList(path);
	}

	private List<ItemStack> doReadItemList(String path) {
		validatePath(path);
		// get items
		List<ItemStack> value = new ArrayList<>();
		for (String key : readKeysForSection(path)) {
			try {
				ItemStack item = readItemStack(path + "." + key, null);
				if (item == null) {
					throwError("element " + key + " is invalid for item list at path " + path);
				}
				value.add(item);
			} catch (Throwable ignored) {
				throwError("element " + key + " is invalid for item list at path " + path);
			}
		}
		return value;
	}

	// value : get chance item list
	public List<ItemChancePair> readItemChancePairList(String path, List<ItemChancePair> def) {
		return contains(path) ? doReadItemChancePairList(path) : def;
	}

	public List<ItemChancePair> readMandatoryItemChancePairList(String path) {
		if (!contains(path)) {
			throwMissing(path, "chance item list");
		}
		return doReadItemChancePairList(path);
	}

	private List<ItemChancePair> doReadItemChancePairList(String path) {
		validatePath(path);
		// get items
		List<ItemChancePair> value = new ArrayList<>();
		for (String key : readKeysForSection(path)) {
			try {
				ItemChancePair item = doReadChanceItem(path + "." + key);
				if (item == null) {
					throwError("element " + key + " is invalid for item list at path " + path);
				}
				value.add(item);
			} catch (Throwable exception) {
				throwError("element " + key + " is invalid for item list at path " + path, exception);
			}
		}
		return value;
	}

	// get locations item list
	/*public List<ElementGUIItem> readGUIItemTripleList(String path, List<ElementGUIItem> def) {
		return contains(path) ? doReadGUIItemTripleList(path) : def;
	}

	public List<ElementGUIItem> readMandatoryGUIItemTripleList(String path) {
		if (!contains(path)) {
			throwMissing(path, "icon/locations list");
		}
		return doReadGUIItemTripleList(path);
	}

	private List<ElementGUIItem> doReadGUIItemTripleList(String path) {
		validatePath(path);
		// get items
		List<ElementGUIItem> value = new ArrayList<>();
		for (String key : readKeysForSection(path)) {
			try {
				GUIItemTriple item = readMandatoryGUIItemTriple(path + "." + key);
				if (item == null) {
					throwError("element " + key + " is invalid for icon/locations list at path " + path);
				}
				value.add(item);
			} catch (Throwable ignored) {
				throwError("element " + key + " is invalid for icon/locations list at path " + path);
			}
		}
		return value;
	}

	// get locations item
	public GUIItemTriple readLocationsItem(String path, GUIItemTriple def) throws Throwable {
		return contains(path) ? doReadGUIItemTriple(path, false) : def;
	}

	public GUIItemTriple readMandatoryGUIItemTriple(String path) throws Throwable {
		if (!contains(path)) {
			throwMissing(path, "icon/locations");
		}
		return doReadGUIItemTriple(path, true);
	}

	private GUIItemTriple doReadGUIItemTriple(String path, boolean mandatory) throws Throwable {
		validatePath(path);
		// get item
		ItemStack item = mandatory ? readMandatoryItemStack(path + ".icon") : doReadItemStack(path + ".icon");
		boolean persistent = readBoolean(path + ".persistent", false);
		List<String> rawLocations = mandatory ? readMandatoryStringList(path + ".locations") : doReadStringList(path + ".locations");
		List<IntegerPair> locations = new ArrayList<>();
		for (String location : rawLocations) {
			try {
				String[] split = location.split(",");
				if (split.length == 1) {
					locations.add(IntegerPair.of(-1, Integer.parseInt(split[0])));
				} else if (split.length == 2) {
					locations.add(IntegerPair.of(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
				}
			} catch (Throwable error) {
				throwError("couldn't read location " + location + " at path " + path + ".locations");
			}
		}
		GUIItemTriple value = new GUIItemTriple(item, persistent, locations);
		return value;
	}*/

	// get chance item
	public ItemChancePair readChanceItem(String path, ItemChancePair def) throws Throwable {
		return contains(path) ? doReadChanceItem(path) : def;
	}

	public ItemChancePair readMandatoryChanceItem(String path) throws Throwable {
		if (!contains(path)) {
			throwMissing(path, "chance item");
		}
		return doReadChanceItem(path);
	}

	private ItemChancePair doReadChanceItem(String path) throws Throwable {
		if (!contains(path)) {
			return null;
		}
		if (!isConfigurationSection(path)) {
			throwMissing(path, "chance item");
		}
		// read
		ItemStack item = readItemStack(path, null);
		int chance = readMandatoryInteger(path + ".chance");
		return new ItemChancePair(item, chance);
	}

	// get item
	public ItemStack readItemStack(String path, ItemStack def) throws Throwable {
		return contains(path) ? doReadItemStack(path) : def;
	}

	public ItemStack readMandatoryItemStack(String path) throws Throwable {
		if (!contains(path)) {
			throwMissing(path, "item");
		}
		return doReadItemStack(path);
	}

	private ItemStack doReadItemStack(String path) throws Throwable {
		if (!contains(path)) {
			return null;
		}
		if (!isConfigurationSection(path)) {
			throwError("missing item config section at path " + path);
		}
		// read config in DataIO and use the adapter so it's consistent for both config and json
		DataIO data = readObject(path, true); // true to convert snake_case to camelCase because we're using that for json
		return AdapterItemStack.INSTANCE.readCurrent(data);
	}

	// get cost
	public Cost readCost(String path, Cost def) throws Throwable {
		return contains(path) ? doReadCost(path) : def;
	}

	public Cost readMandatoryCost(String path) throws Throwable {
		if (!contains(path)) {
			throwMissing(path, "cost");
		}
		Cost cost = doReadCost(path);
		// empty
		if (cost.isEmpty()) {
			throwError("cost at path " + path + " is empty");
		}
		return cost;
	}

	private Cost doReadCost(String path) throws Throwable {
		if (!contains(path)) {
			return null;
		}
		if (!contains(path)) {
			throwMissing(path, "cost");
		}
		// read
		Cost value = new Cost();
		if (isConfigurationSection(path)) {
			// single item
			if (contains(path + ".item")) {
				ItemStack item = readMandatoryItemStack(path + ".item");
				String displayName = readString(path + ".item.display_name", null);
				value.add(item, displayName);
			}
			// multiple items
			else {
				if (contains(path + ".items")) {
					for (String itemId : readKeysForSection(path + ".items")) {
						ItemStack item = readMandatoryItemStack(path + ".items." + itemId);
						String displayName = readString(path + ".items." + itemId + ".display_name", null);
						value.add(item, displayName);
					}
				}
			}
			// currencies
			if (contains(path + ".currencies")) {
				for (String currencyId : readKeysForSection(path + ".currencies")) {
					Currency currency = Currency.safeValueOf(currencyId);
					if (currency == null) throwError("invalid currency " + currencyId + " for cost config at path " + path);
					double amount = readMandatoryDouble(path + ".currencies." + currencyId);
					value.add(currency, amount);
				}
			}
		}
		// string, currency
		else {
			String[] raw = readString(path, null).split(" ");
			Currency currency = null;
			Double amount = null;
			// specified currency
			if (raw.length > 1) {
				currency = Currency.safeValueOf(raw[0].trim());
				if (currency == null) throwError("invalid currency " + raw[0].trim() + " for cost config at path " + path);
				amount = NumberUtils.doubleOrNull(raw[1]);
				if (amount == null) throwError("invalid amount " + raw[1].trim() + " for cost config at path " + path);
			}
			// no currency specified, use Vault
			else {
				currency = Currency.VAULT;
				amount = NumberUtils.doubleOrNull(raw[0].trim());
				if (amount == null) throwError("invalid amount " + raw[0].trim() + " for cost config at path " + path);
			}
			// load
			value.add(currency, amount);
		}
		// return
		return value;
	}

	// get value
	public <T> T readValue(String path, T def, Serializer<T> serializer) {
		return readValue(path, def, serializer, null);
	}

	public <T> T readValue(String path, T def, Serializer<T> serializer, ValueValidator<T> validator) {
		return contains(path) ? doReadValue(path, serializer, validator) : def;
	}

	public <T> T readMandatoryValue(String path, Serializer<T> serializer) {
		return readMandatoryValue(path, serializer, null);
	}

	public <T> T readMandatoryValue(String path, Serializer<T> serializer, ValueValidator<T> validator) {
		if (!contains(path)) {
			throwMissing(path, serializer.getTypeName());
		}
		return doReadValue(path, serializer, validator);
	}

	private <T> T doReadValue(String path, Serializer<T> serializer, ValueValidator<T> validator) {
		validatePath(path);
		String raw = readString(path, null);
		T value = serializer.deserialize(raw);
		if (value == null) {
			throwError("Invalid " + serializer.getTypeName() + " at path " + path);
		}
		if (validator != null && !validator.isValid(value)) {
			throwError("Invalid " + serializer.getTypeName() + " (" + validator.getMustBeDescription() + ") at path " + path);
		}
		return value;
	}

	// get enum
	public <T extends Enum<T>> T readEnum(String path, T def, Class<T> enumClass) {
		return readValue(path, def, Serializer.ofEnum(enumClass));
	}

	public <T extends Enum<T>> T readMandatoryEnum(String path, Class<T> enumClass) {
		return readMandatoryValue(path, Serializer.ofEnum(enumClass));
	}

	// get location
	public Location readLocation(String path, Location def) {
		return readValue(path, def, Serializer.LOCATION);
	}

	public Location readMandatoryLocation(String path) {
		return readMandatoryValue(path, Serializer.LOCATION);
	}

	// set
	public void copyFrom(YMLConfiguration src, String srcPath, String targetPath) {
		write(targetPath, null);
		if (src.contains(srcPath)) {
			// proceed section
			if (src.isConfigurationSection(srcPath)) {
				for (String key : src.readKeysForSection(srcPath)) {
					copyFrom(src, srcPath + "." + key, targetPath + "." + key);
				}
			}
			// proceed value
			else {
				write(targetPath, src.read(srcPath, null));
			}
		}
	}

	public void copy(String path, String targetPath) {
		if (contains(path)) {
			validatePath(targetPath);
			copyRec(path, targetPath, false);
		}
	}

	public void move(String path, String targetPath) {
		if (contains(path)) {
			validatePath(targetPath);
			copyRec(path, targetPath, true);
		}
	}

	private void copyRec(String path, String target, boolean removeSrc) {
		// proceed section
		if (isConfigurationSection(path)) {
			for (String key : readKeysForSection(path)) {
				copyRec(path + "." + key, target + "." + key, false);
			}
			if (removeSrc) write(path, null);
		}
		// proceed value
		else {
			write(target, read(path, null));
			if (removeSrc) write(path, null);
		}
	}

	public void write(String path, Object value) {
		if (path.startsWith(".")) {
			path = path.substring(1);  // just ignore blank, so we don't have to always worry about that (in migrations, for instance)
		}
		validatePath(path);
		// remove
		if (value == null) {
			if (path.isEmpty()) {
				for (String key : readKeysForSection("")) {
					yml.set(key, null);
				}
			} else {
				yml.set(path, null);
			}
		}
		// set
		else {
			if (value instanceof ItemStack) {
				try {
					DataIO itemWriter = new DataIO();
					AdapterItemStack.INSTANCE.write((ItemStack) value, itemWriter);
					if (itemWriter.isEmpty()) {
						yml.set(path, null);
					} else {
						SectionNode parent = path.contains(".") ? yml.mkdirs(path.substring(0, path.lastIndexOf('.'))) : yml.getBase();
						String id = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
						parent.setConfigNode(itemWriter.toYML(parent, id, true));
					}
				} catch (Throwable exception) {
					exception.printStackTrace();
				}
			} else if (value instanceof Collection) {
				Collection coll = (Collection) value;
				yml.set(path, coll.isEmpty() ? new ArrayList<String>() : Serializer.find(coll.iterator().next().getClass()).serialize(coll));
			} else {
				Serializer serializer = Serializer.find(value.getClass());
				yml.set(path, serializer.serialize(value));
			}
		}
	}

	// utils
	public void validatePath(String path) {
		if (StringUtils.hasChar(path, ' ') || StringUtils.hasChar(path, '\t') || path.trim().length() != path.length()) {
			throwError("path '" + path + "' contains whitespaces");
		}
	}

	// logs
	public String buildMistakeErrorHeader() {
		return "Configuration mistake in file " + logFilePath + " : ";
	}

	public String buildFormatErrorHeader() {
		return "Formatting error in file " + logFilePath + " : ";
	}

	private void throwMissing(String path, String type) {
		throw new ConfigError(buildMistakeErrorHeader() + "missing " + type + " at path " + path);
	}

	private void throwError(String message) {
		throw new ConfigError(buildMistakeErrorHeader() + message);
	}

	private void throwError(String message, Throwable cause) {
		throw new Error("Error when reading file " + logFilePath + " : " + message, cause);
	}

}
