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

import com.gengoai.SystemInfo;
import com.gengoai.collection.Index;
import com.gengoai.collection.Indexes;
import com.gengoai.collection.multimap.ListMultimap;
import com.gengoai.config.Config;
import com.gengoai.graph.Edge;
import com.gengoai.graph.Graph;
import com.gengoai.graph.Vertex;
import com.gengoai.io.Resources;
import com.gengoai.io.resource.Resource;
import com.gengoai.logging.Logger;
import com.gengoai.string.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * <p>An implementation of a <code>GraphRender</code> and <code>GraphWriter</code> for GraphViz</p>
 *
 * @param <V> the vertex type
 */
public class GraphViz<V> implements GraphWriter<V>, GraphRenderer<V> {

   public static <V> GraphVizBuilder<V> builder() {
      return new GraphVizBuilder<V>();
   }

   public enum Format {
      JPG("jpg"),
      PNG("png"),
      SVG("svg");

      private final String extension;

      Format(String extension) {
         this.extension = extension;
      }

      public String getExtension() {
         return extension;
      }

   }

   private static final Logger log = Logger.getLogger(GraphViz.class);
   private static String DOT = "/usr/bin/dot";
   private EdgeEncoder<V> edgeEncoder;
   private VertexEncoder<V> vertexEncoder;
   private Format format = Format.PNG;

   /**
    * Instantiates a new GraphViz writer/renderer.
    */
   public GraphViz() {
      this(null, null);
   }

   /**
    * Instantiates a new GraphViz writer/renderer.
    *
    * @param vertexEncoder the vertex encoder
    */
   public GraphViz(VertexEncoder<V> vertexEncoder) {
      this(vertexEncoder, null);
   }

   /**
    * Instantiates a new GraphViz writer/renderer.
    *
    * @param edgeEncoder the edge encoder
    */
   public GraphViz(EdgeEncoder<V> edgeEncoder) {
      this(null, edgeEncoder);
   }

   /**
    * Instantiates a new GraphViz writer/renderer.
    *
    * @param vertexEncoder the vertex encoder
    * @param edgeEncoder   the edge encoder
    */
   public GraphViz(VertexEncoder<V> vertexEncoder, EdgeEncoder<V> edgeEncoder) {
      this(vertexEncoder, edgeEncoder, Format.PNG);
   }

   /**
    * Instantiates a new GraphViz writer/renderer.
    *
    * @param vertexEncoder the vertex encoder
    * @param edgeEncoder   the edge encoder
    * @param format        the format
    */
   public GraphViz(VertexEncoder<V> vertexEncoder, EdgeEncoder<V> edgeEncoder, Format format) {
      setEdgeEncoder(edgeEncoder);
      setVertexEncoder(vertexEncoder);
      setFormat(format);
      String configName = "graphviz.dot." + SystemInfo.OS_NAME;
      log.fine("Looking for dot in config {0}", configName);
      if (Config.hasProperty(configName)) {
         DOT = Config.get(configName).asString();
         log.fine("Setting DOT location to {0} from config {1}", DOT, configName);
      }
   }

   @Override
   public void render(Graph<V> graph, Resource location, ListMultimap<String, String> parameters) throws IOException {
      Resource tempLoc = Resources.temporaryFile();
      tempLoc.deleteOnExit();
      write(graph, tempLoc, parameters);


      Runtime rt = Runtime.getRuntime();
      String[] args = {DOT, "-T" + format.getExtension(), tempLoc.asFile()
                                                                 .get().getAbsolutePath(), "-o", location.asFile()
                                                                                                         .get().getAbsolutePath()};
      Process p = rt.exec(args);

      try {
         p.waitFor();
      } catch (InterruptedException e) {
         throw new RuntimeException(e);
      }
   }

   private String escape(String input) {
      if (input == null || input.length() == 0) {
         return "\"" + StringUtils.EMPTY + "\"";
      }
      if (input.length() == 1) {
         return "\"" + input + "\"";
      }
      if (input.charAt(0) == '"' && input.charAt(input.length() - 1) == '"') {
         return input;
      }
      return "\"" + input + "\"";
   }

