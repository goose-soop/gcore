package com.guillaumevdn.gcore.lib.configuration.reader.token;

import com.guillaumevdn.gcore.lib.configuration.reader.ReaderContext;
import com.guillaumevdn.gcore.lib.function.ThrowableFunction;

/*
 * @author GuillaumeVDN
 */
public class ReaderSectionEmpty implements ThrowableFunction<ReaderContext, Boolean> {

	@Override
	public Boolean apply(ReaderContext context) throws Throwable {
		if (context.getIdentifiableLine() != null && context.getRemaining().equals("{}")) {
			context.getParent().addSection(context.getId());
			return true;
		}
		return false;
	}

}
