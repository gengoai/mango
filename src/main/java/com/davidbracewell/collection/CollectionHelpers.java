package com.davidbracewell.collection;

import com.davidbracewell.collection.list.SortedArrayList;
import com.davidbracewell.conversion.Cast;
import lombok.NonNull;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The interface Collection helpers.
 *
 * @author David B. Bracewell
 */
public interface CollectionHelpers {



  /**
   * As stream stream.
   *
   * @param <T>    the type parameter
   * @param values the values
   * @return the stream
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  static <T> Stream<T> asStream(T... values) {
    if (values == null) {
      return Stream.empty();
    }
    return Stream.of(values);
  }

  /**
   * As stream stream.
   *
   * @param <T>   the type parameter
   * @param value the value
   * @return the stream
   */
  static <T> Stream<T> asStream(T value) {
    if (value == null) {
      return Stream.empty();
    }
    return Stream.of(value);
  }

  /**
   * As stream stream.
   *
   * @param <T>      the type parameter
   * @param iterator the iterator
   * @return the stream
   */
  static <T> Stream<T> asStream(Iterator<? extends T> iterator) {
    if (iterator == null) {
      return Stream.empty();
    }
    return asStream(iterator, false);
  }

  /**
   * As parallel stream stream.
   *
   * @param <T>      the type parameter
   * @param iterator the iterator
   * @return the stream
   */
  static <T> Stream<T> asParallelStream(Iterator<? extends T> iterator) {
    if (iterator == null) {
      return Stream.empty();
    }
    return asStream(iterator, true);
  }

