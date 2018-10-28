package com.gengoai.graph.algorithms;

import com.gengoai.graph.Graph;

import java.util.Deque;

/**
 * @author David B. Bracewell
 */
public class BreadthFirstVisitor<V> extends AbstractGraphVisitor<V> {
   private static final long serialVersionUID = 1L;

   public BreadthFirstVisitor(Graph<V> graph) {
      super(graph);
   }


   @Override
   protected void add(Deque<V> deque, V vertex) {
      deque.offer(vertex);
   }

   @Override
   protected V nextV(Deque<V> deque) {
      return deque.remove();
   }
}//END OF BreadthFirstVisitor
