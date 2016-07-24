package com.davidbracewell.stream;

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.SerializableBinaryOperator;
import com.davidbracewell.function.SerializableComparator;
import com.davidbracewell.function.SerializableConsumer;
import com.davidbracewell.function.SerializableFunction;
import com.davidbracewell.function.SerializablePredicate;
import com.davidbracewell.function.SerializableRunnable;
import com.davidbracewell.function.SerializableToDoubleFunction;
import com.davidbracewell.io.resource.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collector;

/**
 * @author David B. Bracewell
 */
public class ReusableLocalStream<T> implements MStream<T> {
  final Collection<T> backingCollection;
  SerializableRunnable onClose;

  public ReusableLocalStream(Collection<T> backingCollection) {
    this.backingCollection = backingCollection;
  }

  private MStream<T> getStream() {
    return new LocalStream<>(backingCollection.stream());
  }

  @Override
  public SerializableRunnable getOnCloseHandler() {
    return onClose;
  }

  @Override
  public MStream<T> filter(SerializablePredicate<? super T> predicate) {
    return getStream().filter(predicate);
  }

  @Override
  public <R> MStream<R> map(SerializableFunction<? super T, ? extends R> function) {
    return getStream().map(function);
  }

  @Override
  public <R> MStream<R> flatMap(SerializableFunction<? super T, Iterable<? extends R>> mapper) {
    return getStream().flatMap(mapper);
  }

  @Override
  public <R, U> MPairStream<R, U> flatMapToPair(SerializableFunction<? super T, ? extends Iterable<? extends Map.Entry<? extends R, ? extends U>>> function) {
    return getStream().flatMapToPair(function);
  }

  @Override
  public <R, U> MPairStream<R, U> mapToPair(SerializableFunction<? super T, ? extends Map.Entry<? extends R, ? extends U>> function) {
    return getStream().mapToPair(function);
  }

  @Override
  public <U> MPairStream<U, Iterable<T>> groupBy(SerializableFunction<? super T, ? extends U> function) {
    return getStream().groupBy(function);
  }

  @Override
  public <R> R collect(Collector<? super T, T, R> collector) {
    return getStream().collect(collector);
  }

  @Override
  public List<T> collect() {
    return getStream().collect();
  }

  @Override
  public Optional<T> reduce(SerializableBinaryOperator<T> reducer) {
    return getStream().reduce(reducer);
  }

  @Override
  public T fold(T zeroValue, SerializableBinaryOperator<T> operator) {
    return getStream().fold(zeroValue, operator);
  }

  @Override
  public void forEach(SerializableConsumer<? super T> consumer) {
    backingCollection.forEach(consumer);
  }

  @Override
  public void forEachLocal(SerializableConsumer<? super T> consumer) {
    backingCollection.forEach(consumer);
  }

  @Override
  public Iterator<T> iterator() {
    return backingCollection.iterator();
  }

  @Override
  public Optional<T> first() {
    return getStream().first();
  }

  @Override
  public MStream<T> sample(int number) {
    return getStream().sample(number);
  }

  @Override
  public long count() {
    return backingCollection.size();
  }

  @Override
  public boolean isEmpty() {
    return backingCollection.isEmpty();
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
  public MStream<T> limit(long number) {
    return getStream().limit(number);
  }

  @Override
  public List<T> take(int n) {
    return getStream().take(n);
  }

  @Override
  public MStream<T> skip(long n) {
    return getStream().skip(n);
  }

  @Override
  public void onClose(SerializableRunnable closeHandler) {
    this.onClose = closeHandler;
  }

  @Override
  public MStream<T> sorted(boolean ascending) {
    return getStream().sorted(ascending);
  }

  @Override
  public Optional<T> max(SerializableComparator<? super T> comparator) {
    return getStream().max(comparator);
  }

  @Override
  public Optional<T> min(SerializableComparator<? super T> comparator) {
    return getStream().min(comparator);
  }

  @Override
  public <U> MPairStream<T, U> zip(MStream<U> other) {
    return getStream().zip(other);
  }

  @Override
  public MPairStream<T, Long> zipWithIndex() {
    return getStream().zipWithIndex();
  }

  @Override
  public MDoubleStream mapToDouble(SerializableToDoubleFunction<? super T> function) {
    return getStream().mapToDouble(function);
  }

  @Override
  public MStream<T> cache() {
    return this;
  }

  @Override
  public MStream<T> union(MStream<T> other) {
    if (other == null) {
      return this;
    } else if (other instanceof ReusableLocalStream) {
      List<T> list = new ArrayList<>(backingCollection);
      list.addAll(Cast.<ReusableLocalStream<T>>as(other).backingCollection);
      return new ReusableLocalStream<>(list);
    }
    return getStream().union(other);
  }

  @Override
  public void saveAsTextFile(Resource location) {
    getStream().saveAsTextFile(location);
  }

  @Override
  public MStream<T> parallel() {
    return getStream().parallel();
  }

  @Override
  public MStream<T> shuffle(Random random) {
    return getStream().shuffle(random);
  }

  @Override
  public MStream<T> repartition(int numPartitions) {
    return this;
  }

  @Override
  public StreamingContext getContext() {
    return JavaStreamingContext.INSTANCE;
  }

  @Override
  public void close() throws IOException {
    if (onClose != null) {
      onClose.run();
    }
  }
}// END OF ReusableLocalStream
