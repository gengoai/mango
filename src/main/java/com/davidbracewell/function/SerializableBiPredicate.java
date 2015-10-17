
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.BiPredicate;

@FunctionalInterface
public interface SerializableBiPredicate<T,U> extends BiPredicate<T,U>, Serializable {

}//END OF SerializableBiPredicate
