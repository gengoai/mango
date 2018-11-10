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

import com.gengoai.collection.Index;
import com.gengoai.collection.Indexes;
import com.gengoai.collection.multimap.Multimap;
import com.gengoai.graph.Edge;
import com.gengoai.graph.Graph;
import com.gengoai.graph.Vertex;
import com.gengoai.io.resource.Resource;
import com.graphdrawing.graphml.xmlns.*;

import javax.xml.bind.JAXB;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import static com.graphdrawing.graphml.xmlns.GraphEdgedefaultType.DIRECTED;

/**
 * <p>A  writer for the GraphML format</p>
 *
 * @param <V> the vertex type
 * @author David B. Bracewell
 */
public class GraphMLWriter<V> implements GraphWriter<V> {

   private VertexEncoder<V> vertexEncoder;
   private EdgeEncoder<V> edgeEncoder;


   /**
    * Instantiates a new GraphML reader and writer.
    */
   public GraphMLWriter() {
      this(null, null);
   }

   /**
    * Instantiates a new GraphML reader and writer
    *
    * @param vertexEncoder the vertex encoder
    * @param edgeEncoder   the edge encoder
    */
   public GraphMLWriter(VertexEncoder<V> vertexEncoder,
                        EdgeEncoder<V> edgeEncoder
                       ) {
      setVertexEncoder(vertexEncoder);
      setEdgeEncoder(edgeEncoder);
   }


   @Override
   public void setEdgeEncoder(EdgeEncoder<V> encoder) {
      if (encoder == null) {
         this.edgeEncoder = DefaultEncodersDecoders.defaultEdgeEncoder();
      } else {
         this.edgeEncoder = encoder;
      }
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
   public void write(Graph<V> graph, Resource location, Multimap<String, String> parameters) throws IOException {
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

}//END OF GraphML
