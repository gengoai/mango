package com.davidbracewell;

import com.davidbracewell.function.SerializableSupplier;
import lombok.NonNull;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * @author David B. Bracewell
 */
public final class Lazy<T> implements Serializable {
  private static final long serialVersionUID = 1L;
  private volatile T lazyObject = null;
  private AtomicBoolean constructed = new AtomicBoolean(false);
  private volatile SerializableSupplier<? extends T> supplier;

  public Lazy(@NonNull SerializableSupplier<? extends T> supplier) {
    this.supplier = supplier;
  }

  public T get() {
    if (!constructed.get()) {
      return compute(supplier);
    }
    return lazyObject;
  }


  private synchronized T compute(Supplier<? extends T> supplier) {
    if (!constructed.get() && lazyObject == null) {
      lazyObject = supplier.get();
    }
    constructed.set(true);
    return lazyObject;
  }


}// END OF Lazy
