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

package com.gengoai.graph.clustering;


import com.gengoai.graph.Graph;

import java.util.List;
import java.util.Set;

/**
 * Common interface for methods that cluster the vertices of a graph
 *
 * @param <V> the vertex type
 */
public interface Clusterer<V> {

   /**
    * Clusters the vertices of the given graph and returns a list of clusters (sets).
    *
    * @param g the graph
    * @return the clusters
    */
   List<Set<V>> cluster(Graph<V> g);

}//END OF Clusterer
