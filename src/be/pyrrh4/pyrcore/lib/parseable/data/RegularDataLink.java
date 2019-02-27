package be.pyrrh4.pyrcore.lib.parseable.data;

import be.pyrrh4.pyrcore.lib.PyrPlugin;
import be.pyrrh4.pyrcore.lib.configuration.YMLConfiguration;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;

public class RegularDataLink extends DataLink {

	// base
	private YMLConfiguration config = null;
	private String path = null;
	private boolean contains = false;

	public RegularDataLink(Parseable component, PyrPlugin plugin, String superId, YMLConfiguration config, String path) {
		super(component, plugin, superId);
		this.config = config;
		this.path = path;
	}

	// get
	public YMLConfiguration getConfig() {
		return config;
	}

	public String getPath() {
		return path;
	}

	@Override
	public boolean contains() {
		return contains;
	}

	// set
	@Override
	public void setContains(boolean contains) {
		this.contains = contains;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	// methods
	@Override
	protected String buildLog(String error) {
		return "ON LOAD OF <" + getSuperId() + ">, component <" + getComponent().getId() + "> at path <" + getPath() + "> : " + error;
	}

	@Override
	public RegularDataLink clone() {
		return new RegularDataLink(getComponent(), getPlugin(), getSuperId(), getConfig(), getPath());
	}

}
