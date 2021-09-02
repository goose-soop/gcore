package com.guillaumevdn.gcore.lib.configuration.file.node;

import java.util.List;

import org.bukkit.Bukkit;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.reader.YMLReader;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public class SingleValueNode extends ConfigNode {

	private List<String> valueWithLineBreaks;
	private String trailingComment;

	public SingleValueNode(SectionNode parent, String id, String value, String trailingComment) {
		super(parent, id);
		this.trailingComment = trailingComment;
		setValueString(value);
	}

	public SingleValueNode(SectionNode parent, String id, List<String> valueWithLineBreaks, String trailingComment) {
		super(parent, id);
		if (valueWithLineBreaks.isEmpty()) {
			throw new IllegalArgumentException("value can't be null (empty list)");
		}
		this.trailingComment = trailingComment;
		setValueWithLineBreaks(valueWithLineBreaks);
	}

	// ----- get
	public String getValue() {
		return StringUtils.toTextString(" ", valueWithLineBreaks);
	}

	public List<String> getValueWithLineBreaks() {
		return valueWithLineBreaks;
	}

	public String getTrailingComment() {
		return trailingComment;
	}

	// ----- set
	public void setValueString(String value) {
		if (value == null) throw new IllegalArgumentException("value can't be null");
		// find line length limit
		int lengthLimit;
		if (valueWithLineBreaks != null && valueWithLineBreaks.size() > 1) {
			int med = valueWithLineBreaks.stream().mapToInt(line -> line.length()).sum() / valueWithLineBreaks.size();
			if (med > 25) { // on abuse pas trop non plus quand même
				lengthLimit = med;
			} else {
				lengthLimit = 75;
			}
		} else {
			lengthLimit = Integer.MAX_VALUE;
		}
		// set value
		if (value.length() > lengthLimit) {
			setValueWithLineBreaks(StringUtils.splitLongText(value, lengthLimit, v -> StringUtils.retranslateColorCodes(v)));
		} else {
			setValueWithLineBreaks(CollectionUtils.asList(value));
		}
	}

	public void setValueWithLineBreaks(List<String> value) {
		if (value == null || value.isEmpty()) throw new IllegalArgumentException("value can't be null");
		this.valueWithLineBreaks = value;
	}

	// ----- print
	public void print() {
		String prefix = getPrefix();
		if (valueWithLineBreaks.size() == 1) {
			Bukkit.getLogger().info(getDepthLevel() + " " + prefix + getId() + ": " + YMLReader.wrapValueToWrite(StringUtils.retranslateColorCodes(valueWithLineBreaks.get(0))) + (trailingComment != null ? " #" + trailingComment : ""));
		} else {
			Bukkit.getLogger().info(getDepthLevel() + " " + prefix + getId() + ": >" + (trailingComment != null ? " #" + trailingComment : ""));
			for (String line : StringUtils.retranslateColorCodes(valueWithLineBreaks)) {
				Bukkit.getLogger().info(getDepthLevel() + " " + prefix + "  " + line);
			}
		}
	}

	// ----- write
	@Override
	public void write(Appendable writer, WriteType type) throws Throwable {
		String prefix = getPrefix();

		if (type.writePrefix()) writer.append(prefix);
		if (type.writeId()) writer.append(YMLReader.wrapIdToWrite(getId()) + ": ");

		if (valueWithLineBreaks.size() == 1) {
			writer.append(YMLReader.wrapValueToWrite(StringUtils.retranslateColorCodes(valueWithLineBreaks.get(0))) + (trailingComment != null ? " #" + trailingComment : "") + "\n");
		} else {
			writer.append(">" + (trailingComment != null ? " #" + trailingComment : "") + "\n");
			for (String line : StringUtils.retranslateColorCodes(valueWithLineBreaks)) {
				writer.append(prefix + "  " + line + "\n");
			}
		}
	}

	@Override
	public void writeInCompact(Appendable writer, boolean compactParent) throws Throwable {
		String value = "";
		for (String v : valueWithLineBreaks) {
			value += v + " ";
		}
		value = value.trim();
		writer.append(YMLReader.wrapIdToWrite(getId()) + ": " + YMLReader.wrapValueToWrite(StringUtils.retranslateColorCodes(value)));
	}

	// ----- clone
	@Override
	public SingleValueNode clone(SectionNode parent) {
		return new SingleValueNode(parent, getId(), valueWithLineBreaks, trailingComment);
	}

}
