package com.gengoai.collection.multimap;

import java.util.List;

/**
 * Multimap which stores values in a list
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public abstract class ListMultimap<K, V> extends BaseMultimap<K, V, List<V>> {
   private static final long serialVersionUID = 1L;
}//END OF ListMultimap
