package com.guillaumevdn.gcore.lib.configuration.reader.token;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.configuration.reader.ReaderContext;
import com.guillaumevdn.gcore.lib.configuration.reader.YMLReader;
import com.guillaumevdn.gcore.lib.function.ThrowableFunction;

/*
 * @author GuillaumeVDN
 */
public class ReaderListCompact implements ThrowableFunction<ReaderContext, Boolean> {

	@Override
	public Boolean apply(ReaderContext context) throws Throwable {
		if (context.getIdentifiableLine() != null && context.isRemainingWrappedWith('[', ']')) {
			List<String> list = new ArrayList<>();
			String content = context.getRemaining().substring(1, context.getRemaining().length() - 1).trim().replace("\"\"", "@@@").replace("''", "@@&");
			// read content
			if (!content.isEmpty()) {
				Character wrapping = null;
				int wrappingCount = 0;
				int lastStart = 0;
				for (int i = 0; i < content.length(); ++i) {
					char ch = content.charAt(i);
					if (wrapping != null && ch == wrapping) {
						--wrappingCount;
					} else if (ch == ',') {
						if (wrappingCount == 0) {
							list.add(YMLReader.unwrapValue(content.substring(lastStart, i), false).replace("@@@", "\"").replace("@@&", "'"));
						}
						wrappingCount = 0;
						lastStart = ++i;
						if (lastStart <= content.length() && (content.charAt(lastStart) == '\'' || content.charAt(lastStart) == '"')) {
							wrapping = content.charAt(lastStart);
							wrappingCount = 1;
						} else {
							wrapping = null;
						}
					}
				}
				list.add(YMLReader.unwrapValue(content.substring(lastStart), false).replace("@@@", "\"").replace("@@&", "'"));
			}
			// add list
			context.getParent().setListValue(context.getId(), list, true, false, context.getTrailingComment());
			return true;
		}
		return false;
	}

}
