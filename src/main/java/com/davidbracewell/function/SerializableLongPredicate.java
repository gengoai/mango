
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.LongPredicate;

@FunctionalInterface
public interface SerializableLongPredicate extends LongPredicate, Serializable {

}//END OF SerializableLongPredicate
