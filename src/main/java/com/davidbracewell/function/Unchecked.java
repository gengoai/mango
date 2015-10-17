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

import com.google.common.base.Throwables;

import java.io.Serializable;
import java.util.function.*;

public interface Unchecked {

  /**
   * Generates a version of DoubleToIntFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static DoubleToIntFunction doubleToIntFunction(CheckedDoubleToIntFunction checked) {
    return (Serializable & DoubleToIntFunction) (t) -> {
      try {
        return checked.applyAsInt(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of IntToDoubleFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static IntToDoubleFunction intToDoubleFunction(CheckedIntToDoubleFunction checked) {
    return (Serializable & IntToDoubleFunction) (t) -> {
      try {
        return checked.applyAsDouble(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of Consumer that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @return The checked functional.
   */
  static <T> Consumer<T> consumer(CheckedConsumer<T> checked) {
    return (Serializable & Consumer<T>) (t) -> {
      try {
        checked.accept(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of IntPredicate that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static IntPredicate intPredicate(CheckedIntPredicate checked) {
    return (Serializable & IntPredicate) (t) -> {
      try {
        return checked.test(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of ObjLongConsumer that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @return The checked functional.
   */
  static <T> ObjLongConsumer<T> objLongConsumer(CheckedObjLongConsumer<T> checked) {
    return (Serializable & ObjLongConsumer<T>) (t, value) -> {
      try {
        checked.accept(t, value);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of BiPredicate that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @param <U>     Functional parameter
   * @return The checked functional.
   */
  static <T, U> BiPredicate<T, U> biPredicate(CheckedBiPredicate<T, U> checked) {
    return (Serializable & BiPredicate<T, U>) (t, U) -> {
      try {
        return checked.test(t, U);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of DoubleUnaryOperator that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static DoubleUnaryOperator doubleUnaryOperator(CheckedDoubleUnaryOperator checked) {
    return (Serializable & DoubleUnaryOperator) (t) -> {
      try {
        return checked.applyAsDouble(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of IntUnaryOperator that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static IntUnaryOperator intUnaryOperator(CheckedIntUnaryOperator checked) {
    return (Serializable & IntUnaryOperator) (t) -> {
      try {
        return checked.applyAsInt(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of LongUnaryOperator that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static LongUnaryOperator longUnaryOperator(CheckedLongUnaryOperator checked) {
    return (Serializable & LongUnaryOperator) (t) -> {
      try {
        return checked.applyAsLong(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of BooleanSupplier that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static BooleanSupplier booleanSupplier(CheckedBooleanSupplier checked) {
    return (Serializable & BooleanSupplier) () -> {
      try {
        return checked.getAsBoolean();
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of IntSupplier that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static IntSupplier intSupplier(CheckedIntSupplier checked) {
    return (Serializable & IntSupplier) () -> {
      try {
        return checked.getAsInt();
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of IntBinaryOperator that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static IntBinaryOperator intBinaryOperator(CheckedIntBinaryOperator checked) {
    return (Serializable & IntBinaryOperator) (t, u) -> {
      try {
        return checked.applyAsInt(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of ObjIntConsumer that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @return The checked functional.
   */
  static <T> ObjIntConsumer<T> objIntConsumer(CheckedObjIntConsumer<T> checked) {
    return (Serializable & ObjIntConsumer<T>) (t, value) -> {
      try {
        checked.accept(t, value);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of LongBinaryOperator that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static LongBinaryOperator longBinaryOperator(CheckedLongBinaryOperator checked) {
    return (Serializable & LongBinaryOperator) (t, u) -> {
      try {
        return checked.applyAsLong(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of UnaryOperator that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @return The checked functional.
   */
  static <T> UnaryOperator<T> unaryOperator(CheckedUnaryOperator<T> checked) {
    return (Serializable & UnaryOperator<T>) (t) -> {
      try {
        return checked.apply(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of BinaryOperator that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @return The checked functional.
   */
  static <T> BinaryOperator<T> binaryOperator(CheckedBinaryOperator<T> checked) {
    return (Serializable & BinaryOperator<T>) (t, u) -> {
      try {
        return checked.apply(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of Predicate that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @return The checked functional.
   */
  static <T> Predicate<T> predicate(CheckedPredicate<T> checked) {
    return (Serializable & Predicate<T>) (t) -> {
      try {
        return checked.test(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of ToDoubleFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @return The checked functional.
   */
  static <T> ToDoubleFunction<T> toDoubleFunction(CheckedToDoubleFunction<T> checked) {
    return (Serializable & ToDoubleFunction<T>) (t) -> {
      try {
        return checked.applyAsDouble(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of Supplier that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @return The checked functional.
   */
  static <T> Supplier<T> supplier(CheckedSupplier<T> checked) {
    return (Serializable & Supplier<T>) () -> {
      try {
        return checked.get();
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of ToDoubleBiFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @param <U>     Functional parameter
   * @return The checked functional.
   */
  static <T, U> ToDoubleBiFunction<T, U> toDoubleBiFunction(CheckedToDoubleBiFunction<T, U> checked) {
    return (Serializable & ToDoubleBiFunction<T, U>) (t, u) -> {
      try {
        return checked.applyAsDouble(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of LongPredicate that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static LongPredicate longPredicate(CheckedLongPredicate checked) {
    return (Serializable & LongPredicate) (t) -> {
      try {
        return checked.test(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of BiConsumer that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @param <U>     Functional parameter
   * @return The checked functional.
   */
  static <T, U> BiConsumer<T, U> biConsumer(CheckedBiConsumer<T, U> checked) {
    return (Serializable & BiConsumer<T, U>) (t, u) -> {
      try {
        checked.accept(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of LongSupplier that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static LongSupplier longSupplier(CheckedLongSupplier checked) {
    return (Serializable & LongSupplier) () -> {
      try {
        return checked.getAsLong();
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of ToLongFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @return The checked functional.
   */
  static <T> ToLongFunction<T> toLongFunction(CheckedToLongFunction<T> checked) {
    return (Serializable & ToLongFunction<T>) (t) -> {
      try {
        return checked.applyAsLong(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of IntFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <R>     Functional parameter
   * @return The checked functional.
   */
  static <R> IntFunction<R> intFunction(CheckedIntFunction<R> checked) {
    return (Serializable & IntFunction<R>) (t) -> {
      try {
        return checked.apply(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of IntConsumer that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static IntConsumer intConsumer(CheckedIntConsumer checked) {
    return (Serializable & IntConsumer) (t) -> {
      try {
        checked.accept(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of BiFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @param <U>     Functional parameter
   * @param <R>     Functional parameter
   * @return The checked functional.
   */
  static <T, U, R> BiFunction<T, U, R> biFunction(CheckedBiFunction<T, U, R> checked) {
    return (Serializable & BiFunction<T, U, R>) (t, u) -> {
      try {
        return checked.apply(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of LongToDoubleFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static LongToDoubleFunction longToDoubleFunction(CheckedLongToDoubleFunction checked) {
    return (Serializable & LongToDoubleFunction) (t) -> {
      try {
        return checked.applyAsDouble(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of DoubleBinaryOperator that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static DoubleBinaryOperator doubleBinaryOperator(CheckedDoubleBinaryOperator checked) {
    return (Serializable & DoubleBinaryOperator) (t, u) -> {
      try {
        return checked.applyAsDouble(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of LongFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <R>     Functional parameter
   * @return The checked functional.
   */
  static <R> LongFunction<R> longFunction(CheckedLongFunction<R> checked) {
    return (Serializable & LongFunction<R>) (t) -> {
      try {
        return checked.apply(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of LongToIntFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static LongToIntFunction longToIntFunction(CheckedLongToIntFunction checked) {
    return (Serializable & LongToIntFunction) (t) -> {
      try {
        return checked.applyAsInt(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of ToLongBiFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @param <U>     Functional parameter
   * @return The checked functional.
   */
  static <T, U> ToLongBiFunction<T, U> toLongBiFunction(CheckedToLongBiFunction<T, U> checked) {
    return (Serializable & ToLongBiFunction<T, U>) (t, u) -> {
      try {
        return checked.applyAsLong(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of DoublePredicate that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static DoublePredicate doublePredicate(CheckedDoublePredicate checked) {
    return (Serializable & DoublePredicate) (t) -> {
      try {
        return checked.test(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of DoubleFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <R>     Functional parameter
   * @return The checked functional.
   */
  static <R> DoubleFunction<R> doubleFunction(CheckedDoubleFunction<R> checked) {
    return (Serializable & DoubleFunction<R>) (t) -> {
      try {
        return checked.apply(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of LongConsumer that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static LongConsumer longConsumer(CheckedLongConsumer checked) {
    return (Serializable & LongConsumer) (t) -> {
      try {
        checked.accept(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of Function that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @param <R>     Functional parameter
   * @return The checked functional.
   */
  static <T, R> Function<T, R> function(CheckedFunction<T, R> checked) {
    return (Serializable & Function<T, R>) (t) -> {
      try {
        return checked.apply(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of DoubleToLongFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static DoubleToLongFunction doubleToLongFunction(CheckedDoubleToLongFunction checked) {
    return (Serializable & DoubleToLongFunction) (t) -> {
      try {
        return checked.applyAsLong(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of DoubleConsumer that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static DoubleConsumer doubleConsumer(CheckedDoubleConsumer checked) {
    return (Serializable & DoubleConsumer) (t) -> {
      try {
        checked.accept(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of IntToLongFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static IntToLongFunction intToLongFunction(CheckedIntToLongFunction checked) {
    return (Serializable & IntToLongFunction) (t) -> {
      try {
        return checked.applyAsLong(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of ToIntFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @return The checked functional.
   */
  static <T> ToIntFunction<T> toIntFunction(CheckedToIntFunction<T> checked) {
    return (Serializable & ToIntFunction<T>) (t) -> {
      try {
        return checked.applyAsInt(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of DoubleSupplier that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @return The checked functional.
   */
  static DoubleSupplier doubleSupplier(CheckedDoubleSupplier checked) {
    return (Serializable & DoubleSupplier) () -> {
      try {
        return checked.getAsDouble();
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of ToIntBiFunction that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @param <U>     Functional parameter
   * @return The checked functional.
   */
  static <T, U> ToIntBiFunction<T, U> toIntBiFunction(CheckedToIntBiFunction<T, U> checked) {
    return (Serializable & ToIntBiFunction<T, U>) (t, u) -> {
      try {
        return checked.applyAsInt(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


  /**
   * Generates a version of ObjDoubleConsumer that will capture exceptions and rethrow them as runtime exceptions
   *
   * @param checked The checked functional
   * @param <T>     Functional parameter
   * @return The checked functional.
   */
  static <T> ObjDoubleConsumer<T> objDoubleConsumer(CheckedObjDoubleConsumer<T> checked) {
    return (Serializable & ObjDoubleConsumer<T>) (t, value) -> {
      try {
        checked.accept(t, value);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


}//END OF Unchecked