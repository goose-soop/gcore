package be.pyrrh4.pyrcore.lib.loadable;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.lib.configuration.YMLConfiguration;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.placeholder.PlaceholderParser;
import be.pyrrh4.pyrcore.lib.util.Utils;

public abstract class AbstractListSetting<T> extends AbstractSetting<List<String>> {

	// base
	private boolean compact;
	private String loadConfigPath;
	private LoadResult<?> result;
	private List<Integer> shouldParseIndexes = new ArrayList<Integer>();

	public AbstractListSetting(String id, List<String> def, boolean mandatory, String typeName, List<String> description) {
		this(id, def, mandatory, false, typeName, description);
	}

	public AbstractListSetting(String id, List<String> def, boolean mandatory, boolean compact, String typeName, List<String> description) {
		super(id, def, mandatory, typeName, description);
		this.compact = compact;
	}

	// methods
	@Override
	public void setValue(List<String> value) {
		super.setValue(value);
		shouldParseIndexes.clear();
		if (value != null) {
			for (int lineIndex = 0; lineIndex < value.size(); ++lineIndex) {
				String line = value.get(lineIndex);
				for (char valueChar : line.toCharArray()) {
					boolean b = false;
					for (char indicator : parseIndicators) {
						if (valueChar == indicator) {
							shouldParseIndexes.add(lineIndex);
							b = true;
							break;
						}
					}
					if (b) break;
				}
			}
		}
	}

	@Override
	public void load(YMLConfiguration config, String configPath, LoadResult<?> result) {
		this.loadConfigPath = configPath;
		this.result = result;
		if (config.contains(configPath)) {
			setValue(config.getListFormatted(configPath, null, compact));
		} else if (isMandatory()) {
			result.setError("missing setting of type '" + getTypeName() + "' at '" + configPath + "'");
		}
	}

	@Override
	public void save(YMLConfiguration config, String configPath) {
		// get value
		List<String> value = null;
		if (getValue() == null) {
			if (isMandatory() && getDef() != null && !getDef().isEmpty()) {
				value = getDef();
			}
		} else {
			if (!getValue().equals(getDef())) {
				value = getValue();
			}
		}
		// set
		if (value == null) {
			config.set(configPath, null);
		} else {
			config.set(configPath, compact ? Utils.asNiceString(value, false) : value);
		}
	}

	/**
	 * @player the player. It can be null but variables won't be parsed, so the method might return null for some settings (for example number settings containing variables).
	 * @return the parsed value, or null if a problem was encountered
	 */
	private transient ValueCache<T> cache = null;
	public T getParsed(Player player) {
		// return cache if has, and player is null or shouldn't parse anything
		if (player == null || shouldParseIndexes.isEmpty() ? cache != null : false) {
			return cache.getValue();
		}
		// parse
		try {
			// no value
			List<String> raw = getValue() != null ? getValue() : getDef();
			if (raw == null) return null;
			// parse placeholders for lines needed
			List<String> parsedRaw = Utils.emptyList();
			for (int rawIndex = 0; rawIndex < raw.size(); ++rawIndex) {
				String rawLine = raw.get(rawIndex);
				parsedRaw.add(shouldParseIndexes.contains(rawIndex) ? PlaceholderParser.parseAll(player, rawLine) : rawLine);
			}
			// parse value
			T parsedValue = parse(parsedRaw);
			if (parsedValue != null) {// success
				if (player == null || shouldParseIndexes.isEmpty()) cache = new ValueCache<T>(parsedValue);// save cache if player is null or shouldn't parse
				return parsedValue;// return parsed value
			}
		} catch (Throwable ignored) {}
		// couldn't parse
		result.setError("invalid setting of type '" + getTypeName() + "' at '" + loadConfigPath + "'", true, true);
		return null;
	}

	public static List<String> getParsed(List<String> list, Player player) {
		if (list == null || list.isEmpty()) return list;
		List<String> parsed = Utils.emptyList();
		for (String r : list) {
			parsed.add(player != null ? PlaceholderParser.parseAll(player, r) : r);
		}
		return parsed;
	}

	@Override
	public List<String> fillEditorItemLore() {
		return fillEditorItemLore(getDescription());
	}

	@Override
	public List<String> fillEditorItemLore(List<String> description) {
		List<String> value = getValue() != null && !getValue().isEmpty() ? getValue() : getDef();
		if (value == null || value.isEmpty()) {
			value = Utils.asList("§7- §e/" + (getDef() == null || getDef().isEmpty() ? " §7§l(default)" : ""));
		} else {
			value = Utils.addBeforeAll(value, "§7- §e");
			if (value.equals(getDef())) {
				value.add("§7§l(default)");
			}
		}
		return EditorGUI.fillItemLore(description, getTypeName(), value, isMandatory());
	}

	// abstract methods
	protected abstract T parse(List<String> raw) throws Throwable;

}
