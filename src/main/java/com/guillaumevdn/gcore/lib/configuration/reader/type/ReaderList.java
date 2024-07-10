package com.guillaumevdn.gcore.lib.configuration.reader.type;

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
				String listIndent = YMLReader.requireValidIndentLevelPotentiallyLenient(context, peek);

				// read list
				List<String> list = new ArrayList<>(1); // I'd rather take a little more time when loading than having many collections with a 10% fill ratio
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
					// element continuation (kind of poorly formatted but frequent ; it's also useful in some cases, such as book pages,
					// which can contain \n themselves)
					else if (peek.getLine().startsWith(listIndent + " ")) {
						context.getLines().remove(0);
						if (list.isEmpty()) {
							list.add(YMLReader.unwrapValue(peek.getLine().substring(listIndent.length() + 1), false));
						} else {
							list.set(list.size() - 1,
									list.get(list.size() - 1) + "\n" + YMLReader.unwrapValue(peek.getLine().substring(listIndent.length() + 1), false));
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
