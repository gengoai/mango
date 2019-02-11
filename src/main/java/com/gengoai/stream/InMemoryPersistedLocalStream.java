package com.gengoai.stream;

import com.gengoai.Validation;
import com.gengoai.collection.Lists;
import com.gengoai.function.SerializableRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <p>A reusable non-distributed stream backed by a collection.</p>
 *
 * @param <T> the component type of the stream
 * @author David B. Bracewell
 */
public class InMemoryPersistedLocalStream<T> extends LazyLocalStream<T> {
   private final List<T> backingCollection;
   private SerializableRunnable onClose;
   private boolean parallel = false;


   /**
    * Instantiates a new Reusable local stream backed by the the given collection.
    *
    * @param backingCollection the backing collection
    */
   public InMemoryPersistedLocalStream(Collection<T> backingCollection) {
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
   public MStream<Stream<T>> partition(long partitionSize) {
      int numPartitions = (int) (backingCollection.size() / partitionSize);
      return new LocalStream<>(() -> IntStream.range(0, numPartitions)
                                              .mapToObj(i -> backingCollection.subList(i,
                                                                                       (int) Math.min(i + partitionSize,
                                                                                                      backingCollection.size()))
                                                                              .stream()),
                               CacheStrategy.InMemory);
   }

   @Override
   public long count() {
      return backingCollection.size();
   }


   @Override
   public StreamingContext getContext() {
      return LocalStreamingContext.INSTANCE;
   }

   @Override
   protected MStream<T> getLocalStream() {
      return new LocalStream<>(backingCollection::stream, CacheStrategy.InMemory);
   }


   @Override
   public Iterator<T> iterator() {
      return backingCollection.iterator();
   }

   @Override
   public Stream<T> javaStream() {
      if (parallel) {
         return backingCollection.parallelStream().onClose(onClose);
      }
      return backingCollection.stream().onClose(onClose);
   }

   @Override
   public MStream<T> onClose(SerializableRunnable closeHandler) {
      if (onClose == null) {
         this.onClose = closeHandler;
      } else if (closeHandler != null) {
         this.onClose = SerializableRunnable.chain(onClose, closeHandler);
      }
      return this;
   }

   @Override
   public MStream<T> parallel() {
      this.parallel = true;
      return this;
   }


   @Override
   public MStream<T> sample(boolean withReplacement, int number) {
      Validation.checkArgument(number >= 0, "Sample size must be non-negative.");
      if (number == 0) {
         return StreamingContext.local().empty();
      }
      if (withReplacement) {
         return new InMemoryPersistedLocalStream<>(Lists.sampleWithReplacement(backingCollection, number));
      }
      return getLocalStream().sample(false, number);
   }

}// END OF ReusableLocalStream
