package com.guillaumevdn.gcore.lib.element.struct.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

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
import com.guillaumevdn.gcore.lib.object.Optional;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public abstract class BasicElement<T> extends Element implements ParseableElement<T> {

	private final SizeTolerance sizeTolerance;
	private final List<String> defaultValue;
	private List<BiConsumer<T, T>> watchers = null;  // lazy ; almost no elements need watchers

	private boolean hasParseableLocations = false;
	private List<String> rawValue = null;  // IF NO PLACEHOLDERS : this is lazy ; it'll be loaded on-demand (-> when using the editor, or in some particular cases) ; while the parse result (cache) will always be present

	public BasicElement(SizeTolerance sizeTolerance, Element parent, String id, NeedType need, List<String> def, Text editorDescription) {
		super(parent, id, need, editorDescription);
		this.sizeTolerance = sizeTolerance;
		this.defaultValue = def == null ? null : Collections.unmodifiableList(def);
		this.hasParseableLocations = defaultValue != null && StringUtils.hasPlaceholders(defaultValue);
		setValue(null);
	}

	// ----- cache and raw value management

	private ParsedCache<T> parsedCache = new ParsedCache<T>() {
		@Override
		protected void beforeSet(Optional<T> value) {
			if (value != null && value.isPresent()) {  // we successfully parsed it ; so we no longer need the raw value
				rawValue = null;
			} else {  // we're setting a null value, maybe we failed to parse it ; re-load it from cached value
				loadRawValue();
			}
		}
	};

	@Override
	public ParsedCache<T> getCache() {
		return hasParseableLocations() ? null : parsedCache;
	}

	@Override
	public void resetCache() {
		parsedCache.clear();
	}

	protected abstract List<String> loadRawValueFrom(@Nonnull T value);

	private void loadRawValue() {
		if (rawValue == null) {
			T value = !parsedCache.isPresent() ? null : parsedCache.get().orNull();
			if (value != null) {
				rawValue = Collections.unmodifiableList(loadRawValueFrom(value));
			}
		}
	}

	// ----- get
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

	public final List<String> getRawValue() {
		loadRawValue();
		return rawValue;
	}

	public final int getRawValueSize() {
		loadRawValue();
		return rawValue == null ? 0 : rawValue.size();
	}

	public final String getRawValueLine(int index) {
		loadRawValue();
		return rawValue == null || index >= rawValue.size() ? null : rawValue.get(index);
	}

	public final List<String> getRawValueCopy() {
		loadRawValue();
		return rawValue == null ? null : CollectionUtils.asList(rawValue);
	}

	public final List<String> getRawValueCopyOrNewList() {
		loadRawValue();
		return rawValue == null ? new ArrayList<>(0) : CollectionUtils.asList(rawValue);
	}

	public final List<String> getRawValueOrDefault() {
		return getRawValueOrDefaultOr(null);
	}

	public final List<String> getRawValueOrDefaultOr(List<String> def) {
		loadRawValue();
		return rawValue != null ? rawValue : (defaultValue != null ? defaultValue : def);
	}

	public final String getRawValueLineOrDefault(int index) {
		loadRawValue();
		return rawValue != null && index < rawValue.size() ? rawValue.get(index) : (defaultValue != null && index < defaultValue.size() ? defaultValue.get(index) : null);
	}

	public final void ifRawValueOrDefaultIsPresent(Consumer<List<String>> ifPresent) {
		List<String> value = getRawValueOrDefault();
		if (value != null) {
			ifPresent.accept(value);
		}
	}

	@Override
	public final boolean hasParseableLocations() {
		return hasParseableLocations;
	}

	@Override
	public final boolean isCurrentlyDefault() {
		loadRawValue();
		return (rawValue == null && defaultValue != null) || Objects.deepEquals(rawValue, defaultValue);
	}

	// ----- set
	public final void setValue(List<String> newValue) {
		// same value
		loadRawValue();
		if (newValue == null ? this.rawValue == null : this.rawValue != null && CollectionUtils.contentEquals(newValue, this.rawValue)) {
			return;
		}

		// attempt to parse previous and new value for watchers
		T previous = null, next = null;
		try { previous = this.rawValue.size() == 1 ? doParseString(this.rawValue.get(0)) : doParseList(this.rawValue); } catch (Throwable ignored) {}
		try { next = newValue.size() == 1 ? doParseString(newValue.get(0)) : doParseList(newValue); } catch (Throwable ignored) {}

		// reset valuesCache
		resetCache();
		if (getParent() != null) {
			getParent().resetCache();
		}

		// set value
		if (newValue == null) {
			this.rawValue = null;
			this.hasParseableLocations = defaultValue != null && StringUtils.hasPlaceholders(defaultValue);
		} else {
			List<String> v = new ArrayList<>(newValue.size());
			for (String l : newValue) {
				v.add(StringUtils.format(l).trim());  // reformat since color parsing is made on read
			}
			this.rawValue = Collections.unmodifiableList(v);
			this.hasParseableLocations = StringUtils.hasPlaceholders(v);
		}

		// call watchers
		if (watchers != null) {
			for (BiConsumer<T, T> watcher : watchers) {
				watcher.accept(previous, next);
			}
		}
	}

	public final void addWatcher(BiConsumer<T, T> watcher) {
		if (watchers == null) {
			watchers = CollectionUtils.asList(watcher);
		} else {
			watchers.add(watcher);
		}
	}

	// ----- loading and saving
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

		loadRawValue();
		if (rawValue == null || (defaultValue != null && CollectionUtils.contentEquals(rawValue, defaultValue))) {
			config.write(path, null);
		} else {
			config.write(path, rawValue.size() == 1 ? rawValue.get(0) : rawValue);
		}
	}

	// ----- parsing
	@Override
	public final T doParse(Replacer replacer) throws ParsingError {
		loadRawValue();
		List<String> raw = rawValue != null ? rawValue : defaultValue;
		if (raw == null) {
			return null;
		}

		List<String> parsed = replacer.parse(raw);
		T result;
		if (parsed.size() > 1) {
			result = doParseList(parsed);
		} else if (parsed.size() == 1) {
			result = doParseString(parsed.get(0));
		} else {
			result = doParseEmpty();
		}

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

	// ----- editor
	@Override
	public List<String> editorCurrentValue() {
		loadRawValue();
		return rawValue != null ? rawValue : defaultValue;
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
			call.getGUI().setRegularItem(buildEditorItem(call.getPageIndex(), call.getSlot()));
		}
		// other
		else {
			super.onEditorClick(call);
		}
	}

}
