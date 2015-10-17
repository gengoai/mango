
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.BiPredicate;

@FunctionalInterface
public interface CheckedBiPredicate<T,U> extends Serializable {

	boolean test(T t, U u) throws Throwable;

}//END OF CheckedBiPredicate
