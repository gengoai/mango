
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ToDoubleBiFunction;

@FunctionalInterface
public interface SerializableToDoubleBiFunction<T,U> extends ToDoubleBiFunction<T,U>, Serializable {

}//END OF SerializableToDoubleBiFunction
