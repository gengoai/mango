package com.davidbracewell.collection;

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.string.CharPredicate;
import com.davidbracewell.string.StringUtils;
import com.google.common.collect.Iterators;
import lombok.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>A basic <a href="https://en.wikipedia.org/wiki/Trie">Trie</a> implementation that uses hashmaps to store its child
 * nodes. The {@link #find(String, CharPredicate)} method provides functionality to find all elements of the trie in the
 * specified string in longest-first style using the specified CharPredicate to accept or reject matches based on the
 * character after the match, e.g. only match if the next character is whitespace.</p>
 *
 * <p>Note that views of the trie, i.e. keySet(), values(), entrySet(), and the resulting map from prefix(), are
 * unmodifiable.</p>
 *
 * @param <V> the value type of the trie.
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

   /**
    * Instantiates a new Trie initializing the values to those in the given map.
    *
    * @param map A map of string to value used to populate the trie.
    */
   public Trie(@NonNull Map<String, V> map) {
      this();
      putAll(map);
   }

   /**
    * Matches the strings in the trie against a specified text. Matching is doing using a greedy longest match wins way.
    * The give CharPredicate is used to determine if matches are accepted, e.g. only accept a match followed by a
    * whitespace character.
    *
    * @param text      the text to find the trie elements in
    * @param delimiter the predicate that specifies acceptable delimiters
    * @return the list of matched elements
    */
   public List<TrieMatch<V>> find(String text, CharPredicate delimiter) {
      if (StringUtils.isNullOrBlank(text)) {
         return Collections.emptyList();
      }
      if (delimiter == null) {
         delimiter = CharPredicate.ANY;
      }

      int len = text.length();
      StringBuilder key = new StringBuilder();
      int start = 0;
      int lastMatch = -1;
      List<TrieMatch<V>> results = new ArrayList<>();

      for (int i = 0; i < len; i++) {

         key.append(text.charAt(i));

         //We have a key match
         if (containsKey(key.toString())) {
            int nextI = lastMatch = i + 1;

            //There is something longer!
            if (nextI < len && prefix(key.toString() + text.charAt(i + 1)).size() > 0) {
               continue;
            }


            lastMatch = -1;
            //check if we accept
            if (delimiter.matches(text.charAt(nextI))) {
               V value = get(key.toString());
               results.add(new TrieMatch<>(start, nextI, value));
               start = nextI;
            }

         } else if (prefix(key.toString()).isEmpty()) {

            //We cannot possibly match anything
            if (lastMatch != -1) {
               //We have a good match, so lets use it
               int nextI = lastMatch;
               if (nextI >= 1 && delimiter.matches(text.charAt(nextI))) {
                  key = new StringBuilder(text.substring(start, nextI));
                  V value = get(key.toString());
                  results.add(new TrieMatch<>(start, nextI, value));
                  i = nextI;
                  lastMatch = -1;
                  start = nextI;
               } else {
                  start = i;
               }
            } else {
               start = i;
            }

            if (start < len) {
               key.setLength(1);
               key.setCharAt(0, text.charAt(start));
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
      if (key == null) {
         return false;
      }
      TrieNode<V> match = root.find(key.toString());
      return match != null && StringUtils.safeEquals(match.matches, Cast.as(key), true);
   }

   @Override
   public boolean containsValue(Object value) {
      return values().contains(value);
   }

   @Override
   public V get(Object key) {
      if (key == null) {
         return null;
      }
      TrieNode<V> match = root.find(key.toString());
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
    * <p>Returns an unmodifiable map view of this Trie containing only those elements with the given prefix.</p>
    *
    * @param prefix the prefix to match
    * @return A unmodifiable map view of the trie whose elements have the given prefix
    */
   public Map<String, V> prefix(@NonNull String prefix) {
      final TrieNode<V> match = root.find(prefix);
      if (match == null) {
         return Collections.emptyMap();
      }
      return new AbstractMap<String, V>() {
         @Override
         public Set<Entry<String, V>> entrySet() {
            return new AbstractSet<Entry<String, V>>() {
               @Override
               public Iterator<Entry<String, V>> iterator() {
                  return Iterators.unmodifiableIterator(new EntryIterator<>(match));
               }

               @Override
               public int size() {
                  return match.size;
               }
            };
         }
      };
   }

   @Override
   public Set<String> keySet() {
      return new AbstractSet<String>() {

         @Override
         public boolean contains(Object o) {
            return Trie.this.containsKey(o);
         }


         @Override
         public Iterator<String> iterator() {
            return new KeyIterator<>(root);
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
            return new ValueIterator<>(root);
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
         public boolean contains(Object o) {
            if (o instanceof Entry) {
               Entry entry = Cast.as(o);
               return Trie.this.containsKey(entry.getKey()) && Trie.this.get(entry.getKey()).equals(entry.getValue());
            }
            return false;
         }

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
   public V put(@NonNull String key, V value) {
      return root.extend(key.toCharArray(), 0, value);
   }

   @Override
   public V remove(Object key) {
      if (key == null) {
         return null;
      }
      TrieNode<V> node = root.find(key.toString());
      V value = null;
      if (node != null) {
         node.matches = null;
         value = node.value;
         node.value = null;
         if (value != null) {
            node.size--;
         }
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

   private static class TrieNode<V> implements Serializable {
      private static final long serialVersionUID = 1L;
      private final Character nodeChar;
      private final TrieNode<V> parent;
      private final int depth;
      private V value;
      private String matches;
      private int size = 0;
      private Map<Character, TrieNode<V>> children = new HashMap<>(1);

      TrieNode<V> find(String string) {
         if (string == null || string.length() == 0) {
            return null;
         }
         TrieNode<V> node = this;
         if (nodeChar == null) {
            node = children.get(string.charAt(0));
         } else if (nodeChar != string.charAt(0)) {
            return null;
         }
         for (int i = 1; node != null && i < string.length(); i++) {
            node = node.children.get(string.charAt(i));
         }
         return node;
      }

      public boolean contains(String string) {
         return find(string) != null;
      }

      private TrieNode(Character nodeChar, TrieNode<V> parent) {
         this.nodeChar = nodeChar;
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

      void prune() {
         if (parent == null) {
            return;
         }
         if (matches == null && children.isEmpty()) {
            parent.children.remove(nodeChar);
         }
         parent.size--;
         parent.prune();
      }

      Iterator<Map.Entry<String, V>> subTreeIterator() {
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

   }

   private static abstract class TrieIterator<V, E> implements Iterator<E> {
      private final Queue<TrieNode<V>> queue = new LinkedList<>();
      private TrieNode<V> current = null;
      private TrieNode<V> old = null;

      private TrieIterator(TrieNode<V> node) {
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
      public E next() {
         old = move();
         if (old == null) {
            throw new NoSuchElementException();
         }
         current = null;
         return convert(old);
      }

      abstract E convert(TrieNode<V> node);

   }

   private static class KeyIterator<V> extends TrieIterator<V, String> {

      private KeyIterator(TrieNode<V> node) {
         super(node);
      }

      @Override
      String convert(TrieNode<V> node) {
         return node.matches;
      }

   }

   private static class ValueIterator<V> extends TrieIterator<V, V> {

      private ValueIterator(TrieNode<V> node) {
         super(node);
      }

      @Override
      V convert(TrieNode<V> node) {
         return node.value;
      }
   }

   private static class EntryIterator<V> extends TrieIterator<V, Map.Entry<String, V>> {

      private EntryIterator(TrieNode<V> node) {
         super(node);
      }

      @Override
      Map.Entry<String, V> convert(final TrieNode<V> old) {
         return new Map.Entry<String, V>() {
            @Override
            public String getKey() {
               return node.matches;
            }

            @Override
            public V getValue() {
               return node.value;
            }

            @Override
            public V setValue(V value) {
               V oldValue = node.value;
               node.value = value;
               return oldValue;
            }

            TrieNode<V> node = old;

            @Override
            public String toString() {
               return node.matches + "=" + node.value;
            }

            @Override
            public boolean equals(Object obj) {
               if (obj == null || !(obj instanceof Entry)) {
                  return false;
               }
               Entry e = Cast.as(obj);
               return e.getKey().equals(node.matches) && e.getValue().equals(node.value);
            }
         };
      }


   }


}// END OF Trie
