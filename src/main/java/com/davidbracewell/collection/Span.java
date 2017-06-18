package com.davidbracewell.collection;

import com.davidbracewell.conversion.Cast;
import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author David B. Bracewell
 */
public class Span implements Serializable, Comparable<Span> {
   private static final long serialVersionUID = 1L;
   private final int start;
   private final int end;


   /**
    * Instantiates a new Span.
    *
    * @param start the start
    * @param end   the end
    */
   public Span(int start, int end) {
      Preconditions.checkArgument(end >= start, "Ending offset must be >= Starting offset");
      this.end = end;
      this.start = start;
   }

   /**
    * The starting offset
    *
    * @return The start offset (inclusive).
    */
   public int start() {
      return start;
   }

   /**
    * The ending offset
    *
    * @return The ending offset (exclusive).
    */
   public int end() {
      return end;
   }

   /**
    * The length of the span
    *
    * @return The length of the span
    */
   public int length() {
      return end() - start();
   }

   /**
    * Checks if the span is empty (<code>start == end</code>)
    *
    * @return True if the span is empty, False if not
    */
   public boolean isEmpty() {
      return length() == 0 || start() < 0 || end() < 0;
   }

   /**
    * Returns true if the bounds of other text are connected with the bounds of this text.
    *
    * @param other The other text to check if this one overlaps
    * @return True if the two texts are in the same document and overlap, False otherwise
    */
   public boolean overlaps(Span other) {
      return other != null && this.start() < other.end() && this.end() > other.start();
   }

   /**
    * Returns true if the bounds of the other text do not extend outside the bounds of this text.
    *
    * @param other The other text to check if this one encloses
    * @return True if the two texts are in the same document and this text encloses the other, False otherwise
    */
   public boolean encloses(Span other) {
      return other != null && other.start() >= this.start() && other.end() < this.end();
   }

   @Override
   public String toString() {
      return "(" + start + ", " + end + ")";
   }

   @Override
   public int hashCode() {
      return Objects.hash(start, end);
   }

   @Override
   public boolean equals(Object other) {
      return other != null &&
                other.getClass().equals(Span.class) &&
                Cast.<Span>as(other).start == this.start &&
                Cast.<Span>as(other).end == this.end;
   }

   @Override
   public int compareTo(Span o) {
      if (o == null) {
         return -1;
      }
      if (start < o.start) {
         return -1;
      }
      if (start > o.start) {
         return 1;
      }
      return Integer.compare(end, o.end);
   }

   public static Span of(int start, int end) {
      return new Span(start, end);
   }

}//END OF Span
