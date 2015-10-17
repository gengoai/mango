
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.IntSupplier;

@FunctionalInterface
public interface SerializableIntSupplier extends IntSupplier, Serializable {

}//END OF SerializableIntSupplier
