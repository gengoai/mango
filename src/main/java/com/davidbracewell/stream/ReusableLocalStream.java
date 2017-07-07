package com.davidbracewell.stream;

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.*;
import com.davidbracewell.io.resource.Resource;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * <p>A reusable non-distributed stream backed by a collection.</p>
 *
 * @param <T> the component type of the stream
 * @author David B. Bracewell
 */
public class ReusableLocalStream<T> implements MStream<T> {
   private final List<T> backingCollection;
   private SerializableRunnable onClose;
   private boolean parallel = false;

   /**
    * Instantiates a new Reusable local stream backed by the the given collection.
    *
    * @param backingCollection the backing collection
    */
   public ReusableLocalStream(@NonNull Collection<T> backingCollection) {
      this.backingCollection = new ArrayList<>(backingCollection);
   }

   @Override
   public MStream<T> cache() {
      return this;
   }

   @Override
   public void close() throws IOException {
      if (onClose != null) {
         onClose.run();
      }
      try {
         this.backingCollection.clear();
      } catch (UnsupportedOperationException uoe) {
         //noopt
      }
   }

   @Override
   public <R> R collect(@NonNull Collector<? super T, T, R> collector) {
      return getStream().collect(collector);
   }

   @Override
   public List<T> collect() {
      return getStream().collect();
   }

   @Override
   public long count() {
      return backingCollection.size();
   }

   @Override
   public Map<T, Long> countByValue() {
      return getStream().countByValue();
   }

   @Override
   public MStream<T> distinct() {
      return getStream().distinct();
   }

   @Override
   public MStream<T> filter(@NonNull SerializablePredicate<? super T> predicate) {
      return getStream().filter(predicate);
   }

   @Override
   public Optional<T> first() {
      return getStream().first();
   }

   @Override
   public <R> MStream<R> flatMap(@NonNull SerializableFunction<? super T, Stream<? extends R>> mapper) {
      return getStream().flatMap(mapper);
   }

   @Override
   public <R, U> MPairStream<R, U> flatMapToPair(@NonNull SerializableFunction<? super T, Stream<? extends Map.Entry<? extends R, ? extends U>>> function) {
      return getStream().flatMapToPair(function);
   }

   @Override
   public T fold(T zeroValue, @NonNull SerializableBinaryOperator<T> operator) {
      return getStream().fold(zeroValue, operator);
   }

   @Override
   public void forEach(@NonNull SerializableConsumer<? super T> consumer) {
      backingCollection.forEach(consumer);
   }

   @Override
   public void forEachLocal(@NonNull SerializableConsumer<? super T> consumer) {
      backingCollection.forEach(consumer);
   }

   @Override
   public StreamingContext getContext() {
      return LocalStreamingContext.INSTANCE;
   }

   @Override
   public SerializableRunnable getOnCloseHandler() {
      return onClose;
   }

   private MStream<T> getStream() {
      MStream<T> stream = new LocalStream<>(backingCollection.stream());
      if (parallel) {
         stream = stream.parallel();
      }
      return stream;
   }

   @Override
   public <U> MPairStream<U, Iterable<T>> groupBy(@NonNull SerializableFunction<? super T, ? extends U> function) {
      return getStream().groupBy(function);
   }

   @Override
   public boolean isEmpty() {
      return backingCollection.isEmpty();
   }

   @Override
   public boolean isReusable() {
      return true;
   }

   @Override
   public Iterator<T> iterator() {
      return backingCollection.iterator();
   }

   @Override
   public MStream<T> limit(long number) {
      return getStream().limit(number);
   }

   @Override
   public <R> MStream<R> map(@NonNull SerializableFunction<? super T, ? extends R> function) {
      return getStream().map(function);
   }

   @Override
   public MDoubleStream mapToDouble(@NonNull SerializableToDoubleFunction<? super T> function) {
      return getStream().mapToDouble(function);
   }

