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
public class ReaderCompactNestedMap implements ThrowableFunction<ReaderContext, Boolean> {

	@Override
	public Boolean apply(ReaderContext context) throws Throwable {
		if (context.getIdentifiableLine() != null) {
			// first line of the section is not a list, or contain no identifiable key
			ReaderLine first = context.peekNonEmptyNonCommentLine();
			if (first == null || !first.getLine().trim().startsWith("-")) {
				return false;
			}

			String mainSectionIndent = YMLReader.requireValidIndentLevelPotentiallyLenient(context, first);  // this throws an error if invalid indent ; it's the same as the list reader though so it would be thrown anyways at some point if invalid

			if (context.getRemaining().equals("%")) {  // forces the compact nested map
			} else if (first.getLine().startsWith(mainSectionIndent + "- ")) {  // automatically detect compact nested map if we find a complex line
				ReaderLine firstLineOfMap = context.peekComplexLineThatStartsWith(mainSectionIndent + "- ");
				if (firstLineOfMap == null) {
					return false;
				}
			}

			// init sub section so we can log things with it
			SectionNode main = new SectionNode(context.getParent(), context.getId(), SectionNodeType.COMPACT_NESTED_MAP, context.getTrailingComment());
			String subsectionIndent = mainSectionIndent + context.getIndentLevel();

			// rebuild lines to read them like a normal map (with IDs)
			List<ReaderLine> mainLines = new ArrayList<>();
			int currentElementId = 0;

			for (ReaderLine peek; (peek = context.peekLine()) != null; ) {
				// remove comment from line to process it
				String maybeEmptyPeek = peek.getLine().trim();
				int c = YMLReader.indexOfComment(maybeEmptyPeek);
				if (c != -1) {
					maybeEmptyPeek = maybeEmptyPeek.substring(0, c).trim();
				}

				// ignore empty/comment lines (we allow spacing between sections because it's clearier to read)
				if (maybeEmptyPeek.isEmpty()) {
					context.getLines().remove(0);
					if (c != -1) {  // ... still add comment though
						mainLines.add(peek);
					}
					continue;
				}
				// starting a new element
				else if (peek.getLine().startsWith(mainSectionIndent + "- ")) {
					context.getLines().remove(0);

					String currentIdAlphabetic = StringUtils.alphabeticCountFor(++currentElementId);
					String firstLine = peek.getLine().substring((mainSectionIndent + "- ").length());
					if (YMLReader.indexOfColonSeparator(firstLine) != -1 || firstLine.trim().startsWith("- ")) {  // more than likely a subsection, or a list (or something they need to wrap)
						mainLines.add(new ReaderLine(peek.getNumber(), mainSectionIndent + currentIdAlphabetic + ":"));
						mainLines.add(new ReaderLine(peek.getNumber(), subsectionIndent + firstLine));
					} else {
						mainLines.add(new ReaderLine(peek.getNumber(), mainSectionIndent + currentIdAlphabetic + ": " + firstLine));
					}
				}
				// apparently content of subsection, or list, or developed value, etc
				else if (peek.getLine().startsWith(subsectionIndent)) {
					context.getLines().remove(0);
					mainLines.add(peek);
				}
				// not in subsection
				else {
					break;
				}
			}

			// add and read main section with those new lines
			context.getParent().setConfigNode(main);
			YMLReader.readSection(context.getFile(), main, mainLines, mainSectionIndent, context.getIndentLevel());

			// if last element is a comment/line break, actually add it to the parent because we might have read it wrongly
			while (!main.getNodes().isEmpty()) {
				Node last = main.getNodes().get(main.getNodes().size() - 1);
				if (last instanceof CommentNode || last instanceof LineBreaksNode) {
					main.removeNode(main.getNodes().size() - 1);
					main.getParent().addNode(last.clone(main.getParent()));
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
