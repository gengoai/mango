package com.gengoai.graph;

import java.util.Objects;

/**
 * An undirected weighted edge implementation.
 *
 * @param <V> The type of vertex
 */
public class UndirectedEdge<V> extends Edge<V> {
   private static final long serialVersionUID = 961303328216002925L;

   private double weight;

//   @Override
//   public JsonEntry toJson() {
//      return JsonEntry.object()
//                      .addProperty("weight", weight);
//   }

   protected UndirectedEdge(V vertex1, V vertex2, double weight) {
      super(vertex1, vertex2);
      this.weight = weight;
   }

   @Override
   public double getWeight() {
      return weight;
   }

   @Override
   public void setWeight(double weight) {
      this.weight = weight;
   }

   @Override
   public boolean isWeighted() {
      return true;
   }

   @Override
   public boolean isDirected() {
      return false;
   }

   @Override
   public int hashCode() {
      return 31 * (vertex1.hashCode()) + 31 * (vertex2.hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (obj == this) {
         return true;
      }
      if (obj instanceof UndirectedEdge) {
         UndirectedEdge otherEdge = (UndirectedEdge) obj;
         return (Objects.equals(vertex1, otherEdge.vertex1) && Objects.equals(vertex2, otherEdge.vertex2)) ||
                   (Objects.equals(vertex1, otherEdge.vertex2) && Objects.equals(vertex2, otherEdge.vertex1));
      }
      return false;
   }

   @Override
   public String toString() {
      return "UndirectedEdge{ " + vertex1 + " - " + vertex2 + (isWeighted() ? " : " + getWeight() : "") + "}";
   }
}// END OF UndirectedEdge
