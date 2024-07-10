package com.guillaumevdn.gcore.lib.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class TextElement implements Text {

	private List<String> lines = new ArrayList<>();
	private TextType type = TextType.NORMAL;

	public TextElement() {
		this(new ArrayList<>());
	}

	public TextElement(String... lines) {
		this(CollectionUtils.asList(lines));
	}

	public TextElement(List<String> lines) {
		setLines(lines);
	}

	// ----- get
	@Override
	public List<String> getCurrentLines() {
		return lines;
	}

	@Override
	public String getCurrentLine(int index, String def) {
		return index >= 0 && index < lines.size() ? lines.get(index) : def;
	}

	@Override
	public TextType getType() {
		return type;
	}

	// ----- set
	@Override
	public void setLines(List<String> newLines) {
		lines = StringUtils.formatCopy(newLines);
		type = !lines.isEmpty() && lines.get(0).trim().equalsIgnoreCase("@random") ? TextType.RANDOM_LINE : TextType.NORMAL;
		if (type.equals(TextType.RANDOM_LINE) || (!lines.isEmpty() && lines.get(0).equalsIgnoreCase("@empty"))) {
			lines.remove(0);
		}
		lines = Collections.unmodifiableList(lines);
	}

	// ----- parse
	@Override
	public String parseLine(Replacer replacer) {
		if (lines.isEmpty()) {
			return "";
		} else {
			int lineIndex = type.equals(TextType.RANDOM_LINE) ? NumberUtils.random(0, lines.size() - 1) : 0;
			String line = lines.get(lineIndex);
			return replacer != null ? replacer.parse(line) : line;
		}
	}

	@Override
	public List<String> parseLines(Replacer replacer) {
		if (lines.isEmpty()) {
			return new ArrayList<>();
		} else if (type.equals(TextType.RANDOM_LINE)) {
			List<String> list = CollectionUtils.asList(CollectionUtils.random(lines));
			return replacer != null ? replacer.parse(list, false) : list;
		} else {
			return replacer != null ? replacer.parse(lines) : CollectionUtils.asList(lines);
		}
	}

	// ----- object
	@Override
	public String toString() {
		return StringUtils.toTextString("@@@", lines);
	}

	@Override
	public int hashCode() {
		return Objects.hash(lines);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		Text other = ObjectUtils.castOrNull(obj, Text.class);
		return other != null && CollectionUtils.contentEquals(lines, other.getCurrentLines());
	}

}
