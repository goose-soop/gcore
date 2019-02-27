package be.pyrrh4.pyrcore.lib.parseable.data;

import be.pyrrh4.pyrcore.lib.Logger;
import be.pyrrh4.pyrcore.lib.Logger.Level;
import be.pyrrh4.pyrcore.lib.PyrPlugin;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;

public abstract class DataLink {

	// base
	private Parseable component;
	private String superId;
	private PyrPlugin plugin;
	private String lastError = null;

	// compact
	public DataLink(Parseable component, PyrPlugin plugin, String superId) {
		this.component = component;
	}

	// get
	public Parseable getComponent() {
		return component;
	}

	public String getSuperId() {
		return superId;
	}

	public PyrPlugin getPlugin() {
		return plugin;
	}

	public String getLastError() {
		return lastError;
	}

	// set
	public void setComponent(Parseable component) {
		this.component = component;
	}

	// methods
	public void log(String error) {
		String log = buildLog(this.lastError = error);
		Logger.log(Level.SEVERE, plugin != null ? plugin.getName() : "UNKNOWN PLUGIN", log);
	}

	// abstract
	public abstract boolean contains();
	public abstract void setContains(boolean contains);
	protected abstract String buildLog(String error);
	@Override
	public abstract DataLink clone();

	// copy
	public void copyPropertiesTo(DataLink other) {
		other.plugin = plugin;
		other.superId = superId;
	}

}