  /**
   * As stream stream.
   *
   * @param <T>      the type parameter
   * @param iterator the iterator
   * @param parallel the parallel
   * @return the stream
   */
  static <T> Stream<T> asStream(Iterator<? extends T> iterator, boolean parallel) {
    if (iterator == null) {
      return Stream.empty();
    }
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), parallel);
  }

  /**
   * As stream stream.
   *
   * @param <T>      the type parameter
   * @param iterable the iterable
   * @return the stream
   */
  static <T> Stream<T> asStream(Iterable<? extends T> iterable) {
    if (iterable == null) {
      return Stream.empty();
    }
    return asStream(iterable, false);
  }

  /**
   * As parallel stream stream.
   *
   * @param <T>      the type parameter
   * @param iterable the iterable
   * @return the stream
   */
  static <T> Stream<T> asParallelStream(Iterable<? extends T> iterable) {
    if (iterable == null) {
      return Stream.empty();
    }
    return asStream(iterable, true);
  }

  /**
   * As stream stream.
   *
   * @param <T>      the type parameter
   * @param iterable the iterable
   * @param parallel the parallel
   * @return the stream
   */
  static <T> Stream<T> asStream(Iterable<? extends T> iterable, boolean parallel) {
    if (iterable == null) {
      return Stream.empty();
    }
    return StreamSupport.stream(Cast.as(iterable.spliterator()), parallel);
  }

  /**
   * As list list.
   *
   * @param <T>    the type parameter
   * @param <Y>    the type parameter
   * @param first  the first
   * @param others the others
   * @return the list
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  static <T, Y extends T> List<T> list(Y first, Y... others) {
    return createList(ArrayList::new, first, others);
  }

  /**
   * As linked list list.
   *
   * @param <T>    the type parameter
   * @param <Y>    the type parameter
   * @param first  the first
   * @param others the others
   * @return the list
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  static <T, Y extends T> List<T> linkedList(Y first, Y... others) {
    return createList(LinkedList::new, first, others);
  }

  /**
   * As sorted list list.
   *
   * @param <T>    the type parameter
   * @param <Y>    the type parameter
   * @param first  the first
   * @param others the others
   * @return the list
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  static <T, Y extends T> List<T> sortedList(Y first, Y... others) {
    return createList(SortedArrayList::new, first, others);
  }

  /**
   * As list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param supplier the supplier
   * @param first    the first
   * @param others   the others
   * @return the list
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  static <T, Y extends T> List<T> createList(@NonNull Supplier<List<T>> supplier, Y first, Y... others) {
    if (first == null) {
      return Collections.emptyList();
    }
    if (others == null) {
      return Collections.singletonList(first);
    }
    return createList(supplier, Stream.concat(asStream(first), asStream(others)));
  }

  /**
   * As array list list.
   *
   * @param <T>    the type parameter
   * @param <Y>    the type parameter
   * @param stream the stream
   * @return the list
   */
  static <T, Y extends T> List<T> asArrayList(Stream<Y> stream) {
    return createList(ArrayList::new, stream);
  }

  /**
   * As linked list list.
   *
   * @param <T>    the type parameter
   * @param <Y>    the type parameter
   * @param stream the stream
   * @return the list
   */
  static <T, Y extends T> List<T> asLinkedList(Stream<Y> stream) {
    return createList(LinkedList::new, stream);
  }

  /**
   * As sorted list list.
   *
   * @param <T>    the type parameter
   * @param <Y>    the type parameter
   * @param stream the stream
   * @return the list
   */
  static <T, Y extends T> List<T> asSortedList(Stream<Y> stream) {
    return createList(SortedArrayList::new, stream);
  }

  /**
   * As array list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterator the iterator
   * @return the list
   */
  static <T, Y extends T> List<T> asArrayList(Iterator<Y> iterator) {
    return createList(ArrayList::new, asStream(iterator));
  }

  /**
   * As linked list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterator the iterator
   * @return the list
   */
  static <T, Y extends T> List<T> asLinkedList(Iterator<Y> iterator) {
    return createList(LinkedList::new, asStream(iterator));
  }

  /**
   * As sorted list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterator the iterator
   * @return the list
   */
  static <T, Y extends T> List<T> asSortedList(Iterator<Y> iterator) {
    return createList(SortedArrayList::new, asStream(iterator));
  }

  /**
   * As array list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterable the iterable
   * @return the list
   */
  static <T, Y extends T> List<T> asArrayList(Iterable<Y> iterable) {
    return createList(ArrayList::new, asStream(iterable));
  }

  /**
   * As linked list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterable the iterable
   * @return the list
   */
  static <T, Y extends T> List<T> asLinkedList(Iterable<Y> iterable) {
    return createList(LinkedList::new, asStream(iterable));
  }

  /**
   * As sorted list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterable the iterable
   * @return the list
   */
  static <T, Y extends T> List<T> asSortedList(Iterable<Y> iterable) {
    return createList(SortedArrayList::new, asStream(iterable));
  }

  /**
   * Create list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param supplier the supplier
   * @param stream   the stream
   * @return the list
   */
  static <T, Y extends T> List<T> createList(@NonNull Supplier<List<T>> supplier, Stream<Y> stream) {
    if (stream == null) {
      return Collections.emptyList();
    }
    return stream.collect(Collectors.toCollection(supplier));
  }


  @SafeVarargs
  @SuppressWarnings("varargs")
  static <T, Y extends T> Set<T> set(Y... others) {
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
  static <T, Y extends T> Set<T> treeSet(Y first, Y... others) {
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
  static <T, Y extends T> Set<T> linkedHashSet(Y first, Y... others) {
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
  static <T, Y extends T> Set<T> createSet(@NonNull Supplier<Set<T>> supplier, Y first, Y... others) {
    if (first == null) {
      return Collections.emptySet();
    }
    if (others == null) {
      return Collections.singleton(first);
    }
    return createSet(supplier, Stream.concat(asStream(first), asStream(others)));
  }

  @SafeVarargs
  @SuppressWarnings("varargs")
  static <T, Y extends T> Set<T> createSet(@NonNull Supplier<Set<T>> supplier, Y... others) {
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
  static <T, Y extends T> Set<T> asSet(Iterator<Y> iterator) {
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
  static <T, Y extends T> Set<T> asTreeSet(Iterator<Y> iterator) {
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
  static <T, Y extends T> Set<T> asLinkedHashSet(Iterator<Y> iterator) {
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
  static <T, Y extends T> Set<T> asSet(Iterable<Y> iterable) {
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
  static <T, Y extends T> Set<T> asTreeSet(Iterable<Y> iterable) {
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
  static <T, Y extends T> Set<T> asLinkedHashSet(Iterable<Y> iterable) {
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
  static <T, Y extends T> Set<T> asSet(Stream<Y> stream) {
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
  static <T, Y extends T> Set<T> asTreeSet(Stream<Y> stream) {
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
  static <T, Y extends T> Set<T> asLinkedHashSet(Stream<Y> stream) {
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
  static <T, Y extends T> Set<T> createSet(@NonNull Supplier<Set<T>> supplier, Stream<Y> stream) {
    if (stream == null) {
      return Collections.emptySet();
    }
    return stream.collect(Collectors.toCollection(supplier));
  }

}//END OF CollectionHelpers
