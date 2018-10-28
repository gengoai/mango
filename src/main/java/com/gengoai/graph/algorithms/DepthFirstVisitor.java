package com.gengoai.graph.algorithms;

import com.gengoai.graph.Graph;

import java.util.Deque;

/**
 * @author David B. Bracewell
 */
public class DepthFirstVisitor<V> extends AbstractGraphVisitor<V> {
   private static final long serialVersionUID = 1L;

   public DepthFirstVisitor(Graph<V> graph) {
      super(graph);
   }

   @Override
   protected void add(Deque<V> deque, V vertex) {
      deque.push(vertex);
   }

   @Override
   protected V nextV(Deque<V> deque) {
      return deque.pop();
   }

}//END OF BreadthFirstVisitor
