
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ToDoubleBiFunction;

@FunctionalInterface
public interface CheckedToDoubleBiFunction<T,U> extends Serializable {

	double applyAsDouble(T t, U u) throws Throwable;

}//END OF CheckedToDoubleBiFunction
