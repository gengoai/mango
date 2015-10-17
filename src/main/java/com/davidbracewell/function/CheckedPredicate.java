
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.Predicate;

@FunctionalInterface
public interface CheckedPredicate<T> extends Serializable {

	boolean test(T t) throws Throwable;

}//END OF CheckedPredicate
