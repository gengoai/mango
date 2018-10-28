package com.gengoai.graph.algorithms;

import com.gengoai.graph.Graph;

import java.io.Serializable;
import java.util.*;

/**
 * @author David B. Bracewell
 */
public abstract class AbstractGraphVisitor<V> implements GraphVisitor<V>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Graph<V> graph;

   public AbstractGraphVisitor(Graph<V> graph) {
      this.graph = graph;
   }

   public Graph<V> getGraph() {
      return graph;
   }

   @Override
   public final Iterator<V> iterator(V startingPoint) {
      return new GenericIterator(startingPoint);
   }

   protected abstract void add(Deque<V> deque, V vertex);

   protected abstract V nextV(Deque<V> deque);

   private class GenericIterator implements Iterator<V> {
      private final Deque<V> deque = new LinkedList<>();
      private final Set<V> visited = new HashSet<>();

      protected GenericIterator(V startingVertex) {
         this.deque.offer(startingVertex);
      }

      @Override
      public boolean hasNext() {
         return deque.size() > 0;
      }

      @Override
      public V next() {
         if (deque.isEmpty()) {
            throw new NoSuchElementException();
         }
         V top = nextV(deque);
         graph.getSuccessors(top).stream()
              .filter(v -> !visited.contains(v) && !deque.contains(v))
              .forEachOrdered(v -> add(deque, v));
         visited.add(top);
         return top;
      }
   }
}//END OF AbstractGraphVisitor
