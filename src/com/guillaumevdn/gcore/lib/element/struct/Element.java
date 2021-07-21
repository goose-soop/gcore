package com.guillaumevdn.gcore.lib.element.struct;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.element.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.element.struct.list.referenceable.Node;
import com.guillaumevdn.gcore.lib.element.struct.map.AbstractMapElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableElement;
import com.guillaumevdn.gcore.lib.exception.ConfigError;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.tuple.IntegerPair;

/**
 * @author GuillaumeVDN
 */
public abstract class Element implements IElement, Comparable<Element> {

	private final String typeName;
	private Element parent;
	private SuperElement superElement;
	private final String id;
	private final NeedType need;
	private final Text editorDescription;

	private String forcedConfigurationPath = null;  // used in YMLConfiguration, to load elements quickly with no parent

	public Element(String typeName, Element parent, String id, NeedType need, Text editorDescription) {
		this.typeName = typeName;
		this.id = id.toLowerCase();
		this.need = need;
		this.editorDescription = editorDescription;
		setParent(parent);
	}

	// ----- get
	public String getTypeName() {
		return typeName;
	}

	public final Element getParent() {
		return parent;
	}

	@Override
	public final SuperElement getSuperElement() {
		return superElement;
	}

	public final <T extends SuperElement> T getSuperElementAsOrNull(Class<T> elementClass) {
		return ObjectUtils.castOrNull(superElement, elementClass);
	}

	public final String getId() {
		return id;
	}

	public final NeedType getNeed() {
		return need;
	}

	public final boolean isRequiredInContext() {
		Element parent = this.parent;
		if (parent == null || parent.readContains()) {
			return getNeed().equals(NeedType.REQUIRED);
		}
		return false;
	}

	public Text getEditorDescription() {
		return editorDescription;
	}

	public String getConfigurationPath() {
		if (forcedConfigurationPath != null) {
			return forcedConfigurationPath;
		}

		// this method is overriden for super elements, so parent can't be null here
		String parentPath = parent.getConfigurationPath();
		return parentPath == null || parentPath.isEmpty() ? id : parentPath + "." + id;
	}

	@Override
	public abstract boolean hasParseableLocations();
	public abstract boolean isCurrentlyDefault();

	// ----- set
	public final void setParent(Element parent) {
		this.parent = parent;
		// find super element
		Element elem = this;
		while (elem != null) {
			superElement = ObjectUtils.castOrNull(elem, SuperElement.class);
			if (superElement != null) break;
			elem = elem.getParent();
		}
		if (superElement == null) {
			throw new IllegalArgumentException("no super element found in chain");
		}
	}

	public final void setForcedConfigurationPath(String forcedConfigurationPath) {
		this.forcedConfigurationPath = forcedConfigurationPath;
	}

	// ----- object
	@Override
	public final String toString() {
		return id;
	}

	@Override
	public final int compareTo(Element other) {
		return StringUtils.compareAlphabeticallyWithNumbersIgnoreCase(id, other.id);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		Element other = ObjectUtils.castOrNull(obj, getClass());
		return other != null && other.getId().equals(id);
	}

	// ----- loading and saving
	private boolean readContains = false;

	public final boolean readContains() {
		return readContains;
	}

	public void resetCache() {
	}

	/** @throws Throwable if any error, other than a ConfigError or one with a ConfigError parent, occurred */
	public final void read() throws Throwable {
		readContains = false;
		clearBeforeRead();
		try {
			// contains
			YMLConfiguration config = getSuperElement().getConfiguration();
			String path = getConfigurationPath();
			if (config.contains(path)) {
				readContains = true;
				doRead();
			}
			// not in config
			else {
				if (getNeed().equals(NeedType.REQUIRED)) {
					ParseableElement parseable = ObjectUtils.castOrNull(this, ParseableElement.class);
					Object def = parseable == null ? null : parseable.parseGeneric().orNull();
					if (def == null) {
						superElement.addLoadError("missing " + getTypeName() + " at path " + path);
					}
				}
			}
		} catch (Throwable exception) {
			ConfigError configError = ObjectUtils.findCauseOrNull(exception, ConfigError.class);
			if (configError != null) {  // a config error that wasn't catched already ? :think:
				superElement.addLoadError(configError.getMessage());
			} else {  // regular error, re-throw it
				throw exception;
			}
		}
	}

