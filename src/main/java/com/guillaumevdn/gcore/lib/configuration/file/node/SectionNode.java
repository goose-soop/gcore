package com.guillaumevdn.gcore.lib.configuration.file.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.file.Node;
import com.guillaumevdn.gcore.lib.configuration.file.YMLFile;
import com.guillaumevdn.gcore.lib.configuration.reader.YMLReader;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class SectionNode extends ConfigNode {

	private List<Node> nodes = new ArrayList<>(1);
	private String trailingComment;
	private SectionNodeType type;

	SectionNode(YMLFile file) {
		super(file);
		this.type = SectionNodeType.REGULAR;
	}

	public SectionNode(SectionNode parent, String id, SectionNodeType type, String trailingComment) {
		super(parent, id);
		this.trailingComment = trailingComment;
		this.type = type;
	}

	// ----- get
	public String getPath() {
		if (this instanceof SuperNode) {
			return "";
		}
		String path = getId();
		for (SectionNode node = this; (node = node.getParent()) != null && !(node instanceof SuperNode);) {
			path = node.getId() + "." + path;
		}
		return path;
	}

	public List<String> getConfigKeys() {
		return nodes.stream().filter(node -> node instanceof ConfigNode).map(node -> ((ConfigNode) node).getId()).collect(Collectors.toList());
	}

	public List<Node> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

	public ConfigNode getConfigNode(String id) {
		return (ConfigNode) nodes.stream().filter(node -> node instanceof ConfigNode).filter(node -> ((ConfigNode) node).getId().equalsIgnoreCase(id))
				.findFirst().orElse(null);
	}

	public String getTrailingComment() {
		return trailingComment;
	}

	public final boolean isCompact() {
		return type.equals(SectionNodeType.COMPACT);
	}

	public final boolean isCompactNestedMap() {
		return type.equals(SectionNodeType.COMPACT_NESTED_MAP);
	}

	public final SectionNodeType getType() {
		return type;
	}

	// ----- set
	public void purgeEmptySections() {
		CollectionUtils.iterate(nodes, (next, it) -> {
			SectionNode subSection = ObjectUtils.castOrNull(next, SectionNode.class);
			if (subSection != null) {
				subSection.purgeEmptySections();
				if (subSection.nodes.isEmpty()) {
					it.remove();
				}
			}
		});
	}

	public void addNode(Node node) {
		nodes.add(node);
	}

	public void addLineBreaks(int count) {
		nodes.add(new LineBreaksNode(this, count));
	}

	public void addComment(List<String> comment) {
		nodes.add(new CommentNode(this, comment));
	}

	public SectionNode addSection(String id, SectionNodeType type) {
		SectionNode node = new SectionNode(this, id, type, null);
		addNode(node);
		return node;
	}

	public void setConfigNode(ConfigNode node) {
		for (int i = 0; i < nodes.size(); ++i) {
			ConfigNode elem = ObjectUtils.castOrNull(nodes.get(i), ConfigNode.class);
			if (elem != null && elem.getId().equalsIgnoreCase(node.getId())) {
				nodes.set(i, node);
				return;
			}
		}
		nodes.add(node);
	}

	public void insertBeforeFirstConfigNode(ConfigNode node) {
		removeConfigNode(node);
		for (int i = 0; i < nodes.size(); ++i) {
			ConfigNode elem = ObjectUtils.castOrNull(nodes.get(i), ConfigNode.class);
			if (elem != null) {
				nodes.add(i, node);
				return;
			}
		}
		nodes.add(node);
	}

	public Node removeNode(int index) {
		return nodes.remove(index);
	}

	public void removeConfigNode(ConfigNode node) {
		removeConfigNode(node.getId());
	}

	public void removeConfigNode(String nodeId) {
		for (int i = 0; i < nodes.size(); ++i) {
			ConfigNode elem = ObjectUtils.castOrNull(nodes.get(i), ConfigNode.class);
			if (elem != null && elem.getId().equalsIgnoreCase(nodeId)) {
				nodes.remove(i);
				return;
			}
		}
	}

	public void setSingleValue(String id, String value, String trailingComment) {
		if (value == null) {
			removeConfigNode(id);
		} else {
			for (int i = 0; i < nodes.size(); ++i) {
				ConfigNode configNode = ObjectUtils.castOrNull(nodes.get(i), ConfigNode.class);
				if (configNode != null && configNode.getId().equalsIgnoreCase(id)) {
					// it's a single value already
					SingleValueNode singleNode = ObjectUtils.castOrNull(configNode, SingleValueNode.class);
					if (singleNode != null) {
						singleNode.setValueString(value);
						return;
					}
					// it's not a single value, replace it
					nodes.set(i, new SingleValueNode(this, id, value, trailingComment));
					return;
				}
			}
			// path don't exist, add new value
			SingleValueNode element = new SingleValueNode(this, id, value, trailingComment);
			nodes.add(element);
		}
	}

	public void setSingleValue(String id, List<String> valueWithLineBreaks, String trailingComment) {
		if (valueWithLineBreaks == null || valueWithLineBreaks.isEmpty()) {
			removeConfigNode(id);
		} else {
			for (int i = 0; i < nodes.size(); ++i) {
				ConfigNode configNode = ObjectUtils.castOrNull(nodes.get(i), ConfigNode.class);
				if (configNode != null && configNode.getId().equalsIgnoreCase(id)) {
					// it's a single value already
					SingleValueNode singleNode = ObjectUtils.castOrNull(configNode, SingleValueNode.class);
					if (singleNode != null) {
						singleNode.setValueWithLineBreaks(valueWithLineBreaks);
						return;
					}
					// it's not a single value, replace it
					nodes.set(i, new SingleValueNode(this, id, valueWithLineBreaks, trailingComment));
					return;
				}
			}
			// path don't exist, add new value
			SingleValueNode element = new SingleValueNode(this, id, valueWithLineBreaks, trailingComment);
			nodes.add(element);
		}
	}

	public void setListValue(String id, Collection<?> value, boolean compact, boolean ez, String trailingComment) {
		if (value == null) {
			removeConfigNode(id);
		} else {
			List<String> list = value.stream().map(obj -> String.valueOf(obj)).collect(Collectors.toList());
			for (int i = 0; i < nodes.size(); ++i) {
				ConfigNode configNode = ObjectUtils.castOrNull(nodes.get(i), ConfigNode.class);
				if (configNode != null && configNode.getId().equalsIgnoreCase(id)) {
					// it's a list value already
					ListValueNode listNode = ObjectUtils.castOrNull(configNode, ListValueNode.class);
					if (listNode != null) {
						listNode.setValue(list);
						return;
					}
					// it's not a list value, replace it
					nodes.set(i, new ListValueNode(this, id, list, compact, ez, trailingComment));
					return;
				}
			}
			// path don't exist, add new value
			ListValueNode element = new ListValueNode(this, id, list, compact, ez, trailingComment);
			nodes.add(element);
		}
	}

	// ----- convert
	public DataIO toIO(boolean snakeCaseToCamelCase, @Nullable Replacer replacer) {
		if (replacer == null) {
			replacer = Replacer.GENERIC;
		}
		DataIO data = new DataIO();
		for (Node elem : nodes) {
			ConfigNode node = ObjectUtils.castOrNull(elem, ConfigNode.class);
			if (node != null) {
				String key = node.getId();
				if (node instanceof SectionNode) {
					data.writeObject(maybeToCamelCase(key, snakeCaseToCamelCase), ((SectionNode) node).toIO(snakeCaseToCamelCase, replacer));
				} else if (node instanceof ListValueNode) {
					data.writeSerializedList(maybeToCamelCase(key, snakeCaseToCamelCase), replacer.parse(((ListValueNode) node).getValue()));
				} else if (node instanceof SingleValueNode) {
					// attempt to cast value into number, or just write the value as it is
					String value = replacer.parse(((SingleValueNode) node).getValue());
					Long nbl = NumberUtils.longOrNull(value);
					if (nbl != null)
						data.write(maybeToCamelCase(key, snakeCaseToCamelCase), nbl);
					else {
						Integer nbi = NumberUtils.integerOrNull(value);
						if (nbi != null)
							data.write(maybeToCamelCase(key, snakeCaseToCamelCase), nbi);
						else {
							Double nbd = NumberUtils.doubleOrNull(value);
							if (nbd != null)
								data.write(maybeToCamelCase(key, snakeCaseToCamelCase), nbd);
							else
								data.write(maybeToCamelCase(key, snakeCaseToCamelCase), StringUtils.format(value));
						}
					}
				}
			}
		}
		return data;
	}

	private static String maybeToCamelCase(String key, boolean snakeCaseToCamelCase) {
		return snakeCaseToCamelCase ? StringUtils.snakeCaseToCamelCase(key) : key;
	}

	// ----- write
	private static final List<String> FORCE_LINE_BREAKS = CollectionUtils.asList("branches", "objects", "contents");

	@Override
	public void write(Appendable writer, WriteType type) throws Throwable {
		// force compact
		if (isCompact()) {
			writeInCompact(writer, true);
		}
		// regular section or compact nested map
		else {

			if (type.writePrefix())
				writer.append(getPrefix());
			if (type.writeId())
				writer.append(YMLReader.wrapIdToWrite(getId()) + ":");

			if (nodes.isEmpty()) {
				writer.append(" {}" + (trailingComment != null ? "  #" + trailingComment : "") + "\n");
			} else {
				if (type.writeId() || trailingComment != null) {
					writer.append((trailingComment != null ? "  #" + trailingComment : "") + "\n");
				}
				boolean forceLineBreaks = FORCE_LINE_BREAKS.contains(getId());
				if (forceLineBreaks) {
					writer.append("\n");
				}
				for (int i = 0; i < getNodes().size(); ++i) {
					Node elem = getNodes().get(i);
					if (isCompactNestedMap()) {
						if (elem instanceof ConfigNode) {
							if (type.writeId() || trailingComment != null
									|| (type.equals(WriteType.VALUE) && i > 0) /* for anonymous sections, we still need to write more lines */) {
								writer.append(elem.getPrefix());
							}
							writer.append("- ");
						}
						elem.write(writer, WriteType.VALUE);
					} else {
						elem.write(writer, getParent() != null && getParent().isCompactNestedMap() && i == 0 ? WriteType.ID_VALUE : WriteType.PREFIX_ID_VALUE);
					}
					if (forceLineBreaks && i + 1 < getNodes().size() && !(elem instanceof LineBreaksNode)
							&& !(getNodes().get(i + 1) instanceof LineBreaksNode)) {
						writer.append("\n");
					}
				}
			}
		}
	}

	@Override
	public void writeInCompact(Appendable writer, boolean compactParent) throws Throwable {
		if (compactParent) {
			if (!getParent().isCompactNestedMap()) {
				writer.append((compactParent ? getPrefix() : "") + YMLReader.wrapIdToWrite(getId()) + ": ");
			}
		}
		if (isCompactNestedMap()) {
			throw new IllegalArgumentException("can't have a compact nested map inside of a compact section");
		}
		if (nodes.isEmpty()) {
			writer.append("{}");
		} else {
			writer.append("{ ");
			for (int i = 0; i < getNodes().size(); ++i) {
				Node elem = getNodes().get(i);
				elem.writeInCompact(writer, false);
				if (i + 1 < getNodes().size()) {
					writer.append(", ");
				}
			}
			writer.append(" }");
		}
		writer.append((trailingComment != null ? "  #" + trailingComment : "") + (compactParent ? "\n" : ""));
	}

	// ----- clone
	@Override
	public SectionNode clone(SectionNode parent) {
		SectionNode clone = new SectionNode(parent, getId(), type, trailingComment);
		nodes.forEach(element -> clone.nodes.add(element.clone(clone)));
		return clone;
	}

	// ----- type
	public static enum SectionNodeType {

		REGULAR, COMPACT, COMPACT_NESTED_MAP

	}

}
