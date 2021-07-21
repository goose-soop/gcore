package com.guillaumevdn.gcore.lib.configuration.reader;

import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.reader.type.ReaderComment;
import com.guillaumevdn.gcore.lib.configuration.reader.type.ReaderLineBreak;
import com.guillaumevdn.gcore.lib.configuration.reader.type.ReaderList;
import com.guillaumevdn.gcore.lib.configuration.reader.type.ReaderListCompact;
import com.guillaumevdn.gcore.lib.configuration.reader.type.ReaderListEz;
import com.guillaumevdn.gcore.lib.configuration.reader.type.ReaderSection;
import com.guillaumevdn.gcore.lib.configuration.reader.type.ReaderSectionCompact;
import com.guillaumevdn.gcore.lib.configuration.reader.type.ReaderSectionEmpty;
import com.guillaumevdn.gcore.lib.configuration.reader.type.ReaderCompactNestedMap;
import com.guillaumevdn.gcore.lib.configuration.reader.type.ReaderValue;
import com.guillaumevdn.gcore.lib.configuration.reader.type.ReaderValueDeveloped;
import com.guillaumevdn.gcore.lib.configuration.reader.type.ReaderValueDevelopedWeak;
import com.guillaumevdn.gcore.lib.function.ThrowableFunction;

/*
 * @author GuillaumeVDN
 */
public enum TokenType {

	LINE_BREAK(new ReaderLineBreak()),
	COMMENT(new ReaderComment()),

	LIST_COMPACT(new ReaderListCompact()),
	LIST_EZ(new ReaderListEz()),
	LIST(new ReaderList()),

	VALUE_DEVELOPED(new ReaderValueDeveloped()),
	VALUE_DEVELOPED_WEAK(new ReaderValueDevelopedWeak()),
	VALUE(new ReaderValue()),

	SECTION(new ReaderSection()),
	SECTION_NESTED_MAP(new ReaderCompactNestedMap()),
	SECTION_EMPTY(new ReaderSectionEmpty()),
	SECTION_COMPACT(new ReaderSectionCompact()),
	;

	public static final List<TokenType> NOT_IDENTIFIABLE = CollectionUtils.asList(LINE_BREAK, COMMENT);
	public static final List<TokenType> IDENTIFIABLE = CollectionUtils.asList(
			SECTION_EMPTY,
			SECTION_COMPACT,
			SECTION_NESTED_MAP,

			LIST_COMPACT,
			LIST_EZ,
			LIST,

			VALUE_DEVELOPED,
			VALUE_DEVELOPED_WEAK,
			VALUE,

			SECTION
			);

	private ThrowableFunction<ReaderContext, Boolean> reader;  // return true to start checking the first token type again

	TokenType(ThrowableFunction<ReaderContext, Boolean> reader) {
		this.reader = reader;
	}

	// ----- do
	public boolean read(ReaderContext context) throws Throwable {
		context.setTokenType(this);
		return reader.apply(context);
	}

}
