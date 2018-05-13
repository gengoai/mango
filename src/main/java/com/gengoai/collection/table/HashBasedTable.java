package com.gengoai.collection.table;

import com.gengoai.collection.Iterators;
import com.gengoai.collection.Streams;
import com.gengoai.conversion.Cast;
import lombok.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.gengoai.tuple.Tuples.$;

/**
 * The type Base table.
 *
 * @param <R> the type parameter
 * @param <C> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class HashBasedTable<R, C, V> implements Table<R, C, V>, Serializable {

   private final Map<R, Map<C, V>> map = new HashMap<>();

   @Override
   public V get(R row, C column) {
      return map.containsKey(row) ? map.get(row).get(column) : null;
   }

   @Override
   public V put(R row, C column, V value) {
      createRowIfNeeded(row);
      return map.get(row).put(column, value);
   }

   @Override
   public V remove(R row, C column) {
      if (map.containsKey(row)) {
         V value = map.get(row).remove(column);
         deleteRowIfEmpty(row);
         return value;
      }
      return null;
   }

   @Override
   public Map<R, V> removeColumn(C column) {
      Map<R, V> rval = new HashMap<>();
      for (R row : map.keySet()) {
         rval.put(row, map.get(row).remove(column));
      }
      map.keySet().removeIf(row -> map.get(row).isEmpty());
      return rval;
   }

   @Override
   public Map<C, V> removeRow(R row) {
      return map.remove(row);
   }

   @Override
   public Map<R, V> column(C column) {
      return new ColumnView(column);
   }

   @Override
   public Map<C, V> row(@NonNull R row) {
      return new RowView(row);
   }

   @Override
   public boolean containsColumn(C column) {
      return map.keySet().stream().anyMatch(row -> map.get(row).containsKey(column));
   }

   @Override
   public boolean containsRow(R row) {
      return map.containsKey(row);
   }

   @Override
   public boolean contains(R row, C column) {
      return map.containsKey(row) && map.get(row).containsKey(column);
   }

   @Override
   public int size() {
      return map.values().stream().mapToInt(Map::size).sum();
   }

   @Override
   public Collection<V> values() {
      return map.values().stream().flatMap(map -> map.values().stream()).collect(Collectors.toSet());
   }

   @Override
   public Set<C> columnKeySet() {
      return map.values().stream().flatMap(map -> map.keySet().stream()).collect(Collectors.toSet());
   }

   @Override
   public Set<R> rowKeySet() {
      return map.keySet();
   }

   private void createRowIfNeeded(R row) {
      if (!map.containsKey(row)) {
         map.put(row, new HashMap<>());
      }
   }

   private void deleteRowIfEmpty(R row) {
      if (map.get(row).isEmpty()) {
         map.remove(row);
      }
   }


   private class RowView extends AbstractMap<C, V> {
      private final R row;

      private RowView(R row) {
         this.row = row;
      }

      @Override
      public int size() {
         return map.containsKey(row) ? map.get(row).size() : 0;
      }

      @Override
      public Set<Entry<C, V>> entrySet() {
         return map.containsKey(row) ? map.get(row).entrySet() : Collections.emptySet();
      }

      @Override
      public V get(Object key) {
         return HashBasedTable.this.get(row, Cast.as(key));
      }

      @Override
      public V put(C key, V value) {
         return HashBasedTable.this.put(row, key, value);
      }

      @Override
      public V remove(Object key) {
         return HashBasedTable.this.remove(row, Cast.as(key));
      }

      @Override
      public void clear() {
         map.remove(row);
      }
   }

   private class ColumnView extends AbstractMap<R, V> {
      private final C column;

      private ColumnView(C column) {
         this.column = column;
      }


      @Override
      public Set<Entry<R, V>> entrySet() {
         return new AbstractSet<Entry<R, V>>() {
            @Override
            public Iterator<Entry<R, V>> iterator() {
               return Iterators.transform(rowKeySet().stream()
                                                     .filter(row -> map.get(row).containsKey(column))
                                                     .iterator(),
                                          row -> $(row, HashBasedTable.this.get(row, column)));
            }

            @Override
            public int size() {
               return (int) Streams.asStream(iterator()).count();
            }
         };
      }


      @Override
      public V get(Object key) {
         return HashBasedTable.this.get(Cast.as(key), column);
      }

      @Override
      public V put(R key, V value) {
         return HashBasedTable.this.put(key, column, value);
      }

      @Override
      public V remove(Object key) {
         return HashBasedTable.this.remove(Cast.as(key), column);
      }

      @Override
      public void clear() {
         HashBasedTable.this.removeColumn(column);
      }
   }


   @Override
   public void clear() {
      map.clear();
   }
}//END OF BaseTable
