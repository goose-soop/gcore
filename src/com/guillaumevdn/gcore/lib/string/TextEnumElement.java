package com.guillaumevdn.gcore.lib.string;

import java.util.List;

import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public interface TextEnumElement extends Text {

	String getId();
	TextElement getText();

	// ----- override methods with text
	@Override
	default List<String> getCurrentLines() {
		return getText().getCurrentLines();
	}

	@Override
	default String getCurrentLine(int index, String def) {
		return getText().getCurrentLine(index, def);
	}

	@Override
	default TextType getType() {
		return getText().getType();
	}

	// ----- set
	@Override
	default void setLines(List<String> newLines) {
		getText().setLines(newLines);
	}

	// ----- parse
	@Override
	default String parseLine(Replacer replacer) {
		return getText().parseLine(replacer);
	}

	@Override
	default List<String> parseLines(Replacer replacer) {
		return getText().parseLines(replacer);
	}
	
}
