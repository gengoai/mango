package com.davidbracewell.collection.set;

import com.davidbracewell.collection.Collect;
import lombok.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.davidbracewell.collection.CollectionHelpers.asStream;


/**
 * The type Sets.
 *
 * @author David B. Bracewell
 */
public final class Sets {

  private Sets() {
    throw new IllegalAccessError();
  }

  public static <E, R> Set<R> transform(Set<? extends E> set, @NonNull Function<? super E, R> mapper) {
    if (set == null) {
      return Collections.emptySet();
    }
    return set.stream().map(mapper).collect(Collectors.toCollection(bestSupplier(set, null)));
  }

  /**
   * Difference set.
   *
   * @param <E> the type parameter
   * @param s1  the s 1
   * @param s2  the s 2
   * @return the set
   */
  public static <E> Set<E> difference(Set<? extends E> s1, Set<? extends E> s2) {
    return Collect.difference(bestSupplier(s1, s2), s1, s2);
  }

  /**
   * Union set.
   *
   * @param <E> the type parameter
   * @param s1  the s 1
   * @param s2  the s 2
   * @return the set
   */
  public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2) {
    return Collect.union(bestSupplier(s1, s2), s1, s2);
  }

  /**
   * Intersection set.
   *
   * @param <E> the type parameter
   * @param s1  the s 1
   * @param s2  the s 2
   * @return the set
   */
  public static <E> Set<E> intersection(Set<? extends E> s1, Set<? extends E> s2) {
    return Collect.intersection(bestSupplier(s1, s2), s1, s2);
  }

  private static <E> Supplier<Set<E>> bestSupplier(Set<?> set1, Set<?> set2) {
    if (set1 == null && set2 == null) {
      return HashSet::new;
    } else if (set1 == null) {
      return () -> Collect.create(set2.getClass());
    }
    return () -> Collect.create(set1.getClass());
  }


  /**
   * Set set.
   *
   * @param <T>    the type parameter
   * @param <Y>    the type parameter
   * @param others the others
   * @return the set
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <T, Y extends T> Set<T> set(Y... others) {
    return createSet(HashSet::new, others);
  }


  /**
   * As sorted set set.
   *
   * @param <T>    the type parameter
   * @param <Y>    the type parameter
   * @param first  the first
   * @param others the others
   * @return the set
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <T, Y extends T> Set<T> treeSet(Y first, Y... others) {
    return createSet(TreeSet::new, first, others);
  }

  /**
   * As linked hash set set.
   *
   * @param <T>    the type parameter
   * @param <Y>    the type parameter
   * @param first  the first
   * @param others the others
   * @return the set
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <T, Y extends T> Set<T> linkedHashSet(Y first, Y... others) {
    return createSet(LinkedHashSet::new, first, others);
  }

  /**
   * As set set.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param supplier the supplier
   * @param first    the first
   * @param others   the others
   * @return the set
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <T, Y extends T> Set<T> createSet(@NonNull Supplier<Set<T>> supplier, Y first, Y... others) {
    if (first == null) {
      return Collections.emptySet();
    }
    if (others == null) {
      return Collections.singleton(first);
    }
    return createSet(supplier, Stream.concat(asStream(first), asStream(others)));
  }

  /**
   * Create set set.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param supplier the supplier
   * @param others   the others
   * @return the set
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <T, Y extends T> Set<T> createSet(@NonNull Supplier<Set<T>> supplier, Y... others) {
    if (others == null) {
      return Collections.emptySet();
    }
    return createSet(supplier, Stream.of(others));
  }

  /**
   * As set set.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterator the iterator
   * @return the set
   */
  public static <T, Y extends T> Set<T> asSet(Iterator<Y> iterator) {
    return createSet(HashSet::new, asStream(iterator));
  }

  /**
   * As tree set set.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterator the iterator
   * @return the set
   */
  public static <T, Y extends T> Set<T> asTreeSet(Iterator<Y> iterator) {
    return createSet(TreeSet::new, asStream(iterator));
  }

  /**
   * As linked hash set set.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterator the iterator
   * @return the set
   */
  public static <T, Y extends T> Set<T> asLinkedHashSet(Iterator<Y> iterator) {
    return createSet(LinkedHashSet::new, asStream(iterator));
  }

  /**
   * As set set.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterable the iterable
   * @return the set
   */
  public static <T, Y extends T> Set<T> asSet(Iterable<Y> iterable) {
    return createSet(HashSet::new, asStream(iterable));
  }

  /**
   * As tree set set.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterable the iterable
   * @return the set
   */
  public static <T, Y extends T> Set<T> asTreeSet(Iterable<Y> iterable) {
    return createSet(TreeSet::new, asStream(iterable));
  }

  /**
   * As linked hash set set.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterable the iterable
   * @return the set
   */
  public static <T, Y extends T> Set<T> asLinkedHashSet(Iterable<Y> iterable) {
    return createSet(LinkedHashSet::new, asStream(iterable));
  }

  /**
   * As set set.
   *
   * @param <T>    the type parameter
   * @param <Y>    the type parameter
   * @param stream the stream
   * @return the set
   */
  public static <T, Y extends T> Set<T> asSet(Stream<Y> stream) {
    return createSet(HashSet::new, stream);
  }

  /**
   * As tree set set.
   *
   * @param <T>    the type parameter
   * @param <Y>    the type parameter
   * @param stream the stream
   * @return the set
   */
  public static <T, Y extends T> Set<T> asTreeSet(Stream<Y> stream) {
    return createSet(TreeSet::new, stream);
  }

  /**
   * As linked hash set set.
   *
   * @param <T>    the type parameter
   * @param <Y>    the type parameter
   * @param stream the stream
   * @return the set
   */
  public static <T, Y extends T> Set<T> asLinkedHashSet(Stream<Y> stream) {
    return createSet(LinkedHashSet::new, stream);
  }

  /**
   * Create set set.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param supplier the supplier
   * @param stream   the stream
   * @return the set
   */
  public static <T, Y extends T> Set<T> createSet(@NonNull Supplier<Set<T>> supplier, Stream<Y> stream) {
    if (stream == null) {
      return Collections.emptySet();
    }
    return stream.collect(Collectors.toCollection(supplier));
  }

}//END OF Sets
