package com.guillaumevdn.gcore.lib.element.struct.parsing;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.guillaumevdn.gcore.lib.element.struct.IElement;
import com.guillaumevdn.gcore.lib.element.struct.SuperElement;
import com.guillaumevdn.gcore.lib.object.Optional;
import com.guillaumevdn.gcore.lib.object.OptionalIfPresentFail;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public interface ParseableElement<T> extends IElement {

	String getTypeName();
	String getConfigurationPath();
	SuperElement getSuperElement();
	ParsedCache<T> getCache();  // if not null, will allow the thing to be cached whatever the replacer is (it means there's no placeholders)

	default Optional<T> parseGeneric() {
		return parse(Replacer.GENERIC);
	}

	default Optional<T> parse(@Nonnull Replacer replacer) {  // used most of the time
		try {
			return parseNoCatch(replacer);
		} catch (Throwable error) {
			ParsingError.print(error, this);
			return Optional.empty();
		}
	}

	default Optional<T> parseNoCatch(@Nonnull Replacer replacer) throws ParsingError {  // useful when parsing some containers
		ParsedCache<T> cache = getCache();
		if (cache != null && cache.isPresent()) {
			return cache.get();
		}
		try {
			Optional<T> parsed = Optional.of(doParse(replacer));
			if (cache != null) {
				cache.set(parsed);
			}
			return parsed;
		} catch (Throwable error) {
			/* - actually don't valuesCache when errors because it might not be noticed then. prefer error spamming so that they actually fix it ; (there's already a logspam prevention in loggers as well)
			if (valuesCache != null) {
				valuesCache.set(Optional.empty());
			}*/
			throw error instanceof ParsingError ? (ParsingError) error : new ParsingError(this, error);
		}
	}

	T doParse(@Nonnull Replacer replacer) throws ParsingError;

	// ----- allow some shotcuts, otherwise it's waaaaaayy too boring
	default T directParseOrNull(@Nonnull Replacer replacer) {
		return parse(replacer).orNull();
	}

	default T directParseOrElse(@Nonnull Replacer replacer, T def) {
		return parse(replacer).orElse(def);
	}

	default T directParseOrElse(@Nonnull Replacer replacer, Supplier<T> def) {
		return parse(replacer).orElse(def);
	}

	default OptionalIfPresentFail directParseAndIfPresentDo(@Nonnull Replacer replacer, Consumer<T> ifPresent) {
		return parse(replacer).ifPresentDo(ifPresent);
	}

	default T parseNoCatchOrThrowParsingNull(@Nonnull Replacer replacer) throws ParsingError {  // useful when parsing some containers
		return parseNoCatch(replacer).orThrowParsingNull(this);
	}

}
