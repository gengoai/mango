
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ObjIntConsumer;

@FunctionalInterface
public interface CheckedObjIntConsumer<T> extends Serializable {

	void accept(T t, int value) throws Throwable;

}//END OF CheckedObjIntConsumer
