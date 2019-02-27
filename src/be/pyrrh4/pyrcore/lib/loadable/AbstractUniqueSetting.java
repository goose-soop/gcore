package be.pyrrh4.pyrcore.lib.loadable;

import java.util.List;

import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.lib.configuration.YMLConfiguration;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.placeholder.PlaceholderParser;
import be.pyrrh4.pyrcore.lib.util.Utils;

public abstract class AbstractUniqueSetting<T> extends AbstractSetting<String> {

	// base
	private String loadConfigPath;
	private LoadResult<?> result;
	private boolean shouldParse = false;

	public AbstractUniqueSetting(String id, String def, boolean mandatory, String typeName, List<String> description) {
		super(id, def, mandatory, typeName, description);
	}

	// methods
	@Override
	public void setValue(String value) {
		super.setValue(value);
		shouldParse = false;
		if (value != null) {
			for (char valueChar : value.toCharArray()) {
				for (char indicator : parseIndicators) {
					if (valueChar == indicator) {
						shouldParse = true;
						return;
					}
				}
			}
		}
	}

	@Override
	public void load(YMLConfiguration config, String configPath, LoadResult<?> result) {
		this.loadConfigPath = configPath;
		this.result = result;
		if (config.contains(configPath)) {
			setValue(config.getStringFormatted(configPath, null));
		} else if (isMandatory()) {
			result.setError("missing setting of type '" + getTypeName() + "' at '" + configPath + "'");
		}
	}

	@Override
	public void save(YMLConfiguration config, String configPath) {
		if (getValue() == null) {
			if (isMandatory() && getDef() != null && !getDef().isEmpty() && !getDef().equals("0")) {
				config.set(configPath, getDef());
			}
		} else {
			if (!getValue().equals(getDef())) {
				config.set(configPath, getValue());
			}
		}
	}

	/**
	 * @player the player. It can be null, but always check if the value should be parsed. If it should be parsed but player is null, then this method might return null for some settings (for example number settings containing variables).
	 * @return the parsed value, or null if the config doesn't contain the value or if a problem was encountered
	 */
	private transient ValueCache<T> cache = null;
	public T getParsed(Player player) {
		// return cache if has, and player is null or shouldn't parse
		if (player == null || !shouldParse ? cache != null : false) {
			return cache.getValue();
		}
		// parse
		try {
			// no value
			String raw = getValue() != null ? getValue() : getDef();
			if (raw == null) return null;
			// parse placeholders
			String parsedRaw = player != null ? PlaceholderParser.parseAll(player, raw) : raw;
			// parse value
			T parsedValue = parse(parsedRaw);
			if (parsedValue != null) {// success
				if (player == null || !shouldParse) cache = new ValueCache<T>(parsedValue);// save cache if player is null or shouldn't parse
				return parsedValue;// return parsed value
			}
		} catch (Throwable ignored) {}
		// couldn't parse value
		result.setError("invalid setting of type '" + getTypeName() + "' at '" + loadConfigPath + "'", true, true);
		return null;
	}

	@Override
	public List<String> fillEditorItemLore() {
		return fillEditorItemLore(getDescription());
	}

	@Override
	public List<String> fillEditorItemLore(List<String> description) {
		String val = getValue() != null && !getValue().isEmpty() ? getValue() : getDef();
		if (val == null) val = "§e/";
		else if (val.equals(getDef())) val = "§e" + val + " §7§l(default)";
		return EditorGUI.fillItemLore(description, getTypeName(), Utils.asList(val), isMandatory());
	}

	// abstract methods
	protected abstract T parse(String raw) throws Throwable;

}
