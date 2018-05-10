package com.gengoai.collection;

import com.gengoai.Validation;
import com.gengoai.collection.list.PrimitiveArrayList;
import com.gengoai.conversion.Cast;
import com.gengoai.tuple.Tuple2;
import com.gengoai.tuple.Tuples;
import lombok.NonNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gengoai.collection.Streams.asStream;

/**
 * @author David B. Bracewell
 */
public final class Iterables {

   private Iterables() {
      throw new IllegalAccessError();
   }

   /**
    * Wraps an <code>array</code> as an <code>Iterable</code>
    *
    * @param <T>       the component type of the array
    * @param array     The array to wrap
    * @param itemClass the component type of the array
    * @return An Iterable wrapping the iterator.
    */
   public static <T> Iterable<T> asIterable(@NonNull final Object array, @NonNull final Class<T> itemClass) {
      Validation.checkArgument(array.getClass().isArray());
      if (array.getClass().getComponentType().isPrimitive()) {
         return new PrimitiveArrayList<>(array, itemClass);
      }
      return () -> new Iterator<T>() {
         int pos = 0;

         @Override
         public boolean hasNext() {
            return pos < Array.getLength(array);
         }

         @Override
         public T next() {
            Validation.checkElementIndex(pos, Array.getLength(array));
            return itemClass.cast(Array.get(array, pos++));
         }
      };
   }

   /**
    * Wraps an <code>Iterator</code> as an <code>Iterable</code>
    *
    * @param <T>      the type parameter
    * @param iterator The iterator to wrap
    * @return An Iterable wrapping the iterator.
    */
   public static <T> Iterable<T> asIterable(final Iterator<? extends T> iterator) {
      if (iterator == null) {
         return () -> Cast.as(Collections.emptyIterator());
      }
      return () -> Cast.as(iterator);
   }

   public static <E extends Comparable<? super E>> Tuple2<Integer, E> maxIndexAndValue(@NonNull Iterable<? extends E> iterable) {
      return maxIndexAndValue(iterable, Sorting.natural());
   }

   public static <E extends Comparable<? super E>> Tuple2<Integer, E> maxIndexAndValue(@NonNull Iterable<? extends E> iterable, @NonNull Comparator<? super E> comparator) {
      int index = -1;
      E max = null;
      int i = 0;
      for (E e : iterable) {
         if (max == null || comparator.compare(e, max) > 0) {
            max = e;
            index = i;
         }
         i++;
      }

      return Tuples.$(index, max);
   }

   public static <E extends Comparable<? super E>> Tuple2<Integer, E> minIndexAndValue(@NonNull Iterable<? extends E> iterable) {
      return minIndexAndValue(iterable, Sorting.natural());
   }

   public static <E extends Comparable<? super E>> Tuple2<Integer, E> minIndexAndValue(@NonNull Iterable<? extends E> iterable, @NonNull Comparator<? super E> comparator) {
      int index = -1;
      E min = null;
      int i = 0;
      for (E e : iterable) {
         if (min == null || comparator.compare(e, min) < 0) {
            min = e;
            index = i;
         }
         i++;
      }

      return Tuples.$(index, min);
   }

   public static int size(@NonNull Iterable<?> iterable) {
      return (int) Streams.asStream(iterable).count();
   }

   /**
    * <p>Returns the first item in an iterable. </p>
    *
    * @param <T>      the type of element in the iterable
    * @param iterable the iterable
    * @return An optional containing the first element in the iterable or null if none
    */
   public static <T> Optional<T> getFirst(@NonNull Iterable<? extends T> iterable) {
      return Cast.as(Streams.asStream(iterable).findFirst());
   }

   public static <T> T getFirstOrNull(@NonNull Iterable<? extends T> iterable) {
      return Streams.asStream(iterable).findFirst().orElse(null);
   }

   /**
    * <p>Returns the last item in an iterable. </p>
    *
    * @param <T>      the type of element in the iterable
    * @param iterable the iterable
    * @return An optional containing the last element in the iterable or null if none
    */
   public static <T> Optional<T> getLast(@NonNull Iterable<? extends T> iterable) {
      T o = null;
      for (T t : iterable) {
         o = t;
      }
      return Optional.ofNullable(o);
   }

   public static <T> T getLastOrNull(@NonNull Iterable<? extends T> iterable) {
      return getLast(iterable).orElse(null);
   }

   /**
    * <p>Sorts the items of an iterable returning an array of the sorted items.</p>
    *
    * @param iterable The iterable instance to sort
    * @param <E>      The component type of the iterable which implements the <code>Comparable</code> interface.
    * @return A list of the items in the given iterable sorted using the items natural comparator.
    */
   public static <E extends Comparable<? super E>> List<E> sort(@NonNull Iterable<E> iterable) {
      return asStream(iterable).sorted().collect(Collectors.toList());
   }

   /**
    * <p>Sorts the items of an iterable returning an array of the sorted items.</p>
    *
    * @param iterable   The iterable instance to sort
    * @param comparator The comparator to use for sorting
    * @param <E>        The component type of the iterable.
    * @return A list of the items in the given iterable sorted using the given comparator.
    */
   public static <E> List<E> sort(@NonNull Iterable<E> iterable, @NonNull Comparator<? super E> comparator) {
      return asStream(iterable).sorted(comparator).collect(Collectors.toList());
   }


   /**
    * <p>Zips (combines) two iterators together. For example, if iterable 1 contained [1,2,3] and iterable 2 contained
    * [4,5,6] the result would be [(1,4), (2,5), (3,6)]. Note that the length of the resulting stream will be the
    * minimum of the two iterables.</p>
    *
    * @param <T>       the component type of the first iterator
    * @param <U>       the component type of the second iterator
    * @param iterable1 the iterator making up the key in the resulting entries
    * @param iterable2 the iterator making up the value in the resulting entries
    * @return A stream of entries whose keys are taken from iterable1 and values are taken from iterable2
    */
   public static <T, U> Stream<Map.Entry<T, U>> zip(@NonNull final Iterable<? extends T> iterable1, @NonNull final Iterable<? extends U> iterable2) {
      return Iterators.zip(iterable1.iterator(), iterable2.iterator());
   }

}//END OF Iterables
