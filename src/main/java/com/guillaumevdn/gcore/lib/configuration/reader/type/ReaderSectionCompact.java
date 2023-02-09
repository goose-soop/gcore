package com.guillaumevdn.gcore.lib.configuration.reader.type;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode.SectionNodeType;
import com.guillaumevdn.gcore.lib.configuration.reader.ReaderContext;
import com.guillaumevdn.gcore.lib.configuration.reader.YMLReader;
import com.guillaumevdn.gcore.lib.function.ThrowableFunction;

/*
 * @author GuillaumeVDN
 */
public class ReaderSectionCompact implements ThrowableFunction<ReaderContext, Boolean> {

	@Override
	public Boolean apply(ReaderContext context) throws Throwable {
		if (context.getIdentifiableLine() != null && context.isRemainingWrappedWith("{ ", " }") /* spaces are important here, to mark a clear difference with placeholders */) {
			SectionNode section = context.getParent().addSection(context.getId(), SectionNodeType.COMPACT);
			readCompactSection(section, context.getRemaining().substring(1, context.getRemaining().length() - 1).trim(), context);
			return true;
		}
		return false;
	}

	private void readCompactSection(SectionNode section, String remaining, ReaderContext context) {

		// not enough content
		remaining = remaining.trim();
		if (remaining.length() < 4) {  // minimum acceptable value should be something like 'a:[]' or 'a:{}'
			context.throwError(section, "not enough content at line " + context.getIdentifiableLine().getNumber() + " near \"" + remaining + "\"");
		}

		// split contents
		List<String> contents = new ArrayList<>();
		int commaIndex = -1;

		while ((commaIndex = indexOfCommaSeparator(remaining)) != -1) {
			if (commaIndex == -2) {
				context.throwError(section, "found invalid comma or subsection or list at line " + context.getIdentifiableLine().getNumber() + " near \"" + remaining + "\"");
			}
			contents.add(remaining.substring(0, commaIndex).trim());
			remaining = remaining.substring(commaIndex + 1).trim();
		}
		contents.add(remaining);

		// read contents
		for (String content : contents) {

			// detect id
			int index = YMLReader.indexOfColonSeparator(content);
			if (index == -1) {
				context.throwError("expected : at line " + context.getIdentifiableLine().getNumber());
			}

			// read id and content
			String id = YMLReader.unwrapValue(content.substring(0, index), true);
			if (id == null) {
				context.throwError("expected id before : at line " + context.getIdentifiableLine().getNumber() + " near \"" + content + "\"");
			}
			content = content.substring(index + 1).trim();

			// section
			if (content.startsWith("{ ") && content.endsWith(" }") /* spaces are important here, to mark a clear difference with placeholders */) {
				SectionNode subsection = section.addSection(id, SectionNodeType.COMPACT);
				readCompactSection(subsection, content.substring(1, content.length() - 1).trim(), context);
			}
			// list
			else if (content.charAt(0) == '[' && content.charAt(content.length() - 1) == ']') {
				List<String> list = ReaderListCompact.readContentFromStringWithBrackets(content);
				section.setListValue(id, list, true, false, null);
			}
			// value
			else {
				section.setSingleValue(id, YMLReader.unwrapValue(content, true), null);
			}
		}

	}

	private int indexOfCommaSeparator(String line) {
		if (line.isEmpty()) {
			return -1;
		}
		Character wrapping = null;
		int subsectionCount = 0;
		boolean inList = false;
		char[] chars = line.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
			char ch = chars[i];
			if (ch == '\'' || ch == '"') {
				if (wrapping == null) {
					wrapping = ch;
				} else if (wrapping == ch) {
					wrapping = null;
				} else {
					// a wrapping was already defined and we found the other type in string, ignore it
				}
			} else if (ch == '{') {
				if (wrapping == null) {
					if (inList) return -2;
					++subsectionCount;
				}
			} else if (ch == '}') {
				if (wrapping == null) {
					if (inList) return -2;
					if (subsectionCount == 0) return -2;
					--subsectionCount;
				}
			} else if (ch == '[') {
				if (wrapping == null) {
					if (inList) return -2;
					inList = true;
				}
			} else if (ch == ']') {
				if (wrapping == null) {
					if (!inList) return -2;
					inList = false;
				}
			} else if (ch == ',') {
				if (wrapping == null && subsectionCount == 0 && !inList) {
					return i;
				}
			}
		}
		// didn't find end of wrapping (invalid), or just no comma
		return wrapping != null ? -2 : -1;
	}

}
