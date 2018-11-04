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

package com.gengoai.graph.io;

import com.gengoai.conversion.Cast;
import com.gengoai.graph.Edge;
import com.gengoai.graph.Graph;
import com.gengoai.graph.Vertex;
import com.gengoai.io.resource.Resource;
import com.graphdrawing.graphml.xmlns.*;

import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>A reader and writer for the GraphML format</p>
 *
 * @param <V> the vertex type
 * @author David B. Bracewell
 */
public class GraphMLReader<V> implements GraphReader<V> {

   private EdgeDecoder<V> edgeDecoder;
   private VertexDecoder<V> vertexDecoder;


   /**
    * Instantiates a new GraphML reader and writer.
    *
    * @param vClass the v class
    */
   public GraphMLReader(Class<V> vClass) {
      this(DefaultEncodersDecoders.defaultVertexDecoder(vClass), null);
   }

   /**
    * Instantiates a new GraphML reader and writer
    *
    * @param vertexDecoder the vertex decoder
    * @param edgeDecoder   the edge decoder
    */
   public GraphMLReader(VertexDecoder<V> vertexDecoder,
                        EdgeDecoder<V> edgeDecoder
                       ) {
      setEdgeDecoder(edgeDecoder);
      setVertexDecoder(vertexDecoder);
   }


   @Override
   public Graph<V> read(Resource location) throws IOException {
      GraphmlType gml;
      try (Reader reader = location.reader()) {
         gml = JAXB.unmarshal(reader, GraphmlType.class);
      }
      Graph<V> graph = null;
      for (Object o : gml.getGraphOrData()) {
         if (o instanceof GraphType) {
            GraphType gt = Cast.as(o);

            switch (gt.getEdgedefault()) {
               case DIRECTED:
                  graph = Graph.directed();
                  break;
               default:
                  graph = Graph.undirected();
                  break;
            }

            Map<String, V> idToVertexMap = new HashMap<>();
            //Read all the nodes
            for (Object dornore : gt.getDataOrNodeOrEdge()) {
               if (dornore instanceof NodeType) {
                  NodeType node = Cast.as(dornore);
                  V vertex;

                  Vertex.VertexBuilder vertexBuilder = Vertex.builder();

                  if (node.getDataOrPort().isEmpty()) {
                     vertexBuilder.label(node.getId());
                  }

                  node.getDataOrPort().stream()
                      .filter(dataOrPort -> dataOrPort instanceof DataType)
                      .forEach(dataOrPort -> {
                         DataType dataType = Cast.as(dataOrPort);
                         if (dataType.getKey().equals("label")) {
                            vertexBuilder.label(dataType.getContent());
                         } else {
                            vertexBuilder.property(dataType.getKey(), dataType.getContent());
                         }
                      });


                  vertex = vertexDecoder.decode(vertexBuilder.build());
                  if (vertex == null) {
                     throw new IllegalStateException(
                        "Vertex [" + node.getId() + "] is not convertable to the vertex type");
                  }

                  idToVertexMap.put(node.getId(), vertex);
                  graph.addVertex(vertex);
               }
            }

            for (Object dornore : gt.getDataOrNodeOrEdge()) {
               if (dornore instanceof EdgeType) {
                  EdgeType edge = Cast.as(dornore);
                  Edge<V> newEdge = graph.addEdge(idToVertexMap.get(edge.getSource()),
                                                  idToVertexMap.get(edge.getTarget()));
                  Map<String, String> properties = new HashMap<>();
                  edge.getData().forEach(data -> properties.put(data.getKey(), data.getContent()));
                  edgeDecoder.decode(newEdge, properties);
               }
            }

         }
      }

      return graph;
   }

   @Override
   public void setEdgeDecoder(EdgeDecoder<V> decoder) {
      if (decoder == null) {
         this.edgeDecoder = DefaultEncodersDecoders.defaultEdgeDecoder();
      } else {
         this.edgeDecoder = decoder;
      }
   }

   @Override
   public void setVertexDecoder(VertexDecoder<V> decoder) {
      this.vertexDecoder = decoder;
   }

}//END OF GraphML
