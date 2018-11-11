package com.gengoai.concurrent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>Thread safe double container</p>
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
    * Gets the double value
    *
    * @return the double
    */
   public double get() {
      return doubleValue();
   }

   /**
    * Sets to the given value.
    *
    * @param value the value
    */
   public void set(double value) {
      backing.set(Double.doubleToLongBits(value));
   }

   /**
    * Atomically sets to the given value and returns the old value.
    *
    * @param value the value to set
    * @return the value before the update
    */
   public double getAndSet(double value) {
      return backing.getAndSet(Double.doubleToLongBits(value));
   }

   /**
    * Atomically sets the value to the given updated value if the current value == the expected value.
    *
    * @param expect the expected value
    * @param update the updated value
    * @return True if updated, False if not
    */
   public final boolean weakCompareAndSet(double expect, double update) {
      return backing.weakCompareAndSet(Double.doubleToLongBits(expect),
                                       Double.doubleToLongBits(update));
   }

   /**
    * Atomically adds the given value to the current value.
    *
    * @param value the value
    * @return the updated value
    */
   public final double addAndGet(double value) {
      return backing.addAndGet(Double.doubleToLongBits(value));
   }

   /**
    * Atomically adds the given value to the current value.
    *
    * @param value the value
    * @return the previous value
    */
   public final double getAndAdd(double value) {
      return backing.getAndAdd(Double.doubleToLongBits(value));
   }


}//END OF AtomicDouble
