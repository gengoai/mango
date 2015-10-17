
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.IntFunction;

@FunctionalInterface
public interface SerializableIntFunction<R> extends IntFunction<R>, Serializable {

}//END OF SerializableIntFunction
