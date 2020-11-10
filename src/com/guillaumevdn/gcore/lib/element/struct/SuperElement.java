package com.guillaumevdn.gcore.lib.element.struct;

import java.io.File;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;

/**
 * @author GuillaumeVDN
 */
public interface SuperElement {

	// get
	public String getId();
	public GPlugin getPlugin();
	public File getOwnFile();
	public List<String> getLoadErrors();
	public YMLConfiguration getConfiguration();
	public String getConfigurationPath();

	// load/save
	public void reloadConfiguration();
	public void read() throws Throwable;
	public void write() throws Throwable;
	public void addLoadError(String error);

	// editor
	Mat editorIconType();
	ItemStack editorIcon();
	void onEditorClick(ClickCall call);
	default void onEditorChange(Element changed) {
		try {
			changed.write();
			getConfiguration().save();
		} catch (Throwable exception) {
			getPlugin().getMainLogger().error("Couldn't save element " + getId() + " to " + (getOwnFile() != null ? getOwnFile() : getConfiguration().getFile() + "/" + getConfigurationPath()), exception);
		}
	}

}
