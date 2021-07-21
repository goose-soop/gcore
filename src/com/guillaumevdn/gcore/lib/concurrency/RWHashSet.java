package com.guillaumevdn.gcore.lib.concurrency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.object.Optional;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.wrapper.WrapperBoolean;

/**
 * This is to avoid having to/forgetting to synchronize manually on this set.
 * Iteration/stream methods will therefore throw an UnsupportedOperationException when called and forEach() or iterate() should be used instead.
 * @author GuillaumeVDN
 */
public class RWHashSet<T> extends HashSet<T> {

	private static final long serialVersionUID = -5108501966613387973L;

	// -------------------- lock --------------------

	private final RWLock lock = new RWLock();

	// -------------------- iteration --------------------

	@Override
	public final Iterator<T> iterator() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Spliterator<T> spliterator() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Stream<T> stream() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Stream<T> parallelStream() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void forEach(Consumer<? super T> action) {
		lock.read(() -> {
			Iterator<T> it = super.iterator();
			while (it.hasNext()) {
				action.accept(it.next());
			}
		});
	}

	public final void forEachThrowable(ThrowableConsumer<? super T> action) throws Throwable {
		lock.readThrowable(() -> {
			Iterator<T> it = super.iterator();
			while (it.hasNext()) {
				action.accept(it.next());
			}
		});
	}

	/**
	 * @param consumer next element, remover (set to true to remove current element), breaker (set to true to stop iterating)
	 */
	public final void iterate(TriConsumer<T, WrapperBoolean /* remover */, WrapperBoolean /* breaker */> consumer) {
		lock.lockRead();
		try {
			Iterator<T> it = super.iterator();
			WrapperBoolean remover = WrapperBoolean.of(false);
			WrapperBoolean breaker = WrapperBoolean.of(false);
			while (it.hasNext()) {
				T next = it.next();
				consumer.accept(next, remover, breaker);
				if (remover.get()) {
					lock.unlockRead();
					lock.lockWrite();  // wait until this set is good for modification
					try {
						it.remove();
					} catch (Throwable error) {  // if anything happens during write state, unlock write lock and rethrow error
						lock.unlockWrite();
						throw error;
					}
					lock.lockRead();  // downgrade to read lock (by releasing read lock first) to have priority and continue reading
					lock.unlockWrite();
					remover.set(false);
				}
				if (breaker.get()) {
					break;
				}
			}
		} finally {
			lock.unlockRead();
		}
	}

	public final Optional<T> findFirst() {
		return lock.read(() -> super.isEmpty() ? Optional.empty() : Optional.of(super.iterator().next()));
	}

	public final HashSet<T> copy() {
		return lock.read(() -> {
			HashSet<T> copy = new HashSet<>();
			Iterator<T> i = super.iterator();
			while (i.hasNext()) {
				copy.add(i.next());
			}
			return copy;
		});
	}

	public final List<T> copyToList() {
		return lock.read(() -> {
			List<T> copy = new ArrayList<>();
			Iterator<T> i = super.iterator();
			while (i.hasNext()) {
				copy.add(i.next());
			}
			return copy;
		});
	}

	public final <R> R streamResult(Function<Stream<T>, R> operator) {
		return lock.read(() -> {
			return operator.apply(StreamSupport.stream(super.spliterator(), false));
		});
	}

	// -------------------- object --------------------

	private int unsafeSize() { return super.size(); }
	private Iterator<T> unsafeIterator() { return super.iterator(); }
	private boolean unsafeContains(Object o) { return super.contains(o); }

	@Override
	public final boolean equals(Object o) {  // adapted code from HashSet
		if (o == this)
			return true;
		if (!(o instanceof Set))
			return false;
		Set<?> other = (Set<?>) o;

		return lock.read(() -> {
			// other is also a RW set ; lock once and use unsafe methods to avoid locking/unlocking every 3 nanoseconds
			RWHashSet<?> otherRW = (RWHashSet<?>) ObjectUtils.castOrNull(other, RWHashSet.class);
			if (otherRW != null) {
				return otherRW.lock.read(() -> {
					if (otherRW.unsafeSize() != super.size())
						return false;
					try {
						Iterator<?> it = otherRW.unsafeIterator();
						while (it.hasNext()) {
							if (!unsafeContains(it.next())) {
								return false;
							}
						}
						return true;
					} catch (ClassCastException unused)   {
						return false;
					} catch (NullPointerException unused) {
						return false;
					}
				});
			}
			// other is not a RW set ; use regular methods
			else {
				if (other.size() != super.size())
					return false;
				try {
					return super.containsAll(other);
				} catch (ClassCastException unused)   {
					return false;
				} catch (NullPointerException unused) {
					return false;
				}
			}
		});
	}

