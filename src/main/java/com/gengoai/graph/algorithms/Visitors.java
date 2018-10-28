package com.gengoai.graph.algorithms;

import com.gengoai.graph.Graph;

import java.util.*;

/**
 * @author David B. Bracewell
 */
public enum Visitors implements GraphVisitor {
   BREADTH_FIRST {
      @Override
      public <V> Iterator<V> iterator(final Graph<V> graph, V startingVertex) {
         return new GenericIterator<V>(graph, startingVertex) {
            @Override
            protected void add(V vertex) {
               deque.offer(vertex);
            }

            @Override
            protected V nextV() {
               return deque.remove();
            }
         };
      }
   },
   DEPTH_FIRST {
      @Override
      public <V> Iterator<V> iterator(final Graph<V> graph, V startingVertex) {
         return new GenericIterator<V>(graph, startingVertex) {
            @Override
            protected void add(V vertex) {
               deque.push(vertex);
            }

            @Override
            protected V nextV() {
               return deque.pop();
            }
         };
      }
   };


   private static abstract class GenericIterator<V> implements Iterator<V> {
      private final Graph<V> graph;
      protected final Deque<V> deque = new LinkedList<>();
      private final Set<V> visited = new HashSet<>();

      protected GenericIterator(Graph<V> graph, V startingVertex) {
         this.graph = graph;
         this.deque.offer(startingVertex);
      }

      protected abstract void add(V vertex);

      protected abstract V nextV();

      @Override
      public boolean hasNext() {
         return deque.size() > 0;
      }

      @Override
      public V next() {
         if (deque.isEmpty()) {
            throw new NoSuchElementException();
         }
         V top = nextV();
         graph.getSuccessors(top).stream()
              .filter(v -> !visited.contains(v) && !deque.contains(v))
              .forEachOrdered(this::add);
         visited.add(top);
         return top;
      }
   }

}//END OF Visitors
