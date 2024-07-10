package com.guillaumevdn.gcore.lib.configuration.reader.type;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.configuration.reader.ReaderContext;
import com.guillaumevdn.gcore.lib.configuration.reader.ReaderLine;
import com.guillaumevdn.gcore.lib.function.ThrowableFunction;

/*
 * @author GuillaumeVDN
 */
public class ReaderValueDeveloped implements ThrowableFunction<ReaderContext, Boolean> {

	@Override
	public Boolean apply(ReaderContext context) throws Throwable {
		if (context.getIdentifiableLine() != null && (context.getRemaining().trim().equals(">") || context.getRemaining().trim().equals(">-"))) {
			// no value
			ReaderLine peek = context.peekLine();
			if (peek == null) {
				context.throwError("expected value at line " + (context.getIdentifiableLine().getNumber() + 1));
			}
			// find indent
			String valueIndent = context.getCurrentIndent() + context.getIndentLevel();
			if (!peek.getLine().startsWith(valueIndent)) {
				context.throwIndentError(peek, valueIndent.length(), true);
			}
			String s = peek.getLine().substring(valueIndent.length());
			while (s.startsWith(context.getIndentLevel())) {
				valueIndent += context.getIndentLevel();
				s = s.substring(context.getIndentLevel().length());
			}
			/*
			 * actually ignore extra indentation for this, lines are trimmed anyway if (!s.isEmpty() && s.charAt(0) == ' ') {
			 * context.throwIndentError(peek, valueIndent.length(), false); }
			 */
			// read lines
			List<String> value = new ArrayList<>();
			while ((peek = context.peekLine()) != null) {
				// element
				if (peek.getLine().startsWith(valueIndent)) {
					context.getLines().remove(0);
					value.add(peek.getLine().substring(valueIndent.length()).trim());
				}
				// not an element
				else {
					break;
				}
			}
			// add value
			context.getParent().setSingleValue(context.getId(), value, context.getTrailingComment());
			return true;
		}
		return false;
	}

}
