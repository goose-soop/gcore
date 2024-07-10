package com.guillaumevdn.gcore.lib.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.string.TextElement;

/**
 * @author GuillaumeVDN
 */
public final class Optional<T> {

	private T value;

	private Optional(T value) {
		this.value = value;
	}

	// ----- filter
	/** @return this optional, with value set to null if it doesn't match the filter */
	public Optional<T> filter(Predicate<T> filter) {
		if (!isPresent() || !filter.test(value)) {
			value = null;
		}
		return this;
	}

	// ----- get/or else
	public boolean isPresent() {
		return value != null;
	}

	public T orElse(Supplier<T> def) {
		return value != null ? value : (def != null ? def.get() : null);
	}

	public <TT> TT orElseAs(Supplier<T> def) {
		return (TT) orElse(def);
	}

	public Optional<T> orElseOptional(Optional<T> def) {
		return value != null ? this : def;
	}

	public T orElse(T def) {
		return value != null ? value : def;
	}

	public T orNull() {
		return value;
	}

	public <TT> TT orNullAs() {
		return (TT) value;
	}

	public T orEmptyList() {
		return orElse(() -> (T) new ArrayList<>(0));
	}

	public T orEmptyMap() {
		return orElse(() -> (T) new HashMap<>(0, 1f));
	}

	public T orEmptyText() {
		return orElse(() -> (T) new TextElement());
	}

	public boolean listContains(Object elem) {
		return value != null && ((Collection<?>) value).contains(elem);
	}

	// ----- do
	public <R> Optional<R> mapCastOrNull(Class<R> castClass) {
		return isPresent() ? of(ObjectUtils.castOrNull(value, castClass)) : empty();
	}

	public <R> Optional<R> ifPresentMapCastOrNull(Class<R> castClass) {
		return mapCastOrNull(castClass);
	}

	public <R> Optional<R> map(Function<T, R> mapper) {
		return isPresent() ? of(mapper.apply(value)) : empty();
	}

	public <R> Optional<R> ifPresentMap(Function<T, R> mapper) {
		return map(mapper);
	}

	public <R> Optional<R> mapOptional(Function<T, Optional<R>> mapper) {
		return isPresent() ? mapper.apply(value) : empty();
	}

	@Deprecated
	public <R> Optional<R> ifPresentMapOptional(Function<T, Optional<R>> mapper) {
		return mapOptional(mapper);
	}

	public OptionalIfPresentFail ifPresentDo(Consumer<T> ifPresent) {
		if (value != null)
			ifPresent.accept(value);
		return new OptionalIfPresentFail(value != null);
	}

	public OptionalIfPresentFail ifPresentDoThrowable(ThrowableConsumer<T> ifPresent) throws Throwable {
		if (value != null)
			ifPresent.accept(value);
		return new OptionalIfPresentFail(value != null);
	}

	public <E> OptionalIfPresentFail ifPresentForEach(Consumer<E> ifPresent) {
		if (value != null) {
			Collection<E> coll = (Collection<E>) value;
			coll.forEach(ifPresent);
		}
		return new OptionalIfPresentFail(value != null);
	}

	public <K, V> OptionalIfPresentFail ifPresentMapForEach(BiConsumer<K, V> ifPresent) {
		if (value != null) {
			Map<K, V> map = (Map<K, V>) value;
			map.forEach(ifPresent);
		}
		return new OptionalIfPresentFail(value != null);
	}

	public <X extends Throwable> T orThrow(Supplier<X> builder) throws X {
		if (value != null)
			return value;
		throw builder.get();
	}

	public T orThrowParsingNull(ParseableElement atFault) throws ParsingError {
		return orThrow(() -> new ParsingError(atFault, "invalid value"));
	}

	// ----- object
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		Optional other = ObjectUtils.castOrNull(obj, Optional.class);
		return other != null && Objects.deepEquals(value, other.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	// ----- static
	public static <T> Optional<T> of(T value) {
		return new Optional<>(value);
	}

	public static <T> Optional<T> empty() {
		return new Optional<>(null);
	}

}
