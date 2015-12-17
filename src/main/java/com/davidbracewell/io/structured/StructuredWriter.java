package com.davidbracewell.io.structured;

import com.davidbracewell.DynamicEnum;
import com.davidbracewell.collection.Counter;
import com.davidbracewell.collection.MultiCounter;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;
import lombok.NonNull;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * The interface Structured writer.
 *
 * @author David B. Bracewell
 */
public interface StructuredWriter extends AutoCloseable {

  StructuredWriter beginDocument() throws IOException;

  void endDocument() throws IOException;

  /**
   * Begin object structured writer.
   *
   * @param name the name
   * @return the structured writer
   * @throws IOException the io exception
   */
  StructuredWriter beginObject(String name) throws IOException;

  StructuredWriter beginObject() throws IOException;

  /**
   * End object structured writer.
   *
   * @return the structured writer
   * @throws IOException the io exception
   */
  StructuredWriter endObject() throws IOException;

  /**
   * Begin array structured writer.
   *
   * @param name the name
   * @return the structured writer
   * @throws IOException the io exception
   */
  StructuredWriter beginArray(String name) throws IOException;

  StructuredWriter beginArray() throws IOException;

  /**
   * End array structured writer.
   *
   * @return the structured writer
   */
  StructuredWriter endArray() throws IOException;

  /**
   * In array boolean.
   *
   * @return the boolean
   */
  boolean inArray();

  /**
   * In object boolean.
   *
   * @return the boolean
   */
  boolean inObject();

  /**
   * Flush.
   */
  void flush() throws IOException;

  /**
   * Write key value structured writer.
   *
   * @param key   the key
   * @param value the value
   * @return the structured writer
   * @throws IOException the io exception
   */
  StructuredWriter writeKeyValue(String key, Object value) throws IOException;

  /**
   * Write value structured writer.
   *
   * @param value the value
   * @return the structured writer
   * @throws IOException the io exception
   */
  default StructuredWriter writeValue(Object value) throws IOException {
    return writeObject(value);
  }

  /**
   * Write null structured writer.
   *
   * @return the structured writer
   * @throws IOException the io exception
   */
  StructuredWriter writeNull() throws IOException;

  /**
   * Write number structured writer.
   *
   * @param number the number
   * @return the structured writer
   * @throws IOException the io exception
   */
  StructuredWriter writeNumber(Number number) throws IOException;

  /**
   * Write string structured writer.
   *
   * @param string the string
   * @return the structured writer
   * @throws IOException the io exception
   */
  StructuredWriter writeString(String string) throws IOException;

  /**
   * Write boolean structured writer.
   *
   * @param value the value
   * @return the structured writer
   * @throws IOException the io exception
   */
  StructuredWriter writeBoolean(boolean value) throws IOException;


  /**
   * Write object structured writer.
   *
   * @param object the object
   * @return the structured writer
   * @throws IOException the io exception
   */
  default StructuredWriter writeObject(@NonNull Object object) throws IOException {
    if (object == null) {
      writeNull();
    } else if (object instanceof Number) {
      writeNumber(Cast.as(object));
    } else if (object instanceof String) {
      writeString(Cast.as(object));
    } else if (object instanceof Boolean) {
      writeBoolean(Cast.as(object));
    } else if (object instanceof Enum || object instanceof DynamicEnum) {
      writeString(Convert.convert(object, String.class));
    } else if (object instanceof Collection) {
      writeCollection(Cast.as(object));
    } else if (object instanceof Map) {
      writeMap(Cast.as(object));
    } else if (object.getClass().isArray()) {
      writeArray(Cast.as(object));
    } else if (object instanceof Multimap) {
      writeMap(Cast.<Multimap>as(object).asMap());
    } else if (object instanceof Counter) {
      writeMap(Cast.<Counter>as(object).asMap());
    } else if (object instanceof MultiCounter) {
      writeMap(Cast.<MultiCounter>as(object).asMap());
    } else if (object instanceof Iterable) {
      writeCollection(new AbstractCollection<Object>() {
        @Override
        public Iterator<Object> iterator() {
          return Cast.<Iterable<Object>>as(object).iterator();
        }

        @Override
        public int size() {
          return Iterables.size(Cast.as(object));
        }
      });
    } else if (object instanceof Iterator) {
      writeCollection(new AbstractCollection<Object>() {
        @Override
        public Iterator<Object> iterator() {
          return Cast.as(object);
        }

        @Override
        public int size() {
          return Iterators.size(Cast.as(object));
        }
      });
    } else if (object instanceof Writeable) {
      writeKeyValue("class", object.getClass().getName());
      Cast.<Writeable>as(object).write(this);
    } else {
      writeValue(Convert.convert(object, String.class));
    }
    return this;
  }

  /**
   * Write object structured writer.
   *
   * @param name   the name
   * @param object the object
   * @return the structured writer
   * @throws IOException the io exception
   */
  default StructuredWriter writeObject(@NonNull String name, @NonNull Object object) throws IOException {
    beginObject(name);
    writeObject(object);
    endObject();
    return this;
  }

  /**
   * Write map structured writer.
   *
   * @param map the map
   * @return the structured writer
   * @throws IOException the io exception
   */
  default StructuredWriter writeMap(@NonNull Map<?, ?> map) throws IOException {
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      writeKeyValue(Convert.convert(entry.getKey(), String.class), entry.getValue());
    }
    return this;
  }

  /**
   * Write map structured writer.
   *
   * @param name the name
   * @param map  the map
   * @return the structured writer
   * @throws IOException the io exception
   */
  default StructuredWriter writeMap(@NonNull String name, @NonNull Map<?, ?> map) throws IOException {
    beginObject(name);
    writeMap(map);
    endObject();
    return this;
  }

  /**
   * Write collection structured writer.
   *
   * @param collection the collection
   * @return the structured writer
   * @throws IOException the io exception
   */
  default StructuredWriter writeCollection(@NonNull Collection<?> collection) throws IOException {
    for (Object o : collection) {
      writeValue(o);
    }
    return this;
  }

  /**
   * Write collection structured writer.
   *
   * @param name       the name
   * @param collection the collection
   * @return the structured writer
   * @throws IOException the io exception
   */
  default StructuredWriter writeCollection(@NonNull String name, @NonNull Collection<?> collection) throws IOException {
    beginArray(name);
    writeCollection(collection);
    endArray();
    return this;
  }


  /**
   * Write array structured writer.
   *
   * @param <T>   the type parameter
   * @param array the array
   * @return the structured writer
   * @throws IOException the io exception
   */
  default <T> StructuredWriter writeArray(@NonNull T[] array) throws IOException {
    for (T o : array) {
      writeValue(o);
    }
    return this;
  }

  /**
   * Write array structured writer.
   *
   * @param <T>   the type parameter
   * @param name  the name
   * @param array the array
   * @return the structured writer
   * @throws IOException the io exception
   */
  default <T> StructuredWriter writeArray(@NonNull String name, @NonNull T[] array) throws IOException {
    beginArray(name);
    writeArray(array);
    endArray();
    return this;
  }


}//END OF StructuredWriter
