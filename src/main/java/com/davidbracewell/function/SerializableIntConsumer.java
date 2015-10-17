
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface SerializableIntConsumer extends IntConsumer, Serializable {

}//END OF SerializableIntConsumer