   @Override
   public <R, U> MPairStream<R, U> mapToPair(@NonNull SerializableFunction<? super T, ? extends Map.Entry<? extends R, ? extends U>> function) {
      return getStream().mapToPair(function);
   }

   @Override
   public Optional<T> max(@NonNull SerializableComparator<? super T> comparator) {
      return getStream().max(comparator);
   }

   @Override
   public Optional<T> min(@NonNull SerializableComparator<? super T> comparator) {
      return getStream().min(comparator);
   }

   @Override
   public void onClose(SerializableRunnable closeHandler) {
      this.onClose = closeHandler;
   }

   @Override
   public MStream<T> parallel() {
      this.parallel = true;
      return this;
   }

   @Override
   public MStream<Iterable<T>> partition(long partitionSize) {
      Preconditions.checkArgument(partitionSize > 0, "Number of partitions must be greater than zero.");
      return new ReusableLocalStream<>(Cast.cast(Lists.partition(backingCollection, (int) partitionSize)));
   }

   @Override
   public Optional<T> reduce(@NonNull SerializableBinaryOperator<T> reducer) {
      return getStream().reduce(reducer);
   }

   @Override
   public MStream<T> repartition(int numPartitions) {
      return this;
   }

   @Override
   public MStream<T> sample(boolean withReplacement, int number) {
      Preconditions.checkArgument(number >= 0, "Sample size must be non-negative.");
      if (number == 0) {
         return StreamingContext.local().empty();
      }
      Random random = new Random();
      if (withReplacement) {
         List<T> sample = new ArrayList<>();
         while (sample.size() < number) {
            sample.add(backingCollection.get(random.nextInt(backingCollection.size())));
         }
         return new ReusableLocalStream<>(sample);
      }
      return getStream().sample(false, number);
   }

   @Override
   public void saveAsTextFile(Resource location) {
      getStream().saveAsTextFile(location);
   }

   @Override
   public MStream<T> shuffle(Random random) {
      ReusableLocalStream<T> newStream = new ReusableLocalStream<>(new ArrayList<>(backingCollection));
      Collections.shuffle(newStream.backingCollection);
      return newStream;
   }

   @Override
   public MStream<T> skip(long n) {
      return getStream().skip(n);
   }

   @Override
   public MStream<T> sorted(boolean ascending) {
      return getStream().sorted(ascending);
   }

   @Override
   public <R extends Comparable<R>> MStream<T> sorted(boolean ascending, @NonNull SerializableFunction<? super T, ? extends R> keyFunction) {
      return getStream().sorted(ascending, keyFunction);
   }

   @Override
   public MStream<Iterable<T>> split(int n) {
      Preconditions.checkArgument(n > 0, "N must be greater than zero.");
      final int pSize = backingCollection.size() / n;
      List<Iterable<T>> partitions = new ArrayList<>();
      for (int i = 0; i < n; i++) {
         int start = i * pSize;
         int end = Math.min(start + pSize, backingCollection.size());
         if (i + 1 == n) {
            end = Math.max(end, backingCollection.size());
         }
         partitions.add(backingCollection.subList(start, end));
      }
      ReusableLocalStream<Iterable<T>> stream = new ReusableLocalStream<>(partitions);
      if (parallel) {
         stream.parallel = true;
      }
      return stream;
   }

   @Override
   public List<T> take(int n) {
      return getStream().take(n);
   }

   @Override
   public MStream<T> union(@NonNull MStream<T> other) {
      if (other.isReusable() && other.isEmpty()) {
         return this;
      } else if (this.isEmpty()) {
         return other;
      } else if (other instanceof ReusableLocalStream || other instanceof LocalStream) {
         return getStream().union(other).cache();
      }
      return getStream().union(other);
   }

   @Override
   public <U> MPairStream<T, U> zip(@NonNull MStream<U> other) {
      return getStream().zip(other);
   }

   @Override
   public MPairStream<T, Long> zipWithIndex() {
      return getStream().zipWithIndex();
   }

}// END OF ReusableLocalStream
