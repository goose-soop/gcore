package be.pyrrh4.pyrcore.lib.parseable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.data.CompactDataLink;
import be.pyrrh4.pyrcore.lib.parseable.data.DataLink;
import be.pyrrh4.pyrcore.lib.parseable.data.RegularDataLink;
import be.pyrrh4.pyrcore.lib.parseable.placeholder.PlaceholderParser;
import be.pyrrh4.pyrcore.lib.util.Utils;

public abstract class PrimitiveParseable<T> extends Parseable {

	// compact list separator
	public static final String PRIMITIVE_COMPACT_LIST_SEPARATOR = ",,";

	// base
	private String typeName;
	private List<String> defaultValue;
	private List<String> value = null;
	private List<Integer> parseableIndexes = new ArrayList<Integer>();

	public PrimitiveParseable(String id, Parseable parent, List<String> defaultValue, String typeName, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, mandatory, editorSlot, editorIcon, editorDescription);
		this.typeName = typeName;
		this.defaultValue = defaultValue;
	}

	public String getTypeName() {
		return typeName;
	}

	public List<String> getDefaultValue() {
		return defaultValue;
	}

	public List<String> getValue() {
		return value;
	}

	private static final List<Character> parseIndicators = Utils.asList('{', '%');
	public void setValue(List<String> value) {
		if (this.value != null) parseableIndexes.clear();
		this.value = value;
		if (value != null) {
			for (int lineIndex = 0; lineIndex < value.size(); ++lineIndex) {
				String line = value.get(lineIndex);
				searchChar:
					for (char valueChar : line.toCharArray()) {
						for (char indicator : parseIndicators) {
							if (valueChar == indicator) {
								parseableIndexes.add(lineIndex);
								break searchChar;
							}
						}
					}
			}
		}
	}

	// cache
	private class Cache {
		private T value;
	}

	// parse
	/**
	 * @param player the parsing player (can be null but variables won't be parsed, so the result could be null, for example number primitives containing variables)
	 * @return the parsed value, or null if a problem was encountered
	 */
	private Cache cache = null;
	public T getParsedValue(Player parser) {
		// return cache if has, and parser is null or shouldn't parse anything
		if (parser == null || parseableIndexes.isEmpty() ? cache != null : false) {
			return cache.value;
		}
		// no value
		List<String> raw = value != null ? value : defaultValue;
		if (raw == null) return null;
		// parse placeholders for lines needed
		List<String> parsedRaw = Utils.emptyList();
		for (int rawIndex = 0; rawIndex < raw.size(); ++rawIndex) {
			String rawLine = raw.get(rawIndex);
			parsedRaw.add(parseableIndexes.contains(rawIndex) && parser != null ? getParsed(rawLine, parser) : rawLine);
		}
		// parse
		try {
			// parse value
			T parsedValue = parseValue(parsedRaw, parser);
			if (parsedValue != null) {// success
				if (parser == null || parseableIndexes.isEmpty()) {// save cache if player is null or shouldn't parse
					cache = new Cache();
					cache.value = parsedValue;
				}
				return parsedValue;// return parsed value
			}
		}
		// couldn't parse
		catch (Throwable exception) {
			getLastData().log("invalid primitive setting of type " + typeName + " (" + exception.getMessage() + ")");
			return null;
		}
		// unknown
		getLastData().log("invalid primitive setting of type " + typeName);
		return null;
	}

	public static List<String> getParsed(List<String> list, Player parser) {
		if (list == null || list.isEmpty() || parser == null) return list;
		List<String> parsed = Utils.emptyList();
		for (String line : list) {
			parsed.add(getParsed(line, parser));
		}
		return parsed;
	}

	public static String getParsed(String string, Player parser) {
		return PlaceholderParser.parseAll(parser, string);
	}

	public abstract T parseValue(List<String> value, Player parser) throws Throwable;

	// load and save
	@Override
	public void load(DataLink data) {
		// set last data
		setLastData(data);
		// compact data
		if (data instanceof CompactDataLink) {
			// config
			CompactDataLink compactData = (CompactDataLink) data;
			CompactDataLink parentData = getParent() != null && Utils.instanceOf(getParent().getLastData(), CompactDataLink.class) ? (CompactDataLink) getParent().getLastData() : null;
			if (parentData == null || parentData.getParameters() == null || !parentData.getParameters().containsKey(getId())) {
				value = null;
				if (isMandatory()) {
					data.log("missing mandatory primitive setting of type " + typeName);
				}
				return;
			}
			compactData.setContains(true);
			// reset
			if (value != null) {
				value.clear();
			} else {
				value = Utils.emptyList();
			}
			// set
			value.addAll(Utils.split(PRIMITIVE_COMPACT_LIST_SEPARATOR, Utils.format(String.valueOf(parentData.getParameters().get(getId()))), true));
		}
		// regular data
		else if (data instanceof RegularDataLink) {
			// config
			RegularDataLink regularData = (RegularDataLink) data;
			if (regularData.getConfig() == null || !regularData.getConfig().contains(regularData.getPath())) {
				value = null;
				if (isMandatory()) {
					data.log("missing mandatory primitive setting of type " + typeName);
				}
				return;
			}
			regularData.setContains(true);
			// reset
			if (value != null) {
				value.clear();
			} else {
				value = Utils.emptyList();
			}
			// set
			Object load = regularData.getConfig().getObject(regularData.getPath(), null);
			if (load instanceof Collection<?>) {
				for (Object object : (Collection<?>) load) {
					value.add(Utils.format(String.valueOf(object)));
				}
			} else {
				value.addAll(Utils.split(PRIMITIVE_COMPACT_LIST_SEPARATOR, Utils.format(String.valueOf(load)), true));
			}
		}
	}

	@Override
	public void save(DataLink data) {
		// set last data
		setLastData(data);
		data.setContains(true);
		// set config value
		if (data instanceof RegularDataLink) {
			RegularDataLink regularData = (RegularDataLink) data;
			regularData.getConfig().set(regularData.getPath(), value == null || value.isEmpty() || (!isMandatory() && Utils.equals(value, defaultValue)) ? null : (value.size() == 1 ? value.get(0) : value));
			regularData.setContains(regularData.getConfig().contains(regularData.getPath()));
		}
	}

	// editor
	@Override
	public List<String> describe(int depth) {
		String spaces = Utils.copyString(" ", depth + 1);
		List<String> val = value != null ? value : (defaultValue != null ? defaultValue : null);
		List<String> desc = Utils.emptyList();
		String first = spaces + "§6> " + getId() + " :";
		if (val == null) {
			desc.add(first += " §8(no value)" + (defaultValue == null ? " §7§l(default)" : ""));
		} else if (val.isEmpty()) {
			desc.add(first += " §8(empty value)" + (defaultValue != null && defaultValue.isEmpty() ? " §7§l(default)" : ""));
		} else if (val.size() == 1) {
			desc.add(first += " §e" + val.get(0) + (defaultValue != null && defaultValue.isEmpty() ? " §7§l(default)" : ""));
		} else {
			desc.add(first += (val.equals(defaultValue) ? " §7§l(default)" : ""));
			for (String v : val) {
				desc.add(spaces + "  §6- §e" + v);
			}
		}
		return desc;
	}

	// clone
	protected PrimitiveParseable() {
		super();
	}

	@Override
	public PrimitiveParseable<T> clone() {
		// clone
		PrimitiveParseable<T> clone = (PrimitiveParseable<T>) super.clone();
		// clone properties
		clone.typeName = typeName;
		clone.defaultValue = defaultValue != null ? Utils.asList(defaultValue) : null;
		clone.value = value != null ? Utils.asList(value) : null;
		clone.parseableIndexes = Utils.asList(parseableIndexes);
		// success
		return clone;
	}

}
