package com.gengoai.collection;

import com.gengoai.conversion.Convert;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.List;
import java.util.ListIterator;

import static com.gengoai.Validation.*;

/**
 * <p>Wraps a primitive array treating it as a list of a given type. Most useful for converting primitives to their
 * boxed types.</p>
 *
 * @param <E> the boxed type
 * @author David B. Bracewell
 */
public class PrimitiveArrayList<E> extends AbstractList<E> implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Object array;
   private final Class<?> primitiveClass;
   private final Class<E> boxedType;
   private final int start;
   private final int end;


   /**
    * Instantiates a new Primitive array list.
    *
    * @param array     the array to wrap
    * @param boxedType the boxed type
    */
   public PrimitiveArrayList(Object array, Class<E> boxedType) {
      this(validateArg(array,
                       a -> a.getClass().isArray(),
                       false),
           validateArg(array.getClass().getComponentType(),
                       Class::isPrimitive,
                       false),
           notNull(boxedType),
           0,
           -1);
   }

   private PrimitiveArrayList(Object array, Class<?> primitiveClass, Class<E> boxedType, int start, int end) {
      this.array = array;
      this.primitiveClass = primitiveClass;
      this.boxedType = boxedType;
      this.start = start;
      this.end = end < 0 ? Array.getLength(array) : end;
   }

   @Override
   public E set(int index, E element) {
      E out = get(start + index);
      Array.set(array, start + index, Convert.convert(element, primitiveClass));
      return out;
   }

   @Override
   public E get(int index) {
      Object o = Array.get(array, start + index);
      return Convert.convert(o, boxedType);
   }

   @Override
   public int size() {
      return (end - start);
   }

   @Override
   public ListIterator<E> listIterator() {
      return new PrimitiveArrayListIterator();
   }

   @Override
   public List<E> subList(int fromIndex, int toIndex) {
      return new PrimitiveArrayList<>(array, primitiveClass, boxedType, fromIndex, toIndex);
   }

   @Override
   public ListIterator<E> listIterator(int index) {
      checkElementIndex(index, PrimitiveArrayList.this.size());
      return new PrimitiveArrayListIterator(start + index - 1);
   }

   private class PrimitiveArrayListIterator implements ListIterator<E> {
      private int index;

      private PrimitiveArrayListIterator() {
         this(0);
      }

      private PrimitiveArrayListIterator(int index) {
         this.index = index - 1;
      }

      @Override
      public boolean hasNext() {
         return nextIndex() < PrimitiveArrayList.this.size();
      }

      @Override
      public E next() {
         checkElementIndex(nextIndex(), PrimitiveArrayList.this.size());
         index = nextIndex();
         return PrimitiveArrayList.this.get(index);
      }

      @Override
      public boolean hasPrevious() {
         return index > 0;
      }

      @Override
      public E previous() {
         checkElementIndex(previousIndex(), PrimitiveArrayList.this.size());
         index = previousIndex();
         return PrimitiveArrayList.this.get(index);
      }

      @Override
      public int nextIndex() {
         return index + 1;
      }

      @Override
      public int previousIndex() {
         return index - 1;
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }

      @Override
      public void set(E e) {
         checkElementIndex(index, PrimitiveArrayList.this.size());
         PrimitiveArrayList.this.set(index, e);
      }

      @Override
      public void add(E e) {
         throw new UnsupportedOperationException();
      }
   }

}//END OF PrimitiveArrayList
