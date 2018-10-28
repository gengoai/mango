package com.gengoai.collection;

import com.gengoai.json.JsonEntry;
import com.gengoai.json.JsonSerializable;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.gengoai.reflection.Types.getOrObject;

/**
 * A table is a two dimensional structure that associates a value with two keys (i.e. a row and column key). A table
 * maybe sparse, meaning not all cells contain values. Methods on a table that work with rows and columns return Map
 * views that when updated will be reflected in table.
 *
 * @param <R> the row type parameter
 * @param <C> the column type parameter
 * @param <V> the value type parameter
 * @author David B. Bracewell
 */
public interface Table<R, C, V> extends JsonSerializable {

   static <R, C, V> Table<R, C, V> fromJson(Table<R, C, V> table, JsonEntry entry, Type... params) {
      Type row = getOrObject(0, params);
      Type col = getOrObject(1, params);
      Type cell = getOrObject(2, params);
      Index<R> rowIndex = Indexes.indexOf(entry.getProperty("rowKeys").getAsArray(row));
      Index<C> colIndex = Indexes.indexOf(entry.getProperty("colKeys").getAsArray(col));
      entry.getProperty("cells").propertyIterator().forEachRemaining(rowEntry -> {
         R rowV = rowIndex.get(Integer.parseInt(rowEntry.getKey()));
         rowEntry.getValue().propertyIterator().forEachRemaining(colEntry -> {
            C colV = colIndex.get(Integer.parseInt(colEntry.getKey()));
            table.put(rowV, colV, colEntry.getValue().getAs(cell));
         });
      });
      return table;
   }

   static <R, C, V> Table<R, C, V> fromJson(JsonEntry entry, Type... params) {
      return fromJson(new HashBasedTable<>(), entry, params);
   }

   @Override
   default JsonEntry toJson() {
      JsonEntry table = JsonEntry.object();
      Index<R> rowIndex = Indexes.indexOf(rowKeySet());
      Index<C> colIndex = Indexes.indexOf(columnKeySet());
      table.addProperty("rowKeys", rowIndex);
      table.addProperty("colKeys", colIndex);
      JsonEntry cells = JsonEntry.object();
      rowKeySet().forEach(row -> {
         JsonEntry rowObj = JsonEntry.object();
         row(row).forEach((c, v) -> {
            int ci = colIndex.getId(c);
            rowObj.addProperty(Integer.toString(ci), v);
         });
         cells.addProperty(Integer.toString(rowIndex.getId(row)), rowObj);
      });
      table.addProperty("cells", cells);
      return table;
   }

   /**
    * Gets the value of the cell for the given row and column or null if not available.
    *
    * @param row    the row
    * @param column the column
    * @return the value of the cell at the given row and column or null
    */
   V get(R row, C column);

   /**
    * Sets the value of the cell at the given row and column
    *
    * @param row    the row
    * @param column the column
    * @param value  the value
    * @return the previous value
    */
   V put(R row, C column, V value);

   /**
    * Removes the value at the given cell.
    *
    * @param row    the row
    * @param column the column
    * @return the value of the cell
    */
   V remove(R row, C column);

   /**
    * Removes a column from the table
    *
    * @param column the column
    * @return Map containing row, value pairs
    */
   Map<R, V> removeColumn(C column);

   /**
    * Removes a row from the table
    *
    * @param row the row
    * @return Map containing column, value pairs
    */
   Map<C, V> removeRow(R row);

   /**
    * Returns a map view for a column in the table
    *
    * @param column the column
    * @return Map of row,value pairs
    */
   Map<R, V> column(C column);

   /**
    * Returns a map view for a row in the table
    *
    * @param row the row
    * @return Map of column,value pairs
    */
   Map<C, V> row(R row);

   /**
    * Checks if column key exists in the table
    *
    * @param column the column
    * @return True if it exists, False otherwise
    */
   boolean containsColumn(C column);

   /**
    * Checks if row key exists in the table
    *
    * @param row the row
    * @return True if it exists, False otherwise
    */
   boolean containsRow(R row);

   /**
    * Checks if a value exists for a given row and column
    *
    * @param row    the row
    * @param column the column
    * @return True if it exists, False otherwise
    */
   boolean contains(R row, C column);

   /**
    * The size in number of row and column mappings
    *
    * @return number of row and column mappings
    */
   int size();

   /**
    * Collection of cell values in the table
    *
    * @return the collection of cell values in the table
    */
   Collection<V> values();

   /**
    * Returns a set of column keys that have one or more values associated
    *
    * @return the set of column keys that have one or more values associated
    */
   Set<C> columnKeySet();

   /**
    * Returns a set of row keys that have one or more values associated
    *
    * @return the set of row keys that have one or more values associated
    */
   Set<R> rowKeySet();

   /**
    * Clears the table.
    */
   void clear();

}//END OF Table
