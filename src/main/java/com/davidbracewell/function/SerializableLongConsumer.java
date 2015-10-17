
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.LongConsumer;

@FunctionalInterface
public interface SerializableLongConsumer extends LongConsumer, Serializable {

}//END OF SerializableLongConsumer
