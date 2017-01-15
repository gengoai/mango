package com.davidbracewell.collection.map;

import com.davidbracewell.collection.MapDBSpec;
import com.davidbracewell.io.Commitable;
import com.davidbracewell.reflection.Reflect;
import com.davidbracewell.reflection.ReflectionException;
import com.google.common.base.Throwables;
import com.google.common.collect.ForwardingMap;
import lombok.NonNull;
import org.mapdb.DB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * @author David B. Bracewell
 */
public class OffHeapMap<K, V> extends ForwardingMap<K, V> implements AutoCloseable, Serializable, Commitable {

   private static final long serialVersionUID = 1L;
   private final MapDBSpec spec;
   private final transient Map<K, V> wrapped;
   private final transient DB database;


   /**
    * Constructs an instance of a OffHeapMap according to the given {@link MapDBSpec}.
    *
    * @param spec The specification for the backend storage
    */
   public OffHeapMap(@NonNull MapDBSpec spec) {
      this.spec = new MapDBSpec(spec);
      this.database = spec.createDB();
      this.wrapped = this.database.getHashMap(spec.getDatabaseName());
   }


   @Override
   protected Map<K, V> delegate() {
      return wrapped;
   }


   /**
    * Gets the database backing the map.
    *
    * @return The database
    */
   public DB getDatabase() {
      return database;
   }


   @Override
   public void close() throws Exception {
      if (!database.isClosed()) {
         database.commit();
         database.close();
      }
   }

   @Override
   public void commit() {
      database.commit();
   }

   private void readObject(ObjectInputStream o)
      throws IOException, ClassNotFoundException {
      o.defaultReadObject();
      Reflect r = Reflect.onObject(this).allowPrivilegedAccess();
      try {
         r.set("database", spec.createDB());
         r.set("wrapped", database.getHashMap(spec.getDatabaseName()));
      } catch (ReflectionException e) {
         throw Throwables.propagate(e);
      }
   }


}//END OF OffHeapMap
