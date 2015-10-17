
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.Consumer;

@FunctionalInterface
public interface SerializableConsumer<T> extends Consumer<T>, Serializable {

}//END OF SerializableConsumer
