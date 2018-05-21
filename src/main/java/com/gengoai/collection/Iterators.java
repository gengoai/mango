package com.gengoai.collection;

import com.gengoai.Validation;
import com.gengoai.function.SerializableFunction;
import com.gengoai.function.SerializablePredicate;
import lombok.NonNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.gengoai.Validation.notNull;
import static com.gengoai.Validation.validate;
import static com.gengoai.tuple.Tuples.$;

/**
 * The type Iterators.
 *
 * @author David B. Bracewell
 */
public final class Iterators {
   private Iterators() {
      throw new IllegalAccessError();
   }

   /**
    * Transform iterator.
    *
    * @param <I>      the type parameter
    * @param <O>      the type parameter
    * @param iterator the iterator
    * @param function the function
    * @return the iterator
    */
   public static <I, O> Iterator<O> transform(final Iterator<? extends I> iterator,
                                              final SerializableFunction<? super I, ? extends O> function
                                             ) {
      return new TransformedIterator<>(notNull(iterator), notNull(function));
   }

   /**
    * Filter iterator.
    *
    * @param <E>      the type parameter
    * @param iterator the iterator
    * @param filter   the filter
    * @return the iterator
    */
   public static <E> Iterator<E> filter(final Iterator<? extends E> iterator,
                                        final SerializablePredicate<? super E> filter
                                       ) {
      return new FilteredIterator<>(notNull(iterator), notNull(filter));
   }

   /**
    * Concat iterator.
    *
    * @param <T>       the type parameter
    * @param iterators the iterators
    * @return the iterator
    */
   @SafeVarargs
   public static <T> Iterator<T> concat(Iterator<? extends T>... iterators) {
      return new ConcatIterator<T>(Arrays.asList(notNull(iterators)).iterator());
   }

