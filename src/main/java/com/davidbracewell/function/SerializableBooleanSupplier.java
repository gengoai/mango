
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.BooleanSupplier;

@FunctionalInterface
public interface SerializableBooleanSupplier extends BooleanSupplier, Serializable {

}//END OF SerializableBooleanSupplier
