package com.gengoai.collection.multimap;

import java.util.Set;

/**
 * Multimap which stores values in a set
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public abstract class SetMultimap<K, V> extends BaseMultimap<K, V, Set<V>> {
   private static final long serialVersionUID = 1L;
}//END OF ListMultimap
