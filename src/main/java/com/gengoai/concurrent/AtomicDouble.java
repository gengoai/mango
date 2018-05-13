package com.gengoai.concurrent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The type Atomic double.
 *
 * @author David B. Bracewell
 */
public class AtomicDouble extends Number {
   private final AtomicLong backing;

   /**
    * Instantiates a new Atomic double.
    */
   public AtomicDouble() {
      this(0d);
   }

   /**
    * Instantiates a new Atomic double.
    *
    * @param value the value
    */
   public AtomicDouble(double value) {
      this.backing = new AtomicLong(Double.doubleToLongBits(value));
   }

   @Override
   public int intValue() {
      return (int) Double.longBitsToDouble(backing.get());
   }

   @Override
   public long longValue() {
      return backing.get();
   }

   @Override
   public float floatValue() {
      return (float) Double.longBitsToDouble(backing.get());
   }

   @Override
   public double doubleValue() {
      return Double.longBitsToDouble(backing.get());
   }

   /**
    * Get double.
    *
    * @return the double
    */
   public double get() {
      return doubleValue();
   }

   /**
    * Set.
    *
    * @param value the value
    */
   public void set(double value) {
      backing.set(Double.doubleToLongBits(value));
   }

   /**
    * Gets and set.
    *
    * @param value the value
    * @return the and set
    */
   public double getAndSet(double value) {
      return backing.getAndSet(Double.doubleToLongBits(value));
   }

   /**
    * Weak compare and set boolean.
    *
    * @param expect the expect
    * @param update the update
    * @return the boolean
    */
   public final boolean weakCompareAndSet(double expect, double update) {
      return backing.weakCompareAndSet(Double.doubleToLongBits(expect),
                                       Double.doubleToLongBits(update));
   }

   /**
    * Add and get double.
    *
    * @param value the value
    * @return the double
    */
   public final double addAndGet(double value) {
      return backing.addAndGet(Double.doubleToLongBits(value));
   }

   /**
    * Gets and add.
    *
    * @param value the value
    * @return the and add
    */
   public final double getAndAdd(double value) {
      return backing.getAndAdd(Double.doubleToLongBits(value));
   }


}//END OF AtomicDouble
