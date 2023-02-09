package com.guillaumevdn.gcore.lib.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.SuperElement;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.object.NeedType;

/**
 * @author GuillaumeVDN
 */
public final class FakeConfigSuperElement extends Element implements SuperElement {

	private List<String> loadErrors = new ArrayList<>();
	private YMLConfiguration config;
	private String parentPath;

	public FakeConfigSuperElement(YMLConfiguration config, String parentPath) {
		super(null, parentPath.replace('.', '_'), NeedType.OPTIONAL, null);
		this.config = config;
		this.parentPath = parentPath;
	}

	// ----- get
	@Override
	public GPlugin getPlugin() {
		return config.getPlugin();
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
		return parentPath;
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
