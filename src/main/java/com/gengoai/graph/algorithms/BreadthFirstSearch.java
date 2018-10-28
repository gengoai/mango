package com.gengoai.graph.algorithms;

import com.gengoai.collection.Lists;
import com.gengoai.graph.Edge;
import com.gengoai.graph.Graph;
import com.gengoai.tuple.Tuple2;

import java.io.Serializable;
import java.util.*;

import static com.gengoai.collection.Lists.arrayListOf;
import static com.gengoai.collection.Lists.asArrayList;
import static com.gengoai.collection.Sets.hashSetOf;
import static com.gengoai.tuple.Tuples.$;

/**
 * @author David B. Bracewell
 */
public class BreadthFirstSearch implements GraphSearch, Serializable {


   @Override
   public <V> List<Edge<V>> search(Graph<V> graph, V startingPoint, V endingPoint) {
      Deque<List<Tuple2<V, Edge<V>>>> pathes = new LinkedList<>();
      Set<V> visited = hashSetOf(startingPoint);
      graph.getEdges(startingPoint)
           .forEach(edge -> {
              Tuple2<V, Edge<V>> entry = $(startingPoint, edge);
              pathes.offer(arrayListOf(entry));
           });

      while (pathes.size() > 0) {
         List<Tuple2<V, Edge<V>>> path = pathes.remove();
         Tuple2<V, Edge<V>> last = path.get(path.size() - 1);
         V nextV = last.v2.getOppositeVertex(last.v1);
         if (nextV.equals(endingPoint)) {
            return Lists.transform(path, Tuple2::getV2);
         }
         if (visited.contains(nextV)) {
            continue;
         }
         visited.add(nextV);

         graph.getEdges(nextV)
              .stream()
              .filter(edge -> !visited.contains(edge.getOppositeVertex(nextV)))
              .forEachOrdered(edge -> {
                 List<Tuple2<V, Edge<V>>> newPath = asArrayList(path);
                 newPath.add($(nextV, edge));
                 pathes.offer(newPath);
              });
      }


      return Collections.emptyList();
   }


}//END OF BreadthFirstSearch
