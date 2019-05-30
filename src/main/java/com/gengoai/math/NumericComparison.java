package com.gengoai.math;

import com.gengoai.function.SerializableBiFunction;

/**
 * Methods for comparing numeric (double) values.
 *
 * @author David B. Bracewell
 */
public enum NumericComparison implements SerializableBiFunction<Number, Number, Boolean> {
   /**
    * Is <code>beingCompared</code> greater than <code>comparedAgainst</code>
    */
   GT {
      @Override
      public boolean compare(double beingCompared, double comparedAgainst) {
         return beingCompared > comparedAgainst;
      }
   },
   /**
    * Is <code>beingCompared</code> greater than or equal to <code>comparedAgainst</code>
    */
   GTE {
      @Override
      public boolean compare(double beingCompared, double comparedAgainst) {
         return beingCompared >= comparedAgainst;
      }
   },
   /**
    * Is <code>beingCompared</code> less than <code>comparedAgainst</code>
    */
   LT {
      @Override
      public boolean compare(double beingCompared, double comparedAgainst) {
         return beingCompared < comparedAgainst;
      }
   },
   /**
    * Is <code>beingCompared</code> less than or equal to <code>comparedAgainst</code>
    */
   LTE {
      @Override
      public boolean compare(double beingCompared, double comparedAgainst) {
         return beingCompared <= comparedAgainst;
      }
   },
   /**
    * Is <code>beingCompared</code> equal to <code>comparedAgainst</code>
    */
   EQ {
      @Override
      public boolean compare(double beingCompared, double comparedAgainst) {
         return Double.compare(beingCompared,comparedAgainst) == 0;
//         System.out.println(" >> " + beingCompared + " = " + comparedAgainst);
//         if (Double.isFinite(beingCompared) && Double.isFinite(comparedAgainst)) {
//            return beingCompared == comparedAgainst;
//         }
//         if (Double.isFinite(beingCompared)) {
//            if (Double.isNaN(comparedAgainst)) {
//               return Double.isNaN(beingCompared);
//            }
//            return beingCompared == comparedAgainst;
//         }
//         if (Double.isNaN(beingCompared)) {
//            return Double.isNaN(comparedAgainst);
//         }
//         return beingCompared == comparedAgainst;
      }
   },
   /**
    * Is <code>beingCompared</code> not equal to <code>comparedAgainst</code>
    */
   NE {
      @Override
      public boolean compare(double beingCompared, double comparedAgainst) {
         return beingCompared != comparedAgainst;
      }
   };

   @Override
   public Boolean apply(Number number, Number number2) {
      return compare(number.doubleValue(), number2.doubleValue());
   }

   /**
    * Compares two given numeric values
    *
    * @param beingCompared   The number being compared
    * @param comparedAgainst The number being compared against
    * @return true if the inequality holds
    */
   public abstract boolean compare(double beingCompared, double comparedAgainst);


   public String asString() {
      switch (this) {
         case EQ:
            return "=";
         case GT:
            return ">";
         case GTE:
            return ">=";
         case LT:
            return "<";
         case LTE:
            return "<=";
         case NE:
            return "!=";
      }
      throw new IllegalArgumentException();
   }

}// END OF Inequality
