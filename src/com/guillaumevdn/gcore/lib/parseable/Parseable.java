package com.guillaumevdn.gcore.lib.parseable;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorItem;
import com.guillaumevdn.gcore.lib.parseable.editor.ModifCallback;
import com.guillaumevdn.gcore.lib.util.Pair;
import com.guillaumevdn.gcore.lib.util.Utils;

public abstract class Parseable implements Comparable<Parseable>, Cloneable {

	// base
	protected String id;
	protected Parseable parent;
	protected boolean mandatory;
	protected int editorSlot;
	protected Mat editorIcon;
	protected List<String> editorDescription;
	private ConfigData lastData;

	public Parseable(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		this.id = id;
		this.parent = parent;
		this.mandatory = mandatory;
		this.editorSlot = editorSlot;
		this.editorIcon = editorIcon;
		this.editorDescription = editorDescription;
	}

	// get
	public String getId() {
		return id;
	}

	public Parseable getParent() {
		return parent;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public int getEditorSlot() {
		return editorSlot;
	}

	public Mat getEditorIcon() {
		return editorIcon;
	}

	public List<String> getEditorDescription() {
		return editorDescription;
	}

	public ConfigData getLastData() {
		return lastData;
	}

	public void setLastData(ConfigData lastData) {
		this.lastData = lastData;
	}

	// set
	/** To use with extreme caution ! */
	@Deprecated
	public void setId(String id) {
		this.id = id;
	}

	// load and save
	public abstract boolean hasErrors();
	public abstract void load(ConfigData data);
	public abstract void save(ConfigData data);

	// editor
	public EditorGUI createEditor(final EditorGUI parent, final Player player, final ModifCallback onModif) {
		if (getLastData() == null || getLastData().getPlugin() == null) return null;
		return new EditorGUI(getLastData().getPlugin(), parent, parent != null ? Utils.getNewInventoryName(parent.getName(), getId()) : getId(), getEditorSize(), getEditorRegularSlots()) {
			@Override
			protected void fill() {
				fillEditor(this, player, onModif);
				if (parent != null) {
					setPersistentItem(new EditorItem("control_item_back", getEditorBackSlot(), Mat.ARROW, GLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
						@Override
						protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
							parent.open(player);
						}
					});
				}
			}
		};
	}

	public abstract int getEditorSize();
	public abstract List<Integer> getEditorRegularSlots();
	public abstract int getEditorBackSlot();
	protected abstract void fillEditor(EditorGUI gui, Player player, ModifCallback onModif);
	public abstract List<String> describe(int depth);

	// misc
	@Override
	public int compareTo(Parseable other) {
		return String.CASE_INSENSITIVE_ORDER.compare(id, other.id);
	}

	@Override
	public String toString() {
		return id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parseable other = (Parseable) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	// clone
	protected Parseable() {
	}

	@Override
	public Parseable clone() {
		return cloneAs(getClass());
	}

	public <T extends Parseable> T cloneAs(Class<T> clazz) {
		try {
			// new object
			Constructor<T> constructor = clazz.getConstructor();
			constructor.setAccessible(true);
			T clone = constructor.newInstance();
			// clone properties
			clone.id = id;
			clone.parent = parent;
			clone.mandatory = mandatory;
			clone.editorIcon = editorIcon;
			clone.editorDescription = Utils.asList(editorDescription);
			// success
			return clone;
		} catch (Throwable exception) {
			exception.printStackTrace();
			return null;
		}
	}

	// arguments
	public static final List<String> ARGUMENT_SEPARATORS = Utils.asList("~", "@", "$", "%", "^");

	public static Map<String, String> getCompactArguments(String raw, int depthIndex) {
		String separator = ARGUMENT_SEPARATORS.get(depthIndex);
		Map<String, String> result = new HashMap<String, String>();
		for (String rawArgument : Utils.split(separator, raw, false)) {
			Pair<String, String> separated = Utils.separateRootAtChar(rawArgument, ' ');
			result.put(separated.getA(), separated.getB());
		}
		return result;
	}

}