   @Override
   public void setVertexEncoder(VertexEncoder<V> serializer) {
      if (serializer == null) {
         this.vertexEncoder = DefaultEncodersDecoders.defaultVertexEncoder();
      } else {
         this.vertexEncoder = serializer;
      }
   }

   @Override
   public void setEdgeEncoder(EdgeEncoder<V> serializer) {
      if (serializer == null) {
         this.edgeEncoder = DefaultEncodersDecoders.defaultEdgeEncoder();
      } else {
         this.edgeEncoder = serializer;
      }
   }

   @Override
   public void write(Graph<V> graph, Resource location, ListMultimap<String, String> parameters) throws IOException {
      location.setCharset(StandardCharsets.UTF_8);
      try (BufferedWriter writer = new BufferedWriter(location.writer())) {

         //Write the header
         if (graph.isDirected()) {
            writer.write("digraph G {");
         } else {
            writer.write("graph G {");
         }
         writer.newLine();

         writer.write("rankdir = BT;\n");
         if (parameters.containsKey("graph")) {
            writer.write("graph [");
            for (String property : parameters.get("graph")) {
               writer.write(property);
            }
            writer.write("];");
            writer.newLine();
         }

         Index<V> vertexIndex = Indexes.indexOf(graph.vertices());

         for (V vertex : graph.vertices()) {
            Vertex vertexProps = vertexEncoder.encode(vertex);
            writer.write(Integer.toString(vertexIndex.getId(vertex)));
            writer.write(" [");
            writer.write("label=" + escape(vertexProps.getLabel()) + " ");
            for (Map.Entry<String, String> entry : vertexProps.getProperties().entrySet()) {
               writer.write(entry.getKey() + "=" + escape(entry.getValue()) + " ");
            }
            writer.write("];");
            writer.newLine();
         }

         for (Edge<V> edge : graph.edges()) {
            writer.write(Integer.toString(vertexIndex.getId(edge.getFirstVertex())));
            if (graph.isDirected()) {
               writer.write(" -> ");
            } else {
               writer.write(" -- ");
            }
            writer.write(Integer.toString(vertexIndex.getId(edge.getSecondVertex())));

            Map<String, String> edgeProps = edgeEncoder.encode(edge);
            if (edgeProps != null && !edgeProps.isEmpty()) {
               writer.write(" [");
               for (Map.Entry<String, String> entry : edgeProps.entrySet()) {
                  writer.write(entry.getKey() + "=" + escape(entry.getValue()) + " ");
               }
               writer.write("];");
            }

            writer.newLine();
         }

         //write the footer
         writer.write("}");
         writer.newLine();
      }
   }

   /**
    * Sets format.
    *
    * @param format the format
    */
   public void setFormat(Format format) {
      this.format = format == null ? Format.PNG : format;
   }


   public static class GraphVizBuilder<V> {
      private VertexEncoder<V> vertexEncoder;
      private EdgeEncoder<V> edgeEncoder;
      private Format format;

      GraphVizBuilder() {
      }

      public GraphViz<V> build() {
         return new GraphViz<V>(vertexEncoder, edgeEncoder, format);
      }

      public GraphVizBuilder<V> edgeEncoder(EdgeEncoder<V> edgeEncoder) {
         this.edgeEncoder = edgeEncoder;
         return this;
      }

      public GraphVizBuilder<V> format(Format format) {
         this.format = format;
         return this;
      }

      public String toString() {
         return "GraphViz.GraphVizBuilder(vertexEncoder=" + this.vertexEncoder + ", edgeEncoder=" + this.edgeEncoder + ", format=" + this.format + ")";
      }

      public GraphVizBuilder<V> vertexEncoder(VertexEncoder<V> vertexEncoder) {
         this.vertexEncoder = vertexEncoder;
         return this;
      }
   }
}//END OF GraphViz
