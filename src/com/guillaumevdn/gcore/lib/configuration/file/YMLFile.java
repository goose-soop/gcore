package com.guillaumevdn.gcore.lib.configuration.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;

import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.configuration.file.Node.WriteType;
import com.guillaumevdn.gcore.lib.configuration.file.node.ConfigNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.ListValueNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode.SectionNodeType;
import com.guillaumevdn.gcore.lib.configuration.file.node.SingleValueNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.SuperNode;
import com.guillaumevdn.gcore.lib.configuration.reader.YMLReader;
import com.guillaumevdn.gcore.lib.exception.ConfigError;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public class YMLFile {

	private SuperNode base = new SuperNode(this);
	private YMLConfiguration configuration;

	public YMLFile(YMLConfiguration configuration) {
		this.configuration = configuration;
	}

	// ----- get
	public SuperNode getBase() {
		return base;
	}

	public YMLConfiguration getConfiguration() {
		return configuration;
	}

	public boolean contains(String path) {
		return getConfigNode(path) != null;
	}

	public boolean isConfigurationSection(String path) {
		return getSectionNode(path) != null;
	}

	public SectionNode getSectionNode(String path) {
		return ObjectUtils.castOrNull(getConfigNode(path), SectionNode.class);
	}

	public Object getConfigValue(String path) {
		ConfigNode node = getConfigNode(path);
		if (node != null) {
			SingleValueNode singleNode = ObjectUtils.castOrNull(node, SingleValueNode.class);
			if (singleNode != null) {
				return singleNode.getValue();
			}
			ListValueNode listNode = ObjectUtils.castOrNull(node, ListValueNode.class);
			if (listNode != null) {
				return listNode.getValue();
			}
			throw new ConfigError("expected config value but found " + node.getClass().getSimpleName() + " at path " + path);
		}
		return null;
	}

	public String getConfigValueString(String path) {
		Object value = getConfigValue(path);
		if (value == null) {
			return null;
		}
		List<String> list = ObjectUtils.castOrNull(value, List.class);
		if (list != null) {
			return list.isEmpty() ? null : list.get(0);
		}
		return (String) value;
	}

	public ConfigNode getConfigNode(String path) {
		if (path.isEmpty()) return base;
		if (path.charAt(0) == '.') path = path.substring(1);
		List<String> split = StringUtils.split(path, ".", -1);
		SectionNode node = base;
		String id = null;
		while (node != null && (id = split.remove(0)) != null) {
			ConfigNode next = node.getConfigNode(id);
			if (next == null) {
				return null;
			}
			if (split.isEmpty()) {
				return next;
			}
			node = ObjectUtils.castOrNull(next, SectionNode.class);
		}
		return null;
	}

	// ----- write
	public void write(File file) throws Throwable {
		File parent = file.getParentFile();
		if (parent != null) {
			parent.mkdirs();
		}
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));) {
			base.write(writer, WriteType.PREFIX_ID_VALUE);
		}
	}

	public SectionNode mkdirs(String path) {
		return mkdirs(path, SectionNodeType.REGULAR);
	}

	public SectionNode mkdirs(String path, SectionNodeType type) {
		List<String> split = StringUtils.split(path, ".", -1);
		SectionNode node = base;
		String id = null;
		while (!split.isEmpty() && (id = split.remove(0)) != null) {
			ConfigNode sub = node.getConfigNode(id);
			if (ObjectUtils.instanceOf(sub, SectionNode.class)) {
				node = (SectionNode) sub;
			} else {
				if (sub != null) {
					node.removeConfigNode(sub);
				}

				SectionNodeType subType;
				if (node.getType().equals(SectionNodeType.COMPACT)) {
					subType = SectionNodeType.COMPACT;
				} else if (split.isEmpty()) {
					subType = type;
				} else {
					subType = SectionNodeType.REGULAR;  // do not automatically create compact nested maps for intermediate parents
				}

				sub = new SectionNode(node, id, subType, null);
				node.setConfigNode(sub);
				node = (SectionNode) sub;
			}
		}
		return node;
	}

	public void set(String path, Object value) {
		if (value == null) {
			remove(path);
		} else {
			ConfigNode old = getConfigNode(path);
			int index = path.lastIndexOf('.');
			String id = index == -1 ? path : path.substring(index + 1);
			SectionNode parent = index == -1 ? base : mkdirs(path.substring(0, index));
			if (value instanceof Collection<?>) {
				parent.setListValue(id, (Collection<?>) value, false, false, old != null ? old.getTrailingComment() : null);
			} else {
				parent.setSingleValue(id, String.valueOf(value), old != null ? old.getTrailingComment() : null);
			}
		}
	}

	public void remove(String path) {
		if (path == null || path.isEmpty()) {
			base.getNodes().clear();
		} else {
			if (path.contains(".")) {
				int index = path.lastIndexOf('.');
				SectionNode parent = getSectionNode(path.substring(0, index));
				if (parent != null) {
					parent.removeConfigNode(path.substring(index + 1));
					base.purgeEmptySections();
				}
			} else {
				base.removeConfigNode(path);
				base.purgeEmptySections();
			}
		}
	}

	// ----- do
	public void print() {
		try {
			StringBuilder builder = new StringBuilder();
			base.write(builder, WriteType.PREFIX_ID_VALUE);
			Bukkit.getLogger().info(builder.toString());
		} catch (Throwable exeption) {
			exeption.printStackTrace();
		}
	}

	// ----- read
	public void read() throws Throwable {
		try {
			this.base = YMLReader.readFile(this);
		} catch (Throwable error) {
			this.base = new SuperNode(this);
			throw error;
		}
	}

}
