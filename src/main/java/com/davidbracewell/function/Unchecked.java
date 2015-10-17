package com.davidbracewell.function;

import com.google.common.base.Throwables;

import java.util.function.*;

/**
 * @author David B. Bracewell
 */
public interface Unchecked {


  static <T, U> BiConsumer<T, U> biConsumer(CheckedBiConsumer<T, U> checked) {
    return (t, u) -> {
      try {
        checked.accept(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T, U, R> BiFunction<T, U, R> biFunction(CheckedBiFunction<T, U, R> checked) {
    return (t, u) -> {
      try {
        return checked.apply(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T> BinaryOperator<T> binaryOperator(CheckedBinaryOperator<T> checked) {
    return (t, u) -> {
      try {
        return checked.apply(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T, U> BiPredicate<T, U> biPredicate(CheckedBiPredicate<T, U> checked) {
    return (t, u) -> {
      try {
        return checked.test(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static BooleanSupplier booleanSupplier(CheckedBooleanSupplier checked) {
    return () -> {
      try {
        return checked.getAsBoolean();
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T> Consumer<T> consumer(CheckedConsumer<T> checked) {
    return (t) -> {
      try {
        checked.accept(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static DoubleBinaryOperator doubleBinaryOperator(CheckedDoubleBinaryOperator checked) {
    return (t, u) -> {
      try {
        return checked.apply(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static DoubleConsumer doubleConsumer(CheckedDoubleConsumer checked) {
    return (t) -> {
      try {
        checked.apply(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <R> DoubleFunction<R> doubleFunction(CheckedDoubleFunction<R> checked) {
    return (r) -> {
      try {
        return checked.apply(r);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static DoublePredicate doublePredicate(CheckedDoublePredicate checked) {
    return (t) -> {
      try {
        return checked.test(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static DoubleSupplier doubleSupplier(CheckedDoubleSupplier checked) {
    return () -> {
      try {
        return checked.getAsDouble();
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static DoubleToIntFunction doubleToIntFunction(CheckedDoubleToIntFunction checked) {
    return (t) -> {
      try {
        return checked.applyAsInt(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static DoubleToLongFunction doubleToLongFunction(CheckedDoubleToLongFunction checked) {
    return (t) -> {
      try {
        return checked.applyAsLong(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static DoubleUnaryOperator doubleUnaryOperator(CheckedDoubleUnaryOperator checked) {
    return (t) -> {
      try {
        return checked.applyAsDouble(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T, R> Function<T, R> function(CheckedFunction<T, R> checked) {
    return (t) -> {
      try {
        return checked.apply(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static IntBinaryOperator intBinaryOperator(CheckedIntBinaryOperator checked) {
    return (t, u) -> {
      try {
        return checked.apply(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static IntConsumer intConsumer(CheckedIntConsumer checked) {
    return (t) -> {
      try {
        checked.apply(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <R> IntFunction<R> intFunction(CheckedIntFunction<R> checked) {
    return (r) -> {
      try {
        return checked.apply(r);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static IntPredicate intPredicate(CheckedIntPredicate checked) {
    return (t) -> {
      try {
        return checked.test(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static IntSupplier intSupplier(CheckedIntSupplier checked) {
    return () -> {
      try {
        return checked.getAsInt();
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static IntToDoubleFunction intToDoubleFunction(CheckedIntToDoubleFunction checked) {
    return (t) -> {
      try {
        return checked.applyAsDouble(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static IntToLongFunction intToLongFunction(CheckedIntToLongFunction checked) {
    return (t) -> {
      try {
        return checked.applyAsLong(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static IntUnaryOperator intUnaryOperator(CheckedIntUnaryOperator checked) {
    return (t) -> {
      try {
        return checked.applyAsInt(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static LongBinaryOperator longBinaryOperator(CheckedLongBinaryOperator checked) {
    return (t, u) -> {
      try {
        return checked.apply(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static LongConsumer longConsumer(CheckedLongConsumer checked) {
    return (t) -> {
      try {
        checked.apply(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <R> LongFunction<R> longFunction(CheckedLongFunction<R> checked) {
    return (r) -> {
      try {
        return checked.apply(r);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static LongPredicate longPredicate(CheckedLongPredicate checked) {
    return (t) -> {
      try {
        return checked.test(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static LongSupplier longSupplier(CheckedLongSupplier checked) {
    return () -> {
      try {
        return checked.getAsLong();
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static LongToDoubleFunction longToDoubleFunction(CheckedLongToDoubleFunction checked) {
    return (t) -> {
      try {
        return checked.applyAsDouble(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static LongToIntFunction longToIntFunction(CheckedLongToIntFunction checked) {
    return (t) -> {
      try {
        return checked.applyAsInt(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static LongUnaryOperator longUnaryOperator(CheckedLongUnaryOperator checked) {
    return (t) -> {
      try {
        return checked.applyAsLong(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T> ObjDoubleConsumer<T> objDoubleConsumer(CheckedObjDoubleConsumer<T> checked) {
    return (t, value) -> {
      try {
        checked.accept(t, value);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T> ObjIntConsumer<T> objIntConsumer(CheckedObjIntConsumer<T> checked) {
    return (t, value) -> {
      try {
        checked.accept(t, value);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T> ObjLongConsumer<T> objLongConsumer(CheckedObjLongConsumer<T> checked) {
    return (t, value) -> {
      try {
        checked.accept(t, value);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T> Predicate<T> predicate(CheckedPredicate<T> checked) {
    return (t) -> {
      try {
        return checked.test(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T> Supplier<T> supplier(CheckedSupplier<T> checked) {
    return () -> {
      try {
        return checked.get();
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T, U> ToDoubleBiFunction<T, U> toDoubleBiFunction(CheckedToDoubleBiFunction<T, U> checked) {
    return (t, u) -> {
      try {
        return checked.applyAsDouble(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T> ToDoubleFunction<T> toDoubleFunction(CheckedToDoubleFunction<T> checked) {
    return (t) -> {
      try {
        return checked.applyAsDouble(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T, U> ToIntBiFunction<T, U> toIntBiFunction(CheckedToIntBiFunction<T, U> checked) {
    return (t, u) -> {
      try {
        return checked.applyAsInt(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T> ToIntFunction<T> toIntFunction(CheckedToIntFunction<T> checked) {
    return (t) -> {
      try {
        return checked.applyAsInt(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static  <T, U> ToLongBiFunction<T, U> toLongBiFunction(CheckedToLongBiFunction<T, U> checked) {
    return (t, u) -> {
      try {
        return checked.applyAsLong(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T> ToLongFunction<T> toLongFunction(CheckedToLongFunction<T> checked) {
    return (t) -> {
      try {
        return checked.applyAsLong(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  static <T> UnaryOperator<T> unaryOperator(CheckedUnaryOperator<T> checked) {
    return (t) -> {
      try {
        return checked.apply(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


}// END OF Unchecked
