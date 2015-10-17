
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.IntToLongFunction;

@FunctionalInterface
public interface SerializableIntToLongFunction extends IntToLongFunction, Serializable {

}//END OF SerializableIntToLongFunction
