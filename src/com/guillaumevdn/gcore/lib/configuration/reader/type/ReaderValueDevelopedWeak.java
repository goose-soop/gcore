package com.guillaumevdn.gcore.lib.configuration.reader.type;

import java.util.List;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.reader.ReaderContext;
import com.guillaumevdn.gcore.lib.configuration.reader.ReaderLine;
import com.guillaumevdn.gcore.lib.function.ThrowableFunction;

/*
 * @author GuillaumeVDN
 */
public class ReaderValueDevelopedWeak implements ThrowableFunction<ReaderContext, Boolean> {

	@Override
	public Boolean apply(ReaderContext context) throws Throwable {
		if (context.getIdentifiableLine() != null && !context.getRemaining().isEmpty()) {
			// find wrapping eventually
			Character wrapping = context.getRemaining().charAt(0);
			if (wrapping != '\'' && wrapping != '"') {
				wrapping = null;
			}
			// correctly wrapped value, skip
			if (wrapping != null && context.getRemaining().charAt(context.getRemaining().length() - 1) == wrapping) {
				return false;
			}
			// no peek
			ReaderLine peek = context.peekLine();
			if (peek == null) {
				if (wrapping != null) { // if started with wrapping, we must have a weak value
					context.throwError("expected continuation of value at line " + (context.getIdentifiableLine().getNumber() + 1));
				} else { // if didn't start with wrapping, maybe it's not a weak value then
					return false;
				}
			}
			// find indent
			String valueIndent = context.getCurrentIndent() + context.getIndentLevel();
			if (!peek.getLine().startsWith(valueIndent)) {
				if (wrapping != null) { // if started with wrapping, we must have a weak value
					context.throwIndentError(peek, valueIndent.length(), true);
				} else { // if didn't start with wrapping, maybe it's not a weak value then
					return false;
				}
			}
			String s = peek.getLine().substring(valueIndent.length());
			while (s.startsWith(context.getIndentLevel())) {
				valueIndent += context.getIndentLevel();
				s = s.substring(context.getIndentLevel().length());
			}
			if (!s.isEmpty() && s.charAt(0) == ' ') {
				if (wrapping != null) { // if started with wrapping, we must have a weak value
					/*  actually ignore extra indentation for this, lines are trimmed anyway
					context.throwIndentError(peek, valueIndent.length(), false);
					*/
				} else { // if didn't start with wrapping, maybe it's not a weak value then
					return false;
				}
			}
			// read lines
			String wrappingStr = String.valueOf(wrapping);
			List<String> value = CollectionUtils.asList(wrapping == null ? context.getRemaining() : context.getRemaining().substring(1));
			while ((peek = context.peekLine()) != null) {
				// value
				if (peek.getLine().startsWith(valueIndent)) {
					context.getLines().remove(0);
					value.add(peek.getLine().substring(valueIndent.length()).replace(wrappingStr + wrappingStr, wrappingStr).trim());
				}
				// not an element
				else {
					break;
				}
			}
			// last value doesn't end with a wrapping !
			if (wrapping != null) {
				List<String> notEmpty = value.stream().filter(line -> !line.isEmpty()).collect(Collectors.toList());
				String last = notEmpty.isEmpty() /* LMAO GOT IT ??? */ ? null : notEmpty.get(notEmpty.size() - 1);
				if (last == null || last.charAt(last.length() - 1) != wrapping) {
					context.throwError("didn't find closing " + wrapping + " for weak-developed value at line " + (context.getIdentifiableLine().getNumber() + value.size()));
				}
			}
			// seems good :monkaGIGA:
			context.getParent().setSingleValue(context.getId(), value, context.getTrailingComment());
			return true;
		}
		return false;
	}

}
