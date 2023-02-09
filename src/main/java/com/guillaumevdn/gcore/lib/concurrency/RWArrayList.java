package com.guillaumevdn.gcore.lib.concurrency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

import com.guillaumevdn.gcore.lib.collection.IteratorControls;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * This is to avoid having to/forgetting to synchronize manually on this set.
 * Iteration/stream methods will therefore throw an UnsupportedOperationException when called and forEach() or iterate() should be used instead.
 * @author GuillaumeVDN
 */
public class RWArrayList<T> extends ArrayList<T> {

	private static final long serialVersionUID = -5108501966613387973L;

	// -------------------- construct --------------------

	public RWArrayList(int initialCapacity) {
		super(initialCapacity);
	}

	// -------------------- element modifier for subclasses --------------------

	protected T elementModifier(Object element) {
		return (T) element;
	}

	// -------------------- lock --------------------

	private final RWLock lock = new RWLock();

	// -------------------- iteration --------------------

	@Override
	public final Iterator<T> iterator() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
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
	public List<T> subList(int fromIndex, int toIndex) {
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

	public final void iterate(BiConsumer<T, IteratorControls> consumer) {
		lock.lockRead();
		try {
			IteratorControls iter = new IteratorControls();
			Iterator<T> it = super.iterator();
			while (it.hasNext()) {
				T next = it.next();
				consumer.accept(next, iter);
				if (iter.mustRemove()) {
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
				}
				if (iter.mustStop()) {
					break;
				}
				iter.reset();
			}
		} finally {
			lock.unlockRead();
		}
	}

	public final ArrayList<T> copy() {
		return lock.read(() -> {
			ArrayList<T> copy = new ArrayList<>();
			Iterator<T> i = super.iterator();
			while (i.hasNext()) {
				copy.add(i.next());
			}
			return copy;
		});
	}

	public final ArrayList<T> consume() {
		return lock.write(() -> {
			ArrayList<T> copy = new ArrayList<>();
			Iterator<T> i = super.iterator();
			while (i.hasNext()) {
				copy.add(i.next());
			}
			super.clear();
			return copy;
		});
	}

	public final <R> R streamResult(Function<Stream<T>, R> operator) {
		return lock.read(() -> {
			return operator.apply(StreamSupport.stream(super.spliterator(), false));
		});
	}

	public final Optional<T> findFirst() {
		return lock.read(() -> StreamSupport.stream(super.spliterator(), false).findFirst());
	}

	public final Optional<T> findFirst(Predicate<? super T> filter) {
		return lock.read(() -> StreamSupport.stream(super.spliterator(), false).filter(filter).findFirst());
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
			RWArrayList<?> otherRW = (RWArrayList<?>) ObjectUtils.castOrNull(other, RWArrayList.class);
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
	public final RWArrayList<T> clone() {
		return lock.read(() -> {
			RWArrayList<T> clone = new RWArrayList<>(super.size());
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
	public T get(int index) {
		return lock.read(() -> {
			return super.get(index);
		});
	}

	@Nullable
	public T getLast() {
		return lock.read(() -> {
			return super.isEmpty() ? null : super.get(super.size() - 1);
		});
	}

	@Override
	public int indexOf(Object o) {
		return lock.read(() -> {
			return super.indexOf(elementModifier(o));
		});
	}

	@Override
	public int lastIndexOf(Object o) {
		return lock.read(() -> {
			return super.lastIndexOf(elementModifier(o));
		});
	}

	@Override
	public final boolean contains(Object o) {
		return lock.read(() -> {
			return super.contains(elementModifier(o));
		});
	}

	@Override
	public final boolean containsAll(Collection<?> c) {
		return lock.read(() -> {
			for (Object o : c) {
				if (!super.contains(elementModifier(o))) {
					return false;
				}
			}
			return true;
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

	@Override
	public void trimToSize() {
		lock.write(() -> {
			super.trimToSize();
		});
	}

	@Override
	public void ensureCapacity(int minCapacity) {  // not the internal one
		lock.write(() -> {
			super.ensureCapacity(minCapacity);
		});
	}

	@Override
	public boolean add(T e) {
		return lock.write(() -> {
			return super.add(elementModifier(e));
		});
	}

	@Override
	public void add(int index, T e) {
		lock.write(() -> {
			super.add(index, elementModifier(e));
		});
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return lock.write(() -> {
			super.ensureCapacity(super.size() + c.size());
			int i = index - 1;
			for (T e : c) {
				super.add(++i, elementModifier(e));
			}
			return c.size() != 0;
		});
	}

	@Override
	public final boolean addAll(Collection<? extends T> c) {
		return lock.write(() -> {
			super.ensureCapacity(super.size() + c.size());
			for (T e : c) {
				super.add(elementModifier(e));
			}
			return c.size() != 0;
		});
	}

	@Override
	public T set(int index, T e) {
		return lock.write(() -> {
			return super.set(index, elementModifier(e));
		});
	}

	@Override
	public void replaceAll(UnaryOperator<T> operator) {
		lock.write(() -> {
			super.replaceAll(t -> elementModifier(operator.apply(t)));
		});
	}

	@Override
	public void sort(Comparator<? super T> c) {
		lock.write(() -> {
			super.sort(c);
		});
	}

	@Override
	public T remove(int index) {
		return lock.write(() -> {
			return super.remove(index);
		});
	}

	public T removeLast() {
		return lock.write(() -> {
			return super.remove(super.size() - 1);
		});
	}

	@Nullable
	public T consumeFirst() {
		return lock.write(() -> {
			if (super.isEmpty()) {
				return null;
			}
			return super.remove(0);
		});
	}

	@Override
	public final boolean retainAll(Collection<?> c) {
		Objects.requireNonNull(c);
		List<?> filter = c.stream().map(f -> elementModifier(f)).collect(Collectors.toList());
		return removeIf(elem -> !filter.contains(elem));  // remove if other collection does not contain this element
	}

	@Override
	public final boolean removeAll(Collection<?> c) {
		Objects.requireNonNull(c);
		List<?> filter = c.stream().map(f -> elementModifier(f)).collect(Collectors.toList());
		return removeIf(elem -> filter.contains(elem));  // remove if other collection contain this element
	}

	@Override
	public final boolean removeIf(Predicate<? super T> filter) {
		Objects.requireNonNull(filter);
		lock.lockRead();
		try {
			boolean modified = false;
			Iterator<T> it = super.iterator();
			while (it.hasNext()) {
				if (filter.test(it.next())) {
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
					modified = true;
				}
			}
			return modified;
		} finally {
			lock.unlockRead();
		}
	}

	/* -------------------- (potential) write methods with loss of performance --------------------
		due to (a) pre-check that would be made during iteration in the original method
		       (b) using a write lock without being sure that modifications will be made
		... I'm too lazy to completely rewrite a HashSet implementation with "low-level" direct operations on the table :Kappa:
	 */

	@Override
	public final boolean remove(Object o) {
		boolean exists = contains(elementModifier(o));
		if (exists) {
			lock.write(() -> {
				super.remove(elementModifier(o));
			});
		}
		return exists;
	}

	/* -------------------- extra methods  -------------------- */

	public final T random() {
		return lock.read(() -> {
			final int size = super.size();
			return size == 0 ? null : super.get(NumberUtils.random(0, size - 1));
		});
	}

}
