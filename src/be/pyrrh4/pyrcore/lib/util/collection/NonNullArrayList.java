package be.pyrrh4.pyrcore.lib.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NonNullArrayList<E> extends ArrayList<E> {

	private static final long serialVersionUID = -3495320704443730160L;

	/**
	 * Constructs an empty list with the specified initial capacity.
	 *
	 * @param  initialCapacity  the initial capacity of the list
	 * @throws IllegalArgumentException if the specified initial capacity
	 *         is negative
	 */
	public NonNullArrayList(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public NonNullArrayList() {
		super();
	}

	/**
	 * Constructs a list containing the elements of the specified
	 * collection, in the order they are returned by the collection's
	 * iterator.
	 *
	 * @param c the collection whose elements are to be placed into this list
	 * @throws NullPointerException if the specified collection is null
	 */
	public NonNullArrayList(Collection<? extends E> c) {
		super(c);
	}

	// add
	@Override
	public boolean add(E e) {
		if (e == null) return false;
		return super.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (c.isEmpty()) return true;
		List<E> copy = new ArrayList<E>();
		for (E e : c) {
			if (e != null) {
				copy.add(e);
			}
		}
		return !copy.isEmpty() && addAll(copy);
	}

	@Override
	public void add(int index, E element) {
		if (element == null) return;
		super.add(index, element);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		if (c.isEmpty()) return true;
		List<E> copy = new ArrayList<E>();
		for (E e : c) {
			if (e != null) {
				copy.add(e);
			}
		}
		return !copy.isEmpty() && addAll(index, copy);
	}

	@Override
	public E set(int index, E element) {
		return element == null ? null : super.set(index, element);
	}

}
