package com.davidbracewell.function;

import com.google.common.base.Throwables;

/**
 * @author David B. Bracewell
 */
public interface Unchecked {

  default <T, U> SerializableBiConsumer<T, U> from(CheckedBiConsumer<T, U> checked) {
    return (t, u) -> {
      try {
        checked.accept(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T, U, R> SerializableBiFunction<T, U, R> from(CheckedBiFunction<T, U, R> checked) {
    return (t, u) -> {
      try {
        return checked.apply(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T> SerializableBinaryOperator<T> from(CheckedBinaryOperator<T> checked) {
    return (t, u) -> {
      try {
        return checked.apply(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T, U> SerializableBiPredicate<T, U> from(CheckedBiPredicate<T, U> checked) {
    return (t, u) -> {
      try {
        return checked.test(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableBooleanSupplier from(CheckedBooleanSupplier checked) {
    return () -> {
      try {
        return checked.getAsBoolean();
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T> SerializableConsumer<T> from(CheckedConsumer<T> checked) {
    return (t) -> {
      try {
        checked.accept(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableDoubleBinaryOperator from(CheckedDoubleBinaryOperator checked) {
    return (t, u) -> {
      try {
        return checked.apply(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableDoubleConsumer from(CheckedDoubleConsumer checked) {
    return (t) -> {
      try {
        checked.apply(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <R> SerializableDoubleFunction<R> from(CheckedDoubleFunction<R> checked) {
    return (r) -> {
      try {
        return checked.apply(r);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableDoublePredicate from(CheckedDoublePredicate checked) {
    return (t) -> {
      try {
        return checked.test(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableDoubleSupplier from(CheckedDoubleSupplier checked) {
    return () -> {
      try {
        return checked.getAsDouble();
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableDoubleToIntFunction from(CheckedDoubleToIntFunction checked) {
    return (t) -> {
      try {
        return checked.applyAsInt(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableDoubleToLongFunction from(CheckedDoubleToLongFunction checked) {
    return (t) -> {
      try {
        return checked.applyAsLong(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableDoubleUnaryOperator from(CheckedDoubleUnaryOperator checked) {
    return (t) -> {
      try {
        return checked.applyAsDouble(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T, R> SerializableFunction<T, R> from(CheckedFunction<T, R> checked) {
    return (t) -> {
      try {
        return checked.apply(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableIntBinaryOperator from(CheckedIntBinaryOperator checked) {
    return (t, u) -> {
      try {
        return checked.apply(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableIntConsumer from(CheckedIntConsumer checked) {
    return (t) -> {
      try {
        checked.apply(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <R> SerializableIntFunction<R> from(CheckedIntFunction<R> checked) {
    return (r) -> {
      try {
        return checked.apply(r);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableIntPredicate from(CheckedIntPredicate checked) {
    return (t) -> {
      try {
        return checked.test(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableIntSupplier from(CheckedIntSupplier checked) {
    return () -> {
      try {
        return checked.getAsInt();
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableIntToDoubleFunction from(CheckedIntToDoubleFunction checked) {
    return (t) -> {
      try {
        return checked.applyAsDouble(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableIntToLongFunction from(CheckedIntToLongFunction checked) {
    return (t) -> {
      try {
        return checked.applyAsLong(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableIntUnaryOperator from(CheckedIntUnaryOperator checked) {
    return (t) -> {
      try {
        return checked.applyAsInt(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableLongBinaryOperator from(CheckedLongBinaryOperator checked) {
    return (t, u) -> {
      try {
        return checked.apply(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableLongConsumer from(CheckedLongConsumer checked) {
    return (t) -> {
      try {
        checked.apply(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <R> SerializableLongFunction<R> from(CheckedLongFunction<R> checked) {
    return (r) -> {
      try {
        return checked.apply(r);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableLongPredicate from(CheckedLongPredicate checked) {
    return (t) -> {
      try {
        return checked.test(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableLongSupplier from(CheckedLongSupplier checked) {
    return () -> {
      try {
        return checked.getAsLong();
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableLongToDoubleFunction from(CheckedLongToDoubleFunction checked) {
    return (t) -> {
      try {
        return checked.applyAsDouble(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableLongToIntFunction from(CheckedLongToIntFunction checked) {
    return (t) -> {
      try {
        return checked.applyAsInt(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default SerializableLongUnaryOperator from(CheckedLongUnaryOperator checked) {
    return (t) -> {
      try {
        return checked.applyAsLong(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T> SerializableObjDoubleConsumer<T> from(CheckedObjDoubleConsumer<T> checked) {
    return (t, value) -> {
      try {
        checked.accept(t, value);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T> SerializableObjIntConsumer<T> from(CheckedObjIntConsumer<T> checked) {
    return (t, value) -> {
      try {
        checked.accept(t, value);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T> SerializableObjLongConsumer<T> from(CheckedObjLongConsumer<T> checked) {
    return (t, value) -> {
      try {
        checked.accept(t, value);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T> SerializablePredicate<T> from(CheckedPredicate<T> checked) {
    return (t) -> {
      try {
        return checked.test(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T> SerializableSupplier<T> from(CheckedSupplier<T> checked) {
    return () -> {
      try {
        return checked.get();
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T, U> SerializableToDoubleBiFunction<T, U> from(CheckedToDoubleBiFunction<T, U> checked) {
    return (t, u) -> {
      try {
        return checked.applyAsDouble(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T> SerializableToDoubleFunction<T> from(CheckedToDoubleFunction<T> checked) {
    return (t) -> {
      try {
        return checked.applyAsDouble(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T, U> SerializableToIntBiFunction<T, U> from(CheckedToIntBiFunction<T, U> checked) {
    return (t, u) -> {
      try {
        return checked.applyAsInt(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T> SerializableToIntFunction<T> from(CheckedToIntFunction<T> checked) {
    return (t) -> {
      try {
        return checked.applyAsInt(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T, U> SerializableToLongBiFunction<T, U> from(CheckedToLongBiFunction<T, U> checked) {
    return (t, u) -> {
      try {
        return checked.applyAsLong(t, u);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T> SerializableToLongFunction<T> from(CheckedToLongFunction<T> checked) {
    return (t) -> {
      try {
        return checked.applyAsLong(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }

  default <T> SerializableUnaryOperator<T> from(CheckedUnaryOperator<T> checked) {
    return (t) -> {
      try {
        return checked.apply(t);
      } catch (Throwable e) {
        throw Throwables.propagate(e);
      }
    };
  }


}// END OF Unchecked
