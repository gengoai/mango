/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.gengoai.graph.algorithms;

import com.gengoai.Validation;
import com.gengoai.graph.Edge;
import com.gengoai.graph.Graph;
import com.gengoai.collection.LRUMap;
import com.gengoai.collection.Sorting;
import com.gengoai.collection.counter.Counter;
import com.gengoai.collection.counter.Counters;
import com.gengoai.collection.multimap.ArrayListMultimap;
import com.gengoai.collection.multimap.ListMultimap;
import com.gengoai.conversion.Cast;
import com.gengoai.tuple.Tuple2;
import lombok.NonNull;

import java.util.*;

/**
 * The type Dijkstra shortest path.
 *
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class DijkstraShortestPath<V> implements SingleSourceShortestPath<V>, ShortestPath<V> {

   private final Graph<V> graph;
   private final Map<V, ListMultimap<V, Edge<V>>> pathMap = new LRUMap<>(100);
   private final boolean treatUndirected;

   /**
    * Instantiates a new Dijkstra shortest path.
    *
    * @param graph the graph
    */
   public DijkstraShortestPath(Graph<V> graph) {
      this(graph, false);
   }

   /**
    * Instantiates a new Dijkstra shortest path.
    *
    * @param graph           the graph
    * @param treatUndirected the treat undirected
    */
   public DijkstraShortestPath(@NonNull Graph<V> graph, boolean treatUndirected) {
      this.graph = graph;
      this.treatUndirected = treatUndirected;
   }


   @Override
   public Counter<V> singleSourceShortestDistance(@NonNull V source) {
      Validation.checkArgument(graph.containsVertex(source), "Vertex must be in the graph.");

      Counter<V> distances = Counters.newCounter();

      if (!pathMap.containsKey(source)) {
         singleSourceShortestPath(source);
      }

      for (V v : graph.vertices()) {
         if (!v.equals(source)) {
            if (pathMap.get(source).containsKey(v)) {
               double distance = 0d;
               for (Edge<V> e : pathMap.get(source).get(v)) {
                  distance += e.getWeight();
               }
               distances.set(v, distance);
            } else {
               distances.set(v, Double.POSITIVE_INFINITY);
            }
         }
      }


      return distances;
   }

   @Override
   public ListMultimap<V, Edge<V>> singleSourceShortestPath(@NonNull V source) {
      Validation.checkArgument(graph.containsVertex(source), "Vertex must be in the graph.");

      if (pathMap.containsKey(source)) {
         return pathMap.get(source);
      }

      Counter<V> distances = Counters.newCounter();
      Set<V> visited = new HashSet<>(Collections.singleton(source));
      Map<V, V> previous = new HashMap<>();

      distances.set(source, 0d);
      for (V v : graph.vertices()) {
         if (!v.equals(source)) {
            distances.set(v, Double.POSITIVE_INFINITY);
         }
      }

      Comparator<Tuple2<V, Double>> comparator = Cast.as(Sorting.mapEntryComparator(false, true));
      PriorityQueue<Tuple2<V, Double>> queue = new PriorityQueue<>(comparator);
      queue.add(Tuple2.of(source, 0d));

      while (!queue.isEmpty()) {
         Tuple2<V, Double> next = queue.remove();
         V u = next.v1;
         visited.add(u);

         for (Edge<V> out : treatUndirected ? graph.getEdges(u) : graph.getOutEdges(u)) {
            V v = out.getOppositeVertex(u);
            double alt = distances.get(u) + out.getWeight();
            if (alt < distances.get(v)) {
               distances.set(v, alt);
               previous.put(v, u);
               if (!visited.contains(v)) {
                  queue.add(Tuple2.of(v, alt));
               }
            }
         }

      }

      ListMultimap<V, Edge<V>> list = new ArrayListMultimap<>();
      pathMap.put(source, list);
      for (V v : graph.vertices()) {
         if (v.equals(source)) {
            continue;
         }
         if (Double.isFinite(distances.get(v))) {
            Deque<V> stack = new LinkedList<>();
            V u = v;
            while (u != null && previous.containsKey(u)) {
               stack.push(u);
               u = previous.get(u);
            }
            V from = source;
            while (!stack.isEmpty()) {
               V to = stack.pop();
               Edge<V> edge = graph.getEdge(from, to);
               if (treatUndirected && edge == null) {
                  edge = graph.getEdge(to, from);
               }
               list.put(v, edge);
               from = to;
            }
         }
      }
      return list;
   }

   @Override
   public double distance(@NonNull V from, @NonNull V to) {
      Validation.checkArgument(graph.containsVertex(from), "Vertex must be in the graph.");
      Validation.checkArgument(graph.containsVertex(to), "Vertex must be in the graph.");
      return singleSourceShortestDistance(from).get(to);
   }

   @Override
   public List<Edge<V>> path(@NonNull V from, @NonNull V to) {
      Validation.checkArgument(graph.containsVertex(from), "Vertex must be in the graph.");
      Validation.checkArgument(graph.containsVertex(to), "Vertex must be in the graph.");
      return Collections.unmodifiableList(singleSourceShortestPath(from).get(to));
   }

   /**
    * Resets cached data
    */
   public void reset() {
      pathMap.clear();
   }


}//END OF DijkstraShortestPath
