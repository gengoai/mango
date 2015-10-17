
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.IntFunction;

@FunctionalInterface
public interface CheckedIntFunction<R> extends Serializable {

	R apply(int t) throws Throwable;

}//END OF CheckedIntFunction
