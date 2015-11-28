package com.davidbracewell.function;

import java.io.Serializable;

/**
 * @author David B. Bracewell
 */
@FunctionalInterface
public interface SerializableRunnable extends Runnable, Serializable {
}