	/** @throws Throwable if any error occurred */
	public final void write() throws Throwable {
		doWrite();
	}

	protected abstract void clearBeforeRead();
	protected abstract void doRead() throws Throwable;
	protected abstract void doWrite() throws Throwable;

	// ----- editor
	public abstract Mat editorIconType();

	public List<String> editorCurrentValue() {
		return null;
	}

	public final List<String> editorCurrent() {
		List<String> value = editorCurrentValue();
		if (value == null || value.isEmpty() || value.get(0).isEmpty()) {
			if (this instanceof SuperElement) {
				return null;
			}
			return (isCurrentlyDefault() ? TextEditorGeneric.elementCurrentValueNoneDefault : TextEditorGeneric.elementCurrentValueNone).parseLines();
		} else if (value.size() == 1) {
			return (isCurrentlyDefault() ? TextEditorGeneric.elementCurrentValueSingleDefault : TextEditorGeneric.elementCurrentValueSingle).replace("{value}", () -> value.get(0)).parseLines();
		} else {
			List<String> valueList = new ArrayList<>();
			value.forEach(line -> valueList.addAll(TextEditorGeneric.elementCurrentValueListLine.replace("{line}", () -> line).parseLines()));
			return (isCurrentlyDefault() ? TextEditorGeneric.elementCurrentValueListDefault : TextEditorGeneric.elementCurrentValueList).replace("{value}", () -> valueList).parseLines();
		}
	}

	public List<String> nonControlEditorIconLore() {
		List<String> lore = CollectionUtils.asList();
		// current value
		List<String> current = editorCurrent();
		if (current != null) {
			lore.add("§r");
			lore.addAll(current);
		}
		// description
		if (getEditorDescription() != null) {
			if (current != null) lore.add("§r");
			lore.addAll(TextEditorGeneric.elementDescription.replace("{description}", () -> getEditorDescription().parseLines()).parseLines());
		}
		// type
		if (!(this instanceof SuperElement)) {
			if (current != null || getEditorDescription() != null) lore.add("§r");
			lore.addAll((getNeed().equals(NeedType.REQUIRED) ? TextEditorGeneric.elementTypeMandatory : TextEditorGeneric.elementTypeOptional).replace("{type}", () -> getTypeName()).parseLines());
		}
		return lore;
	}

	public List<String> editorIconLore() {
		return nonControlEditorIconLore();
	}

	public final ItemStack editorIcon() {
		List<String> lore = editorIconLore();
		lore = lore == null ? null : StringUtils.splitLongText(lore, 50);
		if (lore != null && lore.size() > 30) {
			while (lore.size() > 30) lore.remove(lore.size() - 1);
			lore.add("§7...");
		}
		ItemStack icon = ItemUtils.addAllFlags(ItemUtils.createItem(editorIconType(), "§6" + getId(), lore));
		if (!isCurrentlyDefault()) {
			icon.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		}
		if (this instanceof AbstractMapElement) {
			int abstraction = getAbstraction(this, 1);
			icon.setAmount(abstraction > 64 ? 64 : abstraction);
		}
		return icon;
	}

	private int getAbstraction(Element element, int current) {
		int biggest = current;
		if (element instanceof AbstractMapElement) {
			for (Object elem : ((AbstractMapElement<?, ?>) element).values()) {
				if (elem instanceof Element) {
					int level = getAbstraction((Element) elem, current + 1);
					if (level > biggest) {
						biggest = level;
					}
				} else if (elem instanceof Node) {
					int level = getAbstraction(((Node) elem).getValue(), current + 1);
					if (level > biggest) {
						biggest = level;
					}
				}
			}
		}
		return biggest;
	}

	public final GUIItem buildEditorItem(int page, int slot) {
		return new GUIItem("element_" + getId(), CollectionUtils.asList(IntegerPair.of(page, slot)), ItemUtils.addAllFlags(editorIcon()), call -> onEditorClick(call));
	}

	public EditorGUI editorGUI(ClickCall fromCall) {
		return null;
	}

	public void onEditorClick(ClickCall call) {
		// left-click : edit
		if (call.getType().equals(ClickType.LEFT)) {
			EditorGUI editor = editorGUI(call);
			if (editor != null) {
				editor.openFor(call.getClicker(), call);
			}
		}
	}

}
