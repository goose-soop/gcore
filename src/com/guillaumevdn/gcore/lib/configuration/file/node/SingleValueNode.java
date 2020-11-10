package com.guillaumevdn.gcore.lib.configuration.file.node;

import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
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

	// get
	public String getValue() {
		return StringUtils.toTextString(" ", valueWithLineBreaks);
	}

	public List<String> getValueWithLineBreaks() {
		return valueWithLineBreaks;
	}

	public String getTrailingComment() {
		return trailingComment;
	}

	// set
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

	// print
	public void print() {
		String prefix = getPrefix();
		if (valueWithLineBreaks.size() == 1) {
			System.out.println(getDepthLevel() + " " + prefix + getId() + ": " + wrapValueToWrite(StringUtils.retranslateColorCodes(valueWithLineBreaks.get(0))) + (trailingComment != null ? " #" + trailingComment : ""));
		} else {
			System.out.println(getDepthLevel() + " " + prefix + getId() + ": >" + (trailingComment != null ? " #" + trailingComment : ""));
			for (String line : StringUtils.retranslateColorCodes(valueWithLineBreaks)) {
				System.out.println(getDepthLevel() + " " + prefix + "  " + line);
			}
		}
	}

	// write
	@Override
	public void write(Appendable writer) throws Throwable {
		String prefix = getPrefix();
		if (valueWithLineBreaks.size() == 1) {
			writer.append(prefix + getId() + ": " + wrapValueToWrite(StringUtils.retranslateColorCodes(valueWithLineBreaks.get(0))) + (trailingComment != null ? " #" + trailingComment : "") + "\n");
		} else {
			writer.append(prefix + getId() + ": >" + (trailingComment != null ? " #" + trailingComment : "") + "\n");
			for (String line : StringUtils.retranslateColorCodes(valueWithLineBreaks)) {
				writer.append(prefix + "  " + line + "\n");
			}
		}
	}

	// utils
	public static String wrapValueToWrite(String value) {
		// empty
		if (value == null || value.isEmpty()) {
			return "''";
		}
		// primitive
		Double dbl = NumberUtils.doubleOrNull(value);
		if (dbl != null) {
			return StringUtils.getDoubleFormat(3).format(dbl);
		}
		if (NumberUtils.doubleOrNull(value) != null || NumberUtils.integerOrNull(value) != null || NumberUtils.longOrNull(value) != null || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
			return value;
		}
		// contains :
		if (value.contains(":")) {
			return "'" + value.replace("'", "''") + "'";
		}
		// starting or ending with a non-alphanumeric character
		if (!StringUtils.isAlphanumeric(value.charAt(0)) || (value.length() != 1 && !StringUtils.isAlphanumeric(value.charAt(value.length() - 1)))) {
			return "'" + value.replace("'", "''") + "'";
		}
		// don't wrap
		return value;
	}

	// clone
	@Override
	public SingleValueNode clone(SectionNode parent) {
		return new SingleValueNode(parent, getId(), valueWithLineBreaks, trailingComment);
	}

}
