package com.guillaumevdn.gcore.lib.configuration.reader.type;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.reader.ReaderContext;
import com.guillaumevdn.gcore.lib.configuration.reader.YMLReader;
import com.guillaumevdn.gcore.lib.function.ThrowableFunction;

/*
 * @author GuillaumeVDN
 */
public class ReaderValue implements ThrowableFunction<ReaderContext, Boolean> {

	@Override
	public Boolean apply(ReaderContext context) throws Throwable {
		if (context.getIdentifiableLine() != null && !context.getRemaining().isEmpty()) {
			String unwrapped = YMLReader.unwrapValue(context.getRemaining(), true);
			if (unwrapped != null) {
				context.getParent().setSingleValue(context.getId(), CollectionUtils.asList(unwrapped), context.getTrailingComment());
				return true;
			}
		}
		return false;
	}

}
