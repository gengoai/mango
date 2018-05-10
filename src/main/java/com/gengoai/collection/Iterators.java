package com.gengoai.collection;

import com.gengoai.Validation;
import com.gengoai.tuple.Tuple2;
import lombok.NonNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.gengoai.collection.Streams.asStream;

/**
 * @author David B. Bracewell
 */
public final class Iterators {
   private Iterators() {
      throw new IllegalAccessError();
   }

   public static <IN, OUT> Iterator<OUT> transform(@NonNull final Iterator<IN> iterator,
                                                   @NonNull final Function<? super IN, ? extends OUT> function
                                                  ) {
      return new Iterator<OUT>() {
         @Override
         public boolean hasNext() {
            return iterator.hasNext();
         }

         @Override
         public OUT next() {
            return function.apply(iterator.next());
         }

         @Override
         public void remove() {
            iterator.remove();
         }
      };
   }

   public static <T> Iterator<T> unmodifiableIterator(@NonNull final Iterator<T> iterator) {
      return new Iterator<T>() {
         @Override
         public boolean hasNext() {
            return iterator.hasNext();
         }

         @Override
         public T next() {
            return iterator.next();
         }
      };
   }

   public static <T> Iterator<List<T>> partition(@NonNull final Iterator<T> iterator, int partitionSize) {
      return partition(iterator, partitionSize, false);
   }

   public static <T> Iterator<List<T>> partition(@NonNull final Iterator<T> iterator, int partitionSize, boolean pad) {
      Validation.checkArgument(partitionSize > 0, "Partition size must be greater than zero.");
      return new Iterator<List<T>>() {
         @Override
         public boolean hasNext() {
            return iterator.hasNext();
         }

         @Override
         public List<T> next() {
            Validation.checkState(iterator.hasNext());
            List<T> nextList = new ArrayList<>();
            while (iterator.hasNext() && nextList.size() < partitionSize) {
               nextList.add(iterator.next());
            }
            if (pad) {
               while (nextList.size() < partitionSize) {
                  nextList.add(null);
               }
            }
            return Collections.unmodifiableList(nextList);
         }
      };
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
   public static <T, U> Stream<Map.Entry<T, U>> zip(@NonNull final Iterator<? extends T> iterator1, @NonNull final Iterator<? extends U> iterator2) {
      return asStream(new Iterator<Map.Entry<T, U>>() {
         @Override
         public boolean hasNext() {
            return iterator1.hasNext() && iterator2.hasNext();
         }

         @Override
         public Map.Entry<T, U> next() {
            if (!iterator1.hasNext() || !iterator2.hasNext()) {
               throw new NoSuchElementException();
            }
            return new Tuple2<>(iterator1.next(), iterator2.next());
         }
      });
   }
}//END OF Iterators
