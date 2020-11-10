package com.guillaumevdn.gcore.lib.configuration.reader.token;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.configuration.reader.ReaderContext;
import com.guillaumevdn.gcore.lib.configuration.reader.ReaderLine;
import com.guillaumevdn.gcore.lib.function.ThrowableFunction;

/*
 * @author GuillaumeVDN
 */
public class ReaderComment implements ThrowableFunction<ReaderContext, Boolean> {

	@Override
	public Boolean apply(ReaderContext context) throws Throwable {
		List<String> comment = new ArrayList<>();
		for (ReaderLine line; (line = context.peekLine()) != null && line.getLine().trim().startsWith("#"); ) {
			comment.add(line.getLine().trim().substring(1));
			context.getLines().remove(0);
		}
		if (!comment.isEmpty()) {
			context.getParent().addComment(comment);
			return true;
		}
		return false;
	}

}
