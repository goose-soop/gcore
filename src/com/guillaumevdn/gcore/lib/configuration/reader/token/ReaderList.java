package com.guillaumevdn.gcore.lib.configuration.reader.token;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.reader.ReaderContext;
import com.guillaumevdn.gcore.lib.configuration.reader.ReaderLine;
import com.guillaumevdn.gcore.lib.configuration.reader.YMLReader;
import com.guillaumevdn.gcore.lib.function.ThrowableFunction;

/*
 * @author GuillaumeVDN
 */
public class ReaderList implements ThrowableFunction<ReaderContext, Boolean> {

	@Override
	public Boolean apply(ReaderContext context) throws Throwable {
		if (context.getIdentifiableLine() != null && context.getRemaining().isEmpty()) {
			ReaderLine peek = context.peekNonEmptyNonCommentLine();
			if (peek != null && peek.getLine().trim().startsWith("-")) {
				// find indent
				String listIndent = context.getCurrentIndent(); // don't add context.getIndentLevel() here, the list might be formatted without extra leading indent level
				if (!peek.getLine().startsWith(listIndent)) {
					context.throwIndentError(peek, listIndent.length(), true);
				}
				String s = peek.getLine().substring(listIndent.length());
				while (s.startsWith(context.getIndentLevel())) {
					listIndent += context.getIndentLevel();
					s = s.substring(context.getIndentLevel().length());
				}
				if (s.charAt(0) == ' ') {
					context.throwIndentError(peek, listIndent.length(), false);
				}
				// read list
				List<String> list = new ArrayList<>();
				while ((peek = context.peekLine()) != null) {
					// remove comment from line
					String maybeEmptyPeek = peek.getLine().trim();
					int c = YMLReader.indexOfComment(maybeEmptyPeek);
					if (c != -1) {
						maybeEmptyPeek = maybeEmptyPeek.substring(0, c).trim();
					}
					// ignore empty/comment lines
					if (maybeEmptyPeek.isEmpty()) {
						context.getLines().remove(0);
						if (c != -1) { // ... still add comment to section though
							context.getParent().addComment(CollectionUtils.asList(peek.getLine().substring(c + 1)));
						}
						continue;
					}
					// element start
					else if (peek.getLine().startsWith(listIndent + "-")) {
						context.getLines().remove(0);
						list.add(YMLReader.unwrapValue(peek.getLine().substring(listIndent.length() + 1), false));
					}
					// element continuation (kind of poorly formatted but frequent)
					else if (peek.getLine().startsWith(listIndent + " ")) {
						context.getLines().remove(0);
						if (list.isEmpty()) {
							list.add(YMLReader.unwrapValue(peek.getLine().substring(listIndent.length() + 1), false));
						} else {
							list.set(list.size() - 1, list.get(list.size() - 1) + " " + YMLReader.unwrapValue(peek.getLine().substring(listIndent.length() + 1), false));
						}
					}
					// not an element
					else {
						break;
					}
				}
				// add list
				context.getParent().setListValue(context.getId(), list, false, false, context.getTrailingComment());
				return true;
			}
		}
		return false;
	}

}
