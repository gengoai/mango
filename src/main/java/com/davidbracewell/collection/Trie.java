package com.davidbracewell.collection;

import com.davidbracewell.collection.trie.TrieMatch;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.string.CharPredicate;
import com.davidbracewell.string.StringUtils;
import lombok.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.davidbracewell.tuple.Tuples.$;

/**
 * The type Trie.
 *
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class Trie<V> implements Serializable, Map<String, V> {
  private static final long serialVersionUID = 1L;
  private final TrieNode<V> root;

  /**
   * Instantiates a new Trie.
   */
  public Trie() {
    this.root = new TrieNode<>(null, null);
  }

  public Trie(@NonNull Map<String, V> map) {
    this();
    putAll(map);
  }

  private Trie(TrieNode<V> root) {
    if (root != null) {
      this.root = root;
    } else {
      this.root = new TrieNode<>(null, null);
    }
  }

  /**
   * Find all list.
   *
   * @param text      the text
   * @param delimiter the delimiter
   * @return the list
   */
  public List<TrieMatch<V>> findAll(String text, CharPredicate delimiter) {
    int len = text.length();
    StringBuilder key = new StringBuilder();
    int start = 0;
    int lastMatch = -1;
    List<TrieMatch<V>> results = new ArrayList<>();

    for (int i = 0; i < len; i++) {
      key.append(text.charAt(i));
      //We have a key match
      if (containsKey(key.toString())) {
        int nextI = i + 1;
        lastMatch = i + 1;

        //There is something longer!
        if (nextI < len && !prefix(key.toString() + text.charAt(i + 1)).isEmpty()) {
          continue;
        }

        lastMatch = -1;

        //check if we accept
        if (delimiter.matches(text.charAt(nextI))) {
          V value = get(key.toString());
          results.add(new TrieMatch<>(i + 1, start, value));
          start = i + 1;
          continue;
        }
      }

      if (prefix(key.toString()).isEmpty()) {
        if (lastMatch != -1) {
          int nextI = lastMatch;
          if (nextI >= 1 && delimiter.matches(text.charAt(nextI))) {
            key = new StringBuilder(text.substring(start, nextI));
            V value = get(key.toString());
            results.add(new TrieMatch<>(nextI, start, value));
            i = lastMatch;
            lastMatch = -1;
          }

        }

        start = i;
        if (key.length() > 1) {
          key.setLength(1);
          key.setCharAt(0, text.charAt(i));
        } else {
          key.setLength(0);
        }

      }

    }

    return results;

  }


  @Override
  public int size() {
    return root.size;
  }

  @Override
  public boolean isEmpty() {
    return root.size == 0;
  }

  @Override
  public boolean containsKey(Object key) {
    TrieNode<V> match = find(Cast.as(key));
    return match != null && StringUtils.safeEquals(match.matches, Cast.as(key), true);
  }

  @Override
  public boolean containsValue(Object value) {
    return values().contains(value);
  }

  @Override
  public V get(Object key) {
    TrieNode<V> match = find(Cast.as(key));
    if (match != null) {
      return match.value;
    }
    return null;
  }

  @Override
  public String toString() {
    return entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(", ", "{", "}"));
  }

  /**
   * Prefix trie.
   *
   * @param prefix the prefix
   * @return the trie
   */
  public Trie<V> prefix(String prefix) {
    TrieNode<V> match = find(prefix);
    if (match == null) {
      return new Trie<>();
    }
    return new Trie<>(match);
  }

  @Override
  public Set<String> keySet() {
    return new AbstractSet<String>() {
      @Override
      public Iterator<String> iterator() {
        return Streams.asStream(root.subTreeIterator()).map(Map.Entry::getKey).iterator();
      }

      @Override
      public int size() {
        return root.size;
      }
    };
  }

  @Override
  public Collection<V> values() {
    return new AbstractCollection<V>() {
      @Override
      public Iterator<V> iterator() {
        return Streams.asStream(root.subTreeIterator()).map(Map.Entry::getValue).iterator();
      }

      @Override
      public int size() {
        return root.size;
      }
    };
  }

  @Override
  public Set<Entry<String, V>> entrySet() {
    return new AbstractSet<Entry<String, V>>() {
      @Override
      public Iterator<Entry<String, V>> iterator() {
        return root.subTreeIterator();
      }

      @Override
      public int size() {
        return root.size;
      }
    };
  }

  @Override
  public V put(String key, V value) {
    return root.extend(key.toCharArray(), 0, value);
  }

  @Override
  public V remove(Object key) {
    TrieNode<V> node = find(Cast.as(key));
    V value = null;
    if (node != null) {
      node.matches = null;
      value = node.value;
      node.value = null;
      node.prune();
    }
    return value;
  }

  @Override
  public void putAll(Map<? extends String, ? extends V> m) {
    m.forEach(this::put);
  }

  @Override
  public void clear() {
    root.children.clear();
    root.size = 0;
    root.matches = null;
    root.value = null;
    root.prune();
  }

  private TrieNode<V> find(String string) {
    if (string == null || string.length() == 0 || string.length() < root.depth) {
      return null;
    }
    char[] array = string.toCharArray();
    TrieNode<V> node = root;
    int i = 0;
    if (root.matches == null) {
      while (i < root.depth) {
        node = node.parent;
        i++;
      }
      i = 0;
    } else {
      i = root.depth;
      if (!root.matches.substring(0, i).equals(string.substring(0, i))) {
        return null;
      }
    }
    for (; i < array.length && node != null; i++) {
      node = node.children.get(array[i]);
    }
    return node;
  }

  private static class TrieNode<V> implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Character c;
    private final TrieNode<V> parent;
    private final int depth;
    private V value;
    private String matches;
    private int size = 0;
    private Map<Character, TrieNode<V>> children = new HashMap<>(1);


    private TrieNode(Character c, TrieNode<V> parent) {
      this.c = c;
      this.parent = parent;
      if (parent == null) {
        this.depth = 0;
      } else {
        this.depth = parent.depth + 1;
      }
    }

    @Override
    public String toString() {
      return "(" + matches + ", " + value + ")";
    }

    private void prune() {
      if (parent == null) {
        return;
      }
      if (matches == null && children.isEmpty()) {
        parent.children.remove(c);
      }
      parent.size--;
      parent.prune();
    }

    private Iterator<Map.Entry<String, V>> subTreeIterator() {
      return new EntryIterator<>(this);
    }

    /**
     * Extend v.
     *
     * @param word  the word
     * @param start the start
     * @param value the value
     * @return the v
     */
    V extend(char[] word, int start, V value) {
      TrieNode<V> node = this;
      if (start == word.length) {
        V old = node.value;
        node.value = value;
        node.matches = new String(word);
        if (old == null) {
          node.size++;
        }
        return old;
      }
      if (!children.containsKey(word[start])) {
        children.put(word[start], new TrieNode<>(word[start], this));
      }
      V toReturn = children.get(word[start]).extend(word, start + 1, value);
      if (toReturn == null) {
        size++;
      }
      return toReturn;
    }

    private static class EntryIterator<V> implements Iterator<Map.Entry<String, V>> {
      /**
       * The Queue.
       */
      final Queue<TrieNode<V>> queue = new LinkedList<>();
      /**
       * The Current.
       */
      TrieNode<V> current = null;
      /**
       * The Old.
       */
      TrieNode<V> old = null;

      /**
       * Instantiates a new Entry iterator.
       *
       * @param node the node
       */
      public EntryIterator(TrieNode<V> node) {
        if (node.matches != null) {
          queue.add(node);
        } else {
          queue.addAll(node.children.values());
        }
      }

      private TrieNode<V> move() {
        while (current == null || current.matches == null) {
          if (queue.isEmpty()) {
            return null;
          }
          current = queue.remove();
          queue.addAll(current.children.values());
        }
        return current;
      }


      @Override
      public boolean hasNext() {
        return move() != null;
      }

      @Override
      public Map.Entry<String, V> next() {
        old = move();
        if (old == null) {
          throw new NoSuchElementException();
        }
        current = null;
        return $(old.matches, old.value);
      }

      @Override
      public void remove() {
        old.value = null;
        old.matches = null;
        old.size--;
        old.prune();
      }
    }
  }


}// END OF Trie
