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

import com.gengoai.collection.index.Index;
import com.gengoai.collection.index.Indexes;
import com.gengoai.collection.multimap.ListMultimap;
import com.gengoai.conversion.Cast;
import com.gengoai.graph.AdjacencyMatrix;
import com.gengoai.graph.Edge;
import com.gengoai.graph.Graph;
import com.gengoai.graph.Vertex;
import com.gengoai.io.resource.Resource;
import com.graphdrawing.graphml.xmlns.*;

import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static com.graphdrawing.graphml.xmlns.GraphEdgedefaultType.DIRECTED;

/**
 * <p>A reader and writer for the GraphML format</p>
 *
 * @param <V> the vertex type
 * @author David B. Bracewell
 */
public class GraphML<V> implements GraphReader<V>, GraphWriter<V> {

   private VertexEncoder<V> vertexEncoder;
   private EdgeEncoder<V> edgeEncoder;
   private VertexDecoder<V> vertexDecoder;
   private EdgeDecoder<V> edgeDecoder;


   /**
    * Instantiates a new GraphML reader and writer.
    */
   public GraphML() {
      this(null, null, null, null);
   }

   /**
    * Instantiates a new GraphML reader and writer
    *
    * @param vertexEncoder the vertex encoder
    * @param vertexDecoder the vertex decoder
    * @param edgeEncoder   the edge encoder
    * @param edgeDecoder   the edge decoder
    */
   public GraphML(VertexEncoder<V> vertexEncoder,
                  VertexDecoder<V> vertexDecoder,
                  EdgeEncoder<V> edgeEncoder,
                  EdgeDecoder<V> edgeDecoder
                 ) {
      setVertexEncoder(vertexEncoder);
      setEdgeEncoder(edgeEncoder);
      setVertexDecoder(vertexDecoder);
      setEdgeDecoder(edgeDecoder);
   }

   public static <V> GraphMLBuilder<V> builder() {
      return new GraphMLBuilder<V>();
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
                  graph = AdjacencyMatrix.directed();
                  break;
               default:
                  graph = AdjacencyMatrix.undirected();
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
   public void setEdgeEncoder(EdgeEncoder<V> encoder) {
      if (encoder == null) {
         this.edgeEncoder = DefaultEncodersDecoders.defaultEdgeEncoder();
      } else {
         this.edgeEncoder = encoder;
      }
   }

   public void setVertexDecoder(VertexDecoder<V> decoder) {
      this.vertexDecoder = decoder;
   }

   @Override
   public void setVertexEncoder(VertexEncoder<V> encoder) {
      if (encoder == null) {
         this.vertexEncoder = DefaultEncodersDecoders.defaultVertexEncoder();
      } else {
         this.vertexEncoder = encoder;
      }
   }

   @Override
   public void write(Graph<V> graph, Resource location, ListMultimap<String, String> parameters) throws IOException {
      GraphType graphType = new GraphType();
      graphType.setEdgedefault(graph.isDirected() ? DIRECTED : GraphEdgedefaultType.UNDIRECTED);
      graphType.setId("G");
      Index<V> vertexIndex = Indexes.indexOf(graph.vertices());


      for (V vertex : graph.vertices()) {
         Vertex vertexProps = vertexEncoder.encode(vertex);
         NodeType node = new NodeType();
         node.setId(Integer.toString(vertexIndex.getId(vertex)));

         DataType dataType = new DataType();
         dataType.setKey("label");
         dataType.setContent(vertexProps.getLabel());
         node.getDataOrPort().add(dataType);

         for (Map.Entry<String, String> entry : vertexProps.getProperties().entrySet()) {
            dataType = new DataType();
            dataType.setKey(entry.getKey());
            dataType.setContent(entry.getValue());
            node.getDataOrPort().add(dataType);
         }

         graphType.getDataOrNodeOrEdge().add(node);
      }

      for (Edge<V> edge : graph.edges()) {
         EdgeType edgeType = new EdgeType();
         edgeType.setDirected(graph.isDirected());
         edgeType.setSource(Integer.toString(vertexIndex.getId(edge.getFirstVertex())));
         edgeType.setTarget(Integer.toString(vertexIndex.getId(edge.getSecondVertex())));

         Map<String, String> edgeProperties = edgeEncoder.encode(edge);
         for (Map.Entry<String, String> property : edgeProperties.entrySet()) {
            DataType edgeData = new DataType();
            edgeData.setKey(property.getKey());
            edgeData.setContent(property.getValue());
            edgeType.getData().add(edgeData);
         }
         graphType.getDataOrNodeOrEdge().add(edgeType);
      }

      for (Map.Entry<String, String> entry : parameters.entries()) {
         DataType dataType = new DataType();
         dataType.setKey(entry.getKey());
         dataType.setContent(entry.getValue());
         graphType.getDataOrNodeOrEdge().add(dataType);
      }

      try (Writer writer = location.writer()) {
         GraphmlType gml = new GraphmlType();
         gml.getGraphOrData().add(graphType);
         JAXB.marshal(gml, writer);
      }
   }

   public static class GraphMLBuilder<V> {
      private VertexEncoder<V> vertexEncoder;
      private VertexDecoder<V> vertexDecoder;
      private EdgeEncoder<V> edgeEncoder;
      private EdgeDecoder<V> edgeDecoder;

      GraphMLBuilder() {
      }

      public GraphML<V> build() {
         return new GraphML<V>(vertexEncoder, vertexDecoder, edgeEncoder, edgeDecoder);
      }

      public GraphMLBuilder<V> edgeDecoder(EdgeDecoder<V> edgeDecoder) {
         this.edgeDecoder = edgeDecoder;
         return this;
      }

      public GraphMLBuilder<V> edgeEncoder(EdgeEncoder<V> edgeEncoder) {
         this.edgeEncoder = edgeEncoder;
         return this;
      }

      public String toString() {
         return "GraphML.GraphMLBuilder(vertexEncoder=" + this.vertexEncoder + ", vertexDecoder=" + this.vertexDecoder + ", edgeEncoder=" + this.edgeEncoder + ", edgeDecoder=" + this.edgeDecoder + ")";
      }

      public GraphMLBuilder<V> vertexDecoder(VertexDecoder<V> vertexDecoder) {
         this.vertexDecoder = vertexDecoder;
         return this;
      }

      public GraphMLBuilder<V> vertexEncoder(VertexEncoder<V> vertexEncoder) {
         this.vertexEncoder = vertexEncoder;
         return this;
      }
   }
}//END OF GraphML
