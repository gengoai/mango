package com.gengoai.collection;

import com.gengoai.Validation;
import com.gengoai.conversion.Cast;
import com.gengoai.function.SerializableFunction;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.gengoai.Validation.checkArgument;
import static com.gengoai.Validation.notNull;
import static com.gengoai.collection.Streams.asStream;

/**
 * The type Iterables.
 *
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
   public static <T> Iterable<T> asIterable(final Object array, final Class<T> itemClass) {
      notNull(array);
      notNull(itemClass);
      checkArgument(array.getClass().isArray());
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
      notNull(iterator);
      return () -> Cast.as(iterator);
   }

   /**
    * Concat iterable.
    *
    * @param <T>       the type parameter
    * @param iterables the iterables
    * @return the iterable
    */
   @SafeVarargs
   public static <T> Iterable<T> concat(Iterable<? extends T>... iterables) {
      final Iterator<? extends T>[] iterators = Streams.asStream(notNull(iterables))
                                                       .toArray((IntFunction<Iterator<? extends T>[]>) Iterator[]::new);
      return new IteratorIterable<>(() -> Iterators.concat(iterators));
   }

   /**
    * Get optional.
    *
    * @param <T>      the type parameter
    * @param iterable the iterable
    * @param index    the index
    * @return the optional
    */
   public static <T> Optional<T> get(Iterable<? extends T> iterable, int index) {
      notNull(iterable);
      checkArgument(index >= 0, "index must be >= 0");
      if (iterable instanceof List) {
         List<T> list = Cast.as(iterable);
         if (index < list.size()) {
            return Optional.ofNullable(list.get(index));
         }
         return Optional.empty();
      }
      return Iterators.get(iterable.iterator(), index);
   }

   /**
    * Get t.
    *
    * @param <T>          the type parameter
    * @param iterable     the iterable
    * @param index        the index
    * @param defaultValue the default value
    * @return the t
    */
   public static <T> T get(Iterable<? extends T> iterable, int index, T defaultValue) {
      return get(iterable, index).orElse(Cast.as(defaultValue));
   }

   /**
    * <p>Returns the first item in an iterable. </p>
    *
    * @param <T>      the type of element in the iterable
    * @param iterable the iterable
    * @return An optional containing the first element in the iterable or null if none
    */
   public static <T> Optional<T> getFirst(Iterable<? extends T> iterable) {
      return Iterators.next(notNull(iterable).iterator());
   }

   /**
    * Gets first.
    *
    * @param <T>          the type parameter
    * @param iterable     the iterable
    * @param defaultValue the default value
    * @return the first
    */
   public static <T> T getFirst(Iterable<T> iterable, T defaultValue) {
      return Iterators.next(notNull(iterable).iterator(), defaultValue);
   }

   /**
    * <p>Returns the last item in an iterable. </p>
    *
    * @param <T>      the type of element in the iterable
    * @param iterable the iterable
    * @return An optional containing the last element in the iterable or null if none
    */
   public static <T> Optional<T> getLast(Iterable<? extends T> iterable) {
      notNull(iterable);
      if (iterable instanceof List) {
         List<T> list = Cast.as(iterable);
         return Optional.ofNullable(list.get(list.size() - 1));
      }
      return Iterators.last(iterable.iterator());
   }

   /**
    * <p>Returns the last item in an iterable. </p>
    *
    * @param <T>          the type of element in the iterable
    * @param iterable     the iterable
    * @param defaultValue default value if the list is empty
    * @return An optional containing the last element in the iterable or null if none
    */
   public static <T> T getLast(Iterable<? extends T> iterable, T defaultValue) {
      return getLast(notNull(iterable)).orElse(Cast.as(defaultValue));
   }

