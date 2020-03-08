package com.gengoai.collection.tree;

import com.gengoai.Validation;

import java.util.Objects;

/**
 * The type Simple span.
 */
public class SimpleSpan implements Span {
   private static final long serialVersionUID = 1L;
   private int end;
   private int start;

   /**
    * Instantiates a new Simple span.
    *
    * @param start the start
    * @param end   the end
    */
   protected SimpleSpan(int start, int end) {
      Validation.checkArgument(end >= start, "Ending offset must be >= Starting offset");
      this.end = end;
      this.start = start;
   }

   @Override
   public int end() {
      return end;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Span)) return false;
      Span that = (Span) o;
      return end == that.end() && start == that.start();
   }

   @Override
   public int hashCode() {
      return Objects.hash(end, start);
   }

   @Override
   public int length() {
      return end - start;
   }

   /**
    * Sets end.
    *
    * @param end the end
    */
   protected void setEnd(int end) {
      this.end = end;
   }

   /**
    * Sets start.
    *
    * @param start the start
    */
   protected void setStart(int start) {
      this.start = start;
   }

   @Override
   public int start() {
      return start;
   }

   @Override
   public String toString() {
      return "Span{" +
         "end=" + end +
         ", start=" + start +
         '}';
   }
}//END OF SimpleSpan