   /**
    * Unmodifiable iterator iterator.
    *
    * @param <T>      the type parameter
    * @param iterator the iterator
    * @return the iterator
    */
   public static <T> Iterator<T> unmodifiableIterator(final Iterator<? extends T> iterator) {
      return new SimpleIteratorDecorator<T>(notNull(iterator)) {
         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   /**
    * Partition iterator.
    *
    * @param <T>           the type parameter
    * @param iterator      the iterator
    * @param partitionSize the partition size
    * @return the iterator
    */
   public static <T> Iterator<List<T>> partition(@NonNull final Iterator<T> iterator, int partitionSize) {
      return partition(iterator, partitionSize, false);
   }

   /**
    * Partition iterator.
    *
    * @param <T>           the type parameter
    * @param iterator      the iterator
    * @param partitionSize the partition size
    * @param pad           the pad
    * @return the iterator
    */
   public static <T> Iterator<List<T>> partition(final Iterator<? extends T> iterator, int partitionSize, boolean pad) {
      Validation.checkArgument(partitionSize > 0, "Partition size must be greater than zero.");
      return new PartitionedIterator<>(notNull(iterator), partitionSize, pad);
   }


   /**
    * <p>Zips (combines) two iterators together. For example, if iterator 1 contained [1,2,3] and iterator 2 contained
    * [4,5,6] the result would be [(1,4), (2,5), (3,6)]. Note that the length of the resulting stream will be the
    * minimum of the two iterators.</p>
    *
    * @param <T>       the component type of the first iterator
    * @param <U>       the component type of the second iterator
    * @param iterator1 the iterator making up the key in the resulting entries
    * @param iterator2 the iterator making up the value in the resulting entries
    * @return A stream of entries whose keys are taken from iterator1 and values are taken from iterator2
    */
   public static <T, U> Iterator<Map.Entry<T, U>> zip(final Iterator<? extends T> iterator1,
                                                      final Iterator<? extends U> iterator2
                                                     ) {
      return new ZippedIterator<>(notNull(iterator1), notNull(iterator2));
   }


   /**
    * Size int.
    *
    * @param iterator the iterator
    * @return the int
    */
   public static int size(Iterator<?> iterator) {
      return (int) Streams.asStream(notNull(iterator)).count();
   }


   private static class PartitionedIterator<E> implements Iterator<List<E>> {
      private final Iterator<? extends E> backing;
      private final int partitionSize;
      private final boolean pad;

      private PartitionedIterator(Iterator<? extends E> backing, int partitionSize, boolean pad) {
         this.backing = backing;
         this.partitionSize = partitionSize;
         this.pad = pad;
      }

      @Override
      public boolean hasNext() {
         return backing.hasNext();
      }

      @Override
      public List<E> next() {
         Validation.checkState(backing.hasNext());
         List<E> nextList = new ArrayList<>();
         while (backing.hasNext() && nextList.size() < partitionSize) {
            nextList.add(backing.next());
         }
         if (pad) {
            while (nextList.size() < partitionSize) {
               nextList.add(null);
            }
         }
         return Collections.unmodifiableList(nextList);
      }
   }

   private static class ZippedIterator<T, U> implements Iterator<Map.Entry<T, U>> {
      private final Iterator<? extends T> iterator1;
      private final Iterator<? extends U> iterator2;

      private ZippedIterator(Iterator<? extends T> iterator1, Iterator<? extends U> iterator2) {
         this.iterator1 = iterator1;
         this.iterator2 = iterator2;
      }

      @Override
      public boolean hasNext() {
         return iterator1.hasNext() && iterator2.hasNext();
      }

      @Override
      public Map.Entry<T, U> next() {
         return $(iterator1.next(), iterator2.next());
      }

      @Override
      public void remove() {
         iterator1.remove();
         iterator2.remove();
      }
   }

   private static class FilteredIterator<E> implements Iterator<E> {
      private final Iterator<? extends E> backing;
      private final SerializablePredicate<? super E> filter;
      private E next = null;
      private boolean canRemove = false;

      private FilteredIterator(Iterator<? extends E> backing, SerializablePredicate<? super E> filter) {
         this.backing = backing;
         this.filter = filter;
      }

      private boolean advance() {
         while (next == null && backing.hasNext()) {
            next = backing.next();
            canRemove = false;
            if (!filter.test(next)) {
               next = null;
            }
         }
         return next != null;
      }

      @Override
      public boolean hasNext() {
         return advance();
      }

      @Override
      public E next() {
         E toReturn = validate(advance(), NoSuchElementException::new, next);
         next = null;
         canRemove = true;
         return toReturn;
      }

      @Override
      public void remove() {
         validate(canRemove, IllegalStateException::new, true);
         backing.remove();
      }
   }

   private static class TransformedIterator<I, O> implements Iterator<O> {
      private final Iterator<? extends I> backing;
      private final SerializableFunction<? super I, ? extends O> transform;

      private TransformedIterator(Iterator<? extends I> backing, SerializableFunction<? super I, ? extends O> transform) {
         this.backing = backing;
         this.transform = transform;
      }

      @Override
      public boolean hasNext() {
         return backing.hasNext();
      }

      @Override
      public O next() {
         return transform.apply(backing.next());
      }

      @Override
      public void remove() {
         backing.remove();
      }
   }

   private static class ConcatIterator<E> implements Iterator<E> {
      private final Iterator<Iterator<? extends E>> iterators;
      private Iterator<? extends E> current = null;

      private ConcatIterator(Iterator<Iterator<? extends E>> iterators) {
         this.iterators = iterators;
      }

      private boolean advance() {
         if (current != null && current.hasNext()) {
            return true;
         }
         while (iterators.hasNext()) {
            current = iterators.next();
            if (current.hasNext()) {
               return true;
            }
         }
         return false;
      }

      @Override
      public boolean hasNext() {
         return advance();
      }

      @Override
      public E next() {
         validate(advance(), NoSuchElementException::new, null);
         return current.next();
      }

      @Override
      public void remove() {
         current.remove();
      }
   }

}//END OF Iterators
