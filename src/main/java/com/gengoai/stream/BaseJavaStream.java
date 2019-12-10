/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.gengoai.stream;

import com.gengoai.Validation;
import com.gengoai.function.SerializableBinaryOperator;
import com.gengoai.function.SerializableComparator;
import com.gengoai.function.SerializableConsumer;
import com.gengoai.function.Unchecked;
import com.gengoai.io.resource.Resource;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * The type Base java stream.
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
abstract class BaseJavaStream<T> implements MStream<T>, Serializable {
   private static final long serialVersionUID = 1L;

   @Override
   public final <R> R collect(Collector<? super T, ?, R> collector) {
      return javaStream().collect(collector);
   }

   @Override
   public final List<T> collect() {
      return javaStream().collect(Collectors.toList());
   }

   @Override
   public long count() {
      return javaStream().count();
   }

   @Override
   public final Map<T, Long> countByValue() {
      return javaStream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
   }

   @Override
   public final Optional<T> first() {
      return javaStream().findFirst();
   }

   @Override
   public final T fold(T zeroValue, SerializableBinaryOperator<T> operator) {
      return javaStream().reduce(zeroValue, operator);
   }

   @Override
   public final void forEach(SerializableConsumer<? super T> consumer) {
      javaStream().forEach(consumer);
   }

   @Override
   public final void forEachLocal(SerializableConsumer<? super T> consumer) {
      javaStream().forEach(consumer);
   }

   @Override
   public final boolean isDistributed() {
      return false;
   }

   @Override
   public final boolean isEmpty() {
      return count() == 0;
   }

   @Override
   public Iterator<T> iterator() {
      return javaStream().iterator();
   }

   @Override
   public final Optional<T> max(SerializableComparator<? super T> comparator) {
      return javaStream().max(comparator);
   }

   @Override
   public final Optional<T> min(SerializableComparator<? super T> comparator) {
      return javaStream().min(comparator);
   }

   @Override
   public MStream<T> persist(StorageLevel storageLevel) {
      switch (storageLevel) {
         case InMemory:
            return new InMemoryPersistedLocalStream<>(javaStream().collect(Collectors.toList()));
         case OnDisk:
         case OffHeap:
            return new OnDiskPersistedLocalStream<>(javaStream());
      }
      throw new IllegalArgumentException();
   }

   @Override
   public final Optional<T> reduce(SerializableBinaryOperator<T> reducer) {
      return javaStream().reduce(reducer);
   }


   @Override
   public final void saveAsTextFile(Resource location) {
      try (BufferedWriter writer = new BufferedWriter(location.writer())) {
         javaStream().forEach(Unchecked.consumer(o -> {
            writer.write(o.toString());
            writer.newLine();
         }));
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public final List<T> take(int n) {
      Validation.checkArgument(n >= 0, "N must be non-negative.");
      if (n == 0) {
         return Collections.emptyList();
      }
      return javaStream().limit(n).collect(Collectors.toList());
   }


}//END OF BaseJavaStream
