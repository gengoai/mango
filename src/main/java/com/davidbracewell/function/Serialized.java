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
 */

package com.davidbracewell.function;

import java.io.Serializable;
import java.util.function.*;

public interface Serialized {

  static Runnable runnable(SerializableRunnable runnable){ return runnable;}

  /**
   * Generates a serialized version of DoubleToIntFunction
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static DoubleToIntFunction doubleToIntFunction(SerializableDoubleToIntFunction serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of IntToDoubleFunction
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static IntToDoubleFunction intToDoubleFunction(SerializableIntToDoubleFunction serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of Consumer
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @return The serialized functional.
   */
  static <T> Consumer<T> consumer(SerializableConsumer<T> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of IntPredicate
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static IntPredicate intPredicate(SerializableIntPredicate serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of ObjLongConsumer
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @return The serialized functional.
   */
  static <T> ObjLongConsumer<T> objLongConsumer(SerializableObjLongConsumer<T> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of BiPredicate
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @param <U>        Functional parameter
   * @return The serialized functional.
   */
  static <T, U> BiPredicate<T, U> biPredicate(SerializableBiPredicate<T, U> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of DoubleUnaryOperator
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static DoubleUnaryOperator doubleUnaryOperator(SerializableDoubleUnaryOperator serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of IntUnaryOperator
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static IntUnaryOperator intUnaryOperator(SerializableIntUnaryOperator serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of LongUnaryOperator
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static LongUnaryOperator longUnaryOperator(SerializableLongUnaryOperator serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of BooleanSupplier
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static BooleanSupplier booleanSupplier(SerializableBooleanSupplier serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of IntSupplier
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static IntSupplier intSupplier(SerializableIntSupplier serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of IntBinaryOperator
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static IntBinaryOperator intBinaryOperator(SerializableIntBinaryOperator serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of ObjIntConsumer
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @return The serialized functional.
   */
  static <T> ObjIntConsumer<T> objIntConsumer(SerializableObjIntConsumer<T> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of LongBinaryOperator
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static LongBinaryOperator longBinaryOperator(SerializableLongBinaryOperator serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of UnaryOperator
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @return The serialized functional.
   */
  static <T> UnaryOperator<T> unaryOperator(SerializableUnaryOperator<T> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of BinaryOperator
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @return The serialized functional.
   */
  static <T> BinaryOperator<T> binaryOperator(SerializableBinaryOperator<T> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of Predicate
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @return The serialized functional.
   */
  static <T> Predicate<T> predicate(SerializablePredicate<T> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of ToDoubleFunction
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @return The serialized functional.
   */
  static <T> ToDoubleFunction<T> toDoubleFunction(SerializableToDoubleFunction<T> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of Supplier
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @return The serialized functional.
   */
  static <T> Supplier<T> supplier(SerializableSupplier<T> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of ToDoubleBiFunction
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @param <U>        Functional parameter
   * @return The serialized functional.
   */
  static <T, U> ToDoubleBiFunction<T, U> toDoubleBiFunction(SerializableToDoubleBiFunction<T, U> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of LongPredicate
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static LongPredicate longPredicate(SerializableLongPredicate serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of BiConsumer
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @param <U>        Functional parameter
   * @return The serialized functional.
   */
  static <T, U> BiConsumer<T, U> biConsumer(SerializableBiConsumer<T, U> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of LongSupplier
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static LongSupplier longSupplier(SerializableLongSupplier serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of ToLongFunction
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @return The serialized functional.
   */
  static <T> ToLongFunction<T> toLongFunction(SerializableToLongFunction<T> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of IntFunction
   *
   * @param serialized The serialized functional
   * @param <R>        Functional parameter
   * @return The serialized functional.
   */
  static <R> IntFunction<R> intFunction(SerializableIntFunction<R> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of IntConsumer
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static IntConsumer intConsumer(SerializableIntConsumer serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of BiFunction
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @param <U>        Functional parameter
   * @param <R>        Functional parameter
   * @return The serialized functional.
   */
  static <T, U, R> BiFunction<T, U, R> biFunction(SerializableBiFunction<T, U, R> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of LongToDoubleFunction
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static LongToDoubleFunction longToDoubleFunction(SerializableLongToDoubleFunction serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of DoubleBinaryOperator
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static DoubleBinaryOperator doubleBinaryOperator(SerializableDoubleBinaryOperator serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of LongFunction
   *
   * @param serialized The serialized functional
   * @param <R>        Functional parameter
   * @return The serialized functional.
   */
  static <R> LongFunction<R> longFunction(SerializableLongFunction<R> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of LongToIntFunction
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static LongToIntFunction longToIntFunction(SerializableLongToIntFunction serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of ToLongBiFunction
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @param <U>        Functional parameter
   * @return The serialized functional.
   */
  static <T, U> ToLongBiFunction<T, U> toLongBiFunction(SerializableToLongBiFunction<T, U> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of DoublePredicate
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static DoublePredicate doublePredicate(SerializableDoublePredicate serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of DoubleFunction
   *
   * @param serialized The serialized functional
   * @param <R>        Functional parameter
   * @return The serialized functional.
   */
  static <R> DoubleFunction<R> doubleFunction(SerializableDoubleFunction<R> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of LongConsumer
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static LongConsumer longConsumer(SerializableLongConsumer serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of Function
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @param <R>        Functional parameter
   * @return The serialized functional.
   */
  static <T, R> Function<T, R> function(SerializableFunction<T, R> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of DoubleToLongFunction
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static DoubleToLongFunction doubleToLongFunction(SerializableDoubleToLongFunction serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of DoubleConsumer
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static DoubleConsumer doubleConsumer(SerializableDoubleConsumer serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of IntToLongFunction
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static IntToLongFunction intToLongFunction(SerializableIntToLongFunction serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of ToIntFunction
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @return The serialized functional.
   */
  static <T> ToIntFunction<T> toIntFunction(SerializableToIntFunction<T> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of DoubleSupplier
   *
   * @param serialized The serialized functional
   * @return The serialized functional.
   */
  static DoubleSupplier doubleSupplier(SerializableDoubleSupplier serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of ToIntBiFunction
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @param <U>        Functional parameter
   * @return The serialized functional.
   */
  static <T, U> ToIntBiFunction<T, U> toIntBiFunction(SerializableToIntBiFunction<T, U> serialized) {
    return serialized;
  }


  /**
   * Generates a serialized version of ObjDoubleConsumer
   *
   * @param serialized The serialized functional
   * @param <T>        Functional parameter
   * @return The serialized functional.
   */
  static <T> ObjDoubleConsumer<T> objDoubleConsumer(SerializableObjDoubleConsumer<T> serialized) {
    return serialized;
  }


}//END OF Serialized