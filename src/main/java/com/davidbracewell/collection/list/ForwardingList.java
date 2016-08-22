package com.davidbracewell.collection.list;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * The type Forwarding list.
 *
 * @param <E> the type parameter
 * @author David B. Bracewell
 */
public abstract class ForwardingList<E> implements List<E>, Serializable {
  private static final long serialVersionUID = 1L;


  /**
   * Delegate list.
   *
   * @return the list
   */
  protected abstract List<E> delegate();

  @Override
  public int size() {
    if (delegate() == null) {
      return 0;
    }
    return delegate().size();
  }

  @Override
  public boolean isEmpty() {
    return delegate() == null || delegate().isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return delegate() != null && delegate().contains(o);
  }

  @Override
  public Iterator<E> iterator() {
    if (delegate() == null) {
      return Collections.emptyIterator();
    }
    return delegate().iterator();
  }

  @Override
  public Object[] toArray() {
    if (delegate() == null) {
      return null;
    }
    return delegate().toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    if (delegate() == null) {
      return null;
    }
    return delegate().toArray(a);
  }

  @Override
  public boolean add(E e) {
    return delegate() != null && delegate().add(e);
  }

  @Override
  public boolean remove(Object o) {
    return delegate() != null && delegate().remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return delegate() != null && delegate().containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    return delegate() != null && delegate().addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    return delegate() != null && delegate().addAll(index, c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return delegate() != null && delegate().retainAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return delegate() != null && delegate().retainAll(c);
  }

  @Override
  public void clear() {
    if (delegate() != null) {
      delegate().clear();
    }
  }

  @Override
  public E get(int index) {
    return delegate().get(index);
  }

  @Override
  public E set(int index, E element) {
    return delegate().set(index, element);
  }

  @Override
  public void add(int index, E element) {
    if (delegate() != null) {
      delegate().add(index, element);
    }
  }

  @Override
  public E remove(int index) {
    return delegate().remove(index);
  }

  @Override
  public int indexOf(Object o) {
    if (delegate() == null) {
      return -1;
    }
    return delegate().indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    if (delegate() == null) {
      return -1;
    }
    return delegate().lastIndexOf(o);
  }

  @Override
  public ListIterator<E> listIterator() {
    if (delegate() == null) {
      return Collections.emptyListIterator();
    }
    return delegate().listIterator();
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    if (delegate() == null) {
      return Collections.emptyListIterator();
    }
    return delegate().listIterator(index);
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    if (delegate() == null) {
      return this;
    }
    return delegate().subList(fromIndex, toIndex);
  }

}// END OF ForwardingList
