package com.davidbracewell.collection.map;

import com.davidbracewell.conversion.Cast;

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
 * @author David B. Bracewell
 */
public class ImmutableMap<K, V> implements Map<K, V>, Serializable {
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

  public static <K, V> Map<K, V> of() {
    return new ImmutableMap<>(new AbstractMap.SimpleImmutableEntry[0]);
  }

  public static <K, V> Map<K, V> of(K key1, V value1) {
    return new ImmutableMap<>(new AbstractMap.SimpleImmutableEntry[]{
      new AbstractMap.SimpleImmutableEntry(key1, value1)
    });
  }

  public static <K, V> Map<K, V> of(K key1, V value1,
                                    K key2, V value2) {
    return new ImmutableMap<>(new AbstractMap.SimpleImmutableEntry[]{
      new AbstractMap.SimpleImmutableEntry(key1, value1),
      new AbstractMap.SimpleImmutableEntry(key2, value2)
    });
  }

  public static <K, V> Map<K, V> of(K key1, V value1,
                                    K key2, V value2,
                                    K key3, V value3) {
    return new ImmutableMap<>(new AbstractMap.SimpleImmutableEntry[]{
      new AbstractMap.SimpleImmutableEntry(key1, value1),
      new AbstractMap.SimpleImmutableEntry(key2, value2),
      new AbstractMap.SimpleImmutableEntry(key3, value3)
    });
  }

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
