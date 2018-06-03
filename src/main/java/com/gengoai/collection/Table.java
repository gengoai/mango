package com.gengoai.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author David B. Bracewell
 */
public interface Table<R, C, V> {

   V get(R row, C column);

   V put(R row, C column, V value);

   V remove(R row, C column);

   Map<R, V> removeColumn(C column);

   Map<C, V> removeRow(R row);

   Map<R, V> column(C column);

   Map<C, V> row(R row);

   boolean containsColumn(C column);

   boolean containsRow(R row);

   boolean contains(R row, C column);

   int size();

   Collection<V> values();

   Set<C> columnKeySet();

   Set<R> rowKeySet();

   void clear();

}//END OF Table
