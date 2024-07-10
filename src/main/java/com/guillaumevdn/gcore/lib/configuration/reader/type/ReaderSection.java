package com.guillaumevdn.gcore.lib.configuration.reader.type;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.configuration.file.Node;
import com.guillaumevdn.gcore.lib.configuration.file.node.CommentNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.LineBreaksNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode.SectionNodeType;
import com.guillaumevdn.gcore.lib.configuration.reader.ReaderContext;
import com.guillaumevdn.gcore.lib.configuration.reader.ReaderLine;
import com.guillaumevdn.gcore.lib.configuration.reader.YMLReader;
import com.guillaumevdn.gcore.lib.function.ThrowableFunction;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/*
 * @author GuillaumeVDN
 */
public class ReaderSection implements ThrowableFunction<ReaderContext, Boolean> {

	@Override
	public Boolean apply(ReaderContext context) throws Throwable {
		if (context.getIdentifiableLine() != null) {
			// init sub section so we can log things with it
			SectionNode sub = new SectionNode(context.getParent(), context.getId(), SectionNodeType.REGULAR, context.getTrailingComment());

			// read section lines
			List<ReaderLine> subsectionLines = new ArrayList<>();
			ReaderLine firstNonEmptySectionLine = null;
			String sectionIndent = context.getCurrentIndent() + context.getIndentLevel();
			for (ReaderLine peek; (peek = context.peekLine()) != null;) {
				// remove comment from line to process id
				String maybeEmptyPeek = peek.getLine().trim();
				int c = YMLReader.indexOfComment(maybeEmptyPeek);
				if (c != -1) {
					maybeEmptyPeek = maybeEmptyPeek.substring(0, c).trim();
				}
				// ignore empty/comment lines (we allow spacing between sections because it's clearier to read)
				if (maybeEmptyPeek.isEmpty()) {
					context.getLines().remove(0);
					if (c != -1) { // ... still add comment though
						subsectionLines.add(peek);
					}
					continue;
				}
				// add line if regular
				else if (peek.getLine().startsWith(sectionIndent)) {
					context.getLines().remove(0);
					subsectionLines.add(peek);
					if (firstNonEmptySectionLine == null) {
						firstNonEmptySectionLine = peek;
					}
				}
				// not in section
				else {
					break;
				}
			}

			// no lines
			if (firstNonEmptySectionLine == null) {
				// maybe this is just a fucked up value such as "element:" and nothing behind, skip it
				context.getFile().getConfiguration().getPlugin().getMainLogger()
						.warning("Found empty value for key '" + context.getId() + "' at line " + context.getIdentifiableLine().getNumber() + " in file "
								+ context.getFile().getConfiguration().getLogFilePath() + ", it's most likely a configuration mistake");
				return true;
			}

			// maybe fix extra indentation (:grr:)
			int extra = StringUtils.countLeadingChar(firstNonEmptySectionLine.getLine().substring(sectionIndent.length()), ' ');
			if (extra != 0) {
				if (extra % context.getIndentLevel().length() != 0) {
					context.throwIndentError(sub, firstNonEmptySectionLine, null, false);
				}
				sectionIndent += StringUtils.repeatString(" ", extra);
				for (ReaderLine subsectionLine : subsectionLines) {
					if (!subsectionLine.getLine().startsWith(sectionIndent)) {
						context.throwIndentError(sub, subsectionLine, sectionIndent.length(), false);
					}
				}
			}

			// add and read section
			context.getParent().setConfigNode(sub);
			YMLReader.readSection(context.getFile(), sub, subsectionLines, sectionIndent, context.getIndentLevel());

			// if last element is a comment/line break, actually add it to the parent because we might have read it wrongly
			while (!sub.getNodes().isEmpty()) {
				Node last = sub.getNodes().get(sub.getNodes().size() - 1);
				if (last instanceof CommentNode || last instanceof LineBreaksNode) {
					sub.removeNode(sub.getNodes().size() - 1);
					sub.getParent().addNode(last.clone(sub.getParent()));
				} else {
					break;
				}
			}

			// done
			return true;
		}
		return false;
	}

}
