package be.pyrrh4.pyrcore.lib.loadable;

import java.util.List;

import be.pyrrh4.pyrcore.lib.configuration.YMLConfiguration;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorCallback;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.util.Utils;

public abstract class AbstractSetting<T> implements Comparable<AbstractSetting<?>> {

	// static base
	protected static final List<Character> parseIndicators = Utils.asList('{', '%');

	// base
	private String id;
	private T def;
	private boolean mandatory;
	private String typeName;
	private List<String> description;
	private T value = null;

	public AbstractSetting(String id, T def, boolean mandatory, String typeName, List<String> description) {
		this.id = id;
		this.def = def;
		this.mandatory = mandatory;
		this.typeName = typeName;
		this.description = description;
	}

	// get
	public String getId() {
		return id;
	}

	public T getDef() {
		return def;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public String getTypeName() {
		return typeName;
	}

	public List<String> getDescription() {
		return description;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	// overriden
	@Override
	public String toString() {
		return getId();
	}

	@Override
	public int compareTo(AbstractSetting<?> other) {
		if (isMandatory() && !other.isMandatory()) return -1;
		if (other.isMandatory() && !isMandatory()) return 1;
		return String.CASE_INSENSITIVE_ORDER.compare(getId(), other.getId());
	}

	// abstract methods
	public abstract void load(YMLConfiguration config, String configPath, LoadResult<?> result);
	public abstract void save(YMLConfiguration config, String configPath);
	public abstract void initializeEditorItem(final EditorGUI parent, final int slot, final EditorCallback onModif);
	public abstract List<String> fillEditorItemLore();
	public abstract List<String> fillEditorItemLore(List<String> description);

}
