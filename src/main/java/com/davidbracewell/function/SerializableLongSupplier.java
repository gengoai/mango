
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.LongSupplier;

@FunctionalInterface
public interface SerializableLongSupplier extends LongSupplier, Serializable {

}//END OF SerializableLongSupplier
