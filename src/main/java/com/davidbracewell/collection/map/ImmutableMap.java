package com.davidbracewell.collection.map;

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.tuple.Tuple2;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.davidbracewell.collection.Streams.asStream;

/**
 * The type Immutable map.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
@EqualsAndHashCode
public final class ImmutableMap<K, V> implements Map<K, V>, Serializable {
  private static final long serialVersionUID = 1L;
  private final AbstractMap.SimpleImmutableEntry[] entries;
  private final int size;

  private ImmutableMap(AbstractMap.SimpleImmutableEntry[] entries) {
    this.size = (int) asStream(entries).parallel().map(Entry::getKey).distinct().count();
    this.entries = new AbstractMap.SimpleImmutableEntry[this.size];
    for (AbstractMap.SimpleImmutableEntry entry : entries) {
      add(entry.getKey(), entry.getValue());
    }
  }

  /**
   * Of map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param tuples the tuples
   * @return the map
   */
  @SafeVarargs
  public static <K, V> Map<K, V> of(Tuple2<? extends K, ? extends V>... tuples) {
    if (tuples == null) {
      return of();
    }
    return new ImmutableMap<>(
      asStream(tuples)
        .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()))
        .toArray(AbstractMap.SimpleImmutableEntry[]::new)
    );
  }

  /**
   * Of map.
   *
   * @param <K> the type parameter
   * @param <V> the type parameter
   * @return the map
   */
  public static <K, V> Map<K, V> of() {
    return new ImmutableMap<>(new AbstractMap.SimpleImmutableEntry[0]);
  }

  /**
   * Of map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @return the map
   */
  public static <K, V> Map<K, V> of(K key1, V value1) {
    return new ImmutableMap<>(new AbstractMap.SimpleImmutableEntry[]{
      new AbstractMap.SimpleImmutableEntry(key1, value1)
    });
  }

  /**
   * Of map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @return the map
   */
  public static <K, V> Map<K, V> of(K key1, V value1,
                                    K key2, V value2) {
    return new ImmutableMap<>(new AbstractMap.SimpleImmutableEntry[]{
      new AbstractMap.SimpleImmutableEntry(key1, value1),
      new AbstractMap.SimpleImmutableEntry(key2, value2)
    });
  }

  /**
   * Of map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @return the map
   */
  public static <K, V> Map<K, V> of(K key1, V value1,
                                    K key2, V value2,
                                    K key3, V value3) {
    return new ImmutableMap<>(new AbstractMap.SimpleImmutableEntry[]{
      new AbstractMap.SimpleImmutableEntry(key1, value1),
      new AbstractMap.SimpleImmutableEntry(key2, value2),
      new AbstractMap.SimpleImmutableEntry(key3, value3)
    });
  }

  /**
   * Of map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @return the map
   */
  public static <K, V> Map<K, V> of(K key1, V value1,
                                    K key2, V value2,
                                    K key3, V value3,
                                    K key4, V value4) {
    return new ImmutableMap<>(new AbstractMap.SimpleImmutableEntry[]{
      new AbstractMap.SimpleImmutableEntry(key1, value1),
      new AbstractMap.SimpleImmutableEntry(key2, value2),
      new AbstractMap.SimpleImmutableEntry(key3, value3),
      new AbstractMap.SimpleImmutableEntry(key4, value4)
    });
  }

  /**
   * Of map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @return the map
   */
  public static <K, V> Map<K, V> of(K key1, V value1,
                                    K key2, V value2,
                                    K key3, V value3,
                                    K key4, V value4,
                                    K key5, V value5) {
    return new ImmutableMap<>(new AbstractMap.SimpleImmutableEntry[]{
      new AbstractMap.SimpleImmutableEntry(key1, value1),
      new AbstractMap.SimpleImmutableEntry(key2, value2),
      new AbstractMap.SimpleImmutableEntry(key3, value3),
      new AbstractMap.SimpleImmutableEntry(key4, value4),
      new AbstractMap.SimpleImmutableEntry(key5, value5)
    });
  }


  /**
   * Of map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @return the map
   */
  public static <K, V> Map<K, V> of(K key1, V value1,
                                    K key2, V value2,
                                    K key3, V value3,
                                    K key4, V value4,
                                    K key5, V value5,
                                    K key6, V value6) {
    return new ImmutableMap<>(new AbstractMap.SimpleImmutableEntry[]{
      new AbstractMap.SimpleImmutableEntry(key1, value1),
      new AbstractMap.SimpleImmutableEntry(key2, value2),
      new AbstractMap.SimpleImmutableEntry(key3, value3),
      new AbstractMap.SimpleImmutableEntry(key4, value4),
      new AbstractMap.SimpleImmutableEntry(key5, value5),
      new AbstractMap.SimpleImmutableEntry(key6, value6),
    });
  }

  /**
   * Of map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @param key7   the key 7
   * @param value7 the value 7
   * @return the map
   */
  public static <K, V> Map<K, V> of(K key1, V value1,
                                    K key2, V value2,
                                    K key3, V value3,
                                    K key4, V value4,
                                    K key5, V value5,
                                    K key6, V value6,
                                    K key7, V value7) {
    return new ImmutableMap<>(new AbstractMap.SimpleImmutableEntry[]{
      new AbstractMap.SimpleImmutableEntry(key1, value1),
      new AbstractMap.SimpleImmutableEntry(key2, value2),
      new AbstractMap.SimpleImmutableEntry(key3, value3),
      new AbstractMap.SimpleImmutableEntry(key4, value4),
      new AbstractMap.SimpleImmutableEntry(key5, value5),
      new AbstractMap.SimpleImmutableEntry(key6, value6),
      new AbstractMap.SimpleImmutableEntry(key7, value7),
    });
  }

  /**
   * Of map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @param key7   the key 7
   * @param value7 the value 7
   * @param key8   the key 8
   * @param value8 the value 8
   * @return the map
   */
  public static <K, V> Map<K, V> of(K key1, V value1,
                                    K key2, V value2,
                                    K key3, V value3,
                                    K key4, V value4,
                                    K key5, V value5,
                                    K key6, V value6,
                                    K key7, V value7,
                                    K key8, V value8) {
    return new ImmutableMap<>(new AbstractMap.SimpleImmutableEntry[]{
      new AbstractMap.SimpleImmutableEntry(key1, value1),
      new AbstractMap.SimpleImmutableEntry(key2, value2),
      new AbstractMap.SimpleImmutableEntry(key3, value3),
      new AbstractMap.SimpleImmutableEntry(key4, value4),
      new AbstractMap.SimpleImmutableEntry(key5, value5),
      new AbstractMap.SimpleImmutableEntry(key6, value6),
      new AbstractMap.SimpleImmutableEntry(key7, value7),
      new AbstractMap.SimpleImmutableEntry(key8, value8),
    });
  }

  /**
   * Of map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @param key7   the key 7
   * @param value7 the value 7
   * @param key8   the key 8
   * @param value8 the value 8
   * @param key9   the key 9
   * @param value9 the value 9
   * @return the map
   */
  public static <K, V> Map<K, V> of(K key1, V value1,
                                    K key2, V value2,
                                    K key3, V value3,
                                    K key4, V value4,
                                    K key5, V value5,
                                    K key6, V value6,
                                    K key7, V value7,
                                    K key8, V value8,
                                    K key9, V value9) {
    return new ImmutableMap<>(new AbstractMap.SimpleImmutableEntry[]{
      new AbstractMap.SimpleImmutableEntry(key1, value1),
      new AbstractMap.SimpleImmutableEntry(key2, value2),
      new AbstractMap.SimpleImmutableEntry(key3, value3),
      new AbstractMap.SimpleImmutableEntry(key4, value4),
      new AbstractMap.SimpleImmutableEntry(key5, value5),
      new AbstractMap.SimpleImmutableEntry(key6, value6),
      new AbstractMap.SimpleImmutableEntry(key7, value7),
      new AbstractMap.SimpleImmutableEntry(key8, value8),
      new AbstractMap.SimpleImmutableEntry(key9, value9),
    });
  }


  /**
   * Of map.
   *
   * @param <K>     the type parameter
   * @param <V>     the type parameter
   * @param key1    the key 1
   * @param value1  the value 1
   * @param key2    the key 2
   * @param value2  the value 2
   * @param key3    the key 3
   * @param value3  the value 3
   * @param key4    the key 4
   * @param value4  the value 4
   * @param key5    the key 5
   * @param value5  the value 5
   * @param key6    the key 6
   * @param value6  the value 6
   * @param key7    the key 7
   * @param value7  the value 7
   * @param key8    the key 8
   * @param value8  the value 8
   * @param key9    the key 9
   * @param value9  the value 9
   * @param key10   the key 10
   * @param value10 the value 10
   * @return the map
   */
  public static <K, V> Map<K, V> of(K key1, V value1,
                                    K key2, V value2,
                                    K key3, V value3,
                                    K key4, V value4,
                                    K key5, V value5,
                                    K key6, V value6,
                                    K key7, V value7,
                                    K key8, V value8,
                                    K key9, V value9,
                                    K key10, V value10) {
    return new ImmutableMap<>(new AbstractMap.SimpleImmutableEntry[]{
      new AbstractMap.SimpleImmutableEntry(key1, value1),
      new AbstractMap.SimpleImmutableEntry(key2, value2),
      new AbstractMap.SimpleImmutableEntry(key3, value3),
      new AbstractMap.SimpleImmutableEntry(key4, value4),
      new AbstractMap.SimpleImmutableEntry(key5, value5),
      new AbstractMap.SimpleImmutableEntry(key6, value6),
      new AbstractMap.SimpleImmutableEntry(key7, value7),
      new AbstractMap.SimpleImmutableEntry(key8, value8),
      new AbstractMap.SimpleImmutableEntry(key9, value9),
      new AbstractMap.SimpleImmutableEntry(key10, value10)
    });
  }


  @Override
  public String toString() {
    return asStream(entries)
      .map(e -> e.getKey().toString() + "=" + e.getValue().toString())
      .collect(Collectors.joining(", ", "{", "}"));
  }

  private void add(Object key, Object value) {
    int hash = key.hashCode() % size;
    while (entries[hash] != null && !entries[hash].getKey().equals(key)) {
      hash = (hash + 1) % size;
    }
    entries[hash] = new AbstractMap.SimpleImmutableEntry<>(key, value);
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  private int findKey(Object key) {
    int hash = key.hashCode() % size;
    int count = 0;
    while (entries[hash] != null && !entries[hash].getKey().equals(key)) {
      hash = (hash + 1) % size;
      if (count == size) {
        return -1;
      }
      count++;
    }
    return hash;
  }

  @Override
  public boolean containsKey(Object key) {
    return findKey(key) != -1;
  }

  @Override
  public boolean containsValue(Object value) {
    return asStream(entries).anyMatch(e -> e.getValue().equals(value));
  }

  @Override
  public V get(Object key) {
    int index = findKey(key);
    return index == -1 ? null : Cast.as(entries[index].getValue());
  }

  @Override
  public V put(K key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V remove(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<K> keySet() {
    return new AbstractSet<K>() {
      @Override
      public Iterator<K> iterator() {
        return asStream(entries).map(Entry::getKey).map(Cast::<K>as).iterator();
      }

      @Override
      public int size() {
        return size;
      }
    };
  }

  @Override
  public Collection<V> values() {
    return new AbstractCollection<V>() {
      @Override
      public Iterator<V> iterator() {
        return asStream(entries).map(Entry::getValue).map(Cast::<V>as).iterator();
      }

      @Override
      public int size() {
        return size;
      }
    };
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return new AbstractSet<Entry<K, V>>() {

      @Override
      public Iterator<Entry<K, V>> iterator() {
        return asStream(entries).map(Cast::<Entry<K, V>>as).iterator();
      }

      @Override
      public int size() {
        return size;
      }
    };
  }
}// END OF ImmutableMap
