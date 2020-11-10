package com.guillaumevdn.gcore.lib.configuration.reader;

import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.reader.token.ReaderComment;
import com.guillaumevdn.gcore.lib.configuration.reader.token.ReaderLineBreak;
import com.guillaumevdn.gcore.lib.configuration.reader.token.ReaderList;
import com.guillaumevdn.gcore.lib.configuration.reader.token.ReaderListEz;
import com.guillaumevdn.gcore.lib.configuration.reader.token.ReaderListCompact;
import com.guillaumevdn.gcore.lib.configuration.reader.token.ReaderSection;
import com.guillaumevdn.gcore.lib.configuration.reader.token.ReaderSectionEmpty;
import com.guillaumevdn.gcore.lib.configuration.reader.token.ReaderValue;
import com.guillaumevdn.gcore.lib.configuration.reader.token.ReaderValueDeveloped;
import com.guillaumevdn.gcore.lib.configuration.reader.token.ReaderValueDevelopedWeak;
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
	SECTION_EMPTY(new ReaderSectionEmpty()),
	;

	public static final List<TokenType> NOT_IDENTIFIABLE = CollectionUtils.asList(LINE_BREAK, COMMENT);
	public static final List<TokenType> IDENTIFIABLE = CollectionUtils.asList(SECTION_EMPTY, LIST_COMPACT, LIST_EZ, LIST, VALUE_DEVELOPED, VALUE_DEVELOPED_WEAK, VALUE, SECTION);

	private ThrowableFunction<ReaderContext, Boolean> reader; // return true to start checking the first token type again

	TokenType(ThrowableFunction<ReaderContext, Boolean> reader) {
		this.reader = reader;
	}

	// do
	public boolean read(ReaderContext context) throws Throwable {
		context.setTokenType(this);
		return reader.apply(context);
	}

}
