
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ObjIntConsumer;

@FunctionalInterface
public interface SerializableObjIntConsumer<T> extends ObjIntConsumer<T>, Serializable {

}//END OF SerializableObjIntConsumer
