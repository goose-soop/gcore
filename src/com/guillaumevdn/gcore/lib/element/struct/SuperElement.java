package com.guillaumevdn.gcore.lib.element.struct;

import java.io.File;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.element.struct.container.ContainerElement;
import com.guillaumevdn.gcore.lib.element.struct.map.AbstractMapElement;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.time.duration.ElementDuration;

/**
 * @author GuillaumeVDN
 */
public interface SuperElement {

	// ----- get
	public String getId();
	public GPlugin getPlugin();
	public File getOwnFile();
	public List<String> getLoadErrors();
	public YMLConfiguration getConfiguration();
	public String getConfigurationPath();

	// ----- load/save
	public void reloadConfiguration();
	public void read() throws Throwable;
	public void write() throws Throwable;
	public void addLoadError(String error);

	// ----- editor
	Mat editorIconType();
	ItemStack editorIcon();
	void onEditorClick(ClickCall call);
	default void onEditorChange(Element changed) {
		try {
			// write
			if (ObjectUtils.instanceOf(changed.getParent(), ElementDuration.class)) {
				changed.getParent().write();
			} else {
				changed.write();
			}

			// attempt to fix parents maps if nothing was written
			Element elem = changed, parent;
			for (; elem != null && (parent = elem.getParent()) != null; elem = parent) {
				if (!getConfiguration().contains(parent.getConfigurationPath())) {  // parent is not contained
					if (parent instanceof AbstractMapElement && !(parent instanceof ContainerElement)) {  // ... and parent is a map
						getConfiguration().getBackingYML().mkdirs(elem.getConfigurationPath());  // ... then write empty section so element key is there
						break;
					}
				}
			}

			// save config
			getConfiguration().save();
		} catch (Throwable exception) {
			getPlugin().getMainLogger().error("Couldn't save element " + getId() + " to " + (getOwnFile() != null ? getOwnFile() : getConfiguration().getFile() + "/" + getConfigurationPath()), exception);
		}
	}

}
