
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.LongPredicate;

@FunctionalInterface
public interface CheckedLongPredicate extends Serializable {

	boolean test(long t) throws Throwable;

}//END OF CheckedLongPredicate
