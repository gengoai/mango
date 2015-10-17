
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface SerializableIntToDoubleFunction extends IntToDoubleFunction, Serializable {

}//END OF SerializableIntToDoubleFunction