//
//   /**
//    * Max index and value tuple 2.
//    *
//    * @param <E>      the type parameter
//    * @param iterable the iterable
//    * @return the tuple 2
//    */
//   public static <E extends Comparable<? super E>> Tuple2<Integer, E> maxIndexAndValue(Iterable<? extends E> iterable) {
//      return maxIndexAndValue(notNull(iterable), Sorting.natural());
//   }
//
//   /**
//    * Max index and value tuple 2.
//    *
//    * @param <E>        the type parameter
//    * @param iterable   the iterable
//    * @param comparator the comparator
//    * @return the tuple 2
//    */
//   public static <E extends Comparable<? super E>> Tuple2<Integer, E> maxIndexAndValue(Iterable<? extends E> iterable,
//                                                                                       Comparator<? super E> comparator
//                                                                                      ) {
//      notNull(iterable);
//      notNull(comparator);
//      int index = -1;
//      E max = null;
//      int i = 0;
//      for (E e : iterable) {
//         if (max == null || comparator.compare(e, max) > 0) {
//            max = e;
//            index = i;
//         }
//         i++;
//      }
//
//      return Tuples.$(index, max);
//   }
//
//   /**
//    * Min index and value tuple 2.
//    *
//    * @param <E>      the type parameter
//    * @param iterable the iterable
//    * @return the tuple 2
//    */
//   public static <E extends Comparable<? super E>> Tuple2<Integer, E> minIndexAndValue(@NonNull Iterable<? extends E> iterable) {
//      return minIndexAndValue(iterable, Sorting.natural());
//   }
//
//   /**
//    * Min index and value tuple 2.
//    *
//    * @param <E>        the type parameter
//    * @param iterable   the iterable
//    * @param comparator the comparator
//    * @return the tuple 2
//    */
//   public static <E extends Comparable<? super E>> Tuple2<Integer, E> minIndexAndValue(@NonNull Iterable<? extends E> iterable, @NonNull Comparator<? super E> comparator) {
//      int index = -1;
//      E min = null;
//      int i = 0;
//      for (E e : iterable) {
//         if (min == null || comparator.compare(e, min) < 0) {
//            min = e;
//            index = i;
//         }
//         i++;
//      }
//
//      return Tuples.$(index, min);
//   }

   /**
    * Gets the size of the iterable
    *
    * @param iterable the iterable
    * @return the number of items in the iterable
    */
   public static int size(Iterable<?> iterable) {
      notNull(iterable);
      if (iterable instanceof Collection) {
         return ((Collection) iterable).size();
      }
      return Iterators.size(iterable.iterator());
   }

   /**
    * <p>Sorts the items of an iterable returning an array of the sorted items.</p>
    *
    * @param <E>      The component type of the iterable which implements the <code>Comparable</code> interface.
    * @param iterable The iterable instance to sort
    * @return A list of the items in the given iterable sorted using the items natural comparator.
    */
   public static <E extends Comparable<? super E>> List<E> sort(Iterable<? extends E> iterable) {
      return asStream(notNull(iterable)).sorted().collect(Collectors.toList());
   }

   /**
    * <p>Sorts the items of an iterable returning an array of the sorted items.</p>
    *
    * @param <E>        The component type of the iterable.
    * @param iterable   The iterable instance to sort
    * @param comparator The comparator to use for sorting
    * @return A list of the items in the given iterable sorted using the given comparator.
    */
   public static <E> List<E> sort(Iterable<? extends E> iterable, Comparator<? super E> comparator) {
      return asStream(notNull(iterable)).sorted(notNull(comparator)).collect(Collectors.toList());
   }

   /**
    * Transform iterable.
    *
    * @param <I>      the type parameter
    * @param <O>      the type parameter
    * @param iterable the iterable
    * @param function the function
    * @return the iterable
    */
   public static <I, O> Iterable<O> transform(final Iterable<? extends I> iterable,
                                              final SerializableFunction<? super I, ? extends O> function
                                             ) {
      return new IteratorIterable<>(() -> Iterators.transform(notNull(iterable).iterator(), notNull(function)));
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
   public static <T, U> Iterable<Map.Entry<T, U>> zip(final Iterable<? extends T> iterable1,
                                                      final Iterable<? extends U> iterable2
                                                     ) {
      return new IteratorIterable<>(() -> Iterators.zip(notNull(iterable1).iterator(), notNull(iterable2).iterator()));
   }

   /**
    * Creates pairs of entries from the given iterable and its index in the iterable (0 based)
    *
    * @param <T>      the iterator type parameter
    * @param iterable the iterator
    * @return the iterable with index values
    */
   public static <T> Iterable<Map.Entry<T, Integer>> zipWithIndex(final Iterable<? extends T> iterable) {
      return new IteratorIterable<>(() -> Iterators.zipWithIndex(notNull(iterable).iterator()));
   }

   private static class IteratorIterable<T> implements Iterable<T> {
      private final Supplier<Iterator<? extends T>> supplier;

      private IteratorIterable(Supplier<Iterator<? extends T>> supplier) {
         this.supplier = supplier;
      }

      @Override
      public Iterator<T> iterator() {
         return Cast.cast(supplier.get());
      }
   }

}//END OF Iterables
