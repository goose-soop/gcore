package be.pyrrh4.pyrcore.lib.parseable.data;

import java.util.Map;

import be.pyrrh4.pyrcore.lib.PyrPlugin;
import be.pyrrh4.pyrcore.lib.configuration.YMLConfiguration;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class CompactDataLink extends RegularDataLink {

	// base
	private Map<String, String> parameters;
	private String raw;
	private boolean contains = false;

	public CompactDataLink(Parseable component, PyrPlugin plugin, String superId, YMLConfiguration config, String path) {
		super(component, plugin, superId, config, path);
	}

	// get
	public CompactDataLink getCompact() {
		CompactDataLink data = this;
		for (;;) {
			DataLink parent = data.getComponent().getParent() != null ? data.getComponent().getParent().getLastData() : null;
			if (!Utils.instanceOf(parent, CompactDataLink.class)) break;
			data = (CompactDataLink) parent;
		}
		return data;
	}

	public int getDepth() {
		int depth = 1;
		CompactDataLink data = this;
		for (; data != null; ++depth) {
			DataLink parent = data.getComponent().getParent() != null ? data.getComponent().getParent().getLastData() : null;
			if (!Utils.instanceOf(parent, CompactDataLink.class)) break;
			data = (CompactDataLink) parent;
		}
		return depth;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public String getRaw() {
		return raw;
	}

	@Override
	public boolean contains() {
		return contains;
	}

	// set
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public void setRaw(String raw) {
		this.raw = raw;
	}

	@Override
	public void setContains(boolean contains) {
		this.contains = contains;
	}

	// methods
	@Override
	protected String buildLog(String error) {
		return "ON LOAD OF <" + getSuperId() + ">, component <" + getComponent().getId() + "> for compact component at path <" + getCompact().getPath() + "> : " + error;
	}

	@Override
	public CompactDataLink clone() {
		return new CompactDataLink(getComponent(), getPlugin(), getSuperId(), getConfig(), getPath());
	}

}
