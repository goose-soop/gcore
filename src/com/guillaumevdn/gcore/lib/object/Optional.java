package com.guillaumevdn.gcore.lib.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
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

	// filter
	/** @return this optional, with value set to null if it doesn't match the filter */
	public Optional<T> filter(Predicate<T> filter) {
		if (!isPresent() || !filter.test(value)) {
			value = null;
		}
		return this;
	}

	// get/or else
	public boolean isPresent() {
		return value != null;
	}

	public T orElse(Supplier<T> def) {
		return value != null ? value : (def != null ? def.get() : null);
	}

	public T orElse(T def) {
		return value != null ? value : def;
	}

	public T orNull() {
		return value;
	}

	public T orEmptyList() {
		return orElse(() -> (T) new ArrayList<>());
	}

	public T orEmptyMap() {
		return orElse(() -> (T) new ArrayList<>());
	}

	public T orEmptyText() {
		return orElse(() -> (T) new TextElement());
	}

	public boolean listContains(Object elem) {
		return value != null && ((Collection<?>) value).contains(elem);
	}

	// do
	public <R> Optional<R> ifPresentMap(Function<T, R> mapper) {
		return isPresent() ? of(mapper.apply(value)) : empty();
	}

	public OptionalIfPresentFail ifPresentDo(Consumer<T> ifPresent) {
		if (value != null) ifPresent.accept(value);
		return new OptionalIfPresentFail(value != null);
	}

	public OptionalIfPresentFail ifPresentDoThrowable(ThrowableConsumer<T> ifPresent) throws Throwable {
		if (value != null) ifPresent.accept(value);
		return new OptionalIfPresentFail(value != null);
	}

	public <E> OptionalIfPresentFail ifPresentForEach(Consumer<E> ifPresent) {
		Collection<E> coll = (Collection<E>) value;
		if (coll != null) {
			for (E elem : coll) {
				ifPresent.accept(elem);
			}
		}
		return new OptionalIfPresentFail(coll != null);
	}

	public <X extends Throwable> T orThrow(Supplier<X> builder) throws X {
		if (value != null) return value;
		throw builder.get();
	}

	public T orThrowParsingNull(ParseableElement atFault) throws ParsingError {
		return orThrow(() -> new ParsingError(atFault, "invalid value"));
	}

	// object
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

	// static
	public static <T> Optional<T> of(T value) {
		return new Optional<>(value);
	}

	public static <T> Optional<T> empty() {
		return new Optional<>(null);
	}

}
