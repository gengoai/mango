
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface CheckedFunction<T,R> extends Serializable {

	R apply(T t) throws Throwable;

}//END OF CheckedFunction
