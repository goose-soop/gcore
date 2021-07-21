package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.configuration.file.node.ListValueNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.SingleValueNode;
import com.guillaumevdn.gcore.lib.element.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.exception.ConfigError;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * Represents an element that can't be edited in-game and has no particular parsing
 * @author GuillaumeVDN
 */
public class ElementConfigSection<T> extends Element {

	private SectionNode value = null;

	public ElementConfigSection(String typeName, Element parent, String id, Need need, Text editorDescription) {
		super(typeName, parent, id, need.getType(), editorDescription);
	}

	// ----- get
	public SectionNode getValue() {
		return value;
	}

	@Override
	public boolean hasParseableLocations() {
		return false;
	}

	@Override
	public boolean isCurrentlyDefault() {
		return false;
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.NETHER_STAR;
	}

	// ----- set
	public void setValue(SectionNode value) {
		this.value = value;
	}

	// ----- loading and saving
	@Override
	protected void clearBeforeRead() {
		value = null;
	}

	@Override
	protected void doRead() throws Throwable {
		YMLConfiguration config = getSuperElement().getConfiguration();
		String path = getConfigurationPath();
		try {
			value = config.getBackingYML().getSectionNode(path).clone(null);
		} catch (Throwable exception) {
			ConfigError configError = ObjectUtils.findCauseOrNull(exception, ConfigError.class);
			if (configError != null) {
				getSuperElement().addLoadError(configError.getMessage().replace(", in file " + config.getLogFilePath(), ""));
			}
		}
	}

	@Override
	protected void doWrite() throws Throwable {
		YMLConfiguration config = getSuperElement().getConfiguration();
		String path = getConfigurationPath();
		if (value == null) {
			config.write(path, null);
		} else {
			int dotIndex = path.lastIndexOf('.');
			SectionNode parentNode = dotIndex != -1 ? config.getBackingYML().mkdirs(path.substring(0, dotIndex)) : config.getBackingYML().getBase();
			SectionNode clone = value.clone(parentNode);
			parentNode.setConfigNode(clone);
		}
	}

	// ----- editor
	@Override
	public EditorGUI editorGUI(ClickCall fromCall) {
		return null;
	}

	@Override
	public List<String> editorCurrentValue() {
		if (value == null || value.getConfigKeys().isEmpty()) {
			return null;
		}
		List<String> desc = new ArrayList<>();
		addCurrentValue(value, desc);
		return desc;
	}

	private void addCurrentValue(SectionNode section, List<String> list) {
		String prefix = "§7" + StringUtils.repeatString(">", section.getDepthLevel());
		section.getNodes().forEach(node -> {
			if (node instanceof SectionNode) {
				list.add(prefix + " §b" + ((SectionNode) node).getId());
				addCurrentValue((SectionNode) node, list);
			} else if (node instanceof SingleValueNode) {
				list.add(prefix + " §b" + ((SingleValueNode) node).getId() + " §7: " + ((SingleValueNode) node).getValue());
			} else if (node instanceof ListValueNode) {
				list.add(prefix + " §b" + ((ListValueNode) node).getId() + " §7:");
				((ListValueNode) node).getValue().forEach(line -> list.add(prefix + " §7- " + line));
			}
		});
	}

}
