package com.guillaumevdn.gcore.lib.gui.element;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.SuperElement;
import com.guillaumevdn.gcore.lib.element.struct.container.ContainerElement;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementString;
import com.guillaumevdn.gcore.lib.gui.element.item.element.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ElementGUIType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIType;
import com.guillaumevdn.gcore.lib.object.Optional;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementGUI extends ContainerElement implements SuperElement {

	private ElementString name = addString("name", Need.required(), TextEditorGeneric.descriptionGuiName);
	private ElementGUIType type = add(new ElementGUIType(this, "type", Need.optional(GUIType.CHEST_6_ROW), TextEditorGeneric.descriptionGuiType));
	private ElementGUIItemList defaultContents = null;

	public ElementGUI(File file, String id, boolean defContents) {
		super("GUI", null, id, Need.optional(), null);
		this.file = file;
		if (defContents) defaultContents = add(new ElementGUIItemList(this, "contents", Need.optional(), TextEditorGeneric.descriptionGuiContents));
	}

	// super element
	private File file;
	private List<String> loadErrors = new ArrayList<>();
	protected YMLConfiguration config = null;

	@Override public GPlugin getPlugin() { return GCore.inst(); }
	@Override public File getOwnFile() { return file; }
	@Override public List<String> getLoadErrors() { return Collections.unmodifiableList(loadErrors); }
	@Override public YMLConfiguration getConfiguration() { if (config == null) { reloadConfiguration(); } return config; }
	@Override public String getConfigurationPath() { return ""; }
	@Override public void addLoadError(String error) { loadErrors.add(error); }
	@Override public void reloadConfiguration() { this.config = new YMLConfiguration(getPlugin(), file); }

	// default implementation
	public List<ElementGUIItem> getContents() {
		return defaultContents.values();
	}

	public Optional<ElementGUIItem> getContent(String id) {
		return defaultContents.getElement(id);
	}

	// get
	public ElementString getName() {
		return name;
	}

	public ElementGUIType getSize() {
		return type;
	}

	// build
	public ActiveElementGUI build(Replacer replacer, ClickCall fromCall) {
		return new ActiveElementGUI(this, replacer, fromCall);
	}

	// editor
	@Override
	public Mat editorIconType() {
		return CommonMats.CHEST;
	}

}
