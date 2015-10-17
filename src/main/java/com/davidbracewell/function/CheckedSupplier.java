
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.Supplier;

@FunctionalInterface
public interface CheckedSupplier<T> extends Serializable {

	T get() throws Throwable;

}//END OF CheckedSupplier
