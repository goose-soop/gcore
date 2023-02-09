package com.guillaumevdn.gcore.lib.configuration.file.node;

import java.util.List;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.reader.YMLReader;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public class ListValueNode extends ConfigNode {

	private List<String> value;
	private boolean compact, ez;
	private String trailingComment;

	public ListValueNode(SectionNode parent, String id, List<String> value, boolean compact, boolean ez, String trailingComment) {
		super(parent, id);
		this.value = value;
		this.compact = compact;
		this.ez = ez;
		this.trailingComment = trailingComment;
	}

	// ----- get
	public List<String> getValue() {
		return value;
	}

	public boolean isCompact() {
		return compact;
	}

	public boolean isEz() {
		return ez;
	}

	public String getTrailingComment() {
		return trailingComment;
	}

	// ----- set
	public void setValue(List<String> value) {
		if (value == null) throw new IllegalArgumentException("value can't be null");
		this.value = CollectionUtils.asList(value);
		if (value.stream().anyMatch(line -> line.contains("\n"))) {
			ez = false;
		}
	}

	// ----- write
	@Override
	public void write(Appendable writer, WriteType type) throws Throwable {
		String prefix = getPrefix();

		if (type.writePrefix()) writer.append(prefix);
		if (type.writeId()) writer.append(YMLReader.wrapIdToWrite(getId()) + ": ");

		if (value.isEmpty()) {
			writer.append("[]" + (trailingComment != null ? "  #" + trailingComment : "") + "\n");
		} else if (shouldWriteCompact()) {
			writer.append("[" + StringUtils.toTextString(", ", StringUtils.retranslateColorCodesCopy(value.stream().map(line -> YMLReader.wrapValueToWrite(line.replace('\n', ' '))).collect(Collectors.toList()))) + "]" + (trailingComment != null ? "  #" + trailingComment : "") + "\n");
		} else if (ez) {
			writer.append("|" + (trailingComment != null ? "  #" + trailingComment : "") + "\n");
			String linePrefix = prefix + "  ";
			for (String line : StringUtils.retranslateColorCodesCopy(value)) {
				writer.append(linePrefix + line + "\n");
			}
		} else {
			if (type.writeId() || trailingComment != null) {
				writer.append((trailingComment != null ? " #" + trailingComment : "") + "\n");
			}
			String linePrefix = prefix + "  - ";
			int i = 0;
			for (String line : StringUtils.retranslateColorCodesCopy(value)) {
				if (type.writePrefix() || i > 0) {
					writer.append(linePrefix);
				} else {
					writer.append("- ");
				}
				if (line.contains("\n")) {
					String[] split = line.split("\\n");
					for (int j = 0; j < split.length; ++j) {
						writer.append((split[j].contains("&") ? split[j] : "&r" + split[j]) + "\n");
						if (j + 1 < split.length) {
							writer.append(prefix + "    ");
						}
					}
				} else {
					writer.append(YMLReader.wrapValueToWrite(line) + "\n");
				}
				++i;
			}
		}
	}

	@Override
	public void writeInCompact(Appendable writer, boolean compactParent) throws Throwable {
		if (value.isEmpty()) {
			writer.append(YMLReader.wrapIdToWrite(getId()) + ": []");
		} else {
			writer.append(YMLReader.wrapIdToWrite(getId()) + ": [" + StringUtils.toTextString(", ", StringUtils.retranslateColorCodesCopy(value.stream().map(line -> YMLReader.wrapValueToWrite(line.replace('\n', ' '))).collect(Collectors.toList()))) + "]");
		}
	}

	private boolean shouldWriteCompact() {
		if (compact) return true;
		if (ez) return false;
		if (value.stream().allMatch(elem -> NumberUtils.longOrNull(elem) != null || NumberUtils.doubleOrNull(elem) != null)) return true;
		if (value.size() == 1 && !value.get(0).contains(",") && value.get(0).length() < 25 && StringUtils.unformat(value.get(0)).length() == value.get(0).length()) return true;
		return false;
	}

	// ----- clone
	@Override
	public ListValueNode clone(SectionNode parent) {
		return new ListValueNode(parent, getId(), CollectionUtils.asList(value), compact, ez, trailingComment);
	}

}
