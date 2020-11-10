package com.guillaumevdn.gcore.lib.element.struct.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsedCache;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.exception.ConfigError;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public abstract class BasicElement<T> extends Element implements ParseableElement<T> {

	private final SizeTolerance sizeTolerance;
	private final List<String> defaultValue;
	private List<String> value = null;
	private boolean isParseable = false;
	private final List<BiConsumer<T, T>> watchers = new ArrayList<>();

	public BasicElement(String typeName, SizeTolerance sizeTolerance, Element parent, String id, NeedType need, List<String> def, Text editorDescription) {
		super(typeName, parent, id, need, editorDescription);
		this.sizeTolerance = sizeTolerance;
		this.defaultValue = def == null ? null : Collections.unmodifiableList(def);
		setValue(null);
	}

	// get
	public SizeTolerance getSizeTolerance() {
		return sizeTolerance;
	}

	public final List<String> getDefaultValue() {
		return defaultValue;
	}

	public final String getDefaultValueLine(int index) {
		return defaultValue == null || index >= defaultValue.size() ? null : defaultValue.get(index);
	}

	public final List<String> getDefaultValueCopy() {
		return defaultValue == null ? null : CollectionUtils.asList(defaultValue);
	}

	public final List<String> getValue() {
		return value;
	}

	public final String getValueLine(int index) {
		return value == null || index >= value.size() ? null : value.get(index);
	}

	public final List<String> getValueCopy() {  // this is actually used in one case, so not useless :erycJebaited:
		return value == null ? null : CollectionUtils.asList(value);
	}

	public final List<String> getValueCopyOrNewList() {
		return value == null ? new ArrayList<>() : CollectionUtils.asList(value);
	}

	public final List<String> getValueOrDefault() {
		return getValueOrDefaultOr(null);
	}

	public final List<String> getValueOrDefaultOr(List<String> def) {
		return value != null ? value : (defaultValue != null ? defaultValue : def);
	}

	public final String getValueLineOrDefault(int index) {
		return value != null && index < value.size() ? value.get(index) : (defaultValue != null && index < defaultValue.size() ? defaultValue.get(index) : null);
	}

	public final void ifValueOrDefaultIsPresent(Consumer<List<String>> ifPresent) {
		List<String> value = getValueOrDefault();
		if (value != null) {
			ifPresent.accept(value);
		}
	}

	@Override
	public final boolean hasParseableLocations() {
		return isParseable;
	}

	@Override
	public final boolean isCurrentlyDefault() {
		return (value == null && defaultValue != null) || Objects.deepEquals(value, defaultValue);
	}

	// set
	public final void setValue(List<String> newValue) {
		// same value
		if (newValue == null ? this.value == null : this.value != null && CollectionUtils.contentEquals(newValue, this.value)) {
			return;
		}
		// attempt to parse previous and new value for watchers
		T previous = null, next = null;
		try { previous = this.value.size() == 1 ? doParseString(this.value.get(0)) : doParseList(this.value); } catch (Throwable ignored) {}
		try { next = newValue.size() == 1 ? doParseString(newValue.get(0)) : doParseList(newValue); } catch (Throwable ignored) {}
		// set value
		if (newValue == null) {
			this.value = null;
			this.isParseable = false;
		} else {
			this.value = Collections.unmodifiableList(StringUtils.formatCopy(newValue)); // reformat since color parsing is made on read
			this.isParseable = StringUtils.hasPlaceholders(newValue);
		}
		// reset cache
		resetCache();
		if (getParent() != null) {
			getParent().resetCache();
		}
		// call watchers
		for (BiConsumer<T, T> watcher : watchers) {
			watcher.accept(previous, next);
		}
	}

	public final void addWatcher(BiConsumer<T, T> watcher) {
		watchers.add(watcher);
	}

	// loading and saving
	@Override
	protected void clearBeforeRead() {
		setValue(null);
	}

	@Override
	protected void doRead() throws Throwable {
		YMLConfiguration config = getSuperElement().getConfiguration();
		String path = getConfigurationPath();
		try {
			List<String> value = config.readStringList(path, null);
			if (value != null) {
				if (value.size() == 0 && !sizeTolerance.allowEmpty()) {
					getSuperElement().addLoadError(getTypeName() + " at path " + path + " shouldn't be empty");
					value = null;
				} else if (value.size() > 1 && !sizeTolerance.allowList()) {
					getSuperElement().addLoadError(getTypeName() + " at path " + path + " shouldn't be a list");
					value = null;
				}
			}
			setValue(value);
		} catch (Throwable exception) {
			ConfigError configError = ObjectUtils.findCauseOrNull(exception, ConfigError.class);
			if (configError != null) {
				getSuperElement().addLoadError(StringUtils.capitalize(configError.getMessage().replace(config.buildMistakeErrorHeader(), "").replace(config.buildFormatErrorHeader(), "")));
			} else {
				getSuperElement().getPlugin().getMainLogger().error("Couldn't read element " + getClass().getSimpleName() + " at path " + path + " in file " + config.getLogFilePath(), exception);
			}
		}
	}

	@Override
	protected void doWrite() throws Throwable {
		YMLConfiguration config = getSuperElement().getConfiguration();
		String path = getConfigurationPath();
		if (value == null || (defaultValue != null && CollectionUtils.contentEquals(value, defaultValue))) {
			config.write(path, null);
		} else {
			config.write(path, value.size() == 1 ? value.get(0) : value);
		}
	}

	// parsing
	private ParsedCache<T> cache = new ParsedCache<>();

	@Override
	public ParsedCache<T> getCache() {
		return hasParseableLocations() ? null : cache;
	}

	@Override
	public void resetCache() {
		cache.clear();
	}

	@Override
	public final T doParse(Replacer replacer) throws ParsingError {
		// no value
		List<String> raw = value != null ? value : defaultValue;
		if (raw == null) {
			return null;
		}
		// parse
		List<String> parsed = replacer.parse(raw);
		T result;
		if (parsed.size() > 1) {
			result = doParseList(parsed);
		} else if (parsed.size() == 1) {
			result = doParseString(parsed.get(0));
		} else {
			result = doParseEmpty();
		}
		// return
		return result;
	}

	protected T doParseEmpty() throws ParsingError {
		throw new ParsingError(this, "can't parse an empty value for element of type " + getTypeName());
	}

	protected T doParseList(List<String> raw) throws ParsingError {
		throw new ParsingError(this, "can't parse a list for element of type " + getTypeName());
	}

	protected T doParseString(String raw) throws ParsingError {
		throw new ParsingError(this, "can't parse a string value for element of type " + getTypeName());
	}

	// editor
	@Override
	public List<String> editorCurrentValue() {
		return value != null ? value : defaultValue;
	}

	@Override
	public List<String> editorIconLore() {
		List<String> lore = super.editorIconLore();
		lore.add("§r");
		lore.addAll(TextEditorGeneric.controlEdit.parseLines());
		lore.addAll(TextEditorGeneric.controlClear.parseLines());
		return lore;
	}

	@Override
	public void onEditorClick(ClickCall call) {
		// control + drop : clear
		if (call.getType().equals(ClickType.CONTROL_DROP)) {
			setValue(null);
			getSuperElement().onEditorChange(this);
			call.getGUI().setRegularItem(buildEditorItem(call.getSlot()));
		}
		// other
		else {
			super.onEditorClick(call);
		}
	}

}
