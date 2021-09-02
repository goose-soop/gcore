package com.guillaumevdn.gcore.lib.configuration.reader.type;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.configuration.reader.ReaderContext;
import com.guillaumevdn.gcore.lib.configuration.reader.ReaderLine;
import com.guillaumevdn.gcore.lib.function.ThrowableFunction;

/*
 * @author GuillaumeVDN
 */
public class ReaderListEz implements ThrowableFunction<ReaderContext, Boolean> {

	@Override
	public Boolean apply(ReaderContext context) throws Throwable {
		if (context.getIdentifiableLine() != null && (context.getRemaining().equals("|") || context.getRemaining().equals("|-"))) {
			// no value
			ReaderLine peek = context.peekLine();
			if (peek == null) {
				context.throwError("expected value at line " + (context.getIdentifiableLine().getNumber() + 1));
			}
			// find indent
			String listIndent = context.getCurrentIndent() + context.getIndentLevel();
			if (!peek.getLine().startsWith(listIndent)) {
				context.throwIndentError(peek, listIndent.length(), true);
			}
			String s = peek.getLine().substring(listIndent.length());
			while (s.startsWith(context.getIndentLevel())) {
				listIndent += context.getIndentLevel();
				s = s.substring(context.getIndentLevel().length());
			}
			/*  actually ignore extra indentation for this, lines are trimmed anyway
			if (!s.isEmpty() && s.charAt(0) == ' ') {
				context.throwIndentError(peek, listIndent.length(), false);
			}*/
			// read lines
			List<String> list = new ArrayList<>(1);  // I'd rather take a little more time when loading than having many collections with a 10% fill ratio
			while ((peek = context.peekLine()) != null) {
				// element
				if (peek.getLine().startsWith(listIndent)) {
					context.getLines().remove(0);
					list.add(peek.getLine().substring(listIndent.length()).trim());
				}
				// not an element
				else {
					break;
				}
			}
			// add list
			context.getParent().setListValue(context.getId(), list, false, true, context.getTrailingComment());
			return true;
		}
		return false;
	}

}
