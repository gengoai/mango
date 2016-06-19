package com.davidbracewell;

import com.davidbracewell.function.SerializableSupplier;
import lombok.NonNull;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>Lazily create a value in a thread safe manner.</p>
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public final class Lazy<T> implements Serializable {
  private static final long serialVersionUID = 1L;
  private volatile AtomicReference<T> atomicReference = new AtomicReference<>(null);
  private volatile SerializableSupplier<? extends T> supplier;

  /**
   * Instantiates a new Lazy.
   *
   * @param supplier the supplier used to generate the value
   */
  public Lazy(@NonNull SerializableSupplier<? extends T> supplier) {
    this.supplier = supplier;
  }

  /**
   * Gets the value lazily
   *
   * @return the value
   */
  public T get() {
    T val = atomicReference.get();
    if (val == null) {
      atomicReference.compareAndSet(null, supplier.get());
      val = atomicReference.get();
    }
    return val;
  }

}// END OF Lazy