	@Override
	public final int hashCode() {  // adapted code from HashSet
		return lock.read(() -> {
			int h = 0;
			Iterator<T> i = super.iterator();
			while (i.hasNext()) {
				T obj = i.next();
				if (obj != null)
					h += obj.hashCode();
			}
			return h;
		});
	}

	@Override
	public final String toString() {  // adapted code from HashSet
		return lock.read(() -> {
			Iterator<T> it = super.iterator();
			if (! it.hasNext())
				return "[]";

			StringBuilder sb = new StringBuilder();
			sb.append('[');
			for (;;) {
				T e = it.next();
				sb.append(e == this ? "(this Collection)" : e);
				if (!it.hasNext())
					return sb.append(']').toString();
				sb.append(',').append(' ');
			}
		});
	}

	public String toTextString(String separator) {
		return lock.read(() -> {
			StringBuilder builder = new StringBuilder();
			Iterator iterator = super.iterator();
			while (iterator.hasNext()) {
				StringUtils.toTextStringObject(iterator.next(), builder);
				if (iterator.hasNext()) {
					builder.append(separator);
				}
			}
			return builder.toString();
		});
	}

	@Override
	public final RWHashSet<T> clone() {
		return lock.read(() -> {
			RWHashSet<T> clone = new RWHashSet<>();
			clone.addAll(this);
			return clone;
		});
	}

	// -------------------- read methods --------------------

	@Override
	public final int size() {
		return lock.read(() -> {
			return super.size();
		});
	}

	@Override
	public final boolean isEmpty() {
		return lock.read(() -> {
			return super.isEmpty();
		});
	}

	@Override
	public final boolean contains(Object o) {
		return lock.read(() -> {
			return super.contains(o);
		});
	}

	@Override
	public final boolean containsAll(Collection<?> c) {
		return lock.read(() -> {
			return super.containsAll(c);
		});
	}

	@Override
	public final Object[] toArray() {
		return lock.read(() -> {
			return super.toArray();
		});
	}

	public final <U extends Object> U[] toArray(U[] a) {
		return lock.read(() -> {
			return super.toArray(a);
		});
	}

	// -------------------- write methods --------------------

	@Override
	public final void clear() {
		lock.write(() -> {
			super.clear();
		});
	}

	/* -------------------- (potential) write methods with loss of performance --------------------
		due to (a) pre-check that would be made during iteration in the original method
		       (b) using a write lock without being sure that modifications will be made
		... I'm too lazy to completely rewrite a HashSet implementation with "low-level" direct operations on the table :Kappa:
	 */

	@Override
	public final boolean add(T e) {
		return lock.write(() -> super.add(e));
	}

	@Override
	public final boolean addAll(Collection<? extends T> other) {
		return lock.write(() -> super.addAll(other));
	}

	@Override
	public final boolean remove(Object o) {
		return lock.write(() -> super.remove(o));
	}

	@Override
	public final boolean retainAll(Collection<?> c) {
		Objects.requireNonNull(c);
		return removeIf(elem -> !c.contains(elem));  // remove if other collection does not contain this element
	}

	@Override
	public final boolean removeAll(Collection<?> c) {
		Objects.requireNonNull(c);
		return removeIf(elem -> c.contains(elem));  // remove if other collection contain this element
	}

	@Override
	public final boolean removeIf(Predicate<? super T> filter) {
		Objects.requireNonNull(filter);
		return lock.write(() -> {
			boolean modified = false;
			Iterator<T> it = super.iterator();
			while (it.hasNext()) {
				if (filter.test(it.next())) {
					it.remove();
					modified = true;
				}
			}
			return modified;
		});
	}

}
