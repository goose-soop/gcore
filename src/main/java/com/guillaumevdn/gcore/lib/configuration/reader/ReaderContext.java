package com.guillaumevdn.gcore.lib.configuration.reader;

import java.util.List;

import com.guillaumevdn.gcore.lib.configuration.file.YMLError;
import com.guillaumevdn.gcore.lib.configuration.file.YMLFile;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.SuperNode;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/*
 * @author GuillaumeVDN
 */
public class ReaderContext {

	private YMLFile file;
	private SectionNode parent;
	private TokenType tokenType = null;
	private ReaderLine identifiableLine = null;
	private String id = null;
	private String remaining = null;
	private String trailingComment = null;
	private final List<ReaderLine> lines;
	private String currentIndent;
	private String indentLevel;

	public ReaderContext(YMLFile file, SectionNode parent, List<ReaderLine> lines, String currentIndent, String indentLevel) {
		this.file = file;
		this.parent = parent;
		this.lines = lines;
		this.currentIndent = currentIndent;
		this.indentLevel = indentLevel;
	}

	// ----- get
	public YMLFile getFile() {
		return file;
	}

	public SectionNode getParent() {
		return parent;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public ReaderLine getIdentifiableLine() {
		return identifiableLine;
	}

	public String getId() {
		return id;
	}

	public String getPath() {
		return parent instanceof SuperNode ? id : parent.getPath() + "." + id;
	}

	public String getRemaining() {
		return remaining;
	}

	public boolean isRemainingWrappedWith(char begin, char end) {
		return remaining != null && remaining.length() >= 2 && remaining.charAt(0) == begin && remaining.charAt(remaining.length() - 1) == end;
	}

	public boolean isRemainingWrappedWith(String begin, String end) {
		return remaining != null && remaining.length() >= begin.length() + end.length() && remaining.startsWith(begin) && remaining.endsWith(end);
	}

	public String getTrailingComment() {
		return trailingComment;
	}

	public List<ReaderLine> getLines() {
		return lines;
	}

	public String getCurrentIndent() {
		return currentIndent;
	}

	public String getIndentLevel() {
		return indentLevel;
	}

	// ----- set
	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	// ----- do
	public ReaderLine peekLine() {
		return lines.isEmpty() ? null : lines.get(0);
	}

	public ReaderLine peekNonEmptyNonCommentLine() {
		for (int i = 0; i < lines.size(); ++i) {
			ReaderLine line = lines.get(i);
			String l = line.getLine();
			int c = YMLReader.indexOfComment(l);
			if (c != -1) {
				l = l.substring(0, c);
			}
			if (!l.trim().isEmpty()) {
				return line;
			}
		}
		return null;
	}

	public ReaderLine peekComplexLineThatStartsWith(String startsWith) {  // a complex line is something that can be interpreted as something more than a scalar value (see cases below)
		for (int i = 0; i < lines.size(); ++i) {
			ReaderLine line = lines.get(i);
			String l = line.getLine();
			int c = YMLReader.indexOfComment(l);
			if (c != -1) {
				l = l.substring(0, c);
			}
			if (l.trim().isEmpty()) {
				continue;  // skip comments
			}
			if (!l.startsWith(startsWith)) {
				return null;
			}
			l = l.substring(startsWith.length()).trim();
			if (YMLReader.indexOfColonSeparator(l) != -1 || l.startsWith("[") || l.startsWith("- ") || l.equals("{}") || l.equals(">") || l.equals("|") || l.equals("%")) {
				return line;
			}
		}
		return null;
	}

	public void consumeIdentifiable() {
		identifiableLine = lines.remove(0);
		// invalid indent
		if (StringUtils.countLeadingChar(identifiableLine.getLine(), ' ') != currentIndent.length()) {
			throwIndentError(identifiableLine, currentIndent.length(), false);
		}
		// detect id
		int index = YMLReader.indexOfColonSeparator(identifiableLine.getLine());
		if (index == -1) {
			throwError("expected : at line " + identifiableLine.getNumber());
		}
		// read id and remaining
		id = YMLReader.unwrapValue(identifiableLine.getLine().substring(currentIndent.length(), index), true);
		if (id == null) {
			throwError("expected id before : at line " + identifiableLine.getNumber());
		}
		remaining = identifiableLine.getLine().substring(index + 1).trim();
		// detect invalid wrapping and read trailing comment
		trailingComment = null;
		if (!remaining.isEmpty()) {
			// wrapped
			Character wrapping = remaining.charAt(0);
			if (wrapping == '\'' || wrapping == '"') {
				// read trailing comment
				int commentIndex = YMLReader.indexOfComment(remaining);
				if (commentIndex != -1) {
					trailingComment = remaining.substring(commentIndex + 1);
					remaining = remaining.substring(0, commentIndex).trim();
				}
				// detect invalid wrapping
				if (StringUtils.countChar(remaining.replace("''", ""), '\'') > 2) {
					throwError("invalid wrapping at line " + identifiableLine.getNumber());
				}
			}
			// not wrapped
			else {
				// read trailing comment
				int commentIndex = YMLReader.indexOfComment(remaining);
				if (commentIndex != -1) {
					trailingComment = remaining.substring(commentIndex + 1);
					remaining = remaining.substring(0, commentIndex).trim();
				}
			}
		}
	}

	public void resetIdentifiable() {
		identifiableLine = null;
		id = null;
		remaining = null;
		trailingComment = null;
	}

	public void throwError(String error) {
		throwError(parent, error);
	}

	public void throwIndentError(ReaderLine line, Integer expectedSpaces, boolean atLeast) {
		throwIndentError(parent, line, expectedSpaces, atLeast);
	}

	public void throwIndentError(SectionNode section, ReaderLine line, Integer expectedSpaces, boolean atLeast) {
		throwError(parent, "unexpected indent at line " + line.getNumber() + (expectedSpaces == null ? "" : ", expected " + (atLeast ? "at least " : "") + StringUtils.pluralizeAmountDesc("space", expectedSpaces) + " but found " + StringUtils.countLeadingChar(line.getLine(), ' ')));
	}

	public void throwError(SectionNode section, String error) {
		throw new YMLError(buildLogHeader(section) + error + " (while reading " + tokenType + ")");
	}

	private String buildLogHeader(SectionNode section) {
		return getFile().getConfiguration().buildFormatErrorHeader() + "in " + (section instanceof SuperNode ? "root section" : "section " + section.getPath()) + ", ";
	}

}
