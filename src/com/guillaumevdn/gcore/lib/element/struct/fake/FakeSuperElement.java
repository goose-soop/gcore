package com.guillaumevdn.gcore.lib.element.struct.fake;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.configuration.FakeYMLConfiguration;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.SuperElement;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.object.NeedType;

/**
 * @author GuillaumeVDN
 */
public final class FakeSuperElement extends Element implements SuperElement {

	private GPlugin plugin;
	private List<String> loadErrors = new ArrayList<>();
	private FakeYMLConfiguration config;

	public FakeSuperElement(GPlugin plugin, String id) {
		super(null, id, NeedType.OPTIONAL, null);
		this.plugin = plugin;
		config = new FakeYMLConfiguration(plugin);
	}

	// ----- get
	@Override
	public GPlugin getPlugin() {
		return plugin;
	}

	@Override
	public File getOwnFile() {
		return null;
	}

	@Override
	public List<String> getLoadErrors() {
		return loadErrors;
	}

	@Override
	public YMLConfiguration getConfiguration() {
		return config;
	}

	@Override
	public String getConfigurationPath() {
		return "";
	}

	@Override
	public boolean hasParseableLocations() {
		return false;
	}

	@Override
	public boolean isCurrentlyDefault() {
		return true;
	}

	// ----- load/save
	public void reloadConfiguration() {
	}

	@Override
	protected void doRead() throws Throwable {
	}

	@Override
	protected void doWrite() throws Throwable {
	}

	@Override
	protected void clearBeforeRead() {
	}

	@Override
	public void addLoadError(String error) {
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.APPLE;
	}

	@Override
	public void onEditorClick(ClickCall call) {
	}

	@Override
	public void onEditorChange(Element changed) {
	}

}
