package com.guillaumevdn.gcore.lib.configuration.reader;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.configuration.file.YMLFile;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.SuperNode;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

public class YMLReader {

	public static SuperNode readFile(YMLFile file) throws Throwable {
		try {
			SuperNode base = new SuperNode(file);
			if (file.getConfiguration().getFile().exists()) {
				// read lines and replace tabs
				List<String> lines = FileUtils.readLines(file.getConfiguration().getFile());
				for (int i = 0; i < lines.size(); ++i) {
					String line = lines.get(i);
					int tabCount = StringUtils.countLeadingChar(line, '\t');
					if (tabCount != 0) {
						lines.set(i, StringUtils.repeatString("  ", tabCount) + line.substring(tabCount));
					}
				}
				// find indent level
				int indentLevel = 0;
				for (String line : lines) {
					int index = YMLReader.indexOfComment(line);
					if (index != -1) {
						line = line.substring(0, index);
					}
					if (!line.trim().isEmpty()) {
						if ((indentLevel = StringUtils.countLeadingChar(line, ' ')) != 0) {
							break;
						}
					}
				}
				if (indentLevel == 0) { // there are no sections or maybe the file is empty
					indentLevel = 2;
				}
				// read
				List<ReaderLine> rootLines = new ArrayList<>();
				for (int i = 0; i < lines.size(); ++i) {
					rootLines.add(new ReaderLine(i + 1, lines.get(i)));
				}
				readSection(file, base, rootLines, "", StringUtils.repeatString(" ", indentLevel));
			}
			return base;
		} catch (Throwable exception) {
			exception.printStackTrace();
			throw exception;
		}
	}

	public static void readSection(YMLFile file, SectionNode parent, List<ReaderLine> lines, String currentIndent, String indentLevel) throws Throwable {
		ReaderContext context = new ReaderContext(file, parent, lines, currentIndent, indentLevel);
		for (;;) {
			// done
			if (context.getLines().isEmpty()) {
				return;
			}
			// read non-identifiable tokens
			context.resetIdentifiable();
			if (readTokens(context, TokenType.NOT_IDENTIFIABLE)) {
				continue;
			}
			// read identifiable tokens
			context.consumeIdentifiable();
			if (readTokens(context, TokenType.IDENTIFIABLE)) {
				continue;
			}
			// warn in console that this is an incorrect value (such as 'key: ')
			GCore.inst().getMainLogger().warning("Found standalone key at path " + parent.getPath() + " in file " + file.getConfiguration().getLogFilePath());
		}
	}

	private static boolean readTokens(ReaderContext context, List<TokenType> tokens) throws Throwable {
		for (TokenType token : tokens) {
			if (token.read(context)) {
				return true;
			}
		}
		return false;
	}

	public static String unwrapValue(String string, boolean nullIfEmpty) {
		// empty
		string = string.trim();
		if (string.isEmpty()) {
			return nullIfEmpty ? null : string;
		}
		// unwrap
		char wrapping = string.charAt(0);
		if (wrapping == '\'' || wrapping == '"') {
			int commentIndex = YMLReader.indexOfComment(string);
			if (commentIndex != -1) {
				string = string.substring(0, commentIndex).trim();
			}
			if (string.charAt(string.length() - 1) == wrapping) {
				String wrappingStr = String.valueOf(wrapping);
				string = string.substring(1, string.length() - 1).replace(wrappingStr + wrappingStr, wrappingStr); // if value is wrapped, don't trim
			}
		}
		// equals to null ? consider it as a mistake ; if they really want the 'null' string, they can wrap it with &r
		if (string.trim().equals("null")) {
			return null;
		}
		// done
		return string;
	}

	public static int indexOfComment(String string) {
		Character wrapping = null;
		int wrappingCount = 0;
		char[] chars = string.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
			char ch = chars[i];
			if (ch == '\'' || ch == '"') {
				if (wrapping == null || wrapping == ch) {
					wrapping = ch;
					++wrappingCount;
				} else {
					// a wrapping was already defined and we found the other in string, ignore it
				}
			} else if (ch == '#') {
				if (wrappingCount % 2 == 0) {
					return i;
				}
			}
		}
		return -1;
	}

}
