package com.guillaumevdn.gcore.lib.configuration.reader.type;

import com.guillaumevdn.gcore.lib.configuration.reader.ReaderContext;
import com.guillaumevdn.gcore.lib.configuration.reader.ReaderLine;
import com.guillaumevdn.gcore.lib.function.ThrowableFunction;

/*
 * @author GuillaumeVDN
 */
public class ReaderLineBreak implements ThrowableFunction<ReaderContext, Boolean> {

	@Override
	public Boolean apply(ReaderContext context) throws Throwable {
		int count = 0;
		for (ReaderLine line; (line = context.peekLine()) != null && line.getLine().trim().isEmpty(); ) {
			context.getLines().remove(0);
			++count;
		}
		if (count > 0) {
			context.getParent().addLineBreaks(count);
			return true;
		}
		return false;
	}

}
