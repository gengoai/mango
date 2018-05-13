package com.gengoai.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * @author David B. Bracewell
 */
public abstract class SetDecorator<E> implements Set<E> {

   public abstract Set<E> getDecorated();

   @Override
   public int size() {
      return getDecorated().size();
   }

   @Override
   public boolean isEmpty() {
      return getDecorated().isEmpty();
   }

   @Override
   public boolean contains(Object o) {
      return getDecorated().contains(o);
   }

   @Override
   public Iterator<E> iterator() {
      return getDecorated().iterator();
   }

   @Override
   public Object[] toArray() {
      return getDecorated().toArray();
   }

   @Override
   public <T> T[] toArray(T[] a) {
      return getDecorated().toArray(a);
   }

   @Override
   public boolean add(E e) {
      return getDecorated().add(e);
   }

   @Override
   public boolean remove(Object o) {
      return getDecorated().remove(o);
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      return getDecorated().containsAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends E> c) {
      return getDecorated().addAll(c);
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      return getDecorated().retainAll(c);
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      return getDecorated().removeAll(c);
   }

   @Override
   public void clear() {
      getDecorated().clear();
   }
}//END OF SetDecorator
