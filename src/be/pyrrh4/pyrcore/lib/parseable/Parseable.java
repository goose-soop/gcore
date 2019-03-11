package be.pyrrh4.pyrcore.lib.parseable;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.data.DataLink;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.editor.ModifCallback;
import be.pyrrh4.pyrcore.lib.util.Pair;
import be.pyrrh4.pyrcore.lib.util.Utils;

public abstract class Parseable implements Comparable<Parseable>, Cloneable {

	// limit
	public static final int MAX_DESCRIPTION_DEPTH = 2;

	// base
	protected String id;
	protected Parseable parent;
	protected boolean mandatory;
	protected int editorSlot;
	protected Mat editorIcon;
	protected List<String> editorDescription;
	private DataLink lastData;

	public Parseable(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		this.id = id;
		this.parent = parent;
		this.mandatory = mandatory;
		this.editorSlot = editorSlot;
		this.editorIcon = editorIcon;
		this.editorDescription = editorDescription;
	}

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

	public DataLink getLastData() {
		return lastData;
	}

	public void setLastData(DataLink lastData) {
		this.lastData = lastData;
	}

	// load and save
	public abstract void load(DataLink data);
	public abstract void save(DataLink data);

	// editor
	public abstract int getEditorSize();
	public abstract int getEditorMaxRegularSlot();
	public abstract int getEditorBackSlot();
	public abstract void fillEditor(EditorGUI gui, Player player, ModifCallback onModif);
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
