package com.guillaumevdn.gcore.lib.configuration.reader.type;

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
			List<String> list = readContentFromStringWithBrackets(context.getRemaining());
			context.getParent().setListValue(context.getId(), list, true, false, context.getTrailingComment());
			return true;
		}
		return false;
	}

	public static List<String> readContentFromStringWithBrackets(String contentWithBrackets) {
		String content = contentWithBrackets.substring(1, contentWithBrackets.length() - 1).trim();
		return readContentFromString(content);
	}

	public static List<String> readContentFromString(String content) {
		List<String> list = new ArrayList<>(1);  // I'd rather take a little more time when loading than having many collections with a 10% fill ratio
		content = content.replace("\"\"", "@@@").replace("''", "@@&");
		// read content
		if (!content.isEmpty()) {
			Character wrapping = null;
			int wrappingCount = 0;
			int lastStart = 0;
			for (int i = 0; i < content.length(); ++i) {
				char ch = content.charAt(i);
				if (wrapping != null && ch == wrapping) {
					// escaped wrapping
					if (i + 1 < content.length() && content.charAt(i + 1) == wrapping) {
						++i;
						continue;
					}
					// ended wrapping
					--wrappingCount;
				} else if (ch == ',') {
					if (wrappingCount == 0) {
						// decode old value
						list.add(YMLReader.unwrapValue(content.substring(lastStart, i), false).replace("@@@", "\"").replace("@@&", "'"));
						wrappingCount = 0;
						lastStart = ++i;
						// wrapping start
						if (lastStart <= content.length() && (content.charAt(lastStart) == '\'' || content.charAt(lastStart) == '"')) {
							wrapping = content.charAt(lastStart);
							wrappingCount = 1;
						} else {
							wrapping = null;
						}
					}
				}
			}
			list.add(YMLReader.unwrapValue(content.substring(lastStart), false).replace("@@@", "\"").replace("@@&", "'"));
		}
		return list;
	}

}
