package com.gengoai.graph.algorithms;

import com.gengoai.graph.Edge;
import com.gengoai.graph.Graph;
import com.gengoai.tuple.Tuple2;

import java.util.Deque;
import java.util.List;

import static com.gengoai.Validation.notNull;

/**
 * @author David B. Bracewell
 */
public class BreadthFirstSearch<V> extends AbstractGraphSearch<V> {
   private static final long serialVersionUID = 1L;

   /**
    * Instantiates a new Abstract graph search.
    *
    * @param graph the graph
    */
   public BreadthFirstSearch(Graph<V> graph) {
      super(notNull(graph));
   }

   @Override
   protected void add(Deque<List<Tuple2<V, Edge<V>>>> pathes, List<Tuple2<V, Edge<V>>> path) {
      pathes.offer(path);
   }

   @Override
   protected List<Tuple2<V, Edge<V>>> next(Deque<List<Tuple2<V, Edge<V>>>> pathes) {
      return pathes.remove();
   }
}//END OF BreadthFirstSearch
